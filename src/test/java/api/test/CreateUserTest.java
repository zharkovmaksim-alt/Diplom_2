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

public class CreateUserTest {
    private UserClient userClient;
    private String accessToken;
    private String email;
    private String password;
    private String name;

    @Before
    public void setUp() {
        userClient = new UserClient();
        long timestamp = System.currentTimeMillis();
        email = "test_" + timestamp + "@yandex.ru";
        password = "password123";
        name = "TestUser";
    }

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
        // Создаем первого пользователя
        userClient.createUser(email, password, name);

        // Пытаемся создать второго с тем же email
        Response response = userClient.createUser(email, password, "AnotherUser");

        assertEquals(HttpStatus.SC_FORBIDDEN, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("User already exists", response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка, что нельзя создать пользователя без email")
    public void createUserWithoutEmailShouldReturnError() {
        Response response = userClient.createUser("", password, name);

        assertEquals(HttpStatus.SC_FORBIDDEN, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("Email, password and name are required fields",
                response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание пользователя без password")
    @Description("Проверка, что нельзя создать пользователя без password")
    public void createUserWithoutPasswordShouldReturnError() {
        Response response = userClient.createUser(email, "", name);

        assertEquals(HttpStatus.SC_FORBIDDEN, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("Email, password and name are required fields",
                response.jsonPath().getString("message"));
    }

    @Test
    @DisplayName("Создание пользователя без name")
    @Description("Проверка, что нельзя создать пользователя без name")
    public void createUserWithoutNameShouldReturnError() {
        Response response = userClient.createUser(email, password, "");

        assertEquals(HttpStatus.SC_FORBIDDEN, response.statusCode());
        assertFalse(response.jsonPath().getBoolean("success"));
        assertEquals("Email, password and name are required fields",
                response.jsonPath().getString("message"));
    }
}