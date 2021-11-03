import tester.Tester;
import javalib.worldimages.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.awt.Color;
import javalib.funworld.*;
import javalib.worldcanvas.WorldSceneBase;
import java.util.Random;

//represents our game
class SpaceInvaders extends World {
  // represents all gameboard pieces
  GameBoard gameboardPieces;
  // random object for testing
  Random rand;

 

  SpaceInvaders() {
    this.gameboardPieces = new Utils().buildDefault();
    this.rand = new Random();
  }

  SpaceInvaders(GameBoard gameboardPieces) {
    this.gameboardPieces = gameboardPieces;
  }

  SpaceInvaders(GameBoard gameboardPieces, Random rand) {
    this.gameboardPieces = gameboardPieces;
    this.rand = rand;
  }

  SpaceInvaders(Random rand) {
    this.gameboardPieces = new GameBoard(rand);
  }

  // renders this world
  public WorldScene makeScene() {
    return this.gameboardPieces.makeBoard().fold(new Render(),
        new WorldScene(500, 800));
  }

  // new world is created based on the key that is pressed
  // design choice to have it move when key is pressed
  // instead of constantly to emmulate the actual space
  // game
  //
  public World onKeyEvent(String key) {
    if (key.equals(" ")) {
      return this.addBullet();
    }
    else if (key.equals("left")) {
      return this.moveSLeft();
    }
    else if (key.equals("right")) {
      return this.moveSRight();
    }
    else {
      return this;
    }
  }

  // On every clock tick, this world is altered
  public World onTick() {
    return new SpaceInvaders(this.gameboardPieces.tick());
  }

  // if there is less than 3 bullets on the board shoots a new bullet
  public World addBullet() {
    if (this.gameboardPieces.amountBullet()) {
      return this;
    }
    else {
      return new SpaceInvaders(this.gameboardPieces.shootSS());
    }
  }

  // moves the spaceship to the left if it is not on the border
  public World moveSLeft() {
    if (this.gameboardPieces.touchesBorder(20)) {
      return this;
    }
    else {
      return new SpaceInvaders(this.gameboardPieces.moveShipL());
    }
  }

  // moves the spaceship to the right if it is not on the border
  public World moveSRight() {
    if (this.gameboardPieces.touchesBorder(480)) {
      return this;
    }

    else {
      return new SpaceInvaders(this.gameboardPieces.moveShipR());
    }
  }

  // ends the world and displays the correct ending screen
  // based on win or loss
  public WorldEnd worldEnds() {
    if (this.gameboardPieces.collisionIonSS()) {
      return new WorldEnd(true, this.makeAFinalScene());
    }
    else if (this.gameboardPieces.allMt()) {
      return new WorldEnd(true, this.makeFinalWin());
    }
    else {
      return new WorldEnd(false, this.makeScene());
    }
  }

  // makes the final scene when you lose the game
  public WorldSceneBase makeAFinalScene() {
    return new WorldScene(500, 800).placeImageXY(new TextImage("You Lost", Color.red),
        250, 400);
  }

  // makes the final scene when you win the game
  // when all the invaders are destroyed
  public WorldSceneBase makeFinalWin() {
    return new WorldScene(500, 800).placeImageXY(new TextImage("You Win!!!", Color.green),
        250, 400);
  }

}

//represents all of the pieces involved in the game
class GameBoard {
  IGamePiece spaceShip;
  IList<IGamePiece> row1Inv;
  IList<IGamePiece> row2Inv;
  IList<IGamePiece> row3Inv;
  IList<IGamePiece> row4Inv;
  IList<IGamePiece> iBullets;
  IList<IGamePiece> sBullets;
  Random rand;

  // for use in big bang
  GameBoard(IGamePiece spaceShip, IList<IGamePiece> row1Inv, IList<IGamePiece> row2Inv,
      IList<IGamePiece> row3Inv, IList<IGamePiece> row4Inv, IList<IGamePiece> iBullets,
      IList<IGamePiece> sBullets) {
    this.spaceShip = spaceShip;
    this.row1Inv = row1Inv;
    this.row2Inv = row2Inv;
    this.row3Inv = row3Inv;
    this.row4Inv = row4Inv;
    this.iBullets = iBullets;
    this.sBullets = sBullets;
    this.rand = new Random();
  }

  // for tests????
  GameBoard(IGamePiece spaceShip, IList<IGamePiece> row1Inv, IList<IGamePiece> row2Inv,
      IList<IGamePiece> row3Inv, IList<IGamePiece> row4Inv, IList<IGamePiece> iBullets,
      IList<IGamePiece> sBullets, Random rand) {
    this.spaceShip = spaceShip;
    this.row1Inv = row1Inv;
    this.row2Inv = row2Inv;
    this.row3Inv = row3Inv;
    this.row4Inv = row4Inv;
    this.iBullets = iBullets;
    this.sBullets = sBullets;
    this.rand = rand;
  }

  // for testing too!
  GameBoard(Random rand) {
    this(new Spaceship(new CartPt(250, 750), Color.black),
        new Utils().buildInv(new Invader(new CartPt(90, 20), Color.red), 9),
        new Utils().buildInv(new Invader(new CartPt(90, 70), Color.red), 9),
        new Utils().buildInv(new Invader(new CartPt(90, 120), Color.red), 9),
        new Utils().buildInv(new Invader(new CartPt(90, 170), Color.red), 9),
        new MtList<IGamePiece>(), new MtList<IGamePiece>(), rand);
  }

  // creates a new gameboard on every tick
  // updated
  public GameBoard tick() {
    IList<IGamePiece> updatedIBullet = this.shootIB().filter(new OnBoarder())
        .map(new MoveIBullets());
    IList<IGamePiece> updatedSBullet = this.sBullets.filter(new OnBoarder())
        .map(new MoveSBullets());
    Predicate<IGamePiece> hit = new InvaderHit(updatedSBullet);

    return new GameBoard(this.spaceShip, this.row1Inv.filter(hit), this.row2Inv.filter(hit),
        this.row3Inv.filter(hit), this.row4Inv.filter(hit), updatedIBullet,
        updatedSBullet.filter(new InvaderHit(this.allInvadersLocs())));
  }

  // creates a list of every element in this GameBoard
  public IList<IGamePiece> makeBoard() {
    return new ConsList<IGamePiece>(this.spaceShip, (this.row1Inv.append(this.row2Inv
        .append(this.row3Inv.append(this.row4Inv.append(this.iBullets.append(this.sBullets)))))));
  }

  // is the amount of bullets on this board equal to 3
  public Boolean amountBullet() {
    return this.sBullets.length() == 3;
  }

  // checks if the amount of bullets in the game
  // is equal to 10
  public Boolean amountIBullet() {
    return this.iBullets.length() == 10;
  }

