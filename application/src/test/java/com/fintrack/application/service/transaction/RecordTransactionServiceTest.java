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
import com.fintrack.domain.exception.InsufficientFundsException;
import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.account.AccountType;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.transaction.Transaction;
import com.fintrack.domain.model.transaction.TransactionType;
import com.fintrack.domain.model.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecordTransactionServiceTest {

    @Mock LoadAccountPort loadAccountPort;
    @Mock SaveAccountPort saveAccountPort;
    @Mock SaveTransactionPort saveTransactionPort;
    @Mock LoadBudgetPort loadBudgetPort;
    @Mock SaveBudgetPort saveBudgetPort;
    @Mock AccountCachePort accountCachePort;
    @Mock DomainEventPublisherPort eventPublisher;

    RecordTransactionService service;
    UserId ownerId;
    Account account;

    @BeforeEach
    void setUp() {
        service = new RecordTransactionService(
                loadAccountPort, saveAccountPort, saveTransactionPort,
                loadBudgetPort, saveBudgetPort, accountCachePort, eventPublisher
        );
        ownerId = UserId.generate();
        account = Account.open(
                ownerId,
                AccountType.CHECKING,
                "Test Account",
                Money.of("1000.00", "USD")
        );
    }

    @Test
    @DisplayName("should record a debit transaction successfully")
    void shouldRecordDebitTransaction() {
        var command = new RecordTransactionUseCase.Command(
                account.getId(), ownerId, TransactionType.DEBIT,
                BigDecimal.valueOf(200), "USD", "Groceries", "FOOD_AND_DRINK"
        );
        when(loadAccountPort.loadByIdAndOwner(account.getId(), ownerId))
                .thenReturn(Optional.of(account));
        when(saveAccountPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(saveTransactionPort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(loadBudgetPort.loadByOwnerCategoryAndPeriod(any(), any(), any()))
                .thenReturn(Optional.empty());

        Transaction result = service.recordTransaction(command);

        assertNotNull(result);
        assertEquals(TransactionType.DEBIT, result.getType());
        assertEquals(Money.of("200.00", "USD"), result.getAmount());
        verify(accountCachePort).cacheBalance(any(AccountId.class), any(Money.class));
        verify(eventPublisher).publishAll(anyList());
    }

    @Test
    @DisplayName("should throw when account not found")
    void shouldThrowWhenAccountNotFound() {
        var command = new RecordTransactionUseCase.Command(
                account.getId(), ownerId, TransactionType.DEBIT,
                BigDecimal.valueOf(100), "USD", "Coffee", "FOOD_AND_DRINK"
        );
        when(loadAccountPort.loadByIdAndOwner(any(), any())).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> service.recordTransaction(command));
        verifyNoInteractions(saveAccountPort, saveTransactionPort, eventPublisher);
    }

    @Test
    @DisplayName("should throw on insufficient funds")
    void shouldThrowOnInsufficientFunds() {
        var command = new RecordTransactionUseCase.Command(
                account.getId(), ownerId, TransactionType.DEBIT,
                BigDecimal.valueOf(9999), "USD", "Expensive", "SHOPPING"
        );
        when(loadAccountPort.loadByIdAndOwner(account.getId(), ownerId))
                .thenReturn(Optional.of(account));

        assertThrows(InsufficientFundsException.class, () -> service.recordTransaction(command));
    }
}