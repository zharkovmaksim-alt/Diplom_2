package api;

import io.restassured.RestAssured;

public class BaseClient {
    protected static final String BASE_URL = "https://stellarburgers.education-services.ru/api";

    static {
        RestAssured.baseURI = BASE_URL;
    }
}