  // creates a new Gameboard with a new bullet
  public GameBoard shootSS() {
    ConsList<IGamePiece> newBullets = new ConsList<IGamePiece>(this.spaceShip.makeBullet(),
        this.sBullets);

    return new GameBoard(this.spaceShip, this.row1Inv, this.row2Inv, this.row3Inv, this.row4Inv,
        this.iBullets, newBullets);
  }

  // does the spaceship in this gameboard touch the border of the scene
  public Boolean touchesBorder(int border) {
    return this.spaceShip.borderTouch(border);
  }

  // moves the spaceship in this gameboard to the left
  // and creates a new gameboard with its new position
  public GameBoard moveShipL() {
    return new GameBoard(this.spaceShip.move(-5), this.row1Inv, this.row2Inv, this.row3Inv,
        this.row4Inv, this.iBullets, this.sBullets);
  }

  // moves the spaceship in this gameboard to the right and
  // creates a new gameboard with its new position
  public GameBoard moveShipR() {
    return new GameBoard(this.spaceShip.move(5), this.row1Inv, this.row2Inv, this.row3Inv,
        this.row4Inv, this.iBullets, this.sBullets);
  }

  // creates a list of all of possible invaders in this gameboard
  public IList<IGamePiece> allInvadersLocs() {
    return this.row1Inv.append(this.row2Inv.append(this.row3Inv.append(this.row4Inv)))
        .map(new AllContacts()).fold(new AppendLists(), new MtList<IGamePiece>());
  }

  // determines if any of the invader bullets have collided
  // with the spaceship
  public Boolean collisionIonSS() {
    IList<IGamePiece> possSS = this.spaceShip.possContact(40);
    IList<IList<IGamePiece>> possIB = this.iBullets.map(new AllContacts());

    return possSS.orMap(new WouldCollide(possIB.fold(new AppendLists(), new MtList<IGamePiece>())));
  }

  // appends all rows of the invaders into one list
  public IList<IGamePiece> allInvaders() {
    return this.row1Inv.append(this.row2Inv.append(this.row3Inv.append(this.row4Inv)));
  }

  // shoots an invader bullet from a random invader
  // if there are less that 10 bullet in the game
  public IList<IGamePiece> shootIB() {
    if (this.iBullets.length() < 10) {
      return new ConsList<IGamePiece>(
          this.allInvaders().index(this.rand.nextInt(this.allInvaders().length()) + 1).makeBullet(),
          this.iBullets);
    }

    else {
      return this.iBullets;
    }
  }

  // determines if there are no more invaders on the board
  public Boolean allMt() {
    return this.allInvaders().length() == 0;
  }
}

//represents all types of GamePieces for Space Invaders
interface IGamePiece {

  // moves the cartpt of the IGamePiece a certain amount
  IGamePiece move(int move);

  // determines if the piece's location (x or y value) equals a certain amount
  Boolean borderTouch(int border);

  // makes a new bullets at the pieces location
  IGamePiece makeBullet();

  // places the gamepiece on a given WorldScene
  WorldScene draw(WorldScene acc);

  // creates an invader that is moved 40 pixels to the right
  IGamePiece newInvader();

  // makes list of all possible points spaceinvader can touch
  IList<IGamePiece> possContact(int width);

  // moves this IGamePiece by the given x and y
  IGamePiece movePiece(int x, int y);

  // determines if this piece's location is over the gameboard
  Boolean onTheBoard();

  // determines the width of this IGamePiece
  int width();

  // Does this IGamePiece have the same Location
  // as the given IGamePiece
  Boolean sameLoc(IGamePiece that);

  // Determines if this IGamepiece has the same Center
  // as the given
  Boolean sameLocHelp(CartPt center);
}

//abstraction class for all IGamePieces
abstract class AGamePiece implements IGamePiece {
  CartPt location;
  Color color;

  AGamePiece(CartPt location, Color color) {
    this.location = location;
    this.color = color;

  }

  // places the gamepiece on a given WorldScene
  public abstract WorldScene draw(WorldScene acc);

  // makes a new bullets at the pieces location
  public IGamePiece makeBullet() {
    return this;
  }

  // determines if the piece's location (x or y value) equals a certain amount
  public Boolean borderTouch(int border) {
    return this.location.x == border;
  }

  // creates an invader that is moved 40 pixels to the right
  public IGamePiece newInvader() {
    return this;
  }

  // determines if this piece's location is over the gameboard
  public Boolean onTheBoard() {
    return true;
  }

  // moves the cartpt of the IGamePiece a certain amount
  public abstract IGamePiece move(int move);

  // determines the width for this AGamePiece
  public abstract int width();

  // moves this AGamePiece by the given x and y
  public abstract IGamePiece movePiece(int x, int y);

  // creates a list of all posible contacts for this AGamePiece
  public abstract IList<IGamePiece> possContact(int width);

  // does this AGamePiece have the same location as the given
  public Boolean sameLoc(IGamePiece that) {
    return that.sameLocHelp(this.location);
  }

  // does this AGamePiece have the same center as the given
  public Boolean sameLocHelp(CartPt center) {
    return this.location.x == center.x && this.location.y == center.y;
  }
}

//represents the spaceship in this gae
class Spaceship extends AGamePiece {

  Spaceship(CartPt location, Color color) {
    super(location, color);
  }

  // moves the cartpt of the IGamePiece a certain amount
  public IGamePiece move(int move) {
    return new Spaceship(this.location.moveLoc(move, 0), this.color);
  }

  // places the gamepiece on a given WorldScene
  public WorldScene draw(WorldScene acc) {
    return acc.placeImageXY(new RectangleImage(40, 20, "solid", this.color), this.location.x,
        this.location.y);
  }

  // makes a new bullets at the pieces location
  public IGamePiece makeBullet() {
    return new SBullet(this.location, Color.black);
  }

  // determines the width of this spaceship
  public int width() {
    return 40;
  }

  // moves this spaceship by the given x and y
  public IGamePiece movePiece(int x, int y) {
    return new Spaceship(this.location.moveLoc(x, y), this.color);
  }

  // determines all possible contact locations of this spaceship
  public IList<IGamePiece> possContact(int width) {
    int x = this.width();
    if (width == 0) {
      return new ConsList<IGamePiece>(this.movePiece(width - x / 2, -10), new MtList<IGamePiece>());
    }

    else {
      return new ConsList<IGamePiece>(this.movePiece(width - x / 2, -10),
          this.possContact(width - 1));
    }
  }

}

//represents an invader in the game
class Invader extends AGamePiece {

  Invader(CartPt location, Color color) {
    super(location, color);
  }

  // moves the cartpt of the IGamePiece a certain amount
  public IGamePiece move(int move) {
    return this;
  }

  // places the gamepiece on a given WorldScene
  public WorldScene draw(WorldScene acc) {
    return acc.placeImageXY(new RectangleImage(20, 20, "solid", this.color), this.location.x,
        this.location.y);
  }

