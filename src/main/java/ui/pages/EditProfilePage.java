package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import static org.assertj.core.api.Assertions.assertThat;
import org.openqa.selenium.Alert;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public class EditProfilePage extends BasePage<EditProfilePage> {
    private final SelenideElement nameInput = $(Selectors.byAttribute("placeholder", "Enter new name"));
    private final SelenideElement saveButton = $(Selectors.byText("\uD83D\uDCBE Save Changes"));
    public static final String INVALID_NEW_USERNAME = "a";

    @Override
    public String url() { return "/profile"; }

    public EditProfilePage changeName(String newName) {
        nameInput.shouldBe(Condition.visible).clear();
        nameInput.setValue(newName);
        saveButton.click();
        return this;
    }

    public EditProfilePage checkAlertMessageAndAccept(BankAlerts expectedAlert) {
        Alert alert = switchTo().alert();
        assertThat(alert.getText()).isEqualTo(expectedAlert.getMessage());
        alert.accept();
        return this;
    }
}
