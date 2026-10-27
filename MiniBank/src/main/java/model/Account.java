package model;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;

public abstract class Account implements Transactable, InterestBearing {
    @Id private final String accountNumber;
    @MaxLength(100) private String ownerName;
    @Positive(message = "balance must be > 0") private long balance;
    private boolean active = true;
    private static long accountCounter = 0;

    protected Account(String ownerName, long openingBalance) {
        this.accountNumber = String.format("AC%04d", ++accountCounter);
        this.ownerName = ownerName;
        this.balance = openingBalance;
    }
    protected Account(String ownerName) { this(ownerName, 0); }

    public String getAccountNumber() { return accountNumber; }
    public String getOwnerName() { return ownerName; }
    @Override public synchronized long getBalance() { return balance; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    protected synchronized void setBalanceForTesting(long balance) { this.balance = balance; }

    @Override public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Deposit amount must be greater than zero.");
        balance = Math.addExact(balance, amount);
    }

    @Override public synchronized boolean withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Withdrawal amount must be greater than zero.");
        if (!active || !canWithdraw(amount)) {
            long shortfall = Math.max(1L, amount - Math.max(0L, balance));
            throw new InsufficientFundsException("Withdrawal not allowed for account " + accountNumber, shortfall);
        }
        balance -= amount;
        return true;
    }

    public abstract double interestRate();
    public abstract boolean canWithdraw(long amount);

    public void transfer(Account to, long amount) throws BankException {
        if (to == null) throw new BankException("Destination account was not found.");
        if (to == this) throw new BankException("Cannot transfer to the same account.");
        try {
            if (amount <= 0) throw new InvalidAmountException("Transfer amount must be greater than zero.");
            Account first = this.accountNumber.compareTo(to.accountNumber) < 0 ? this : to;
            Account second = first == this ? to : this;
            synchronized (first) {
                synchronized (second) {
                    this.withdraw(amount);
                    to.deposit(amount);
                }
            }
        } catch (BankException e) {
            throw e;
        } finally {
            // Hook for transaction logging/cleanup; locks are released automatically.
        }
    }

    @Override public String toString() {
        return "Account{" + "accountNumber='" + accountNumber + '\'' +
                ", ownerName='" + ownerName + '\'' + ", balance=" + getBalance() + '}';
    }
    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account account)) return false;
        return accountNumber.equals(account.accountNumber);
    }
    @Override public int hashCode() { return accountNumber.hashCode(); }
}
