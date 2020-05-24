//This class represents a ordered collection of documents.
public class DocumentCollection {
  //the first element in the collection
  private DocumentCollectionCell first;

  //the last element in the collection
  private DocumentCollectionCell last;

  //the number of elements in this collection
  private int size;

  //Constructs an empty collection
  public DocumentCollection() {
    this.first = null;
    this.last = null;
    this.size = 0;
  }

  //Inserts the specified Document at the beginning of the collection.
  public void prependDocument(Document doc) {
    if (doc == null) {
      return;
    }

    if (this.isEmpty()) {
      this.first = new DocumentCollectionCell(doc, null);
      this.last = first;
    } else {
      this.first = new DocumentCollectionCell(doc, first);
    }
    
    size++;
  }

   //Inserts the specified Document at the end of the collection.
  public void appendDocument(Document doc) {
    if (doc == null) {
      return;
    }

    if (this.isEmpty()) {
      /* list empty, add as only element */
      this.first = new DocumentCollectionCell(doc, null);
      this.last = first;
    } else {
      last.setNext(new DocumentCollectionCell(doc, null));
      last = last.getNext();
    }

    size++;
  }

  //Returns the index in this collection of the specified Document
  public int indexOf(Document doc) {
    if (doc == null || this.isEmpty()) {
      return -1;
    }

    DocumentCollectionCell tmp = this.first;
    int index = 0;

    while (tmp != null) {
      if (tmp.getDocument().equals(doc)) {
        return index;
      }

      tmp = tmp.getNext();
      index++;
    }

    return -1;
  }

  //Returns true, if the specified Document is contained in this collection.
  public boolean contains(Document doc) {
    return (this.indexOf(doc) != -1);
  }

  //Removes the element at the specified index.
  public boolean remove(int index) {
    if (index < 0 || index >= this.numDocuments()) {
      return false;
    }

    if (this.isEmpty()) {
      return false;
    }

    /* remove first */
    if (index == 0) {
      this.removeFirstDocument();
      return true;
    }

    /* remove last */
    if (index == this.numDocuments() - 1) {
      this.removeLastDocument();
      return true;
    }

    /* loop to index, keep track of previous */
    DocumentCollectionCell actual = this.first.getNext();
    DocumentCollectionCell prev = this.first;
    int i = 1;

    while (i < index) {
      prev = actual;
      actual = actual.getNext();
      i++;
    }

    /* delete actual */
    prev.setNext(actual.getNext());
    size--;
    return true;
  }

  //Removes the last element from the collection.
  public void removeLastDocument() {
    if (this.isEmpty()) {
      return;
    }

    /* one element: clear list and return */
    if (this.numDocuments() == 1) {
      this.clear();
      return;
    }

    /* navigate to the element before last */
    DocumentCollectionCell newLast = this.first;
    while (newLast.getNext() != this.last) {
      newLast = newLast.getNext();
    }

    /* assign new last element */
    newLast.setNext(null);
    this.last = newLast;
    size--;
  }

  // Removes the first element from the collection.
  public void removeFirstDocument() {
    if (this.isEmpty()) {
      return;
    }

    /* one element: clear list and return */
    if (this.numDocuments() == 1) {
      this.clear();
      return;
    }

    this.first = this.first.getNext();
    size--;
  }

  //Returns the first element of the collection or null, if it is empty.
  public Document getFirstDocument() {
    if (this.isEmpty()) {
      return null;
    }

    return this.first.getDocument();
  }

  //Returns the last element of the collection or null, if it is empty.
  public Document getLastDocument() {
    if (this.isEmpty()) {
      return null;
    }

    return this.last.getDocument();
  }

  //Deletes all elements from the collection.
  private void clear() {
    this.first = null;
    this.last = null;
    this.size = 0;
  }

  //Determines, whether this collection is empty.
  public boolean isEmpty() {
    return this.size == 0;
  }

  //Returns the number of Documents in this collection.
  public int numDocuments() {
    return this.size;
  }

  //Returns the Document in this collection at the specified index.
  public Document get(int index) {
    if (index < 0 || index >= this.size) {
      return null;
    }

    return getDocumentCollectionCell(index).getDocument();
  }

  //This method returns a WordCountsArray containing all words of all Documents in this collection.
  private WordCountsArray allWords() {
    DocumentCollectionCell tmp = this.first;
    WordCountsArray allWords = new WordCountsArray(0);

    while (tmp != null) {
      Document doc = tmp.getDocument();
      WordCountsArray wca = doc.getWordCounts();

      for (int i = 0; i < wca.size(); i++) {
        allWords.add(wca.getWord(i), 0);
      }

      tmp = tmp.getNext();
    }

    return allWords;
  }

