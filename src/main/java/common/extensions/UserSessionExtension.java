package common.extensions;

import api.configs.Config;
import api.models.CustomerModel;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import java.util.List;

public class UserSessionExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext extensionContext) throws Exception {
        UserSession annotation = extensionContext.getRequiredTestMethod().getAnnotation(UserSession.class);
        if (annotation != null) {
            try {
                SessionStorage.getUser();
            } catch (IndexOutOfBoundsException | IllegalStateException e) {
                String defaultLogin = Config.getProperty("defaultApiUser");
                String defaultPassword = Config.getProperty("defaultApiPassword");

                if (defaultLogin == null) defaultLogin = "kate1998";
                if (defaultPassword == null) defaultPassword = "verysTRongPassword33$";

                CustomerModel user = CustomerModel.builder()
                        .username(defaultLogin)
                        .password(defaultPassword)
                        .build();

                SessionStorage.addUsers(List.of(user));
            }
        }
    }
}