  // creates an invader that is moved 40 pixels to the right
  public IGamePiece newInvader() {
    return new Invader(new CartPt(this.location.x + 40, this.location.y), Color.red);
  }

  // makes a new bullets at the pieces location
  public IGamePiece makeBullet() {
    return new IBullet(this.location, Color.red);
  }

  // determines the width of this invader
  public int width() {
    return 20;
  }

  // moves this invader by the given x and y
  public IGamePiece movePiece(int x, int y) {
    return new Invader(this.location.moveLoc(x, y), this.color);
  }

  // determines all possible points of contact for this Invader
  public IList<IGamePiece> possContact(int width) {
    int x = this.width();
    if (width == 0) {
      return new ConsList<IGamePiece>(this.movePiece(width - x / 2, 10), new MtList<IGamePiece>());
    }

    else {
      return new ConsList<IGamePiece>(this.movePiece(width - x / 2, 10),
          this.possContact(width - 1));
    }
  }
}

//represents a bullet shot by the spaceship
//in our game
class SBullet extends AGamePiece {

  SBullet(CartPt location, Color color) {
    super(location, color);
  }

  // moves the cartpt of the IGamePiece a certain amount
  public IGamePiece move(int move) {
    return new SBullet(this.location.moveLoc(0, move), this.color);
  }

  // places the gamepiece on a given WorldScene
  public WorldScene draw(WorldScene acc) {
    return acc.placeImageXY(new CircleImage(4, "solid", this.color), this.location.x,
        this.location.y);
  }

  // determines if this piece's location is over the gameboard
  public Boolean onTheBoard() {
    return this.location.y != 0;
  }

  // determines the width of this SBullet
  public int width() {
    return 0;
  }

  // moves this SBullet by the given x and y
  public IGamePiece movePiece(int x, int y) {
    return new SBullet(this.location.moveLoc(x, y), this.color);
  }

  // determines all possible points of contact
  // of this SBullet
  public IList<IGamePiece> possContact(int width) {
    return new ConsList<IGamePiece>(this.movePiece(0, -4),
        new ConsList<IGamePiece>(this.movePiece(-4, 0),
            new ConsList<IGamePiece>(this.movePiece(4, 0), new MtList<IGamePiece>())));
  }
}

//represents a bullet shot by an invader in this game
class IBullet extends AGamePiece {

  IBullet(CartPt location, Color color) {
    super(location, color);
  }

  // moves the cartpt of the IGamePiece a certain amount
  public IGamePiece move(int move) {
    return new IBullet(this.location.moveLoc(0, move), this.color);
  }

  // places the gamepiece on a given WorldScene
  public WorldScene draw(WorldScene acc) {
    return acc.placeImageXY(new CircleImage(4, "solid", this.color), this.location.x,
        this.location.y);
  }

  // determines if this piece's location is over the gameboard
  public Boolean onTheBoard() {
    return this.location.y != 800;
  }

  // determines the width of this IBullet
  public int width() {
    return 0;
  }

  // moves this IBullet by the given x and y
  public IGamePiece movePiece(int x, int y) {
    return new IBullet(this.location.moveLoc(x, y), this.color);
  }

  // determines all possible points of contact of this IBullet
  public IList<IGamePiece> possContact(int width) {
    return new ConsList<IGamePiece>(this.movePiece(0, 4),
        new ConsList<IGamePiece>(this.movePiece(-4, 0),
            new ConsList<IGamePiece>(this.movePiece(4, 0), new MtList<IGamePiece>())));
  }
}

//represents a list with a generic type
interface IList<T> {
  // filter this list by the given predicate
  IList<T> filter(Predicate<T> pred);

  // maps a function onto every member of this list
  <Y> IList<Y> map(Function<T, Y> fun);

  // combines the items in this list using the given function
  <U> U fold(BiFunction<T, U, U> fun, U base);

  // checks if any item in this list returns true based on pred
  Boolean orMap(Predicate<T> pred);

  // checks if all items in this list return true based on pred
  Boolean andMap(Predicate<T> pred);

  // appends this list to the given list
  IList<T> append(IList<T> given);

  // returns the length of this list
  int length();

  // returns the element of this list located
  // at the given place in the list
  T index(int n);
}

//represents an MtList of the given generic type
class MtList<T> implements IList<T> {

  // filter this list by the given predicate
  public IList<T> filter(Predicate<T> pred) {
    return this;
  }

  // maps a function onto every member of this list
  public <Y> IList<Y> map(Function<T, Y> fun) {
    return new MtList<Y>();
  }

  // combines the items in this list using the given function
  public <U> U fold(BiFunction<T, U, U> fun, U base) {
    return base;
  }

  // checks if any item in this list returns true based on pred
  public Boolean orMap(Predicate<T> pred) {
    return false;
  }

  // checks if all items in this list return true based on pred
  public Boolean andMap(Predicate<T> pred) {
    return true;
  }

  // appends this list to the given list
  public IList<T> append(IList<T> given) {
    return given;
  }

  // returns the length of this list
  public int length() {
    return 0;
  }

  // returns an error, since this will never be accessed
  public T index(int n) {
    return null;
    // we chose to keep this as null instead of throwing an exception
    // caused some issues, and it will never hit an empty case anyway
  }
}

//represents a non-empty list of the generic type
class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;

  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  // filter this list by the given predicate
  public IList<T> filter(Predicate<T> pred) {
    if (pred.test(this.first)) {
      return new ConsList<T>(this.first, this.rest.filter(pred));
    }
    else {
      return this.rest.filter(pred);
    }
  }

  // maps a function onto every member of this list
  public <Y> IList<Y> map(Function<T, Y> fun) {
    return new ConsList<Y>(fun.apply(this.first), this.rest.map(fun));
  }

  // checks if any item in this list returns true based on pred
  public Boolean orMap(Predicate<T> pred) {
    return pred.test(this.first) || this.rest.orMap(pred);
  }

  // checks if all items in this list return true based on pred
  public Boolean andMap(Predicate<T> pred) {
    return pred.test(this.first) && this.rest.andMap(pred);
  }

  // combines the items in this list using the given function
  public <U> U fold(BiFunction<T, U, U> fun, U base) {
    return fun.apply(this.first, this.rest.fold(fun, base));
  }

  // appends this list to the given list
  public IList<T> append(IList<T> given) {
    return new ConsList<T>(this.first, this.rest.append(given));
  }

  // returns the length of this list
  public int length() {
    return 1 + this.rest.length();
  }

  // returns the element located at the given location
  // in the list
  public T index(int n) {
    if (n == 0) {
      return this.first;
    }

    else if (this.rest.length() == 0) {
      return this.first;
    }
    else {
      return this.rest.index(n - 1);
    }
  }
}

//Maps a function that alters the location of a gamepiece
class MoveIBullets implements Function<IGamePiece, IGamePiece> {

