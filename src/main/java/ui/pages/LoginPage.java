package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    private final SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    private final SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));
    private final SelenideElement button = $("button");

    @Override
    public String url() {
        return "/login";
    }

    public UserDashboard authAsUser(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        button.click();
        return new UserDashboard();
    }
}
