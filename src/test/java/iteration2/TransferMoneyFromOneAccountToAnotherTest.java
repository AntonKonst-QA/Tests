package iteration2;

import models.GenerateTransferRequest;
import models.GenerateTransferResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import requests.UserGenerateTransferRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class TransferMoneyFromOneAccountToAnotherTest extends BaseTest{
    // Перевод на свой аккаунт
    @Test
    public void successTransferMoneyBetweenMyAccountTest() {
        int senderId = 1;
        int receiverId = 2;
        double amount = 220.1;

        UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );

        GenerateTransferRequest body = GenerateTransferRequest.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();

        // Проверяем баланс (GET) до перевода
        double senderBefore = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
        double receiverBefore = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

        GenerateTransferResponse response = transferAction.post(body)
                .extract()
                .as(GenerateTransferResponse.class);

        double senderAfter = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
        double receiverAfter = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

        softly.assertThat(response.getMessage())
                .as("Сообщение об успешном переводе")
                .isEqualTo("Transfer successful");

        softly.assertThat(senderAfter)
                .as("Списание средств у отправителя")
                .isEqualTo(senderBefore - amount);

        softly.assertThat(receiverAfter)
                .as("Зачисление средств у получателя")
                .isEqualTo(receiverBefore + amount);
    }

    // Перевод на чужой аккаунт
    @Test
    public void successTransferMoneyToAnotherTest(){
        int senderId = 1;
        int receiverId = 3;
        double amount = 0.65;

        UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );

        GenerateTransferRequest body = GenerateTransferRequest.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();

        // Проверяем баланс (GET) до перевода
        double senderBefore = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
        double receiverBefore = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

        GenerateTransferResponse response = transferAction.post(body)
                .extract()
                .as(GenerateTransferResponse.class);

        double senderAfter = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
        double receiverAfter = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

        softly.assertThat(response.getMessage())
                .as("Сообщение об успешном переводе")
                .isEqualTo("Transfer successful");

        softly.assertThat(senderAfter)
                .as("Списание средств у отправителя")
                .isEqualTo(senderBefore - amount);

        softly.assertThat(receiverAfter)
                .as("Зачисление средств у получателя")
                .isEqualTo(receiverBefore + amount);
    }

    // Перевод больше допустимого лимита
    @Nested
    class LimitValuesTransferTests {
        @Test
        public void moreThanPermissibleAmountTest() {
            int senderId = 1;
            int receiverId = 2;
            double amount = 10000.1;

            UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );

            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(senderId)
                    .receiverAccountId(receiverId)
                    .amount(amount)
                    .build();

            // Проверяем баланс (GET) до перевода
            double senderBefore = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
            double receiverBefore = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

            String response = transferAction.post(body)
                    .extract()
                    .asString();

            double senderAfter = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
            double receiverAfter = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

            softly.assertThat(response)
                    .as("Сообщение о неуспешном переводе")
                    .contains("Invalid transfer");

            softly.assertThat(senderAfter)
                    .as("Баланс отправителя не должен измениться")
                    .isEqualTo(senderBefore);

            softly.assertThat(receiverAfter)
                    .as("Баланс получателя не должен измениться")
                    .isEqualTo(receiverBefore);
        }

        @Test
        public void lessThanPermissibleAmountTest() {
            int senderId = 1;
            int receiverId = 2;
            double amount = 9999.99;

            UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );
            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(senderId)
                    .receiverAccountId(receiverId)
                    .amount(amount)
                    .build();

            // Проверяем баланс (GET) до перевода
            double senderBefore = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
            double receiverBefore = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

            GenerateTransferResponse response = transferAction.post(body)
                    .extract()
                    .as(GenerateTransferResponse.class);

            double senderAfter = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
            double receiverAfter = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

            softly.assertThat(response.getMessage())
                    .as("Сообщение об успешном переводе")
                    .isEqualTo("Transfer successful");

            softly.assertThat(senderAfter)
                    .as("Списание средств у отправителя")
                    .isEqualTo(senderBefore - amount);

            softly.assertThat(receiverAfter)
                    .as("Зачисление средств у получателя")
                    .isEqualTo(receiverBefore + amount);
        }

        @Test
        public void moreThanZeroTest() {
            int senderId = 1;
            int receiverId = 2;
            double amount = 0.01;

            UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );

            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(senderId)
                    .receiverAccountId(receiverId)
                    .amount(amount)
                    .build();

            // Проверяем баланс (GET) до перевода
            double senderBefore = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
            double receiverBefore = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

            GenerateTransferResponse response = transferAction.post(body)
                    .extract()
                    .as(GenerateTransferResponse.class);

            double senderAfter = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
            double receiverAfter = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

            softly.assertThat(response.getMessage())
                    .as("Сообщение об успешном переводе")
                    .isEqualTo("Transfer successful");

            softly.assertThat(senderAfter)
                    .as("Списание средств у отправителя")
                    .isEqualTo(senderBefore - amount);

            softly.assertThat(receiverAfter)
                    .as("Зачисление средств у получателя")
                    .isEqualTo(receiverBefore + amount);
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

            UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );

            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(senderId)
                    .receiverAccountId(receiverId)
                    .amount(invalidDeposit)
                    .build();

            // Проверяем баланс (GET) до перевода

            double senderBefore = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
            double receiverBefore = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

            String response = transferAction.post(body)
                    .extract()
                    .asString();

            double senderAfter = Math.round(transferAction.getAccount(senderId).getBalance() * 100.0) / 100.0;
            double receiverAfter = Math.round(transferAction.getAccount(receiverId).getBalance() * 100.0) / 100.0;

            softly.assertThat(response)
                    .as("Сообщение о неуспешном переводе")
                    .contains("Invalid transfer");

            softly.assertThat(senderAfter)
                    .as("Сумма у отправителя не изменилась")
                    .isEqualTo(senderBefore);

            softly.assertThat(receiverAfter)
                    .as("Сумма у получателя не изменилась")
                    .isEqualTo(receiverBefore);
        }
    }
}