  public IGamePiece apply(IGamePiece t) {
    return t.move(10);
  }
}

//Maps a function that alters the location of a gamepiece
class MoveSBullets implements Function<IGamePiece, IGamePiece> {

  public IGamePiece apply(IGamePiece t) {
    return t.move(-10);
  }
}

//predicate that checks if this given is equal to that CartPt
class SameCartPt implements Predicate<CartPt> {
  CartPt given;

  SameCartPt(CartPt given) {
    this.given = given;
  }

  public boolean test(CartPt t) {
    return given.x == t.x && given.y == t.y;
  }
}

// does this gamepiece have the same location as the given
class SameLocation implements Predicate<IGamePiece> {
  IGamePiece given;

  SameLocation(IGamePiece given) {
    this.given = given;
  }

  public boolean test(IGamePiece t) {
    return given.sameLoc(t);
  }
}

//dpes this IGamepiece have the same location as any 
// in the given list
class WouldCollide implements Predicate<IGamePiece> {
  IList<IGamePiece> pieces;

  WouldCollide(IList<IGamePiece> pieces) {
    this.pieces = pieces;
  }

  public boolean test(IGamePiece t) {
    return pieces.orMap(new SameLocation(t));
  }
}

//predicate that checks if a given point is contains in this list of CartPts
class WithinPoints implements Predicate<CartPt> {
  IList<CartPt> points;

  WithinPoints(IList<CartPt> points) {
    this.points = points;
  }

  public boolean test(CartPt t) {
    return points.orMap(new SameCartPt(t));
  }
}

//BiFunction that draws the worldscene 
class Render implements BiFunction<IGamePiece, WorldScene, WorldScene> {

  public WorldScene apply(IGamePiece t, WorldScene u) {
    return t.draw(u);
  }
}

//predicate that tests if the gamepiece is off the board
class OnBoarder implements Predicate<IGamePiece> {

  public boolean test(IGamePiece t) {
    return t.onTheBoard();
  }
}

// creates a list of all possible contacts of the given IGamepiece
class AllContacts implements Function<IGamePiece, IList<IGamePiece>> {

  public IList<IGamePiece> apply(IGamePiece t) {
    return t.possContact(t.width());
  }
}

//appends all of the items in the given list
class AppendLists implements BiFunction<IList<IGamePiece>, IList<IGamePiece>, IList<IGamePiece>> {

  public IList<IGamePiece> apply(IList<IGamePiece> t, IList<IGamePiece> u) {
    return t.append(u);
  }
}

//
class InvaderHit implements Predicate<IGamePiece> {

  IList<IGamePiece> bullets;

  InvaderHit(IList<IGamePiece> bullets) {
    this.bullets = bullets;
  }

  public boolean test(IGamePiece t) {
    return !t.possContact(t.width()).orMap(new WouldCollide(bullets));
  }
}

//represents a cartesian point on the board 
// of the game
class CartPt {
  int x;
  int y;

  CartPt(int x, int y) {
    this.x = x;
    this.y = y;
  }

  // changes the x and y of this by given amounts
  public CartPt moveLoc(int x, int y) {
    return new CartPt(this.x + x, this.y + y);
  }
}

//represents the utility function used in the code
class Utils {

  // builds a row of invaders
  public IList<IGamePiece> buildInv(IGamePiece base, int lengthAcc) {
    if (lengthAcc == 0) {
      return new MtList<IGamePiece>();
    }
    else {
      return new ConsList<IGamePiece>(base, buildInv(base.newInvader(), lengthAcc - 1));
    }
  }

  // builds the starting board for SpaceInvaders
  public GameBoard buildDefault() {
    return new GameBoard(new Spaceship(new CartPt(250, 750), Color.black),
        this.buildInv(new Invader(new CartPt(90, 20), Color.red), 9),
        this.buildInv(new Invader(new CartPt(90, 70), Color.red), 9),
        this.buildInv(new Invader(new CartPt(90, 120), Color.red), 9),
        this.buildInv(new Invader(new CartPt(90, 170), Color.red), 9), new MtList<IGamePiece>(),
        new MtList<IGamePiece>());
  }
}

class ExamplesGamePiece {
  CartPt topLeft = new CartPt(0, 0);
  CartPt shipStart = new CartPt(150, 580);
  CartPt rw1inv1 = new CartPt(0, 50);
  CartPt rw1inv2 = new CartPt(3, 20);
  CartPt rw1inv8 = new CartPt(200, 150);
  CartPt rw1inv9 = new CartPt(115, 20);
  CartPt rw4inv1 = new CartPt(0, 50);
  CartPt rw4inv2 = new CartPt(3, 20);
  CartPt rw4inv4 = new CartPt(1, 40);
  CartPt rw4inv6 = new CartPt(15, 300);
  CartPt rw4inv8 = new CartPt(200, 150);
  CartPt rw4inv9 = new CartPt(115, 20);

  IGamePiece spaceShip = new Spaceship(this.shipStart, Color.black);
  IGamePiece spaceShipleft = new Spaceship(new CartPt(20, 50), Color.black);
  IGamePiece invader = new Invader(this.rw1inv1, Color.red);
  IGamePiece ibullet = new IBullet(this.rw4inv6, Color.red);
  IGamePiece sbullet = new SBullet(this.rw4inv8, Color.black);
  IGamePiece offSBullet = new SBullet(new CartPt(10, 0), Color.black);
  IGamePiece offIBullet = new IBullet(new CartPt(10, 800), Color.red);

  IList<IGamePiece> board = new ConsList<>(this.spaceShip,
      new ConsList<>(this.invader, new MtList<>()));
  IList<IGamePiece> mtBoard = new MtList<>();

  IList<CartPt> cartptL = new ConsList<>(this.topLeft,
      new ConsList<>(this.shipStart, new ConsList<>(this.rw1inv1, new MtList<>())));
  IList<CartPt> cartptL1 = new ConsList<>(this.rw4inv4,
      new ConsList<>(this.rw4inv6, new ConsList<>(this.rw4inv8, new MtList<>())));

  IList<CartPt> mtcartpt = new MtList<>();

  IList<IGamePiece> loBullet = new ConsList<>(this.ibullet, new MtList<>());

  Invader inv1 = new Invader(this.rw1inv1, Color.red);
  IGamePiece inv2 = new Invader(this.rw4inv6, Color.red);

  Utils utility = new Utils();

  WorldScene testScene = new WorldScene(200, 400);

  // tests for width
  Boolean testWidth(Tester t) {
    return t.checkExpect(this.spaceShip.width(), 40) && t.checkExpect(this.inv1.width(), 20);
  }

