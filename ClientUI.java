import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class ClientUI {

    private final Socket socket;
    private final BufferedReader receiveFromServerReader;
    private final PrintWriter sendToServerWriter;

    // UI elements
    private final JFrame frame = new JFrame("Suchmaschine");
    private final JPanel mainPanel = new JPanel();
    private final JPanel queryPanel = new JPanel();
    private final JPanel countPanel = new JPanel();
    private final JTextField queryTextField = new JTextField(30);
    private final JTextField countTextField = new JTextField(30);
    private final JButton queryButton = new JButton("Query");
    private final JButton countButton = new JButton("Count");
    private final JButton exitButton = new JButton("Exit");
    private final JButton addButton = new JButton("Add");
    private final JButton pageRankButton = new JButton("PageRank");
    private final JButton listButton = new JButton("List");

    abstract class ReceiveLinesFromServerThread extends Thread {
        abstract public void setSubscribingTextArea(final JTextArea subscribingTextArea);
    }

    /**
     * A thread to receive the textual response from the server.
     * Publish/subscribe principle:
     * the incoming stream from server is either buffered or
     * appended to the given text area.
     */
    private final ReceiveLinesFromServerThread textLinesReceiver = new ReceiveLinesFromServerThread() {
        final StringBuilder strBuilder = new StringBuilder();
        JTextArea subscribingTextArea;
        @Override public void run() {
            receiveFromServerReader
                    .lines()
                    .map(line -> line.replace("> ", "") + "\n")
                    .forEach(lineFromServer -> {
                        synchronized (this) {
                            if (subscribingTextArea == null)
                                strBuilder.append(lineFromServer);
                            else {
                                final JTextArea unchangeableTextArea = subscribingTextArea;
                                EventQueue.invokeLater(() -> unchangeableTextArea.append(lineFromServer));
                            }
                        }
                    });
        }
        public void setSubscribingTextArea(final JTextArea subscribingTextArea) {
            synchronized (this) {
                this.subscribingTextArea = subscribingTextArea;
                if (subscribingTextArea != null) {
                    final String result = strBuilder.toString();
                    strBuilder.setLength(0);
                    EventQueue.invokeLater(() -> subscribingTextArea.append(result));
                }
            }
        }
    };

    private ClientUI() throws IOException {
        // establish connection to server
        socket = new Socket("localhost",8000);
        receiveFromServerReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        sendToServerWriter = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        textLinesReceiver.start();

        EventQueue.invokeLater(() -> {
            // listener methods
            exitButton.addActionListener(actionEvent -> new Thread(() -> {
                disconnect();
                System.exit(0);
            }).start());
            queryButton.addActionListener(actionEvent -> new Thread(() -> {
                sendQuery("query", queryTextField.getText());
                EventQueue.invokeLater(() -> new TextAreaWindow("Query"));
            }).start());
            countButton.addActionListener(actionEvent -> new Thread(() -> {
                sendQuery("count", countTextField.getText());
                EventQueue.invokeLater(() -> new TextAreaWindow("Count"));
            }).start());
            pageRankButton.addActionListener(actionEvent -> new Thread(() -> {
                sendQuery("pageRank");
                EventQueue.invokeLater(() -> new TextAreaWindow("Count"));
            }).start());
            listButton.addActionListener(actionEvent -> new Thread(() -> {
                sendQuery("list");
                EventQueue.invokeLater(() -> new TextAreaWindow("List"));
            }).start());
            addButton.addActionListener(actionEvent -> new AddDocumentWindow());

            // GUI creation
            queryPanel.add(queryButton);
            queryPanel.add(queryTextField);
            countPanel.add(countButton);
            countPanel.add(countTextField);
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setOpaque(true);
            mainPanel.add(exitButton);
            mainPanel.add(addButton);
            mainPanel.add(pageRankButton);
            mainPanel.add(listButton);
            mainPanel.add(queryPanel);
            mainPanel.add(countPanel);
            frame.setContentPane(mainPanel);
            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    private void disconnect() {
        try {
            socket.close();
            receiveFromServerReader.close();
            sendToServerWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendQuery(final String command) {
        final String cmd = command + "\r\n";
        sendToServerWriter.print(cmd);
        sendToServerWriter.flush();
    }

    private void sendQuery(final String command, final String parameter) {
        final String cmd = command + " " + parameter + "\r\n";
        sendToServerWriter.print(cmd);
        sendToServerWriter.flush();
    }

    class TextAreaWindow extends JFrame {
        final JTextArea textArea = new JTextArea(30, 30);
        TextAreaWindow(final String title) {
            super(title);
            add(textArea);
            pack();
            setVisible(true);
            new Thread(() ->
                    textLinesReceiver.setSubscribingTextArea(textArea)
            ).start();
            disableButtons();
            addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(final WindowEvent e) {
                    textLinesReceiver.setSubscribingTextArea(null);
                    enableButtons();
                }
            });
        }
    }

    class AddDocumentWindow extends JFrame {
        final JTextField docTitleField = new JTextField("doc title", 30);
        final JTextArea docContentField = new JTextArea("doc content", 30, 30);
        AddDocumentWindow() {
            super("Add Document");
            final JButton addDocumentButton = new JButton("Add Document");
            addDocumentButton.addActionListener((actionEvent) ->
                new Thread(() -> {
                    sendQuery("add", toString());
                    System.out.println("Successfully added document " + this);
                }).start()
            );
            setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
            docContentField.setLineWrap(true);
            add(docTitleField);
            add(docContentField);
            add(addDocumentButton);
            pack();
            setVisible(true);
            disableButtons();
            addWindowListener(new WindowAdapter() {
                @Override public void windowClosing(final WindowEvent e) {
                    enableButtons();
                }
            });
        }
        @Override public String toString() {
            return docTitleField.getText() + ":" + docContentField.getText();
        }
    }

    private void disableButtons() {
        queryButton.setEnabled(false);
        countButton.setEnabled(false);
        exitButton.setEnabled(false);
        addButton.setEnabled(false);
        pageRankButton.setEnabled(false);
    }

    private void enableButtons() {
        queryButton.setEnabled(true);
        countButton.setEnabled(true);
        exitButton.setEnabled(true);
        addButton.setEnabled(true);
        pageRankButton.setEnabled(true);
    }

    public static void main(final String[] args) {
        try {
            new ClientUI();
        } catch (IOException e) {
            System.out.println("Could not establish connection");
            System.exit(1);
        }
    }
}
