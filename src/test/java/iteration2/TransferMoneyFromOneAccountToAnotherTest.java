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

        GenerateTransferRequest body = GenerateTransferRequest.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();

        UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );

        GenerateTransferResponse response = transferAction.post(body)
                .extract()
                .as(GenerateTransferResponse.class);

        softly.assertThat(response.getMessage())
                .as("Проверка сообщения об успешном переводе")
                .isEqualTo("Transfer successful");

        // Проверяем результаты (GET)
        transferAction.get(body);
    }

    // Перевод на чужой аккаунт
    @Test
    public void successTransferMoneyToAnotherTest(){
        int senderId = 1;
        int receiverId = 3;
        double amount = 0.65;

        GenerateTransferRequest body = GenerateTransferRequest.builder()
                .senderAccountId(senderId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();

        UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                RequestSpecs.authUser(),
                ResponseSpecs.successResponse()
        );

        GenerateTransferResponse response = transferAction.post(body)
                .extract()
                .as(GenerateTransferResponse.class);

        softly.assertThat(response.getMessage())
                .as("Проверка сообщения об успешном переводе")
                .isEqualTo("Transfer successful");

        // Проверяем результаты (GET)
        transferAction.get(body);
    }

    // Перевод больше допустимого лимита
    @Nested
    class LimitValuesTransferTests {
        @Test
        public void moreThanPermissibleAmountTest() {
            int senderId = 1;
            int receiverId = 2;
            double amount = 10000.1;

            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(senderId)
                    .receiverAccountId(receiverId)
                    .amount(amount)
                    .build();

                    UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );

            String response = transferAction.post(body)
                    .extract()
                    .asString();

            softly.assertThat(response)
                    .as("Проверка сообщения о неуспешном переводе")
                    .contains("Invalid transfer");


            // Проверяем результаты (GET)
            new UserGenerateTransferRequester(RequestSpecs.authUser(), ResponseSpecs.successResponse())
                    .get(body);
        }

        @Test
        public void lessThanPermissibleAmountTest() {
            int senderId = 1;
            int receiverId = 2;
            double amount = 9999.99;

            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(senderId)
                    .receiverAccountId(receiverId)
                    .amount(amount)
                    .build();

            UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );

            GenerateTransferResponse response = transferAction.post(body)
                    .extract()
                    .as(GenerateTransferResponse.class);

            softly.assertThat(response.getMessage())
                    .as("Проверка сообщения об успешном переводе")
                    .isEqualTo("Transfer successful");

            // Проверяем результаты (GET)
            transferAction.get(body);
        }

        @Test
        public void moreThanZeroTest() {
            int senderId = 1;
            int receiverId = 2;
            double amount = 0.01;

            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(senderId)
                    .receiverAccountId(receiverId)
                    .amount(amount)
                    .build();

            UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.successResponse()
            );

            GenerateTransferResponse response = transferAction.post(body)
                    .extract()
                    .as(GenerateTransferResponse.class);

            softly.assertThat(response.getMessage())
                    .as("Проверка сообщения об успешном переводе")
                    .isEqualTo("Transfer successful");

            // Проверяем результаты (GET)
            transferAction.get(body);
        }
    }

    @Nested
    class NegativeTests {
        @ParameterizedTest
        @ValueSource(doubles = {
                0.0, // Нет суммы на депозите
                10000.1, // Превышение максимальную сумму перевода
                5001.0, // Превышение максимального баланс на депозите
                -0.1 // Отрицательная сумма не депозите
        })

        public  void failedTransferMoneyTest(double invalidDeposit) {
            int senderId = 1;
            int receiverId = 2;

            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(senderId)
                    .receiverAccountId(receiverId)
                    .amount(invalidDeposit)
                    .build();

            UserGenerateTransferRequester transferAction = new UserGenerateTransferRequester(
                    RequestSpecs.authUser(),
                    ResponseSpecs.badRequestResponse()
            );

            String response = transferAction.post(body)
                    .extract()
                    .asString();

            softly.assertThat(response)
                    .as("Проверка текста ошибки при не успешном переоводе")
                    .contains("Invalid transfer");
            // Проверяем результаты (GET)
            new UserGenerateTransferRequester(RequestSpecs.authUser(), ResponseSpecs.successResponse())
                    .get(body);
        }
    }
}