  // tests for BuildInv(IGamepiece, int)
  Boolean testBuildInv(Tester t) {
    return t.checkExpect(this.utility.buildInv(this.inv1, 0), new MtList<Invader>())
        && t.checkExpect(this.utility.buildInv(this.inv1, 3),
            new ConsList<Invader>(this.inv1,
                new ConsList<Invader>(new Invader(new CartPt(40, 50), Color.red),
                    new ConsList<Invader>(new Invader(new CartPt(80, 50), Color.red),
                        new MtList<Invader>()))));
  }

  // test for possible list of invader
  Boolean testinvaderPoss(Tester t) {
    return t.checkExpect(this.spaceShip.possContact(1),
        new ConsList<IGamePiece>(new Spaceship(new CartPt(131, 570), Color.black),
            new ConsList<IGamePiece>(new Spaceship(new CartPt(130, 570), Color.black),
                new MtList<IGamePiece>())))
        && t.checkExpect(this.ibullet.possContact(0),
            new ConsList<IGamePiece>(new IBullet(new CartPt(15, 304), Color.red),
                new ConsList<IGamePiece>(new IBullet(new CartPt(11, 300), Color.red),
                    new ConsList<IGamePiece>(new IBullet(new CartPt(19, 300), Color.red),
                        new MtList<IGamePiece>()))));
  }

  // tests for orMap(Predicate<T>)
  Boolean testorMap(Tester t) {
    return t.checkExpect(cartptL.orMap(new SameCartPt(this.topLeft)), true)
        && t.checkExpect(cartptL.orMap(new SameCartPt(this.rw4inv2)), false)
        && t.checkExpect(mtcartpt.orMap(new SameCartPt(this.topLeft)), false);

  }

  // Tests for andMap(Predicate<T>)
  Boolean testandMap(Tester t) {
    return t.checkExpect(cartptL.andMap(new SameCartPt(this.topLeft)), false)
        && t.checkExpect(cartptL.andMap(new SameCartPt(this.rw4inv2)), false)
        && t.checkExpect(mtcartpt.andMap(new SameCartPt(this.topLeft)), true);
  }

  // Tests for fold(BiFunction<T U U>, U)
  Boolean testfold(Tester t) {
    return t.checkExpect(this.board.fold(new Render(), new WorldScene(600, 500)),
        new WorldScene(600, 500).placeImageXY(new RectangleImage(20, 20, "solid", Color.red), 0, 50)
            .placeImageXY(new RectangleImage(40, 20, "solid", Color.black), 150, 580))
        && t.checkExpect(this.mtBoard.fold(new Render(), new WorldScene(600, 500)),
            new WorldScene(600, 500));
  }

  // Tests for Render()
  Boolean testRender(Tester t) {
    return t.checkExpect(new Render().apply(ibullet, new WorldScene(600, 400)),
        new WorldScene(600, 400).placeImageXY(new CircleImage(4, "solid", Color.red), 15, 300));
  }

  // tests for WithinPoints(IList<CartPT>)
  Boolean testWithinPoints(Tester t) {
    return t.checkExpect(new WithinPoints(this.cartptL).test(this.shipStart), true)
        && t.checkExpect(new WithinPoints(this.cartptL).test(this.rw4inv9), false)
        && t.checkExpect(new WithinPoints(this.mtcartpt).test(this.shipStart), false);
  }

  // tests for WouldCollide
  Boolean testWouldCollide(Tester t) {
    return t.checkExpect(new WouldCollide(this.board).test(this.spaceShip), true)
        && t.checkExpect(new WouldCollide(this.board).test(this.invader), true)
        && t.checkExpect(new WouldCollide(this.board).test(this.inv2), false)
        && t.checkExpect(new WouldCollide(this.mtBoard).test(this.inv2), false);
  }

  // test for AllContacts
  Boolean testAllContacts(Tester t) {
    return t.checkExpect(new AllContacts().apply(this.spaceShip), this.spaceShip.possContact(40))
        && t.checkExpect(new AllContacts().apply(this.inv1), this.inv1.possContact(20))
        && t.checkExpect(new AllContacts().apply(this.ibullet), this.ibullet.possContact(0))
        && t.checkExpect(new AllContacts().apply(this.sbullet), this.sbullet.possContact(0));
  }

  Boolean testInvaderHit(Tester t) {
    return t.checkExpect(new InvaderHit(this.board).test(this.ibullet), true)
        && t.checkExpect(new InvaderHit(this.board).test(this.spaceShip),
            !this.spaceShip.possContact(this.spaceShip.width()).orMap(new WouldCollide(board)))
        && t.checkExpect(new InvaderHit(this.mtBoard).test(this.spaceShip), true);
  }

  Boolean testAppendsList(Tester t) {
    return t.checkExpect(new AppendLists().apply(this.mtBoard, this.loBullet),
        this.loBullet.append(this.mtBoard))
        && t.checkExpect(new AppendLists().apply(this.mtBoard, this.mtBoard), this.mtBoard)
        && t.checkExpect(new AppendLists().apply(this.loBullet, this.loBullet),
            this.loBullet.append(this.loBullet));
  }

  // tests for Append(IList<T>)
  Boolean testappend(Tester t) {
    return t.checkExpect(this.cartptL.append(this.mtcartpt), this.cartptL)
        && t.checkExpect(this.cartptL.append(this.cartptL1),
            new ConsList<CartPt>(this.topLeft,
                new ConsList<CartPt>(this.shipStart,
                    new ConsList<CartPt>(this.rw1inv1,
                        new ConsList<CartPt>(this.rw4inv4, new ConsList<CartPt>(this.rw4inv6,
                            new ConsList<CartPt>(this.rw4inv8, new MtList<>())))))));
  }

  // tests for map(Function<T, Y)
  Boolean testmap(Tester t) {
    return t.checkExpect(this.loBullet.map(new MoveIBullets()),
        new ConsList<IGamePiece>(new IBullet(new CartPt(15, 310), Color.red),
            new MtList<IGamePiece>()))
        && t.checkExpect(this.mtBoard.map(new MoveIBullets()), this.mtBoard);
  }

  // test Index(int)
  Boolean testIndex(Tester t) {
    return t.checkExpect(this.cartptL.index(0), this.topLeft)
        && t.checkExpect(this.cartptL.index(1), this.shipStart);
  }

  // tests for length()
  Boolean testLength(Tester t) {
    return t.checkExpect(this.cartptL.length(), 3) && t.checkExpect(this.mtcartpt.length(), 0);
  }

  // tests draw()
  Boolean testDraw(Tester t) {
    return t.checkExpect(this.spaceShip.draw(this.testScene),
        this.testScene.placeImageXY(new RectangleImage(40, 20, "solid", Color.black), 150, 580))
        && t.checkExpect(this.invader.draw(this.testScene),
            this.testScene.placeImageXY(new RectangleImage(20, 20, "solid", Color.red), 0, 50))
        && t.checkExpect(this.ibullet.draw(this.testScene),
            this.testScene.placeImageXY(new CircleImage(4, "solid", Color.red), 15, 300))
        && t.checkExpect(this.sbullet.draw(this.testScene),
            this.testScene.placeImageXY(new CircleImage(4, "solid", Color.black), 200, 150));
  }

