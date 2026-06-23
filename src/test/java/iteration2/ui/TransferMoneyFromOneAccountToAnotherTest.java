package iteration2.ui;

import api.generators.CreateUserInAPIForUi;
import api.models.AccountModel;
import api.models.GenerateDepositRequest;
import api.steps.AdminSteps;
import common.annotations.UserSession;
import common.extensions.UserSessionExtension;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.pages.BankAlerts;
import ui.pages.TransferPage;
import ui.pages.UserDashboard;

import java.math.BigDecimal;

@ExtendWith(UserSessionExtension.class)
public class TransferMoneyFromOneAccountToAnotherTest extends BaseUiTest {

    private String senderAcc;
    private String receiverAcc;
    private int senderId;
    private BigDecimal senderBefore;

    @BeforeEach
    public void prepareData() {
        this.userData = CreateUserInAPIForUi.createReadyToUseUser();
        initSteps(userData.response.getUsername(), userData.rawPassword);
        AccountModel acc1 = accountSteps.createAccount();
        accountSteps.createAccount();
        accountSteps.createAccount();

        accountSteps.deposit(new GenerateDepositRequest(acc1.getId(), new BigDecimal("100")));

        var accounts = accountSteps.getAccounts();

        this.senderAcc = acc1.getAccountNumber();
        this.senderId = acc1.getId();

        this.receiverAcc = accounts.stream()
                .filter(a -> !a.getAccountNumber().equals(senderAcc))
                .findFirst()
                .orElseThrow()
                .getAccountNumber();

        login(userData.response.getUsername(), userData.rawPassword);
    }

    public void tearDown() {
        if (userData != null) {
            try {
                AdminSteps.deleteUser(String.valueOf(userData.response.getId()));
            } catch (Exception e) {
                System.err.println("Пользователь уже был удален или возникла ошибка: " + e.getMessage());
            } finally {
                userData = null;
            }
        }
    }

    @Test
    @UserSession

    public void userCanTransferMoneyTest() {

        this.senderBefore = accountSteps.getBalance(senderId);

        new UserDashboard()
                .checkWelcomeText()
                .openTransferPage()
                // Было: (receiverAcc, "Recipient Name", senderAcc, ...)
                // Стало:
                .makeTransfer(senderAcc, "Recipient Name", receiverAcc, TransferPage.VALID_TRANSFER)
                .checkAlertMessageAndAccept(BankAlerts.SUCCESSFULLY_TRANSFERRED_FROM_ONE_ACCOUNT_TO_ANOTHER, TransferPage.VALID_TRANSFER, receiverAcc);

        softly.assertThat(accountSteps.getBalance(senderId))
                .isCloseTo(senderBefore.subtract(TransferPage.VALID_TRANSFER), Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }

//     Негативный тест
    @Test
    @UserSession
    public void userCanNotTransferMoneyTest() {

        this.senderBefore = accountSteps.getBalance(senderId);

        new UserDashboard()
                .checkWelcomeText()
                .openTransferPage()
                .makeTransfer(receiverAcc, "Recipient Name", senderAcc, TransferPage.INVALID_TRANSFER)
                .checkAlertMessageAndAccept(BankAlerts.ERROR_TRANSFER_AMOUNT);

        softly.assertThat(accountSteps.getBalance(senderId))
                .as("Баланс отправителя не должен измениться при ошибке")
                .isCloseTo(senderBefore, Offset.offset(BigDecimal.ONE.movePointLeft(2)));
    }
}
