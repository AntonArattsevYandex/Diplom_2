import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
@Epic("API тесты для Stellar Burgers")
@Feature("Получение заказов пользователя")
public class UserOrdersTest extends TestBase {
    private final String testEmail = "unique_user_" + System.currentTimeMillis() + "@example.com";
    private final String testPassword = "password123";
    private final String testName = "Unique User";

    @Test
    @Story("Получение заказов с валидной аутентификацией")
    public void getOrdersWithValidAuth() {
        registerNewUser(testEmail, testPassword, testName);
        String authToken = getAuthToken(testEmail, testPassword);

        String orderData = "{ \"ingredients\": [\"61c0c5a71d1f82001bdaaa70\", \"61c0c5a71d1f82001bdaaa72\"] }";
        Response orderResponse = createOrder(orderData, authToken);
        verifyOrderCreation(orderResponse);

        Response ordersResponse = fetchUserOrders(authToken);
        verifyOrdersResponse(ordersResponse);
        removeTestUser(testEmail, testPassword);
    }

    @Test
    @Story("Получение заказов без аутентификации")
    public void getOrdersWithoutAuth() {
        Response response = fetchOrdersWithoutAuth();
        verifyResponseCode(response, 401);
    }
}