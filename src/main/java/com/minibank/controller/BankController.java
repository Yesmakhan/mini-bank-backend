package com.minibank.controller;

import com.minibank.model.Account;
import com.minibank.model.Transaction;
import com.minibank.model.User;
import com.minibank.service.BankService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class BankController {

    private final BankService bankService;

    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {
        User user = bankService.createUser(request.fullName(), request.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return bankService.getAllUsers();
    }

    @PostMapping("/accounts")
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account account = bankService.createAccount(
                request.userId(),
                request.initialBalance(),
                request.currency()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @GetMapping("/accounts/{id}")
    public Account getAccount(@PathVariable Long id) {
        return bankService.getAccount(id);
    }

    @GetMapping("/users/{userId}/accounts")
    public List<Account> getUserAccounts(@PathVariable Long userId) {
        return bankService.getUserAccounts(userId);
    }

    @PostMapping("/transfers")
    public ResponseEntity<Transaction> transfer(@RequestBody TransferRequest request) {
        Transaction transaction = bankService.transfer(
                request.fromAccountId(),
                request.toAccountId(),
                request.amount()
        );
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/accounts/{id}/transactions")
    public List<Transaction> getAccountTransactions(@PathVariable Long id) {
        return bankService.getAccountTransactions(id);
    }

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    public record CreateUserRequest(String fullName, String email) {}
    public record CreateAccountRequest(Long userId, BigDecimal initialBalance, String currency) {}
    public record TransferRequest(Long fromAccountId, Long toAccountId, BigDecimal amount) {}
}