import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class SynchronizedLdcWrapper {
  private LinkedDocumentCollection ldc;
  
  private final double pageRankDampingFactor = 0.85;
  private final double weightingFactor = 0.6;

  public SynchronizedLdcWrapper() {
    this.ldc = new LinkedDocumentCollection();
  }
  
  public synchronized void appendDocument(LinkedDocument doc) {
    ldc.appendDocument(doc);
  }
  
  public synchronized void forEach(Consumer<Document> cb) {
    for (int i = 0; i < ldc.numDocuments(); i++)
      cb.accept(ldc.get(i));
  }
  
  public synchronized void query(String query, BiConsumer<Document, Double> cb) {
    double[] relevance = ldc.match(query, pageRankDampingFactor, weightingFactor);
    for (int i = 0; i < ldc.numDocuments(); i++)
      cb.accept(ldc.get(i), relevance[i]);
  }
  
  public synchronized void pageRank(BiConsumer<Document, Double> cb) {
    double[] pageRanks = ldc.pageRank(pageRankDampingFactor);
    for (int i = 0; i < ldc.numDocuments(); i++)
      cb.accept(ldc.get(i), pageRanks[i]);
  }
  
  public synchronized void crawl() {
    ldc = ldc.crawl();
  }
}
