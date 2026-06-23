package iteration2.api;

import api.models.CreateUserResponse;
import api.models.CustomerModel;
import api.models.GenerateDepositRequest;
import api.models.GenerateTransferRequest;
import api.steps.AccountSteps;
import api.steps.UserSteps;
import common.storage.SessionStorage;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static api.TestConstants.*;

public class TransferMoneyFromOneAccountToAnotherTest extends BaseTest{

    private AccountSteps accountSteps;
    private UserSteps userSteps;
    private AccountSteps alienAccountSteps;

    private int primaryAccountId;
    private int secondaryAccountId;
    private int alienAccountId;

    @BeforeEach
    public void prepareData() {
        var userRequest = api.generators.RandomModelGenerator.generate(api.models.CreateUserRequest.class);
        String uniqueHash1 = java.util.UUID.randomUUID().toString().substring(0, 6);
        userRequest.setUsername("u_" + uniqueHash1);
        userRequest.setPassword(userRequest.getPassword().replace("^", "").replace("$", "") + "1aA!");
        userRequest.setRole("USER");

        new api.requests.skeleton.requesters.ValidatedCrudRequester<api.models.CreateUserRequest, CreateUserResponse>(
                api.specs.RequestSpecs.adminSpec(),
                api.requests.skeleton.Endpoint.ADMIN_USER,
                api.specs.ResponseSpecs.entityWasCreated()
        ).post(userRequest);

        var alienRequest = api.generators.RandomModelGenerator.generate(api.models.CreateUserRequest.class);
        String uniqueHash2 = java.util.UUID.randomUUID().toString().substring(0, 6);
        alienRequest.setUsername("a_" + uniqueHash2);
        alienRequest.setPassword(alienRequest.getPassword().replace("^", "").replace("$", "") + "1aA!");
        alienRequest.setRole("USER");

        new api.requests.skeleton.requesters.ValidatedCrudRequester<api.models.CreateUserRequest, CreateUserResponse>(
                api.specs.RequestSpecs.adminSpec(),
                api.requests.skeleton.Endpoint.ADMIN_USER,
                api.specs.ResponseSpecs.entityWasCreated()
        ).post(alienRequest);

        CustomerModel dynamicUser = CustomerModel.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .build();

        SessionStorage.addUsers(List.of(dynamicUser));
        this.accountSteps = SessionStorage.getAccountSteps();
        this.userSteps = SessionStorage.getUserSteps();

        primaryAccountId = accountSteps.createAccount().getId();
        secondaryAccountId = accountSteps.createAccount().getId();

        alienAccountSteps = new AccountSteps(alienRequest.getUsername(), alienRequest.getPassword());
        alienAccountId = alienAccountSteps.createAccount().getId();

        for (int i = 0; i < 3; i++) {
            accountSteps.deposit(GenerateDepositRequest.builder()
                    .id(primaryAccountId)
                    .balance(new BigDecimal("5000.00"))
                    .build());
        }
    }

