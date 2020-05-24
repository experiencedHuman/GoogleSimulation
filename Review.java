//The class Review represents a review of a Document.
public class Review {
  
  private Author author;
  private Document reviewedDocument;
  private String language;
  private Date releaseDate;
  private int rating;
  private String content;

  public static final int MAX_RATING = 10;
  public static final int MIN_RATING = 0;

  public Review(Author author, Document reviewedDocument, String language, Date releaseDate, int rating, String content) {
    this.setAuthor(author);
    this.setReviewedDocument(reviewedDocument);
    this.setLanguage(language);
    this.setReleaseDate(releaseDate);
    this.setRating(rating);
    this.setContent(content);
  }

  //Returns the author of the review.
  public Author getAuthor() {
    return author;
  }

  //Returns the reviewed document.
  public Document getReviewedDocument() {
    return reviewedDocument;
  }

  //Returns the language the review is written in.
  public String getLanguage() {
    return language;
  }

  //Returns the release date of the review.
  public Date getReleaseDate() {
    return releaseDate;
  }

  //Returns how the reviewed document has been rated.
  public int getRating() {
    return rating;
  }

  //Returns the written review text docContentField.
  public String getContent() {
    return content;
  }


  //Returns a brief string representation of this review.
  public String toString() {
    return this.reviewedDocument.toString() + " is rated " + this.rating + " by " + this.author.toString();
  }

  //Returns the age of this review at the specified date in days.
  public int getAgeAt(Date today) {
    return this.releaseDate.getAgeInDaysAt(today);
  }

  //Sets the Author of this review.
  public void setAuthor(Author author) {
    this.author = author;
  }

  //Sets the language of this review.
  public void setLanguage(String language) {
    if (language == null) {
      this.language = "";
    } else {
      this.language = language;
    }
  }

  //Sets the docContentField of this review.
  public void setContent(String content) {
    if (content == null) {
      this.content = "";
    } else {
      this.content = content;
    }
  }

  //Sets the reviewed Document of this review.
  public void setReviewedDocument(Document doc) {
    this.reviewedDocument = doc;
  }


  //Sets the release date Date of this review.
  public void setReleaseDate(Date date) {
    this.releaseDate = date;
  }

  //Sets the rating of this review.
  public void setRating(int rating) {
    if (rating < Review.MIN_RATING) {
      this.rating = Review.MIN_RATING;
    } else if (rating > Review.MAX_RATING) {
      this.rating = Review.MAX_RATING;
    } else {
      this.rating = rating;
    }
  }

  //Returns true, if this instance and the specified Review equal.
  public boolean equals(Review review) {
    if (this == review) {
      return true;
    }

    if (review == null) {
      return false;
    }

    return this.language.equals(review.language) && this.rating == review.rating
        && ((this.author != null && this.author.equals(review.author))
            || (this.author == null && review.author == null))
        && ((this.releaseDate != null && this.releaseDate.equals(review.releaseDate))
            || (this.releaseDate == null && review.releaseDate == null))
        && ((this.reviewedDocument != null && this.reviewedDocument.equals(review.reviewedDocument))
            || (this.reviewedDocument == null && review.reviewedDocument == null));
  }
}
