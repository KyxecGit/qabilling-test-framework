package qabilling.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 📚 БАЗОВЫЙ КЛАСС ДЛЯ ВСЕХ ТЕСТОВ
 * 
 * Содержит:
 * - Логирование (автоматически показывает имя подкласса)
 * - Получение заголовка авторизации (Bearer токен)
 * 
 * Используй:
 *   public class MyTest extends BaseApiTest { ... }
 */
public abstract class BaseApiTest {
    
    // ✅ Instance logger (показывает правильное имя класса, не BaseApiTest)
    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * Получить готовый заголовок авторизации
     * @return "Bearer <token>"
     */
    protected String getAuthHeader() {
        return "Bearer " + ApiConfig.getToken();
    }
}
