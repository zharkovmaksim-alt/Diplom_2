package api;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class UserTest {
    private UserClient userClient = new UserClient();
    private String accessToken;

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка, что можно создать нового пользователя с уникальными данными")
    public void createUniqueUserShouldReturnSuccess() {
        String email = "testuser_" + System.currentTimeMillis() + "@yandex.ru";
        String password = "password123";
        String name = "TestUser";

        Response response = userClient.createUser(email, password, name);
        accessToken = response.jsonPath().getString("accessToken");

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
        assertEquals(email, response.jsonPath().getString("user.email"));
        assertEquals(name, response.jsonPath().getString("user.name"));
    }

    @Test
    @DisplayName("Создание пользователя с существующим email")
    @Description("Проверка, что нельзя создать пользователя с уже существующим email")
    public void createUserWithExistingEmailShouldReturnError() {
        String email = "existing_" + System.currentTimeMillis() + "@yandex.ru";
        String password = "password123";

        userClient.createUser(email, password, "User");
        Response response = userClient.createUser(email, password, "User");

        assertEquals(HttpStatus.SC_FORBIDDEN, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("User already exists", response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка, что нельзя создать пользователя без email")
    public void createUserWithoutEmailShouldReturnError() {
        Response response = userClient.createUser("", "password123", "User");

        assertEquals(HttpStatus.SC_FORBIDDEN, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("Email, password and name are required fields",
                response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание пользователя без password")
    @Description("Проверка, что нельзя создать пользователя без password")
    public void createUserWithoutPasswordShouldReturnError() {
        Response response = userClient.createUser("test_" + System.currentTimeMillis() + "@yandex.ru", "", "User");

        assertEquals(HttpStatus.SC_FORBIDDEN, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("Email, password and name are required fields",
                response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание пользователя без name")
    @Description("Проверка, что нельзя создать пользователя без name")
    public void createUserWithoutNameShouldReturnError() {
        Response response = userClient.createUser("test_" + System.currentTimeMillis() + "@yandex.ru", "password123", "");

        assertEquals(HttpStatus.SC_FORBIDDEN, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("Email, password and name are required fields",
                response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Логин существующего пользователя")
    @Description("Проверка, что можно войти с правильными данными")
    public void loginExistingUserShouldReturnSuccess() {
        String email = "login_" + System.currentTimeMillis() + "@yandex.ru";
        String password = "password123";
        String name = "LoginUser";

        userClient.createUser(email, password, name);
        Response response = userClient.loginUser(email, password);
        accessToken = response.jsonPath().getString("accessToken");

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
        assertEquals(email, response.jsonPath().getString("user.email"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка, что нельзя войти с неверным паролем")
    public void loginWithInvalidPasswordShouldReturnError() {
        String email = "login_" + System.currentTimeMillis() + "@yandex.ru";
        String password = "password123";
        userClient.createUser(email, password, "LoginUser");

        Response response = userClient.loginUser(email, "wrong");

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("email or password are incorrect",
                response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Логин с неверным логином")
    @Description("Проверка, что нельзя войти с неверным логином")
    public void loginWithInvalidEmailShouldReturnError() {
        String email = "nonexistent_" + System.currentTimeMillis() + "@yandex.ru";
        Response response = userClient.loginUser(email, "password123");

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("email or password are incorrect",
                response.jsonPath().getString("message"));
    }
}