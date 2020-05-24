//Helper class for the cells of the DocumentCollection
public class DocumentCollectionCell {
  //the document in this cell
  private Document document;

  //pointer to the next cell
  private DocumentCollectionCell next;

  //the similarity of the document in this cell
  private double querySimilarity;

  //Constructs a new instance.
  public DocumentCollectionCell(Document document, DocumentCollectionCell next) {
    this.document = document;
    this.next = next;
    this.querySimilarity = 0;
  }

  //Returns the next DocumentCollectionCell
  public DocumentCollectionCell getNext() {
    return next;
  }

  //Set the next DocumentCollectionCell to the specified value
  public void setNext(DocumentCollectionCell next) {
    this.next = next;
  }

  //Returns the similarity of the Document in this cell
  public double getQuerySimilarity() {
    return querySimilarity;
  }

  //Sets the similarity of the Document in this cell
  public void setQuerySimilarity(double querySimilarity) {
    this.querySimilarity = querySimilarity;
  }

  //Returns the Document in this cell.
  public Document getDocument() {
    return document;
  }

  //Sets the Document in this cell and returns the Document that used to be in this cell.
  public Document setDocument(Document document) {
    Document oldDocument = this.document;
    this.document = document;
    return oldDocument;
  }
}