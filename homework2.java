import tester.Tester;

class Place {
  String name;
  ILoFeature features;
  
  Place(String name, ILoFeature features) {
    this.name = name;
    this.features = features;
  }
  
  /* fields: 
   *   this.name ... String
   *   this.features ... ILoFeature
   *   
   * methods:
   *   this.restCount() ... int
   *   this.restSum() ... double
   *   this.foodinessRating() ... double
   *   this.restaurantInfo() ... String
   *   
   * methods for fields:
   *   this.features.numOfRest() ... int
   *   this.features.sumRestRating() ... double
   *   this.features.info() ... String
   */
  
  // counts number of restaurants in a place
  public int restCount() {
    return this.features.numOfRest();
  }
  
  // sums all the ratings of every restaurant in a place
  public double restSum() {
    return this.features.sumRestRating();
  }
  
  // calculates average of all restaurants in a place
  public double foodinessRating() {
    if (this.features.numOfRest() == 0 & this.features.sumRestRating() == 0.0) {
      return 0;
    }
    else {
      return this.restSum() / this.restCount();
    }
  }
  
  // appends all the restaurants and their type in a place
  public String restaurantInfo() {
    return this.features.info();
  }
}

interface ILoFeature {
  
  int numOfRest();
  
  double sumRestRating();
  
  String info();
}

class MtLoFeature implements ILoFeature {

  /* fields:
   *   none
   *   
   * methods:
   *   this.numOfRest() ... int
   *   this.sumRestRating() ... double
   *   this.info() ... String
   *   
   * methods for fields:
   *   none
   * 
   */
  
  // returns number of restaurants
  public int numOfRest() {
    return 0;
  }

  // returns sum of restaurant ratings
  public double sumRestRating() {
    return 0.0;
  }

  // returns restaurant info
  public String info() {
    return "";
  }
}

class ConsLoFeature implements ILoFeature {
  IFeature first;
  ILoFeature rest;
  
  ConsLoFeature(IFeature first, ILoFeature rest) {
    this.first = first;
    this.rest = rest;
  }
  
  /* fields: 
   *   this.first ... IFeature
   *   this.rest ... ILoFeature
   *   
   * methods:
   *   this.numOfRest() ... int
   *   this.sumRestRating() ... double
   *   this.info() ... String
   *   
   * methods for fields:
   *   this.first.restRating ... double
   *   this.first.numbRest() ... int
   *   this.first.restType() ... string
   *   this.rest.numOfRest() ... int
   *   this.rest.sumRestRating() ... double
   *   this.rest.info() ... String
   */

  // returns number of restaurants
  public int numOfRest() {
    return this.first.numbRest() + this.rest.numOfRest();
  }

  // adds all restaurant ratings
  public double sumRestRating() {
    return this.first.restRating() + this.rest.sumRestRating();
  }

  // produces string of all restaurants along with its type separated by a comma
  public String info() {
    if (this.rest.info().equals("")) {
      return this.first.restType();
    }
    else if (this.first.restType().equals("")) {
      return this.rest.info();
    }
    else {    
      return this.first.restType() + ", " + this.rest.info();
    }
  }
}

interface IFeature {
  double restRating();
  
  int numbRest();

  String restType();
}

class Restaurant implements IFeature {
  String name; 
  String type;
  double averageRating;
  
  Restaurant(String name, String type, double averageRating) {
    this.name = name;
    this.type = type;
    this.averageRating = averageRating;
  }
  /* fields: 
   *   this.name ... String
   *   this.type ... String
   *   this.averageRating ... double
   *   
   * methods:
   *   this.restRating() ... double
   *   this.restType() ... String
   *   this.numbRest() ... int
   *   
   * methods for fields:
   *   
   * 
   */

  // returns rating of feature
  public double restRating() {
    return this.averageRating;
    
  }

  // returns string of restaurant name and type in brackets
  public String restType() {
    return this.name + " (" + this.type + ")";
  }

  // returns number of restaurants
  public int numbRest() {
    return 1;
  }
}

class Venue implements IFeature {
  String name; 
  String type;
  int capacity;
  
  Venue(String name, String type, int capacity) {
    this.name = name;
    this.type = type;
    this.capacity = capacity;
  }
  
  /* fields: 
   *   this.name ... String
   *   this.type ... String
   *   this.capacity ... int
   *   
   * methods:
   *   this.restRating() ... double
   *   this.restType() ... String
   *   this.numbRest() ... int
   *   
   * methods for fields:
   *   none
   * 
   */

  // returns rating of feature
  public double restRating() {
    return 0.0;
  }
  
  // returns string of restaurant name and type in brackets
  public String restType() {
    return "";
  }

  // returns number of restaurants
  public int numbRest() {
    return 0;
  }
}

// this class is this

class ShuttleBus implements IFeature {
  String name;
  Place destination;

  ShuttleBus(String name, Place destination) {
    this.name = name;
    this.destination = destination;
  }
  
