public class WordCountsArray {
  private WordCount[] wordCounts;
  private int actualSize;
  private int maxSize;

  // Creates a new instance of this class.
  public WordCountsArray(int maxSize) {
    if (maxSize < 0) {
      this.maxSize = 0;
    } else {
      this.maxSize = maxSize;
    }

    this.actualSize = 0;
    this.wordCounts = new WordCount[this.maxSize];
  }

  // Adds the specified word with the specified count 
  public void add(String word, int count) {
    if (word == null || word.equals("")) {
      return;
    }

    if (count < 0) {
      return;
    }

    int index = getIndexOfWord(word.toLowerCase());

    if (index == -1) {
      if (actualSize == maxSize) {
        this.doubleSize();
      }

      this.wordCounts[actualSize] = new WordCount(word.toLowerCase(), count);
      this.actualSize++;
    } else {
      this.wordCounts[index].incrementCount(count);
    }
  }

  // Determines, whether the words of this instance and the words in the specified WordCountsArray are equal
  private boolean wordsEqual(WordCountsArray wca) {
    if (this == wca) {
      return true;
    }

    if ((wca == null) || (this.size() != wca.size())) {
      return false;
    }

    // compare every single word at every position
    for (int i = 0; i < this.size(); i++) {
      if (!this.getWord(i).equals(wca.getWord(i))) {
        return false;
      }
    }

    return true;
  }

  // Calculate the scalar product of the word counts of this instance and the word counts of the specified WordCountsArray.
  private double scalarProduct(WordCountsArray wca) {
    if (wca == null) {
      return 0;
    }
    if (this.size() != wca.size()) {
      return 0;
    }

    if ((this != wca) && !this.wordsEqual(wca)) {
      return 0;
    }

    double scalarProduct = 0;

    for (int i = 0; i < this.size(); i++) {
      scalarProduct += this.getCount(i) * wca.getCount(i);
    }

    return scalarProduct;
  }

  // Sorts the WordCount-objects
   public void sort() {
    this.doBubbleSort();
  }

  //Sorts the WordCount objects managed by this instance with the bubble sort algorithm.
  private void doBubbleSort() {
    for (int pass = 1; pass < this.actualSize; pass++) {
      for (int i = 0; i < this.actualSize - pass; i++) {
        if (this.getWord(i).compareTo(this.getWord(i + 1)) > 0) {
          WordCount tmp = this.wordCounts[i];
          this.wordCounts[i] = this.wordCounts[i + 1];
          this.wordCounts[i + 1] = tmp;
        }
      }
    }
  }

  private static int bucketAt(String word, int maxLength, int index) {
    int iPrime = maxLength - index - 1;
    if(iPrime >= word.length())
      return 0;
    return word.charAt(iPrime) - 'a' + 1;
  }
  
  // Sorts the WordCounts-objects managed by this instance with the bucket sort algorithm.
  private void doBucketSort() {
    // one bucket for every character
    WordCountsArray[] buckets = new WordCountsArray[27];

    for (int i = 0; i < buckets.length; i++)
      buckets[i] = new WordCountsArray(this.actualSize / buckets.length);

    int maxLength = 0;
    for (int i = 0; i < this.actualSize; i++) {
      maxLength = Math.max(maxLength, this.getWord(i).length());
    }

    // sort words into buckets
    for (int i = 0; i < this.actualSize; i++) {
      String word = this.getWord(i);
      int charIndex = bucketAt(word, maxLength, 0);
      if (charIndex >= 0) {
        int count = this.getCount(i);
        buckets[charIndex].add(word, count);
      }
    }

    for (int pos = 1; pos < maxLength; pos++) {
      WordCountsArray[] bucketsNew = new WordCountsArray[buckets.length];
      for (int i = 0; i < bucketsNew.length; i++)
        bucketsNew[i] = new WordCountsArray(this.actualSize / buckets.length);
      for (int i = 0; i < buckets.length; i++) {
        for (int j = 0; j < buckets[i].size(); j++) {
          String word = buckets[i].getWord(j);
          int charIndex = bucketAt(word, maxLength, pos);
          int count = buckets[i].getCount(j);
          bucketsNew[charIndex].add(word, count);
        }
      }
      buckets = bucketsNew;
    }

    WordCount[] newWordCounts = new WordCount[this.actualSize];

    int j = 0;
    for (int bucket = 0; bucket < buckets.length; bucket++) {
      for (int i = 0; i < buckets[bucket].size(); i++) {
        newWordCounts[j] = buckets[bucket].get(i);
        j++;
      }
    }

    this.wordCounts = newWordCounts;
    this.maxSize = this.actualSize;
  }

  //Private helper method that returns the WordCount-object at the specified index.
  private WordCount get(int index) {
    if (index < 0 || index >= this.actualSize) {
      return null;
    }

    return this.wordCounts[index];
  }

  // Calculate the similarity of this instance and the specified WordCountsArray
  public double computeSimilarity(WordCountsArray wca) {
    if (wca == null) {
      return 0;
    }

    double scalarProductThis = this.scalarProduct(this);
    double scalarProductWca = wca.scalarProduct(wca);

    double scalarProduct = 0;

    if (scalarProductThis != 0 && scalarProductWca != 0) {
      scalarProduct = this.scalarProduct(wca) / (Math.sqrt(scalarProductThis * scalarProductWca));
    }

    return scalarProduct;
  }

