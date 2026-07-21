package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class OrderClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    public Response getIngredients() {
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .get(BASE_URL + "/ingredients");
    }

    public Response createOrder(String ingredients, String accessToken) {
        String body = String.format("{\"ingredients\":%s}", ingredients);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .body(body)
                .post(BASE_URL + "/orders");
    }

    public Response createOrderWithoutAuth(String ingredients) {
        String body = String.format("{\"ingredients\":%s}", ingredients);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(BASE_URL + "/orders");
    }
}