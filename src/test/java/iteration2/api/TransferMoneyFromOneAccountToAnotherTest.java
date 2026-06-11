package iteration2.api;

import api.generators.RandomModelGenerator;
import api.models.BaseModel;
import api.models.GenerateTransferRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requesters.ValidatedCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.storage.SessionStorage;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static api.TestConstants.SENDER_ID;
import static api.TestConstants.SUCCESS_TRANSFER_MESSAGE;
import static api.TestConstants.*;

public class TransferMoneyFromOneAccountToAnotherTest extends BaseTest{

    @ParameterizedTest(name = "Перевод со своего аккаунта на аккаунт {0}, сумма перевода: {1}")
    @CsvSource ({
            "2, 1.1", // Кейс №1: перевод на свой аккаунт
            "3, 0.65", // Кейс №2: перевод на чужой аккаунт
            "2, 9999.99", // Кейс №3: перевод на свой аккаунт суммы, меньше максимальной
            "2, 0.01", // Кейс №4: перевод на свой аккаунт суммы, больше нуля
    })
    void successTransferTest(int receiverId, BigDecimal amount) {
        // Проверяем баланс (GET) до перевода
        BigDecimal senderBefore = accountSteps.getBalance(SENDER_ID);
        BigDecimal receiverBefore = accountSteps.getBalance(receiverId);

        var body = RandomModelGenerator.generate(GenerateTransferRequest.class);
        body.setSenderAccountId(SENDER_ID);
        body.setReceiverAccountId(receiverId);
        body.setAmount(amount);

        var response = accountSteps.transfer(body);

        softly.assertThat(response.getMessage())
                .as("Сообщение об успешном переводе")
                .isEqualTo(SUCCESS_TRANSFER_MESSAGE);

        softly.assertThat(accountSteps.getBalance(SENDER_ID))
                .as("Списание средств у отправителя")
                .isCloseTo(senderBefore.subtract(amount), Offset.offset(BigDecimal.ONE.movePointLeft(2)));

        softly.assertThat(accountSteps.getBalance(receiverId))
                .as("Зачисление средств у получателя")
                .isCloseTo(receiverBefore.add(amount), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }

    // Перевод больше допустимого лимита
    @Nested
    class LimitValuesTransferTests {
        @Test
        public void moreThanPermissibleAmountTest() {

            BigDecimal senderBefore = accountSteps.getBalance(SENDER_ID);
            BigDecimal receiverBefore = accountSteps.getBalance(RECEIVER_ID);

            var body = RandomModelGenerator.generate(GenerateTransferRequest.class);
            body.setSenderAccountId(SENDER_ID);
            body.setReceiverAccountId(RECEIVER_ID);
            body.setAmount(NON_VALID_TRANSFER_AMOUNT);

            new ValidatedCrudRequester<GenerateTransferRequest, BaseModel>(
                    RequestSpecs.authUser(SessionStorage.getUser().getUsername(), SessionStorage.getUser().getPassword()),
                    Endpoint.TRANSFER,
                    ResponseSpecs.badRequestResponse()
            ).getCrudRequester().post(body);

            BigDecimal senderAfter = accountSteps.getBalance(SENDER_ID);
            BigDecimal receiverAfter = accountSteps.getBalance(RECEIVER_ID);

            softly.assertThat(senderAfter).isEqualTo(senderBefore);
            softly.assertThat(receiverAfter).isEqualTo(receiverBefore);
        }
    }

    @Nested
    class NegativeTests {
        private static Stream<Arguments> provideInvalidTransfers() {
            return Stream.of(
                    Arguments.of(new BigDecimal("0.0"), TRANSFER_AMOUNT_MUST_BE_AT_LEAST_0_01), // Нет суммы на депозите
                    Arguments.of(new BigDecimal("10000.1"), TRANSFER_AMOUNT_CANNOT_EXCEED_10000), // Превышение максимальную сумму перевода
                    Arguments.of(new BigDecimal("5001.0"), INVALID_TRANSFER_MESSAGE), // Превышение максимального баланс на депозите
                    Arguments.of(new BigDecimal("-0.1"), TRANSFER_AMOUNT_MUST_BE_AT_LEAST_0_01) // Отрицательная сумма не депозите
            );
        }

        @ParameterizedTest(name = "Проверка перевода с некорректной суммойЖ {0}")
        @MethodSource("provideInvalidTransfers")
        public void failedTransferMoneyTest(BigDecimal invalidDeposit, String expectedError) {

            BigDecimal senderBefore = accountSteps.getBalance(SENDER_ID);
            BigDecimal receiverBefore = accountSteps.getBalance(RECEIVER_ID);

            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(SENDER_ID)
                    .receiverAccountId(RECEIVER_ID)
                    .amount(invalidDeposit)
                    .build();

            String errorMessage = accountSteps.transferExpectingError(body);

            softly.assertThat(errorMessage)
                    .as("Сообщение о неуспешном переводе")
                    .contains(expectedError);

            softly.assertThat(accountSteps.getBalance(SENDER_ID))
                    .as("Сумма у отправителя не изменилась")
                    .isCloseTo(senderBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));

            softly.assertThat(accountSteps.getBalance(RECEIVER_ID))
                    .as("Сумма у получателя не изменилась")
                    .isCloseTo(receiverBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
        }
    }
}
