import tester.Tester;

// a piece of media
interface IMedia {

  // is this media really old?
  boolean isReallyOld();

  // are captions available in this language?
  boolean isCaptionAvailable(String language);

  // a string showing the proper display of the media
  String format();
}
//to abstract qualities many IMedia share

abstract class AMedia implements IMedia {
  String title;
  ILoString captionOptions;

  AMedia(String title, ILoString captionOptions) {
    this.title = title;
    this.captionOptions = captionOptions;
  }
  
  /* fields: 
   *   this.title... String
   *   this.captionOptions ... ILoString
   *   
   * methods:
   *   this.isReallyOld() ... Boolean
   *   this.isCaptionAvailable(String) ... Boolean
   *   this.format() .... String
   *   
   * methods for fields:
   *   
   */
  
  //checks to see if this IMedia was released before 1930
  public boolean isReallyOld() {
    return false;
  }

  //checks to see if the language provided is included in the caption options
  public boolean isCaptionAvailable(String language) {
    return this.captionOptions.inCaptions(language);
  }

  //formats the contents of the IMedia into a nice string
  public abstract String format();
}

// represents a movie
class Movie extends AMedia {
  int year;

  Movie(String title, int year, ILoString captionOptions) {
    super(title, captionOptions);
    this.year = year;
  }
  
  /* fields: 
   *   this.year ... int
   *   
   * methods:
   *   this.isReallyOld() ... Boolean
   *   this.format() .... String
   *   
   * methods for fields:
   *   
   */
  

  //checks to see if this IMedia was released before 1930
  public boolean isReallyOld() {
    return this.year < 1930;
  }
  
  //formats the contents of the IMedia into a nice string
  public String format() {
    return this.title + " (" + this.year + ")";
  }
}

// represents a TV episode
class TVEpisode extends AMedia {
  String showName;
  int seasonNumber;
  int episodeOfSeason;

  TVEpisode(String title, String showName, int seasonNumber, int episodeOfSeason,
      ILoString captionOptions) {
    super(title, captionOptions);
    this.showName = showName;
    this.seasonNumber = seasonNumber;
    this.episodeOfSeason = episodeOfSeason;
  }
  
  /* fields: 
   *   this.showName ... String
   *   this.seasonNumber ... int
   *   this.episodeOfSeason ... int
   *   
   * methods:
   *   this.format() .... String
   *   
   * methods for fields:
   *   
   */
  
  //formats the contents of the IMedia into a nice string
  public String format() {
    return this.showName + " " + this.seasonNumber + "." + this.episodeOfSeason + " - "
        + this.title;
  }
}

// represents a YouTube video
class YTVideo extends AMedia {
  String channelName;

  public YTVideo(String title, String channelName, ILoString captionOptions) {
    super(title, captionOptions);
    this.channelName = channelName;
  }
  
  /* fields: 
   *   this.channelName ... String
   *   
   * methods:
   *   this.format() .... String
   *   
   * methods for fields:
   *   
   */
  
  //formats the contents of the IMedia into a nice string
  public String format() {
    return this.title + " by " + this.channelName;
  }
}

// lists of strings
interface ILoString {

  boolean inCaptions(String that);
}

// an empty list of strings
class MtLoString implements ILoString {

  /* fields: 
   *   
   * methods:
   *   this.inCaptions(String) ... Boolean
   *   
   * methods for fields:
   *   
   */
  //checks to see if that string is in this ILoString
  public boolean inCaptions(String that) {
    return false;
  }
}

// a non-empty list of strings
class ConsLoString implements ILoString {
  String first;
  ILoString rest;

  ConsLoString(String first, ILoString rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /* fields: 
   *   this.first ... String
   *   this.rest ... ILoString
   * methods:
   *   this.inCaptions(String) ... Boolean
   *   
   * methods for fields:
   *   this.first.equals(String) ... Boolean
   *   this.rest.inCaptions(String) ... Boolean
   */
  
  //checks to see if that string is in this ILoString
  public boolean inCaptions(String that) {
    return this.first.equals(that) || this.rest.inCaptions(that);
  }
}

class ExamplesMedia {
  ILoString ilo1 = new ConsLoString("English", new ConsLoString("French", new MtLoString()));
  ILoString ilo2 = new ConsLoString("English", new ConsLoString("Chinese", new MtLoString()));
  ILoString ilo3 = new ConsLoString("Chinese", new ConsLoString("Italian", new MtLoString()));
  ILoString ilo4 = new ConsLoString("Russian", new MtLoString());
  ILoString ilo5 = new ConsLoString("Icelandic",
      new ConsLoString("Zulu", new ConsLoString("English", new MtLoString())));
  ILoString ilo6 = new ConsLoString("Icelandic",
      new ConsLoString("Flemish", new ConsLoString("Cantonese", new MtLoString())));
  ILoString ilo7 = new MtLoString();

  IMedia walle = new Movie("Walle", 2009, ilo1);
  IMedia up = new Movie("Up", 1930, ilo2);
  IMedia oldmovie = new Movie("Old", 1929, ilo7);

  IMedia friends = new TVEpisode("Dinner", "Friends", 3, 23, ilo3);
  IMedia narcos = new TVEpisode("Drugs", "Narcos", 1, 2, ilo4);

  IMedia soccer = new YTVideo("Soccer", "Soccer Girls", ilo5);
  IMedia bball = new YTVideo("Basketball", "BBall Girls", ilo6);
  IMedia hockey = new YTVideo("Hockey", "Hockey Girls", ilo7);

  boolean testisReallyOld(Tester t) {
    return t.checkExpect(this.walle.isReallyOld(), false)
        && t.checkExpect(this.up.isReallyOld(), false)
        && t.checkExpect(this.oldmovie.isReallyOld(), true)
        && t.checkExpect(this.friends.isReallyOld(), false)
        && t.checkExpect(this.bball.isReallyOld(), false);
  }

  boolean testisCaptionAvailable(Tester t) {
    return t.checkExpect(this.walle.isCaptionAvailable("English"), true)
        && t.checkExpect(this.walle.isCaptionAvailable("Chinese"), false)
        && t.checkExpect(this.friends.isCaptionAvailable("Italian"), true)
        && t.checkExpect(this.hockey.isCaptionAvailable("English"), false);
  }

  boolean testinCaptions(Tester t) {
    return t.checkExpect(this.ilo1.inCaptions("English"), true)
        && t.checkExpect(this.ilo5.inCaptions("Zuluu"), false)
        && t.checkExpect(this.ilo7.inCaptions("English"), false);
  }

  boolean testformat(Tester t) {
    return t.checkExpect(this.walle.format(), "Walle (2009)")
        && t.checkExpect(this.friends.format(), "Friends 3.23 - Dinner")
        && t.checkExpect(this.soccer.format(), "Soccer by Soccer Girls");
  }
}