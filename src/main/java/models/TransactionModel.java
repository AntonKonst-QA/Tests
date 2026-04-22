package models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionModel extends BaseModel {
    private int id;
    private double amount;
    private String type;
    private String timestamp;
    private int relatedAccountId;
}
