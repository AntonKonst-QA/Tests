package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {
    private SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    private SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));
    private SelenideElement loginButton = $("button");

    @Override
    public String url() {
        return "/login";
    }

    public LoginPage login(String username, String password) {
        usernameInput.setValue(username);
        passwordInput.setValue(password);
        loginButton.click();
        return this;
    }
}
