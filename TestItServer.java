import java.io.IOException;
import java.net.ServerSocket;

public class TestItServer {

  public static void main(String[] args) throws IOException {
    @SuppressWarnings("resource")
    ServerSocket serverSocket = new ServerSocket(8000);
    SynchronizedLdcWrapper slw = new SynchronizedLdcWrapper();
    while (true) {
      java.net.Socket client = serverSocket.accept();
      System.out.println("Client connected!");

      TestItThread tid = new TestItThread(slw, client);
      tid.start();
    }
  }

}
