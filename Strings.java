// CS 2510, Assignment 3

import tester.Tester;

// to represent a list of Strings
interface ILoString {

  String combine();

  ILoString findAndReplace(String str1, String str2);

  Boolean anyDupes();

  Boolean anyDupesHelp(String that);

  ILoString sort();

  ILoString sortHelp(String that);

  Boolean isSorted();

  Boolean isSortedHelp(String that);

  ILoString interleave(ILoString that);

  ILoString merge(ILoString that);

  ILoString reverse();

  ILoString reverseHelp(String str);

  String reverseConcat();

  Boolean isDoubledList();

  Boolean isDoubledListHelp(String str);

  Boolean isPalindromeList();
}

// to represent an empty list of Strings
class MtLoString implements ILoString {
  
  /* fields: 
   *   none
   *   
   * methods:
   *   this.combine() ... String
   *   this.findAndReplace(String, String) ... ILoString
   *   this.anyDupes() ... Boolean
   *   this.anyDupesHelp(String) ... Boolean
   *   this.sort() ... ILoString
   *   this.sortHelp(String) ... ILoString
   *   this.isSorted() ... Boolean
   *   this.isSortedHelp(String) ... Boolean
   *   this.interleave(ILoString) ... ILoString
   *   this.merge() ... ILoString
   *   this.reverse() ... ILoString
   *   this.reverseHelp(String) ... ILoString
   *   this.reverseConcat() ... String
   *   this.isDoubledList() ... Boolean
   *   this.isDoubledListHelp(String) ... Boolean
   *   this.isPalindromeList() ... Boolean
   *   
   *   
   * methods for fields:
   *   none
   */
  
  MtLoString() {
  }

  // combine all Strings in this list into one
  public String combine() {
    return "";
  }

  // finds every occurrence of one string and replaces it with the other string
  public ILoString findAndReplace(String str1, String str2) {
    return this;
  }

  public Boolean anyDupes() {
    return false;
  }

  // checks to see if this string is equal to that
  public Boolean anyDupesHelp(String that) {
    return false;
  }

  // sorts all elements in alphabetical order
  public ILoString sort() {
    return this;
  }
  // determines if one string is lexicographically before another

  public ILoString sortHelp(String that) {
    return new ConsLoString(that, new MtLoString());
  }

  // is this list sorted lexicographically
  public Boolean isSorted() {
    return true;
  }

  // does that string come before this string
  public Boolean isSortedHelp(String that) {
    return true;
  }

  // takes this list of Strings and a given list of Strings,
  // and produces a list where the first, third, fifth... elements are from this
  // list,
  // and the second, fourth, sixth... elements are from the given list
  public ILoString interleave(ILoString that) {
    return that;
  }

  // merges two ordered lists into one big order list
  public ILoString merge(ILoString that) {
    return that;
  }

  // reverses all elements in the list
  public ILoString reverse() {
    return this;
  }

  // adds this string to the last element of the last
  public ILoString reverseHelp(String str) {
    return new ConsLoString(str, new MtLoString());
  }

  // reverses elements in list and concatenates all string into a mega string
  public String reverseConcat() {
    return "";
  }

  // determines if this list has any equal string values in position 1&2, 3&4, ...
  public Boolean isDoubledList() {
    return true;
  }

  // checks to see if the first of this is equals to a string along with checking
  // if the rest of the list has any doubled strings
  public Boolean isDoubledListHelp(String str) {
    return false;
  }

  // is this list read the same in either order
  public Boolean isPalindromeList() {
    return true;
  }
}

