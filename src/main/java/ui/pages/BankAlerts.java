package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlerts {
    SUCCESSFULLY_DEPOSITED("✅ Successfully deposited $%s to account %s!"),
    NAME_UPDATE_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_MUST_CONTAIN_TWO_WORDS("Name must contain two words with letters only"),
    PLEASE_ENTER_VALID_NAME("❌ Please enter a valid name."),
    SUCCESSFULLY_TRANSFERRED_FROM_ONE_ACCOUNT_TO_ANOTHER("✅ Successfully transferred $%s to account %s!"),
    ERROR_TRANSFER_AMOUNT("❌ Error: Transfer amount must be at least 0.01"),
    PLEASE_ENTER_A_VALID_AMOUNT("❌ Please enter a valid amount.");

    private final String message;

    BankAlerts(String message) {
        this.message = message;
    }

    public String getFormattedMessage(Object... args) {
        return String.format(this.message, args);
    }
}
