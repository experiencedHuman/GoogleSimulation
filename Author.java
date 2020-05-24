//This class represents an author of a Document or a Review
public class Author {
  
  private String firstName;
  private String lastName;
  private Date birthday;
  private String residence;
  private String email;

  //constructs an author with the given values.
  public Author(String firstName, String lastName, Date birthday, String residence, String email) {
    /* use this methods, just in case the value of the parameters is null */
    this.setFirstName(firstName);
    this.setLastName(lastName);
    this.setResidence(residence);
    this.setEmail(email);

    this.birthday = birthday;
  }

  //Returns the first name of the author.
  public String getFirstName() {
    return firstName;
  }

  //Returns the last name of the author.
  public String getLastName() {
    return lastName;
  }

  //Returns the birthday of the author.
  public Date getBirthday() {
    return birthday;
  }

  //Returns the residence of the author.
  public String getResidence() {
    return residence;
  }

  //Returns the email address of the author.
  public String getEmail() {
    return email;
  }

  //Returns a string representation of this author.
  public String toString() {
    return this.firstName + " " + this.lastName;
  }

  //Returns the contact information of the author.
  public String getContactInformation() {
    return this.firstName + " " + this.lastName + Terminal.NEWLINE + "<" + this.email + ">" + Terminal.NEWLINE
        + this.residence;

  }

  //Returns the age of this author at the specified date in years.
  public int getAgeAt(Date today) {
    return this.birthday.getAgeInYearsAt(today);
  }

  //Sets the first name of the author.
  public void setFirstName(String firstName) {
    if (firstName == null) {
      this.firstName = "";
    } else {
      this.firstName = firstName;
    }
  }

  //Sets the last name of the author.
  public void setLastName(String lastName) {
    if (lastName == null) {
      this.lastName = "";
    } else {
      this.lastName = lastName;
    }
  }

  //Sets the residence of the author.
  public void setResidence(String residence) {
    if (residence == null) {
      this.residence = "";
    } else {
      this.residence = residence;
    }
  }

  //Sets the email address of the author.
  public void setEmail(String email) {
    if (email == null || !email.contains("@")) {
      this.email = "experiencedPenguin@penguins.com";
    } else {
      this.email = email;
    }
  }

  //Returns true, if this instance and the specified Author are equal.
  public boolean equals(Author author) {
    if (this == author) {
      return true;
    }

    if (author == null) {
      return false;
    }

    return this.firstName.equals(author.firstName) && this.lastName.equals(author.lastName)
        && this.residence.equals(author.residence) && this.email.equals(author.email)
        && ((this.birthday != null && this.birthday.equals(author.birthday))
            || (this.birthday == null && author.birthday == null));
  }
}
