package api.step;

import api.model.OrderRequest;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.List;

public class OrderClient extends BaseClient {
    private static final Gson gson = new Gson();

    @Step("Получение списка ингредиентов")
    public Response getIngredients() {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .get("/ingredients");
    }

    @Step("Создание заказа с авторизацией")
    public Response createOrder(List<String> ingredients, String accessToken) {
        OrderRequest request = new OrderRequest(ingredients);
        String body = gson.toJson(request);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .post("/orders");
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderWithoutAuth(List<String> ingredients) {
        OrderRequest request = new OrderRequest(ingredients);
        String body = gson.toJson(request);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/orders");
    }
}