import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@Epic("API тесты для Stellar Burgers")
@Feature("Аутентификация пользователя")
public class UserLoginTests extends TestBase {
    private final String testEmail = "unique_user_" + System.currentTimeMillis() + "@example.com";
    private final String testPassword = "password123";
    private final String testName = "Unique User";

    @Test
    @Story("Успешный вход с валидными данными")
    public void successfulLoginWithValidCredentials() {
        Response registrationResponse = registerNewUser(testEmail, testPassword, testName);
        verifySuccessfulRegistration(registrationResponse, testEmail, testName);

        Response loginResponse = authenticateUser(testEmail, testPassword);
        verifySuccessfulLogin(loginResponse, testEmail, testName);

        removeTestUser(testEmail, testPassword);
    }

    @Test
    @Story("Попытка входа без пароля")
    public void loginAttemptWithoutPassword() {
        registerNewUser(testEmail, testPassword, testName);
        Response loginResponse = authenticateUser(testEmail, "");

        verifyResponseCode(loginResponse, 401);
        verifyErrorMessage(loginResponse, "email or password are incorrect");
        removeTestUser(testEmail, testPassword);
    }

    @Test
    @Story("Попытка входа с неверными данными")
    public void loginAttemptWithInvalidCredentials() {
        String invalidCredentials = "{\"email\":\"invalid@user.com\",\"password\":\"wrong\"}";
        Response response = performLogin(invalidCredentials);

        verifyResponseCode(response, 401);
        verifyErrorMessage(response, "email or password are incorrect");
    }
}