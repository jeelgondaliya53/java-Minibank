package app;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.*;
import service.AccountWorker;
import service.TransactionBuffer;
import service.TransactionProcessor;
import util.AnnotationValidator;
import util.CommandParser;
import util.StatementFormatter;
import util.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;

public class MiniBank {
    private static final List<Account> accounts = new ArrayList<>();

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("--demo")) {
            runDemonstrations();
            return;
        }
        runMenu();
    }

    private static void runMenu() {
        Scanner sc = new Scanner(System.in);
        BankInfo bank = new BankInfo("MiniBank", "Ahmedabad Branch");
        out.println("====================================");
        out.println("Welcome to " + bank.name());
        out.println("Branch: " + bank.branch());
        out.println("====================================");
        int choice;
        do {
            out.println("\n----- MiniBank Menu -----");
            out.println("1. Open Account");
            out.println("2. Deposit");
            out.println("3. Withdraw");
            out.println("4. Transfer");
            out.println("5. Print Account Statement");
            out.println("6. List Accounts");
            out.println("7. Exit");
            out.print("Enter your choice: ");
            while (!sc.hasNextInt()) {
                out.print("Please enter a number: "); sc.next();
            }
            choice = sc.nextInt();
            switch (choice) {
                case 1 -> openAccount(sc);
                case 2 -> transact(sc, true);
                case 3 -> transact(sc, false);
                case 4 -> transfer(sc);
                case 5 -> statement(sc);
                case 6 -> accounts.forEach(out::println);
                case 7 -> out.println("Thank you for using MiniBank. Goodbye!");
                default -> out.println("Invalid choice. Please try again.");
            }
        } while (choice != 7);
        sc.close();
    }

    private static void openAccount(Scanner sc) {
        out.print("Account holder name: ");
        String name = sc.next();
        out.println("Account type: 1. Savings  2. Current  3. Fixed Deposit");
        out.print("Choose type: ");
        int type = readInt(sc);
        out.print("Opening balance (whole rupees): ");
        long balance = readLong(sc);
        try {
            Account account;
            if (type == 1) account = new SavingsAccount(name, balance, 500);
            else if (type == 2) account = new CurrentAccount(name, balance, 5000);
            else if (type == 3) account = new FixedDepositAccount(name, balance);
            else { out.println("Invalid account type."); return; }
            accounts.add(account);
            out.println("Account created successfully: " + account.getAccountNumber());
        } catch (Exception e) { out.println("Could not create account: " + e.getMessage()); }
    }

    private static void transact(Scanner sc, boolean deposit) {
        Account account = findAccount(sc);
        if (account == null) return;
        out.print("Amount: "); long amount = readLong(sc);
        try {
            if (deposit) { account.deposit(amount); out.println("Deposit successful."); }
            else { account.withdraw(amount); out.println("Withdrawal successful."); }
            out.println("New balance: Rs. " + account.getBalance());
        } catch (BankException e) { out.println("Transaction failed: " + e.getMessage()); }
    }

    private static void transfer(Scanner sc) {
        out.print("From account number: "); String fromNo = sc.next();
        out.print("To account number: "); String toNo = sc.next();
        Account from = findByNumber(fromNo), to = findByNumber(toNo);
        if (from == null || to == null) { out.println("Account not found."); return; }
        out.print("Amount: "); long amount = readLong(sc);
        try { from.transfer(to, amount); out.println("Transfer successful."); }
        catch (BankException e) { out.println("Transfer failed: " + e.getMessage()); }
    }

    private static void statement(Scanner sc) {
        Account account = findAccount(sc);
        if (account != null) out.println(StatementFormatter.buildStatement(account));
    }
    private static Account findAccount(Scanner sc) {
        out.print("Account number: ");
        Account account = findByNumber(sc.next());
        if (account == null) out.println("Account not found.");
        return account;
    }
    private static Account findByNumber(String number) {
        return accounts.stream().filter(a -> a.getAccountNumber().equalsIgnoreCase(number)).findFirst().orElse(null);
    }
    private static int readInt(Scanner sc) {
        while (!sc.hasNextInt()) { out.print("Enter a whole number: "); sc.next(); }
        return sc.nextInt();
    }
    private static long readLong(Scanner sc) {
        while (!sc.hasNextLong()) { out.print("Enter a whole number: "); sc.next(); }
        return sc.nextLong();
    }

    private static void runDemonstrations() {
        out.println("=== Practical 1: Header and menu types ===");
        BankInfo info = new BankInfo("MiniBank", "Ahmedabad Branch");
        out.println(info);
        for (MenuOption option : MenuOption.values()) out.println(option);

        out.println("\n=== Practicals 2-3: Accounts, equals, clone and polymorphism ===");
        SavingsAccount savings = new SavingsAccount("Asha", 5000, 500);
        CurrentAccount current = new CurrentAccount("Ravi", 1000, 2000);
        FixedDepositAccount fixed = new FixedDepositAccount("Mira", 10000);
        Account[] sample = {savings, current, fixed};
        try { savings.deposit(1000); savings.withdraw(500); current.deposit(200); }
        catch (BankException e) { out.println(e.getMessage()); }
        for (Account a : sample) out.println(a + " | interest=" + a.interestRate() + "%");
        out.println("Savings equals itself: " + savings.equals(savings));
        Object any = savings;
        out.println("Object is a SavingsAccount: " + (any instanceof SavingsAccount));
        Customer customer = new Customer("Asha", "asha@example.com", "9876543210",
                new Customer.Address("12 Main Road", "Ahmedabad", "380001"));
        out.println("Customer id: " + customer.getCustomerId() + ", cloned id: " + customer.clone().getCustomerId());

        out.println("\n=== Practical 4: Validators, parser and statement ===");
        out.println("Mobile valid: " + Validator.isValidMobile("9876543210") + ", invalid: " + Validator.isValidMobile("123"));
        out.println("Email valid: " + Validator.isValidEmail("asha@example.com") + ", invalid: " + Validator.isValidEmail("wrong-email"));
        out.println("PAN valid: " + Validator.isValidPan("ABCDE1234F") + ", invalid: " + Validator.isValidPan("123"));
        out.println("IFSC valid: " + Validator.isValidIfsc("SBIN0001234") + ", invalid: " + Validator.isValidIfsc("bad"));
        Command command = CommandParser.parse("DEPOSIT " + savings.getAccountNumber() + " 500");
        out.println("Parsed command: " + command.type() + ", " + command.accountNumber() + ", " + command.amount());
        out.println(StatementFormatter.buildStatement(savings));

        out.println("\n=== Practical 5-6: Interfaces and withdraw rules ===");
        WithdrawRule anonymousRule = new WithdrawRule() {
            @Override public boolean allow(Account account, long amount) { return account.canWithdraw(amount); }
        };
        WithdrawRule lambdaRule = (account, amount) -> account.canWithdraw(amount);
        out.println("Anonymous rule allows Rs. 100: " + anonymousRule.allow(savings, 100));
        out.println("Lambda rule allows Rs. 100: " + lambdaRule.allow(savings, 100));
        out.println("Savings yearly interest estimate: Rs. " + savings.yearlyInterest());
        if (savings instanceof Premium premiumAccount) out.println("Premium account");
        else out.println("Savings account is not marked premium.");

        out.println("\n=== Practical 7: Annotation validation ===");
        CurrentAccount invalidForAnnotationDemo = new CurrentAccount("Test", -10, 100);
        for (String error : AnnotationValidator.validate(invalidForAnnotationDemo)) out.println("Validation error: " + error);

        out.println("\n=== Practical 8: Custom exceptions and transfer ===");
        try { savings.withdraw(-1); }
        catch (BankException e) { out.println("Friendly error: " + e.getMessage()); }
        try { savings.transfer(current, 100); out.println("Transfer demo completed."); }
        catch (BankException e) { out.println("Transfer error: " + e.getMessage()); }
        try (DemoResource resource = new DemoResource()) { out.println("Try-with-resources example running."); }

        out.println("\n=== Practical 9: Synchronized account workers ===");
        CurrentAccount threaded = new CurrentAccount("Thread Test", 0, 0);
        Thread[] workers = new Thread[10];
        for (int i = 0; i < workers.length; i++) workers[i] = new Thread(new AccountWorker(threaded, 100, 10), "Worker-" + (i + 1));
        for (Thread worker : workers) worker.start();
        for (Thread worker : workers) {
            try { worker.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); return; }
        }
        out.println("Expected final balance Rs. 10000; actual balance Rs. " + threaded.getBalance());

        out.println("\n=== Practical 10: Executor, producer-consumer and safe lock ordering ===");
        TransactionProcessor processor = new TransactionProcessor();
        for (int i = 0; i < 20; i++) processor.submit(() -> {
            try { threaded.deposit(1); } catch (InvalidAmountException e) { out.println(e.getMessage()); }
        });
        processor.stop();
        out.println("Balance after executor tasks: Rs. " + threaded.getBalance());
        runProducerConsumerDemo(threaded);
        Thread t1 = new Thread(() -> safeTransfer(threaded, current, 1), "Transfer-A-to-B");
        Thread t2 = new Thread(() -> safeTransfer(current, threaded, 1), "Transfer-B-to-A");
        t1.start(); t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        out.println("Both transfer threads completed using consistent account lock ordering.");
        out.println("\nAll practical demonstrations finished.");
    }

    private static void safeTransfer(Account from, Account to, long amount) {
        try { from.transfer(to, amount); }
        catch (BankException e) { out.println(Thread.currentThread().getName() + ": " + e.getMessage()); }
    }

    private static void runProducerConsumerDemo(Account account) {
        TransactionBuffer buffer = new TransactionBuffer();
        Thread consumer = new Thread(() -> {
            try {
                Runnable task;
                while ((task = buffer.take()) != null) task.run();
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }, "Transaction-Consumer");
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) buffer.put(() -> {
                    try { account.deposit(2); } catch (InvalidAmountException e) { out.println(e.getMessage()); }
                });
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            finally { buffer.finish(); }
        }, "Transaction-Producer");
        consumer.start(); producer.start();
        try { producer.join(); consumer.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        out.println("Producer-consumer transactions completed.");
    }

    private static class DemoResource implements AutoCloseable {
        @Override public void close() { out.println("AutoCloseable resource closed."); }
    }
}
