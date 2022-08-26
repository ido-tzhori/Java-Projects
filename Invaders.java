import tester.Tester;
import javalib.worldimages.*;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.awt.Color;
import javalib.funworld.*;

class SpaceInvaders extends World {
  GameBoard gameboardPieces;
  
  SpaceInvaders(GameBoard gameboardPieces) {
    this.gameboardPieces = gameboardPieces;
  }

  @Override
  public WorldScene makeScene() {
    // TODO Auto-generated method stub
    return this.gameboardPieces.makeBoard().fold(new Render(), new WorldScene(300, 600));
  }
}

class GameBoard {
  IList<IGamePiece> spaceShip;
  IList<IGamePiece> row1Inv;;
  IList<IGamePiece> row2Inv;
  IList<IGamePiece> row3Inv;
  IList<IGamePiece> row4Inv;
  IList<IGamePiece> iBullets;
  IList<IGamePiece> sBullets;
  
  //do we need any exceptions here??? for the amount of spaceships in a row
  //what about for the spaces between them?
  GameBoard(IList<IGamePiece> spaceShip, IList<IGamePiece> row1Inv, IList<IGamePiece> row2Inv,
      IList<IGamePiece> row3Inv, IList<IGamePiece> row4Inv, IList<IGamePiece> iBullets,
      IList<IGamePiece> sBullets) {
    this.spaceShip = spaceShip;
    this.row1Inv = row1Inv;
    this.row2Inv = row2Inv;
    this.row3Inv = row3Inv;
    this.row4Inv = row4Inv;
    this.iBullets = iBullets;
    this.sBullets = sBullets;
    
  }
  
  public IList<IGamePiece> makeBoard() {
    return this.spaceShip.append(this.row1Inv.append(this.row2Inv.append(this.row3Inv.append
        (this.row4Inv.append(this.iBullets.append(this.sBullets))))));
  }
  

}


interface IGamePiece {
  
  IGamePiece move(int move);
  
  WorldImage draw();
  
  CartPt getLocation();
}

abstract class AGamePiece implements IGamePiece {
  CartPt location;
  Color color;
  
  AGamePiece(CartPt location, Color color) {
    this.location = location;
    this.color = color;
    
  }
  
  public abstract WorldImage draw();
  
  public CartPt getLocation() {
    return this.location;
  }
  
}

class Spaceship extends AGamePiece {
  
  
  Spaceship(CartPt location, Color color) {
    super(location, color);
  }
  
  public IGamePiece move(int move) {
    return new Spaceship(this.location.moveLoc(move, 0), this.color);
  }
  
  public WorldImage draw() {
    return new RectangleImage(5, 10, "solid", this.color);
  }
}

class Invader extends AGamePiece {
  
  Invader(CartPt location, Color color) {
    super(location, color);
  }
  
  public WorldImage draw() {
    return new RectangleImage(5, 5, "solid", this.color);
  }
  
  public IGamePiece move(int move) {
    return this;
  }
}

class SBullet extends AGamePiece {
  
  SBullet(CartPt location, Color color) {
    super(location, color);
  }
  
  public WorldImage draw() {
    return new CircleImage(1, "solid", this.color);
  }
  
  public IGamePiece move(int move) {
    return new SBullet(this.location.moveLoc(0, -1), this.color);
  }
}

class IBullet extends AGamePiece {
  
  IBullet(CartPt location, Color color) {
    super(location, color);
  }
  
  public WorldImage draw() {
    return new CircleImage(1, "solid", this.color);
  }
  
  public IGamePiece move(int move) {
    return new IBullet(this.location.moveLoc(0, 1), this.color);
  }
}

interface IList<T> {
  //filter this list by the given predicate
  IList<T> filter(Predicate<T> pred);
  //maps a function onto every member of this list
  <Y> IList<Y> map(Function<T, Y> fun);
  //combines the items in this list using the given function
  <U> U fold(BiFunction<T, U, U> fun, U base);
  //checks if any item in this list returns true based on pred
  Boolean orMap(Predicate<T> pred);
  
