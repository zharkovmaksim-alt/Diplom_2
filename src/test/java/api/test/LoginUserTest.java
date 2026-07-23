package api.test;

import api.step.UserClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginUserTest {
    private UserClient userClient;
    private String accessToken;
    private String email;
    private String password;
    private String name;

    @Before
    public void setUp() {
        userClient = new UserClient();
        long timestamp = System.currentTimeMillis();
        email = "login_" + timestamp + "@yandex.ru";
        password = "password123";
        name = "LoginUser";

        // Создаем пользователя для тестов логина
        userClient.createUser(email, password, name);
    }

    @After
    public void tearDown() {
        if (accessToken != null && !accessToken.isEmpty()) {
            userClient.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Логин существующего пользователя")
    @Description("Проверка, что можно войти с правильными данными")
    public void loginExistingUserShouldReturnSuccess() {
        Response response = userClient.loginUser(email, password);
        accessToken = response.jsonPath().getString("accessToken");

        assertEquals(HttpStatus.SC_OK, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("success"));
        assertEquals(email, response.jsonPath().getString("user.email"));
        assertEquals(name, response.jsonPath().getString("user.name"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Проверка, что нельзя войти с неверным паролем")
    public void loginWithInvalidPasswordShouldReturnError() {
        Response response = userClient.loginUser(email, "wrong_password");

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("email or password are incorrect",
                response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Логин с неверным логином")
    @Description("Проверка, что нельзя войти с неверным логином")
    public void loginWithInvalidEmailShouldReturnError() {
        String invalidEmail = "nonexistent_" + System.currentTimeMillis() + "@yandex.ru";
        Response response = userClient.loginUser(invalidEmail, password);

        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("email or password are incorrect",
                response.jsonPath().getString("message"));
    }
}