// to represent a nonempty list of Strings
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
   *   
   * methods:
   *   this.combine() ... String
   *   this.findAndReplace(String, String) ... ILoString
   *   this.anyDupes() ... Boolean
   *   this.anyDupesHelp(String) ... Boolean
   *   this.sort() ... ILoString
   *   this.sortHelp(String) ... ILoString
   *   this.isSorted() ... Boolean
   *   this.isSortedHelp(String) ... Boolean
   *   this.interleave(ILoString) ... ILoString
   *   this.merge() ... ILoString
   *   this.reverse() ... ILoString
   *   this.reverseHelp(String) ... ILoString
   *   this.reverseConcat() ... String
   *   this.isDoubledList() ... Boolean
   *   this.isDoubledListHelp(String) ... Boolean
   *   this.isPalindromeList() ... Boolean
   *   
   *   
   * methods for fields:
   *   this.first.concat(String) ... String
   *   this.first.equals(String) ... Boolean
   *   this.first.compareTo(String) ... int
   *   this.rest.combine() ... String
   *   this.rest.findAndReplace(String, String) ... ILoString
   *   this.rest.anyDupes() ... Boolean
   *   this.rest.anyDupesHelp(String) ... Boolean
   *   this.rest.sort() ... ILoString
   *   this.rest.sortHelp(String) ... ILoString
   *   this.rest.isSorted() ... Boolean
   *   this.rest.isSortedHelp(String) ... Boolean
   *   this.rest.interleave(ILoString) ... ILoString
   *   this.rest.merge() ... ILoString
   *   this.rest.reverse() ... ILoString
   *   this.rest.reverseHelp(String) ... ILoString
   *   this.rest.reverseConcat() ... String
   *   this.rest.isDoubledList() ... Boolean
   *   this.rest.isDoubledListHelp(String) ... Boolean
   *   this.rest.isPalindromeList() ... Boolean
   *   
   */

  // combine all Strings in this list into one
  public String combine() {
    return this.first.concat(this.rest.combine());
  }

  // finds every occurrence of one string and replaces it with the other string

  public ILoString findAndReplace(String str1, String str2) {
    if (this.first.compareTo(str1) == 0) {
      return new ConsLoString(str2, this.rest.findAndReplace(str1, str2));
    }

    else {
      return new ConsLoString(this.first, this.rest.findAndReplace(str1, str2));
    }
  }

  public Boolean anyDupes() {
    return this.rest.anyDupesHelp(this.first);
  }

  // checks to see if that string is seen in this list
  public Boolean anyDupesHelp(String that) {
    if (this.first.compareTo(that) == 0) {
      return true;
    }
    else {
      return this.rest.anyDupesHelp(that);
    }
  }

  // sorts all elements in alphabetical order
  public ILoString sort() {
    return this.rest.sort().sortHelp(this.first);
  }

  // determines if one string is lexicographically before the first of this list
  // and then places that string in order accordingly
  public ILoString sortHelp(String that) {
    if (this.first.toLowerCase().compareTo(that.toLowerCase()) <= 0) {
      return new ConsLoString(this.first, this.rest.sortHelp(that));
    }
    else {
      return new ConsLoString(that, this);
    }
  }

  // checks to see if this list is sorted lexicographically
  public Boolean isSorted() {
    return this.rest.isSortedHelp(this.first) && this.rest.isSorted();
  }

  // sees if that string comes before this string
  public Boolean isSortedHelp(String that) {
    return this.first.toLowerCase().compareTo(that.toLowerCase()) >= 0;
  }

  // takes this list of Strings and a given list of Strings,
  // and produces a list where the first, third, fifth... elements are from this
  // list,
  // and the second, fourth, sixth... elements are from the given list
  public ILoString interleave(ILoString that) {
    return new ConsLoString(this.first, that.interleave(this.rest));
  }

  // merges two ordered lists into one big order list
  public ILoString merge(ILoString that) {
    return that.merge(this.rest).sortHelp(this.first);
  }

  // reverses all elements in the list
  public ILoString reverse() {
    return this.rest.reverse().reverseHelp(this.first);
  }

  // adds string to last element of the list
  public ILoString reverseHelp(String str) {
    return new ConsLoString(this.first, this.rest.reverseHelp(str));
  }

  // reverses elements in list and concatenates all string into a mega string
  public String reverseConcat() {
    return this.reverse().combine();
  }

  // determines if this list has any equal string values in position 1&2, 3&4, ...
  public Boolean isDoubledList() {
    return this.rest.isDoubledListHelp(this.first);
  }

  // checks to see if the first of this is equals to a string along with checking
  // if the rest of the list has any doubled strings
  public Boolean isDoubledListHelp(String str) {
    return this.first.equals(str) && this.rest.isDoubledList();
  }

  // is this list read the same in either order
  public Boolean isPalindromeList() {
    return this.interleave(this.reverse()).isDoubledList();
  }
}

// to represent examples for lists of strings
class ExamplesStrings {

