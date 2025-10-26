package qabilling.core.dto;

/**
 * 📦 БАЗОВЫЙ КЛАСС ДЛЯ ВСЕХ DTO ОБЪЕКТОВ
 * 
 * DTO = Data Transfer Object (Объект для передачи данных)
 * 
 * Назначение:
 * Этот базовый класс определяет контракт для всех DTO.
 * Все DTO-ки (ProfileDto, BalanceDto, CounterDto) наследуют этот класс.
 * 
 * Метод toJson():
 * - Конвертирует DTO в JSON для отправки в API
 * - КАЖДЫЙ потомок должен реализовать свой toJson()
 * - Пример: ProfileDto.toJson() → {"msisdn":"996800123456","userId":1,"pricePlanId":3}
 * 
 * Метод toString():
 * - Переопределён для красивого вывода в логах
 * - Показывает класс и его JSON представление
 * - Пример: "ProfileDto -> {"msisdn":"996800123456","userId":1,"pricePlanId":3}"
 * 
 * Пример использования:
 * {@code
 *   ProfileDto profile = new ProfileDto("996800123456", 1, 3);
 *   String json = profile.toJson();  // Получить JSON
 *   System.out.println(profile);  // Красивый вывод
 * }
 * 
 * @author QABilling Test Framework
 * @version 2.0
 * @since 2025-10-26
 */
public abstract class BaseDto {
    
    /**
     * 🔄 Конвертировать DTO в JSON строку
     * 
     * @return JSON представление объекта (должно быть валидным JSON)
     * 
     * Пример для ProfileDto:
     * {@code
     *   ProfileDto profile = new ProfileDto("996800123456", 1, 3);
     *   String json = profile.toJson();
     *   // Результат: {"msisdn":"996800123456","userId":1,"pricePlanId":3}
     * }
     */
    public abstract String toJson();
    
    /**
     * 📝 Переопределённый toString для красивого вывода
     * 
     * @return строка вида "ClassName -> {json}"
     * 
     * Пример:
     * {@code
     *   System.out.println(profile);
     *   // Вывод: ProfileDto -> {"msisdn":"996800123456","userId":1,"pricePlanId":3}
     * }
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " -> " + toJson();
    }
}
