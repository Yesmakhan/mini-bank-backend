package com.minibank;

import com.minibank.model.Account;
import com.minibank.model.Transaction;
import com.minibank.repository.AccountRepository;
import com.minibank.repository.TransactionRepository;
import com.minibank.repository.UserRepository;
import com.minibank.service.BankService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private BankService bankService;

    @BeforeEach
    void setUp() {
        bankService = new BankService(userRepository, accountRepository, transactionRepository);
    }

    @Test
    void transfer_Success() {
        Account fromAccount = new Account(1L, 10L, new BigDecimal("10000.00"), "KZT", LocalDateTime.now());
        Account toAccount = new Account(2L, 20L, new BigDecimal("5000.00"), "KZT", LocalDateTime.now());
        BigDecimal transferAmount = new BigDecimal("3000.00");

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));
        when(transactionRepository.save(eq(1L), eq(2L), eq(transferAmount), eq("COMPLETED")))
                .thenReturn(new Transaction(100L, 1L, 2L, transferAmount, "COMPLETED", LocalDateTime.now()));

        Transaction result = bankService.transfer(1L, 2L, transferAmount);

        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());

        verify(accountRepository).updateBalance(1L, new BigDecimal("7000.00"));
        verify(accountRepository).updateBalance(2L, new BigDecimal("8000.00"));
    }

    @Test
    void transfer_InsufficientFunds_ThrowsException() {
        Account fromAccount = new Account(1L, 10L, new BigDecimal("1000.00"), "KZT", LocalDateTime.now());
        Account toAccount = new Account(2L, 20L, new BigDecimal("5000.00"), "KZT", LocalDateTime.now());

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromAccount));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toAccount));

        assertThrows(IllegalStateException.class, () ->
                bankService.transfer(1L, 2L, new BigDecimal("5000.00"))
        );

        verify(accountRepository, never()).updateBalance(anyLong(), any());
    }

    @Test
    void transfer_ReverseDirection_LocksInAscendingOrder() {
        Account account1 = new Account(1L, 10L, new BigDecimal("5000.00"), "KZT", LocalDateTime.now());
        Account account2 = new Account(2L, 20L, new BigDecimal("10000.00"), "KZT", LocalDateTime.now());
        BigDecimal amount = new BigDecimal("2000.00");

        when(accountRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(account2));
        when(transactionRepository.save(eq(2L), eq(1L), eq(amount), eq("COMPLETED")))
                .thenReturn(new Transaction(101L, 2L, 1L, amount, "COMPLETED", LocalDateTime.now()));

        bankService.transfer(2L, 1L, amount);

        InOrder inOrder = inOrder(accountRepository);
        inOrder.verify(accountRepository).findByIdForUpdate(1L);
        inOrder.verify(accountRepository).findByIdForUpdate(2L);
    }
}