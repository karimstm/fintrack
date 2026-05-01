package com.fintrack.domain.model.account;

import com.fintrack.domain.event.TransactionRecordedEvent;
import com.fintrack.domain.exception.InsufficientFundsException;
import com.fintrack.domain.model.shared.Money;
import com.fintrack.domain.model.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    private static final Currency USD = Currency.getInstance("USD");
    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.open(
                UserId.generate(),
                AccountType.CHECKING,
                "My Checking Account",
                Money.of(BigDecimal.valueOf(1000), USD)
        );
    }

    @Test
    @DisplayName("should credit amount and increase balance")
    void shouldCreditAndIncreaseBalance() {
        account.credit(Money.of("500.00", "USD"), "Salary", "SALARY");

        assertEquals(Money.of("1500.00", "USD"), account.getBalance());
    }

    @Test
    @DisplayName("should debit amount and decrease balance")
    void shouldDebitAndDecreaseBalance() {
        account.debit(Money.of("200.00", "USD"), "Groceries", "FOOD_AND_DRINK");

        assertEquals(Money.of("800.00", "USD"), account.getBalance());
    }

    @Test
    @DisplayName("should throw when debit exceeds balance")
    void shouldThrowOnInsufficientFunds() {
        assertThrows(InsufficientFundsException.class, () ->
                account.debit(Money.of("2000.00", "USD"), "Big purchase", "SHOPPING")
        );
    }

    @Test
    @DisplayName("should register domain event after debit")
    void shouldRegisterDomainEventAfterDebit() {
        account.debit(Money.of("100.00", "USD"), "Coffee", "FOOD_AND_DRINK");

        var events = account.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(TransactionRecordedEvent.class, events.get(0));
    }

    @Test
    @DisplayName("should clear events after pulling them")
    void shouldClearEventsAfterPull() {
        account.debit(Money.of("100.00", "USD"), "Coffee", "FOOD_AND_DRINK");
        account.pullDomainEvents(); // drain once

        assertTrue(account.pullDomainEvents().isEmpty());
    }

    @Test
    @DisplayName("should not allow debit on frozen account")
    void shouldRejectDebitOnFrozenAccount() {
        account.freeze();

        assertThrows(IllegalStateException.class, () ->
                account.debit(Money.of("100.00", "USD"), "Coffee", "FOOD_AND_DRINK")
        );
    }

    @Test
    @DisplayName("should not close account with non-zero balance")
    void shouldRejectClosingAccountWithBalance() {
        assertThrows(IllegalStateException.class, () -> account.close());
    }
}