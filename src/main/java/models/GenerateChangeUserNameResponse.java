package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateChangeUserNameResponse extends BaseModel {
    private String message;
    private CustomerModel customer;
    private int id;
    private String username;
    private String name;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Customer {
        private int id;
        private String username;
        private String name;
        private String role;
    }
}
