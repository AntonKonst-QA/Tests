package iteration2.ui;

import api.generators.RandomData;
import common.annotations.UserSession;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import ui.pages.BankAlerts;
import ui.pages.EditProfilePage;
import ui.pages.UserDashboard;

@Execution(ExecutionMode.SAME_THREAD)
public class ChangeUserNameTest extends BaseUiTest {

    @Test
    @UserSession
    @Order(1)
    public void userCanChangeUserName() {

        // Шаг 1: Запомнили имя пользователя на бэке ДО изменения имени пользователя
        String nameBefore = userSteps.getProfile().getName();
        String dynamicNewName = RandomData.generateUniqueName();

        // Шаг 2: Проверка UI
        new UserDashboard()
                .checkWelcomeText()
                .openProfilePage()
                .changeName(dynamicNewName)
                .checkAlertMessageAndAccept(BankAlerts.NAME_UPDATE_SUCCESSFULLY);

        // Шаг 3: Проверка изменения на бэке
        String nameAfter = userSteps.getProfile().getName();

        softly.assertThat(nameAfter)
                .isNotEqualTo(nameBefore)
                .isEqualTo(dynamicNewName);
    }

    @Test
    @UserSession
    @Order(2)
    public void userCanNotChangeUserName() {

        // Шаг 1: Запомнили имя пользователя на бэке ДО изменения имени пользователя
        String nameBefore = userSteps.getProfile().getName();

        // Шаг 2: Проверка UI
        new UserDashboard()
                .checkWelcomeText()
                .openProfilePage()
                .changeName(EditProfilePage.INVALID_NEW_USERNAME)
                .checkAlertMessageAndAccept(BankAlerts.NAME_MUST_CONTAIN_TWO_WORDS);

        // Шаг 3: Проверка отсутствия изменения на бэке
        String nameAfter = userSteps.getProfile().getName();
        softly.assertThat(nameAfter)
                .isEqualTo(nameBefore)
                .isNotEqualTo(EditProfilePage.INVALID_NEW_USERNAME);
    }
}
