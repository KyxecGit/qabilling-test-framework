package qabilling.core;

import qabilling.core.utils.AuthUtils;

/**
 * ⚙️ КОНФИГУРАЦИЯ API
 * 
 * Централизованное хранилище:
 * - URL базы API
 * - Credentials для авторизации
 * - Content-Type
 * - ThreadLocal для управления токенами (thread-safe)
 */
public class ApiConfig {
    
    public static final String BASE_URL = "http://195.38.164.168:7173";
    public static final String CONTENT_TYPE = "application/json";
    public static final String USERNAME = "superuser";
    public static final String PASSWORD = "Admin123!@#";
    
    // ✅ ThreadLocal = каждый тест-поток имеет свой токен (thread-safe)
    private static final ThreadLocal<String> cachedToken = new ThreadLocal<>();
    
    /**
     * Получить токен авторизации (с кешированием)
     * Если уже есть - вернёт его, если нет - получит новый
     */
    public static String getToken() {
        String token = cachedToken.get();
        if (token == null) {
            token = AuthUtils.getFreshToken();
            cachedToken.set(token);
        }
        return token;
    }
    
    /**
     * Сбросить токен (если он устарел)
     */
    public static void resetToken() {
        cachedToken.remove();
    }
    
    /**
     * Проверить есть ли токен
     */
    public static boolean hasToken() {
        return cachedToken.get() != null;
    }
}
