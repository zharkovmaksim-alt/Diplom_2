package api;

import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    private UserClient userClient = new UserClient();
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            // TODO: удаление пользователя
        }
    }

    @Test
    public void createUniqueUserShouldReturnSuccess() {
        String email = "testuser_" + System.currentTimeMillis() + "@yandex.ru";
        String password = "password123";
        String name = "TestUser";

        Response response = userClient.createUser(email, password, name);
        accessToken = response.jsonPath().getString("accessToken");

        assertEquals(200, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
        assertEquals(email, response.jsonPath().getString("user.email"));
        assertEquals(name, response.jsonPath().getString("user.name"));
    }

    @Test
    public void createUserWithExistingEmailShouldReturnError() {
        String email = "existing_" + System.currentTimeMillis() + "@yandex.ru";
        String password = "password123";

        userClient.createUser(email, password, "User");
        Response response = userClient.createUser(email, password, "User");

        assertEquals(403, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("User already exists", response.jsonPath().getString("message"));
    }

    @Test
    public void createUserWithoutEmailShouldReturnError() {
        String body = "{\"password\":\"password123\",\"name\":\"User\"}";
        Response response = io.restassured.RestAssured.given()
                .header("Content-Type", "application/json")
                .body(body)
                .post("https://stellarburgers.education-services.ru/api/auth/register");

        assertEquals(403, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("Email, password and name are required fields",
                response.jsonPath().getString("message"));
    }

    @Test
    public void loginExistingUserShouldReturnSuccess() {
        String email = "login_" + System.currentTimeMillis() + "@yandex.ru";
        String password = "password123";
        String name = "LoginUser";

        userClient.createUser(email, password, name);
        Response response = userClient.loginUser(email, password);
        accessToken = response.jsonPath().getString("accessToken");

        assertEquals(200, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
        assertEquals(email, response.jsonPath().getString("user.email"));
    }

    @Test
    public void loginWithInvalidCredentialsShouldReturnError() {
        String email = "nonexistent_" + System.currentTimeMillis() + "@yandex.ru";
        Response response = userClient.loginUser(email, "wrong");

        assertEquals(401, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("email or password are incorrect",
                response.jsonPath().getString("message"));
    }
}
