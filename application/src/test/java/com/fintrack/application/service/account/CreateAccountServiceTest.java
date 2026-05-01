package com.fintrack.application.service.account;

import com.fintrack.application.port.in.account.CreateAccountUseCase;
import com.fintrack.application.port.out.account.SaveAccountPort;
import com.fintrack.application.port.out.notification.DomainEventPublisherPort;
import com.fintrack.domain.model.account.Account;
import com.fintrack.domain.model.account.AccountType;
import com.fintrack.domain.model.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAccountServiceTest {

    @Mock SaveAccountPort saveAccountPort;
    @Mock DomainEventPublisherPort eventPublisher;

    CreateAccountService service;

    @BeforeEach
    void setUp() {
        service = new CreateAccountService(saveAccountPort, eventPublisher);
    }

    @Test
    @DisplayName("should create and save account with correct properties")
    void shouldCreateAndSaveAccount() {
        // Arrange
        var command = new CreateAccountUseCase.Command(
                UserId.generate(),
                AccountType.CHECKING,
                "My Account",
                BigDecimal.valueOf(500),
                Currency.getInstance("USD")
        );
        when(saveAccountPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Account result = service.createAccount(command);

        // Assert
        assertNotNull(result.getId());
        assertEquals("My Account", result.getName());
        assertEquals(AccountType.CHECKING, result.getType());
        verify(saveAccountPort).save(any(Account.class));
    }

    @Test
    @DisplayName("should publish domain events after creation")
    void shouldPublishDomainEvents() {
        var command = new CreateAccountUseCase.Command(
                UserId.generate(),
                AccountType.SAVINGS,
                "Savings",
                BigDecimal.ZERO,
                Currency.getInstance("USD")
        );
        when(saveAccountPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.createAccount(command);

        verify(eventPublisher).publishAll(anyList());
    }

    @Test
    @DisplayName("should reject command with negative initial balance")
    void shouldRejectNegativeBalance() {
        assertThrows(IllegalArgumentException.class, () ->
                new CreateAccountUseCase.Command(
                        UserId.generate(),
                        AccountType.CHECKING,
                        "Bad Account",
                        BigDecimal.valueOf(-100),
                        Currency.getInstance("USD")
                )
        );
        verifyNoInteractions(saveAccountPort, eventPublisher);
    }
}