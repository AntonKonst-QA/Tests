package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import api.generators.GeneratingRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenerateChangeUserNameRequest extends BaseModel{
    @GeneratingRule(regex = "[A-Z][a-z]{4,8} [A-Z][a-z]{4,8}")
    private String name;
}
