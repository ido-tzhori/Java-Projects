import tester.Tester;

// to represent a Polynomial

class Polynomial {
  ILoMonomial monomials;

  Polynomial(ILoMonomial monomials) {
    if (monomials.sameDegree()) {
      // makes sure no Polynomial can be built if any of the Monomials in the
      // ILoMonomial share the same degree
      throw new IllegalArgumentException("Invalid ILoMonomials");
    }
    else {
      this.monomials = monomials;
    }
    /* fields: 
     *   this.monomials ... ILoMonomial
     *   
     * methods:
     *   this.samePolynomial(Polynomial) ... Boolean
     *   
     * methods for fields:
     *   this.monomials.removeZeros() ... ILoMonomials
     */
  }

  // checks to see that that zero-removed Polynomial is that same as this
  // Polynomial
  public Boolean samePolynomial(Polynomial that) {
    return this.monomials.removeZeros().sameILoMonomial(that.monomials.removeZeros());
  }
}

//to represent a list of Monomials

interface ILoMonomial {

  ILoMonomial removeZeros();

  Boolean sameDegree();

  Boolean sameDegreeHelp(Monomial that);

  Boolean sameILoMonomial(ILoMonomial that);

  Boolean containsMonomial(Monomial that);

  Boolean allZeros();

  ILoMonomial remove(Monomial that);
}

//to represent an empty list of Monomials

class MtLoMonomial implements ILoMonomial {
  
  /* fields: 
   *   none
   *   
   * methods:
   *   this.sameDegree() ... Boolean
   *   this.sameDegreeHelp(Monomial) ... Boolean
   *   this.removeZeros() ... ILoMonomial
   *   this.sameILoMonomial(ILoMonomial) ... Boolean
   *   this.containsMonomial(Monomial) ... Boolean
   *   this.allZeros() ... Boolean
   *   this.remove(Monomial) ... ILoMonomial
   * methods for fields:
   *   this.monomials.removeZeros() ... ILoMonomials
   *   
   */
  
  // checks if this has any monomials with the same degree
  public Boolean sameDegree() {
    return false;
  }

  // checks if this has that monomial in it
  public Boolean sameDegreeHelp(Monomial that) {
    return false;
  }

  // removes all monomials in this where the coefficient is equal to zero
  public ILoMonomial removeZeros() {
    return this;
  }

  // checks to see if that ILoMonomial contains all elements of this monomial and
  // vice versa (equality)
  public Boolean sameILoMonomial(ILoMonomial that) {
    return that.allZeros();
  }

  // checks to see if this ILoMonomial has that Monomial within it
  public Boolean containsMonomial(Monomial that) {
    return false;
  }

  // checks to see if all the Monomials in this ILoMonomails have a coefficient of
  // zero
  public Boolean allZeros() {
    return true;
  }

  // removes the first instance of that in this ILoMonomial
  public ILoMonomial remove(Monomial that) {
    return this;
  }
}

class ConsLoMonomial implements ILoMonomial {
  Monomial first;
  ILoMonomial rest;

  ConsLoMonomial(Monomial first, ILoMonomial rest) {
    this.first = first;
    this.rest = rest;
  }

  /* fields: 
   *   this.first ... Monomial
   *   this.rest ... ILoMonomial
   *   
   * methods:
   *   this.sameDegree() ... Boolean
   *   this.sameDegreeHelp(Monomial) ... Boolean
   *   this.removeZeros() ... ILoMonomial
   *   this.sameILoMonomial(ILoMonomial) ... Boolean
   *   this.containsMonomial(Monomial) ... Boolean
   *   this.allZeros() ... Boolean
   *   this.remove(Monomial) ... ILoMonomial
   * methods for fields:
   *   this.first.sameMono(that) ... Boolean
   *   this.first.sameMonoD(that) ... Boolean
   *   this.rest.sameDegree() ... Boolean
   *   this.rest.sameDegreeHelp(Monomial) ... Boolean
   *   this.rest.removeZeros() ... ILoMonomial
   *   this.rest.sameILoMonomial(ILoMonomial) ... Boolean
   *   this.rest.containsMonomial(Monomial) ... Boolean
   *   this.rest.allZeros() ... Boolean
   *   this.rest.remove(Monomial) ... ILoMonomial
   */
  
