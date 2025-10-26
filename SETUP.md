# 🔧 SETUP И ОКРУЖЕНИЕ

> **Для кого:** Тех, кто устанавливает проект в первый раз  
> **Время на чтение:** 10 минут  
> **Важность:** 🔴 КРИТИЧНАЯ (ничего не запустится без этого)

---

## ✅ ТРЕБОВАНИЯ

### Java
```bash
# Проверить установку
java -version

# Вывод должен быть примерно такой:
# java version "21.0.1" 2023-10-17 LTS
# Java(TM) SE Runtime Environment (build 21.0.1+12-LTS-29)
```

**Минимум:** Java 11  
**Рекомендуется:** Java 21 LTS

**Как установить Java 21:**
- **Windows:** https://www.oracle.com/java/technologies/downloads/#java21
- **macOS:** `brew install openjdk@21`
- **Linux:** `sudo apt-get install openjdk-21-jdk`

### Maven
```bash
# Проверить установку
mvn -version

# Вывод должен быть примерно такой:
# Apache Maven 3.9.11 (...)
# Java version: 21.0.1, vendor: Oracle Corporation
```

**Минимум:** Maven 3.8.1  
**Рекомендуется:** Maven 3.9.11+

**Как установить Maven:**
- **Windows:** https://maven.apache.org/install.html
- **macOS:** `brew install maven`
- **Linux:** `sudo apt-get install maven`

### Git
```bash
# Проверить установку
git --version

# Вывод должен быть примерно такой:
# git version 2.42.0
```

---

## 📥 УСТАНОВКА

### Шаг 1: Клонируй репо
```bash
git clone https://github.com/your-org/qabilling-test-framework-main.git
cd qabilling-test-framework-main
```

### Шаг 2: Проверь структуру (должно быть вот так)
```bash
ls -la

# Должно выводить:
# ARCHITECTURE.md
# CONTRIBUTION_GUIDE.md
# DEVELOPER_GUIDE.md
# SETUP.md
# pom.xml
# README.md
# src/
```

### Шаг 3: Скачай зависимости
```bash
mvn clean install
```

**Что происходит:**
- Maven скачивает все зависимости из `pom.xml`
- Компилирует код
- Готово!

**Это может занять 2-3 минуты в первый раз.**

### Шаг 4: Проверь что всё работает
```bash
mvn test
```

**Ожидаемый результат:**
```
[INFO] Tests run: 32, Failures: 16, Errors: 0, Skipped: 0, Time elapsed: 6.3 s

[ERROR] Tests run: 32, Failures: 16, Errors: 0, Skipped: 0
[ERROR] -> [Help 1]

BUILD FAILURE
```

**⚠️ ВНИМАНИЕ:** Тесты должны УПАСТЬ! Это нормально - это ошибки в API (смотри `API_ISSUES_REPORT.md`).

**Если вместо этого видишь компиляцию или другие ошибки - что-то не так.**

---

## 🌐 КОНФИГУРАЦИЯ API

### Текущее окружение
По умолчанию проект настроен на:
```
URL: http://195.38.164.168:7173
Username: superuser
Password: Admin123!@#
```

**Где это? В файле:** `src/test/java/qabilling/core/ApiConfig.java`

### Если нужно изменить окружение

#### Способ 1: Через код (быстро, для тестирования)
```java
// Отредактируй ApiConfig.java
public class ApiConfig {
    public static final String BASE_URL = "http://другой-адрес:порт";
    public static final String USERNAME = "другой-юзер";
    public static final String PASSWORD = "другой-пароль";
}
```

#### Способ 2: Через переменные окружения (для CI/CD)
```bash
# Экспортируй переменные
export API_BASE_URL=http://другой-адрес:7173
export API_USERNAME=superuser
export API_PASSWORD=Admin123!@#

# Потом отредактируй ApiConfig.java чтобы читал из переменных:
public static final String BASE_URL = 
    System.getenv("API_BASE_URL", "http://195.38.164.168:7173");
```

---

## 🏃 ПЕРВЫЙ ЗАПУСК

### 1. Запусти ВСЕ тесты
```bash
mvn clean test
```

**Что должно быть:**
- Компиляция успешна ✅
- Тесты запустились ✅
- Некоторые тесты упали (это API ошибки) ✅

### 2. Запусти ОДИН тест
```bash
mvn test -Dtest=AuthApiTest#shouldGetValidToken
```

**Что должно быть:**
- Тест упал с ошибкой 204 вместо 200 (это API ошибка, описана в `API_ISSUES_REPORT.md`)

### 3. Посмотри логи
```bash
[INFO] --- maven-surefire-plugin:3.2.2:test (default-test) @ billing ---
[INFO] Using auto detected provider org.apache.maven.surefire.junitplatform.JUnitPlatformProvider
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running qabilling.tests.AuthApiTest
[ERROR] Tests run: 3, Failures: 3, ...
```

---

## 🐛 ТИПИЧНЫЕ ПРОБЛЕМЫ

### Проблема 1: "Java not found"
```
'java' is not recognized as an internal or external command
```

