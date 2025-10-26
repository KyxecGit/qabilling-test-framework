package qabilling.core.dto;

import java.util.Objects;

/**
 * 📊 DTO для работы со счётчиками абонентов
 * 
 * ✅ FIX: Добавлены equals() и hashCode() для правильного сравнения объектов
 */
public class CounterDto extends BaseDto {
    
    /** ID счётчика */
    private final int id;
    
    /** Активен ли счётчик (true = активен, false = неактивен) */
    private final boolean state;
    
    /** Количество мегабайт использованных */
    private final int megabyteCount;
    
    /** Количество секунд использованных */
    private final int secondsCount;
    
    /** Количество SMS использованных */
    private final int smsCount;
    
    /** Дата начала счётчика в формате ISO-8601 */
    private final String startDate;
    
    /** Дата окончания счётчика в формате ISO-8601 */
    private final String endDate;
    
    /**
     * Конструктор для создания CounterDto
     * 
     * @param id идентификатор счётчика
     * @param state активен ли счётчик
     * @param megabyteCount количество MB
     * @param secondsCount количество секунд
     * @param smsCount количество SMS
     * @param startDate дата начала
     * @param endDate дата окончания
     */
    public CounterDto(int id, boolean state, int megabyteCount, 
                      int secondsCount, int smsCount, String startDate, String endDate) {
        this.id = id;
        this.state = state;
        this.megabyteCount = megabyteCount;
        this.secondsCount = secondsCount;
        this.smsCount = smsCount;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    /**
     * Создает Builder для удобного создания CounterDto
     * 
     * @return новый Builder
     */
    public static Builder builder() {
        return new Builder();
    }
    
    /**
     * Builder класс для создания CounterDto
     */
    public static class Builder {
        private int id;
        private boolean state;
        private int megabyteCount;
        private int secondsCount;
        private int smsCount;
        private String startDate;
        private String endDate;
        
        public Builder id(int id) {
            this.id = id;
            return this;
        }
        
        public Builder state(boolean state) {
            this.state = state;
            return this;
        }
        
        public Builder megabyteCount(int megabyteCount) {
            this.megabyteCount = megabyteCount;
            return this;
        }
        
        public Builder secondsCount(int secondsCount) {
            this.secondsCount = secondsCount;
            return this;
        }
        
        public Builder smsCount(int smsCount) {
            this.smsCount = smsCount;
            return this;
        }
        
        public Builder startDate(String startDate) {
            this.startDate = startDate;
            return this;
        }
        
        public Builder endDate(String endDate) {
            this.endDate = endDate;
            return this;
        }
        
        public CounterDto build() {
            return new CounterDto(id, state, megabyteCount, secondsCount, smsCount, startDate, endDate);
        }
    }
    
    @Override
    public String toJson() {
        return String.format(
            "{\"id\": %d, \"state\": %b, \"megabyteCount\": %d, \"secondsCount\": %d, \"smsCount\": %d, \"startDate\": \"%s\", \"endDate\": \"%s\"}",
            id, state, megabyteCount, secondsCount, smsCount, startDate, endDate
        );
    }
    
    // ✅ Геттеры для доступа к полям
    public int getId() { return id; }
    public boolean isState() { return state; }
    public int getMegabyteCount() { return megabyteCount; }
    public int getSecondsCount() { return secondsCount; }
    public int getSmsCount() { return smsCount; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    
    // ✅ equals() для сравнения Counter объектов
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CounterDto)) return false;
        CounterDto that = (CounterDto) o;
        return id == that.id &&
               state == that.state &&
               megabyteCount == that.megabyteCount &&
               secondsCount == that.secondsCount &&
               smsCount == that.smsCount &&
               Objects.equals(startDate, that.startDate) &&
               Objects.equals(endDate, that.endDate);
    }
    
    // ✅ hashCode() для использования в HashSet/HashMap
    @Override
    public int hashCode() {
        return Objects.hash(id, state, megabyteCount, secondsCount, smsCount, startDate, endDate);
    }
}
