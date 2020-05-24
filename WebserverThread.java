import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.TreeMap;
import java.util.stream.IntStream;

import util.Terminal;

public class WebserverThread extends Thread {
  private TemplateProcessor tp;
  private LinkedDocumentCollection ldc;
  private Socket client;
  
  public WebserverThread(TemplateProcessor tp, LinkedDocumentCollection ldc, Socket client) {
    this.tp = tp;
    this.ldc = ldc;
    this.client = client;
  }

  private HttpResponse handleMainPage() {
    TreeMap<String, String> varass = new TreeMap<>();
    varass.put("%value", "");
    varass.put("%results", "");
    String body = tp.replace(varass);
    return new HttpResponse(HttpStatus.Ok, body);
  }

  private HttpResponse handleSearchRequest(String query) {
    TreeMap<String, String> varass = new TreeMap<>();
    varass.put("%value", query);
    varass.put("%results", "");
    String msg = "";
    synchronized (ldc) {
      double pageRankDampingFactor = 0.85;
      double weightingFactor = 0.6;
      double[] relevance = ldc.match(query, pageRankDampingFactor, weightingFactor);
      if (ldc.numDocuments() > 0)
        msg += "<tr><td><b>ID</b></td><td><b>Page</b></td><td><b>Relevance</b></td></tr>";
      msg += IntStream.range(0, ldc.numDocuments()).boxed().reduce("", (acc, i) -> {
        acc += "<tr>";
        acc += "<td>" + (i + 1) + "</td>";
        acc += "<td><a href=\"" + ldc.get(i).getTitle() + "\">" + ldc.get(i).getTitle() + "</a></td>";
        acc += "<td>" + relevance[i] + "</td>";
        acc += "</tr>";
        return acc;
      }, (a, b) -> a + b);
    }
    varass.put("%results", msg);
    String body = tp.replace(varass);
    return new HttpResponse(HttpStatus.Ok, body);
  }

  private HttpResponse handleFileRequest(String fileName) {
    if (fileName.contains("/"))
      return new HttpResponse(HttpStatus.Forbidden);
    String[] fileContents = Terminal.readFile(fileName);
    if (fileContents == null)
      return new HttpResponse(HttpStatus.NotFound);
    if(fileContents.length != 2)
      return new HttpResponse(HttpStatus.Forbidden);
    String body = fileContents[1];
    return new HttpResponse(HttpStatus.Ok, body);
  }

  private void communicate(BufferedReader in, PrintWriter out) throws IOException {
    String requestLine = in.readLine();
    if (requestLine == null)
      return;
    in.lines().takeWhile(l -> !l.equals("")).count();
    
    System.out.println("=> Request header received");

    HttpRequest request;
    try {
      request = new HttpRequest(requestLine);
    } catch (InvalidRequestException ex) {
      System.out.println("=> Bad request!");
      out.print(new HttpResponse(HttpStatus.BadRequest));
      return;
    }

    if (request.getMethod() != HttpMethod.GET) {
      System.out.println("=> Invalid method!");
      out.print(new HttpResponse(HttpStatus.MethodNotAllowed));
      return;
    }

    HttpResponse response;
    if (request.getPath().equals("/")) {
      System.out.println("=> Query for main page");
      response = handleMainPage();
    } else if (request.getPath().equals("/search")) {
      System.out.println("=> Search query");
      String query = request.getParameters().get("query");
      response = handleSearchRequest(query);
    } else {
      System.out.println("=> File query");
      String fileName = request.getPath().substring(1);
      response = handleFileRequest(fileName);
    }
    out.print(response);
  }
  
  @Override
  public void run() {
    try {
      BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
      PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
      try {
        communicate(in, out);
      } catch (IOException exp) {
        exp.printStackTrace();
      } finally {
        out.close();
        client.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
