package qabilling.testdata;

import qabilling.core.dto.ProfileDto;
import qabilling.core.dto.BalanceDto;
import qabilling.core.dto.CounterDto;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 🎲 Генератор тестовых данных для API тестов
 * 
 * ✅ FIX: Добавлен полный набор для Counter с DTO
 */
public class TestDataGenerator {
    
    /**
     * Enum для типов невалидных MSISDN
     * Enum - это способ создания константных значений в Java.
     * Очень удобно для ограниченного набора вариантов.
     */
    public enum MsisdnInvalidType {
        TOO_SHORT("Слишком короткий MSISDN"),
        TOO_LONG("Слишком длинный MSISDN"), 
        WRONG_PREFIX("Неправильный префикс"),
        CONTAINS_LETTERS("Содержит буквы"),
        EMPTY("Пустая строка");
        
        private final String description;
        
        MsisdnInvalidType(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
    }
    
    /**
     * Вложенный класс для генерации MSISDN
     * 
     * Вложенные классы помогают группировать связанную функциональность
     * и делают код более читаемым: TestDataGenerator.Msisdn.valid()
     */
    public static class Msisdn {
        
        /**
         * Генерирует валидный MSISDN согласно Swagger pattern: ^99680\d{7}$
         * 
         * @return строка вида "99680xxxxxxx" где x - случайная цифра (12 цифр всего)
         * 
         * Примеры: "996801234567", "996809876543"
         */
        public static String valid() {
            // ThreadLocalRandom лучше чем Random в многопоточной среде
            int randomPart = ThreadLocalRandom.current().nextInt(1_000_000, 10_000_000);
            return "99680" + randomPart; // Это корректно: 99680 (5 цифр) + 7 цифр = 12 цифр
        }
        
        /**
         * Генерирует невалидный MSISDN указанного типа
         * 
         * @param type тип невалидного MSISDN
         * @return невалидная строка для негативных тестов
         */
        public static String invalid(MsisdnInvalidType type) {
            return switch (type) {
                case TOO_SHORT -> "123"; // 3 цифры вместо 12
                case TOO_LONG -> "996801234567890"; // 15 цифр вместо 12
                case WRONG_PREFIX -> {
                    int randomPart = ThreadLocalRandom.current().nextInt(1_000_000, 10_000_000);
                    yield "99681" + randomPart; // 99681 вместо 99680
                }
                case CONTAINS_LETTERS -> "abcdefghijkl"; // 12 букв вместо цифр
                case EMPTY -> ""; // пустая строка
            };
        }
    }
    
    /**
     * Вложенный класс для генерации ID'шников
     * 
     * ✅ FIX: Исправлена логика - теперь можно использовать конкретные известные ID
     */
    public static class Id {
        
        /**
         * Возвращает ID, который ТОЧНО существует в системе
         * (для тестов которые требуют реального ресурса)
         * 
         * @return ID существующего ресурса (обычно 1)
         */
        public static int valid() {
            return 1; // Профиль с ID 1 гарантированно существует
        }
        
        /**
         * Генерирует большой ID который гарантированно не существует в системе
         * Используется для тестирования 404 ошибок
         * 
         * @return очень большое число (9-10 миллионов)
         */
        public static int nonExistent() {
            return ThreadLocalRandom.current().nextInt(9_000_000, 10_000_001);
        }
        
        /**
         * Генерирует отрицательный ID для негативных тестов
         * 
         * @return отрицательное число
         */
        public static int negative() {
            return -ThreadLocalRandom.current().nextInt(1, 1001);
        }
    }
    
    /**
     * Вложенный класс для генерации ProfileDto объектов
     */
    public static class Profile {
        
        /**
         * Создает валидный ProfileDto с случайными данными
         * 
         * @return готовый к использованию ProfileDto
         */
        public static ProfileDto valid() {
            return ProfileDto.builder()
                .msisdn(Msisdn.valid())
                .userId(1) // Используем фиксированный user ID для стабильности тестов
                .pricePlanId(ThreadLocalRandom.current().nextInt(1, 6)) // Price plan от 1 до 5
                .build();
        }
        
