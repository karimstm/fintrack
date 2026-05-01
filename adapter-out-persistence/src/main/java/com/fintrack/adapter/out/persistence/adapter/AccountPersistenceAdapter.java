package com.fintrack.adapter.out.persistence.adapter;

import com.fintrack.adapter.out.persistence.mapper.AccountPersistenceMapper;
import com.fintrack.adapter.out.persistence.repository.AccountJpaRepository;
import com.fintrack.application.port.out.account.DeleteAccountPort;
import com.fintrack.application.port.out.account.LoadAccountPort;
import com.fintrack.application.port.out.account.SaveAccountPort;
import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.user.UserId;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
public class AccountPersistenceAdapter
        implements LoadAccountPort, SaveAccountPort, DeleteAccountPort {

    private final AccountJpaRepository repository;
    private final AccountPersistenceMapper mapper;

    public AccountPersistenceAdapter(
            AccountJpaRepository repository,
            AccountPersistenceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Account> loadById(AccountId accountId) {
        return repository.findById(accountId.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Account> loadByIdAndOwner(AccountId accountId, UserId ownerId) {
        return repository.findByIdAndOwnerId(accountId.value(), ownerId.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Account> loadAllByOwner(UserId ownerId) {
        return repository.findAllByOwnerId(ownerId.value())
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public boolean existsByIdAndOwner(AccountId accountId, UserId ownerId) {
        return repository.existsByIdAndOwnerId(accountId.value(), ownerId.value());
    }

    @Override
    @Transactional
    public Account save(Account account) {
        var entity = mapper.toJpaEntity(account);
        var saved = repository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteByIdAndOwner(AccountId accountId, UserId ownerId) {
        repository.deleteByIdAndOwnerId(accountId.value(), ownerId.value());
    }
}