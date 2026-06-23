package iteration2.api;

import api.models.CreateUserResponse;
import api.models.CustomerModel;
import api.models.GenerateDepositRequest;
import api.steps.AccountSteps;
import api.steps.UserSteps;
import common.storage.SessionStorage;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import api.specs.ResponseSpecs;

import java.math.BigDecimal;
import java.util.List;

import static api.TestConstants.*;

public class UsersDepositTest extends BaseTest {

    private AccountSteps accountSteps;
    private UserSteps userSteps;
    private AccountSteps alienAccountSteps;

    private int dynamicUserId;
    private int dynamicAlienId;

    @BeforeEach
    public void prepareData() {
        api.models.CreateUserRequest userRequest = api.generators.RandomModelGenerator.generate(api.models.CreateUserRequest.class);

        String uniqueHash1 = java.util.UUID.randomUUID().toString().substring(0, 6);
        userRequest.setUsername("u_" + uniqueHash1);

        String cleanPassword1 = userRequest.getPassword().replace("^", "").replace("$", "");
        userRequest.setPassword(cleanPassword1 + "1aA!");
        userRequest.setRole("USER");

        new api.requests.skeleton.requesters.ValidatedCrudRequester<api.models.CreateUserRequest, CreateUserResponse>(
                api.specs.RequestSpecs.adminSpec(),
                api.requests.skeleton.Endpoint.ADMIN_USER,
                api.specs.ResponseSpecs.entityWasCreated()
        ).post(userRequest);

        api.models.CreateUserRequest alienRequest = api.generators.RandomModelGenerator.generate(api.models.CreateUserRequest.class);

        String uniqueHash2 = java.util.UUID.randomUUID().toString().substring(0, 6);
        alienRequest.setUsername("a_" + uniqueHash2);

        String cleanPassword2 = alienRequest.getPassword().replace("^", "").replace("$", "");
        alienRequest.setPassword(cleanPassword2 + "1aA!");
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

        dynamicUserId = accountSteps.createAccount().getId();

        alienAccountSteps = new AccountSteps(alienRequest.getUsername(), alienRequest.getPassword());
        dynamicAlienId = alienAccountSteps.createAccount().getId();
    }

    @Test
    public void successGenerateDepositTest() {
        BigDecimal balanceBefore = accountSteps.getBalance(dynamicUserId);

        GenerateDepositRequest body = GenerateDepositRequest.builder()
                .id(dynamicUserId)
                .balance(VALID_DEPOSIT)
                .build();

        accountSteps.deposit(body);

        softly.assertThat(accountSteps.getBalance(dynamicUserId))
                .as("Баланс должен увеличиться на " + VALID_DEPOSIT)
                .isCloseTo(balanceBefore.add(VALID_DEPOSIT), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }

    @Nested
    class NegativeTests {
        @ParameterizedTest
        @ValueSource(strings = {
                "0.0",
                "5001.0",
                "-5000.0"
        })
        public void failedGenerateDepositTest(BigDecimal invalidDeposit) {
            BigDecimal balanceBefore = accountSteps.getBalance(dynamicUserId);

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(dynamicUserId)
                    .balance(invalidDeposit)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.badRequestResponse());

            softly.assertThat(accountSteps.getBalance(dynamicUserId))
                    .as("Баланс не должен измениться")
                    .isCloseTo(balanceBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
        }
    }

    @Nested
    class DepositToNonAccountOrSomeOneTests {

        @Test
        public void depositToNonAccountTest() {
            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(NON_EXISTENT_ID)
                    .balance(VALID_DEPOSIT)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.forbiddenResponse());
        }

        @Test
        public void depositToSomeOneElseAccountTest() {
            BigDecimal balanceBefore = alienAccountSteps.getBalance(dynamicAlienId);

            GenerateDepositRequest body = GenerateDepositRequest.builder()
                    .id(dynamicAlienId)
                    .balance(VALID_DEPOSIT)
                    .build();

            accountSteps.depositExpectingError(body, ResponseSpecs.forbiddenResponse());

            softly.assertThat(alienAccountSteps.getBalance(dynamicAlienId))
                    .as("Баланс чужого аккаунта не должен измениться")
                    .isCloseTo(balanceBefore, org.assertj.core.data.Offset.offset(BigDecimal.ONE.movePointLeft(2)));
        }

        @Nested
        class LimitValuesDepositTests {

            @Test
            public void moreThanPermissibleAmountTest() {
                BigDecimal balanceBefore = accountSteps.getBalance(dynamicUserId);

                GenerateDepositRequest body = GenerateDepositRequest.builder()
                        .id(dynamicUserId)
                        .balance(TOO_MUCH)
                        .build();

                accountSteps.depositExpectingError(body, ResponseSpecs.badRequestResponse());

                softly.assertThat(accountSteps.getBalance(dynamicUserId))
                        .as("Баланс не должен измениться")
                        .isCloseTo(balanceBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
            }

            @Test
            public void maxAmountValidTest() {
                BigDecimal balanceBefore = accountSteps.getBalance(dynamicUserId);

                accountSteps.deposit(GenerateDepositRequest.builder().id(dynamicUserId).balance(MAX_VALID_DEPOSIT_AMOUNT).build());

                softly.assertThat(accountSteps.getBalance(dynamicUserId))
                        .as("Баланс увеличится на сумму " + MAX_VALID_DEPOSIT_AMOUNT)
                        .isCloseTo(balanceBefore.add(MAX_VALID_DEPOSIT_AMOUNT), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
            }

            @Test
            public void moreThanZeroTest() {
                BigDecimal balanceBefore = accountSteps.getBalance(dynamicUserId);

                accountSteps.deposit(GenerateDepositRequest.builder().id(dynamicUserId).balance(MIN_VALID_DEPOSIT_AMOUNT).build());

                softly.assertThat(accountSteps.getBalance(dynamicUserId))
                        .as("Баланс увеличится на сумму " + MIN_VALID_DEPOSIT_AMOUNT)
                        .isCloseTo(balanceBefore.add(MIN_VALID_DEPOSIT_AMOUNT), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
            }
        }
    }
}