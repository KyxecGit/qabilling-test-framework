package qabilling.tests;

import org.junit.jupiter.api.Test;
import qabilling.core.ApiConfig;
import qabilling.core.BaseApiTest;

import static io.restassured.RestAssured.given;

/**
 * 🔑 Тесты для авторизации
 * 
 * ⚠️ ВАЖНО ДЛЯ НОВИЧКОВ:
 * 
 * Эти тесты МОГУТ ПАДАТЬ если запускаешь их в отдельности.
 * Это НЕ БАГ в фреймворке, а БАГ в API (подробно смотри API_PROBLEMS.md)
 * 
 * Проблема: API возвращает HTTP 403 вместо 401 для ошибок аутентификации.
 * Это нарушает HTTP стандарты (RFC 7235).
 */
public class AuthApiTest extends BaseApiTest {

    @Test
    void shouldGetValidToken() {
        String token = ApiConfig.getToken();
        assert token != null && !token.trim().isEmpty() : "Токен не должен быть пустым";
        
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/admin/counter/all-active")
        .then()
            .statusCode(200);
    }

    @Test
    void shouldRejectInvalidToken() {
        given()
            .header("Authorization", "Bearer invalid.token.here")
        .when()
            .get(ApiConfig.BASE_URL + "/api/admin/counter/all-active")
        .then()
            .statusCode(401);
    }

    @Test
    void shouldRejestRequestWithoutToken() {
        given()
        .when()
            .get(ApiConfig.BASE_URL + "/api/admin/counter/all-active")
        .then()
            .statusCode(401);
    }
}
