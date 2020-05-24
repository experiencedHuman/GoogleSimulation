
public enum HttpStatus {
  Ok(200, "Ok"),
  BadRequest(400, "Bad Request"),
  Forbidden(403, "Forbidden"),
  NotFound(404, "Not Found"), 
  MethodNotAllowed(405, "Method Not Allowed");
  
  
  private int code;
  
  public int getCode() {
    return code;
  }
  
  private String text;
  
  public String getText() {
    return text;
  }
  
  private HttpStatus(int code, String text) {
    this.code = code;
    this.text = text;
  }
}
