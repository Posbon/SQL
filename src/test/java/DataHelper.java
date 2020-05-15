import com.codeborne.selenide.SelenideElement;
import lombok.Value;
import lombok.val;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class DataHelper {
    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("12345");
    }

    public VerificationPage validLogin(DataHelper.AuthInfo info) {
        $("[data-test-id=login] input").setValue(info.getLogin());
        $("[data-test-id=password] input").setValue(info.getPassword());
        $("[data-test-id=action-login]").click();
        return new VerificationPage();
    }


    public static void getAuthInfo(String status) throws SQLException {
        SelenideElement codeField = $("[data-test-id=code] input");
        SelenideElement verifyBtn = $("[data-test-id=action-verify]");
        val authSQL = "select a.code from auth_codes a, users u where a.user_id=u.id and u.login= ? order by a.created desc limit 1;";
        try (
                val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
                val authStmt = conn.prepareStatement(authSQL);) {
            authStmt.setString(1, "vasya");
            open("http://localhost:9999");
            $("[data-test-id=login] input").setValue("vasya");
            $("[data-test-id=password] input").setValue("qwerty123");
            $("[data-test-id=action-login]").click();
            codeField.waitUntil(visible, 1000);
            try (val code = authStmt.executeQuery()) {
                while (code.next()) {
                    val auth_code = code.getString("code");
                    codeField.setValue(auth_code);
                }
            }
        }
    }
}
