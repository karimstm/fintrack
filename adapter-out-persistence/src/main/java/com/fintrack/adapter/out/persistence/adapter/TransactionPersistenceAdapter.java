package com.fintrack.adapter.out.persistence.adapter;

import com.fintrack.adapter.out.persistence.mapper.TransactionPersistenceMapper;
import com.fintrack.adapter.out.persistence.repository.TransactionJpaRepository;
import com.fintrack.application.port.out.transaction.LoadTransactionPort;
import com.fintrack.application.port.out.transaction.SaveTransactionPort;
import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.transaction.Transaction;
import com.fintrack.domain.model.transaction.TransactionId;
import com.fintrack.domain.model.user.UserId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class TransactionPersistenceAdapter
        implements LoadTransactionPort, SaveTransactionPort {

    private final TransactionJpaRepository repository;
    private final TransactionPersistenceMapper mapper;

    public TransactionPersistenceAdapter(
            TransactionJpaRepository repository,
            TransactionPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Transaction> loadById(TransactionId transactionId) {
        return repository.findById(transactionId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Transaction> loadByAccount(AccountId accountId, UserId ownerId) {
        return repository
                .findAllByAccountIdOrderByOccurredAtDesc(accountId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> loadByAccountAndDateRange(
            AccountId accountId, UserId ownerId, Instant from, Instant to) {
        return repository
                .findByAccountIdAndDateRange(accountId.value(), from, to)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    @Transactional
    public Transaction save(Transaction transaction) {
        var entity = mapper.toJpaEntity(transaction);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public List<Transaction> saveAll(List<Transaction> transactions) {
        var entities = transactions.stream()
                .map(mapper::toJpaEntity)
                .toList();
        return repository.saveAll(entities)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}