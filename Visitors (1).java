import tester.Tester;
import java.util.function.BiFunction;
import java.util.function.Function;


//rerpresnets a visitor for an IArith
interface IArithVisitor<R> extends Function<IArith, R> {
  R visitConst(Const c);
  
  R visitUnary(UnaryFormula u);
  
  R visitBinary(BinaryFormula b);
  
}

//represents either a constant, unary formula,
// or binary formula
interface IArith {
  
  //accepts a visitor at this IArith
  <R> R accept(IArithVisitor<R> visitor);
}

//represents a constant
class Const implements IArith {
  double num;
  
  Const(double num) {
    this.num = num;
  }

  //accepts a visitor at this const
  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitConst(this);
  }
  
  
}

//represents a Unary Formula with a function
//that is applied to an IArith
class UnaryFormula implements IArith {
  Function<Double, Double> func;
  String name;
  IArith child;
  
  UnaryFormula(Function<Double, Double> func, String name, IArith child) {
    this.func = func;
    this.name = name;
    this.child = child;
  }

  //accepts a visitor at this UnaryFormula
  public <R> R accept(IArithVisitor<R> visitor) {
    
    return visitor.visitUnary(this);
  }
}

//represents A Binary formula that has a biFunction
// as well as 2 Ariths that the BiFunction is applied to
class BinaryFormula implements IArith {
  BiFunction<Double, Double, Double> func;
  String name;
  IArith left;
  IArith right;
  
  BinaryFormula(BiFunction<Double, Double, Double> func, String name, IArith left,
      IArith right) {
    this.func = func;
    this.name = name;
    this.left = left;
    this.right = right;
  }

  //accepts a visitor at this BinaryFormula
  public <R> R accept(IArithVisitor<R> visitor) {
    return visitor.visitBinary(this);
  }
  
  
}

//represents the addition binary Formula
class Plus implements BiFunction<Double, Double, Double> {

  //applies this function to 2 doubles, and adds them
  public Double apply(Double t, Double u) {
   
    return t + u;
  }
  
}

//represents the subtraction binary Formula
class Minus implements BiFunction<Double, Double, Double> {
  
  //applies this function to 2 doubles, and subtracts the second
  //from the first
  public Double apply(Double t, Double u) {
    return t - u;
  }
}

//represents the multiplication binary Formula
class Mul implements BiFunction<Double, Double, Double> {
  
  //applies this function to 2 doubles, multiplying them
  public Double apply(Double t, Double u) {
    return t * u;
  }
}

//represents the division binary formula
class Div implements BiFunction<Double, Double, Double> {
  
  //applies this function to 2 doubles, dividing the first
  //by the second
  public Double apply(Double t, Double u) {
    if (u == 0) {
      throw new RuntimeException("Cant be divided by 0");
    }
    else {
      return t / u;
    }
  }
}

//represents the negation Unary Formula
class Neg implements Function<Double, Double> {
  
  //applies this function to a double, and negates it
  public Double apply(Double t) {
    return -t;
  }
}

//represents the square Unary Formula
class Sqr implements Function<Double, Double> {
  
  //applies this function to a double
  //and squares it
  public Double apply(Double t) {
    return t * t;
  }
}

//A visitor function that evaluates an IArith
class EvalVisitor implements IArithVisitor<Double> {

  
  //applies this function to the given IArith
  //using double dispath to determine the type 
  public Double apply(IArith t) {
    
    return t.accept(this);
  }

  //returns the number of the constant
  public Double visitConst(Const c) {
    
    return c.num;
  }

  //evaluates the UnaryFunction by applying the function of the 
  //Unary to the evaluated child
  public Double visitUnary(UnaryFormula u) {
    
    return u.func.apply(this.apply(u.child));
  }

  //evaluates the bianry function by applying the function
  //of the binary to the evaluated left and right doubles
  public Double visitBinary(BinaryFormula b) {
    
    return b.func.apply(this.apply(b.left), this.apply(b.right));
  }
}

//A visitor on an IArith that prints the IArith
class PrintVisitor implements IArithVisitor<String> {

  //applies this function to a given IArith, using double dispatch
  //to determine what the IArtith is
  public String apply(IArith t) {
    
    return t.accept(this);
  }

  //returns the double of the given Constant
  //as a string
  public String visitConst(Const c) {
  
    return Double.toString(c.num);
  }

 
  //returns the given Unary formula as a string
  // with the name of the formula in the parenthesis
  //as well as the string for the child
  public String visitUnary(UnaryFormula u) {
    
    return "(" + u.name + " " + this.apply(u.child) + ")";
  }

