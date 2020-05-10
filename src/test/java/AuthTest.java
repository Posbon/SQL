import com.codeborne.selenide.SelenideElement;
import com.github.javafaker.Faker;
import lombok.val;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class AuthTest {
    @BeforeAll
    static void newUser () throws SQLException {
        val faker = new Faker();
        val dataSQL = "INSERT INTO users(id, login, password) VALUES (?, ?, ?);";
        try (val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass");
             val dataStmt = conn.prepareStatement(dataSQL);) {
            dataStmt.setString(1, faker.bothify("********"));
            dataStmt.setString(2, "test");
            dataStmt.setString(3, "pass");
            dataStmt.executeUpdate();
        }
    }

    @AfterAll
    static void cleanData () throws SQLException {
        Data.data();
    }

    @Test
    void validLoginTest () throws SQLException {
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
            verifyBtn.shouldBe(visible).click();
            $("[data-test-id=dashboard]").waitUntil(visible, 1000);
        }
    }
}
