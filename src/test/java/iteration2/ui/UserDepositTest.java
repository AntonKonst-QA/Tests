package iteration2.ui;

import api.generators.CreateUserInAPIForUi;
import api.steps.AccountSteps;
import api.steps.UserSteps;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.pages.BankAlerts;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import java.math.BigDecimal;

public class UserDepositTest extends BaseUiTest {

    private AccountSteps accountSteps;
    private UserSteps userSteps;
    private CreateUserInAPIForUi.UserData userData;

    @BeforeEach
    public void prepareUser() {
        super.setupTest();

        this.userData = CreateUserInAPIForUi.createReadyToUseUser();
        BaseUiTest.usersToDelete.add(userData.response.getId());

        this.accountSteps = new AccountSteps(userData.response.getUsername(), userData.rawPassword);
        this.userSteps = new UserSteps(userData.response.getUsername(), userData.rawPassword);

        login(userData.response.getUsername(), userData.rawPassword);
    }

    @Test
    public void userCanCreateDepositTest() {
        var accounts = accountSteps.getAccounts();
        if (accounts.isEmpty()) {
            throw new RuntimeException("У пользователя нет активных счетов!");
        }

        String myAccountNumber = accounts.get(0).getAccountNumber();
        int myAccountId = accounts.get(0).getId();

        BigDecimal balanceBefore = accountSteps.getBalance(myAccountId);

        new UserDashboard()
                .checkWelcomeText()
                .openDepositModal()
                .makeDeposit(myAccountNumber, DepositPage.VALID_DEPOSIT)
                .checkAlertMessageAndAccept(BankAlerts.SUCCESSFULLY_DEPOSITED, DepositPage.VALID_DEPOSIT, myAccountNumber);

        softly.assertThat(accountSteps.getBalance(myAccountId))
                .isCloseTo(balanceBefore.add(DepositPage.VALID_DEPOSIT), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }

    @Test
    public void userCanNotCreateDepositTest() {
        var accounts = accountSteps.getAccounts();
        String myAccountNumber = accounts.get(0).getAccountNumber();
        int myAccountId = accounts.get(0).getId();

        BigDecimal balanceBefore = accountSteps.getBalance(myAccountId);

        new UserDashboard()
                .checkWelcomeText()
                .openDepositModal()
                .makeDeposit(myAccountNumber, DepositPage.INVALID_DEPOSIT)
                .checkAlertMessageAndAccept(BankAlerts.PLEASE_ENTER_A_VALID_AMOUNT);

        softly.assertThat(accountSteps.getBalance(myAccountId))
                .as("Баланс не должен измениться")
                .isCloseTo(balanceBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }
}