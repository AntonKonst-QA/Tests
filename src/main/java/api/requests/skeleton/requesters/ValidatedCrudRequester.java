package api.requests.skeleton.requesters;

import api.requests.skeleton.interfaces.GetAllEndpointInterface;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.Getter;
import api.models.BaseModel;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.HttpRequest;
import api.requests.skeleton.interfaces.CrudEndpointInterface;

import java.util.Arrays;
import java.util.List;

public class ValidatedCrudRequester<RQ extends BaseModel, RS extends BaseModel>
        extends HttpRequest implements CrudEndpointInterface<RQ, RS>, GetAllEndpointInterface<RS> {

    @Getter
    private final CrudRequester crudRequester;
    private final Class<RS> responseClass;

    @SuppressWarnings("unchecked")
    public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
        this.responseClass = (Class<RS>) endpoint.getResponseModel();
    }

    private RS extractResponse(io.restassured.response.Response response) {
        try {
            return response.as(responseClass);
        } catch (Exception e) {
            System.err.println("❌ Ошибка десериализации ответа в класс " + responseClass.getSimpleName() + ": " + e.getMessage());
            return null;
        }
    }

    @Override
    public RS post(RQ model) {
        return extractResponse(crudRequester.post(model).extract().response());
    }

    @Override
    public RS put(RQ model) {
        return extractResponse(crudRequester.put((BaseModel) model).extract().response());    }

    @Override
    public RS put(long id, RQ model) {
        return extractResponse(crudRequester.put(id, model).extract().response());
    }

    @Override
    public RS delete(long id) {
        return extractResponse(crudRequester.delete(id).extract().response());
    }

    @Override
    public RS get() {
        return extractResponse(crudRequester.get().extract().response());
    }

    @Override
    public RS get(long id) {
        return extractResponse(crudRequester.get(id).extract().response());
    }

    public RS getWithParam(String paramName, Object value) {
        return extractResponse(crudRequester.getWithPathParam(paramName, value).extract().response());
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<RS> getAll(Class<?> clazz) {
        RS[] array = (RS[]) io.restassured.RestAssured.given()
                .spec(requestSpecification)
                .get(endpoint.getUrl())
                .then()
                .spec(responseSpecification)
                .extract().response().as(clazz);

        return Arrays.asList(array);
    }

    public void deleteVoid(long id) {
        crudRequester.delete(id)
                .extract()
                .response();
    }
}