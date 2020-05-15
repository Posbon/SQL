import lombok.val;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;



public class Data {
    public static void data() throws SQLException {
        String deleteCardTransactions = "delete from card_transactions;";
        String deleteCards = "delete from cards;";
        String deleteCodes = "delete from auth_codes;";
        String deleteUsers = "delete from users;";

        try (
                val conn = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/app", "app", "pass"
                );

                PreparedStatement statementCardTransactions = conn.prepareStatement(deleteCardTransactions);
                PreparedStatement statementCards = conn.prepareStatement(deleteCards);
                PreparedStatement statementCodes = conn.prepareStatement(deleteCodes);
                PreparedStatement statementUsers = conn.prepareStatement(deleteUsers);
        ) {
            statementCardTransactions.executeUpdate();
            statementCards.executeUpdate();
            statementCodes.executeUpdate();
            statementUsers.executeUpdate();

        }
    }
}
