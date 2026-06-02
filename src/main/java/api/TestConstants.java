package api;

import java.math.BigDecimal;

public class TestConstants {
    private TestConstants() {}

    // Переменные для првоерки тестов по депозитам
    public static final BigDecimal VALID_DEPOSIT = new BigDecimal("5000");
    public static final int ID = 1;
    public static final int NON_EXISTENT_ID = 0;
    public static final  int ALIEN_ID = 3;
    public static final BigDecimal TOO_MUCH = new BigDecimal("5000.01");
    public static final BigDecimal MAX_VALID_DEPOSIT_AMOUNT = new BigDecimal("4999.99");
    public static final BigDecimal MIN_VALID_DEPOSIT_AMOUNT = new BigDecimal("0.01");
    // Переменные для проверки перевода денег
    public static final int SENDER_ID = 1;
    public static final int RECEIVER_ID = 2;
    public static final BigDecimal NON_VALID_TRANSFER_AMOUNT = new BigDecimal("10000.1");
    // Ожидаемые сообщения от бэка
    public static final String SUCCESS_TRANSFER_MESSAGE = "Transfer successful";
    public static final String INVALID_TRANSFER_MESSAGE = "Invalid transfer";
    public static final String UPDATED_PROFILE_MESSAGE = "Profile updated successfully";
    public static final String INVALID_NAME_MESSAGE = "Name must contain two words with letters only";
    public static final String TRANSFER_AMOUNT_CANNOT_EXCEED_10000 = ("Transfer amount cannot exceed 10000");
    public static final String TRANSFER_AMOUNT_MUST_BE_AT_LEAST_0_01 = ("Transfer amount must be at least 0.01");
}