  ILoString mary = new ConsLoString("Mary ", new ConsLoString("had ", new ConsLoString("a ",
      new ConsLoString("little ", new ConsLoString("lamb.", new MtLoString())))));
  ILoString abcw = new ConsLoString("a", new ConsLoString("d",
      new ConsLoString("e", new ConsLoString("c", new ConsLoString("b", new MtLoString())))));
  ILoString abcr = new ConsLoString("a", new ConsLoString("b",
      new ConsLoString("c", new ConsLoString("d", new ConsLoString("e", new MtLoString())))));
  ILoString LoS1 = new ConsLoString("1", new ConsLoString("2",
      new ConsLoString("3", new ConsLoString("4", new ConsLoString("1", new MtLoString())))));
  ILoString LoS2 = new ConsLoString("hi", new ConsLoString("hi", new MtLoString()));
  ILoString mt = new MtLoString();
  ILoString ab = new ConsLoString("a", new MtLoString());
  ILoString doubled = new ConsLoString("a",
      new ConsLoString("a", new ConsLoString("b", new ConsLoString("b", new MtLoString()))));
  ILoString doublednr = new ConsLoString("a",
      new ConsLoString("a", new ConsLoString("b", new ConsLoString("c", new MtLoString()))));
  ILoString doubledhelp = new ConsLoString("b", new ConsLoString("a",
      new ConsLoString("a", new ConsLoString("c", new ConsLoString("c", new MtLoString())))));
  ILoString palin = new ConsLoString("a",
      new ConsLoString("b", new ConsLoString("a", new MtLoString())));
  ILoString atest = new ConsLoString("a",
      new ConsLoString("a", new ConsLoString("a", new MtLoString())));

  // test the method combine for the lists of Strings
  boolean testCombine(Tester t) {
    return t.checkExpect(this.mary.combine(), "Mary had a little lamb.");
  }

  boolean testfindAndReplace(Tester t) {
    return t.checkExpect(this.mary.findAndReplace("bill", "bob"), mary)
        && t.checkExpect(this.mary.findAndReplace("Mary ", "Bill "),
            new ConsLoString("Bill ",
                new ConsLoString("had ",
                    new ConsLoString("a ",
                        new ConsLoString("little ", new ConsLoString("lamb.", new MtLoString()))))))
        && t.checkExpect(this.LoS1.findAndReplace("1", "2"),
            new ConsLoString("2",
                new ConsLoString("2",
                    new ConsLoString("3",
                        new ConsLoString("4", new ConsLoString("2", new MtLoString()))))))
        && t.checkExpect(this.mt.findAndReplace("A", "a"), mt);

  }

  boolean testanyDupes(Tester t) {
    return t.checkExpect(this.mary.anyDupes(), false) && t.checkExpect(this.LoS2.anyDupes(), true)
        && t.checkExpect(this.mt.anyDupes(), false);
  }

  boolean testanyDupesHelp(Tester t) {
    return t.checkExpect(this.mary.anyDupesHelp("Mary "), true)
        && t.checkExpect(this.abcr.anyDupesHelp("b"), true)
        && t.checkExpect(this.abcr.anyDupesHelp("z"), false)
        && t.checkExpect(this.mt.anyDupesHelp("b"), false);
  }

  boolean testsort(Tester t) {
    return t.checkExpect(this.abcw.sort(), new ConsLoString("a",
        new ConsLoString("b",
            new ConsLoString("c", new ConsLoString("d", new ConsLoString("e", new MtLoString()))))))
        && t.checkExpect(this.mary.sort(),
            new ConsLoString("a ",
                new ConsLoString("had ",
                    new ConsLoString("lamb.",
                        new ConsLoString("little ", new ConsLoString("Mary ", new MtLoString()))))))
        && t.checkExpect(this.mt.sort(), this.mt);
  }

  boolean testsortHelp(Tester t) {
    return t.checkExpect(this.mary.sortHelp("a"),
        new ConsLoString("a",
            new ConsLoString("Mary ", new ConsLoString("had ",
                new ConsLoString("a ",
                    new ConsLoString("little ", new ConsLoString("lamb.", new MtLoString())))))))
        && t.checkExpect(this.mt.sortHelp("a"), new ConsLoString("a", new MtLoString()));
  }

