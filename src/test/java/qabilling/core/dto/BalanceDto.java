package qabilling.core.dto;

import java.util.Locale;
import java.util.Objects;

/**
 * 💰 DTO для работы с балансами абонентов
 * 
 * ✅ FIX: Добавлены equals() и hashCode() для правильного сравнения объектов
 */
public class BalanceDto extends BaseDto {
    
    /** Сумма баланса (должна быть положительной) */
    private final double amount;
    
    /**
     * Конструктор для создания BalanceDto
     * 
     * @param amount сумма баланса (должна быть больше 0)
     */
    public BalanceDto(double amount) {
        this.amount = amount;
    }
    
    /**
     * Создает Builder для удобного создания BalanceDto
     * 
     * @return новый Builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder класс для создания BalanceDto
     */
    public static class Builder {
        private double amount;
        
        /** Устанавливает сумму баланса */
        public Builder amount(double amount) {
            this.amount = amount;
            return this;
        }
        
        /** Создает BalanceDto с установленной суммой */
        public BalanceDto build() {
            return new BalanceDto(amount);
        }
    }
    
    @Override
    public String toJson() {
        // Используем US локаль для правильного формата чисел (с точкой, а не запятой)
        return String.format(Locale.US, "{\"amount\": %.2f}", amount);
    }
    
    /** Геттер для суммы */
    public double getAmount() { 
        return amount; 
    }
    
    /**
     * Фабричный метод для создания невалидного DTO с отрицательной суммой
     * Полезен для негативных тестов
     * 
     * @param negativeAmount отрицательная сумма
     * @return BalanceDto с невалидными данными
     */
    public static BalanceDto createInvalidWithNegativeAmount(double negativeAmount) {
        return new BalanceDto(negativeAmount);
    }
    
    /**
     * Фабричный метод для создания валидного DTO со случайной суммой
     * Полезен для позитивных тестов
     * 
     * @return BalanceDto с валидной случайной суммой от 10.00 до 1000.00
     */
    public static BalanceDto createValidRandom() {
        double randomAmount = 10.0 + (Math.random() * 990.0);
        return new BalanceDto(Math.round(randomAmount * 100.0) / 100.0); // Округляем до 2 знаков
    }
    
    // ✅ equals() для сравнения Balance объектов
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BalanceDto)) return false;
        BalanceDto that = (BalanceDto) o;
        // Сравниваем суммы с точностью до 2 знаков (для работы с double)
        return Math.abs(amount - that.amount) < 0.01;
    }
    
    // ✅ hashCode() для использования в HashSet/HashMap
    @Override
    public int hashCode() {
        // Преобразуем double в long для хеша
        return Objects.hash(Math.round(amount * 100.0));
    }
}