  // checks if this has any monomials with the same degree
  public Boolean sameDegree() {
    return this.rest.sameDegreeHelp(this.first) || this.rest.sameDegree();
  }

  // checks if this has that monomial in it
  public Boolean sameDegreeHelp(Monomial that) {
    if (this.first.sameMonoD(that)) {
      return true;
    }
    else {
      return this.rest.sameDegreeHelp(that);
    }
  }

  // removes all monomials in this where the coefficient is equal to zero
  public ILoMonomial removeZeros() {
    if (this.first.coefficient != 0) {
      return new ConsLoMonomial(this.first, this.rest.removeZeros());
    }
    else {
      return this.rest.removeZeros();
    }
  }

  // checks to see if that ILoMonomial contains all elements of this monomial and
  // vice versa (checks for equality)
  public Boolean sameILoMonomial(ILoMonomial that) {
    return that.containsMonomial(this.first) && this.rest.sameILoMonomial(that.remove(this.first));
  }

  // checks to see if this ILoMonomial has that Monomial within it
  public Boolean containsMonomial(Monomial that) {
    if (this.first.sameMono(that)) {
      return true;
    }
    else {
      return this.rest.containsMonomial(that);
    }
  }

  // checks to see if all the Monomials in this ILoMonomails have a coefficient of
  // zero
  public Boolean allZeros() {
    return this.first.coefficient == 0 && this.rest.allZeros();
  }

  // removes the first instance of that in this ILoMonomial

  public ILoMonomial remove(Monomial that) {
    if (this.first.sameMono(that)) {
      return this.rest;
    }
    else {
      return new ConsLoMonomial(this.first, this.rest.remove(that));
    }
  }
}

class Monomial {
  int degree;
  int coefficient;

  Monomial(int degree, int coefficient) {
    if (degree >= 0) {
      this.degree = degree;
    }

    else {
      // makes sure no Monomial can be built with a non-negative degree
      throw new IllegalArgumentException("Invalid degree: " + Integer.toString(degree));
    }

    this.coefficient = coefficient;
  }
  
  /* fields: 
   *   this.degree ... int
   *   this.coefficient ... int
   *   
   * methods:
   *   this.sameMonoD(Monomial) ... Boolean
   *   this.sameMono(Monomial) ... Boolean
   * methods for fields:
   *   
   */
  
  // checks to see if the degree of that Monomial is the same as the degree of
  // this Monomial
  public Boolean sameMonoD(Monomial that) {
    return this.degree == that.degree;
  }

  // checks to see if that Monomial is equal to this one
  public Boolean sameMono(Monomial that) {
    return this.degree == that.degree && this.coefficient == that.coefficient;
  }
}

class ExamplesPolynomials {
  Monomial mon1 = new Monomial(1, -2);
  Monomial mon2 = new Monomial(0, -4);
  Monomial mon3 = new Monomial(3, 5);
  Monomial mon4 = new Monomial(4, 21);
  Monomial mon5 = new Monomial(5, 8);
  Monomial mon6 = new Monomial(2, 0);
  Monomial smon1 = new Monomial(1, 2);
  Monomial smon2 = new Monomial(0, 89898);

  ILoMonomial ILoM1 = new ConsLoMonomial(mon1, new ConsLoMonomial(mon2, new MtLoMonomial()));
  ILoMonomial ILoM2 = new ConsLoMonomial(mon3, new MtLoMonomial());
  ILoMonomial ILoM3 = new ConsLoMonomial(mon4,
      new ConsLoMonomial(mon2, new ConsLoMonomial(mon6, new MtLoMonomial())));
  ILoMonomial mtLoM = new MtLoMonomial();
  ILoMonomial wLoM1 = new ConsLoMonomial(mon1, new ConsLoMonomial(smon1, new MtLoMonomial()));
  ILoMonomial wLoM2 = new ConsLoMonomial(mon1, new ConsLoMonomial(mon1, new MtLoMonomial()));
  ILoMonomial zeroLoM = new ConsLoMonomial(mon6, new MtLoMonomial());
  ILoMonomial ILoM11 = new ConsLoMonomial(mon1,
      new ConsLoMonomial(mon2, new ConsLoMonomial(mon6, new MtLoMonomial())));
  ILoMonomial ILoM0 = new ConsLoMonomial(mon6,
      new ConsLoMonomial(mon6, new ConsLoMonomial(mon6, new MtLoMonomial())));
  ILoMonomial ILoMd = new ConsLoMonomial(mon1,
      new ConsLoMonomial(mon2, new ConsLoMonomial(mon2, new MtLoMonomial())));
      
