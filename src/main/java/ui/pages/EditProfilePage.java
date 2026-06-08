package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import com.mifmif.common.regex.Generex;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Alert;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public class EditProfilePage extends BasePage<EditProfilePage> {
    private final SelenideElement newNameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private final SelenideElement saveChangeButton = $(Selectors.byText("\uD83D\uDCBE Save Changes"));
    public static final String INVALID_NEW_USERNAME = "nothing";

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage changeName(String newName) {
        newNameInput.shouldBe(Condition.visible).setValue(newName);
        saveChangeButton.click();
        return this;
    }

    public EditProfilePage checkAlertMessageAndAccept(BankAlerts expectedAlertText) {
        Alert alert = switchTo().alert();
        Assertions.assertThat(alert.getText())
                .isEqualTo(expectedAlertText.getMessage());
        alert.accept();
        return this;
    }
}