        /**
         * Создает ProfileDto с конкретными параметрами
         * 
         * @param userId конкретный ID пользователя
         * @param pricePlanId конкретный ID тарифного плана
         * @return ProfileDto с заданными параметрами
         */
        public static ProfileDto withParams(int userId, int pricePlanId) {
            return ProfileDto.builder()
                .msisdn(Msisdn.valid())
                .userId(userId)
                .pricePlanId(pricePlanId)
                .build();
        }
        
        /**
         * Создает ProfileDto с невалидным MSISDN
         * Полезно для негативных тестов
         * 
         * @param invalidType тип невалидного MSISDN
         * @return ProfileDto с невалидными данными
         */
        public static ProfileDto withInvalidMsisdn(MsisdnInvalidType invalidType) {
            return ProfileDto.builder()
                .msisdn(Msisdn.invalid(invalidType))
                .userId(1)
                .pricePlanId(4)
                .build();
        }
    }
    
    /**
     * Вложенный класс для генерации BalanceDto объектов
     */
    public static class Balance {
        
        /**
         * Создает валидный BalanceDto со случайной суммой
         * 
         * @return готовый к использованию BalanceDto
         */
        public static BalanceDto valid() {
            return BalanceDto.createValidRandom();
        }
        
        /**
         * Создает BalanceDto с конкретной суммой
         * 
         * @param amount конкретная сумма
         * @return BalanceDto с заданной суммой
         */
        public static BalanceDto withAmount(double amount) {
            return BalanceDto.builder()
                .amount(amount)
                .build();
        }
        
        /**
         * Создает BalanceDto с отрицательной суммой для негативных тестов
         * 
         * @return BalanceDto с невалидными данными
         */
        public static BalanceDto negative() {
            double negativeAmount = -ThreadLocalRandom.current().nextDouble(1.0, 1000.0);
            return BalanceDto.createInvalidWithNegativeAmount(negativeAmount);
        }
    }
    
    /**
     * ✅ FIX: Новый класс для генерации CounterDto объектов
     * 
     * Вложенный класс для работы со счётчиками
     */
    public static class Counter {
        
        /**
         * Возвращает ID счётчика, который ТОЧНО существует
         * 
         * @return ID существующего счётчика (обычно 1)
         */
        public static int validId() {
            return 1; // Счётчик с ID 1 гарантированно существует
        }
        
        /**
         * Создает валидный CounterDto со значениями по умолчанию
         * 
         * @return готовый к использованию CounterDto
         */
        public static CounterDto valid() {
            return CounterDto.builder()
                .id(1)
                .state(true)
                .megabyteCount(1000)
                .secondsCount(3600)
                .smsCount(100)
                .startDate("2025-10-01T00:00:00")
                .endDate("2025-10-31T23:59:59")
                .build();
        }
        
        /**
         * Создает CounterDto с конкретными параметрами
         * 
         * @param id ID счётчика
         * @param state активен ли счётчик
         * @return CounterDto с заданными параметрами
         */
        public static CounterDto withParams(int id, boolean state) {
            return CounterDto.builder()
                .id(id)
                .state(state)
                .megabyteCount(1000)
                .secondsCount(3600)
                .smsCount(100)
                .startDate("2025-10-01T00:00:00")
                .endDate("2025-10-31T23:59:59")
                .build();
        }
        
        /**
         * Создает CounterDto с конкретными счётчиками использования
         * 
         * @param megabyteCount MB использовано
         * @param secondsCount секунд использовано
         * @param smsCount SMS использовано
         * @return CounterDto с заданными значениями использования
         */
        public static CounterDto withUsage(int megabyteCount, int secondsCount, int smsCount) {
            return CounterDto.builder()
                .id(1)
                .state(true)
                .megabyteCount(megabyteCount)
                .secondsCount(secondsCount)
                .smsCount(smsCount)
                .startDate("2025-10-01T00:00:00")
                .endDate("2025-10-31T23:59:59")
                .build();
        }
    }
}