package common.extensions;

import api.generators.CreateUserInAPIForUi;
import api.models.CustomerModel;
import com.codeborne.selenide.Selenide;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import iteration2.ui.BaseUiTest;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ui.pages.LoginPage;

import java.util.List;

public class UserSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {
            CreateUserInAPIForUi.UserData userData = CreateUserInAPIForUi.createDefaultUser();

            CustomerModel user = CustomerModel.builder()
                    .username(userData.response.getUsername())
                    .password(userData.rawPassword) // !!! Здесь был хэш, теперь будет чистый пароль
                    .build();

            SessionStorage.addUsers(List.of(user));

            Selenide.open("/");
            new LoginPage().login(user.getUsername(), user.getPassword());

            Object testInstance = extensionContext.getRequiredTestInstance();
            if (testInstance instanceof BaseUiTest) {
                ((BaseUiTest) testInstance).initSteps(user.getUsername(), user.getPassword());
            }
        }
    }
}
