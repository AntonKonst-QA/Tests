package iteration2.ui;

import api.models.CustomerModel;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import api.steps.UserSteps;
import ui.pages.BankAlerts;
import ui.pages.EditProfilePage;

public class ChangeUserNameTest extends BaseUiTest {
    private final UserSteps userSteps = new UserSteps();

    @Test
    public void userCanChangeUserName() {
        SoftAssertions softly = new SoftAssertions();

        // Шаг 1: Запомнили имя пользователя на бэке ДО изменения имени пользователя
        String nameBefore = userSteps.getProfile().getName();
        CustomerModel user = CustomerModel.getUser();
        String dynamicNewName = EditProfilePage.generateUniqueName();

        // Шаг 2: Проверка UI
        authAsUser(user.getUsername(), user.getPassword())
                .checkWelcomeText()
                .openProfilePage()
                .changeName(dynamicNewName)
                .checkAlertMessageAndAccept(BankAlerts.NAME_UPDATE_SUCCESSFULLY);

        // Шаг 3: Проверка изменения на бэке
        String nameAfter = userSteps.getProfile().getName();
        softly.assertThat(nameAfter)
                .isNotEqualTo(nameBefore);

        softly.assertThat(nameAfter)
                .isEqualTo(dynamicNewName);

        softly.assertAll();
    }

    @Test
    public void userCanNotChangeUserName() {
        SoftAssertions softly = new SoftAssertions();

        // Шаг 1: Запомнили имя пользователя на бэке ДО изменения имени пользователя
        String nameBefore = userSteps.getProfile().getName();
        CustomerModel user = CustomerModel.getUser();

        // Шаг 2: Проверка UI
        authAsUser(user.getUsername(), user.getPassword())
                .checkWelcomeText()
                .openProfilePage()
                .changeName(EditProfilePage.INVALID_NEW_USERNAME)
                .checkAlertMessageAndAccept(BankAlerts.NAME_MUST_CONTAIN_TWO_WORDS);

        // Шаг 3: Проверка отсутствия изменения на бэке
        String nameAfter = userSteps.getProfile().getName();
        softly.assertThat(nameAfter)
                .isEqualTo(nameBefore);

        softly.assertThat(nameAfter)
                .isNotEqualTo(EditProfilePage.INVALID_NEW_USERNAME);

        softly.assertAll();
    }
}
