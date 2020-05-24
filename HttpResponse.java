public class HttpResponse {
  private HttpStatus status;
  
  public HttpStatus getStatus() {
    return status;
  }
  
  private String body;
  
  public String getBody() {
    return body;
  }

  public HttpResponse(HttpStatus status, String body) {
    this.status = status;
    this.body = body;
  }
  
  public HttpResponse(HttpStatus status) {
    this(status, "");
  }
  
  @Override
  public String toString() {
    StringBuilder responseBuilder = new StringBuilder();
    
    responseBuilder.append("HTTP/2.0 ");
    responseBuilder.append(status.getCode());
    responseBuilder.append(" ");
    responseBuilder.append(status.getText());
    responseBuilder.append("\r\n\r\n");
    
    responseBuilder.append(body);
    
    return responseBuilder.toString();
  }
}