  // Calculate a complex similarity of this instance and the specified WordCountsArray based on the specified DocumentCollection.
  public double computeSimilarity(WordCountsArray wca, DocumentCollection dc) {
    if (wca == null || dc == null) {
      return 0;
    }
    wca.scalarProduct(wca, dc);

    double scalarProduct = 0;

    scalarProduct = this.scalarProduct(wca, dc);

    return scalarProduct;
  }

  // Returns the number of words 
  public int size() {
    return this.actualSize;
  }

  // Returns the word at the position index of the WordCount-Array.
  public String getWord(int index) {
    if (index < 0 || index >= this.actualSize) {
      return null;
    }

    return this.wordCounts[index].getWord();
  }

  // Returns the count of the word at position index of the WordCount-Array.
  public int getCount(int index) {
    if (index < 0 || index >= this.actualSize) {
      return -1;
    }

    return this.wordCounts[index].getCount();
  }

  // Returns the index of the internal WordCount-Array where the specified word is managed.
  public int getIndexOfWord(String word) {
    if (word == null || word.equals("")) {
      return -1;
    }

    for (int i = 0; i < this.actualSize; i++) {
      if (this.wordCounts[i].getWord().equals(word)) {
        return i;
      }
    }

    return -1;
  }

  // Sets the count of the word at position index of the WordCount-Array to the specified count.
  public void setCount(int index, int count) {
    if (index < 0 || index >= this.actualSize) {
      return;
    }

    if (count < 0) {
      this.wordCounts[index].setCount(0);
    } else {
      this.wordCounts[index].setCount(count);
    }
  }

  // Doubles the number of manageable WordCount-objects.
  private void doubleSize() {
    this.maxSize = this.maxSize * 2;

    if (this.maxSize <= 0) {
      this.maxSize = 1;
    }

    WordCount[] newWordCounts = new WordCount[this.maxSize];

    for (int i = 0; i < this.wordCounts.length; i++) {
      newWordCounts[i] = this.wordCounts[i];
    }

    this.wordCounts = newWordCounts;
  }

  // Returns true, if this instance and the specified WordCountsArray are equal.
  public boolean equals(WordCountsArray wca) {
    if (this == wca) {
      return true;
    }

    if ((wca == null) || (this.size() != wca.size())) {
      return false;
    }

    for (int i = 0; i < this.size(); i++) {
      if (!this.getWord(i).equals(wca.getWord(i)) || this.getCount(i) != wca.getCount(i)) {
        return false;
      }
    }

    return true;
  }

  // This private helper method calculates the normalized weights of the words 
  private void calculateNormalizedWeights(DocumentCollection dc) {
    if (dc == null) {
      return;
    }
    this.calculateWeights(dc);
    double norm = 0;

    for (int i = 0; i < this.size(); i++) {
      norm += this.wordCounts[i].getWeight() * this.wordCounts[i].getWeight();
    }

    if (norm > 0) {
      norm = Math.sqrt(norm);

      for (int i = 0; i < this.size(); i++) {
        this.wordCounts[i].setNormalizedWeight(this.wordCounts[i].getWeight() / norm);
      }
    } else {
       for (int i = 0; i < this.size(); i++) {
        this.wordCounts[i].setNormalizedWeight(0);
      }
    }
  }

  // This private helper method calculates the weights of the words according to the specified DocumentCollection.
  private void calculateWeights(DocumentCollection dc) {
    if (dc == null) {
      return;
    }

    // loop over all words, calculate their weights and store them
    for (int i = 0; i < this.size(); i++) {
      int noOfDocumentsContainingWord = dc.noOfDocumentsContainingWord(this.getWord(i));

        if (noOfDocumentsContainingWord != 0) {
        double invDocFreq = Math.log((dc.numDocuments() + 1) / (double) dc.noOfDocumentsContainingWord(this.getWord(i)));
        wordCounts[i].setWeight(this.getCount(i) * invDocFreq);
      } else {
        wordCounts[i].setWeight(0);
      }
    }
  }

  // Calculate the complex scalar product of the normalized weights of this instance and the normalized weights 
  // of the specified WordCountsArray. The scalar product is calculated according to the specified DocumentCollection
  private double scalarProduct(WordCountsArray wca, DocumentCollection dc) {
    if (wca == null || dc == null) { System.out.println("(wca == null || dc == null)");
      return 0;
    }

    if (this.size() != wca.size()) { System.out.println("(this.size() != wca.size()): " + size() + " != " + wca.size());
      return 0;
    }

    if ((this != wca) && !this.wordsEqual(wca)) { System.out.println("((this != wca) && !this.wordsEqual(wca))");
      return 0;
    }
    this.calculateNormalizedWeights(dc);
    double scalarProduct = 0;

    for (int i = 0; i < this.size(); i++) {
      scalarProduct += this.wordCounts[i].getNormalizedWeight() * wca.wordCounts[i].getNormalizedWeight();
    }

    return scalarProduct;
  }
}
