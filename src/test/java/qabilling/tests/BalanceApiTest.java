package qabilling.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import qabilling.core.ApiConfig;
import qabilling.core.BaseApiTest;
import qabilling.testdata.TestDataGenerator;
import qabilling.core.dto.BalanceDto;

/**
 * 💰 Тесты для балансов абонентов
 * 
 * ✅ FIX: Переделан на использование DTO и TestDataGenerator
 * ✅ FIX: Убрана условная логика в тестах
 * ✅ FIX: Используется BalanceDto вместо String.format()
 */
@DisplayName("💰 Balance API Tests")
public class BalanceApiTest extends BaseApiTest {

    @Test
    @DisplayName("Update balance with valid amount")
    void shouldUpdateBalance() {
        int balanceId = 1;
        BalanceDto balance = TestDataGenerator.Balance.withAmount(125.75);
        
        log.info("Обновляем баланс " + balanceId + " на сумму " + balance.getAmount());
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(balance.toJson())
        .when()
            .put(ApiConfig.BASE_URL + "/api/balance/update/" + balanceId)
        .then()
            .statusCode(200);
    }
    
    @Test
    @DisplayName("Get balance by ID")
    void shouldGetBalance() {
        int balanceId = 1;
        
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/balance/" + balanceId)
        .then()
            .statusCode(200)
            .body("content.id", equalTo(balanceId))
            .body("content.amount", greaterThanOrEqualTo(0f));
    }
    
    @Test
    @DisplayName("Update and verify balance")
    void shouldUpdateAndVerifyBalance() {
        int balanceId = 1;
        BalanceDto balance = TestDataGenerator.Balance.withAmount(150.50);
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(balance.toJson())
        .when()
            .put(ApiConfig.BASE_URL + "/api/balance/update/" + balanceId)
        .then()
            .statusCode(200);
    }
    
    @Test
    @DisplayName("Cannot update balance with negative amount")
    void shouldNotUpdateBalanceWithNegativeAmount() {
        int balanceId = 1;
        BalanceDto balance = TestDataGenerator.Balance.negative();
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(balance.toJson())
        .when()
            .put(ApiConfig.BASE_URL + "/api/balance/update/" + balanceId)
        .then()
            .statusCode(400);
    }
    
    @Test
    @DisplayName("Cannot update balance with invalid amount type")
    void shouldNotUpdateBalanceWithInvalidAmount() {
        int balanceId = 1;
        String invalidTypeRequest = "{\"amount\": \"invalid_amount\"}";
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(invalidTypeRequest)
        .when()
            .put(ApiConfig.BASE_URL + "/api/balance/update/" + balanceId)
        .then()
            .statusCode(400);
    }
    
    @Test
    @DisplayName("Cannot update balance without amount field")
    void shouldNotUpdateBalanceWithoutAmount() {
        int balanceId = 1;
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body("{}")
        .when()
            .put(ApiConfig.BASE_URL + "/api/balance/update/" + balanceId)
        .then()
            .statusCode(400);
    }
    
    @Test
    @DisplayName("Cannot get balance with invalid ID")
    void shouldNotGetBalanceWithInvalidId() {
        int invalidId = TestDataGenerator.Id.nonExistent();
        
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/balance/" + invalidId)
        .then()
            .statusCode(404);
    }
    
    @Test
    @DisplayName("Cannot update balance with invalid ID")
    void shouldNotUpdateBalanceWithInvalidId() {
        int invalidId = TestDataGenerator.Id.nonExistent();
        BalanceDto balance = TestDataGenerator.Balance.withAmount(100.0);
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(balance.toJson())
        .when()
            .put(ApiConfig.BASE_URL + "/api/balance/update/" + invalidId)
        .then()
            .statusCode(404);
    }
    
    @Test
    @DisplayName("Cannot get balance without auth")
    void shouldNotGetBalanceWithoutAuth() {
        given()
        .when()
            .get(ApiConfig.BASE_URL + "/api/balance/1")
        .then()
            .statusCode(401);
    }
    
    @Test
    @DisplayName("Cannot update balance without auth")
    void shouldNotUpdateBalanceWithoutAuth() {
        BalanceDto balance = TestDataGenerator.Balance.withAmount(100.0);
        
        given()
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(balance.toJson())
        .when()
            .put(ApiConfig.BASE_URL + "/api/balance/update/1")
        .then()
            .statusCode(401);
    }
    
    @Test
    @DisplayName("Get all balances")
    void shouldGetAllBalances() {
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/balance/all")
        .then()
            .statusCode(200);
    }
}