  //returns the given binary Formula as a string
  // with the name of the formula in the parens
  // along with the strings for both the right and
  // the left IAriths
  public String visitBinary(BinaryFormula b) {
   
    return "(" + b.name + " " + this.apply(b.left) + " "
       + this.apply(b.right) + ")";
  }
  
}

//Visits an IArith, and Produces a new IArith
// where every const has been doubled
class DoublerVisitor implements IArithVisitor<IArith> {

  //applies this visitor to the given IArith
  //using double dispatch to determine which 
  // it goes to
  public IArith apply(IArith t) {
   
    return t.accept(this);
  }

  //creates a new Const with a doubled num
  public IArith visitConst(Const c) {
    
    return new Const(c.num * 2);
  }


  //creates a new Unary formula, with its child having
  // this applied to it
  public IArith visitUnary(UnaryFormula u) {
    
    return new UnaryFormula(u.func, u.name, this.apply(u.child));
  }

 
  //creates a new Binary Formula, with its left and 
  //right IAriths having this applied to it
  public IArith visitBinary(BinaryFormula b) {
   
    return new BinaryFormula(b.func, b.name, this.apply(b.left),
        this.apply(b.right));
  }
  
}

//determines if a negative number is never encountered 
//during the Evaluation of the IArith
class NoNegativeResults implements IArithVisitor<Boolean> {

  // Applies this visitor to the given IArith
  //using double dispatch to determine which IArith is 
  // being applied to
  public Boolean apply(IArith t) {
    
    return t.accept(this);
  }

  //determines if this constant has a double
  // that is positive
  public Boolean visitConst(Const c) {
    
    return c.num >= 0.0;
  }

  //determines if the evalution of the given unary formula
  // is positive, and that the child IArith is also positive
  public Boolean visitUnary(UnaryFormula u) {
    
    return new EvalVisitor().apply(u) >= 0.0 &&
        this.apply(u.child);
  }

  //determines if this Binary formula evaluates to a positive
  // double, as well as both the left and right IAriths
  public Boolean visitBinary(BinaryFormula b) {

    return new EvalVisitor().apply(b) >= 0.0 &&
        this.apply(b.left) && this.apply(b.right);
  }
  
}


class ExamplesArith {
  IArith const1 = new Const(1.0);
  Const const2 = new Const(2.0);
  IArith const5 = new Const(5.0);
  IArith const7 = new Const(7.0);
  IArith constNeg2 = new Const(-2.0);
  IArith const62 = new Const(6.2);
  
  IArith neg = new UnaryFormula(new Neg(), "neg", this.const5);
  IArith sqr = new UnaryFormula(new Sqr(), "sqr", this.const7);
  UnaryFormula neg1 =  new UnaryFormula(new Neg(), "neg", this.const5);
  UnaryFormula sqr1 =  new UnaryFormula(new Sqr(), "sqr", this.const7);
  IArith doubleneg = new UnaryFormula(new Neg(), "neg", this.constNeg2);
  IArith plus = new BinaryFormula(new Plus(), "plus", this.const1, this.const5);
  IArith tree1 = new BinaryFormula(new Plus(), "plus", this.plus, this.neg);
  IArith tree2 = new BinaryFormula(new Minus(), "minus", this.const5, this.const7);
  IArith tree3 = new BinaryFormula(new Plus(), "plus", this.plus, this.doubleneg);
  IArith tree4 = new BinaryFormula(new Mul(), "mul", this.tree3, this.const5);
  UnaryFormula tree = new UnaryFormula(new Neg(), "neg", this.tree1);
  BinaryFormula binary1 = new BinaryFormula(new Plus(), "plus", this.plus, this.neg);
  IArith mult = new BinaryFormula(new Mul(), "mul", this.const62, this.tree4);
  IArith div = new BinaryFormula(new Div(), "div", this.const62, this.const2);

  
  
  
  
  EvalVisitor eval = new EvalVisitor();
  PrintVisitor print = new PrintVisitor();
  DoublerVisitor doubler = new DoublerVisitor();
  NoNegativeResults noNeg = new NoNegativeResults();
  
  //tests visitConst
  void testVisitConst(Tester t) {
    t.checkExpect(noNeg.visitConst(this.const2), true);
    t.checkExpect(doubler.visitConst(this.const2), new Const(4.0));
    t.checkExpect(print.visitConst(this.const2), "2.0");
    t.checkExpect(eval.visitConst(this.const2), 2.0);
  }
  
