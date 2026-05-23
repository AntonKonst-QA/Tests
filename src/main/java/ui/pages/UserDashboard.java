package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {
    private final SelenideElement userDashboardText = $(".welcome-text");
    private final SelenideElement depositMoneyButton = $(Selectors.byText("\uD83D\uDCB0 Deposit Money"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public DepositPage openDepositModal() {
        depositMoneyButton.shouldBe(Condition.visible).click();
        return new DepositPage();
    }

    public UserDashboard checkWelcomeText() {
        userDashboardText.shouldBe(Condition.visible).shouldHave(Condition.matchText("^Welcome, .+\\!$"));
        return this;
    }

    public TransferPage openTransferPage() {
        $(Selectors.byText("\uD83D\uDD04 Make a Transfer")).click();
        $(Selectors.withText("\uD83D\uDD04 Make a Transfer")).shouldBe(Condition.visible);
        $(Selectors.byText("\uD83C\uDD95 New Transfer")).click();
        return new TransferPage();
    }

    public EditProfilePage openProfilePage() {
        $(".user-username").click();
        $(Selectors.withText("✏\uFE0F Edit Profile")).shouldBe(Condition.visible);
        $(Selectors.byText("\uD83D\uDCBE Save Changes")).click();
        return new EditProfilePage();
    }
}
