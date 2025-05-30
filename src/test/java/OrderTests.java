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
@Feature("Работа с заказами")
public class OrderTests extends TestBase {
    private final String testEmail = "unique_user_" + System.currentTimeMillis() + "@example.com";
    private final String testPassword = "password123";
    private final String testName = "Unique User";

    @Test
    @Story("Создание заказа с аутентификацией")
    public void createOrderWithAuth() {
        registerNewUser(testEmail, testPassword, testName);
        String authToken = getAuthToken(testEmail, testPassword);

        String validIngredients = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa70\", \"61c0c5a71d1f82001bdaaa72\"] }";
        Response response = given(requestSpec)
                .header("Authorization", authToken)
                .body(validIngredients)
                .post(ORDERS_ENDPOINT);

        verifyOrderCreation(response);
        removeTestUser(testEmail, testPassword);
    }

    @Test
    @Story("Создание заказа без аутентификации")
    public void createOrderWithoutAuth() {
        String orderData = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa70\", \"61c0c5a71d1f82001bdaaa72\"] }";
        Response response = given(requestSpec)
                .body(orderData)
                .post(ORDERS_ENDPOINT);

        verifyResponseCode(response, 200);
    }

    @Test
    @Story("Создание пустого заказа")
    public void createEmptyOrder() {
        Response response = given(requestSpec)
                .body("{ \"ingredients\": [] }")
                .post(ORDERS_ENDPOINT);

        verifyResponseCode(response, 400);
    }

    @Test
    @Story("Создание заказа с невалидными ингредиентами")
    public void createOrderWithInvalidIngredients() {
        Response response = given(requestSpec)
                .body("{ \"ingredients\": [\"invalid1\", \"invalid2\"] }")
                .post(ORDERS_ENDPOINT);

        verifyResponseCode(response, 500);
    }
}