  //Tests visitUnary
  void testVisitUnary(Tester t) {
    t.checkExpect(noNeg.visitUnary(this.neg1), false);
    t.checkExpect(doubler.visitUnary(this.neg1),
        new UnaryFormula(new Neg(), "neg", new Const(10.0)));
    t.checkExpect(print.visitUnary(this.neg1), "(neg 5.0)");
    t.checkExpect(eval.visitUnary(this.tree), -1.0);
  }
  
  
  //Tests visitBinary
  void testVisitBinary(Tester t) {
    t.checkExpect(noNeg.visitBinary(this.binary1), false);
    t.checkExpect(doubler.visitBinary(this.binary1), new BinaryFormula(
        new Plus(), "plus", new BinaryFormula(new Plus(), "plus",
            new Const(2.0), new Const(10.0)), new UnaryFormula(
               new Neg(), "neg", new Const(10.0))));
    t.checkExpect(print.visitBinary(this.binary1),
        "(plus (plus 1.0 5.0) (neg 5.0))" );
    t.checkExpect(eval.visitBinary(this.binary1), 1.0);    
  }
  
  
  //tests EvalVisitor
  void testEvalVisitor(Tester t) {
    t.checkExpect(eval.apply(neg), -5.0);
    t.checkExpect(eval.apply(plus), 6.0);
    t.checkExpect(eval.apply(tree1), 1.0);
    t.checkExpect(eval.apply(tree4), 40.0);
    t.checkExpect(eval.apply(mult), 248.0);
    t.checkExpect(eval.apply(div), 3.1);
  }
  
  //tests PrintVisitor 
  void testPrintVisitor(Tester t) {
    t.checkExpect(print.apply(neg), "(neg 5.0)");
    t.checkExpect(print.apply(plus), "(plus 1.0 5.0)");
    t.checkExpect(print.apply(tree1), "(plus (plus 1.0 5.0) (neg 5.0))");
  }
  
  //tests DoublerVisitor
  void testDoublerVisitor(Tester t) {
    t.checkExpect(doubler.apply(const1), new Const(2.0));
    t.checkExpect(doubler.apply(neg), new UnaryFormula(
        new Neg(), "neg", new Const(10.0)));
    t.checkExpect(doubler.apply(tree1), new BinaryFormula(
        new Plus(), "plus", new BinaryFormula(new Plus(), "plus",
            new Const(2.0), new Const(10.0)), new UnaryFormula(
               new Neg(), "neg", new Const(10.0))));
  }
  
  //tests NoNegativeResults
  void testNoNegativeResults(Tester t) {
    t.checkExpect(noNeg.apply(const1), true);
    t.checkExpect(noNeg.apply(constNeg2), false);
    t.checkExpect(noNeg.apply(neg), false);
    t.checkExpect(noNeg.apply(plus), true);
    t.checkExpect(noNeg.apply(tree1), false);
    t.checkExpect(noNeg.apply(tree2), false);
    t.checkExpect(noNeg.apply(tree3), false);
  }
  
  //tests Accept(IArtithVisitor)
  void testAccept(Tester t) {
    t.checkExpect(this.const2.accept(this.eval), 2.0);
    t.checkExpect(this.neg1.accept(this.print), "(neg 5.0)");
    t.checkExpect(this.binary1.accept(this.eval), 1.0);
  }
  
  //tests for plus and minus
  void testPlusAndMinus(Tester t) {
    t.checkExpect(new Plus().apply(5.0, 3.0), 8.0);
    t.checkExpect(new Plus().apply(0.0, 0.0), 0.0);
    t.checkExpect(new Plus().apply(5.0, 0.0), 5.0);
    t.checkExpect(new Minus().apply(5.0, 3.0), 2.0);
    t.checkExpect(new Minus().apply(5.0, 8.0), -3.0);
    t.checkExpect(new Minus().apply(5.0, 0.0), 5.0); 
  }
  
  //tests for mult and divide
  void testMultAndDivid(Tester t) {
    t.checkExpect(new Mul().apply(2.0,  3.0), 6.0);
    t.checkExpect(new Mul().apply(2.0, 0.0), 0.0);
    t.checkExpect(new Mul().apply(0.0, 0.0), 0.0);
    t.checkExpect(new Div().apply(5.0, 1.0), 5.0);
    t.checkExpect(new Div().apply(0.0, 1.0), 0.0);
    t.checkExpect(new Div().apply(6.0, 3.0), 2.0);
    t.checkExpect(new Div().apply(5.0, 10.0), .5);

  }
  
  //tests for square and neg
  void testSqrAndNeg(Tester t) {
    t.checkExpect(new Neg().apply(0.0), 0.0);
    t.checkExpect(new Neg().apply(2.0), -2.0);
    t.checkExpect(new Neg().apply(-2.0), 2.0);
    t.checkExpect(new Sqr().apply(1.0), 1.0);
    t.checkExpect(new Sqr().apply(-2.0), 4.0);
    t.checkExpect(new Sqr().apply(3.0), 9.0);
    t.checkExpect(new Sqr().apply(0.0), 0.0);
  }
}















