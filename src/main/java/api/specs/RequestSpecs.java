package api.specs;

import api.configs.Config;
import api.models.LoginUserRequest;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.List;

public class RequestSpecs {
    private static final Log log = LogFactory.getLog(RequestSpecs.class);

    private RequestSpecs() {}

    private static RequestSpecBuilder defaultRequestBuilder() {
        String baseUrl = Config.getProperty("apiBaseUrl") != null
                ? Config.getProperty("apiBaseUrl")
                : Config.getProperty("apiServer");

        if (baseUrl == null) {
            baseUrl = "http://localhost:4111";
        }

        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(
                        new RequestLoggingFilter(),
                        new ResponseLoggingFilter()
                ))
                .setBaseUri(baseUrl)
                .setBasePath(Config.getProperty("apiVersion"));
    }

    public static RequestSpecification unauthSpec() {
        return defaultRequestBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return authAsUser("admin", "admin");
    }

    public static RequestSpecification authAsUser(String username, String password) {
        String token = getUserToken(username, password);

        String authorizationHeader = (token.startsWith("Bearer ") || token.startsWith("Basic "))
                ? token
                : "Bearer " + token;

        return defaultRequestBuilder()
                .addHeader("Authorization", authorizationHeader)
                .build();
    }

    public static String getUserToken(String username, String password) {
        String baseUrl = Config.getProperty("apiBaseUrl") != null
                ? Config.getProperty("apiBaseUrl").trim()
                : "http://localhost:4111";

        String apiVersion = Config.getProperty("apiVersion") != null ? Config.getProperty("apiVersion").trim() : "";

        LoginUserRequest loginBody = LoginUserRequest.builder()
                .username(username)
                .password(password)
                .build();

        String token = io.restassured.RestAssured.given()
                .noFilters()
                .baseUri(baseUrl)
                .basePath(apiVersion)
                .contentType(io.restassured.http.ContentType.JSON)
                .accept(io.restassured.http.ContentType.JSON)
                .body(loginBody)
                .log().all()
                .post("/auth/login")
                .then()
                .log().ifValidationFails()
                .statusCode(200)
                .extract()
                .header("Authorization");

        if (token == null || token.isEmpty()) {
            throw new IllegalStateException("Пустой заголовок 'Authorization'");
        }

        return token;
    }
}