  // tests move(int)
  Boolean testMove(Tester t) {
    return t.checkExpect(this.spaceShip.move(-2), new Spaceship(new CartPt(148, 580), Color.black))
        && t.checkExpect(this.invader.move(1), this.invader)
        && t.checkExpect(this.sbullet.move(-5), new SBullet(new CartPt(200, 145), Color.black))
        && t.checkExpect(this.ibullet.move(10), new IBullet(new CartPt(15, 310), Color.red));
  }

  // tests for moveLoc(int, itn)
  Boolean testMoveLoc(Tester t) {
    return t.checkExpect(this.topLeft.moveLoc(2, 2), new CartPt(2, 2))
        && t.checkExpect(this.rw1inv1.moveLoc(50, -2), new CartPt(50, 48))
        && t.checkExpect(this.topLeft.moveLoc(2, 2), new CartPt(2, 2));
  }

  // tests for SameCartPt(CartPt)
  Boolean testSameCartPt(Tester t) {
    return t.checkExpect(new SameCartPt(this.rw1inv1).test(this.rw1inv1), true)
        && t.checkExpect(new SameCartPt(this.rw1inv1).test(this.rw1inv2), false);
  }

  Boolean testSameLocation(Tester t) {
    return t.checkExpect(new SameLocation(this.invader).test(this.invader), true)
        && t.checkExpect(new SameLocation(this.invader).test(this.inv2), false);
  }

  // Tests for sameLoc(IGamePiece)
  Boolean testSameLoc(Tester t) {
    return t.checkExpect(this.spaceShip.sameLoc(this.spaceShip), true)
        && t.checkExpect(this.spaceShipleft.sameLoc(this.spaceShip), false);
  }

  // tests for SameLocHelp
  Boolean testSameLocHelp(Tester t) {
    return t.checkExpect(this.spaceShip.sameLocHelp(this.shipStart), true)
        && t.checkExpect(this.spaceShip.sameLocHelp(this.rw4inv1), false);
  }

  // tests for makeBullet()
  Boolean testsMakeBullet(Tester t) {
    return t.checkExpect(this.spaceShip.makeBullet(), new SBullet(this.shipStart, Color.black))
        && t.checkExpect(this.invader.makeBullet(), new IBullet(this.rw1inv1, Color.red))
        && t.checkExpect(this.sbullet.makeBullet(), this.sbullet)
        && t.checkExpect(this.ibullet.makeBullet(), this.ibullet);
  }

  // tests for borderTouch(int)
  Boolean testBorderTouch(Tester t) {
    return t.checkExpect(this.spaceShip.borderTouch(20), false)
        && t.checkExpect(this.invader.borderTouch(0), true);
  }

  // tests for offTheBoard()
  Boolean testOnTheBoard(Tester t) {
    return t.checkExpect(this.spaceShip.onTheBoard(), true)
        && t.checkExpect(this.invader.onTheBoard(), true)
        && t.checkExpect(this.sbullet.onTheBoard(), true)
        && t.checkExpect(this.ibullet.onTheBoard(), true)
        && t.checkExpect(this.offSBullet.onTheBoard(), false)
        && t.checkExpect(this.offIBullet.onTheBoard(), false);
  }

  // tests for buildDefault()
  Boolean testBuildDefault(Tester t) {
    return t.checkExpect(utility.buildDefault(),
        new GameBoard(new Spaceship(new CartPt(250, 750), Color.black),
            utility.buildInv(new Invader(new CartPt(90, 20), Color.red), 9),
            utility.buildInv(new Invader(new CartPt(90, 70), Color.red), 9),
            utility.buildInv(new Invader(new CartPt(90, 120), Color.red), 9),
            utility.buildInv(new Invader(new CartPt(90, 170), Color.red), 9),
            new MtList<IGamePiece>(), new MtList<IGamePiece>()));
  }

  // tests for newInvader()
  Boolean testnewInvader(Tester t) {
    return t.checkExpect(this.spaceShip.newInvader(), this.spaceShip)
        && t.checkExpect(this.invader.newInvader(), new Invader(new CartPt(40, 50), Color.red));
  }

  // tests for MoveIBullets
  Boolean testMoveIBullets(Tester t) {
    return t.checkExpect(new MoveIBullets().apply(this.ibullet),
        new IBullet(new CartPt(15, 310), Color.red));
  }

  // tests for MoveSBullets
  Boolean testMoveSBullets(Tester t) {
    return t.checkExpect(new MoveSBullets().apply(this.sbullet),
        new SBullet(new CartPt(200, 140), Color.black));
  }

  // tests for OffBoarder
  Boolean testOffBoarder(Tester t) {
    return t.checkExpect(new OnBoarder().test(this.spaceShip), true)
        && t.checkExpect(new OnBoarder().test(this.invader), true)
        && t.checkExpect(new OnBoarder().test(this.offIBullet), false);
  }

}

class ExamplesWorld {
  CartPt topLeft = new CartPt(0, 0);
  CartPt shipStart = new CartPt(250, 750);
  CartPt rw4inv4 = new CartPt(1, 40);
  CartPt rw4inv5 = new CartPt(20, 100);
  CartPt rw4inv6 = new CartPt(15, 300);
  CartPt rw4inv7 = new CartPt(100, 200);
  CartPt rw4inv8 = new CartPt(200, 150);
  CartPt rw4inv9 = new CartPt(115, 20);
  CartPt row1base = new CartPt(90, 20);
  CartPt row2base = new CartPt(90, 70);
  CartPt row3base = new CartPt(90, 120);
  CartPt row4base = new CartPt(90, 170);

  IGamePiece spaceShip = new Spaceship(this.shipStart, Color.black);
  IGamePiece spaceShipLeft = new Spaceship(new CartPt(20, 50), Color.black);
  IGamePiece shipMovedL = new Spaceship(new CartPt(245, 750), Color.black);
  IGamePiece spaceShipRight = new Spaceship(new CartPt(480, 50), Color.black);
  IGamePiece shipMovedR = new Spaceship(new CartPt(255, 750), Color.black);
  IGamePiece spaceshipHit = new Spaceship(new CartPt(50, 50), Color.black);

  IGamePiece invader = new Invader(this.rw4inv4, Color.red);
  IGamePiece ibullet = new IBullet(this.rw4inv6, Color.red);
  IGamePiece sbullet = new SBullet(this.rw4inv8, Color.black);
  IGamePiece iBulletOffBoard = new IBullet(new CartPt(10, 800), Color.red);
  IGamePiece sBulletOffBoard = new SBullet(new CartPt(10, 0), Color.black);

