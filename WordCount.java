//This class represents a word and its count.
public class WordCount {
  
  private String word;
  private int count;
  private double weight;
  private double normalizedWeight;

  //public constructor-creates instance of this class
  public WordCount(String word) {
    this(word, 0);
  }

  //Creates an instance of this class representing the specified word with its count set to count.
  public WordCount(String word, int count) {
    if (word == null) {
      this.word = "";
    } else {
      this.word = word;
    }

    this.setCount(count);
  }

  //Returns the represented word.
  public String getWord() {
    return word;
  }

  //Returns the count of the represented word
  public int getCount() {
    return count;
  }

  //Sets the count of the represented word.
  public void setCount(int count) {
    if (count < 0) {
      this.count = 0;
    } else {
      this.count = count;
    }
  }

  //Increases the count of the represented word by 1
  public int incrementCount() {
    this.count++;
    return this.count;
  }

  //Increases the count of the represented word by n
  public int incrementCount(int n) {
    if (n > 0) {
      this.count += n;
    }
    return this.count;
  }

  //Returns true, if this instance and the specified WordCount instance are equal.
  public boolean equals(WordCount wordCount) {
    if (wordCount == null)
      return false;
    return this.count == wordCount.count && this.word.equals(wordCount.word);
  }

  //Returns the weight of this word.
  public double getWeight() {
    return weight;
  }

  //Sets the weight of this word.
  public void setWeight(double weight) {
    this.weight = weight;
  }

  //Returns the normalized weight of this word
  public double getNormalizedWeight() {
    return normalizedWeight;
  }

  //Sets the normalized weight of this word
  public void setNormalizedWeight(double normalizedWeight) {
    this.normalizedWeight = normalizedWeight;
  }
}
