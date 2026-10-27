package util;
import model.Account;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public final class StatementFormatter {
    private StatementFormatter() { }
    public static String buildStatement(Account account) {
        StringBuilder sb = new StringBuilder();
        sb.append("========== ACCOUNT STATEMENT ==========\n");
        sb.append("Account Number: ").append(account.getAccountNumber()).append('\n');
        sb.append("Account Holder: ").append(account.getOwnerName()).append('\n');
        sb.append("Balance: Rs. ").append(account.getBalance()).append('\n');
        sb.append("Interest Rate: ").append(account.interestRate()).append("%\n");
        sb.append("Generated: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))).append('\n');
        sb.append("=======================================");
        return sb.toString();
    }
}
