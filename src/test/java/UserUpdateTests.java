import io.qameta.allure.Allure;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@RunWith(JUnit4.class)
@Epic("API тесты для Stellar Burgers")
@Feature("Обновление данных пользователя")
public class UserUpdateTests extends TestBase {
    private final String testEmail = "unique_user_" + System.currentTimeMillis() + "@example.com";
    private final String testPassword = "password123";
    private final String testName = "Unique User";
    private final String updatedEmail = "updated_user_" + System.currentTimeMillis() + "@example.com";

    @Before
    public void setUpTestUser() {
        Allure.step("Создание тестового пользователя", () -> {
            Response response = registerNewUser(testEmail, testPassword, testName);
            verifySuccessfulRegistration(response, testEmail, testName);
        });
    }

    @Test
    @Story("Обновление профиля с аутентификацией")
    public void updateUserProfileWithAuth() {
        String authToken = getAuthToken(testEmail, testPassword);
        String updateData = String.format(
                "{\"email\":\"%s\",\"name\":\"Updated Name\"}",
                updatedEmail
        );

        Response updateResponse = given(requestSpec)
                .header("Authorization", authToken)
                .body(updateData)
                .patch(USER_ENDPOINT);

        verifyResponseCode(updateResponse, 200);
        updateResponse.then()
                .body("success", is(true))
                .body("user.email", equalTo(updatedEmail))
                .body("user.name", equalTo("Updated Name"));

        removeTestUser(updatedEmail, testPassword);
    }

    @Test
    @Story("Обновление профиля без аутентификации")
    public void updateUserProfileWithoutAuth() {
        String updateData = String.format(
                "{\"email\":\"%s\",\"name\":\"Updated Name\"}",
                updatedEmail
        );

        Response response = given(requestSpec)
                .body(updateData)
                .patch(USER_ENDPOINT);

        verifyResponseCode(response, 401);
        removeTestUser(testEmail, testPassword);
    }
}