  //checks if all items in this list return true based on pred
  Boolean andMap(Predicate<T> pred);
  
  //appends this list to the given list
  IList<T> append(IList <T> given);
  
}

class MtList<T> implements IList<T> {

  //filter this list by the given predicate
  public IList<T> filter(Predicate<T> pred) {
    return this;
  }

  //maps a function onto every member of this list
  public <Y> IList<Y> map(Function<T, Y> fun) {
    return new MtList<Y>();
  }

  //combines the items in this list using the given function
  public <U> U fold(BiFunction<T, U, U> fun, U base) {
    return base;
  }
  
  public Boolean orMap(Predicate<T> pred) {
    return false;
  }
  
  public Boolean andMap(Predicate<T> pred) {
    return true;
  }
  
  public IList<T> append(IList<T> given) {
    return given;
  }

}

class ConsList<T> implements IList<T> {
  T first;
  IList<T> rest;
  
  ConsList(T first, IList<T> rest) {
    this.first = first;
    this.rest = rest;
  }

  //filter this list by the given predicate
  public IList<T> filter(Predicate<T> pred) {
    if (pred.test(this.first)) {
      return new ConsList<T>(this.first, this.rest.filter(pred));
    }
    else {
      return this.rest.filter(pred);
    }
  }

  //maps a function onto every member of this list
  public <Y> IList<Y> map(Function<T, Y> fun) {
    return new ConsList<Y>(fun.apply(this.first), this.rest.map(fun));
  }
  
  public Boolean orMap(Predicate<T> pred) {
    return pred.test(this.first) || this.rest.orMap(pred); 
  }
  
  public Boolean andMap(Predicate<T> pred) {
    return pred.test(this.first) && this.rest.andMap(pred); 
  }
  

  //combines the items in this list using the given function
  public <U> U fold(BiFunction<T, U, U> fun, U base) {
    return fun.apply(this.first, this.rest.fold(fun, base));
  }
  
  public IList<T> append(IList<T> given) {
    return new ConsList<T>(this.first, this.rest.append(given));
  }


}



class MoveIBullets implements Function<IBullet, IBullet> {

  public IBullet apply(IBullet t) {
    return new IBullet(t.location.moveLoc(0, -1), t.color);
  }
}

class MoveSBullets implements Function<SBullet, SBullet> {

  public SBullet apply(SBullet t) {
    return new SBullet(t.location.moveLoc(0, +1), t.color);
  }
}



class SameCartPt implements Predicate<CartPt> {
  CartPt given;
  
  SameCartPt(CartPt given) {
    this.given = given;
  }
  public boolean test(CartPt t) {
    return given.x == t.x &&
        given.y == t.y;
  } 
}

class withinPoints implements Predicate<CartPt> {
  IList<CartPt> points;
  
  withinPoints(IList<CartPt> points) {
    this.points = points;
  }
  
  public boolean test(CartPt t) {
    return points.orMap(new SameCartPt(t));
  }

  
}

class Render implements BiFunction<IGamePiece, WorldScene, WorldScene> {

  public WorldScene apply(IGamePiece t, WorldScene u) {
    return u.placeImageXY(t.draw(), t.getLocation().x, t.getLocation().y);
  }
  
}

class appendPieces implements BiFunction<IList<IGamePiece>, IList<IGamePiece>, IList<IGamePiece>> {

  @Override
  public IList<IGamePiece> apply(IList<IGamePiece> t, IList<IGamePiece> u) {
    return t.append(u);
  }

}




class CartPt {
  int x;
  int y;

  CartPt(int x, int y) {
    this.x = x;
    this.y = y;
  }
  
  public CartPt moveLoc(int x, int y) {
    return new CartPt(this.x + x, this.y + y);
  }
  
}
class Utils {
  