  //This method calculates the similarity between the specified query and all Documents in this DocumentCollection and 
  //sorts the Documents in this collection according to the calculated similarity.
  public void match(String searchQuery) {
    if (this.isEmpty()) {
      return;
    }

    if (searchQuery == null || searchQuery.equals("")) {
      return;
    }

    /* add query to collection as document */
    Document queryDocument = new Document("", "", "", null, null, searchQuery);
    this.prependDocument(queryDocument);

    /* add every word to every document with count 0 */
    this.addZeroWordsToDocuments();

    /* sort all WordCountsArrays of all documents */
    DocumentCollectionCell tmp = this.first;
    while (tmp != null) {
      tmp.getDocument().getWordCounts().sort();
      tmp = tmp.getNext();
    }

    /* calculate similarities with query document */
    tmp = this.first.getNext();
    while (tmp != null) {
      WordCountsArray wca1 = tmp.getDocument().getWordCounts();
      WordCountsArray wca2 = queryDocument.getWordCounts();
      //tmp.setQuerySimilarity(wca1.computeSimilarity(wca2));
      tmp.setQuerySimilarity(wca1.computeSimilarity(wca2, this));
      tmp = tmp.getNext();
    }

    /* remove the query we added in the beginning */
    this.removeFirstDocument();

    this.sortBySimilarityDesc();
  }

  //This private helper method swaps the docContentField of the two specified DocumentCollectionCells of this DocumentCollection.
  private void swap(DocumentCollectionCell cell1, DocumentCollectionCell cell2) {
    Document tmpDoc = cell1.getDocument();
    double tmpSim = cell1.getQuerySimilarity();

    cell1.setDocument(cell2.getDocument());
    cell1.setQuerySimilarity(cell2.getQuerySimilarity());

    cell2.setDocument(tmpDoc);
    cell2.setQuerySimilarity(tmpSim);
  }

  //This method sorts the documents in this collection descending, according to their similarity.
  private void sortBySimilarityDesc() {
    for (int pass = 1; pass < this.numDocuments(); pass++) {

      DocumentCollectionCell actCell = this.first;

      for (int i = 0; i < this.numDocuments() - pass; i++) {
        /* swap docContentField of cells, if cells are in wrong order */
        if (actCell.getQuerySimilarity() < actCell.getNext().getQuerySimilarity()) {
          swap(actCell, actCell.getNext());
        }

        actCell = actCell.getNext();
      }
    }
  }

  //This method gets a set of all words of all Documents in this collection and 
  //adds every word to every Document in this collection with count 0.
  private void addZeroWordsToDocuments() {
    WordCountsArray allWords = this.allWords();

    DocumentCollectionCell tmp = this.first;

    while (tmp != null) {
      for (int j = 0; j < allWords.size(); j++) {
        String word = allWords.getWord(j);

        tmp.getDocument().getWordCounts().add(word, 0);
      }

      tmp = tmp.getNext();
    }
  }

  //This method returns the similarity of the Document at the specified index
  public double getQuerySimilarity(int index) {
    if (index < 0 || index >= this.numDocuments()) {
      return -1;
    }

    return this.getDocumentCollectionCell(index).getQuerySimilarity();
  }

  //Returns the DocumentCollectionCell that is at the specified index in this DocumentCollection.
  private DocumentCollectionCell getDocumentCollectionCell(int index) {
    if (index < 0 || index >= this.numDocuments()) {
      return null;
    }

    DocumentCollectionCell tmp = this.first;

    int i = 0;
    while (i < index) {
      tmp = tmp.getNext();
      i++;
    }

    return tmp;
  }

  //Returns a string representation of this DocumentCollection using the titles of the documents.
  public String toString() {
    if (this.numDocuments() == 0) {
      return "[]";
    }

    if (this.numDocuments() == 1) {
      return "[" + this.get(0).getTitle() + "]";
    }

    String res = "[";
    for (int i = 0; i < this.numDocuments() - 1; i++) {
      res += this.get(i).getTitle() + ", ";
    }
    res += this.get(this.numDocuments() - 1).getTitle() + "]";
    return res;
  }

  //Returns the number of documents that contain the specified word.
  public int noOfDocumentsContainingWord(String word) {
    if (word == null) {
      return 0;
    }

    int count = 0;

    /* loop over all documents and check if word is contained */
    for (int i = 0; i < this.numDocuments(); i++) {
      WordCountsArray wca = this.get(i).getWordCounts();

      if (wca != null) {
        int index = wca.getIndexOfWord(word);

        if (index != -1 && wca.getCount(index) > 0) {
          count++;
        }
      }
    }

    return count;
  }
}
