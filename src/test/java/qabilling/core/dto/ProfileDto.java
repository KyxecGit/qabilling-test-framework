package qabilling.core.dto;

import java.util.Objects;

/**
 * 👤 DTO для работы с профилями абонентов
 * 
 * ✅ FIX: Добавлены equals() и hashCode() для правильного сравнения объектов
 */
public class ProfileDto extends BaseDto {
    
    private final String msisdn;
    private final int userId;
    
    /** ID тарифного плана */
    private final int pricePlanId;
    
    /**
     * Конструктор для создания ProfileDto
     * 
     * @param msisdn номер телефона абонента (должен соответствовать паттерну ^99680\d{7}$)
     * @param userId идентификатор пользователя (должен быть > 0)
     * @param pricePlanId идентификатор тарифного плана (должен быть > 0)
     */
    public ProfileDto(String msisdn, int userId, int pricePlanId) {
        this.msisdn = msisdn;
        this.userId = userId;
        this.pricePlanId = pricePlanId;
    }
    
    /**
     * Создает Builder для удобного создания ProfileDto
     * 
     * Пример использования:
     * ProfileDto profile = ProfileDto.builder()
     *     .msisdn("99680123456")
     *     .userId(1)
     *     .pricePlanId(4)
     *     .build();
     * 
     * @return новый Builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder класс для создания ProfileDto
     * 
     * Builder Pattern - это паттерн проектирования, который позволяет 
     * создавать объекты пошагово. Очень удобен для объектов с множеством параметров.
     */
    public static class Builder {
        private String msisdn;
        private int userId;
        private int pricePlanId;
        
        /** Устанавливает MSISDN */
        public Builder msisdn(String msisdn) {
            this.msisdn = msisdn;
            return this;
        }
        
        /** Устанавливает User ID */
        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }
        
        /** Устанавливает Price Plan ID */
        public Builder pricePlanId(int pricePlanId) {
            this.pricePlanId = pricePlanId;
            return this;
        }
        
        /** Создает ProfileDto с установленными параметрами */
        public ProfileDto build() {
            return new ProfileDto(msisdn, userId, pricePlanId);
        }
    }
    
    @Override
    public String toJson() {
        return String.format("{\"msisdn\": \"%s\", \"userId\": %d, \"pricePlanId\": %d}",
            msisdn, userId, pricePlanId);
    }
    
    // ✅ Геттеры для доступа к полям (нужны для тестов и отладки)
    public String getMsisdn() { return msisdn; }
    public int getUserId() { return userId; }
    public int getPricePlanId() { return pricePlanId; }
    
    // ✅ equals() для сравнения Profile объектов
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileDto)) return false;
        ProfileDto that = (ProfileDto) o;
        return userId == that.userId &&
               pricePlanId == that.pricePlanId &&
               Objects.equals(msisdn, that.msisdn);
    }
    
    // ✅ hashCode() для использования в HashSet/HashMap
    @Override
    public int hashCode() {
        return Objects.hash(msisdn, userId, pricePlanId);
    }
}
