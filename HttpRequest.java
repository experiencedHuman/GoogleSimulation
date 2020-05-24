import java.util.Map;
import java.util.TreeMap;

public class HttpRequest {
  private HttpMethod method;
  
  public HttpMethod getMethod() {
    return method;
  }
  
  private String path;
  
  public String getPath() {
    return path;
  }
  
  private Map<String, String> parameters;
  
  public Map<String, String> getParameters() {
    return parameters;
  }
  
  
  public HttpRequest(String requestLine) {
    String[] requestLineParts = requestLine.split(" ");
    if(requestLineParts.length < 2)
      throw new InvalidRequestException();
    String method = requestLineParts[0];
    switch(method) {
      case "GET":
        this.method = HttpMethod.GET;
        break;
      case "POST":
        this.method = HttpMethod.POST;
        break;
      default:
        throw new InvalidRequestException();
    }
    
    this.parameters = new TreeMap<>();
    
    String resource = requestLineParts[1];
    String[] pathParams = resource.split("\\?");
    this.path = pathParams[0];
    if(pathParams.length == 1)
      return;
    if(pathParams.length > 2)
      throw new InvalidRequestException();
    String paramsStr = pathParams[1];
    String[] params = paramsStr.split("&");
    for (int i = 0; i < params.length; i++) {
      String[] paramValue = params[i].split("=");
      if(paramValue.length != 2)
        throw new InvalidRequestException();
      parameters.put(paramValue[0], paramValue[1]);
    }
  }
}