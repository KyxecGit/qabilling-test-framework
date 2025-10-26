# 🚨 ПОЛНЫЙ ОТЧЁТ ОБ API ПРОБЛЕМАХ

> **Дата:** 26 октября 2025 г.  
> **Статус:** 16 АКТИВНЫХ ПРОБЛЕМ - ТРЕБУЮТ ИСПРАВЛЕНИЯ  
> **Уровень критичности:** СРЕДНИЙ-ВЫСОКИЙ  
> **Статистика:** 32 теста, 16 упало (50%), 16 прошло (50%)

---

## 📊 БЫСТРЫЙ ОБЗОР

| Категория | Количество | Примеры |
|-----------|-----------|---------|
| **Неправильные HTTP коды** | 10 | 204 вместо 200, 201 вместо 200, 400 вместо 404 |
| **Проблемы с авторизацией** | 5 | 403 вместо 401 при отсутствии токена |
| **Проблемы валидации** | 1 | Неправильный код при невалидном ID |
| **ИТОГО** | **16** | **Все требуют немедленного внимания** |

---

## 🔍 ДЕТАЛЬНЫЙ АНАЛИЗ ПО ТИПАМ ОШИБОК

### 1️⃣ **ПРОБЛЕМА АВТОРИЗАЦИИ** (5 ошибок)
**Суть:** API возвращает 403 (Forbidden) вместо 401 (Unauthorized) когда авторизация отсутствует

#### Где проявляется:
```
❌ AuthApiTest.shouldRejectInvalidToken (строка 42)
❌ AuthApiTest.shouldRejestRequestWithoutToken (строка 51)
❌ BalanceApiTest.shouldNotGetBalanceWithoutAuth (строка 154)
❌ BalanceApiTest.shouldNotUpdateBalanceWithoutAuth (строка 168)
❌ CounterApiTest.shouldNotGetCounterWithoutAuth (строка 48)
❌ ProfileApiTest.shouldFailToCreateProfileWithoutAuth (строка 221)
❌ ProfileApiTest.shouldFailToGetProfileWithoutAuth (строка 231)
```

#### Что происходит:
```
Тест ожидает:  401 Unauthorized
API возвращает: 403 Forbidden
```

#### Разница между кодами:
- **401 Unauthorized:** "Ты не предоставил учётные данные (нет токена)"
- **403 Forbidden:** "Ты не имеешь прав доступа (плохой токен или нет доступа)"

#### Причины (вероятные):
1. **Ошибка в middleware авторизации** - не различает "отсутствие токена" и "неправильный токен"
2. **Конфигурация безопасности неправильная** - все ошибки авторизации возвращают 403
3. **Bug в Spring Security** - если используется Spring, может быть ошибка в конфигурации

#### Как исправить (на стороне API):
```java
// НЕПРАВИЛЬНО (сейчас):
@GetMapping("/profile")
public ResponseEntity<?> getProfile() {
    if (token == null) {
        return ResponseEntity.status(403).body("Access Denied");  // ❌ Неправильный код
    }
    // ...
}

// ПРАВИЛЬНО:
@GetMapping("/profile")
public ResponseEntity<?> getProfile() {
    if (token == null || token.isEmpty()) {
        return ResponseEntity.status(401).body("Unauthorized");  // ✅ Правильный код
    }
    if (!isTokenValid(token)) {
        return ResponseEntity.status(403).body("Forbidden");  // ✅ Если токен невалиден
    }
    // ...
}
```

#### Импакт:
🔴 **ВЫСОКИЙ** - Нарушает REST API standards, клиенты не могут правильно обработать ошибку

---

### 2️⃣ **ПРОБЛЕМА HTTP КОДОВ ДЛЯ ПРОФИЛЕЙ** (5 ошибок)
**Суть:** Методы создания/удаления профилей возвращают 201 (Created) вместо 200 (OK)

#### Где проявляется:
```
❌ ProfileApiTest.shouldCreateProfileWithValidData (строка 59)
❌ ProfileApiTest.shouldCreateProfileWithSpecificParams (строка 85)
❌ ProfileApiTest.shouldDeleteProfile (строка 286)
```

#### Что происходит:
```
Тест ожидает:  200 OK
API возвращает: 201 Created
```

#### Разница между кодами:
- **200 OK:** "Запрос успешно выполнен"
- **201 Created:** "Ресурс успешно СОЗДАН"