  /* fields: 
   *   this.name ... String
   *   this.destination ... Place
   *   
   * methods:
   *   this.restRating() ... double
   *   this.restType() ... String
   *   this.numbRest() ... int
   * methods for fields:
   *   none
   * 
   */
  
  

  //returns rating of feature
  public double restRating() {
    return this.destination.restSum();
  }
  
  // returns string of restaurant name and type in brackets
  public String restType() {
    return this.destination.restaurantInfo();
  }

  // returns number of restaurants
  public int numbRest() {
    return this.destination.restCount();
  }
 
}

class ExamplesPlaces {
  
  IFeature tdGarden = new Venue("TD Garden","stadium", 19580);
  IFeature theDailyCatch = new Restaurant("The Daily Catch", "Sicilian", 4.4);
  ILoFeature list6 = new ConsLoFeature(this.theDailyCatch, new MtLoFeature());
  ILoFeature northEndLoF = new ConsLoFeature(this.tdGarden, this.list6);
  Place northEnd = new Place("North End", this.northEndLoF);

  IFeature freshmen15 = new ShuttleBus("Freshmen-15", northEnd);
  IFeature borderCafe = new Restaurant("Border Cafe", "Tex-Mex", 4.5);
  IFeature harvardStadium = new Venue("Harvard Stadium","football", 30323);
  ILoFeature list7 = new ConsLoFeature(this.harvardStadium, new MtLoFeature());
  ILoFeature list8 = new ConsLoFeature(this.borderCafe, this.list7);
  ILoFeature harvardLoF = new ConsLoFeature(this.freshmen15, this.list8);
  Place harvard = new Place("Harvard", this.harvardLoF);
  
  IFeature littleItalyExpress = new ShuttleBus("Little Italy Express", northEnd);
  IFeature reginasPizza = new Restaurant("Regina's Pizza", "pizza", 4.0);
  IFeature crimsonCruiser = new ShuttleBus("Crimson Cruiser", harvard);
  IFeature bostonCommon = new Venue("Boston Common","public", 150000);
  ILoFeature list3 = new ConsLoFeature(this.bostonCommon, new MtLoFeature());
  ILoFeature list4 = new ConsLoFeature(this.crimsonCruiser, list3);
  ILoFeature list5 = new ConsLoFeature(this.reginasPizza, list4);
  ILoFeature SSLoF = new ConsLoFeature(this.littleItalyExpress, list5);
  Place southStation = new Place("South Station", this.SSLoF);

  IFeature sarkuJapan = new Restaurant("Sarku Japan", "teriyaki", 3.9);
  IFeature starbucks = new Restaurant("Starbucks", "coffee", 4.1);
  IFeature bridgeShuttle = new ShuttleBus("bridge shuttle", southStation);
  ILoFeature list1 = new ConsLoFeature(this.bridgeShuttle, new MtLoFeature());
  ILoFeature list2 = new ConsLoFeature(this.starbucks, this.list1);
  ILoFeature cambridgeLoF = new ConsLoFeature(this.sarkuJapan, this.list2);
  Place cambridgeSide = new Place("CambridgeSide Galleria", this.cambridgeLoF);
    
  // new map
  
  IFeature venue2 = new Venue("venue1","stadium", 10);
  ILoFeature list13 = new ConsLoFeature(this.venue2, new MtLoFeature());
  Place place2 = new Place("place2", this.list13);
  
  IFeature venue1 = new Venue("venue1","stadium", 10);
  IFeature rest1 = new Restaurant("rest1", "food", 4.6);
  IFeature rest2 = new Restaurant("rest2", "food", 4.7);
  IFeature bus1 = new ShuttleBus("bus1", place2);
  ILoFeature list10 = new ConsLoFeature(this.venue1, new MtLoFeature());
  ILoFeature list11 = new ConsLoFeature(this.rest1, this.list10);
  ILoFeature list12 = new ConsLoFeature(this.rest2, this.list11);
  ILoFeature list14 = new ConsLoFeature(this.bus1, this.list12);
  Place place1 = new Place("place1", this.list14);
  
  IFeature bus2 = new ShuttleBus("bus2", this.place1);
  ILoFeature list15 = new ConsLoFeature(this.bus2, new MtLoFeature());
  Place place3 = new Place("place3", this.list15);
  
  IFeature bus3 = new ShuttleBus("bus3", this.place3);
  IFeature bus4 = new ShuttleBus("bus4", this.place1);
  ILoFeature list16 = new ConsLoFeature(this.bus3, new MtLoFeature());
  ILoFeature list17 = new ConsLoFeature(this.bus4, this.list16);
  Place place4 = new Place("place4", this.list17);
  
  ILoFeature mt = new MtLoFeature();
  Place mtPlace = new Place("mt", mt);
  
  boolean testnumbRest(Tester t) {
    return t.checkExpect(this.rest1.numbRest(), 1) &&
        t.checkExpect(this.bus1.numbRest(), 0) &&
        t.checkExpect(this.venue1.numbRest(), 0) &&
        t.checkExpect(this.bus2.numbRest(), 2);
  }
  
