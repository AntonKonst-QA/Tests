package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import org.apache.commons.codec.cli.Digest;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Alert;

import java.math.BigDecimal;
import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public class TransferPage extends BasePage<TransferPage> {
    private final SelenideElement accountSelector = $(".account-selector");
    private final SelenideElement recipientNameInput = $(Selectors.byAttribute("placeholder", "Enter recipient name"));
    private final SelenideElement recipientAccountInput = $(Selectors.byAttribute("placeholder", "Enter recipient account number"));
    private final SelenideElement amountInput = $(Selectors.byAttribute("placeholder", "Enter amount"));
    private final SelenideElement termsCheckbox = $("input[type='checkbox']");
    private final SelenideElement sendTransferButton = $(Selectors.byText("\uD83D\uDE80 Send Transfer"));

    public static final BigDecimal VALID_TRANSFER = new BigDecimal("4");
    public static final BigDecimal INVALID_TRANSFER = new BigDecimal("0");

    @Override
    public String url() { return "/transfer"; }

    public TransferPage makeTransfer(String accountFrom, String recipientName, String recipientAccount, BigDecimal amount) {
        accountSelector.shouldBe(Condition.visible).selectOptionContainingText(accountFrom);
        recipientNameInput.setValue(recipientName);
        recipientAccountInput.setValue(recipientAccount);
        amountInput.setValue(amount.toString()); // .toString() достаточно
        termsCheckbox.click();
        sendTransferButton.click();
        return this;
    }

    public TransferPage checkAlertMessageAndAccept(BankAlerts expectedAlert, Object... args) {
        Alert alert = switchTo().alert();
        String expectedText = expectedAlert.getFormattedMessage(args);
        Assertions.assertThat(alert.getText())
                        .isEqualTo(expectedText);
        alert.accept();

        return this;
    }
}
