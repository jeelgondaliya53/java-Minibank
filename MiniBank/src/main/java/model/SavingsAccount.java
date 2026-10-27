package model;
public class SavingsAccount extends Account {
    private final long minBalance;
    public SavingsAccount(String ownerName, long openingBalance, long minBalance) {
        super(ownerName, openingBalance); this.minBalance = Math.max(0, minBalance);
    }
    public SavingsAccount(String ownerName, long minBalance) { this(ownerName, 0, minBalance); }
    public long getMinBalance() { return minBalance; }
    @Override public double interestRate() { return 4.0; }
    @Override public boolean canWithdraw(long amount) { return getBalance() - amount >= minBalance; }
}
