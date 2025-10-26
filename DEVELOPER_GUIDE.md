# 👨‍💻 ПОЛНОЕ РУКОВОДСТВО РАЗРАБОТЧИКА

> **Для:** Разработчиков, которые хотят понять и использовать QABilling Test Framework  
> **Уровень:** Junior/Middle QA Engineer  
> **Версия:** 2.0  
> **Обновлено:** 26 октября 2025

---

## 📑 ОГЛАВЛЕНИЕ

1. [Быстрый старт](#быстрый-старт)
2. [Структура проекта](#структура-проекта)
3. [Основные компоненты](#основные-компоненты)
4. [Как писать тесты](#как-писать-тесты)
5. [Как использовать данные](#как-использовать-данные)
6. [Запуск тестов](#запуск-тестов)
7. [Отладка](#отладка)
8. [FAQ](#faq)

---

## 🚀 БЫСТРЫЙ СТАРТ

### 1. Клонируй репо
```bash
git clone <url>
cd qabilling-test-framework-main
```

### 2. Убедись что установлены зависимости
```bash
# Проверить Java
java -version  # Должна быть Java 21+

# Проверить Maven
mvn -version  # Должна быть 3.9.11+
```

### 3. Запусти тесты
```bash
mvn clean test
```

### 4. Смотри результаты
```
✅ 16 тестов прошли
❌ 16 тестов упали (это API ошибки, НЕ наши ошибки)
```

---

## 📂 СТРУКТУРА ПРОЕКТА

### Визуально:
```
qabilling-test-framework-main/
│
├── 📁 src/test/java/qabilling/
│   │
│   ├── 📁 core/                    ← ЯДРО ФРЕЙМВОРКА
│   │   ├── ApiConfig.java          ← Конфиг + кэш токенов
│   │   ├── BaseApiTest.java        ← Базовый класс для тестов
│   │   ├── 📁 dto/                 ← Модели данных
│   │   │   ├── BaseDto.java        ← Базовый DTO
│   │   │   ├── ProfileDto.java     ← Профиль абонента
│   │   │   ├── BalanceDto.java     ← Баланс счёта
│   │   │   └── CounterDto.java     ← Счётчик
│   │   └── 📁 utils/
│   │       └── AuthUtils.java      ← JWT авторизация
│   │
│   ├── 📁 tests/                   ← ТЕСТОВЫЕ КЛАССЫ
│   │   ├── AuthApiTest.java        ← Тесты авторизации
│   │   ├── ProfileApiTest.java     ← Тесты профилей
│   │   ├── BalanceApiTest.java     ← Тесты баланса
│   │   └── CounterApiTest.java     ← Тесты счётчиков
│   │
│   └── 📁 testdata/                ← ГЕНЕРАТОРЫ ДАННЫХ
│       └── TestDataGenerator.java  ← Фабрика тестовых данных
│
├── 📁 src/test/resources/
│   ├── logback.xml                 ← Конфиг логирования
│   └── config.example.properties   ← Пример конфига (не используется)
│
├── pom.xml                         ← Maven конфиг
├── README.md                       ← Основная инструкция
├── ARCHITECTURE.md                 ← Архитектура системы
├── CONTRIBUTION_GUIDE.md           ← Как контрибьютить
├── API_ISSUES_REPORT.md            ← 🚨 ОТЧЁТ ОБ ОШИБКАХ API
└── DEVELOPER_GUIDE.md              ← 👈 ТЫ ЗДЕСЬ
```

### Что находится где:

| Папка | Что здесь | Зачем |
|-------|----------|-------|
| `core/` | ApiConfig, BaseApiTest, DTO, Utils | Базовые компоненты фреймворка |
| `core/dto/` | ProfileDto, BalanceDto, CounterDto | Модели для работы с API |
| `core/utils/` | AuthUtils | Утилиты для авторизации |
| `tests/` | AuthApiTest, ProfileApiTest, ... | Сами тесты |
| `testdata/` | TestDataGenerator | Создание тестовых данных |

---

## 🔧 ОСНОВНЫЕ КОМПОНЕНТЫ

### 1️⃣ **ApiConfig.java** - Конфигурация
```java
// Что это:
// Единое место для всех конфигурационных параметров

// Как использовать:
String url = ApiConfig.BASE_URL;           // "http://195.38.164.168:7173"
String token = ApiConfig.getToken();       // Получить JWT токен
ApiConfig.resetToken();                    // Сбросить кэш токена
boolean hasToken = ApiConfig.hasToken();   // Проверить есть ли токен
```

**Важно:**
- `cachedToken` использует `ThreadLocal` для thread-safety
- Первый вызов `getToken()` получает новый токен с сервера
- Последующие вызовы возвращают кэшированный токен (экономия времени)

---

### 2️⃣ **BaseApiTest.java** - Базовый класс
```java
// Что это:
// Базовый класс, который ДОЛЖНЫ наследовать все тесты

// Как использовать:
public class MyTest extends BaseApiTest {
    @Test
    void myTest() {
        String auth = getAuthHeader();  // Получить "Bearer <token>"
        log.info("Вывод в логи");       // Логирование
    }
}
```

**Важно:**
- Логирование автоматически показывает имя подкласса (MyTest), а не BaseApiTest
- `getAuthHeader()` возвращает готовый заголовок для всех запросов

---

### 3️⃣ **DTO классы** - Модели данных
```java
// ProfileDto - Профиль абонента
ProfileDto profile = ProfileDto.builder()
    .msisdn("996800123456")  // Номер телефона
    .userId(1)               // ID пользователя
    .pricePlanId(4)          // ID тарифа
    .build();

String json = profile.toJson();  // {"msisdn":"996800123456","userId":1,"pricePlanId":4}
System.out.println(profile);     // ProfileDto -> {"msisdn":"996800123456","userId":1,"pricePlanId":4}

// BalanceDto - Баланс счёта
BalanceDto balance = BalanceDto.builder()
    .msisdn("996800123456")
    .amount(100)
    .operationType("CREDIT")
    .build();

// CounterDto - Счётчик
CounterDto counter = CounterDto.builder()
    .counterId(1)
    .status("ACTIVE")
    .build();
```

**Важно:**
- Все DTO используют Builder pattern для удобства
- Метод `toJson()` конвертирует в JSON для отправки в API
- Методы `equals()` и `hashCode()` позволяют сравнивать объекты

---

### 4️⃣ **TestDataGenerator.java** - Генератор данных
```java
// ВАЛИДНЫЕ ДАННЫЕ:
ProfileDto validProfile = TestDataGenerator.Profile.valid();
// Result: ProfileDto с правильными значениями

// НЕВАЛИДНЫЕ ДАННЫЕ (для негативных тестов):
ProfileDto invalidProfile = TestDataGenerator.Profile
    .withInvalidMsisdn(TestDataGenerator.MsisdnInvalidType.TOO_SHORT);
// Result: ProfileDto с MSISDN="123" (слишком короткий)

// ГЕНЕРАЦИЯ MSISDN:
String validMsisdn = TestDataGenerator.Msisdn.valid();       // "996800123456"
String invalidMsisdn = TestDataGenerator.Msisdn.invalid(...); // "123" или другой

// ГЕНЕРАЦИЯ СЧЁТЧИКОВ:
CounterDto counter = TestDataGenerator.Counter.valid();
CounterDto counterWithId = TestDataGenerator.Counter.withId(5);
```

**Типы невалидных MSISDN:**
- `TOO_SHORT` - "123" (3 цифры вместо 12)
- `TOO_LONG` - "996801234567890" (15 цифр)
- `WRONG_PREFIX` - "99681..." (неправильный префикс)
- `CONTAINS_LETTERS` - "abcdefghijkl"
- `EMPTY` - "" (пустая строка)

---

### 5️⃣ **AuthUtils.java** - Авторизация
```java
// Что это делает:
// Получает JWT токен через POST запрос к /api/auth/sign_in

// Как использовать:
String token = AuthUtils.getFreshToken();  // Всегда получить НОВЫЙ токен

// Внутри ApiConfig используется вот так:
String token = ApiConfig.getToken();  // Первый раз → getFreshToken()
String token = ApiConfig.getToken();  // Второй раз → кэшированный токен
```

---

## ✍️ КАК ПИСАТЬ ТЕСТЫ

### Шаблон теста:
```java
package qabilling.tests;

import qabilling.core.BaseApiTest;
import qabilling.core.ApiConfig;
import qabilling.core.dto.ProfileDto;
import qabilling.testdata.TestDataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 📝 Тесты для профилей абонентов
 * 
 * Структура:
 * 1.準備 (Setup) - подготовить данные и окружение
 * 2. 実行 (Act) - выполнить API запрос
 * 3. 検証 (Assert) - проверить результат
 */
public class MyTest extends BaseApiTest {
    
    @Test
    @DisplayName("Должен создать профиль с валидными данными")
    void shouldCreateProfileWithValidData() {
        // 1️⃣ SETUP - Подготовить данные
        ProfileDto profile = TestDataGenerator.Profile.valid();
        log.info("Создаю профиль: {}", profile);
        
        // 2️⃣ ACT - Выполнить API запрос
        var response = given()
            .contentType("application/json")
            .header("Authorization", getAuthHeader())
            .body(profile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(200)  // ⚠️ ВНИМАНИЕ: сейчас API возвращает 201, это ошибка!
            .extract()
            .response();
        
        // 3️⃣ ASSERT - Проверить результат
        assertNotNull(response.jsonPath().get("content.id"));
        log.info("✅ Профиль успешно создан!");
    }
    
    @Test
    @DisplayName("Должен вернуть ошибку при неправильном MSISDN")
    void shouldFailWithInvalidMsisdn() {
        // 1️⃣ SETUP - Создать невалидный профиль
        ProfileDto invalidProfile = TestDataGenerator.Profile
            .withInvalidMsisdn(TestDataGenerator.MsisdnInvalidType.TOO_SHORT);
        
        // 2️⃣ ACT + ASSERT - Запросить и проверить ошибку
        given()
            .contentType("application/json")
            .header("Authorization", getAuthHeader())
            .body(invalidProfile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(400);  // Ожидаем Bad Request
        
        log.info("✅ Ошибка валидации обработана правильно");
    }
    
    @Test
    @DisplayName("Должен вернуть 401 при отсутствии токена")
    void shouldFailWithoutAuth() {
        ProfileDto profile = TestDataGenerator.Profile.valid();
        
        given()
            .contentType("application/json")
            // ❌ Не добавляем Authorization заголовок
            .body(profile.toJson())
        .when()
            .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
        .then()
            .statusCode(401);  // ⚠️ ВНИМАНИЕ: сейчас API возвращает 403, это ошибка!
        
        log.info("✅ Авторизация проверена");
    }
}
```

### Правила написания тестов:

✅ **DO:**
1. Наследовать `BaseApiTest`
2. Использовать `@DisplayName` для описания
3. Использовать `@Test` для методов-тестов
4. Логировать важные моменты через `log.info()`
5. Использовать `TestDataGenerator` для данных
6. Использовать `getAuthHeader()` для авторизации
7. Проверять `statusCode()` сразу после запроса

❌ **DON'T:**
1. Не копировать URL и креденшалы (использовать `ApiConfig`)
2. Не строить JSON вручную (использовать `toJson()`)
3. Не добавлять try-catch (тест должен упасть если ошибка)
4. Не создавать новый токен каждый раз (используется кэш)
5. Не использовать System.out.println (используйте log)

---

## 📊 КАК ИСПОЛЬЗОВАТЬ ДАННЫЕ

### 1. Валидные данные
```java
// Профиль
ProfileDto profile = TestDataGenerator.Profile.valid();
// { "msisdn": "996800xxxx", "userId": 1, "pricePlanId": 3 }

// Баланс
BalanceDto balance = TestDataGenerator.Balance.valid();
// { "msisdn": "996800xxxx", "amount": 1000, "operationType": "CREDIT" }

// Счётчик
CounterDto counter = TestDataGenerator.Counter.valid();
// { "counterId": 1, "status": "ACTIVE" }
```

### 2. Невалидные данные
```java
// Профиль с коротким MSISDN
ProfileDto badProfile = TestDataGenerator.Profile
    .withInvalidMsisdn(TestDataGenerator.MsisdnInvalidType.TOO_SHORT);
// { "msisdn": "123", "userId": 1, "pricePlanId": 3 }

// Баланс с неправильным типом операции
BalanceDto badBalance = TestDataGenerator.Balance
    .withInvalidOperationType("INVALID");
```

### 3. Кастомные данные
```java
// Если встроенных вариантов недостаточно:
ProfileDto custom = ProfileDto.builder()
    .msisdn("996809876543")  // Свой MSISDN
    .userId(99)
    .pricePlanId(5)
    .build();
```

---

## ▶️ ЗАПУСК ТЕСТОВ

### Вариант 1: Все тесты
```bash
mvn clean test
```
**Результат:** Запустятся все 32 теста

### Вариант 2: Только один класс
```bash
mvn test -Dtest=AuthApiTest
```
**Результат:** Запустятся только тесты авторизации

### Вариант 3: Только один тест
```bash
mvn test -Dtest=AuthApiTest#shouldGetValidToken
```
**Результат:** Запустится только один конкретный тест

### Вариант 4: С логированием (DEBUG)
```bash
mvn test -X
```
**Результат:** Очень много информации (для отладки)

### Вариант 5: Параллельный запуск (быстрее)
```bash
mvn test -DthreadCount=4
```
**Результат:** Запуск в 4 потока (быстрее примерно в 3 раза)

---

## 🔍 ОТЛАДКА

### 1. Смотри логи
```bash
# В консоли во время тестов видишь:
[INFO] Запрос к POST /api/auth/sign_in
[INFO] Ответ: 200 OK + тело
[DEBUG] Токен: eyJhbGciOiJIUzI1NiIsInR5cCI...
```

### 2. Добавь свои логи
```java
@Test
void myTest() {
    log.debug("Отладочная информация: {}", someVar);
    log.info("Обычная информация");
    log.warn("Предупреждение");
    log.error("Ошибка", exception);
}
```

### 3. Включи REST Assured логирование
```java
given()
    .log().all()  // ← Логировать запрос
    .contentType("application/json")
    // ...
.when()
    .post(url)
.then()
    .log().all()  // ← Логировать ответ
    .statusCode(200);
```

### 4. Сохрани ответ для анализа
```java
Response response = given()
    .contentType("application/json")
    // ...
.when()
    .post(url)
.then()
    .statusCode(200)
    .extract()
    .response();

String body = response.body().asString();
System.out.println(body);  // Посмотри полное содержимое
```

---

## ❓ FAQ

### В: Почему некоторые тесты падают?
**О:** Смотри `API_ISSUES_REPORT.md` - это не ошибки в тестах, это ошибки в API!

### В: Как добавить новый тест?
**О:** 
1. Создай класс в папке `tests/` наследующий `BaseApiTest`
2. Используй `@Test` и `@DisplayName` на методах
3. Используй `TestDataGenerator` для данных
4. Используй `getAuthHeader()` для авторизации
5. Проверяй `statusCode()` и другие утверждения

### В: Почему медленно запускаются тесты?
**О:** Первый тест долго получает токен, остальные быстро (используется кэш). Если даже второй медленный - это проблема сервера.

### В: Как сбросить кэш токена?
**О:** Добавь в тест:
```java
@BeforeEach
void setup() {
    ApiConfig.resetToken();  // Сбросить кэш перед каждым тестом
}
```

### В: Можно ли запускать тесты параллельно?
**О:** Да! Используем `ThreadLocal` в `ApiConfig`, поэтому каждый поток имеет свой токен:
```bash
mvn test -DthreadCount=8
```

### В: Где хранятся результаты тестов?
**О:** В `target/surefire-reports/`:
```bash
target/surefire-reports/
├── qabilling.tests.AuthApiTest.txt
├── qabilling.tests.ProfileApiTest.txt
└── ...
```

### В: Как добавить новый DTO?
**О:**
1. Создай класс наследующий `BaseDto` в папке `core/dto/`
2. Реализуй метод `toJson()`
3. Добавь `equals()` и `hashCode()`
4. Добавь Builder для удобства
5. Добавь в `TestDataGenerator` методы для создания валидных/невалидных экземпляров

### В: Как добавить новый endpoint для тестирования?
**О:**
1. Создай DTO (если нужен)
2. Добавь методы в `TestDataGenerator`
3. Создай новый тестовый класс
4. Напиши тесты
5. Запусти: `mvn test -Dtest=NewTest`

---

## 🎓 ДОПОЛНИТЕЛЬНЫЕ РЕСУРСЫ

| Документ | Для кого | Что читать |
|----------|---------|-----------|
| `README.md` | Все | Быстрый старт и основное описание |
| `ARCHITECTURE.md` | Архитекторы, Senior QA | Глубокое погружение в архитектуру |
| `CONTRIBUTION_GUIDE.md` | Разработчики | Как контрибьютить в проект |
| `API_ISSUES_REPORT.md` | QA, Разработчики API | Отчёт об всех ошибках в API |
| `DEVELOPER_GUIDE.md` | 👈 ТЫ ЗДЕСЬ | Подробное руководство разработчика |

---

## 📞 КОНТАКТЫ И ПОМОЩЬ

**Что-то не работает?**
1. Проверь логи: `mvn test -X`
2. Смотри `API_ISSUES_REPORT.md` (может это известная ошибка)
3. Гугли ошибку в REST Assured документации
4. Проверь что API сервер запущен: `curl http://195.38.164.168:7173/health`

---

**Счастливого тестирования! 🚀**
