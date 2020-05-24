public class Date {
  
  private int day;
  private int month;
  private int year;

  //Constructs a date that represents the current date
  public Date() {
    this.day = Terminal.TODAYS_DAY;
    this.month = Terminal.TODAYS_MONTH;
    this.year = Terminal.TODAYS_YEAR;
  }

  //Constructs a date with the given values day/month/year
  public Date(int day, int month, int year) {
    this.day = 1;
    this.month = 1;
    this.year = 2020;
    this.setDay(day);
    this.setMonth(month);
    this.setYear(year);
  }

  // Returns the day of the date
  public int getDay() {
    return day;
  }

  //Returns the month of the date
  public int getMonth() {
    return month;
  }

  // Returns the year of the date
  public int getYear() {
    return year;
  }

  // Returns the days of this date since 01/01/1970.
  private int daysSince1970() {
    int days = 0;

    // all former years
    for (int i = 1970; i < this.year; i++) {
      days += this.daysInYear(i);
    }

    // all former months in this year
    for (int i = 1; i < this.month; i++) {
      days += this.daysInMonth(i, this.year);
    }

    // all former days in this month
    days += this.day - 1;

    return days;
  }

  // Returns the number of the days in the specified year.
  private int daysInYear(int year) {
    int days = 0;

    for (int month = 1; month <= 12; month++) {
      days += this.daysInMonth(month, year);
    }

    return days;
  }

  //Returns the days between the specified date and the date
  public int getAgeInDaysAt(Date today) {
    return today.daysSince1970() - this.daysSince1970();
  }

  //Returns the full years between the specified date and the date
  public int getAgeInYearsAt(Date today) {
    int age = today.year - this.year;

    if (today.month > this.month) {
      // birthday was earlier this year
      return age;
    } else if (today.month < this.month) {
      // birthday is in a later month of this year
      return age - 1;
    } else {
      // birthday is in this month
      if (today.day >= this.day) {
        // earlier this month or today
        return age;
      } else {
        // later this month
        return age - 1;
      }
    }
  }

  //Returns the number of the days in the specified month in the specified year.
  private int daysInMonth(int month, int year) {
    switch (month) {
    case 1:
    case 3:
    case 5:
    case 7:
    case 8:
    case 10:
    case 12:
      return 31;
    case 4:
    case 6:
    case 9:
    case 11:
      return 30;
    case 2:
      return daysinFebruary(year);
    }

    return -1;
  }

  //Returns the number of days in the February of the specified year. This method considers leap years.
  private int daysinFebruary(int year) {
    if (year % 4 != 0) {
      return 28;
    }

    if ((year % 100 == 0) && (year % 400 != 0)) {
      return 28;
    }

    return 29;
  }

  //Returns a string representation of this date: day/month/year.
  public String toString() {
    return this.day + "/" + this.month + "/" + this.year;
  }

  //Sets the day of this date.
  public void setDay(int day) {
    if (day < 1) {
      this.day = 1;
    } else if (day > daysInMonth(this.month, this.year)) {
      this.day = daysInMonth(this.month, this.year);
    } else {
      this.day = day;
    }
  }

  //Sets the month of this date.
  public void setMonth(int month) {
    if (month < 1) {
      this.month = 1;
    } else if (month > 12) {
      this.month = 12;
    } else {
      this.month = month;
    }

    this.setDay(this.day);
  }

  // Sets the year of this date.
  public void setYear(int year) {
    if (year < 1970) {
      this.year = 1970;
    } else if (year > 2100) {
      this.year = 2100;
    } else {
      this.year = year;
    }

    this.setDay(this.day);
  }

  //Returns true, if this instance and the specified Date are equal.
  public boolean equals(Date date) {
    if (this == date) {
      return true;
    }

    if (date == null) {
      return false;
    }

    return this.day == date.day && this.month == date.month && this.year == date.year;
  }

}
