//This class represents a linked document.
public class LinkedDocument extends Document {
  //the prefix of the links in the document text
  public static final String LINK_PREFIX = "link:";

  private final String id;

  //the LinkedDocument this instance links to
  private LinkedDocumentCollection outgoingLinks;

  //the LinkedDocuments that link to this instance
  private LinkedDocumentCollection incomingLinks;

  //the LinkedDocument-IDs where this instance links to
  private String[] outgoingIDs;

  //Creates a new instance of this class.
  public LinkedDocument(String title, String language, String description, Date releaseDate, Author author, String text,
      String id) {
    super(title, language, description, releaseDate, author, text);

    this.id = id;
    this.incomingLinks = new LinkedDocumentCollection();

    this.outgoingIDs = this.findOutgoingIDs(text);
    this.outgoingLinks = null;
    this.setLinkCountZero();
  }

  //Returns a new LinkedDocument instance, that is created from the specified file.
  public static LinkedDocument createLinkedDocumentFromFile(String fileName) {
    String[] fileContent = Terminal.readFile(fileName);

    if (fileContent != null && fileContent.length >= 2) {
      String title = fileContent[0];
      String text = fileContent[1];

      return new LinkedDocument(title, "", "", null, null, text, fileName);
    } else {
      return null;
    }
  }


  //Searches the WordCountsArray of this instance for links (beginning with "LINK_PREFIX") and sets their count to 0
  private void setLinkCountZero() {
    WordCountsArray wca = this.getWordCounts();
    for (int i = 0; i < wca.size(); i++) {
      if (wca.getWord(i).startsWith(LinkedDocument.LINK_PREFIX)) {
        wca.setCount(i, 0);
      }
    }
  }

  //Returns true, if this instance and the specified Document equal.
  @Override
  public boolean equals(Document doc) {
    if (doc instanceof LinkedDocument) {
      return this.id.equals(((LinkedDocument) doc).id);
    } else {
      return super.equals(doc);
    }
  }

  //Returns the identifier of this instance.
  public String getID() {
    return this.id;
  }

  //This private helper method creates a new LinkedDocument for every ID and 
  //adds it to the LinkedDocumentCollection of outgoing links, if it is not a self-link.
  private void createOutgoingDocumentCollection() {
    this.outgoingLinks = new LinkedDocumentCollection();
    for (int i = 0; i < this.outgoingIDs.length; i++) {
      LinkedDocument newDoc = LinkedDocument.createLinkedDocumentFromFile(this.outgoingIDs[i]);
      if (!this.equals(newDoc)) {
        this.outgoingLinks.appendDocument(newDoc);
      }
    }
  }

  // This private helper method creates a new LinkedDocument for every ID and 
  //adds it to the LinkedDocumentCollection of outgoing links, if it is not a self-link.
  private void createOutgoingDocumentCollection(LinkedDocumentCollection cache) {
    this.outgoingLinks = new LinkedDocumentCollection();

    LinkedDocument newDoc;
    for (int i = 0; i < this.outgoingIDs.length; i++) {
      newDoc = cache.findByID(this.outgoingIDs[i]);
      if (newDoc == null)
        newDoc = LinkedDocument.createLinkedDocumentFromFile(this.outgoingIDs[i]);
      if (!this.equals(newDoc)) {
        this.outgoingLinks.appendDocument(newDoc);
      }
    }
  }

  //This method returns a LinkedDocumentCollection that contains all LinkedDocuments that this instance links to.
  public LinkedDocumentCollection getOutgoingLinks() {
    if (this.outgoingLinks == null) {
      this.createOutgoingDocumentCollection();
    }

    return this.outgoingLinks;
  }

  //This method returns a LinkedDocumentCollection that contains all LinkedDocuments that this instance links to.
  public LinkedDocumentCollection getOutgoingLinks(LinkedDocumentCollection cache) {
    if (this.outgoingLinks == null) {
      this.createOutgoingDocumentCollection(cache);
    }

    return this.outgoingLinks;
  }

  //This private helper method finds the outgoing links in the specified text.
  private String[] findOutgoingIDs(String text) {
    if (text == null) {
      return null;
    }

    String textCopy = new String(text);

    /* the number of words in the WordCountsArray is sufficient */
    String[] tmpIDs = new String[this.getWordCounts().size()];

    /* get the next index of the LINK_PREFIX */
    int index = textCopy.indexOf(LinkedDocument.LINK_PREFIX);

    int noOfIDs = 0;

    while (index != -1) {
      /* divide the text at the found index */
      String strBeforeLink = textCopy.substring(0, index);
      String strWithLink = textCopy.substring(index);

      int endIndex = strWithLink.indexOf(' ');

      String link;

      /* if endIndex is not found, we are looking at the last word */
      if (endIndex == -1) {
        link = strWithLink.substring(0);
        textCopy = strBeforeLink;
      } else {
        /* otherwise, there are more words */
        link = strWithLink.substring(0, endIndex);
        textCopy = strBeforeLink + strWithLink.substring(endIndex + 1);
      }

      /* add the ID to array */
      tmpIDs[noOfIDs] = link.substring(LinkedDocument.LINK_PREFIX.length());
      noOfIDs++;

      /* get next index for next loop */
      index = textCopy.indexOf(LinkedDocument.LINK_PREFIX);
    }

    /* create new array with the size according to the actual number of found IDs */
    String[] ids = new String[noOfIDs];

    for (int i = 0; i < noOfIDs; i++) {
      ids[i] = tmpIDs[i];
    }

    return ids;
  }

  //Adds the specified LinkedDocument to the LinkedDocumentCollection that represents 
  // the LinkedDocuments that link to this instance.
  public void addIncomingLink(LinkedDocument incomingLink) {
    if (!this.equals(incomingLink)) { 
      this.incomingLinks.appendDocument(incomingLink);
    }
  }

  //Returns a LinkedDocumentCollection of LinkedDocuments that link to this instance.
  public LinkedDocumentCollection getIncomingLinks() {
    return this.incomingLinks;
  }
}
