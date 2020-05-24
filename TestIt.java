public class TestIt {

  public static void main(String[] args) {
    double pageRankDampingFactor = 0.85;
    double weightingFactor = 0.6;

    LinkedDocumentCollection ldc = new LinkedDocumentCollection();
    String command;

    boolean exit = false;

    while (!exit) {
      command = Terminal.askString("> ");

      if (command == null || command.equals("exit")) {
        // Exit program
        exit = true;
      } else if (command.startsWith("add ")) {
        // add a new document
        String titleAndText = command.substring(4);
        int separator = titleAndText.indexOf(':');
        String title = titleAndText.substring(0, separator);
        String text = titleAndText.substring(separator + 1);

        ldc.appendDocument(new LinkedDocument(title, "", "", null, null, text, title));
      } else if (command.startsWith("list")) {
        // list all document in collection
        for (int i = 0; i < ldc.numDocuments(); i++) {
          System.out.println(ldc.get(i).getTitle());
        }
      } else if (command.startsWith("query ")) {
        // query on the documents in the collection
        String query = command.substring(6);

        double[] relevance = ldc.match(query, pageRankDampingFactor, weightingFactor);
        
        String msg = "";
        for (int i = 0; i < ldc.numDocuments(); i++) {
          msg = (i + 1) + ". " + ldc.get(i).getTitle() + "; Relevanz: " + relevance[i];
          System.out.println(msg);
        }

        System.out.println();
      } else if (command.startsWith("count ")) {
        // print the count of a word in each document
        String word = command.substring(6);

        for (int i = 0; i < ldc.numDocuments(); i++) {
          Document doc = ldc.get(i);
          WordCountsArray docWordCounts = doc.getWordCounts();

          int count = docWordCounts.getCount(docWordCounts.getIndexOfWord(word));

          if (count == -1) {
            System.out.println(doc.getTitle() + ": gar nicht.");
          } else {
            System.out.println(doc.getTitle() + ": " + count + "x ");
          }
        }
      } else if (command.startsWith("pageRank")) {
        double[] pageRanks = ldc.pageRank(pageRankDampingFactor);

        for (int i = 0; i < ldc.numDocuments(); i++) {
          System.out.println(ldc.get(i).getTitle() + "; PageRank: " + pageRanks[i]);
        }
      } else if (command.startsWith("crawl")) {
        ldc = ldc.crawl();
      }
    }
  }
}