  boolean testRestRating(Tester t) {
    return t.checkExpect(this.rest1.restRating(), 4.6) &&
        t.checkExpect(this.bus1.restRating(), 0.0) &&
        t.checkExpect(this.venue1.restRating(), 0.0) &&
        t.checkExpect(this.bus2.restRating(), 9.3);
  }
  
  boolean testCount(Tester t) {
    return t.checkExpect(this.northEnd.restCount(), 1) &&
        t.checkExpect(this.harvard.restCount(), 2) &&
        t.checkExpect(this.southStation.restCount(), 4) &&
        t.checkExpect(this.mtPlace.restCount(), 0);
  }
  
  boolean testSumRest(Tester t) {
    return t.checkExpect(this.northEnd.restSum(), 4.4) &&
        t.checkExpect(this.harvard.restSum(), 8.9) &&
        t.checkExpect(this.southStation.restSum(), 17.3) &&
        t.checkExpect(this.mtPlace.restSum(), 0.0);
  }
  
  boolean testCountRestLo(Tester t) {
    return t.checkExpect(this.northEndLoF.numOfRest(), 1) &&
        t.checkExpect(this.harvardLoF.numOfRest(), 2) &&
        t.checkExpect(this.SSLoF.numOfRest(), 4) &&
        t.checkExpect(this.mt.numOfRest(), 0);
  }
 
  boolean testFoodinessRating(Tester t) {
    return t.checkExpect(this.northEnd.foodinessRating(), 4.4) &&
        t.checkExpect(this.harvard.foodinessRating(),4.45) &&
        t.checkExpect(this.southStation.foodinessRating(), 4.325) &&
        t.checkExpect(this.cambridgeSide.foodinessRating(), 4.216666666666666) &&
        t.checkExpect(this.mtPlace.foodinessRating(), 0.0);
  }
  
  boolean testRestType(Tester t) {
    return t.checkExpect(this.sarkuJapan.restType(),"Sarku Japan (teriyaki)") &&
        t.checkExpect(this.venue1.restType(), "") &&
        t.checkExpect(this.bridgeShuttle.restType(), "The Daily Catch (Sicilian),"
            + " Regina's Pizza (pizza), The Daily Catch (Sicilian), Border Cafe (Tex-Mex)");
  }
  
  boolean testRestaurantinfo(Tester t) {
    return t.checkExpect(this.cambridgeSide.restaurantInfo(),
        "Sarku Japan (teriyaki), Starbucks (coffee),"
        + " The Daily Catch (Sicilian), Regina's Pizza (pizza), The Daily Catch (Sicilian),"
        + " Border Cafe (Tex-Mex)") &&
        t.checkExpect(this.northEnd.restaurantInfo(), "The Daily Catch (Sicilian)") &&
        t.checkExpect(this.harvard.restaurantInfo(), "The Daily Catch (Sicilian),"
            + " Border Cafe (Tex-Mex)") &&
        t.checkExpect(this.southStation.restaurantInfo(), "The Daily Catch (Sicilian),"
            + " Regina's Pizza (pizza), The Daily Catch (Sicilian),"
            + " Border Cafe (Tex-Mex)") &&
        t.checkExpect(this.mtPlace.restaurantInfo(), "");
  }
  
  boolean testInfo(Tester t) {
    return t.checkExpect(this.cambridgeLoF.info(), "Sarku Japan (teriyaki),"
        + " Starbucks (coffee),"
        + " The Daily Catch (Sicilian), Regina's Pizza (pizza),"
        + " The Daily Catch (Sicilian),"
        + " Border Cafe (Tex-Mex)") &&
        t.checkExpect(this.northEndLoF.info(), "The Daily Catch (Sicilian)") &&
        t.checkExpect(this.harvardLoF.info(), "The Daily Catch (Sicilian),"
            + " Border Cafe (Tex-Mex)") &&
        t.checkExpect(this.SSLoF.info(), "The Daily Catch (Sicilian),"
            + " Regina's Pizza (pizza), The Daily Catch (Sicilian),"
            + " Border Cafe (Tex-Mex)");
  }
  
  boolean testSumRestLo(Tester t) {
    return t.checkExpect(this.northEndLoF.sumRestRating(), 4.4) &&
        t.checkExpect(this.harvardLoF.sumRestRating(), 8.9) &&
        t.checkExpect(this.SSLoF.sumRestRating(), 17.3);
  }
}

// the methods that iterate through a ILoFeature all double count for some places
// because some places (cambridgeSide) have shuttles that go to places that also have shuttles.
// In my code, the functions that sometimes double count are restCount, sumRest, info, ETC.
// Those shuttles end up making their way back to a place that has already been visited
// if we could use accumulators in this homework we could keep track of the places we've been.
// Basically, some places are visited twice and need count the restaurant twice