  IGamePiece invader1 = new Invader(this.row1base, Color.red);
  IGamePiece invader2 = new Invader(this.row2base, Color.red);
  IGamePiece invader3 = new Invader(this.row3base, Color.red);
  IGamePiece invader4 = new Invader(this.row4base, Color.red);

  IList<IGamePiece> lospaceShip = new ConsList<>(this.spaceShip, new MtList<>());
  IList<IGamePiece> loinvader = new ConsList<>(this.invader, new MtList<>());
  IList<IGamePiece> loIBullet = new ConsList<>(this.ibullet, new MtList<>());
  IList<IGamePiece> moveLoIBullet = new ConsList<>(new IBullet(new CartPt(15, 310), Color.red),
      new MtList<>());
  IList<IGamePiece> loSBullet = new ConsList<>(this.sbullet, new MtList<>());
  IList<IGamePiece> moveLoSBullet = new ConsList<>(new SBullet(new CartPt(200, 140), Color.black),
      new MtList<>());
  IList<IGamePiece> fullSBullet = new ConsList<>(this.sbullet,
      new ConsList<>(this.sbullet, new ConsList<>(this.sbullet, new MtList<>())));
  IList<IGamePiece> shootNew = new ConsList<>(new SBullet(new CartPt(250, 750), Color.black),
      this.loSBullet);

  IList<IGamePiece> board = new ConsList<>(this.spaceShip,
      new ConsList<>(this.invader, new MtList<>()));
  IList<IGamePiece> mtboard = new MtList<>();
  IList<IGamePiece> sBulletOff = new ConsList<>(this.sBulletOffBoard, this.mtboard);
  IList<IGamePiece> iBulletOff = new ConsList<>(this.iBulletOffBoard,
      new ConsList<>(this.ibullet, this.mtboard));
  IList<IGamePiece> bulletHits = new ConsList<>(new IBullet(new CartPt(50, 36), Color.red),
      new MtList<>());

  Utils utility = new Utils();
  Random r = new Random(500);

  IList<IGamePiece> invaderList1 = utility.buildInv(this.invader1, 9);
  IList<IGamePiece> invaderList2 = utility.buildInv(this.invader2, 9);
  IList<IGamePiece> invaderList3 = utility.buildInv(this.invader3, 9);
  IList<IGamePiece> invaderList4 = utility.buildInv(this.invader4, 9);

  GameBoard testgame = new GameBoard(this.spaceShip, this.invaderList1, this.invaderList2,
      this.invaderList3, this.invaderList4, this.loIBullet, this.loSBullet);
  GameBoard fullSBulletGame = new GameBoard(this.spaceShip, this.invaderList1, this.invaderList2,
      this.invaderList3, this.invaderList4, this.loIBullet, this.fullSBullet);
  GameBoard missingInvaders = new GameBoard(this.spaceShip, this.mtboard, this.loinvader,
      this.invaderList3, this.invaderList4, this.loIBullet, this.loSBullet);
  GameBoard bulletsOffBoard = new GameBoard(this.spaceShip, this.invaderList1, this.invaderList2,
      this.invaderList3, this.invaderList4, this.iBulletOff, this.sBulletOff);
  GameBoard shipLeftBorder = new GameBoard(this.spaceShipLeft, this.invaderList1, this.invaderList2,
      this.invaderList3, this.invaderList4, this.loIBullet, this.loSBullet);
  GameBoard leftMoved = new GameBoard(this.shipMovedL, this.invaderList1, this.invaderList2,
      this.invaderList3, this.invaderList4, this.loIBullet, this.loSBullet);
  GameBoard shiprightBorder = new GameBoard(this.spaceShipRight, this.invaderList1,
      this.invaderList2, this.invaderList3, this.invaderList4, this.loIBullet, this.loSBullet);
  GameBoard rightMoved = new GameBoard(this.shipMovedR, this.invaderList1, this.invaderList2,
      this.invaderList3, this.invaderList4, this.loIBullet, this.loSBullet);
  GameBoard easygame = new GameBoard(this.spaceShip, this.mtboard, this.mtboard, this.mtboard,
      this.mtboard, this.mtboard, this.mtboard);
  GameBoard collisionGame = new GameBoard(this.spaceshipHit, this.mtboard, this.mtboard,
      this.mtboard, this.mtboard, this.bulletHits, this.mtboard);
  GameBoard allInvLocs = new GameBoard(this.spaceShip, this.mtboard, this.mtboard, this.mtboard,
      new ConsList<IGamePiece>(this.invader, this.mtboard), this.mtboard, this.mtboard);
  GameBoard randomDefault = new GameBoard(this.spaceShip,
      utility.buildInv(new Invader(new CartPt(90, 20), Color.red), 9),
      utility.buildInv(new Invader(new CartPt(90, 70), Color.red), 9),
      utility.buildInv(new Invader(new CartPt(90, 120), Color.red), 9),
      utility.buildInv(new Invader(new CartPt(90, 170), Color.red), 9),
      new ConsList<IGamePiece>(new IBullet(new CartPt(290, 30), Color.red),
          new MtList<IGamePiece>()),
      new MtList<IGamePiece>());

  GameBoard randomGame = new GameBoard(new Random(500));

  IList<CartPt> cartptL = new ConsList<>(this.topLeft,
      new ConsList<>(this.shipStart, new ConsList<>(this.rw4inv4, new MtList<>())));
  IList<CartPt> cartptL1 = new ConsList<>(this.rw4inv4,
      new ConsList<>(this.rw4inv6, new ConsList<>(this.rw4inv8, new MtList<>())));

  IList<CartPt> mtcartpt = new MtList<>();

  Invader inv1 = new Invader(this.rw4inv4, Color.red);

  SpaceInvaders testworld = new SpaceInvaders(this.testgame);
  SpaceInvaders testLeft = new SpaceInvaders(this.shipLeftBorder);
  SpaceInvaders testRight = new SpaceInvaders(this.shiprightBorder);
  SpaceInvaders easyworld = new SpaceInvaders(this.easygame);
  SpaceInvaders fullSBullets = new SpaceInvaders(this.fullSBulletGame);
  SpaceInvaders randomInvaders = new SpaceInvaders(r);
  SpaceInvaders randomSolution = new SpaceInvaders(this.randomDefault);

  // tests bigBang
  Boolean testBigBang(Tester t) {
    SpaceInvaders w = new SpaceInvaders(utility.buildDefault());
    double tickspeed = 0.1;
    int worldWidth = 500;
    int worldHeight = 800;
    return w.bigBang(worldWidth, worldHeight, tickspeed);
  }

  // tests makeBoard
  Boolean testMakeBoard(Tester t) {
    return t.checkExpect(this.testgame.makeBoard(),
        new ConsList<IGamePiece>(this.spaceShip,
            (this.invaderList1.append(this.invaderList2.append(this.invaderList3
                .append(this.invaderList4.append(this.loIBullet.append(this.loSBullet))))))));
  }