  public IList<IGamePiece> buildInv(Invader base, int LengthAcc) {
    if (LengthAcc == 0) {
      return new MtList<Invader>(); }
      else {
        return new ConsList<Invader>
        (base, buildInv(new Invader
            (new CartPt(base.getLocation().x + 5, base.getLocation().y), base.color), LengthAcc - 1));
      }
    }
  }




class ExamplesGamePiece{
  CartPt topLeft = new CartPt(0, 0);
  CartPt shipStart = new CartPt(150, 580);
  CartPt rw1inv1 = new CartPt(0, 50);
  CartPt rw1inv2 = new CartPt(3, 20);
  CartPt rw1inv3 = new CartPt(4, 15);
  CartPt rw1inv4= new CartPt(1, 40);
  CartPt rw1inv5 = new CartPt(20, 100);
  CartPt rw1inv6 = new CartPt(15, 300);
  CartPt rw1inv7 = new CartPt(100, 200);
  CartPt rw1inv8 = new CartPt(200, 150);
  CartPt rw1inv9 = new CartPt(115, 20);
  CartPt rw2inv1 = new CartPt(0, 50);
  CartPt rw2inv2 = new CartPt(3, 20);
  CartPt rw2inv3 = new CartPt(4, 15);
  CartPt rw2inv4= new CartPt(1, 40);
  CartPt rw2inv5 = new CartPt(20, 100);
  CartPt rw2inv6 = new CartPt(15, 300);
  CartPt rw2inv7 = new CartPt(100, 200);
  CartPt rw2inv8 = new CartPt(200, 150);
  CartPt rw2inv9 = new CartPt(115, 20);
  CartPt rw3inv1 = new CartPt(0, 50);
  CartPt rw3inv2 = new CartPt(3, 20);
  CartPt rw3inv3 = new CartPt(4, 15);
  CartPt rw3inv4= new CartPt(1, 40);
  CartPt rw3inv5 = new CartPt(20, 100);
  CartPt rw3inv6 = new CartPt(15, 300);
  CartPt rw3inv7 = new CartPt(100, 200);
  CartPt rw3inv8 = new CartPt(200, 150);
  CartPt rw3inv9 = new CartPt(115, 20);
  CartPt rw4inv1 = new CartPt(0, 50);
  CartPt rw4inv2 = new CartPt(3, 20);
  CartPt rw4inv3 = new CartPt(4, 15);
  CartPt rw4inv4= new CartPt(1, 40);
  CartPt rw4inv5 = new CartPt(20, 100);
  CartPt rw4inv6 = new CartPt(15, 300);
  CartPt rw4inv7 = new CartPt(100, 200);
  CartPt rw4inv8 = new CartPt(200, 150);
  CartPt rw4inv9 = new CartPt(115, 20);
  
  Invader inv1 = new Invader(this.rw1inv1, Color.red);
  
  Utils utility = new Utils();
  
  
  IGamePiece spaceShip = new Spaceship(this.shipStart, Color.black);

  /*
  IGamePiece ibullet = new IBullet(this.invader2, Color.red);
  IGamePiece sbullet = new SBullet(this.bullet, Color.black);

  
  //checks render
  Boolean testRender(Tester t) {
    return t.checkExpect(this.board.fold(new Render(), new WorldScene(600, 500)), null);
  }
  */
  //checks BuildInv
  Boolean testBuildInv(Tester t) {
    return t.checkExpect(this.utility.buildInv(this.inv1, 0), new MtList<Invader>()) &&
        t.checkExpect(this.utility.buildInv(this.inv1, 3), new ConsList<Invader>(this.inv1,
            new ConsList<Invader>(new Invader(new CartPt(5,50), Color.red), new ConsList<Invader>
            (new Invader(new CartPt(10,50), Color.red), new MtList<Invader>()))));
  }
  
}
