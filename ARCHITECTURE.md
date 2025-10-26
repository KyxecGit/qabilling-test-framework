# 🏗️ АРХИТЕКТУРА QABilling Test Framework
## Глубокое погружение для разработчиков и Senior QA

> **Предназначена для:** Тех, кто хочет понять "как это всё работает под капотом"

---

## 📚 **ОГЛАВЛЕНИЕ**

1. [Философия & Выбор технологий](#1-философия--выбор-технологий)
2. [Архитектурная схема](#2-архитектурная-схема)
3. [Деконструкция ядра](#3-деконструкция-ядра)
4. [Работа с данными](#4-работа-с-данными)
5. [Интеграция с CI/CD](#5-интеграция-с-cicd)
6. [Contribution Guide](#6-contribution-guide)

---

## 1. **ФИЛОСОФИЯ & ВЫБОР ТЕХНОЛОГИЙ**

### 🎯 Почему JUnit 5?

| Характеристика | JUnit 4 | JUnit 5 | Наше решение |
|----------------|---------|---------|-------------|
| Модульность | ❌ монолит | ✅ платформа | ✅ используем |
| Параметризация | ⚠️ ограничена | ✅ @ParameterizedTest | ✅ используем |
| @Nested для группировки | ❌ нет | ✅ есть | ✅ используем |
| Custom Extensions | ❌ нет | ✅ @ExtendWith | ✅ планируем |
| Lifecycle callbacks | ⚠️ @Before/@After | ✅ @BeforeEach/@AfterEach | ✅ используем |

**Вывод:** JUnit 5 даёт нам гибкость, модульность и современный API, что идеально для онбординга Junior-специалистов.

### 💡 Ключевые принципы

1. **Агрессивный минимализм:** Убираем boilerplate, используем встроенные фичи JUnit 5
2. **Очевидность важнее "крутости":** Избегаем сложной рефлексии и магии
3. **SRP (Single Responsibility Principle):** Каждый класс отвечает за одно
4. **Самодокументируемый код:** Имена методов читаются как предложения

### 🏢 Слои архитектуры

```
┌─────────────────────────────────────────────┐
│  📝 TEST LAYER (ProfileApiTest и др.)       │
│  - @Test методы                              │
│  - Читаются как предложения (BDD)           │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│  🎁 ASSERTION LAYER (ApiAssertions)         │
│  - Готовые проверки для API                  │
│  - statusCode, body, structure               │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│  🌐 HTTP LAYER (Rest Assured)               │
│  - given().when().then() DSL                 │
│  - Работа с HTTP запросами/ответами         │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│  🔧 CORE LAYER                              │
│  - BaseApiTest (базовый класс)              │
│  - ApiConfig (конфигурация)                 │
│  - AuthUtils (авторизация)                  │
│  - DriverFactory (управление ресурсами)     │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│  📦 DATA LAYER                              │
│  - DTO классы (ProfileDto и др.)            │
│  - TestDataGenerator (фабрика данных)       │
│  - Логирование (SLF4J + Logback)            │
└──────────────────────────────────────────────┘
```

---

## 2. **АРХИТЕКТУРНАЯ СХЕМА**

### 🔄 Поток выполнения теста

```
┌─────────────────────────────────────────────┐
│  1️⃣ SETUP: Инициализация                    │
│     - JUnit вызывает @BeforeEach (если есть)|
│     - BaseApiTest.getAuthHeader() получает  │
│       токен из ApiConfig                    │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│  2️⃣ CREATE DATA: Создание тестовых данных  │
│     - TestDataGenerator.Profile.valid()     │
│     - ProfileDto с правильными значениями   │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│  3️⃣ API CALL: Вызов API                     │
│     - given() - подготовка запроса          │
│     - .post() - отправка                    │
│     - .extract() - получение ответа         │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│  4️⃣ VERIFY: Проверка результата            │
│     - .statusCode(200)                      │
│     - .body(...) - проверка структуры       │
│     - ApiAssertions.* методы                │
└────────────────┬────────────────────────────┘
                 │
┌────────────────▼────────────────────────────┐
│  5️⃣ TEARDOWN: Очистка                      │
│     - JUnit вызывает @AfterEach (если есть) │
│     - Логирование результатов               │
│     - Закрытие ресурсов                     │
└─────────────────────────────────────────────┘
```

---

## 3. **ДЕКОНСТРУКЦИЯ ЯДРА**

### 📚 **BaseApiTest.java** - Ядро тестирования

```java
public abstract class BaseApiTest {
    
    /**
     * 📝 Логгер для всех тестов
     * Каждый тест может писать логи:
     * log.info("Начало теста");
     * log.debug("Значение переменной: {}", value);
     */
    protected static final Logger log = LoggerFactory.getLogger(BaseApiTest.class);
    
    /**
     * 🎫 Получить Bearer токен для авторизации
     * 
     * Что это делает:
     * 1. Проверяет есть ли токен в кэше (ApiConfig.cachedToken)
     * 2. Если нет - запрашивает новый через AuthUtils
     * 3. Форматирует в "Bearer <token>"
     * 4. Возвращает готовый заголовок
     * 
     * Почему это в базовом классе?
     * - Все тесты наследуют BaseApiTest
     * - Все нужны в авторизации
     * - Код не дублируется (DRY)
     */
    protected String getAuthHeader() {
        return "Bearer " + ApiConfig.getToken();
    }
}
```

**Ключевой момент:** BaseApiTest предоставляет:
- ✅ Логирование через `log` поле
- ✅ Авторизацию через `getAuthHeader()`
- ✅ Rest Assured static imports через наследование

---

### 🔧 **ApiConfig.java** - Конфигурация

```java
public class ApiConfig {
    
    // 📌 Статические значения (не меняются)
    public static final String BASE_URL = "http://195.38.164.168:7173";
    public static final String USERNAME = "superuser";
    public static final String PASSWORD = "Admin123!@#";
    
    // 🔄 Динамические значения (могут меняться)
    private static String cachedToken = null;
    
    /**
     * 🎫 Получить JWT токен (с кэшированием)
     * 
     * Пример работы:
     * 1️⃣ Первый вызов getToken() → получаем новый токен
     * 2️⃣ Сохраняем его в cachedToken
     * 3️⃣ Второй вызов getToken() → возвращаем cachedToken (без запроса)
     * 4️⃣ Все 49 тестов используют один и тот же токен
     * 
     * Зачем кэширование?
     * - Получение токена = сетевой запрос = 500мс
     * - 49 тестов × 500мс = 24.5 секунды потеряны впустую
     * - С кэшем: первый тест 500мс + 48 тестов 0мс = выигрыш в 24 сек
     */
    public static String getToken() {
        if (cachedToken == null) {
            log.info("🔑 Получение нового токена авторизации...");
            cachedToken = AuthUtils.getFreshToken();
        }
        return cachedToken;
    }
    
    /**
     * 🗑️ Сбросить кэш токена
     * 
     * Когда использовать:
     * - Если сервер вернул "token expired"
     * - Если нужно протестировать обновление токена
     * - Если меняешь пользователя в середине тестов
     */
    public static void resetToken() {
        log.warn("🔄 Кэш токена сброшен");
        cachedToken = null;
    }
}
```

**Ключевой момент:** ApiConfig - это "единая источник истины" для конфигурации.

---

### 🔐 **AuthUtils.java** - Утилиты авторизации

```java
public class AuthUtils {
    
    /**
     * 🔓 Получить JWT токен через авторизацию
     * 
     * REST запрос:
     * POST /api/auth/sign_in
     * {
     *   "username": "superuser",
     *   "password": "Admin123!@#"
     * }
     * 
     * Ответ:
     * {
     *   "content": {
     *     "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
     *   }
     * }
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
            throw new RuntimeException(
                "❌ Не удалось извлечь токен из ответа авторизации. " +
                "Проверь username/password в ApiConfig"
            );
        }
        
        log.debug("✅ Токен получен успешно (первые 20 символов: {}...)", 
                  token.substring(0, Math.min(20, token.length())));
        
        return token;
    }
    
    /**
     * 🔍 Извлечь токен из ответа авторизации
     * 
     * Сложность: API может вернуть токен в разных местах:
     * - content.token (правильно по спеке)
     * - token (если переделали ответ)
     * - access_token (стандартное название в OAuth)
     * - Заголовок Authorization: Bearer <token>
     * 
     * Решение: Пытаемся все варианты по очереди
     */
    private static String extractTokenFromResponse(Response response) {
        String[] tokenFields = {
            "content.token",    // Ожидаемое место
            "token",            // Альтернатива 1
            "access_token",     // Альтернатива 2
            "authToken"         // Альтернатива 3
        };
        
        for (String field : tokenFields) {
            try {
                String token = response.jsonPath().getString(field);
                if (token != null && !token.trim().isEmpty()) {
                    log.debug("📍 Токен найден в поле: {}", field);
                    return token;
                }
            } catch (Exception e) {
                log.trace("Поле {} не найдено (нормально)", field);
            }
        }
        
        // Последняя попытка - проверим заголовки
        String authHeader = response.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            log.debug("📍 Токен найден в заголовке Authorization");
            return authHeader.substring(7);
        }
        
        log.error("❌ Токен не найден ни в одном месте. Response:\n{}", 
                  response.prettyPrint());
        
        return null;
    }
}
```

**Ключевой момент:** AuthUtils обрабатывает различные варианты ответов API (tolerant reader pattern).

---

### 🎁 **ApiAssertions.java** - Готовые проверки

```java
public class ApiAssertions {
    
    /**
     * 🎯 Статические методы для HTTP status codes
     * 
     * Вместо:
     *   .statusCode(200)
     * 
     * Пишем:
     *   .statusCode(ApiAssertions.OK())
     * 
     * Преимущества:
     * - IDE автодополнение
     * - Самодокументирование (видно что 200 = OK)
     * - Легче менять (если статус коды вдруг поменяются)
     */
    
    public static int OK() { return 200; }
    public static int CREATED() { return 201; }
    public static int BAD_REQUEST() { return 400; }
    public static int UNAUTHORIZED() { return 401; }
    public static int FORBIDDEN() { return 403; }
    public static int NOT_FOUND() { return 404; }
    public static int SERVER_ERROR() { return 500; }
    
    /**
     * 📋 Matchers для структуры ответа
     * 
     * Вместо:
     *   .body("code", equalTo("OK"))
     *   .body("content", notNullValue())
     * 
     * Пишем:
     *   .body(ApiAssertions.isOkResponse())
     *   .body("content", ApiAssertions.hasContent())
     */
    
    public static Matcher<?> isOkResponse() {
        return hasEntry("code", "OK");
    }
    
    public static Matcher<?> hasContent() {
        return notNullValue();
    }
    
    // ... остальные методы ...
}
```

**Ключевой момент:** ApiAssertions централизует стандартные проверки и делает код более читаемым.

---

## 4. **РАБОТА С ДАННЫМИ**

### 📦 **TestDataGenerator.java** - Фабрика данных

```java
public class TestDataGenerator {
    
    /**
     * 👤 Генератор данных для профилей
     */
    public static class Profile {
        
        /**
         * ✅ Создать валидный профиль
         * 
         * Пример:
         * ProfileDto profile = TestDataGenerator.Profile.valid();
         * // Результат:
         * // - msisdn: 99680123456 (правильный формат)
         * // - userId: 1
         * // - pricePlanId: 3
         */
        public static ProfileDto valid() {
            return ProfileDto.builder()
                .msisdn(TestDataGenerator.Msisdn.valid())
                .userId(1)
                .pricePlanId(3)
                .build();
        }
        
        /**
         * ❌ Создать невалидный профиль с ошибкой MSISDN
         * 
         * Пример:
         * ProfileDto badProfile = TestDataGenerator.Profile.withInvalidMsisdn(
         *     TestDataGenerator.MsisdnInvalidType.TOO_SHORT
         * );
         * // Результат:
         * // - msisdn: "123" (слишком короткий)
         * // - userId: 1
         * // - pricePlanId: 3
         * 
         * Зачем?
         * - Проверяем что API правильно валидирует входные данные
         * - API должна вернуть 400 Bad Request для таких данных
         */
        public static ProfileDto withInvalidMsisdn(MsisdnInvalidType type) {
            return ProfileDto.builder()
                .msisdn(Msisdn.invalid(type))
                .userId(1)
                .pricePlanId(3)
                .build();
        }
    }
    
    /**
     * 📱 Генератор валидных MSISDN
     * 
     * Правильный формат MSISDN:
     * - Начинается с 99680 (префикс оператора)
     * - Далее 7 цифр
     * - Всего: 12 цифр
     * - Примеры: 99680123456, 99680999999
     */
    public static class Msisdn {
        
        public static String valid() {
            // Генерируем случайные 7 цифр после префикса
            int randomPart = ThreadLocalRandom.current().nextInt(1000000, 9999999);
            return "99680" + randomPart;
        }
        
        public static String invalid(MsisdnInvalidType type) {
            return switch(type) {
                case TOO_SHORT -> "123";
                case WRONG_PREFIX -> "99681234567";
                case CONTAINS_LETTERS -> "996800123abc";
                case CONTAINS_SPACES -> "99680 0123456";
                case TOO_LONG -> "9968000000000";
            };
        }
    }
    
    public enum MsisdnInvalidType {
        TOO_SHORT, WRONG_PREFIX, CONTAINS_LETTERS, CONTAINS_SPACES, TOO_LONG
    }
}
```

**Ключевой момент:** TestDataGenerator - это "фабрика" которая создаёт валидные и невалидные данные для тестов.

---

### 📝 **DTO классы** - Модели данных

```java
@DisplayName("👤 Data Transfer Object для профилей абонентов")
public class ProfileDto {
    
    private String msisdn;
    private Integer userId;
    private Integer pricePlanId;
    
    /**
     * 🏗️ Builder паттерн для создания объектов
     * 
     * Вместо конструктора с 10 параметрами:
     *   new ProfileDto("99680123456", 1, 3, null, null, null, null, null, null, null)
     * 
     * Используем Builder:
     *   ProfileDto.builder()
     *     .msisdn("99680123456")
     *     .userId(1)
     *     .pricePlanId(3)
     *     .build()
     * 
     * Преимущества:
     * - Явно видны все поля
     * - Не нужно помнить порядок параметров
     * - IDE автодополнение работает
     * - Легко пропустить опциональные поля
     */
    public static ProfileDtoBuilder builder() {
        return new ProfileDtoBuilder();
    }
    
    /**
     * 📤 Конвертировать DTO в JSON для отправки в API
     * 
     * Пример:
     * ProfileDto profile = TestDataGenerator.Profile.valid();
     * String json = profile.toJson();
     * // Результат: {"msisdn":"99680123456","userId":1,"pricePlanId":3}
     * 
     * Это используется в REST Assured:
     * given()
     *   .body(profile.toJson())  // ← Здесь
     * .when()
     *   .post("/api/admin/profile/create")
     */
    public String toJson() {
        return String.format(
            "{\"msisdn\":\"%s\",\"userId\":%d,\"pricePlanId\":%d}",
            msisdn, userId, pricePlanId
        );
    }
    
    // ... getters, setters, equals, hashCode, toString ...
}
```

**Ключевой момент:** DTO используют Builder паттерн для удобного и ясного создания объектов.

---

## 5. **ИНТЕГРАЦИЯ С CI/CD**

### 🔄 Maven Surefire Plugin (pom.xml)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.2</version>
    <configuration>
        <!-- Включить логирование -->
        <reportFormat>plain</reportFormat>
        
        <!-- Параллельный запуск (опционально) -->
        <parallel>methods</parallel>
        <threadCount>4</threadCount>
        
        <!-- Не падать при ошибках (для CI) -->
        <testFailureIgnore>false</testFailureIgnore>
    </configuration>
</plugin>
```

### 🚀 CI/CD команды

```bash
# На локальной машине (для разработчика)
mvn clean verify

# На Jenkins/Gitlab CI (для автоматизации)
mvn clean verify --batch-mode --no-transfer-progress

# С Allure отчётами
mvn clean verify
allure serve target/allure-results
```

---

## 6. **CONTRIBUTION GUIDE**

### 📝 **Именование веток**
```
feature/TICKET-123-add-new-test
bugfix/TICKET-456-fix-flaky-test
refactor/improve-assertions
```

### ✅ **JUnit Best Practices**

1. **@DisplayName для всех:**
   ```java
   // ❌ Плохо
   @Test
   void test1() { }
   
   // ✅ Хорошо
   @Test
   @DisplayName("Должен создать профиль с валидными данными")
   void shouldCreateProfileWithValidData() { }
   ```

2. **@Nested для группировки:**
   ```java
   @Nested
   @DisplayName("✅ Позитивные тесты")
   class PositiveCases {
       @Test
       void shouldCreateProfile() { }
   }
   
   @Nested
   @DisplayName("❌ Негативные тесты")
   class NegativeCases {
       @Test
       void shouldFailWithInvalidData() { }
   }
   ```

3. **Самодокументируемые имена:**
   ```java
   // ✅ Читается как предложение
   void shouldReturnUnauthorizedWhenNoAuthHeaderProvided() { }
   
   // ❌ Не понятно
   void test_401() { }
   ```

4. **Мягкие проверки (assertAll):**
   ```java
   // ✅ Проверяет ВСЕ условия, потом падает
   Assertions.assertAll(
       () -> assertEquals(200, response.statusCode()),
       () -> assertEquals("OK", response.code()),
       () -> assertNotNull(response.content().id())
   );
   
   // ❌ Падает на первой ошибке
   assertEquals(200, response.statusCode());
   assertEquals("OK", response.code());
   ```

5. **Никогда try-catch в тестах:**
   ```java
   // ❌ Плохо (тест пройдёт даже если будет ошибка)
   @Test
   void badTest() {
       try {
           // код
       } catch (Exception e) {
           e.printStackTrace();
       }
   }
   
   // ✅ Хорошо (тест падает если что-то не так)
   @Test
   void goodTest() {
       // код (ошибка сразу видна)
   }
   ```

---

## 🎯 **КЛЮЧЕВЫЕ ВЫВОДЫ**

| Концепция | Реализация | Преимущество |
|-----------|-----------|-------------|
| **Авторизация** | BaseApiTest + ApiConfig + AuthUtils | Нет дублирования, одна источник истины |
| **Данные** | TestDataGenerator + DTO | Валидные и невалидные данные готовы |
| **Проверки** | ApiAssertions | Стандартные проверки, меньше кода |
| **Логирование** | SLF4J + Logback | Видим что происходит в тестах |
| **Параметризация** | @ParameterizedTest | Вместо 5 методов - 1 параметризованный |
| **Группировка** | @Nested + @Tag | Организованные и понятные тесты |

---

**Эта архитектура делает фреймворк идеальным для обучения Junior-специалистов!** 🚀