  // tests makeScene()
  Boolean testmakeScene(Tester t) {
    return t.checkExpect(this.easyworld.makeScene(),
        new WorldScene(500, 800).placeImageXY(new RectangleImage(40, 20, "solid", Color.black), 250,
            750))
        && t.checkExpect(this.testworld.makeScene(),
            this.testgame.makeBoard().fold(new Render(), new WorldScene(500, 800)));
  }

  // tests onKeyEvent(String)
  Boolean testonKeyEvent(Tester t) {
    return t.checkExpect(this.easyworld.onKeyEvent(" "),
        new SpaceInvaders(
            new GameBoard(this.spaceShip, this.mtboard, this.mtboard, this.mtboard, this.mtboard,
                this.mtboard, new ConsList<>(new SBullet(shipStart, Color.black), new MtList<>()))))
        && t.checkExpect(this.testworld.onKeyEvent("left"), this.testworld.moveSLeft())
        && t.checkExpect(this.testworld.onKeyEvent("right"), this.testworld.moveSRight());
  }

  // tests onTick
  Boolean testonTick(Tester t) {
    World local = new SpaceInvaders(new Random(2));
    return t.checkExpect(local.onTick(), randomSolution);
  }

  // tests addBullet()
  Boolean testaddBullet(Tester t) {
    return t.checkExpect(this.easyworld.addBullet(), new SpaceInvaders(this.easygame.shootSS()))
        && t.checkExpect(this.fullSBullets.addBullet(), fullSBullets)
        && t.checkExpect(this.testworld.addBullet(), new SpaceInvaders(this.testgame.shootSS()));
  }

  // tests moveSLeft()
  Boolean testMoveSLeft(Tester t) {
    return t.checkExpect(this.testLeft.moveSLeft(), this.testLeft)
        && t.checkExpect(this.testworld.moveSLeft(), new SpaceInvaders(this.leftMoved));
  }

  // tests moveSRight()
  Boolean testMoveSRight(Tester t) {
    return t.checkExpect(this.testRight.moveSRight(), this.testRight)
        && t.checkExpect(this.testworld.moveSRight(), new SpaceInvaders(this.rightMoved));
  }

  // tests tick()
  Boolean testTick(Tester t) {
    GameBoard local = new GameBoard(new Random(2));

    return t.checkExpect(local.tick(),
        new GameBoard(this.spaceShip,
            utility.buildInv(new Invader(new CartPt(90, 20), Color.red), 9),
            utility.buildInv(new Invader(new CartPt(90, 70), Color.red), 9),
            utility.buildInv(new Invader(new CartPt(90, 120), Color.red), 9),
            utility.buildInv(new Invader(new CartPt(90, 170), Color.red), 9),
            new ConsList<IGamePiece>(new IBullet(new CartPt(290, 30), Color.red),
                new MtList<IGamePiece>()),
            new MtList<IGamePiece>()));
  }

  // tests shootIB()
  Boolean testShootIB(Tester t) {
    GameBoard local = new GameBoard(new Random(2));

    return t.checkExpect(local.shootIB(), new ConsList<IGamePiece>(
        new IBullet(new CartPt(290, 20), Color.red), new MtList<IGamePiece>()));
  }

  // tests amountBullet
  Boolean testAmountBullet(Tester t) {
    return t.checkExpect(this.testgame.amountBullet(), false)
        && t.checkExpect(utility.buildDefault().amountBullet(), false)
        && t.checkExpect(this.fullSBulletGame.amountBullet(), true);
  }

  // tests shootSS()
  Boolean testShoot(Tester t) {
    return t.checkExpect(this.testgame.shootSS(), new GameBoard(this.spaceShip, this.invaderList1,
        this.invaderList2, this.invaderList3, this.invaderList4, this.loIBullet, this.shootNew));
  }

  // tests touchesBorder(int)
  Boolean testTouchesBorder(Tester t) {
    return t.checkExpect(this.testgame.touchesBorder(250), true)
        && t.checkExpect(this.testgame.touchesBorder(0), false);
  }

  // tests moveshipL()
  Boolean testMoveShipL(Tester t) {
    return t.checkExpect(this.testgame.moveShipL(),
        new GameBoard(new Spaceship(new CartPt(245, 750), Color.black), this.invaderList1,
            this.invaderList2, this.invaderList3, this.invaderList4, this.loIBullet,
            this.loSBullet));
  }

  // tests moveShipR()
  Boolean testMoveShipR(Tester t) {
    return t.checkExpect(this.testgame.moveShipR(),
        new GameBoard(new Spaceship(new CartPt(255, 750), Color.black), this.invaderList1,
            this.invaderList2, this.invaderList3, this.invaderList4, this.loIBullet,
            this.loSBullet));
  }

  // tests allInvaders()
  Boolean testAllInvaders(Tester t) {
    return t.checkExpect(this.testgame.allInvaders(),
        this.invaderList1
            .append(this.invaderList2.append(this.invaderList3.append(this.invaderList4))))
        && t.checkExpect(this.missingInvaders.allInvaders(),
            this.loinvader.append(this.invaderList3.append(this.invaderList4)));
  }

  // tests collisionIonSS
  Boolean testcollisionIonSS(Tester t) {
    return t.checkExpect(this.easygame.collisionIonSS(), false)
        && t.checkExpect(this.testgame.collisionIonSS(), false)
        && t.checkExpect(this.collisionGame.collisionIonSS(), true);
  }

  // tests worldEnds().
  Boolean testWorldEnds(Tester t) {
    return t.checkExpect(this.testworld.worldEnds(),
        new WorldEnd(false, this.testworld.makeScene()))
        && t.checkExpect(this.easyworld.worldEnds(), new WorldEnd(true, new WorldScene(500, 800)
            .placeImageXY(new TextImage("You Win!!!", Color.green), 250, 400)));
  }

  // tests makeAFinalScene()
  Boolean testMakeAFinalScene(Tester t) {
    return t.checkExpect(this.testworld.makeAFinalScene(),
        new WorldScene(500, 800).placeImageXY(new TextImage("You Lost", Color.red), 250, 400));
  }

  // tests makeFinalWin()
  Boolean testMakeFinalWin(Tester t) {
    return t.checkExpect(this.testworld.makeFinalWin(),
        new WorldScene(500, 800).placeImageXY(new TextImage("You Win!!!", Color.green), 250, 400));
  }

  // tests allMt()
  Boolean testAllMt(Tester t) {
    return t.checkExpect(this.testgame.allMt(), false)
        && t.checkExpect(this.easygame.allMt(), true);
  }

  // tests for allInvaderLocs()
  Boolean testAllInvadersLocs(Tester t) {
    return t.checkExpect(this.easygame.allInvadersLocs(), this.mtboard)
        && t.checkExpect(this.allInvLocs.allInvadersLocs(), this.invader.possContact(20));
  }
}