**Решение:**
1. Установи Java 21
2. Добавь в PATH: `C:\Program Files\Java\jdk-21\bin` (Windows)
3. Перезагрузи консоль/IDE

**Проверь:**
```bash
java -version
# Должно работать
```

---

### Проблема 2: "Maven not found"
```
'mvn' is not recognized as an internal or external command
```

**Решение:**
1. Установи Maven 3.9+
2. Добавь в PATH: `C:\apache-maven-3.9.11\bin` (Windows)
3. Перезагрузи консоль/IDE

**Проверь:**
```bash
mvn -version
# Должно работать
```

---

### Проблема 3: "Cannot find symbol" (при компиляции)
```
[ERROR] /path/to/AuthApiTest.java:[32,1] error: cannot find symbol
  symbol:   class AuthApiTest
  location: package qabilling.tests
```

**Решение:**
```bash
# 1. Очисти проект
mvn clean

# 2. Пересоберись
mvn compile

# 3. Переоткрой IDE (IntelliJ / Eclipse)
```

---

### Проблема 4: "Cannot connect to server"
```
java.net.ConnectException: Connection refused: http://195.38.164.168:7173
```

**Решение:**
1. Убедись что сервер запущен: `curl http://195.38.164.168:7173/health`
2. Если не запущен - запусти его
3. Проверь что ты можешь пингануть сервер: `ping 195.38.164.168`
4. Проверь firewall (может блокирует)

---

### Проблема 5: "Tests stuck / frozen"
Тесты запустились но зависли (ничего не происходит)

**Решение:**
1. Убедись что сервер запущен
2. Убедись что сеть подключена
3. Попробуй запустить с timeout'ом:
```bash
mvn test -o  # offline mode
```

---

## 🎯 ОКРУЖЕНИЕ РАЗРАБОТКИ

### Рекомендуемая IDE

#### **IntelliJ IDEA** (лучший выбор для Java)
```bash
# Установка
1. Скачай с https://www.jetbrains.com/idea/
2. Выбери Community Edition (бесплатно)
3. Установи
4. Открой проект: File → Open → /path/to/qabilling-test-framework-main
5. IDE автоматически скачает все зависимости
```

**Что нужно сделать:**
- ✅ File → Project Structure → SDK → выбери Java 21
- ✅ File → Settings → Maven → убедись что Maven найден автоматически
- ✅ Right Click на `pom.xml` → Add as Maven Project

#### **VS Code** (легковесный, бесплатный)
```bash
# Расширения которые нужны:
1. Extension Pack for Java (Microsoft)
2. Maven for Java (Microsoft)
3. Test Runner for Java (Microsoft)

# Потом откроешь проект - VS Code автоматически всё настроит
```

#### **Eclipse** (если выбрал это)
```bash
# Установка
1. Скачай с https://www.eclipse.org/downloads/
2. Выбери Eclipse IDE for Enterprise Java and Web Developers
3. Установи
4. File → Import → Existing Maven Projects
5. Выбери /path/to/qabilling-test-framework-main
```

### Git конфиг (обязательно!)
```bash
# Установи свои данные (для коммитов)
git config --global user.name "Твоё Имя"
git config --global user.email "твой@email.com"

# Проверь
git config --list | grep user
```

---

## 📊 SYSTEM REQUIREMENTS

| Компонент | Минимум | Рекомендуется |
|-----------|---------|--------------|
| Java | 11 | 21 LTS |
| Maven | 3.8.1 | 3.9.11+ |
| RAM | 2 GB | 4 GB |
| Disk | 500 MB | 2 GB |
| OS | Windows 7+ / macOS 10.12+ / Linux | Windows 10+, macOS 11+, Linux modern |

---

## ✨ ГОТОВО!

**Когда всё установится:**
```bash
mvn clean test
```

**Должно вывести:**
```
[INFO] Tests run: 32, Failures: 16
[ERROR] BUILD FAILURE
```

**Это НОРМАЛЬНО!** Это не твоя ошибка - это ошибки в API.

Читай `API_ISSUES_REPORT.md` чтобы понять какие ошибки в API и почему тесты падают.

---

## 🆘 ЧТО ЕСЛИ ЧТО-ТО ПОШЛО НЕ ТАК?

### Вариант 1: Полная очистка и переустановка
```bash
# 1. Удали Maven кэш
rm -rf ~/.m2/repository  # macOS/Linux
# ИЛИ
del /s %USERPROFILE%\.m2\repository  # Windows

# 2. Очисти проект
mvn clean

# 3. Переустанови зависимости
mvn clean install
```

### Вариант 2: Переоткрой IDE и проект
```bash
# 1. Закрой IDE
# 2. Удали папку .idea / .vscode (кэш IDE)
rm -rf .idea  # IntelliJ
rm -rf .vscode  # VS Code

# 3. Переоткрой IDE
# 4. Переоткрой проект
```

### Вариант 3: Проверь все зависимости установлены
```bash
mvn dependency:resolve
mvn dependency:tree
```

---

**Если ничего не помогло - гугли ошибку или проси помощи в командном чате!** 💬