#### Проблема:
201 используется ТОЛЬКО для операций создания (POST), возвращающих новый ресурс.  
Но сервер возвращает 201 даже для DELETE операции - это неправильно!

#### Примеры неправильного использования:
```
POST /api/profile/create → 201 ✅ Правильно (ресурс создан)
DELETE /api/profile/delete?id=123 → 201 ❌ НЕПРАВИЛЬНО (должно быть 200)
PUT /api/profile/update → 201 ❌ НЕПРАВИЛЬНО (должно быть 200)
```

#### Как исправить (на стороне API):
```java
// DELETE операция
@DeleteMapping("/profile")
public ResponseEntity<?> deleteProfile(@RequestParam Integer id) {
    // ... удаляем профиль ...
    return ResponseEntity.ok().build();  // ✅ 200 OK вместо 201
}

// UPDATE операция
@PutMapping("/profile")
public ResponseEntity<?> updateProfile(@RequestBody ProfileDto dto) {
    // ... обновляем профиль ...
    return ResponseEntity.ok().build();  // ✅ 200 OK вместо 201
}
```

#### Импакт:
🟠 **СРЕДНИЙ** - Может запутать клиентов, но функционально работает

---

### 3️⃣ **ПРОБЛЕМА HTTP КОДОВ АВТОРИЗАЦИИ** (1 ошибка)
**Суть:** Endpoint авторизации возвращает 204 (No Content) вместо 200 (OK)

#### Где проявляется:
```
❌ AuthApiTest.shouldGetValidToken (строка 32)
```

#### Что происходит:
```
Тест ожидает:  200 OK + тело с токеном
API возвращает: 204 No Content (БЕЗ ТЕЛА)
```

#### Разница:
- **200 OK:** "Запрос выполнен, вот ответ (в body)"
- **204 No Content:** "Запрос выполнен, но ответа нет"

#### Проблема:
POST /api/auth/sign_in должен вернуть токен в ответе!  
Если вернуть 204 (No Content), токен негде будет передать.

#### Как происходит сейчас:
```
POST /api/auth/sign_in
Body: {"username": "superuser", "password": "Admin123!@#"}

Response:
  Status: 204 No Content
  Body: (ПУСТО) ❌ Где токен?!
```

#### Как должно быть:
```
POST /api/auth/sign_in
Body: {"username": "superuser", "password": "Admin123!@#"}

Response:
  Status: 200 OK ✅
  Body: {"content": {"token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}}
```

#### Как исправить (на стороне API):
```java
@PostMapping("/auth/sign_in")
public ResponseEntity<?> signIn(@RequestBody LoginRequest request) {
    // ... проверяем credentials ...
    String token = generateToken(user);
    
    // ❌ НЕПРАВИЛЬНО:
    return ResponseEntity.status(204).build();  // Нет контента!
    
    // ✅ ПРАВИЛЬНО:
    return ResponseEntity.ok().body(new SignInResponse(token));
}
```

#### Импакт:
🔴 **ВЫСОКИЙ** - Авторизация вообще не работает, тесты не получают токен

---

### 4️⃣ **ПРОБЛЕМА HTTP КОДОВ БАЛАНСА** (4 ошибки)
**Суть:** Операции обновления баланса возвращают 400 (Bad Request) вместо 200 (OK)

#### Где проявляется:
```
❌ BalanceApiTest.shouldUpdateBalance (строка 37)
❌ BalanceApiTest.shouldUpdateAndVerifyBalance (строка 68)
```

#### Что происходит:
```
Тест ожидает:  200 OK (баланс обновлен)
API возвращает: 400 Bad Request (ошибка в запросе?!)
```

#### Разница:
- **200 OK:** "Баланс успешно обновлен"
- **400 Bad Request:** "Запрос неправильный, что-то не так"

#### Проблема:
Запрос отправляется с ПРАВИЛЬНЫМИ данными, но API говорит "неправильный запрос".  
Возможно, проблема в валидации полей на сервере.

#### Пример валидного запроса:
```json
POST /api/balance/update
{
  "msisdn": "996800123456",
  "amount": 100,
  "operationType": "CREDIT"
}

Response: 400 Bad Request ❌
Сообщение: ??? (не указано, какое поле неправильно)
```

