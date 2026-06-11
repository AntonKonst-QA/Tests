package api.specs;

import api.configs.Config;
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

    private RequestSpecs(){}

    private static RequestSpecBuilder defaultRequestBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(),
                        new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("apiServer"))
                .setBasePath(Config.getProperty("apiVersion"));
    }

    public static RequestSpecification authUser(String username, String password) {
        return defaultRequestBuilder()
                .setAuth(io.restassured.RestAssured.preemptive().basic(username, password))
                .build();
    }
}
