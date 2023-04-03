package backend;

import framework.annotations.*;
import framework.request.Request;
import framework.response.JsonResponse;
import framework.response.Response;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ControllerTest {
    int i = 0;
    @Autowired(verbose = false)
    ServiceTest service;
    @Autowired(verbose = false)
    BeanTest bean;
    @Autowired(verbose = false)
    BeanTestPrototype prototypeBean;
    @Autowired(verbose = false)
    @Qualifier("impl")
    Test test;

    public ControllerTest(){}

    @GET
    @Path(path = "/test")
    public Response test1(Request request){
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("route_location", request.getLocation());
        responseMap.put("route_method", request.getMethod().toString());
        responseMap.put("parameters", request.getParameters());
        Response response = new JsonResponse(responseMap);
        return response;
    }
}
