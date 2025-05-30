import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Before;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class TestBase {
    protected static final String BASE_URL = "https://stellarburgers.nomoreparties.site/";
    protected static final String REGISTER_ENDPOINT = "/api/auth/register";
    protected static final String LOGIN_ENDPOINT = "/api/auth/login";
    protected static final String USER_ENDPOINT = "/api/auth/user";
    protected static final String ORDERS_ENDPOINT = "/api/orders";

    protected RequestSpecification requestSpec;

    @Before
    public void setupTestConfig() {
        Allure.step("Настройка базовой конфигурации", () -> {
            requestSpec = given()
                    .baseUri(BASE_URL)
                    .contentType(ContentType.JSON)
                    .log().all();
        });
    }

    @Step("Регистрация нового пользователя")
    public Response registerNewUser(String email, String password, String name) {
        String userData = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\",\"name\":\"%s\"}",
                email, password, name
        );
        return given(requestSpec)
                .body(userData)
                .post(REGISTER_ENDPOINT);
    }

    @Step("Проверка успешной регистрации")
    public void verifySuccessfulRegistration(Response response, String email, String name) {
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name))
                .body("accessToken", not(emptyOrNullString()))
                .body("refreshToken", not(emptyOrNullString()));
    }

    @Step("Аутентификация пользователя")
    public Response authenticateUser(String email, String password) {
        String credentials = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        return given(requestSpec)
                .body(credentials)
                .post(LOGIN_ENDPOINT);
    }

    @Step("Проверка успешной аутентификации")
    public void verifySuccessfulLogin(Response response, String email, String name) {
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(email))
                .body("user.name", equalTo(name));
    }

    @Step("Удаление тестового пользователя")
    public void removeTestUser(String email, String password) {
        Response loginResponse = authenticateUser(email, password);
        String token = loginResponse.path("accessToken");

        given(requestSpec)
                .header("Authorization", token)
                .delete(USER_ENDPOINT)
                .then()
                .statusCode(202);
    }

    @Step("Получение токена аутентификации")
    public String getAuthToken(String email, String password) {
        Response response = authenticateUser(email, password);
        return response.path("accessToken");
    }

    @Step("Создание нового заказа")
    public Response createOrder(String orderData, String token) {
        return given(requestSpec)
                .header("Authorization", token)
                .body(orderData)
                .post(ORDERS_ENDPOINT);
    }

    @Step("Проверка создания заказа")
    public void verifyOrderCreation(Response response) {
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("name", not(emptyOrNullString()))
                .body("order.number", not(emptyOrNullString()));
    }

    @Step("Получение заказов пользователя")
    public Response fetchUserOrders(String token) {
        return given(requestSpec)
                .header("Authorization", token)
                .get(ORDERS_ENDPOINT);
    }

    @Step("Проверка ответа с заказами")
    public void verifyOrdersResponse(Response response) {
        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("orders", not(empty()))
                .body("total", greaterThanOrEqualTo(0))
                .body("totalToday", greaterThanOrEqualTo(0));
    }

    @Step("Получение заказов без аутентификации")
    public Response fetchOrdersWithoutAuth() {
        return given(requestSpec)
                .get(ORDERS_ENDPOINT);
    }

    @Step("Проверка кода ответа")
    public void verifyResponseCode(Response response, int expectedCode) {
        response.then().statusCode(expectedCode);
    }

    @Step("Проверка сообщения об ошибке")
    public void verifyErrorMessage(Response response, String message) {
        response.then().body("message", equalTo(message));
    }

    @Step("Выполнение входа с JSON")
    public Response performLogin(String json) {
        return given(requestSpec)
                .body(json)
                .post(LOGIN_ENDPOINT);
    }
}