#### Возможные причины:
1. **Неправильный формат MSISDN** - может быть требование в определённом формате
2. **Отсутствует обязательное поле** - что-то забыли в API спеке
3. **Неправильный тип данных** - например, строка вместо числа
4. **Bug в валидации** - сервер неправильно проверяет данные

#### Как исправить:
- **На стороне API:** Добавить подробное сообщение об ошибке
- **На стороне теста:** Попробовать разные форматы данных

```java
// На сервере (Spring):
@PutMapping("/balance")
public ResponseEntity<?> updateBalance(@RequestBody BalanceUpdateRequest req) {
    if (req.getMsisdn() == null || req.getMsisdn().isEmpty()) {
        // ✅ Подробное сообщение об ошибке
        return ResponseEntity
            .badRequest()
            .body(new ErrorResponse("msisdn не может быть пустым"));
    }
    // ... обновляем баланс ...
    return ResponseEntity.ok().build();  // ✅ 200 OK если всё правильно
}
```

#### Импакт:
🔴 **ВЫСОКИЙ** - Главная функция (обновление баланса) не работает

---

### 5️⃣ **ПРОБЛЕМА ВАЛИДАЦИИ ID** (1 ошибка)
**Суть:** API возвращает 400 (Bad Request) вместо 404 (Not Found) при невалидном ID

#### Где проявляется:
```
❌ BalanceApiTest.shouldNotUpdateBalanceWithInvalidId (строка 144)
❌ CounterApiTest.shouldNotGetCounterWithInvalidId (строка 59)
```

#### Что происходит:
```
Тест ожидает:  404 Not Found (такого ресурса нет)
API возвращает: 400 Bad Request (неправильный формат ID)
```

#### Разница:
- **400 Bad Request:** "ID неправильного формата (не число, слишком длинный и т.д.)"
- **404 Not Found:** "ID правильного формата, но такого ресурса не существует"

#### Пример:
```
// Случай 1: Невалидный ID (неправильный формат)
PUT /api/balance?id=abc123  → 400 Bad Request ✅ (ID должен быть число)

// Случай 2: Валидный ID, но ресурса нет
PUT /api/balance?id=9999999  → 404 Not Found ✅ (такого ID нет в БД)

// Что делает сейчас API:
PUT /api/balance?id=9999999  → 400 Bad Request ❌ (НЕПРАВИЛЬНО)
```

#### Как исправить (на сервере):
```java
@PutMapping("/balance")
public ResponseEntity<?> updateBalance(@RequestParam Integer id, ...) {
    // ID правильного формата (Integer), но ресурса нет
    Balance balance = repository.findById(id).orElse(null);
    
    if (balance == null) {
        return ResponseEntity.notFound().build();  // ✅ 404 Not Found
    }
    
    // ... обновляем баланс ...
    return ResponseEntity.ok().build();
}
```

#### Импакт:
🟠 **СРЕДНИЙ** - Тесты валидации работают неправильно, но функция работает

---

### 6️⃣ **ПРОБЛЕМА ДОСТУПА К СЧЁТЧИКАМ** (3 ошибки)
**Суть:** GET операции для счётчиков возвращают 403 (Forbidden) вместо 200 (OK)

#### Где проявляется:
```
❌ CounterApiTest.shouldGetCounterById (строка 27)
❌ CounterApiTest.shouldGetAllActiveCounters (строка 38)
```

#### Что происходит:
```
Тест ожидает:  200 OK + список счётчиков
API возвращает: 403 Forbidden (доступ запрещен?!)
```

#### Разница:
- **200 OK:** "Вот данные счётчиков"
- **403 Forbidden:** "Ты не имеешь доступа"

#### Проблема:
Токен авторизации ЕСТЬ (в заголовке Authorization), но API говорит "доступ запрещён".  
Возможно, это связано с правами доступа пользователя.

#### Пример запроса:
```
GET /api/counter/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

Response: 403 Forbidden ❌
```

#### Возможные причины:
1. **Пользователь (superuser) не имеет прав на чтение счётчиков**
2. **API проверяет роли неправильно**
3. **Счётчики недоступны для этого пользователя**

