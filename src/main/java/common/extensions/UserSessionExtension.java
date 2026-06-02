package common.extensions;

import api.configs.Config;
import api.models.CustomerModel;
import com.codeborne.selenide.Selenide;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.LoginPage;

import java.util.List;

public class UserSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {

            CustomerModel user;
            try {
                user = SessionStorage.getUser();
            } catch (IndexOutOfBoundsException | IllegalStateException e) {
                String defaultLogin = Config.getProperty("defaultApiUser");
                String defaultPassword = Config.getProperty("defaultApiPassword");

                if (defaultLogin == null) defaultLogin = "kate1998";
                if (defaultPassword == null) defaultPassword = "verysTRongPassword33$";

                user = CustomerModel.builder()
                        .username(defaultLogin)
                        .password(defaultPassword)
                        .build();

                SessionStorage.addUsers(List.of(user));
            }

            Selenide.open("/");

            new LoginPage().authAsUser(user.getUsername(),user.getPassword());
        }
    }
}
