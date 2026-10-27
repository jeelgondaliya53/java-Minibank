package util;
import model.Command;
import model.TransactionType;
public final class CommandParser {
    private CommandParser() { }
    public static Command parse(String line) {
        if (line == null) throw new IllegalArgumentException("Command cannot be null.");
        String[] parts = line.trim().split("\\s+");
        if (parts.length != 3) throw new IllegalArgumentException("Expected: TYPE ACCOUNT_NUMBER AMOUNT");
        TransactionType type;
        long amount;
        try { type = TransactionType.valueOf(parts[0].toUpperCase()); }
        catch (IllegalArgumentException e) { throw new IllegalArgumentException("Unknown transaction type: " + parts[0]); }
        try { amount = Long.parseLong(parts[2]); }
        catch (NumberFormatException e) { throw new IllegalArgumentException("Amount must be a whole number."); }
        return new Command(type, parts[1], amount);
    }
}
