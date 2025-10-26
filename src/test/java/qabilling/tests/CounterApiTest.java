package qabilling.tests;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import qabilling.core.ApiConfig;
import qabilling.core.BaseApiTest;
import qabilling.testdata.TestDataGenerator;

import static io.restassured.RestAssured.given;

/**
 * API тесты для Counter (счетчики)
 * Использует единый паттерн TestDataGenerator + DTO
 */
class CounterApiTest extends BaseApiTest {
    
    @Test
    @DisplayName("Get counter by valid ID")
    void shouldGetCounterById() {
        int counterId = TestDataGenerator.Counter.validId();
        
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/counter/" + counterId)
        .then()
            .statusCode(200);
    }
    
    @Test
    @DisplayName("Get all active counters")
    void shouldGetAllActiveCounters() {
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/counter/active")
        .then()
            .statusCode(200);
    }
    
    @Test
    @DisplayName("Cannot get counter without auth")
    void shouldNotGetCounterWithoutAuth() {
        given()
        .when()
            .get(ApiConfig.BASE_URL + "/api/counter/1")
        .then()
            .statusCode(401);
    }
    
    @Test
    @DisplayName("Cannot get counter with invalid ID")
    void shouldNotGetCounterWithInvalidId() {
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/counter/99999")
        .then()
            .statusCode(404);
    }
}
