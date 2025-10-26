package qabilling.tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import qabilling.core.ApiConfig;
import qabilling.core.BaseApiTest;
import qabilling.core.dto.ProfileDto;
import qabilling.testdata.TestDataGenerator;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.greaterThan;

/**
 * 👤 Тесты для профилей абонентов
 * 
 * Что тестируем:
 * - Создание профиля ✅
 * - Получение профиля ✅  
 * - Обновление профиля ✅
 * - Удаление профиля ✅
 * - Проверка авторизации 🔐
 * 2. Test Data Generator - централизованное создание тестовых данных
 * 3. Builder паттерн - гибкое создание объектов
 * 4. Понятные имена методов и комментарии
 * 5. Разделение позитивных и негативных тестов
 * 
 * Преимущества современного подхода:
 * - Нет hardcoded JSON строк
 * - Автодополнение в IDE
 * - Проверка типов на этапе компиляции
 * - Легче поддерживать и изменять
 * - Менее подвержен ошибкам
 */
@DisplayName("Profile API Tests - Тесты API для работы с профилями абонентов")
public class ProfileApiTest extends BaseApiTest {

    // =============================================
    // ПОЗИТИВНЫЕ ТЕСТЫ (Happy Path)
    // =============================================

    @Test
    @DisplayName("Создание профиля с валидными данными")
    void shouldCreateProfileWithValidData() {
        // Создаем валидный DTO с помощью нашего генератора
        ProfileDto profile = TestDataGenerator.Profile.valid();
        
        System.out.println("Создаем профиль: " + profile.toString());
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(profile.toJson()) // Используем метод DTO для получения JSON
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(200) // Согласно Swagger должен возвращать строго 200
            .body("content.msisdn", equalTo(profile.getMsisdn()))
            .body("content.id", notNullValue())
            .body("content.user.id", equalTo(profile.getUserId()))
            .body("content.pricePlan.id", equalTo(profile.getPricePlanId()));
    }