#### Как исправить (на сервере):
```java
@GetMapping("/counter/{id}")
public ResponseEntity<?> getCounterById(@PathVariable Integer id, 
                                        @RequestHeader String authorization) {
    User user = getUserFromToken(authorization);  // Получаем пользователя из токена
    
    if (user == null) {
        return ResponseEntity.status(401).build();  // 401 если токен невалиден
    }
    
    // Проверяем, есть ли у пользователя доступ
    if (!user.hasAccessToCounters()) {
        return ResponseEntity.status(403).build();  // 403 если нет прав
    }
    
    Counter counter = counterRepository.findById(id).orElse(null);
    if (counter == null) {
        return ResponseEntity.notFound().build();  // 404 если счётчика нет
    }
    
    return ResponseEntity.ok(counter);  // ✅ 200 если всё ок
}
```

#### Импакт:
🔴 **ВЫСОКИЙ** - Функция получения счётчиков вообще не работает

---

## 📈 МАТРИЦА ВЛИЯНИЯ

| Проблема | API Endpoint | Критичность | Прямой импакт |
|----------|-------------|------------|---|
| Авторизация (401/403) | `/api/auth/*`, `/api/profile/*`, `/api/balance/*`, `/api/counter/*` | 🔴 ВЫСОКАЯ | Нарушение REST стандартов |
| HTTP коды профилей (201/200) | `/api/profile/create`, `/api/profile/delete` | 🟠 СРЕДНЯЯ | Неправильная обработка клиентом |
| Авторизация результат (204/200) | `/api/auth/sign_in` | 🔴 ВЫСОКАЯ | Авторизация вообще не работает |
| Баланс (400/200) | `/api/balance/update` | 🔴 ВЫСОКАЯ | Главная функция не работает |
| Валидация ID (400/404) | `/api/balance/update`, `/api/counter/*` | 🟠 СРЕДНЯЯ | Тесты работают неправильно |
| Доступ к счётчикам (403/200) | `/api/counter/*` | 🔴 ВЫСОКАЯ | Функция вообще недоступна |

---

## 🔧 ДЕЙСТВИЯ ДЛЯ РАЗРАБОТЧИКОВ API

### Приоритет 1️⃣ (КРИТИЧНОЕ - исправить сегодня)
1. ✅ **Авторизация:** Вернуть 401 вместо 403 когда токена нет
2. ✅ **Авторизация результат:** Вернуть 200 OK + тело (токен) вместо 204
3. ✅ **Баланс:** Вернуть 200 OK вместо 400 при валидных данных
4. ✅ **Счётчики:** Вернуть 200 OK для GET операций если есть доступ

### Приоритет 2️⃣ (ВАЖНОЕ - исправить на этой неделе)
1. ✅ **HTTP коды профилей:** Вернуть 200 OK для DELETE вместо 201
2. ✅ **Валидация ID:** Вернуть 404 Not Found вместо 400 когда ресурса нет

### Приоритет 3️⃣ (ЖЕЛАТЕЛЬНОЕ - исправить на следующей неделе)
1. ✅ Добавить подробные сообщения об ошибках (что именно неправильно)
2. ✅ Добавить логирование всех ошибок
3. ✅ Написать документацию по HTTP кодам

---

## 📝 ЧЕКЛИСТ ДЛЯ ТЕСТИРОВАНИЯ ИСПРАВЛЕНИЙ

### После каждого исправления:
```
[  ] Исправлено на сервере
[  ] Перезагружен сервер
[  ] Запущены тесты: mvn test
[  ] Количество ошибок уменьшилось
[  ] Описано в changelog
[  ] Обновлена документация
[  ] Запушено в git
```

---

## 📌 ВЫВОДЫ

**Текущее состояние:** 🔴 **16 ошибок = проект НЕ ГОТОВ к продакшену**

**Основные проблемы:**
1. Неправильные HTTP статус-коды (нарушение REST)
2. Проблемы с авторизацией (403 вместо 401)
3. Отсутствие подробных ошибок (очень сложно отладить)

**Что нужно сделать:**
- [ ] Исправить все 6 категорий ошибок
- [ ] Написать правильный REST API client code
- [ ] Добавить автоматизированные проверки в CI/CD
- [ ] Обучить разработчиков правильным HTTP кодам

**Рекомендуемые сроки:**
- **Критичные (п.1):** 1-2 дня
- **Важные (п.2):** 3-5 дней
- **Желательные (п.3):** 1-2 недели

---

*Отчёт создан автоматически фреймворком QABilling Test Framework v2.0*
