package api.test;

import api.step.UserClient;
import api.step.OrderClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class OrderTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private List<String> validIngredients;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();

        Response ingredientsResponse = orderClient.getIngredients();
        List<String> ids = ingredientsResponse.jsonPath().getList("data._id");
        if (ids != null && ids.size() >= 2) {
            validIngredients = Arrays.asList(ids.get(0), ids.get(1));
        } else {
            validIngredients = Arrays.asList("60d3b41abdacab0026a733c6", "609646e4dc916e00276b2870");
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверка, что авторизованный пользователь может создать заказ")
    public void createOrderWithAuthShouldReturnSuccess() {
        String email = "order_" + System.currentTimeMillis() + "@yandex.ru";
        Response userResponse = userClient.createUser(email, "password123", "OrderUser");
        String token = userResponse.jsonPath().getString("accessToken");

        Response response = orderClient.createOrder(validIngredients, token);

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
        assertNotNull(response.jsonPath().getString("order.number"));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка, что неавторизованный пользователь может создать заказ")
    public void createOrderWithoutAuthShouldReturnSuccess() {
        Response response = orderClient.createOrderWithoutAuth(validIngredients);

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
        assertNotNull(response.jsonPath().getString("order.number"));
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверка, что нельзя создать заказ без ингредиентов")
    public void createOrderWithoutIngredientsShouldReturnError() {
        List<String> emptyIngredients = Collections.emptyList();
        Response response = orderClient.createOrderWithoutAuth(emptyIngredients);

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("Ingredient ids must be provided",
                response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверка, что нельзя создать заказ с неверным хешем ингредиентов")
    public void createOrderWithInvalidHashShouldReturnError() {
        String email = "invalid_" + System.currentTimeMillis() + "@yandex.ru";
        Response userResponse = userClient.createUser(email, "password123", "InvalidUser");
        String token = userResponse.jsonPath().getString("accessToken");

        List<String> invalidIngredients = Collections.singletonList("invalid_hash");
        Response response = orderClient.createOrder(invalidIngredients, token);

        assertEquals(HttpStatus.SC_BAD_REQUEST, response.statusCode());
    }
}