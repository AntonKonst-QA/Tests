package api.requests.skeleton.requesters;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.Getter;
import api.models.BaseModel;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.HttpRequest;
import api.requests.skeleton.interfaces.CrudEndpointInterface;

import java.util.List;

public class ValidatedCrudRequester<RQ extends BaseModel, RS extends BaseModel> extends HttpRequest implements CrudEndpointInterface<RQ, RS> {
    @Getter
    private CrudRequester crudRequester;
    private Class<RS> responseClass;

    public ValidatedCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
        this.responseClass = (Class<RS>) endpoint.getResponseModel();
    }

    private RS extractResponse(ValidatableResponse response) {
        return response.extract().as(responseClass);
    }

    private List<RS> extractList(ValidatableResponse response) {
        return response.extract().jsonPath().getList(".", responseClass);
    }

    @Override
    public List<RS> getAll() {return extractList(crudRequester.get());}

    @Override
    public RS post(RQ model) {return extractResponse(crudRequester.post(model));}

    @Override
    public RS put(RQ model) {
        return extractResponse(crudRequester.put(model));
    }

    @Override
    public RS get() {
        return extractResponse(crudRequester.get());
    }

    @Override
    public RS get(long id) {
        return null;
    }

    public RS getWithParam(String paramName, Object value) {
        return extractResponse(crudRequester.getWithPathParam(paramName, value));
    }
}
