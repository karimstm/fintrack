package com.fintrack.adapter.out.persistence.adapter;

import com.fintrack.adapter.out.persistence.mapper.AccountPersistenceMapper;
import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.account.AccountId;
import com.fintrack.domain.model.account.AccountType;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.user.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({AccountPersistenceAdapter.class, AccountPersistenceMapper.class})
class AccountPersistenceAdapterIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("fintrack_test")
            .withUsername("fintrack")
            .withPassword("fintrack");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        // Re-enable Flyway for this test slice
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
    }

    @Autowired
    AccountPersistenceAdapter adapter;

    @Test
    @DisplayName("should save and reload account correctly")
    void shouldSaveAndReloadAccount() {
        UserId ownerId = UserId.generate();
        Account account = Account.open(
                ownerId,
                AccountType.CHECKING,
                "Test Account",
                Money.of("500.00", "USD")
        );

        Account saved = adapter.save(account);
        Optional<Account> loaded = adapter.loadByIdAndOwner(saved.getId(), ownerId);

        assertTrue(loaded.isPresent());
        assertEquals("Test Account", loaded.get().getName());
        assertEquals(Money.of("500.00", "USD"), loaded.get().getBalance());
    }

    @Test
    @DisplayName("should return empty when account not found")
    void shouldReturnEmptyWhenNotFound() {
        Optional<Account> result = adapter.loadByIdAndOwner(
                AccountId.generate(),
                UserId.generate()
        );
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("should load all accounts by owner")
    void shouldLoadAllAccountsByOwner() {
        UserId ownerId = UserId.generate();
        adapter.save(Account.open(ownerId, AccountType.CHECKING,
                "Checking", Money.of("100.00", "USD")));
        adapter.save(Account.open(ownerId, AccountType.SAVINGS,
                "Savings", Money.of("200.00", "USD")));

        List<Account> accounts = adapter.loadAllByOwner(ownerId);
        assertEquals(2, accounts.size());
    }

    @Test
    @DisplayName("should not return accounts of another owner")
    void shouldIsolateByOwner() {
        UserId owner1 = UserId.generate();
        UserId owner2 = UserId.generate();
        adapter.save(Account.open(owner1, AccountType.CHECKING,
                "Owner1 Account", Money.of("100.00", "USD")));

        List<Account> result = adapter.loadAllByOwner(owner2);
        assertTrue(result.isEmpty());
    }
}