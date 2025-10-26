# 🚀 QABilling API Test Framework
## Автоматизированное тестирование REST API с фокусом на Junior-friendly архитектуру

![Java](https://img.shields.io/badge/Java-21+-orange.svg)
![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)
![JUnit5](https://img.shields.io/badge/JUnit-5.10-green.svg)
![RestAssured](https://img.shields.io/badge/RestAssured-5.4-purple.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

---

## 🎯 **ЧТО ЭТО?**

**QABilling Test Framework** — это **автоматизированное тестирование REST API** QABilling с использованием Java, JUnit 5 и Rest Assured.

**Главная цель:** Обеспечить **максимально простой онбординг** для Junior специалистов, одновременно следуя лучшим практикам (Clean Code, DRY, SOLID).

**Что фреймворк проверяет:**
- ✅ API endpoints для управления профилями абонентов
- ✅ Операции с балансом (пополнение, получение)
- ✅ Работу со счётчиками (активные, удалённые)
- ✅ Авторизацию и безопасность
- 🐛 **Находит баги в API** (уже выявлено 21 несоответствие со Swagger)

---

## 📋 **ТРЕБОВАНИЯ**

Перед запуском убедись, что у тебя установлено:

| Компонент | Версия | Проверка |
|-----------|--------|----------|
| **Java JDK** | 21+ | `java -version` |
| **Apache Maven** | 3.8+ | `mvn -version` |
| **Git** | любая | `git --version` |

**Дополнительно (опционально):**
- **Allure CLI** для красивых отчётов: `npm install -g allure-commandline`

---

## ⚡ **БЫСТРЫЙ СТАРТ (15 минут)**

### Шаг 1: Клонирование репозитория
```bash
git clone https://github.com/KyxecGit/qabilling-test-framework.git
cd qabilling-test-framework
```

### Шаг 2: Загрузка зависимостей
```bash
mvn clean install
```
**Что происходит:** Maven скачает все нужные библиотеки (JUnit 5, Rest Assured, логирование и т.д.)

### Шаг 3: Запуск тестов
```bash
mvn clean verify
```

**Ожидаемый результат:**
```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Tests run: 32, Failures: 16, Errors: 0, Skipped: 0
[INFO] BUILD SUCCESS
```

**16 тестов падает = 16 ошибок API выявлено!** 🎯 Подробнее в [API_ISSUES_REPORT.md](API_ISSUES_REPORT.md)

---

## 🏗️ **СТРУКТУРА ПРОЕКТА**

```
📁 qabilling-test-framework/
│
├── 📄 README.md                    ← Этот файл (для новичков)
├── 📄 SETUP.md                     ← Установка и конфигурация
├── 📄 DEVELOPER_GUIDE.md           ← Полный гайд разработчика
├── 📄 ARCHITECTURE.md              ← Как устроен фреймворк
├── 📄 API_ISSUES_REPORT.md         ← Баги в API (16 ошибок)
├── 📄 CONTRIBUTION_GUIDE.md        ← Как писать новые тесты
├── 📄 DOCUMENTATION_INDEX.md       ← Навигация по документам
│
├── 📁 src/test/
│   ├── java/qabilling/
│   │   ├── 🔧 ApiConfig.java               ← Конфигурация (URL, пароли, ThreadLocal)
│   │   ├── 📚 BaseApiTest.java            ← Базовый класс для всех тестов
│   │   │
│   │   ├── � tests/                      ← ✅ ВСЕ ТЕСТЫ ЗДЕСЬ!
│   │   │   ├── ProfileApiTest.java        ← Тесты профилей абонентов
│   │   │   ├── BalanceApiTest.java        ← Тесты баланса
│   │   │   ├── CounterApiTest.java        ← Тесты счётчиков
│   │   │   └── AuthApiTest.java           ← Тесты авторизации
│   │   │
│   │   ├── 📦 dto/                        ← Data Transfer Objects (модели)
│   │   │   ├── BaseDto.java
│   │   │   ├── ProfileDto.java            ← С equals/hashCode ✅
│   │   │   ├── BalanceDto.java            ← С equals/hashCode ✅
│   │   │   └── CounterDto.java            ← Новый DTO ✅
│   │   │
│   │   ├── 🎲 testdata/                   ← Генераторы тестовых данных
│   │   │   └── TestDataGenerator.java     ← С классом Counter ✅
│   │   │
│   │   └── 🎁 utils/                      ← Вспомогательные функции
│   │       ├── AuthUtils.java             ← Получение JWT токена ✅
│   │       └── ApiAssertions.java         ← Готовые проверки для API
│   │
│   └── resources/
│       └── 📋 logback.xml                 ← Конфигурация логирования
│
├── 📄 pom.xml                      ← Maven конфигурация
└── 📄 target/                      ← Скомпилированные тесты (не коммитим)
```

---

## 📖 **КАК ИСПОЛЬЗОВАТЬ**

### Запуск всех тестов
```bash
mvn clean verify
```

### Запуск только tests класса
```bash
mvn clean verify -Dtest=ProfileApiTest
```

### Запуск конкретного теста
```bash
mvn clean verify -Dtest=ProfileApiTest#shouldCreateProfileWithValidData
```

### Запуск с логированием в консоль
```bash
mvn clean verify -X
```

### Просмотр логов в файле
```bash
tail -f logs/test.log
```

---

## ✍️ **КАК ДОБАВИТЬ НОВЫЙ ТЕСТ**

### 🎯 **5 Шагов для Junior:**

#### **Шаг 1:** Создай новый Test-класс
```java
// File: src/test/java/qabilling/tests/MyNewApiTest.java
package qabilling;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

@DisplayName("🔥 Тесты для моей новой фичи")
public class MyNewApiTest extends BaseApiTest {
    
    @Test
    @DisplayName("Описание теста на русском (это будет видно в отчёте)")
    void shouldDoSomethingImportant() {
        // Код теста пишешь здесь
    }
}
```

#### **Шаг 2:** Используй готовые DTO
```java
ProfileDto profile = TestDataGenerator.Profile.valid();
// Профиль создан с правильными данными ✅
```

#### **Шаг 3:** Вызови API
```java
given()
    .header("Authorization", getAuthHeader())  // ← Получишь токен автоматически
    .contentType("application/json")
    .body(profile.toJson())
.when()
    .post(ApiConfig.BASE_URL + "/api/admin/profile/create")
.then()
    .statusCode(ApiAssertions.OK())            // ← Вместо .statusCode(200)
    .body(ApiAssertions.isOkResponse());       // ← Готовая проверка
```

#### **Шаг 4:** Запусти новый тест
```bash
mvn clean verify -Dtest=MyNewApiTest
```

#### **Шаг 5:** Проверь логи
```bash
cat logs/test.log | grep "MyNewApiTest"
```

**Вот и всё! 🎉**

---

## 🤔 **ЧАСТО ЗАДАВАЕМЫЕ ВОПРОСЫ**

**В:** Где хранятся пароли и учётные данные?  
**О:** В `src/test/resources/config.properties`. Не комитим пароли в Git! Используй `config.example.properties` как шаблон.

**В:** Как запустить тесты на другом сервере (staging vs prod)?  
**О:** Измени `ApiConfig.BASE_URL` или используй переменную окружения: `export API_URL="https://staging.example.com" && mvn test`

**В:** Почему некоторые тесты падают?  
**О:** Потому что API нарушает HTTP стандарты. Подробно смотри **`API_ISSUES_REPORT.md`** - там все 16 ошибок API задокументированы с примерами и решениями.

**В:** Как добавить новую DTO?  
**О:** Скопируй `ProfileDto.java`, переименуй в `YourDto.java` и измени поля. Смотри `CONTRIBUTION_GUIDE.md` для деталей.

---

## 🔗 **ДАЛЬНЕЙШЕЕ ЧТЕНИЕ**

**📚 Быстрая навигация — смотри [DOCUMENTATION_INDEX.md](DOCUMENTATION_INDEX.md)** ← Полный справочник всех документов!

### 👨‍💼 Для новичков (1-2 часа):
1. **[README.md](README.md)** - Этот файл (обзор проекта)
2. **[SETUP.md](SETUP.md)** - Установка и конфигурация
3. **[DEVELOPER_GUIDE.md](DEVELOPER_GUIDE.md)** - Как писать тесты с примерами

### 🐛 Для отладки (30 минут):
4. **[API_ISSUES_REPORT.md](API_ISSUES_REPORT.md)** - Все 16 ошибок API с анализом

### 🏗️ Для архитектуры (1 час):
5. **[ARCHITECTURE.md](ARCHITECTURE.md)** - Как устроен фреймворк
6. **[CONTRIBUTION_GUIDE.md](CONTRIBUTION_GUIDE.md)** - Best practices для тестов

---

## 📞 **НУЖНА ПОМОЩЬ?**

1. Проверь логи: `cat logs/test.log`
2. Смотри примеры в `src/test/java/qabilling/tests/`
3. Читай комментарии в коде (все методы задокументированы)
4. Спроси у team lead или senior QA

---

**Happy Testing! 🚀**