  Polynomial ply1 = new Polynomial(ILoM1);
  // Polynomial wply = new Polynomial(wLoM1);
  Polynomial mtply = new Polynomial(mtLoM);
  Polynomial ply2 = new Polynomial(ILoM2);
  Polynomial ply3 = new Polynomial(ILoM3);
  Polynomial ply11 = new Polynomial(ILoM11);
  Polynomial ply0 = new Polynomial(zeroLoM);

  boolean testsameMonoD(Tester t) {
    return t.checkExpect(this.mon1.sameMonoD(smon1), true)
        && t.checkExpect(this.mon1.sameMonoD(mon2), false);
  }

  boolean testsameMono(Tester t) {
    return t.checkExpect(this.mon1.sameMono(smon1), false)
        && t.checkExpect(this.mon1.sameMono(mon1), true)
        && t.checkExpect(this.mon1.sameMono(mon2), false);
  }

  boolean testsameDegree(Tester t) {
    return t.checkExpect(this.ILoM1.sameDegree(), false)
        && t.checkExpect(this.mtLoM.sameDegree(), false)
        && t.checkExpect(this.wLoM1.sameDegree(), true)
        && t.checkExpect(this.wLoM2.sameDegree(), true)
        && t.checkExpect(this.ILoMd.sameDegree(), true);
  }

  boolean testsameDegreeHelp(Tester t) {
    return t.checkExpect(this.ILoM1.sameDegreeHelp(mon2), true)
        && t.checkExpect(this.ILoM3.sameDegreeHelp(mon1), false)
        && t.checkExpect(this.ILoM3.sameDegreeHelp(mon4), true)
        && t.checkExpect(this.mtLoM.sameDegreeHelp(mon1), false);
  }

  boolean testremoveZeros(Tester t) {
    return t.checkExpect(this.ILoM3.removeZeros(),
        new ConsLoMonomial(mon4, new ConsLoMonomial(mon2, new MtLoMonomial())))
        && t.checkExpect(this.zeroLoM.removeZeros(), new MtLoMonomial())
        && t.checkExpect(this.ILoM1.removeZeros(), this.ILoM1);
  }

  boolean testcontainsMonomial(Tester t) {
    return t.checkExpect(this.ILoM1.containsMonomial(mon1), true)
        && t.checkExpect(this.ILoM1.containsMonomial(mon6), false)
        && t.checkExpect(this.mtLoM.containsMonomial(mon6), false);
  }

  boolean testsameILoMonials(Tester t) {
    return t.checkExpect(this.ILoM1.sameILoMonomial(ILoM1), true)
        && t.checkExpect(this.ILoM1.sameILoMonomial(ILoM2), false)
        && t.checkExpect(this.ILoM1.sameILoMonomial(ILoM11), true)
        && t.checkExpect(this.mtLoM.sameILoMonomial(ILoM0), true);
  }

  boolean testAllZeros(Tester t) {
    return t.checkExpect(this.ILoM1.allZeros(), false) && t.checkExpect(this.mtLoM.allZeros(), true)
        && t.checkExpect(this.ILoM0.allZeros(), true);
  }

  boolean testRemove(Tester t) {
    return t.checkExpect(this.ILoM1.remove(mon1), new ConsLoMonomial(mon2, new MtLoMonomial()))
        && t.checkExpect(this.mtLoM.remove(mon1), this.mtLoM)
        && t.checkExpect(this.ILoM1.remove(mon6), this.ILoM1);
  }

  boolean testsamePolynomial(Tester t) {
    return t.checkExpect(this.ply1.samePolynomial(ply2), false)
        && t.checkExpect(this.ply1.samePolynomial(ply11), true)
        && t.checkExpect(this.mtply.samePolynomial(ply0), true)
        && t.checkExpect(this.ply1.samePolynomial(ply1), true);
  }

  // tests whether code can create invalid monomials and polynomials
  boolean testExceptions(Tester t) {
    return t.checkConstructorException(new IllegalArgumentException("Invalid degree: -1"),
        "Monomial", -1, 5)
        && t.checkConstructorException(new IllegalArgumentException("Invalid ILoMonomials"),
            "Polynomial", ILoM0);
  }
}