  boolean testinterleave(Tester t) {
    return t
        .checkExpect(this.abcr.interleave(this.mary), new ConsLoString("a",
            new ConsLoString("Mary ", new ConsLoString("b", new ConsLoString("had ",
                new ConsLoString("c", new ConsLoString("a ", new ConsLoString("d",
                    new ConsLoString("little ",
                        new ConsLoString("e", new ConsLoString("lamb.", new MtLoString())))))))))))
        && t.checkExpect(this.mt.interleave(this.mary), this.mary)
        && t.checkExpect(this.abcw.interleave(this.ab),
            new ConsLoString("a",
                new ConsLoString("a",
                    new ConsLoString("d",
                        new ConsLoString("e",
                            new ConsLoString("c", new ConsLoString("b", new MtLoString())))))))
        && t.checkExpect(this.mt.interleave(this.mt), mt)
        && t.checkExpect(this.mary.interleave(this.mt), this.mary);
  }

  boolean testisSorted(Tester t) {
    return t.checkExpect(this.abcw.isSorted(), false) && t.checkExpect(this.abcr.isSorted(), true)
        && t.checkExpect(this.mt.isSorted(), true) && t.checkExpect(this.atest.isSorted(), true);
  }

  boolean testisSortedHelp(Tester t) {
    return t.checkExpect(this.LoS2.isSortedHelp("zi"), false)
        && t.checkExpect(this.mary.isSortedHelp("Aary"), true)
        && t.checkExpect(this.mt.isSortedHelp("Bob"), true);
  }

  boolean testMerge(Tester t) {
    return t
        .checkExpect(this.abcr.merge(mary),
            new ConsLoString("a",
                new ConsLoString("a ",
                    new ConsLoString("b",
                        new ConsLoString("c",
                            new ConsLoString("d",
                                new ConsLoString("e",
                                    new ConsLoString("had ", new ConsLoString("lamb.",
                                        new ConsLoString("little ",
                                            new ConsLoString("Mary ", new MtLoString())))))))))))
        && t.checkExpect(this.LoS2.merge(this.mt), this.LoS2)
        && t.checkExpect(this.mt.merge(this.mt), mt);
  }

  boolean testreverseHelp(Tester t) {
    return t.checkExpect(this.abcr.reverseHelp("hi"),
        new ConsLoString("a",
            new ConsLoString("b",
                new ConsLoString("c",
                    new ConsLoString("d",
                        new ConsLoString("e", new ConsLoString("hi", new MtLoString())))))))
        && t.checkExpect(this.mt.reverseHelp("hi"), new ConsLoString("hi", new MtLoString()))
        && t.checkExpect(this.mt.reverseHelp(""), new ConsLoString("", new MtLoString()));
  }

  boolean testreverse(Tester t) {
    return t.checkExpect(this.abcr.reverse(), new ConsLoString("e",
        new ConsLoString("d",
            new ConsLoString("c", new ConsLoString("b", new ConsLoString("a", new MtLoString()))))))
        && t.checkExpect(this.mt.reverse(), mt)
        && t.checkExpect(this.LoS1.reverse(),
            new ConsLoString("1", new ConsLoString("4", new ConsLoString("3",
                new ConsLoString("2", new ConsLoString("1", new MtLoString()))))));
  }

  boolean testreverseConcat(Tester t) {
    return t.checkExpect(this.abcr.reverseConcat(), "edcba")
        && t.checkExpect(this.abcw.reverseConcat(), "bceda")
        && t.checkExpect(this.mt.reverseConcat(), "");
  }

  boolean testisDoubledListHelp(Tester t) {
    return t.checkExpect(this.doubledhelp.isDoubledListHelp("b"), true)
        && t.checkExpect(this.abcr.isDoubledListHelp("a"), false)
        && t.checkExpect(this.mt.isDoubledListHelp("bababooey"), false);
  }

  boolean testisDoubledList(Tester t) {
    return t.checkExpect(this.abcr.isDoubledList(), false)
        && t.checkExpect(this.doubled.isDoubledList(), true)
        && t.checkExpect(this.mt.isDoubledList(), true)
        && t.checkExpect(this.doublednr.isDoubledList(), false);
  }

  boolean testisPalindromeList(Tester t) {
    return t.checkExpect(this.palin.isPalindromeList(), true)
        && t.checkExpect(this.abcr.isPalindromeList(), false)
        && t.checkExpect(this.mt.isPalindromeList(), true);
  }
}