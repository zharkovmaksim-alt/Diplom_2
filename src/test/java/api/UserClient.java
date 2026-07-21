package api;

import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserClient {
    private static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    public Response createUser(String email, String password, String name) {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\",\"name\":\"%s\"}", email, password, name);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(BASE_URL + "/auth/register");
    }

    public Response loginUser(String email, String password) {
        String body = String.format("{\"email\":\"%s\",\"password\":\"%s\"}", email, password);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .post(BASE_URL + "/auth/login");
    }
}
