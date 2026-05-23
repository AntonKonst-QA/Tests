package iteration2.api;

import api.models.GenerateTransferRequest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import api.steps.AccountSteps;

import static org.assertj.core.api.AssertionsForClassTypes.within;

public class TransferMoneyFromOneAccountToAnotherTest extends BaseTest{
    private final AccountSteps accountSteps = new AccountSteps();
    @ParameterizedTest(name = "Перевод со своего аккаунта на аккаунт {0}, сумма перевода: {1}")
    @CsvSource ({
            "2, 1.1", // Кейс №1: перевод на свой аккаунт
            "3, 0.65", // Кейс №2: перевод на чужой аккаунт
            "2, 9999.99", // Кейс №3: перевод на свой аккаунт суммы, меньше максимальной
            "2, 0.01", // Кейс №4: перевод на свой аккаунт суммы, больше нуля
    })
    void successTransferTest(int receiverId, double amount) {
        int senderId = 1;
        // Проверяем баланс (GET) до перевода
        double senderBefore = accountSteps.getBalance(senderId);
        double receiverBefore = accountSteps.getBalance(receiverId);

        var body = GenerateTransferRequest.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();

        var response = accountSteps.transfer(body);

        softly.assertThat(response.getMessage())
                .as("Сообщение об успешном переводе")
                .isEqualTo("Transfer successful");

        softly.assertThat(accountSteps.getBalance(senderId))
                .as("Списание средств у отправителя")
                .isCloseTo(senderBefore - amount, within(0.001));

        softly.assertThat(accountSteps.getBalance(receiverId))
                .as("Зачисление средств у получателя")
                .isCloseTo(receiverBefore + amount, within(0.001));
    }

    // Перевод больше допустимого лимита
    @Nested
    class LimitValuesTransferTests {
        @Test
        public void moreThanPermissibleAmountTest() {
            int senderId = 1;
            int receiverId = 2;
            double amount = 10000.1;

            double senderBefore = accountSteps.getBalance(senderId);
            double receiverBefore = accountSteps.getBalance(receiverId);

            var body = GenerateTransferRequest.builder()
                    .senderAccountId(senderId)
                    .receiverAccountId(receiverId)
                    .amount(amount)
                    .build();
            String errorResponse = accountSteps.transferExpectingError(body);

            softly.assertThat(errorResponse)
                    .as("Сообщение о неуспешном переводе")
                    .contains("Invalid transfer");

            softly.assertThat(accountSteps.getBalance(senderId))
                    .as("Баланс отправителя не должен измениться")
                    .isEqualTo(senderBefore);

            softly.assertThat(accountSteps.getBalance(receiverId))
                    .as("Баланс получателя не должен измениться")
                    .isEqualTo(receiverBefore);
        }
    }

    @Nested
    class NegativeTests {
        @ParameterizedTest(name = "Проверка перевода с некорректной суммой: {0}")
        @ValueSource(doubles = {
                0.0, // Нет суммы на депозите
                10000.1, // Превышение максимальную сумму перевода
                5001.0, // Превышение максимального баланс на депозите
                -0.1 // Отрицательная сумма не депозите
        })

        public  void failedTransferMoneyTest(double invalidDeposit) {
            int senderId = 1;
            int receiverId = 2;

            double senderBefore = accountSteps.getBalance(senderId);
            double receiverBefore = accountSteps.getBalance(receiverId);

            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(senderId)
                    .receiverAccountId(receiverId)
                    .amount(invalidDeposit)
                    .build();

            String errorResponse = accountSteps.transferExpectingError(body);

            softly.assertThat(errorResponse)
                    .as("Сообщение о неуспешном переводе")
                    .contains("Invalid transfer");

            softly.assertThat(accountSteps.getBalance(senderId))
                    .as("Сумма у отправителя не изменилась")
                    .isEqualTo(senderBefore);

            softly.assertThat(accountSteps.getBalance(receiverId))
                    .as("Сумма у получателя не изменилась")
                    .isEqualTo(receiverBefore);
        }
    }
}
