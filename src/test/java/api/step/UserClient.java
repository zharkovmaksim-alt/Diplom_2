package api.step;

import api.model.UserRequest;
import api.model.LoginRequest;
import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class UserClient extends BaseClient {
    private static final Gson gson = new Gson();

    @Step("Создание пользователя: {email}")
    public Response createUser(String email, String password, String name) {
        UserRequest request = new UserRequest(email, password, name);
        String body = gson.toJson(request);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/auth/register");
    }

    @Step("Логин пользователя: {email}")
    public Response loginUser(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        String body = gson.toJson(request);
        return RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("/auth/login");
    }

    @Step("Удаление пользователя")
    public void deleteUser(String accessToken) {
        if (accessToken != null && !accessToken.isEmpty()) {
            RestAssured.given()
                    .header("Authorization", accessToken)
                    .delete("/auth/user");
        }
    }
}