    @Test
    @DisplayName("Создание профиля с конкретными параметрами")
    void shouldCreateProfileWithSpecificParams() {
        // Используем генератор для уникального MSISDN
        ProfileDto profile = ProfileDto.builder()
            .msisdn(TestDataGenerator.Msisdn.valid()) // Генерируем уникальный MSISDN
            .userId(1)             // Используем существующего пользователя
            .pricePlanId(3)        // Используем существующий тарифный план
            .build();
        
        System.out.println("Создаем профиль с конкретными параметрами: " + profile);
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(profile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(200); // Согласно Swagger должен возвращать строго 200
    }

    @Test
    @DisplayName("Получение профиля по ID")
    void shouldGetProfileById() {
        int existingProfileId = 1; // Существующий профиль
        
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/admin/profile/" + existingProfileId)
        .then()
            .statusCode(200)
            .body("code", equalTo("OK"))
            .body("content.id", equalTo(existingProfileId)) // API возвращает обернутый ответ
            .body("content.msisdn", notNullValue())
            .body("content.user", notNullValue())
            .body("content.pricePlan", notNullValue());
    }

    @Test
    @DisplayName("Получение списка всех профилей")
    void shouldGetAllProfiles() {
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/admin/profile/all")
        .then()
            .statusCode(200)
            .body("code", equalTo("OK"))
            .body("content", isA(java.util.List.class))
            .body("content.size()", greaterThan(0)); // API возвращает обернутый ответ
    }

    // =============================================
    // НЕГАТИВНЫЕ ТЕСТЫ (Error Cases)
    // =============================================

    @Test
    @DisplayName("Ошибка при создании профиля с невалидным MSISDN - слишком короткий")
    void shouldFailToCreateProfileWithTooShortMsisdn() {
        // Используем генератор для создания невалидного MSISDN
        ProfileDto invalidProfile = TestDataGenerator.Profile.withInvalidMsisdn(
            TestDataGenerator.MsisdnInvalidType.TOO_SHORT
        );
        
        System.out.println("Тестируем невалидный профиль: " + invalidProfile);
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(invalidProfile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(400); // Bad Request - убираю проверку структуры ответа
    }

    @Test
    @DisplayName("Ошибка при создании профиля с неправильным префиксом MSISDN")
    void shouldFailToCreateProfileWithWrongPrefix() {
        ProfileDto invalidProfile = TestDataGenerator.Profile.withInvalidMsisdn(
            TestDataGenerator.MsisdnInvalidType.WRONG_PREFIX
        );
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(invalidProfile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Ошибка при создании профиля с MSISDN содержащим буквы")
    void shouldFailToCreateProfileWithLettersInMsisdn() {
        ProfileDto invalidProfile = TestDataGenerator.Profile.withInvalidMsisdn(
            TestDataGenerator.MsisdnInvalidType.CONTAINS_LETTERS
        );
        
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(invalidProfile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("Ошибка при получении профиля с несуществующим ID")
    void shouldFailToGetProfileWithNonExistentId() {
        int nonExistentId = TestDataGenerator.Id.nonExistent();
        
        System.out.println("Тестируем несуществующий ID: " + nonExistentId);
        
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/admin/profile/" + nonExistentId)
        .then()
            .statusCode(404); // Not Found
    }

    @Test
    @DisplayName("Ошибка при получении профиля с отрицательным ID")
    void shouldFailToGetProfileWithNegativeId() {
        int negativeId = TestDataGenerator.Id.negative();
        
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/admin/profile/" + negativeId)
        .then()
            .statusCode(404); // Согласно HTTP стандартам - Not Found для несуществующих ID
    }

    // =============================================
    // ТЕСТЫ АВТОРИЗАЦИИ
    // =============================================

    @Test
    @DisplayName("Ошибка при создании профиля без токена авторизации")
    void shouldFailToCreateProfileWithoutAuth() {
        ProfileDto profile = TestDataGenerator.Profile.valid();
        
        given()
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(profile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(401); // Согласно HTTP - 401 Unauthorized без токена
    }

    @Test
    @DisplayName("Ошибка при получении профиля без токена авторизации")
    void shouldFailToGetProfileWithoutAuth() {
        given()
        .when()
            .get(ApiConfig.BASE_URL + "/api/admin/profile/1")
        .then()
            .statusCode(401); // Согласно HTTP - 401 Unauthorized без токена
    }

    // =============================================
    // НЕДОСТАЮЩИЕ ТЕСТЫ ИЗ SWAGGER
    // =============================================

    @Test
    @DisplayName("Обновление профиля с валидными данными")
    void shouldUpdateProfile() {
        int existingProfileId = 1;
        ProfileDto updateData = ProfileDto.builder()
            .msisdn("996800987654") // Правильный формат MSISDN с префиксом 996
            .userId(1)
            .pricePlanId(3)
            .build();

        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(updateData.toJson())
        .when()
            .put(ApiConfig.BASE_URL + "/api/admin/profile/update/" + existingProfileId)
        .then()
            .statusCode(200); // Согласно Swagger должен возвращать строго 200
    }

    @Test
    @DisplayName("Получение профиля по MSISDN")
    void shouldGetProfileByMsisdn() {
        String testMsisdn = "996800987654"; // Используем реальный MSISDN из API (профиль с ID 1)

        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/admin/profile/getByMsisdn/" + testMsisdn)
        .then()
            .statusCode(200)
            .body("code", equalTo("OK"))
            .body("content.msisdn", equalTo(testMsisdn));
    }

    @Test
    @DisplayName("Удаление профиля по ID")
    void shouldDeleteProfile() {
        // Сначала создаем профиль для удаления
        ProfileDto profile = TestDataGenerator.Profile.valid();
        
        int profileId = given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(profile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(200) // Согласно Swagger должен возвращать строго 200
            .extract().path("content.id");

        // Удаляем созданный профиль
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .delete(ApiConfig.BASE_URL + "/api/admin/profile/delete/" + profileId)
        .then()
            .statusCode(200); // Согласно Swagger должен возвращать строго 200

        // Проверяем, что профиль больше недоступен
        given()
            .header("Authorization", getAuthHeader())
        .when()
            .get(ApiConfig.BASE_URL + "/api/admin/profile/" + profileId)
        .then()
            .statusCode(404); // Удаленный профиль должен возвращать 404
    }
}