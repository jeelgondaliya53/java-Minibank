
package service;

import model.Account;
import exception.BankException;
import exception.InvalidAmountException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TransactionProcessor {

    private final ExecutorService executor =
            Executors.newFixedThreadPool(4);

    // Thread-safe account storage for concurrent access.
    private final ConcurrentHashMap<String, Account> sharedAccounts =
            new ConcurrentHashMap<>();

    public void registerAccount(Account account) {
        if (account == null) {
            throw new IllegalArgumentException(
                    "Account cannot be null.");
        }

        sharedAccounts.put(
                account.getAccountNumber(), account);
    }

    public Account getAccount(String accountNumber) {
        return sharedAccounts.get(accountNumber);
    }

    public ConcurrentHashMap<String, Account> getSharedAccounts() {
        return new ConcurrentHashMap<>(sharedAccounts);
    }

    // Practical 10: submit a task to the thread pool.
    public void submit(Runnable task) {
        if (task == null) {
            throw new IllegalArgumentException(
                    "Task cannot be null.");
        }

        executor.execute(task);
    }

    public void submitDeposit(String accountNumber, long amount) {
        submit(() -> {
            Account account = sharedAccounts.get(accountNumber);

            if (account == null) {
                System.out.println(
                        "Account not found: " + accountNumber);
                return;
            }

            try {
                account.deposit(amount);

                TransactionLog.append(
                        java.nio.file.Path.of("logs", "transactions.log"),
                        java.time.LocalDateTime.now()
                                + "|DEPOSIT|" + amount
                                + "|" + accountNumber
                                + "|" + account.getBalance());

            } catch (InvalidAmountException
                     | java.io.IOException e) {
                System.out.println(
                        "Deposit failed: " + e.getMessage());
            }
        });
    }

    public void submitWithdrawal(
            String accountNumber, long amount) {

        submit(() -> {
            Account account = sharedAccounts.get(accountNumber);

            if (account == null) {
                System.out.println(
                        "Account not found: " + accountNumber);
                return;
            }

            try {
                account.withdraw(amount);

                TransactionLog.append(
                        java.nio.file.Path.of("logs", "transactions.log"),
                        java.time.LocalDateTime.now()
                                + "|WITHDRAW|" + amount
                                + "|" + accountNumber
                                + "|" + account.getBalance());

            } catch (BankException
                     | java.io.IOException e) {
                System.out.println(
                        "Withdrawal failed: " + e.getMessage());
            }
        });
    }

    // Practical 10: wait for submitted tasks to finish.
    public void stop() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(
                    30, TimeUnit.SECONDS)) {

                executor.shutdownNow();

                if (!executor.awaitTermination(
                        10, TimeUnit.SECONDS)) {
                    System.err.println(
                            "Some tasks did not terminate.");
                }
            }

        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
