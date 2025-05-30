import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static io.restassured.RestAssured.given;

@RunWith(JUnit4.class)
@Epic("API тесты для Stellar Burgers")
@Feature("Работа с пользователем")
public class UserCreationTests extends TestBase {
    private final String testEmail = "unique_user_" + System.currentTimeMillis() + "@example.com";
    private final String testPassword = "password123";
    private final String testName = "Unique User";

    @Test
    @Story("Успешная регистрация нового пользователя")
    public void registerNewUserSuccessfully() {
        Response response = registerNewUser(testEmail, testPassword, testName);
        verifySuccessfulRegistration(response, testEmail, testName);
        removeTestUser(testEmail, testPassword);
    }

    @Test
    @Story("Регистрация дубликата пользователя")
    public void registerDuplicateUser() {
        registerNewUser(testEmail, testPassword, testName);
        Response duplicateResponse = registerNewUser(testEmail, testPassword, testName);

        verifyResponseCode(duplicateResponse, 403);
        verifyErrorMessage(duplicateResponse, "User already exists");
        removeTestUser(testEmail, testPassword);
    }

    @Test
    @Story("Регистрация без пароля")
    public void registerUserWithoutPassword() {
        String incompleteData = String.format(
                "{\"email\":\"%s\",\"password\":\"\",\"name\":\"%s\"}",
                testEmail, testName
        );
        Response response = given(requestSpec)
                .body(incompleteData)
                .post(REGISTER_ENDPOINT);

        verifyResponseCode(response, 403);
        verifyErrorMessage(response, "Email, password and name are required fields");
    }
}