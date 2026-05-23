package api.models;

import api.configs.Config;
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
public class CustomerModel extends BaseModel {
    private int id;
    private String username;
    private String password;
    private String name;
    private String role;

    public static CustomerModel getUser() {
        return CustomerModel.builder().username(Config.getProperty("user.username"))
                .password(Config.getProperty("user.password")).build();
    }
}
