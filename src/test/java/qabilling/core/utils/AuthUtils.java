package qabilling.core.utils;

import io.restassured.response.Response;
import qabilling.core.ApiConfig;

import static io.restassured.RestAssured.*;

/**
 * 🔐 Утилиты для работы с авторизацией
 */
public class AuthUtils {
    
    /**
     * Получить JWT токен через авторизацию
     * @return JWT токен для API запросов
     */
    public static String getAuthToken() {
        String loginPayload = String.format(
            "{\"username\": \"%s\", \"password\": \"%s\"}", 
            ApiConfig.USERNAME, 
            ApiConfig.PASSWORD
        );
        
        Response response = given()
            .contentType("application/json")
            .body(loginPayload)
        .when()
            .post(ApiConfig.BASE_URL + "/api/auth/sign_in")
        .then()
            .statusCode(200)
            .extract().response();
        
        String token = extractTokenFromResponse(response);
        
        if (token == null || token.trim().isEmpty()) {
            throw new RuntimeException("Не удалось извлечь токен из ответа авторизации");
        }
        
        return token;
    }
    
    /**
     * Извлечь токен из ответа авторизации
     */
    private static String extractTokenFromResponse(Response response) {
        String[] tokenFields = {"content.token", "token", "access_token", "authToken"};
        
        for (String field : tokenFields) {
            String token = response.jsonPath().getString(field);
            if (token != null && !token.trim().isEmpty()) {
                return token;
            }
        }
        
        // Проверим заголовки
        String authHeader = response.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        
        return null;
    }
    
    /**
     * Получить свежий JWT токен
     * @return JWT токен
     */
    public static String getFreshToken() {
        return getAuthToken();
    }
}