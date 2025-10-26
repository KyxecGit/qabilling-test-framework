````markdown
# 📝 CONTRIBUTION GUIDE
## Как писать новые тесты - Гайд для новичков

> **Для кого:** Тех, кто хочет добавить новый тест в фреймворк

---

## 📋 **ОГЛАВЛЕНИЕ**

1. [Структура теста](#1-структура-теста)
2. [Code Style](#2-code-style)
3. [Как писать новый тест](#3-как-писать-новый-тест)
4. [Примеры](#4-примеры)

---

## **1. Структура теста**

Каждый тест должен следовать паттерну **AAA (Arrange-Act-Assert)**:

```java
@Test
@DisplayName("Описание теста на русском (видно в отчёте)")
void shouldDoSomething() {
    // ARRANGE: Подготовка данных
    ProfileDto profile = TestDataGenerator.Profile.valid();
    
    // ACT: Выполнение действия
    Response response = given()
        .header("Authorization", getAuthHeader())
        .contentType(ApiConfig.CONTENT_TYPE)
        .body(profile.toJson())
    .when()
        .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
    .then()
        .extract().response();
    
    // ASSERT: Проверка результата
    assertEquals(200, response.getStatusCode());
    assertNotNull(response.jsonPath().getInt("content.id"));
}
```

---

## **2. Code Style**

### ✅ Правила:

1. **@DisplayName ОБЯЗАТЕЛЕН** - должен описывать что тест проверяет:
   ```java
   // ❌ Плохо
   @Test
   void test1() { }
   
   // ✅ Хорошо
   @Test
   @DisplayName("Создание профиля с валидными данными")
   void shouldCreateProfileWithValidData() { }
   ```

2. **Имена методов** - читаются как BDD сценарий:
   ```java
   shouldCreateProfile()
   shouldReturnNotFoundForNonExistentId()
   shouldFailWithoutAuthentication()
   ```

3. **Комментарии** - объясняют КЕМ и ПОЧЕМУ:
   ```java
   // ✅ Хорошо - объясняет причину
   // Создаём невалидный MSISDN чтобы проверить валидацию API
   
   // ❌ Плохо - уже видно из кода
   // Создаём переменную profile
   ```

4. **Логирование** - помогает при отладке:
   ```java
   log.info("✅ Профиль успешно создан с ID: {}", profileId);
   log.debug("MSISDN для теста: {}", msisdn);
   ```

---

## **3. Как писать новый тест**

### 🎯 Пример: Написать тест получения профиля

**Шаг 1:** Создай новый класс теста
```java
@DisplayName("MyNewApi Tests - Описание")
public class MyNewApiTest extends BaseApiTest {
    
}
```

**Шаг 2:** Используй генератор для тестовых данных
```java
// Валидные данные
ProfileDto profile = TestDataGenerator.Profile.valid();

// Невалидные данные (для negative тестов)
ProfileDto invalid = TestDataGenerator.Profile.withInvalidMsisdn(
    TestDataGenerator.MsisdnInvalidType.TOO_SHORT
);
```

**Шаг 3:** Напиши сам тест
```java
@Test
@DisplayName("Получение профиля по ID")
void shouldGetProfileById() {
    given()
        .header("Authorization", getAuthHeader())
    .when()
        .get(ApiConfig.BASE_URL + "/api/admin/profile/1")
    .then()
        .statusCode(200);
}
```

**Шаг 4:** Запусти тест
```bash
mvn clean verify -Dtest=MyNewApiTest
```

**Шаг 5:** Проверь логи если упал
```bash
cat logs/test.log
```

---

## **4. Примеры**

### ✅ Хороший позитивный тест

```java
@Test
@DisplayName("Создание профиля с валидными данными")
void shouldCreateProfileWithValidData() {
    // Arrange
    ProfileDto profile = TestDataGenerator.Profile.valid();
    log.debug("Создаём профиль: {}", profile.getMsisdn());
    
    // Act & Assert
    given()
        .header("Authorization", getAuthHeader())
        .contentType(ApiConfig.CONTENT_TYPE)
        .body(profile.toJson())
    .when()
        .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
    .then()
        .statusCode(200)
        .body("content.msisdn", equalTo(profile.getMsisdn()));
    
    log.info("✅ Профиль успешно создан");
}
```

### ✅ Хороший негативный тест

```java
@Test
@DisplayName("Ошибка при создании профиля с невалидным MSISDN")
void shouldFailWithInvalidMsisdn() {
    // Arrange
    ProfileDto invalid = TestDataGenerator.Profile.withInvalidMsisdn(
        TestDataGenerator.MsisdnInvalidType.TOO_SHORT
    );
    
    // Act & Assert
    given()
        .header("Authorization", getAuthHeader())
        .contentType(ApiConfig.CONTENT_TYPE)
        .body(invalid.toJson())
    .when()
        .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
    .then()
        .statusCode(400);  // Bad Request
    
    log.debug("❌ API правильно отклонила невалидные данные");
}
```

### ❌ Что не делать

```java
// ❌ ПЛОХО: Hard-coded JSON строки
String json = "{\"msisdn\":\"99680123456\",\"userId\":1}";

// ✅ ХОРОШО: Используй DTO
ProfileDto profile = ProfileDto.builder()
    .msisdn("99680123456")
    .userId(1)
    .build();

// ❌ ПЛОХО: Try-catch в тестах
try {
    // код
} catch (Exception e) {
    e.printStackTrace();
}

// ✅ ХОРОШО: Тест упадёт с ошибкой (нормально!)
// код (если ошибка - падаем)

// ❌ ПЛОХО: Raw status codes
.statusCode(200)
.statusCode(404)

// ✅ ХОРОШО: Используй ApiAssertions (если добавишь)
.statusCode(ApiAssertions.OK())
.statusCode(ApiAssertions.NOT_FOUND())
```

---

## 📊 **Чек-лист перед коммитом**

- [ ] Тест запускается через `mvn clean verify`
- [ ] Есть @DisplayName на всех тестах
- [ ] Имя метода начинается с `should`
- [ ] Используются готовые данные из TestDataGenerator
- [ ] Логирование есть (log.info / log.debug)
- [ ] Нет硬-кодированных значений
- [ ] Нет try-catch блоков
- [ ] Нет неиспользуемых импортов

---

**Happy Testing! 🚀**

````

## 2. **CODE STYLE**

### 🎨 Мы следуем Google Java Style Guide

**Основные правила:**

#### **Отступы:** 4 пробела (не tabs)
```java
public class MyTest {
    @Test
    void myTest() {
        // 4 пробела
        given()
            .header("Authorization", getAuthHeader())  // +4 пробела
        .when()
            .post(ApiConfig.BASE_URL + "/api/test")    // +4 пробела
        .then()
            .statusCode(200);
    }
}
```

#### **Длина строки:** максимум 100 символов
```java
// ❌ Слишком длинная строка (132 символа)
String errorMessage = "Это очень-очень-очень-очень-очень-очень-очень-очень-очень-очень-очень-очень-очень-очень-очень-очень-очень долгое сообщение об ошибке";

// ✅ Правильно (разбиваем на несколько строк)
String errorMessage = 
    "Это очень-очень-очень-очень-очень-очень-очень-очень-очень-очень-очень " +
    "сообщение об ошибке";
```

#### **Пустые строки:**
```java
public class MyTest extends BaseApiTest {
    
    // Пустая строка после объявления класса
    
    @Test
    void firstTest() { }
    
    // Пустая строка между методами
    
    @Test
    void secondTest() { }
}
```

#### **Комментарии с примерами:**
```java
/**
 * 🎯 Получить авторизационный заголовок
 * 
 * Пример:
 * String auth = getAuthHeader();
 * // Результат: "Bearer eyJhbGc..."
 * 
 * @return готовый Bearer токен
 */
protected String getAuthHeader() {
    return "Bearer " + ApiConfig.getToken();
}
```

---

## 3. **JUNIT 5 BEST PRACTICES**

### ✅ **Правило 1: Всегда используй @DisplayName**

```java
// ❌ Плохо - непонятно что тестируется
@Test
void test1() { }

@Test
void test2() { }

// ✅ Хорошо - читается как предложение
@Test
@DisplayName("Должен создать профиль с валидными данными")
void shouldCreateProfileWithValidData() { }

@Test
@DisplayName("Должен вернуть 404 при запросе несуществующего профиля")
void shouldReturnNotFoundForNonExistentProfile() { }
```

**Результат в отчёте:**
```
✅ Должен создать профиль с валидными данными
❌ Должен вернуть 404 при запросе несуществующего профиля
```

---

### ✅ **Правило 2: Группируй тесты логически**

**Вариант 1 (Рекомендуемый):** Используй @Nested классы для большых тест-файлов

```java
@DisplayName("Profile API Tests")
public class ProfileApiTest extends BaseApiTest {
    
    @Nested
    @DisplayName("✅ Позитивные тесты (Happy Path)")
    class PositiveTests {
        
        @Test
        @DisplayName("Создание профиля с валидными данными")
        void shouldCreateProfile() { }
        
        @Test
        @DisplayName("Получение профиля по ID")
        void shouldGetProfile() { }
    }
    
    @Nested
    @DisplayName("❌ Негативные тесты (Error Cases)")
    class NegativeTests {
        
        @Test
        @DisplayName("Ошибка при невалидном MSISDN")
        void shouldFailWithInvalidMsisdn() { }
        
        @Test
        @DisplayName("Ошибка при отсутствии авторизации")
        void shouldFailWithoutAuth() { }
    }
}
```

**Вариант 2 (Текущий стиль в коде):** Группируй с помощью комментариев

```java
@DisplayName("Profile API Tests")
public class ProfileApiTest extends BaseApiTest {
    
    // =============================================
    // ПОЗИТИВНЫЕ ТЕСТЫ (Happy Path)
    // =============================================
    
    @Test
    @DisplayName("Создание профиля с валидными данными")
    void shouldCreateProfile() { }
    
    @Test
    @DisplayName("Получение профиля по ID")
    void shouldGetProfile() { }
    
    // =============================================
    // НЕГАТИВНЫЕ ТЕСТЫ (Error Cases)
    // =============================================
    
    @Test
    @DisplayName("Ошибка при невалидном MSISDN")
    void shouldFailWithInvalidMsisdn() { }
}
```

**Выбери один вариант и используй последовательно по всему проекту.**

---

### ✅ **Правило 3: Самодокументируемые имена методов**

```java
// Имена методов должны ЧЕТКО описывать что они проверяют

// ✅ ХОРОШО - читается как BDD сценарий
void shouldCreateProfileWithValidData() { }
void shouldReturnUnauthorizedWhenNoTokenProvided() { }
void shouldNotAllowCreatingProfileWithDuplicateMsisdn() { }
void shouldUpdateProfileWithoutChangingId() { }

// ❌ ПЛОХО - непонятно
void testProfile() { }
void test1() { }
void profileTest() { }
void check() { }
```

**Правило именования:**
```
should + ЧТО_ДОЛЖНО_ПРОИЗОЙТИ + WHEN + УСЛОВИЕ

shouldCreateProfile() → создание профиля
shouldReturnNotFound() → возврат 404
shouldFail() → падение теста
shouldThrowException() → выброс исключения
```

---

### ✅ **Правило 4: @Tag для категоризации**

```java
@Test
@DisplayName("Создание профиля")
@Tag("smoke")          // Быстрый тест для регулярного запуска
@Tag("api")            // API тест (не UI)
void shouldCreateProfile() { }

@Test
@DisplayName("Обновление профиля")
@Tag("regression")     // Полный набор проверок
@Tag("api")
void shouldUpdateProfile() { }

@Test
@DisplayName("Нагрузочный тест")
@Tag("performance")    // Проверяет скорость
void shouldHandleHighLoad() { }
```

**Запуск тестов по тегам:**
```bash
# Только smoke тесты
mvn clean verify -Dgroups="smoke"

# Smoke и regression тесты
mvn clean verify -Dgroups="smoke,regression"

# Все кроме performance
mvn clean verify -DexcludedGroups="performance"
```

---

### ✅ **Правило 5: Assertive мягкие проверки**

```java
// ❌ ПЛОХО - падает на первой ошибке
@Test
void badAssertion() {
    Response response = getProfileById(1);
    assertEquals(200, response.statusCode());
    assertEquals("OK", response.code());
    assertNotNull(response.content().id());  // Эта проверка может не выполниться
}

// ✅ ХОРОШО - проверяет ВСЁ, потом падает
@Test
void goodAssertion() {
    Response response = getProfileById(1);
    Assertions.assertAll(
        () -> assertEquals(200, response.statusCode()),
        () -> assertEquals("OK", response.code()),
        () -> assertNotNull(response.content().id()),
        () -> assertTrue(response.content().msisdn().matches("^99680\\d{7}$"))
    );
}
```

**Результат:** Если одна проверка упала - мы видим ВСЕ ошибки сразу, а не только первую.

---

### ✅ **Правило 6: НИКОГДА try-catch в тестах**

```java
// ❌ ПЛОХО - тест пройдёт даже если будет ошибка
@Test
void badTest() {
    try {
        int result = 10 / 0;  // ArithmeticException!
        assertEquals(5, result);
    } catch (Exception e) {
        e.printStackTrace();
        // Тест ПРОЙДЁТ потому что мы поймали исключение
    }
}

// ✅ ХОРОШО - тест упадёт с ошибкой (как и должно быть)
@Test
void goodTest() {
    // Не перехватываем исключение - тест упадёт с ошибкой
    int result = 10 / 5;  // = 2
    assertEquals(2, result);
}

// ✅ ХОРОШО - если ошибка ОЖИДАЕТСЯ, используй assertThrows
@Test
void testExpectedException() {
    Assertions.assertThrows(
        ArithmeticException.class,
        () -> { int x = 10 / 0; }
    );
}
```

**Правило:** Тест должен **падать** если что-то не так. Это его работа! 

---

## 4. **КАК ПИСАТЬ ТЕСТЫ**

### 🏗️ Структура теста (AAA Pattern)

```java
@Test
@DisplayName("Создание профиля с валидными данными")
void shouldCreateProfileWithValidData() {
    // Arrange (подготовка данных)
    ProfileDto profile = TestDataGenerator.Profile.valid();
    log.info("📝 Создаём профиль с MSISDN: {}", profile.getMsisdn());
    
    // Act (выполнение действия)
    Response response = given()
        .header("Authorization", getAuthHeader())
        .contentType(ApiConfig.CONTENT_TYPE)
        .body(profile.toJson())
    .when()
        .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
    .then()
        .extract().response();
    
    // Assert (проверка результатов)
    Assertions.assertAll(
        () -> {
            int statusCode = response.getStatusCode();
            assertEquals(ApiAssertions.OK(), statusCode, 
                "Статус код должен быть 200, а не " + statusCode);
        },
        () -> assertNotNull(response.jsonPath().getInt("content.id"), 
                "ID профиля не должен быть null"),
        () -> assertEquals(profile.getMsisdn(), 
               response.jsonPath().getString("content.msisdn"),
               "MSISDN в ответе должен совпадать с отправленным")
    );
    
    log.info("✅ Профиль успешно создан с ID: {}", 
             response.jsonPath().getInt("content.id"));
}
```

### 📊 Использование логирования

```java
@Test
void shouldGetProfileById() {
    log.debug("🔍 Начало теста получения профиля");
    log.debug("   ID профиля: 1");
    
    given()
        .header("Authorization", getAuthHeader())
    .when()
        .get(ApiConfig.BASE_URL + "/api/admin/profile/1")
    .then()
        .statusCode(ApiAssertions.OK())
        .log().ifValidationFails();  // Логируем только если тест упал
    
    log.info("✅ Профиль успешно получен");
}
```

---

## 5. **ПРИМЕРЫ**

### 📌 Полный пример теста

```java
package qabilling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import qabilling.dto.ProfileDto;
import qabilling.testdata.TestDataGenerator;
import qabilling.utils.ApiAssertions;

@DisplayName("Profile API - тесты профилей абонентов")
public class ProfileApiTest extends BaseApiTest {
    
    // =============================================
    // ПОЗИТИВНЫЕ ТЕСТЫ (Happy Path)
    // =============================================
    
    @Test
    @DisplayName("Создание нового профиля с валидными данными")
    void shouldCreateNewProfile() {
        // Arrange
        ProfileDto newProfile = TestDataGenerator.Profile.valid();
        
        // Act & Assert
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(newProfile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(ApiAssertions.OK())
            .body("code", equalTo("OK"))
            .body("content.msisdn", equalTo(newProfile.getMsisdn()));
        
        log.info("✅ Профиль успешно создан");
    }
    
    // =============================================
    // НЕГАТИВНЫЕ ТЕСТЫ (Error Cases)
    // =============================================
    
    @ParameterizedTest(name = "MSISDN: {0}")
    @ValueSource(strings = {
        "123",              // Слишком короткий
        "99681234567",      // Неправильный префикс
        "996800123abc"      // Содержит буквы
    })
    @DisplayName("Ошибка при невалидном MSISDN")
    void shouldFailWithInvalidMsisdn(String invalidMsisdn) {
        // Arrange
        ProfileDto badProfile = ProfileDto.builder()
            .msisdn(invalidMsisdn)
            .userId(1)
            .pricePlanId(3)
            .build();
        
        // Act & Assert
        given()
            .header("Authorization", getAuthHeader())
            .contentType(ApiConfig.CONTENT_TYPE)
            .body(badProfile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(ApiAssertions.BAD_REQUEST());
        
        log.debug("❌ API правильно отклонила невалидный MSISDN: {}", invalidMsisdn);
    }
}
```

---

## 6. **CODE REVIEW CHECKLIST**

### ✅ Перед созданием Pull Request проверь:

- [ ] Ветка создана от `main` с правильным именем (`feature/...`)
- [ ] Код скомпилировался (`mvn clean compile test-compile`)
- [ ] Все тесты проходят (`mvn clean verify`)
- [ ] Нет `TODO`, `FIXME`, `XXX` комментариев
- [ ] Все методы имеют `@DisplayName`
- [ ] Логирование используется (`log.info()`, `log.debug()`)
- [ ] Нет try-catch блоков в тестах
- [ ] Параметры не hardcoded (используются `TestDataGenerator`)
- [ ] Используются `ApiAssertions` вместо raw `statusCode(200)`
- [ ] Javadoc есть на всех public методах ядра
- [ ] Код соответствует Google Java Style
- [ ] Нет неиспользуемых импортов
- [ ] Никакие пароли/токены не закоммичены

### 📝 Шаблон commit message:

```
[TICKET-123] Краткое описание изменения

Детальное описание:
- Что добавили?
- Почему это было необходимо?
- Какие проблемы это решает?

Пример:
[TICKET-456] Add parameterized test for invalid MSISDN

Add parameterized test that verifies API correctly rejects
invalid MSISDN formats (too short, wrong prefix, contains letters).
This ensures API validation layer works correctly before saving data.

Related issue: #456
```

---

## 🎯 **SUMMARY**

| Практика | Статус |
|----------|--------|
| @DisplayName на всё | ✅ ОБЯЗАТЕЛЬНО |
| @Nested группировка | ✅ РЕКОМЕНДУЕТСЯ |
| @Tag категоризация | ✅ РЕКОМЕНДУЕТСЯ |
| Assertive мягкие проверки | ✅ ОБЯЗАТЕЛЬНО |
| Логирование в тестах | ✅ РЕКОМЕНДУЕТСЯ |
| Никакого try-catch | ✅ ОБЯЗАТЕЛЬНО |
| Javadoc в ядре | ✅ ОБЯЗАТЕЛЬНО |
| Code Style 100 символов | ✅ ОБЯЗАТЕЛЬНО |

---

**Happy Contributing! 🚀**
