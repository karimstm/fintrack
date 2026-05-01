package com.fintrack.application.service.transaction;

import com.fintrack.application.port.in.transaction.RecordTransactionUseCase;
import com.fintrack.application.port.out.account.LoadAccountPort;
import com.fintrack.application.port.out.account.SaveAccountPort;
import com.fintrack.application.port.out.budget.LoadBudgetPort;
import com.fintrack.application.port.out.budget.SaveBudgetPort;
import com.fintrack.application.port.out.cache.AccountCachePort;
import com.fintrack.application.port.out.notification.DomainEventPublisherPort;
import com.fintrack.application.port.out.transaction.SaveTransactionPort;
import com.fintrack.domain.exception.AccountNotFoundException;
import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.budget.Budget;
import com.fintrack.domain.model.budget.BudgetPeriod;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.transaction.Category;
import com.fintrack.domain.model.transaction.Transaction;
import com.fintrack.domain.model.transaction.TransactionType;
import com.fintrack.domain.model.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Currency;
import java.util.List;
import java.util.Optional;

public class RecordTransactionService implements RecordTransactionUseCase {

    private static final Logger log = LoggerFactory.getLogger(RecordTransactionService.class);

    private final LoadAccountPort loadAccountPort;
    private final SaveAccountPort saveAccountPort;
    private final SaveTransactionPort saveTransactionPort;
    private final LoadBudgetPort loadBudgetPort;
    private final SaveBudgetPort saveBudgetPort;
    private final AccountCachePort accountCachePort;
    private final DomainEventPublisherPort eventPublisher;

    public RecordTransactionService(
            LoadAccountPort loadAccountPort,
            SaveAccountPort saveAccountPort,
            SaveTransactionPort saveTransactionPort,
            LoadBudgetPort loadBudgetPort,
            SaveBudgetPort saveBudgetPort,
            AccountCachePort accountCachePort,
            DomainEventPublisherPort eventPublisher) {
        this.loadAccountPort = loadAccountPort;
        this.saveAccountPort = saveAccountPort;
        this.saveTransactionPort = saveTransactionPort;
        this.loadBudgetPort = loadBudgetPort;
        this.saveBudgetPort = saveBudgetPort;
        this.accountCachePort = accountCachePort;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Transaction recordTransaction(Command command) {
        log.info("Recording {} of {} on account={}",
                command.type(), command.amount(), command.accountId());

        // 1. Load the account — enforces ownership
        Account account = loadAccountPort
                .loadByIdAndOwner(command.accountId(), command.ownerId())
                .orElseThrow(() -> new AccountNotFoundException(command.accountId()));

        Money amount = Money.of(command.amount(), Currency.getInstance(command.currency()));

        // 2. Apply the transaction to the account aggregate
        Transaction transaction = switch (command.type()) {
            case CREDIT -> account.credit(amount, command.description(), command.category());
            case DEBIT  -> account.debit(amount, command.description(), command.category());
        };

        // 3. Persist the updated account state
        Account savedAccount = saveAccountPort.save(account);

        // 4. Persist the transaction itself
        Transaction savedTransaction = saveTransactionPort.save(transaction);

        // 5. Update cached balance
        accountCachePort.cacheBalance(savedAccount.getId(), savedAccount.getBalance());

        // 6. If it's a debit, check and update the relevant budget
        if (command.type() == TransactionType.DEBIT) {
            updateBudgetIfPresent(command.ownerId(), command.category(), amount);
        }

        // 7. Drain and publish all domain events (TransactionRecordedEvent, BudgetExceededEvent)
        var accountEvents = savedAccount.pullDomainEvents();
        eventPublisher.publishAll(accountEvents);

        log.info("Transaction recorded successfully id={}", savedTransaction.getId());
        return savedTransaction;
    }

    @Override
    public List<Transaction> getTransactions(AccountId accountId, UserId ownerId) {
        // Ensure the caller owns the account before returning transactions
        if (!loadAccountPort.existsByIdAndOwner(accountId, ownerId)) {
            throw new AccountNotFoundException(accountId);
        }
        return loadAccountPort
                .loadByIdAndOwner(accountId, ownerId)
                .map(Account::getTransactions)
                .orElse(List.of());
    }

    private void updateBudgetIfPresent(UserId ownerId, String categoryName, Money amount) {
        try {
            Category category = Category.valueOf(categoryName.toUpperCase());
            BudgetPeriod currentPeriod = BudgetPeriod.current();

            Optional<Budget> budget = loadBudgetPort
                    .loadByOwnerCategoryAndPeriod(ownerId, category, currentPeriod);

            if (budget.isPresent()) {
                Budget b = budget.get();
                b.recordSpending(amount);
                saveBudgetPort.save(b);

                // Publish BudgetExceededEvent if the budget was breached
                eventPublisher.publishAll(b.pullDomainEvents());

                log.debug("Budget updated for category={} spent={}", category, amount);
            }
        } catch (IllegalArgumentException e) {
            // Unknown category — no budget to update, not an error
            log.debug("No budget category match for: {}", categoryName);
        }
    }
}