    @ParameterizedTest(name = "Перевод со своего аккаунта на аккаунт {0}, сумма перевода: {1}")
    @CsvSource ({
            "OWN, 1.1",
            "ALIEN, 0.65",
            "OWN, 9999.99",
            "OWN, 0.01",
    })
    void successTransferTest(String receiverMarker, BigDecimal amount) {
        BigDecimal currentBalance = accountSteps.getBalance(primaryAccountId);
        if (currentBalance.compareTo(new BigDecimal("10000.00")) < 0) {
            accountSteps.deposit(GenerateDepositRequest.builder()
                    .id(primaryAccountId)
                    .balance(new BigDecimal("5000.00"))
                    .build());
        }

        int receiverId = receiverMarker.equals("OWN") ? secondaryAccountId : alienAccountId;

        BigDecimal senderBefore = accountSteps.getBalance(primaryAccountId);
        BigDecimal receiverBefore = receiverMarker.equals("OWN")
                ? accountSteps.getBalance(receiverId)
                : alienAccountSteps.getBalance(receiverId);

        var body = GenerateTransferRequest.builder()
                .senderAccountId(primaryAccountId)
                .receiverAccountId(receiverId)
                .amount(amount)
                .build();

        var response = accountSteps.transfer(body);

        softly.assertThat(response.getMessage())
                .as("Сообщение об успешном переводе")
                .isEqualTo(SUCCESS_TRANSFER_MESSAGE);

        softly.assertThat(accountSteps.getBalance(primaryAccountId))
                .as("Списание средств у отправителя")
                .isCloseTo(senderBefore.subtract(amount), Offset.offset(BigDecimal.ONE.movePointLeft(2)));

        BigDecimal receiverAfter = receiverMarker.equals("OWN")
                ? accountSteps.getBalance(receiverId)
                : alienAccountSteps.getBalance(receiverId);

        softly.assertThat(receiverAfter)
                .as("Зачисление средств у получателя")
                .isCloseTo(receiverBefore.add(amount), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }

    @Nested
    class LimitValuesTransferTests {
        @Test
        public void moreThanPermissibleAmountTest() {
            BigDecimal senderBefore = accountSteps.getBalance(primaryAccountId);
            BigDecimal receiverBefore = alienAccountSteps.getBalance(alienAccountId);

            var body = GenerateTransferRequest.builder()
                    .senderAccountId(primaryAccountId)
                    .receiverAccountId(alienAccountId)
                    .amount(NON_VALID_TRANSFER_AMOUNT)
                    .build();

            String errorMessage = accountSteps.transferExpectingError(body);

            softly.assertThat(errorMessage)
                    .as("Сообщение о неуспешном переводе")
                    .isEqualTo(TRANSFER_AMOUNT_CANNOT_EXCEED_10000);

            softly.assertThat(accountSteps.getBalance(primaryAccountId))
                    .as("Баланс отправителя не должен измениться")
                    .isCloseTo(senderBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));

            softly.assertThat(alienAccountSteps.getBalance(alienAccountId))
                    .as("Баланс получателя не должен измениться")
                    .isCloseTo(receiverBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
        }
    }

    @Nested
    class NegativeTests {
        private static Stream<Arguments> provideInvalidTransfers() {
            return Stream.of(
                    Arguments.of(new BigDecimal("0.0"), TRANSFER_AMOUNT_MUST_BE_AT_LEAST_0_01),
                    Arguments.of(new BigDecimal("10000.1"), TRANSFER_AMOUNT_CANNOT_EXCEED_10000),
                    Arguments.of(new BigDecimal("5001.0"), INVALID_TRANSFER_MESSAGE),
                    Arguments.of(new BigDecimal("-0.1"), TRANSFER_AMOUNT_MUST_BE_AT_LEAST_0_01)
            );
        }

        @ParameterizedTest(name = "Проверка перевода с некорректной суммой: {0}")
        @MethodSource("provideInvalidTransfers")
        public void failedTransferMoneyTest(BigDecimal invalidDeposit, String expectedError) {
            BigDecimal senderBefore = accountSteps.getBalance(primaryAccountId);
            BigDecimal receiverBefore = alienAccountSteps.getBalance(alienAccountId);

            GenerateTransferRequest body = GenerateTransferRequest.builder()
                    .senderAccountId(primaryAccountId)
                    .receiverAccountId(alienAccountId)
                    .amount(invalidDeposit)
                    .build();

            String errorMessage = accountSteps.transferExpectingError(body);

            softly.assertThat(errorMessage)
                    .as("Сообщение о неуспешном переводе")
                    .contains(expectedError);

            softly.assertThat(accountSteps.getBalance(primaryAccountId))
                    .as("Сумма у отправителя не изменилась")
                    .isCloseTo(senderBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));

            softly.assertThat(alienAccountSteps.getBalance(alienAccountId))
                    .as("Сумма у получателя не изменилась")
                    .isCloseTo(receiverBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
        }
    }
}
