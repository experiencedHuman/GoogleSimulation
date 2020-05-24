//This class represents a document.
public class Document {
  
  private String title;
  private String language;
  private String description;
  private Date releaseDate;
  private Author author;

  /**
   * Most common german suffices
   */
  public static final String[] SUFFICES = { "ab", "al", "ant", "artig", "bar", "chen", "ei", "eln", "en", "end", "ent",
  "er", "fach", "fikation", "fizieren", "fähig", "gemäß", "gerecht", "haft", "haltig", "heit", "ieren", "ig", "in",
  "ion", "iren", "isch", "isieren", "isierung", "ismus", "ist", "ität", "iv", "keit", "kunde", "legen", "lein",
  "lich", "ling", "logie", "los", "mal", "meter", "mut", "nis", "or", "sam", "schaft", "tum", "ung", "voll", "wert",
  "würdig", "ie" };

  //the words of this document and their counts
  private WordCountsArray wordCounts;

  //Constructs a document with the given values.
  public Document(String title, String language, String description, Date releaseDate, Author author, String content) {
    /* use this methods, just in case the value of the parameters is null */
    this.setTitle(title);
    this.setLanguage(language);
    this.setDescription(description);

    this.releaseDate = releaseDate;
    this.author = author;
    
    this.addContent(content);
  }

  //Returns the docTitleField of the document
  public String getTitle() {
    return title;
  }

  //Returns the language the document is written in.
  public String getLanguage() {
    return language;
  }

  //Returns a short description of the document.
  public String getDescription() {
    return description;
  }

  public Date getReleaseDate() {
    return releaseDate;
  }

  public Author getAuthor() {
    return author;
  }

  //Returns a WordCountsArray representing the words and its counts of this document
  public WordCountsArray getWordCounts() {
    return this.wordCounts;
  }

  //Returns a brief string representation of this document
  public String toString() {
    return this.title + " by " + this.author.toString();
  }

  //Returns the age of this document at the specified date in days
  public int getAgeAt(Date today) {
    return this.releaseDate.getAgeInDaysAt(today);
  }

  //Sets the docTitleField of the document.
  public void setTitle(String title) {
    if (title == null) {
      this.title = "";
    } else {
      this.title = title;
    }
  }

  //Sets the language of the document.
  public void setLanguage(String language) {
    if (language == null) {
      this.language = "";
    } else {
      this.language = language;
    }
  }

  public void setDescription(String description) {
    if (description == null) {
      this.description = "";
    } else {
      this.description = description;
    }
  }

  public void setReleaseDate(Date releaseDate) {
    this.releaseDate = releaseDate;
  }

  public void setAuthor(Author author) {
    this.author = author;
  }

  //Splits the specified text into its single words.
  private static String[] tokenize(String content) {
    int wordCount = 0;

    for (int i = 0; i < content.length(); i++) {
      if (content.charAt(i) == ' ') {
        wordCount++;
      }
    }

    // there is always one word more than there are spaces
    wordCount++;

    // the resulting array
    String[] words = new String[wordCount];

    String word = "";
    int wordIndex = 0;

    for (int i = 0; i <= content.length(); i++) {
      if (i == content.length() || content.charAt(i) == ' ') {
        if (word.length() > 0) {
          words[wordIndex] = word;
          wordIndex++;

          word = "";
        }
      } else {
        word = word + content.charAt(i);
      }
    }

    return words;
  }

  private void addContent(String content) {
    String[] words = Document.tokenize(content);

    this.wordCounts = new WordCountsArray(0);

    for (int i = 0; i < words.length; i++) {
      String word = words[i];

      String suffix = Document.findSuffix(word);
      word = Document.cutSuffix(word, suffix);

      this.wordCounts.add(word, 1);
    }
  }

  private static boolean sufficesEqual(String word1, String word2, int n) {
    /* if n is too large, last n chars are not equal */
    if (n > word1.length() || n > word2.length()) {
      return false;
    }

    boolean isEqual = true;
    int i = 0;

    while (isEqual && i < n) {
      if (word1.charAt(word1.length() - 1 - i) != word2.charAt(word2.length() - 1 - i)) {
        isEqual = false;
      }
      i++;
    }

    return isEqual;
  }

  private static String findSuffix(String word) {
    if (word == null || word.equals("")) {
      return null;
    }

    String suffix = "";
    String suffixHit = "";
    int i = 0;

    while (i < Document.SUFFICES.length) {
      suffix = Document.SUFFICES[i];

      if (sufficesEqual(word, suffix, suffix.length())) {
        if (suffixHit.length() < suffix.length()) {
          suffixHit = suffix;
        }
      }

      i++;
    }
    return suffixHit;
  }

  private static String cutSuffix(String word, String suffix) {
    if (suffix == null || suffix.equals("")) {
      return word;
    }

    if (word == null) {
      return null;
    }

    if (!sufficesEqual(word, suffix, suffix.length())) {
      return word;
    }

    String wordWithoutSuffix = "";

    for (int i = 0; i < word.length() - suffix.length(); i++) {
      wordWithoutSuffix = wordWithoutSuffix + word.charAt(i);
    }

    return wordWithoutSuffix;
  }

  //Returns true, if this instance and the specified Document equal.
  public boolean equals(Document document) {
    if (this == document) {
      return true;
    }

    if (document == null) {
      return false;
    }

    return this.title.equals(document.title) && this.language.equals(document.language)
        && this.description.equals(document.description)
        && ((this.author != null && this.author.equals(document.author))
            || (this.author == null && document.author == null))
        && ((this.releaseDate != null && this.releaseDate.equals(document.releaseDate))
            || (this.releaseDate == null && document.releaseDate == null))
        && ((this.wordCounts != null && this.wordCounts.equals(document.getWordCounts()))
            || (this.wordCounts == null && document.getWordCounts() == null));
  }
}
