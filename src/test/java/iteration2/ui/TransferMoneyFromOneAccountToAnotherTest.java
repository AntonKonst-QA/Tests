package iteration2.ui;

import api.models.CustomerModel;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import api.steps.AccountSteps;
import ui.pages.BankAlerts;
import ui.pages.TransferPage;

import static org.assertj.core.api.AssertionsForClassTypes.within;

public class TransferMoneyFromOneAccountToAnotherTest extends BaseUiTest {
    private final AccountSteps accountSteps = new AccountSteps();

    @Test
    public void userCanTransferMoneyTest() {
        SoftAssertions softly = new SoftAssertions();
        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        double senderBefore = accountSteps.getBalance(TransferPage.SENDER_ID);
        CustomerModel user = CustomerModel.getUser();

        // Шаг 2: Проверка UI
        authAsUser(user.getUsername(), user.getPassword())
                .checkWelcomeText()
                .openTransferPage()
                .makeTransfer(TransferPage.recipientAccount, TransferPage.recipientName, TransferPage.RECEIVER_ACC_NAME, TransferPage.VALID_TRANSFER)
                .checkAlertMessageAndAccept(BankAlerts.SUCCESSFULLY_TRANSFERRED_FROM_ONE_ACCOUNT_TO_ANOTHER, TransferPage.VALID_TRANSFER,TransferPage.RECEIVER_ACC_NAME);

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(TransferPage.SENDER_ID))
                .as("Списание средств у отправителя")
                .isCloseTo(senderBefore - TransferPage.VALID_TRANSFER, within(0.001));

        softly.assertAll();
    }

//     Негативный тест
    @Test
    public void userCanNotTransferMoneyTest() {
        SoftAssertions softly = new SoftAssertions();

        // Шаг 1: Запомнили баланс на бэке ДО клика в браузере
        double senderBefore = accountSteps.getBalance(TransferPage.SENDER_ID);
        CustomerModel user = CustomerModel.getUser();

        // Шаг 2: Проверка UI
        authAsUser(user.getUsername(), user.getPassword())
                .checkWelcomeText()
                .openTransferPage()
                .makeTransfer(TransferPage.recipientAccount, TransferPage.recipientName, TransferPage.RECEIVER_ACC_NAME, TransferPage.INVALID_TRANSFER)
                .checkAlertMessageAndAccept(BankAlerts.ERROR_TRANSFER_AMOUNT);

        // Шаг 3: Проверка изменения на бэке
        softly.assertThat(accountSteps.getBalance(TransferPage.SENDER_ID))
                .as("Баланс отправителя не должен измениться")
                .isEqualTo(senderBefore);

        softly.assertAll();
    }
}
