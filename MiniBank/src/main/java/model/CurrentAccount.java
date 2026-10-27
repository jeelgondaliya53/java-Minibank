package model;
public class CurrentAccount extends Account {
    private final long overdraftLimit;
    public CurrentAccount(String ownerName, long openingBalance, long overdraftLimit) {
        super(ownerName, openingBalance); this.overdraftLimit = Math.max(0, overdraftLimit);
    }
    public CurrentAccount(String ownerName, long overdraftLimit) { this(ownerName, 0, overdraftLimit); }
    public long getOverdraftLimit() { return overdraftLimit; }
    @Override public double interestRate() { return 0.0; }
    @Override public boolean canWithdraw(long amount) { return getBalance() - amount >= -overdraftLimit; }
}
