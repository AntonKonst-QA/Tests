package iteration2;

import models.GenerateTransferRequest;
import models.GenerateTransferResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import steps.AccountSteps;

import static org.assertj.core.api.AssertionsForClassTypes.within;

public class TransferMoneyFromOneAccountToAnotherTest extends BaseTest{
    private final AccountSteps accountSteps = new AccountSteps();
    // Перевод на свой аккаунт
    @Test
    public void successTransferMoneyBetweenMyAccountTest() {
        int senderId = 1;
        int receiverId = 2;
        double amount = 1.1;

        // Проверяем баланс (GET) до перевода
        double senderBefore = accountSteps.getBalance(senderId);
        double receiverBefore = accountSteps.getBalance(receiverId);

        GenerateTransferRequest body = GenerateTransferRequest.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();

        GenerateTransferResponse response = accountSteps.transfer(body);

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

    // Перевод на чужой аккаунт
    @Test
    public void successTransferMoneyToAnotherTest(){
        int senderId = 1;
        int receiverId = 3;
        double amount = 0.65;

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

        @Test
        public void lessThanPermissibleAmountTest() {
            int senderId = 1;
            int receiverId = 2;
            double amount = 9999.99;

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

        @Test
        public void moreThanZeroTest() {
            int senderId = 1;
            int receiverId = 2;
            double amount = 0.01;

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
                    .isEqualTo(senderBefore - amount, within(0.001));

            softly.assertThat(accountSteps.getBalance(receiverId))
                    .as("Зачисление средств у получателя")
                    .isEqualTo(receiverBefore + amount, within(0.001));
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
