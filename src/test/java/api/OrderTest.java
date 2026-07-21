package api;

import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class OrderTest {
    private UserClient userClient;
    private OrderClient orderClient;
    private String validIngredients;

    @Before
    public void setUp() {
        userClient = new UserClient();
        orderClient = new OrderClient();

        Response ingredientsResponse = orderClient.getIngredients();
        List<String> ids = ingredientsResponse.jsonPath().getList("data._id");
        if (ids != null && ids.size() >= 2) {
            validIngredients = "[\"" + ids.get(0) + "\", \"" + ids.get(1) + "\"]";
        } else {
            validIngredients = "[\"60d3b41abdacab0026a733c6\", \"609646e4dc916e00276b2870\"]";
        }
    }

    @Test
    public void createOrderWithAuthShouldReturnSuccess() {
        String email = "order_" + System.currentTimeMillis() + "@yandex.ru";
        Response userResponse = userClient.createUser(email, "password123", "OrderUser");
        String token = userResponse.jsonPath().getString("accessToken");

        Response response = orderClient.createOrder(validIngredients, token);

        assertEquals(200, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
        assertNotNull(response.jsonPath().getString("order.number"));
    }

    @Test
    public void createOrderWithoutAuthShouldReturnSuccess() {
        Response response = orderClient.createOrderWithoutAuth(validIngredients);

        assertEquals(200, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
        assertNotNull(response.jsonPath().getString("order.number"));
    }

    @Test
    public void createOrderWithoutIngredientsShouldReturnError() {
        Response response = orderClient.createOrderWithoutAuth("[]");

        assertEquals(400, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("Ingredient ids must be provided",
                response.jsonPath().getString("message"));
    }

    @Test
    public void createOrderWithInvalidHashShouldReturnError() {
        String email = "invalid_" + System.currentTimeMillis() + "@yandex.ru";
        Response userResponse = userClient.createUser(email, "password123", "InvalidUser");
        String token = userResponse.jsonPath().getString("accessToken");

        Response response = orderClient.createOrder("[\"invalid_hash\"]", token);

        assertEquals(400, response.statusCode());
    }
}