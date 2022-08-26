import tester.*;  
import javalib.worldimages.*;
import javalib.funworld.*;
import java.awt.Color;
import java.util.Random;

// represents the Mastermind game
class Mastermind extends World {
  boolean duplicatesAllowed; // are duplicate colors allowed in the sequence?
  int sequenceLength; // the length of the sequence
  int numGuesses; // the number of guesses that a player is allowed
  ILoColor possibleColors; // a list of all possible colors a player is allowed to guess

  ILoColor sequence; // the randomly generated sequence that the player tries to guess
  Random rand; // the Random object used to generate random numbers
  ILoColor unfinishedGuess; // a list representing an unfinished guess
  ILoLoColor finishedGuesses; // a list of lists of guesses

  int circleRadius = 20; // the radius of the circle

  // game constructor: new random game is generated every time the constructor is called
  Mastermind(boolean duplicatesAllowed, int sequenceLength, int numGuesses, 
      ILoColor possibleColors) {
    if (possibleColors.length() == 0) {
      throw new IllegalArgumentException("The number of possible colors must be greater than 0");
    }
    else {
      this.possibleColors = possibleColors;
    }

    if (!duplicatesAllowed && (sequenceLength > possibleColors.length())) {
      throw new IllegalArgumentException(
          "The length of the sequence cannot be greater than the length of the"
              + "list of colors when duplicates are not allowed");
    } 
    else if (sequenceLength <= 0) {
      throw new IllegalArgumentException("The sequence length must be greater than 0");
    }
    else {
      this.sequenceLength = sequenceLength;
    }

    if (numGuesses <= 0) {
      throw new IllegalArgumentException("The number of guesses allowed must be greater than 0");
    }
    else {
      this.numGuesses = numGuesses;
    }

    this.duplicatesAllowed = duplicatesAllowed;
    this.rand = new Random();
    this.sequence = generateRandomSequence();
    this.unfinishedGuess = new MtLoColor();
    this.finishedGuesses = new MtLoLoColor();
  }

  // constructor used for testing: generates the same sequence every time
  Mastermind(boolean duplicatesAllowed, int sequenceLength, int numGuesses, 
      ILoColor possibleColors, Random rand) {
    if (possibleColors.length() == 0) {
      throw new IllegalArgumentException("The number of possible colors must be greater than 0");
    }
    else {
      this.possibleColors = possibleColors;
    }

    if (!duplicatesAllowed && (sequenceLength > possibleColors.length())) {
      throw new IllegalArgumentException(
          "The length of the sequence cannot be greater than the length of the"
              + "list of colors when duplicates are not allowed");
    } 
    else if (sequenceLength <= 0) {
      throw new IllegalArgumentException("The sequence length must be greater than 0");
    }
    else {
      this.sequenceLength = sequenceLength;
    }

    if (numGuesses <= 0) {
      throw new IllegalArgumentException("The number of guesses allowed must be greater than 0");
    }
    else {
      this.numGuesses = numGuesses;
    }

    this.duplicatesAllowed = duplicatesAllowed;
    this.rand = rand;
    this.sequence = generateRandomSequence();
    this.unfinishedGuess = new MtLoColor();
    this.finishedGuesses = new MtLoLoColor();
  }

  // constructor used while running game: addition of finished and unfinished guesses to the world
  Mastermind(boolean duplicatesAllowed, int sequenceLength, int numGuesses, 
      ILoColor possibleColors, Random rand, ILoColor sequence, 
      ILoColor unfinishedGuess, ILoLoColor finishedGuesses) {
    this.duplicatesAllowed = duplicatesAllowed;
    this.sequenceLength = sequenceLength;
    this.numGuesses = numGuesses;
    this.possibleColors = possibleColors;
    this.rand = rand;
    this.sequence = sequence;
    this.unfinishedGuess = unfinishedGuess;
    this.finishedGuesses = finishedGuesses;
  }

  /* fields:
   *  this.duplicatesAllowed ... boolean
   *  this.sequenceLength ... int
   *  this.numGuesses ... int
   *  this.possibleColors ... ILoColor
   *  this.rand ... Random
   *  this.sequence ... ILoColor
   *  this.unfinishedGuess ... ILoColor
   *  this.finishedGuesses ... ILoLoColor
   * 
   * methods:
   *  this.generateRandomColor() ... Color
   *  this.generateRandomSequence() ... ILoColor
   *  this.generateRandomSequenceHelp(ILoColor reference, ILoColor acc) ... ILoColor
   *  this.getExactMatches(ILoColor guess, ILoColor answer) ... int
   *  this.getInexactMatches(ILoColor guess, ILoColor answer) ... int
   *  this.onKeyEvent(String key) ... World
   *  this.maybeUpdateNum(int key) ... World
   *  this.maybeUpdateEnter() ... World
   *  this.maybeUpdateDelete() ... World
   *  this.makeScene() ... WorldScene
   *  this.drawBottomRow() ... WorldImage
   *  this.drawFinishedGuesses() ... WorldImage
   *  this.drawUnfinishedGuesses() ... WorldImage
   *  this.drawTopRow() ... WorldImage
   *  this.getBackground() ... WorldImage
   *  this.drawEverything() ... WorldImage
   *  this.lastScene() ... WorldScene
   * 
   * methods of fields:
   *  this.possibleColors ... everything in the ILoColor template
   *  this.sequence ... everything in the ILoColor template
   *  this.unfinishedGuess ... everything in the ILoColor template
   *  this.finishedGuesses ... everything in the ILoLoColor template
   */

  // Generates a random Color from the given ILoColor
  public Color generateRandomColor(ILoColor reference) {
    int index = rand.nextInt(reference.length());
    return reference.getColorAtIndex(index);
  }

  // Generates a random sequence of colors from this.possibleColors
  public ILoColor generateRandomSequence() {
    return this.generateRandomSequenceHelp(this.possibleColors, new MtLoColor());
  }

  // Helper for generateRandomSequence()
  public ILoColor generateRandomSequenceHelp(ILoColor reference, ILoColor acc) {
    if (acc.length() == this.sequenceLength) {
      return acc;
    }
    else {
      Color c = this.generateRandomColor(reference);
      if (this.duplicatesAllowed) {
        return generateRandomSequenceHelp(
            reference, acc.append(new ConsLoColor(c, new MtLoColor())));
      }
      else {
        return generateRandomSequenceHelp(
            reference.removeColor(c), acc.append(new ConsLoColor(c, new MtLoColor())));
      }
    }
  }

  // calculates the number of exact matches between the two lists
  public int getExactMatches(ILoColor guess, ILoColor answer) {
    return guess.getExactMatches(answer);
  }

  //calculates the number of inexact matches between the two lists
  public int getInexactMatches(ILoColor guess, ILoColor answer) {
    return guess.getInexactMatches(answer);
  }

  // key handler for this game
  @Override
  public World onKeyEvent(String key) {
    if ("123456789".contains(key)) {
      return this.maybeUpdateNum(Integer.valueOf(key));
    }
    else if (key.equals("enter")) {
      return this.maybeUpdateEnter();
    }
    else if (key.equals("backspace")) {
      return this.maybeUpdateDelete();
    }
    else {
      return this;
    }
  }

  // Updates the unfinished guess if possible
  public World maybeUpdateNum(int key) {
    if (this.unfinishedGuess.length() < this.sequenceLength) {
      return new Mastermind(this.duplicatesAllowed, this.sequenceLength, this.numGuesses, 
          this.possibleColors, this.rand, this.sequence, 
          this.unfinishedGuess.append(
              new ConsLoColor(this.possibleColors.getColorAtIndex(key - 1), new MtLoColor())), 
          this.finishedGuesses);
    }
    else {
      return this;
    }
  }

  // Stores the guess made if it is the appropriate length
  public World maybeUpdateEnter() {
    if (this.unfinishedGuess.length() == this.sequenceLength) {
      return new Mastermind(this.duplicatesAllowed, this.sequenceLength, this.numGuesses, 
          this.possibleColors, this.rand, this.sequence, new MtLoColor(), 
          this.finishedGuesses.append(new ConsLoLoColor(this.unfinishedGuess, new MtLoLoColor())));
    }
    else {
      return this;
    }
  }

  // Removes one guess from the current guess if possible 
  public World maybeUpdateDelete() {
    int unfinishedGuessLength = this.unfinishedGuess.length();
    if (unfinishedGuessLength <= this.sequenceLength && unfinishedGuessLength > 0) {
      return new Mastermind(this.duplicatesAllowed, this.sequenceLength, this.numGuesses, 
          this.possibleColors, this.rand, this.sequence, this.unfinishedGuess.removeLastColor(), 
          this.finishedGuesses);
    } 
    else {
      return this;
    }
  }

  // draws the world
  @Override
  public WorldScene makeScene() {
    if (this.finishedGuesses.getExactMatchesLast(this.sequence) == this.sequenceLength) {
      this.endOfWorld("WIN!");
    }

    if (this.finishedGuesses.length() == this.numGuesses) {
      this.endOfWorld("LOSE!");
    }

    int widthSequence =  (this.circleRadius * 2) * (this.sequenceLength + 2);
    int widthPossibleColors = (this.possibleColors.length()) * (this.circleRadius * 2);
    int width = Math.max(widthSequence, widthPossibleColors);
    int height = (this.circleRadius * 2) * (this.numGuesses + 2);

    return new WorldScene(width, height)
        .placeImageXY(this.drawEverything(), width / 2, height / 2);
  }

  // draws the bottom row
  public WorldImage drawBottomRow() {
    return this.possibleColors.drawAll(this.circleRadius);
  }

  // draws the finished guesses
  public WorldImage drawFinishedGuesses() {
    return this.finishedGuesses.drawAllFinishedGuesses(this.circleRadius, this.sequence);
  }

  // draws the unfinished guesses
  public WorldImage drawUnfinishedGuesses() {
    return this.unfinishedGuess.drawAll(this.circleRadius);
  }

  // draws the top row
  public WorldImage drawTopRow() {
    return this.sequence.drawAll(this.circleRadius);
  }

  // draws the background of the game
  public WorldImage getBackground() {
    int widthSequence =  (this.circleRadius * 2) * (this.sequenceLength + 2);
    int widthPossibleColors = (this.possibleColors.length()) * (this.circleRadius * 2);
    int width = Math.max(widthSequence, widthPossibleColors);
    int height = (this.circleRadius * 2) * (this.numGuesses + 2);

    return new RectangleImage(width, height, "outline", Color.BLACK);
  }

  // puts all the images together to draw the whole game
  public WorldImage drawEverything() {
    WorldImage background = this.getBackground();
    WorldImage bottom = this.drawBottomRow();
    WorldImage top = 
        new RectangleImage(this.circleRadius * 2 * this.sequenceLength, 
            this.circleRadius * 2, "outline", Color.RED);
    WorldImage finishedGuesses = this.drawFinishedGuesses();
    WorldImage unfinishedGuesses = this.drawUnfinishedGuesses();
    WorldImage allGuesses = 
        new AboveAlignImage(AlignModeX.LEFT, unfinishedGuesses, finishedGuesses);

    WorldImage everything = new AboveAlignImage(AlignModeX.LEFT, allGuesses, bottom);

    return new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.BOTTOM, everything, 0, 0, 
        new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, top, 0, 0, background));
  }

  // draws the final scene of the game
  @Override
  public WorldScene lastScene(String msg) {
    int widthSequence =  (this.circleRadius * 2) * (this.sequenceLength + 2);
    int widthPossibleColors = (this.possibleColors.length()) * (this.circleRadius * 2);
    int width = Math.max(widthSequence, widthPossibleColors);
    int height = (this.circleRadius * 2) * (this.numGuesses + 2);

    WorldImage top = this.drawTopRow();
    WorldImage temp = this.drawEverything();
    WorldImage wrapper = 
        new RectangleImage(this.circleRadius * 4, this.circleRadius * 2, "solid", Color.RED);
    WorldImage text = 
        new OverlayImage(new TextImage(msg, 20, Color.BLACK), wrapper);
    WorldImage last = 
        new OverlayOffsetAlign(AlignModeX.LEFT, AlignModeY.TOP, top, 0, 0, temp);
    WorldImage putTogether = 
        new OverlayOffsetAlign(AlignModeX.RIGHT, AlignModeY.TOP, text, 0, 0, last);

    return new WorldScene(width, height).placeImageXY(putTogether, width / 2, height / 2);
  }
}

// represents a list of list of Colors
interface ILoLoColor {
  // calculate the length of this list
  int length();

  // create a new list by appending that list to this list
  ILoLoColor append(ILoLoColor that);

  // get the last element in this list and calculate its number of exact matches
  int getExactMatchesLast(ILoColor sequence);

  // accumulator method for getExactMatchesLast
  int getExactMatchesLastHelp(ILoColor acc, ILoColor sequence);

  // draws every Color in the list of finished guesses as a circle with a given radius, 
  // and the number of exact and inexact matches for each finished guess
  WorldImage drawAllFinishedGuesses(int radius, ILoColor sequence);

  // draws the number of exact and inexact matches for the first finished guess of this list
  WorldImage drawAllFinishedGuessesHelp(int radius, ILoColor sequence);
}

// represents an empty list of list of Colors
class MtLoLoColor implements ILoLoColor {
  MtLoLoColor() {}

  /* fields:
   * 
   * methods:
   *  this.length() ... int
   *  this.append(ILoLoColor that) ... ILoLoColor
   *  this.getExactMatchesLast(ILoColor sequence) ... int
   *  this.getExactMatchesLastHelp(ILoColor acc, ILoColor sequence) ... int
   *  this.drawAllFinishedGuesses(int radius, ILoColor sequence) ... WorldImage
   *  this.drawAllFinishedGuessesHelp(int radius, ILoColor sequence) ... WorldImage
   * 
   * methods of fields:
   */

  // the length of an empty list is 0
  public int length() {
    return 0;
  }

  // appends that list to this list
  public ILoLoColor append(ILoLoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ILoLoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append(ILoLoColor that) ... ILoLoColor
     *  that.getExactMatchesLast(ILoColor sequence) ... int
     *  that.getExactMatchesLastHelp(ILoColor acc, ILoColor sequence) ... int
     *  that.drawAllFinishedGuesses(int radius, ILoColor sequence) ... WorldImage
     *  that.drawAllFinishedGuessesHelp(int radius, ILoColor sequence) ... WorldImage
     */

    return that;
  }

  // an empty list has no last element, and therefore no exact matches
  public int getExactMatchesLast(ILoColor sequence) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  sequence ... ILoColor
     *  
     * methods on parameters:
     *  sequence.length() ... int
     *  sequence.append() ... ILoColor
     *  sequence.getColorAtIndex(int index) ... Color
     *  sequence.removeColor(Color given) ... ILoColor
     *  sequence.removeLastColor() ... ILoColor
     *  sequence.removeLastColorHelp(Color acc) ... ILoColor
     *  sequence.getExactMatches(ILoColor that) ... int
     *  sequence.getExactMatchesMt(MtLoColor that) ... int
     *  sequence.getExactMatchesCons(ConsLoColor that) ... int
     *  sequence.drawAll(int radius) ... WorldImage
     */

    return 0;
  }

  // calculates the number of exact matches of the last element of the list
  public int getExactMatchesLastHelp(ILoColor acc, ILoColor sequence) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  acc ... ILoColor
     *  sequence ... ILoColor
     *  
     * methods on parameters:
     *  acc.length() ... int
     *  sequence.length() ... int
     *  acc.append() ... ILoColor
     *  sequence.append() ... ILoColor
     *  acc.getColorAtIndex(int index) ... Color
     *  sequence.getColorAtIndex(int index) ... Color
     *  acc.removeColor(Color given) ... ILoColor
     *  sequence.removeColor(Color given) ... ILoColor
     *  acc.removeLastColor() ... ILoColor
     *  sequence.removeLastColor() ... ILoColor
     *  acc.removeLastColorHelp(Color acc) ... ILoColor
     *  sequence.removeLastColorHelp(Color acc) ... ILoColor
     *  acc.getExactMatches(ILoColor that) ... int
     *  sequence.getExactMatches(ILoColor that) ... int
     *  acc.getExactMatchesMt(MtLoColor that) ... int
     *  sequence.getExactMatchesMt(MtLoColor that) ... int
     *  acc.getExactMatchesCons(ConsLoColor that) ... int
     *  sequence.getExactMatchesCons(ConsLoColor that) ... int
     *  acc.drawAll(int radius) ... WorldImage
     *  sequence.drawAll(int radius) ... WorldImage
     */

    return acc.getExactMatches(sequence);
  }

  // an empty list has no Colors, no exact matches, and no inexact matches
  public WorldImage drawAllFinishedGuesses(int radius, ILoColor sequence) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  radius ... int
     *  sequence ... ILoColor
     *  
     * methods on parameters:
     *  methods on parameters:
     *  sequence.length() ... int
     *  sequence.append() ... ILoColor
     *  sequence.getColorAtIndex(int index) ... Color
     *  sequence.removeColor(Color given) ... ILoColor
     *  sequence.removeLastColor() ... ILoColor
     *  sequence.removeLastColorHelp(Color acc) ... ILoColor
     *  sequence.getExactMatches(ILoColor that) ... int
     *  sequence.getExactMatchesMt(MtLoColor that) ... int
     *  sequence.getExactMatchesCons(ConsLoColor that) ... int
     *  sequence.drawAll(int radius) ... WorldImage
     */

    return new EmptyImage();
  }

  // an empty list has no exact matches and no inexact matches
  public WorldImage drawAllFinishedGuessesHelp(int radius, ILoColor sequence) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  radius ... int
     *  sequence ... ILoColor
     *  
     * methods on parameters:
     *  methods on parameters:
     *  sequence.length() ... int
     *  sequence.append() ... ILoColor
     *  sequence.getColorAtIndex(int index) ... Color
     *  sequence.removeColor(Color given) ... ILoColor
     *  sequence.removeLastColor() ... ILoColor
     *  sequence.removeLastColorHelp(Color acc) ... ILoColor
     *  sequence.getExactMatches(ILoColor that) ... int
     *  sequence.getExactMatchesMt(MtLoColor that) ... int
     *  sequence.getExactMatchesCons(ConsLoColor that) ... int
     *  sequence.drawAll(int radius) ... WorldImage
     */

    return new EmptyImage();
  }
}

// represents a cons list of list of Colors
class ConsLoLoColor implements ILoLoColor {
  ILoColor first;
  ILoLoColor rest;

  ConsLoLoColor(ILoColor first, ILoLoColor rest) {
    this.first = first;
    this.rest = rest;
  }

  /* fields:
   *  this.first ... ILoColor
   *  this.rest ... ILoLoColor
   *  
   * methods:
   *  this.length() ... int
   *  this.append(ILoLoColor that) ... ILoLoColor
   *  this.getExactMatchesLast(ILoColor sequence) ... int
   *  this.getExactMatchesLastHelp(ILoColor acc, ILoColor sequence) ... int
   *  this.drawAllFinishedGuesses(int radius, ILoColor sequence) ... WorldImage
   *  this.drawAllFinishedGuessesHelp(int radius, ILoColor sequence) ... WorldImage
   * 
   * methods of fields:
   *  this.rest.length() ... int
   *  this.rest.append(ILoLoColor that) ... ILoLoColor
   *  this.rest.getExactMatchesLast(ILoColor sequence) ... int
   *  this.rest.getExactMatchesLastHelp(ILoColor acc, ILoColor sequence) ... int
   *  this.rest.drawAllFinishedGuesses(int radius, ILoColor sequence) ... WorldImage
   *  this.rest.drawAllFinishedGuessesHelp(int radius, ILoColor sequence) ... WorldImage
   */

  // calculates the length of this list
  public int length() {
    return 1 + this.rest.length();
  }

  // appends that list to this list
  public ILoLoColor append(ILoLoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ILoLoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append(ILoLoColor that) ... ILoLoColor
     *  that.getExactMatchesLast(ILoColor sequence) ... int
     *  that.getExactMatchesLastHelp(ILoColor acc, ILoColor sequence) ... int
     *  that.drawAllFinishedGuesses(int radius, ILoColor sequence) ... WorldImage
     *  that.drawAllFinishedGuessesHelp(int radius, ILoColor sequence) ... WorldImage
     */

    return new ConsLoLoColor(this.first, this.rest.append(that));
  }

  // get the last element in this list, and calculate its number of exact matches
  public int getExactMatchesLast(ILoColor sequence) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  sequence ... ILoColor
     *  
     * methods on parameters:
     *  sequence.length() ... int
     *  sequence.append() ... ILoColor
     *  sequence.getColorAtIndex(int index) ... Color
     *  sequence.removeColor(Color given) ... ILoColor
     *  sequence.removeLastColor() ... ILoColor
     *  sequence.removeLastColorHelp(Color acc) ... ILoColor
     *  sequence.getExactMatches(ILoColor that) ... int
     *  sequence.getExactMatchesMt(MtLoColor that) ... int
     *  sequence.getExactMatchesCons(ConsLoColor that) ... int
     *  sequence.getInexactMatches(ILoColor that) ... int
     *  sequence.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  sequence.removeExactMatches(ILoColor guess) ... ILoColor
     *  sequence.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  sequence.countColors(Color c) ... int
     *  sequence.compareAtIndex(Color c, int index) ... boolean
     *  sequence.drawAll(int radius) ... WorldImage
     */

    return this.rest.getExactMatchesLastHelp(this.first, sequence);
  }

  // accumulator method for getExactMatchesLast
  public int getExactMatchesLastHelp(ILoColor acc, ILoColor sequence) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  acc ... ILoColor
     *  sequence ... ILoColor
     *  
     * methods on parameters:
     *  acc ... everything in the ILoColor template
     *  sequence ... everything in the ILoColor template
     */

    return this.getExactMatchesLast(sequence);
  }

  // draws every Color in this list as a circle with a given radius, 
  // and the number of exact and inexact matches for each finished guess
  public WorldImage drawAllFinishedGuesses(int radius, ILoColor sequence) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  radius ... int
     *  sequence ... ILoColor
     *  
     * methods on parameters:
     *  sequence ... everything in the ILoColor template
     */

    WorldImage colors = this.first.drawAll(radius);

    return new AboveImage(this.rest.drawAllFinishedGuesses(radius, sequence),
        new BesideImage(colors, this.drawAllFinishedGuessesHelp(radius, sequence)));
  }

  // draws the number of exact and inexact matches of the first finished guess in this list
  public WorldImage drawAllFinishedGuessesHelp(int radius, ILoColor sequence) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  radius ... int
     *  sequence ... ILoColor
     *  
     * methods on parameters:
     *  sequence ... everything in the ILoColor template
     */

    WorldImage wrapper = 
        new RectangleImage(radius * 2, radius * 2, OutlineMode.SOLID, Color.WHITE);
    WorldImage exact = 
        new TextImage(String.valueOf(this.first.getExactMatches(sequence)), 30, Color.BLACK);
    WorldImage inexact = 
        new TextImage(String.valueOf(this.first.getInexactMatches(sequence)), 30, Color.BLACK);
    WorldImage exactWrapped = new OverlayImage(exact, wrapper);
    WorldImage inexactWrapped = new OverlayImage(inexact, wrapper);

    return new BesideImage(exactWrapped, inexactWrapped);
  }
}

// represents a list of Colors
interface ILoColor {
  // return the length of this list
  int length();

  // create a new list by appending that list to this list
  ILoColor append(ILoColor that);

  // returns the Color at the given index of this list
  Color getColorAtIndex(int index);

  // removes a given Color from this list
  ILoColor removeColor(Color given);

  // removes the last Color from this list
  ILoColor removeLastColor();

  // accumulator method for removeLastColor method
  ILoColor removeLastColorHelp(Color acc);

  // calculates the number of exact matches between this list and that list
  int getExactMatches(ILoColor that);

  // calculates the number of exact matches between this list and that empty list
  int getExactMatchesMt(MtLoColor that);

  // calculates the number of exact matches between this list and that cons list
  int getExactMatchesCons(ConsLoColor that);

  // calculates the number of inexact matches between this list and that list
  int getInexactMatches(ILoColor that);

  // Helper for getInexactMatches
  int getInexactMatchesHelp(ILoColor that, boolean remove);

  // Removes the exact matches from a list and guess
  ILoColor removeExactMatches(ILoColor guess);

  // Helper for removeExactMatches()
  ILoColor removeExactMatchesHelp(ILoColor guess, int index);

  // Counts the amount of colors in an ILoCircle
  int countColors(Color c);

  // Does the given color match the color at the given index of this ILoColor?
  boolean compareAtIndex(Color c, int index);

  // draws all the Colors in this list as circles with a given radius
  WorldImage drawAll(int radius);
}

class MtLoColor implements ILoColor {
  MtLoColor() {}

  /* fields:
   * 
   * methods:
   *  this.length() ... int
   *  this.append() ... ILoColor
   *  this.getColorAtIndex(int index) ... Color
   *  this.removeColor(Color given) ... ILoColor
   *  this.removeLastColor() ... ILoColor
   *  this.removeLastColorHelp(Color acc) ... ILoColor
   *  this.getExactMatches(ILoColor that) ... int
   *  this.getExactMatchesMt(MtLoColor that) ... int
   *  this.getExactMatchesCons(ConsLoColor that) ... int
   *  this.getInexactMatches(ILoColor that) ... int
   *  this.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
   *  this.removeExactMatches(ILoColor guess) ... ILoColor
   *  this.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
   *  this.countColors(Color c) ... int
   *  this.compareAtIndex(Color c, int index) ... boolean
   *  this.drawAll(int radius) ... WorldImage
   * 
   * methods of fields:
   */

  // the length of an empty list is 0
  public int length() {
    return 0;
  }

  // appends that list to this list
  public ILoColor append(ILoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ILoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    return that;
  }

  // a RuntimeException is thrown because the index is 
  // equal to or longer than the length of the list
  public Color getColorAtIndex(int index) {
    throw new RuntimeException("Given index is equal to or longer than the length of the list");
  }

  // there are no Colors to remove in an empty list
  public ILoColor removeColor(Color given) {
    return this;
  }

  // there is no last Color in an empty list
  public ILoColor removeLastColor() {
    return this;
  }

  // when you get to the empty case, the Color acc represents the last
  // element of the list. we want to remove it, so just ignore it
  public ILoColor removeLastColorHelp(Color acc) {
    return this;
  }

  // an empty list has no Colors, and therefore no exact matches
  public int getExactMatches(ILoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ILoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    return 0;
  }

  // an empty list has no Colors, and therefore no exact matches
  public int getExactMatchesMt(MtLoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... MtLoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    return 0;
  }

  // an empty list has no Colors, and therefore no exact matches
  public int getExactMatchesCons(ConsLoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ConsLoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    return 0;
  }

  public int getInexactMatches(ILoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ILoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    return 0;
  }

  public int getInexactMatchesHelp(ILoColor that, boolean remove) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  remove ... boolean
     *  that ... ILoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    return 0;
  }

  public ILoColor removeExactMatches(ILoColor guess) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  guess ... ILoColor
     *  
     * methods on parameters:
     *  guess.length() ... int
     *  guess.append() ... ILoColor
     *  guess.getColorAtIndex(int index) ... Color
     *  guess.removeColor(Color given) ... ILoColor
     *  guess.removeLastColor() ... ILoColor
     *  guess.removeLastColorHelp(Color acc) ... ILoColor
     *  guess.getExactMatches(ILoColor that) ... int
     *  guess.getExactMatchesMt(MtLoColor that) ... int
     *  guess.getExactMatchesCons(ConsLoColor that) ... int
     *  guess.getInexactMatches(ILoColor that) ... int
     *  guess.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  guess.removeExactMatches(ILoColor guess) ... ILoColor
     *  guess.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  guess.countColors(Color c) ... int
     *  guess.compareAtIndex(Color c, int index) ... boolean
     *  guess.drawAll(int radius) ... WorldImage
     */

    return this;
  }

  public ILoColor removeExactMatchesHelp(ILoColor guess, int index) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  guess ... ILoColor
     *  index ... int
     *  
     * methods on parameters:
     *  guess.length() ... int
     *  guess.append() ... ILoColor
     *  guess.getColorAtIndex(int index) ... Color
     *  guess.removeColor(Color given) ... ILoColor
     *  guess.removeLastColor() ... ILoColor
     *  guess.removeLastColorHelp(Color acc) ... ILoColor
     *  guess.getExactMatches(ILoColor that) ... int
     *  guess.getExactMatchesMt(MtLoColor that) ... int
     *  guess.getExactMatchesCons(ConsLoColor that) ... int
     *  guess.getInexactMatches(ILoColor that) ... int
     *  guess.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  guess.removeExactMatches(ILoColor guess) ... ILoColor
     *  guess.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  guess.countColors(Color c) ... int
     *  guess.compareAtIndex(Color c, int index) ... boolean
     *  guess.drawAll(int radius) ... WorldImage
     */

    return this;
  }

  public int countColors(Color c) {
    return 0;
  }

  // Does the given Color match the color at the given index?
  public boolean compareAtIndex(Color c, int index) {
    return false;
  }

  // there are no Colors to draw in an empty list
  public WorldImage drawAll(int radius) {
    return new EmptyImage();
  }
}

class ConsLoColor implements ILoColor {
  Color first;
  ILoColor rest;

  ConsLoColor(Color first, ILoColor rest) {
    this.first = first;
    this.rest = rest;
  }

  /* fields:
   *  this.first ... Color
   *  this.rest ... ILoColor
   * 
   * methods:
   *  this.length() ... int
   *  this.append() ... ILoColor
   *  this.getColorAtIndex(int index) ... Color
   *  this.removeColor(Color given) ... ILoColor
   *  this.removeLastColor() ... ILoColor
   *  this.removeLastColorHelp(Color acc) ... ILoColor
   *  this.getExactMatches(ILoColor that) ... int
   *  this.getExactMatchesMt(MtLoColor that) ... int
   *  this.getExactMatchesCons(ConsLoColor that) ... int
   *  this.drawAll(int radius) ... WorldImage
   * 
   * methods of fields:
   *  this.rest.length() ... int
   *  this.rest.append() ... ILoColor
   *  this.rest.getColorAtIndex(int index) ... Color
   *  this.rest.removeColor(Color given) ... ILoColor
   *  this.rest.removeLastColor() ... ILoColor
   *  this.rest.removeLastColorHelp(Color acc) ... ILoColor
   *  this.rest.getExactMatches(ILoColor that) ... int
   *  this.rest.getExactMatchesMt(MtLoColor that) ... int
   *  this.rest.getExactMatchesCons(ConsLoColor that) ... int
   *  this.rest.drawAll(int radius) ... WorldImage
   */

  // returns the length of this list
  public int length() {
    return 1 + this.rest.length();
  }

  //appends that list to this list
  public ILoColor append(ILoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ILoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    return new ConsLoColor(this.first, this.rest.append(that));
  }

  // a RuntimeException is thrown if the index is equal to or longer than the length of this list,
  // otherwise this method returns the color at the given index
  public Color getColorAtIndex(int index) {
    if (index >= this.length()) {
      throw new RuntimeException("Given index is equal to or longer than the length of the list");
    }
    else {
      if (index == 0) {
        return this.first;
      }
      else {
        return this.rest.getColorAtIndex(index -= 1);
      }
    }
  }

  //removes the given Color from this list
  public ILoColor removeColor(Color given) {
    if (given.equals(this.first)) {
      return this.rest;
    }
    else {
      return new ConsLoColor(this.first, this.rest.removeColor(given));
    }
  }

  //removes the last Color from this list
  public ILoColor removeLastColor() {
    return this.rest.removeLastColorHelp(this.first);
  }

  // accumulator method for removeLastColor
  public ILoColor removeLastColorHelp(Color acc) {
    return new ConsLoColor(acc, this.removeLastColor());
  }


  // calculates the number of exact matches between this list and that list
  public int getExactMatches(ILoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ILoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    return that.getExactMatchesCons(this);
  }

  // an empty list contains no Colors, and therefore no exact matches
  public int getExactMatchesMt(MtLoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... MtLoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    return 0;
  }

  // calculates the number of exact matches between this list and that cons list
  public int getExactMatchesCons(ConsLoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ConsLoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    if (this.first.equals(that.first)) {
      return 1 + this.rest.getExactMatches(that.rest);
    }
    else {
      return this.rest.getExactMatches(that.rest);
    }
  }

  public int getInexactMatches(ILoColor that) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ConsLoColor
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    return this.getInexactMatchesHelp(that, true);
  } 

  public int getInexactMatchesHelp(ILoColor that, boolean remove) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  that ... ConsLoColor
     *  remove ... boolean
     *  
     * methods on parameters:
     *  that.length() ... int
     *  that.append() ... ILoColor
     *  that.getColorAtIndex(int index) ... Color
     *  that.removeColor(Color given) ... ILoColor
     *  that.removeLastColor() ... ILoColor
     *  that.removeLastColorHelp(Color acc) ... ILoColor
     *  that.getExactMatches(ILoColor that) ... int
     *  that.getExactMatchesMt(MtLoColor that) ... int
     *  that.getExactMatchesCons(ConsLoColor that) ... int
     *  that.getInexactMatches(ILoColor that) ... int
     *  that.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  that.removeExactMatches(ILoColor guess) ... ILoColor
     *  that.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  that.countColors(Color c) ... int
     *  that.compareAtIndex(Color c, int index) ... boolean
     *  that.drawAll(int radius) ... WorldImage
     */

    if (remove) {
      ILoColor guess = that.removeExactMatches(this);
      ILoColor solution = this.removeExactMatches(that);

      int num = Math.min(guess.countColors(this.first), solution.countColors(this.first));
      return num + solution.removeColor(this.first).getInexactMatchesHelp(guess, false);
    } 
    else {
      int num = Math.min(that.countColors(this.first), this.countColors(this.first));
      return num + this.removeColor(this.first).getInexactMatchesHelp(that, false);
    }
  }

  // 
  public ILoColor removeExactMatches(ILoColor guess) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  guess ... ILoColor
     *  
     * methods on parameters:
     *  guess.length() ... int
     *  guess.append() ... ILoColor
     *  guess.getColorAtIndex(int index) ... Color
     *  guess.removeColor(Color given) ... ILoColor
     *  guess.removeLastColor() ... ILoColor
     *  guess.removeLastColorHelp(Color acc) ... ILoColor
     *  guess.getExactMatches(ILoColor that) ... int
     *  guess.getExactMatchesMt(MtLoColor that) ... int
     *  guess.getExactMatchesCons(ConsLoColor that) ... int
     *  guess.getInexactMatches(ILoColor that) ... int
     *  guess.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  guess.removeExactMatches(ILoColor guess) ... ILoColor
     *  guess.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  guess.countColors(Color c) ... int
     *  guess.compareAtIndex(Color c, int index) ... boolean
     *  guess.drawAll(int radius) ... WorldImage
     */

    return this.removeExactMatchesHelp(guess, 0); 
  } 

  public ILoColor removeExactMatchesHelp(ILoColor guess, int index) {

    /* everything in this class template plus:
     * 
     * parameters:
     *  guess ... ILoColor
     *  index ... int
     *  
     * methods on parameters:
     *  guess.length() ... int
     *  guess.append() ... ILoColor
     *  guess.getColorAtIndex(int index) ... Color
     *  guess.removeColor(Color given) ... ILoColor
     *  guess.removeLastColor() ... ILoColor
     *  guess.removeLastColorHelp(Color acc) ... ILoColor
     *  guess.getExactMatches(ILoColor that) ... int
     *  guess.getExactMatchesMt(MtLoColor that) ... int
     *  guess.getExactMatchesCons(ConsLoColor that) ... int
     *  guess.getInexactMatches(ILoColor that) ... int
     *  guess.getInexactMatchesHelp(ILoColor that, boolean remove) ... int
     *  guess.removeExactMatches(ILoColor guess) ... ILoColor
     *  guess.removeExactMatchesHelp(ILoColor guess, int index) ... ILoColor
     *  guess.countColors(Color c) ... int
     *  guess.compareAtIndex(Color c, int index) ... boolean
     *  guess.drawAll(int radius) ... WorldImage
     */

    if (guess.compareAtIndex(this.first, index)) {
      return this.rest.removeExactMatchesHelp(guess, index += 1);
    } 
    else {
      return new ConsLoColor(this.first, this.rest.removeExactMatchesHelp(guess, index += 1)); 
    }
  }

  //Does the given Color match the color at the given index?
  public boolean compareAtIndex(Color c, int index) {
    if (index == 0) {
      return this.first.equals(c); 
    } 
    else {
      return this.rest.compareAtIndex(c, index -= 1);
    }
  }

  public int countColors(Color c) {
    if (this.first.equals(c)) {
      return 1 + this.rest.countColors(c);
    } 
    else {
      return this.rest.countColors(c); 
    }
  }

  // draws all the Colors in this list as circles with a given radius
  public WorldImage drawAll(int radius) {
    return new BesideImage(
        new CircleImage(radius, "solid", this.first), 
        this.rest.drawAll(radius));
  }
}

class ExamplesMastermind {
  ILoColor mt = new MtLoColor();
  ILoColor r = new ConsLoColor(Color.RED, this.mt);
  ILoColor ro = new ConsLoColor(Color.ORANGE, this.r);
  ILoColor roy = new ConsLoColor(Color.YELLOW, this.ro);
  ILoColor royg = new ConsLoColor(Color.GREEN, this.roy);

  ILoLoColor r2 = new ConsLoLoColor(this.r, new MtLoLoColor());

  ILoColor bogr = new ConsLoColor(Color.BLUE, new ConsLoColor(Color.ORANGE, 
      new ConsLoColor(Color.GREEN, new ConsLoColor(Color.RED, this.mt))));

  ILoColor royb = new ConsLoColor(Color.RED, new ConsLoColor(Color.ORANGE, 
      new ConsLoColor(Color.YELLOW, new ConsLoColor(Color.BLUE, this.mt))));

  ILoColor obruy = new ConsLoColor(Color.ORANGE, new ConsLoColor(Color.BLUE, 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.BLACK, new ConsLoColor(Color.YELLOW, this.mt)))));

  Mastermind exampleBoard = new Mastermind(false, 3, 15, this.royg, new Random(0));
  Mastermind exampleBoard2 = new Mastermind(false, 4, 13, this.bogr, new Random(1));
  Mastermind exampleBoard5 = new Mastermind(false, 4, 13, this.bogr, new Random(2));
  Mastermind exampleBoard3 = 
      new Mastermind(false, 1, 1, this.r, new Random(0), this.r, this.r, this.r2);

  Mastermind exampleBoard6 = new Mastermind(true, 6, 16, this.royb, new Random(2)); 
  Mastermind exampleBoard4 = new Mastermind(false, 5, 13, this.obruy, new Random(3)); 

  ILoColor exampleSolution = 
      new ConsLoColor(Color.ORANGE, 
          new ConsLoColor(Color.RED, 
              new ConsLoColor(Color.GREEN, this.mt)));

  ILoColor rog = 
      new ConsLoColor(Color.RED,
          new ConsLoColor(Color.ORANGE, 
              new ConsLoColor(Color.GREEN, this.mt)));
  ILoColor ryg =
      new ConsLoColor(Color.RED,
          new ConsLoColor(Color.YELLOW,
              new ConsLoColor(Color.GREEN, this.mt)));
  ILoColor rrrr = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.RED,
              new ConsLoColor(Color.RED,
                  new ConsLoColor(Color.RED, this.mt))));
  ILoColor rorr = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.ORANGE,
              new ConsLoColor(Color.RED,
                  new ConsLoColor(Color.RED, this.mt))));
  ILoColor royr = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.ORANGE,
              new ConsLoColor(Color.YELLOW,
                  new ConsLoColor(Color.RED, this.mt))));
  ILoColor rrrg = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.RED,
              new ConsLoColor(Color.RED,
                  new ConsLoColor(Color.GREEN, this.mt))));

  ILoColor seq1 = new ConsLoColor(Color.ORANGE, new ConsLoColor(Color.RED, 
      new ConsLoColor(Color.RED, new ConsLoColor(Color.BLUE, this.mt))));
  ILoColor seq2 = 
      new ConsLoColor(Color.GREEN, new ConsLoColor(Color.ORANGE, new ConsLoColor(Color.RED, 
      new ConsLoColor(Color.RED, new ConsLoColor(Color.BLUE, this.mt)))));

  ILoColor uGreen = new ConsLoColor(Color.GREEN, this.mt);
  ILoColor uRG = new ConsLoColor(Color.RED, this.uGreen);
  ILoColor uRRG = new ConsLoColor(Color.RED, this.uRG);

  boolean testBigBang(Tester t) {
    int worldWidth = 800;
    int worldHeight =  800;
    return exampleBoard.bigBang(worldWidth, worldHeight);
  }

  boolean testException(Tester t) {
    return t.checkConstructorException(
        new IllegalArgumentException(
            "The length of the sequence cannot be greater than the length of the"
                + "list of colors when duplicates are not allowed"),
        "Mastermind", false, 10, 1, this.uGreen)
        && t.checkConstructorException(
            new IllegalArgumentException("The sequence length must be greater than 0"),
            "Mastermind", false, 0, 1, this.uGreen)
        && t.checkConstructorException(
            new IllegalArgumentException("The sequence length must be greater than 0"),
            "Mastermind", false, -1, 1, this.uGreen)
        && t.checkConstructorException(
            new IllegalArgumentException("The number of guesses allowed must be greater than 0"),
            "Mastermind", false, 1, 0, this.uGreen)
        && t.checkConstructorException(
            new IllegalArgumentException("The number of guesses allowed must be greater than 0"),
            "Mastermind", false, 1, -1, this.uGreen)
        && t.checkConstructorException(
            new IllegalArgumentException("The number of possible colors must be greater than 0"),
            "Mastermind", false, 1, 1, this.mt);
  }

  boolean testGenerateRandomColor(Tester t) {
    return t.checkExpect(this.exampleBoard5.generateRandomColor(this.royg), Color.YELLOW)
        && t.checkExpect(this.exampleBoard5.generateRandomColor(this.bogr), Color.RED);
  }

  boolean testGenerateRandomSequence(Tester t) {
    return t.checkExpect(this.exampleBoard.generateRandomSequence(), new ConsLoColor(Color.ORANGE, 
        new ConsLoColor(Color.RED, 
            new ConsLoColor(Color.GREEN, this.mt))))
        && t.checkExpect(this.exampleBoard2.generateRandomSequence(), new ConsLoColor(Color.BLUE, 
            new ConsLoColor(Color.GREEN, 
                new ConsLoColor(Color.ORANGE, new ConsLoColor(Color.RED, this.mt)))))
        && t.checkExpect(this.exampleBoard6.generateRandomSequence(), new ConsLoColor(Color.BLUE, 
            new ConsLoColor(Color.RED, new ConsLoColor(Color.BLUE, new ConsLoColor(Color.BLUE, 
                new ConsLoColor(Color.BLUE, new ConsLoColor(Color.ORANGE, this.mt)))))))
        && t.checkExpect(this.exampleBoard4.generateRandomSequence(), new ConsLoColor(Color.RED, 
            new ConsLoColor(Color.YELLOW, new ConsLoColor(Color.BLUE, 
                new ConsLoColor(Color.ORANGE, new ConsLoColor(Color.BLACK, this.mt))))));
  }

  boolean testGetExactMatches(Tester t) {
    return t.checkExpect(this.exampleBoard.getExactMatches(this.bogr, this.royg), 1)
        && t.checkExpect(this.exampleBoard.getExactMatches(this.ryg, this.rog), 2)
        && t.checkExpect(this.exampleBoard2.getExactMatches(this.royg,  this.bogr), 1)
        && t.checkExpect(this.exampleBoard2.getExactMatches(this.rorr,  this.royg), 1)
        && t.checkExpect(this.exampleBoard2.getExactMatches(this.rrrg, this.rorr), 2);
  }

  boolean testGetInexactMatches(Tester t) {
    return t.checkExpect(this.exampleBoard.getInexactMatches(this.bogr, this.royg), 2)
        && t.checkExpect(this.exampleBoard.getInexactMatches(this.ryg, this.rog), 0)
        && t.checkExpect(this.exampleBoard2.getInexactMatches(this.royg,  this.bogr), 2)
        && t.checkExpect(this.exampleBoard2.getInexactMatches(this.rorr,  this.royg), 1)
        && t.checkExpect(this.exampleBoard2.getInexactMatches(this.rrrg, this.rorr), 1);
  }

  boolean testOnKeyEvent(Tester t) {
    return t.checkExpect(this.exampleBoard.onKeyEvent("d"), this.exampleBoard)
        && t.checkExpect(this.exampleBoard2.onKeyEvent("s"), this.exampleBoard2)
        && t.checkExpect(this.exampleBoard2.onKeyEvent("2"), this.exampleBoard2.maybeUpdateNum(2))
        && t.checkExpect(this.exampleBoard.onKeyEvent("1"), this.exampleBoard.maybeUpdateNum(1))
        && t.checkExpect(this.exampleBoard.onKeyEvent("enter"), 
            this.exampleBoard.maybeUpdateEnter())
        && t.checkExpect(this.exampleBoard2.onKeyEvent("enter"), 
            this.exampleBoard2.maybeUpdateEnter())
        && t.checkExpect(this.exampleBoard.onKeyEvent("delete"), 
            this.exampleBoard.maybeUpdateDelete())
        && t.checkExpect(this.exampleBoard2.onKeyEvent("delete"), 
            this.exampleBoard2.maybeUpdateDelete());
  }

  boolean testMaybeUpdateNum(Tester t) {
    return t.checkExpect(this.exampleBoard.maybeUpdateNum(2), 
        new Mastermind(this.exampleBoard.duplicatesAllowed, this.exampleBoard.sequenceLength, 
            this.exampleBoard.numGuesses, this.exampleBoard.possibleColors, this.exampleBoard.rand,
            this.exampleBoard.sequence, new ConsLoColor(Color.YELLOW, this.mt), 
            this.exampleBoard.finishedGuesses))
        && t.checkExpect(this.exampleBoard.maybeUpdateNum(3), 
            new Mastermind(this.exampleBoard.duplicatesAllowed, this.exampleBoard.sequenceLength, 
                this.exampleBoard.numGuesses, 
                this.exampleBoard.possibleColors, this.exampleBoard.rand,
                this.exampleBoard.sequence, new ConsLoColor(Color.ORANGE, this.mt), 
                this.exampleBoard.finishedGuesses))
        && t.checkExpect(new Mastermind(this.exampleBoard.duplicatesAllowed, 
            this.exampleBoard.sequenceLength, 
            this.exampleBoard.numGuesses, this.exampleBoard.possibleColors, this.exampleBoard.rand,
            this.exampleBoard.sequence, new ConsLoColor(Color.ORANGE, this.mt), 
            this.exampleBoard.finishedGuesses).maybeUpdateNum(2), 
            new Mastermind(this.exampleBoard.duplicatesAllowed, this.exampleBoard.sequenceLength, 
                this.exampleBoard.numGuesses, 
                this.exampleBoard.possibleColors, this.exampleBoard.rand,
                this.exampleBoard.sequence, 
                new ConsLoColor(Color.ORANGE, new ConsLoColor(Color.YELLOW, this.mt)), 
                this.exampleBoard.finishedGuesses))
        && t.checkExpect(this.exampleBoard2.maybeUpdateNum(2), 
            new Mastermind(this.exampleBoard2.duplicatesAllowed, 
                this.exampleBoard2.sequenceLength, 
                this.exampleBoard2.numGuesses, this.exampleBoard2.possibleColors, 
                this.exampleBoard2.rand,
                this.exampleBoard2.sequence, new ConsLoColor(Color.ORANGE, this.mt), 
                this.exampleBoard2.finishedGuesses));
  }

  boolean testMaybeUpdateEnter(Tester t) {
    return t.checkExpect(this.exampleBoard.maybeUpdateEnter(), this.exampleBoard)
        && t.checkExpect(this.exampleBoard2.maybeUpdateEnter(), this.exampleBoard2)
        && t.checkExpect(new Mastermind(this.exampleBoard2.duplicatesAllowed, 
            this.exampleBoard2.sequenceLength, 
            this.exampleBoard2.numGuesses, this.exampleBoard2.possibleColors, 
            this.exampleBoard2.rand,
            this.exampleBoard2.sequence, seq1, 
            this.exampleBoard2.finishedGuesses).maybeUpdateEnter(), 
            new Mastermind(this.exampleBoard2.duplicatesAllowed, 
                this.exampleBoard2.sequenceLength, 
                this.exampleBoard2.numGuesses, this.exampleBoard2.possibleColors, 
                this.exampleBoard2.rand,
                this.exampleBoard2.sequence, this.mt, 
                this.exampleBoard2.finishedGuesses.append(
                    new ConsLoLoColor(seq1, new MtLoLoColor()))));
  }

  boolean testMaybeUpdateDelete(Tester t) {
    return t.checkExpect(new Mastermind(this.exampleBoard.duplicatesAllowed, 
        this.exampleBoard.sequenceLength, 
        this.exampleBoard.numGuesses, this.exampleBoard.possibleColors, this.exampleBoard.rand,
        this.exampleBoard.sequence, this.uGreen, 
        this.exampleBoard.finishedGuesses).maybeUpdateDelete(), 
        new Mastermind(this.exampleBoard.duplicatesAllowed, this.exampleBoard.sequenceLength, 
            this.exampleBoard.numGuesses, this.exampleBoard.possibleColors, this.exampleBoard.rand,
            this.exampleBoard.sequence, this.mt, 
            this.exampleBoard.finishedGuesses))
        && t.checkExpect(new Mastermind(this.exampleBoard.duplicatesAllowed, 
            this.exampleBoard.sequenceLength, 
            this.exampleBoard.numGuesses, this.exampleBoard.possibleColors, this.exampleBoard.rand,
            this.exampleBoard.sequence, this.uRG, 
            this.exampleBoard.finishedGuesses).maybeUpdateDelete(), 
            new Mastermind(this.exampleBoard.duplicatesAllowed, this.exampleBoard.sequenceLength, 
                this.exampleBoard.numGuesses, this.exampleBoard.possibleColors, 
                this.exampleBoard.rand,
                this.exampleBoard.sequence, new ConsLoColor(Color.RED, this.mt), 
                this.exampleBoard.finishedGuesses))
        && t.checkExpect(new Mastermind(this.exampleBoard.duplicatesAllowed, 
            this.exampleBoard.sequenceLength, 
            this.exampleBoard.numGuesses, this.exampleBoard.possibleColors, 
            this.exampleBoard.rand,
            this.exampleBoard.sequence, this.uRRG, 
            this.exampleBoard.finishedGuesses).maybeUpdateDelete(), 
            new Mastermind(this.exampleBoard.duplicatesAllowed, this.exampleBoard.sequenceLength, 
                this.exampleBoard.numGuesses, this.exampleBoard.possibleColors,
                this.exampleBoard.rand,
                this.exampleBoard.sequence, 
                new ConsLoColor(Color.RED, new ConsLoColor(Color.RED, this.mt)), 
                this.exampleBoard.finishedGuesses))
        && t.checkExpect(this.exampleBoard.maybeUpdateDelete(), this.exampleBoard)
        && t.checkExpect(this.exampleBoard2.maybeUpdateDelete(), this.exampleBoard2);
  }

  // testing the makeScene method
  public boolean testMakeScene(Tester t) {
    return t.checkExpect(this.exampleBoard3.makeScene(),
        new WorldScene(120, 120).placeImageXY(this.exampleBoard3.drawEverything(), 60, 60));
  }

  // testing the drawBottomRow method
  public boolean testDrawBottomRow(Tester t) {
    return t.checkExpect(this.exampleBoard3.drawBottomRow(), 
        new BesideImage(new CircleImage(20, "solid", Color.RED), new EmptyImage()));
  }

  // testing the drawFinishedGuesses method
  public boolean testDrawFinishedGuesses(Tester t) {
    return t.checkExpect(this.exampleBoard3.drawFinishedGuesses(), 
        new AboveImage(
            new EmptyImage(),
            new BesideImage(
                new BesideImage(
                    new CircleImage(20, "solid", Color.RED),
                    new EmptyImage()),
                new BesideImage(
                    new OverlayImage(
                        new TextImage("1", 30, Color.BLACK),
                        new RectangleImage(40, 40, "solid", Color.WHITE)),
                    new OverlayImage(
                        new TextImage("0", 30, Color.BLACK),
                        new RectangleImage(40, 40, "solid", Color.WHITE))))));
  }

  // testing the drawUnfinishedGuesses method
  public boolean testDrawUnfinishedGuesses(Tester t) {
    return t.checkExpect(this.exampleBoard3.drawUnfinishedGuesses(), 
        new BesideImage(new CircleImage(20, "solid", Color.RED), new EmptyImage()));
  }

  // testing the drawTopRow method
  public boolean testDrawTopRow(Tester t) {
    return t.checkExpect(this.exampleBoard3.drawTopRow(), 
        new BesideImage(new CircleImage(20, "solid", Color.RED), new EmptyImage()));
  }

  // testing the getBackground method
  public boolean testGetBackground(Tester t) {
    return t.checkExpect(this.exampleBoard3.getBackground(), 
        new RectangleImage(120, 120, "outline", Color.BLACK));
  }

  // testing the drawEverything method
  public boolean testDrawEverything(Tester t) {
    return t.checkExpect(this.exampleBoard3.drawEverything(), 
        new OverlayOffsetAlign(
            AlignModeX.LEFT, 
            AlignModeY.BOTTOM,
            new AboveAlignImage(
                AlignModeX.LEFT,
                new AboveAlignImage(
                    AlignModeX.LEFT, 
                    this.exampleBoard3.drawUnfinishedGuesses(),
                    this.exampleBoard3.drawFinishedGuesses()),
                this.exampleBoard3.drawBottomRow()), 0, 0, 
            new OverlayOffsetAlign(
                AlignModeX.LEFT, 
                AlignModeY.TOP, 
                new RectangleImage(40, 40, "outline", Color.RED), 0, 0, 
                this.exampleBoard3.getBackground())));
  }

  // testing the lastScene method
  public boolean testLastScene(Tester t) {
    return t.checkExpect(this.exampleBoard3.lastScene("win"), 
        new WorldScene(120, 120).placeImageXY(
            new OverlayOffsetAlign(
                AlignModeX.LEFT,
                AlignModeY.TOP,
                this.exampleBoard3.drawTopRow(), 0, 0,
                new OverlayOffsetAlign(
                    AlignModeX.RIGHT,
                    AlignModeY.TOP,
                    new OverlayImage(
                        new TextImage("win", 20, Color.BLACK),
                        new RectangleImage(80, 40, "solid", Color.RED)), 0, 0,
                    this.exampleBoard3.drawEverything())), 60, 60));
  }
}


class ExamplesILoLoColor {
  // general examples
  ILoColor mt = new MtLoColor();
  ILoColor g = new ConsLoColor(Color.GREEN, this.mt);
  ILoColor b = new ConsLoColor(Color.BLUE, this.mt);
  ILoColor r = new ConsLoColor(Color.RED, this.mt);

  ILoColor gg = new ConsLoColor(Color.GREEN, this.g);
  ILoColor gb = new ConsLoColor(Color.GREEN, this.b);
  ILoColor gr = new ConsLoColor(Color.GREEN, this.r);
  ILoColor bb = new ConsLoColor(Color.BLUE, this.b);
  ILoColor bg = new ConsLoColor(Color.BLUE, this.g);
  ILoColor br = new ConsLoColor(Color.BLUE, this.r);
  ILoColor rr = new ConsLoColor(Color.RED, this.r);
  ILoColor rg = new ConsLoColor(Color.RED, this.g);
  ILoColor rb = new ConsLoColor(Color.RED, this.b);

  ILoLoColor empty = new MtLoLoColor();
  ILoLoColor list1 = new ConsLoLoColor(this.gg, this.empty);
  ILoLoColor list2 = new ConsLoLoColor(this.bb, this.list1);
  ILoLoColor list3 = new ConsLoLoColor(this.rr, this.list2);

  ConsLoLoColor list4 = new ConsLoLoColor(this.gg, this.empty);
  ConsLoLoColor list5 = new ConsLoLoColor(this.bb, this.list4);
  ConsLoLoColor list6 = new ConsLoLoColor(this.rr, this.list5);

  // expected results for append method
  ILoLoColor list1append3 =
      new ConsLoLoColor(this.gg, 
          new ConsLoLoColor(this.rr, 
              new ConsLoLoColor(this.bb,
                  new ConsLoLoColor(this.gg, this.empty))));

  ILoLoColor list3append1 = 
      new ConsLoLoColor(this.rr, 
          new ConsLoLoColor(this.bb, 
              new ConsLoLoColor(this.gg,
                  new ConsLoLoColor(this.gg, this.empty))));

  // testing the length method
  boolean testLength(Tester t) {
    return t.checkExpect(this.empty.length(), 0)
        && t.checkExpect(this.list1.length(), 1)
        && t.checkExpect(this.list2.length(), 2)
        && t.checkExpect(this.list3.length(), 3);
  }

  // testing the append method
  boolean testAppend(Tester t) {
    return t.checkExpect(this.empty.append(this.empty), this.empty)
        && t.checkExpect(this.empty.append(this.list1), this.list1)
        && t.checkExpect(this.list1.append(this.empty), this.list1)
        && t.checkExpect(this.list1.append(this.list3), this.list1append3)
        && t.checkExpect(this.list3.append(this.list1), this.list3append1);
  }

  // testing the getExactMatchesLast function
  boolean testGetExactMatchesLast(Tester t) {
    return t.checkExpect(this.empty.getExactMatchesLast(this.gg), 0)
        && t.checkExpect(this.list1.getExactMatchesLast(this.gg), 2)
        && t.checkExpect(this.list2.getExactMatchesLast(this.gg), 2)
        && t.checkExpect(this.list3.getExactMatchesLast(this.rr), 0);
  }

  // testing the getExactMatchesLastHelp function
  boolean testGetExactMatchesLastHelp(Tester t) {
    return t.checkExpect(this.empty.getExactMatchesLastHelp(this.gg, this.gg), 2)
        && t.checkExpect(this.empty.getExactMatchesLastHelp(this.gg, this.bb), 0)
        && t.checkExpect(this.list1.getExactMatchesLastHelp(this.gg, this.gg), 2)
        && t.checkExpect(this.list2.getExactMatchesLastHelp(this.gg, this.rr), 0)
        && t.checkExpect(this.list3.getExactMatchesLastHelp(this.gg, this.gg), 2);
  }

  // testing the drawAllFinished method
  boolean testDrawAllFinishedGuesses(Tester t) {
    return t.checkExpect(this.empty.drawAllFinishedGuesses(15, this.mt), new EmptyImage())
        && t.checkExpect(this.empty.drawAllFinishedGuesses(15, this.gg), new EmptyImage())
        && t.checkExpect(this.list1.drawAllFinishedGuesses(15, this.gg), 
            new AboveImage(
                new EmptyImage(),
                new BesideImage(
                    list4.first.drawAll(15), 
                    list4.drawAllFinishedGuessesHelp(15, this.gg))))
        && t.checkExpect(this.list2.drawAllFinishedGuesses(15, this.bb), 
            new AboveImage(
                new AboveImage(
                    new EmptyImage(),
                    new BesideImage(
                        list4.first.drawAll(15), 
                        list4.drawAllFinishedGuessesHelp(15, this.bb))),
                new BesideImage(
                    list5.first.drawAll(15), 
                    list5.drawAllFinishedGuessesHelp(15, this.bb))))
        && t.checkExpect(this.list3.drawAllFinishedGuesses(15, this.rr), 
            new AboveImage(
                new AboveImage(
                    new AboveImage(
                        new EmptyImage(),
                        new BesideImage(
                            list4.first.drawAll(15), 
                            list4.drawAllFinishedGuessesHelp(15, this.rr))),
                    new BesideImage(
                        list5.first.drawAll(15), 
                        list5.drawAllFinishedGuessesHelp(15, this.rr))),
                new BesideImage(
                    list6.first.drawAll(15), 
                    list6.drawAllFinishedGuessesHelp(15, this.rr))));
  }

  // testing the drawAllFinishedHelp method
  boolean testDrawAllFinishedGuessesHelp(Tester t) {
    return t.checkExpect(this.empty.drawAllFinishedGuessesHelp(15, this.gg), new EmptyImage())
        && t.checkExpect(this.list1.drawAllFinishedGuessesHelp(15, this.gg), 
            new BesideImage(
                new OverlayImage(
                    new TextImage("2", 30, Color.BLACK), 
                    new RectangleImage(30, 30, "solid", Color.WHITE)),
                new OverlayImage(
                    new TextImage("0", 30, Color.BLACK), 
                    new RectangleImage(30, 30, "solid", Color.WHITE))))
        && t.checkExpect(this.list2.drawAllFinishedGuessesHelp(15, this.rb),
            new BesideImage(
                new OverlayImage(
                    new TextImage("1", 30, Color.BLACK), 
                    new RectangleImage(30, 30, "solid", Color.WHITE)),
                new OverlayImage(
                    new TextImage("0", 30, Color.BLACK), 
                    new RectangleImage(30, 30, "solid", Color.WHITE))))
        && t.checkExpect(this.list3.drawAllFinishedGuessesHelp(15, this.gg), 
            new BesideImage(
                new OverlayImage(
                    new TextImage("0", 30, Color.BLACK), 
                    new RectangleImage(30, 30, "solid", Color.WHITE)),
                new OverlayImage(
                    new TextImage("0", 30, Color.BLACK), 
                    new RectangleImage(30, 30, "solid", Color.WHITE))));
  }
}

class ExamplesILoColor {
  // general examples
  ILoColor mt = new MtLoColor();
  ILoColor g = new ConsLoColor(Color.GREEN, this.mt);
  ILoColor yg = new ConsLoColor(Color.YELLOW, this.g);
  ILoColor oyg = new ConsLoColor(Color.ORANGE, this.yg);
  ILoColor royg = new ConsLoColor(Color.RED, this.oyg);

  // expected results for the append method
  ILoColor gg = new ConsLoColor(Color.GREEN, new ConsLoColor(Color.GREEN, this.mt));
  ILoColor ygg = new ConsLoColor(Color.YELLOW, this.gg);

  // expected results for the removeColor method
  ILoColor roy = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.ORANGE, 
              new ConsLoColor(Color.YELLOW, this.mt)));
  ILoColor rog = 
      new ConsLoColor(Color.RED,
          new ConsLoColor(Color.ORANGE, 
              new ConsLoColor(Color.GREEN, this.mt)));
  ILoColor ryg =
      new ConsLoColor(Color.RED,
          new ConsLoColor(Color.YELLOW,
              new ConsLoColor(Color.GREEN, this.mt)));

  // expected results for the removeLastColor method
  ILoColor y = new ConsLoColor(Color.YELLOW, this.mt);
  ILoColor oy = new ConsLoColor(Color.ORANGE, new ConsLoColor(Color.YELLOW, this.mt));

  // examples for the getExactMatches method
  ILoColor rrrr = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.RED,
              new ConsLoColor(Color.RED,
                  new ConsLoColor(Color.RED, this.mt))));
  ILoColor rorr = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.ORANGE,
              new ConsLoColor(Color.RED,
                  new ConsLoColor(Color.RED, this.mt))));
  ILoColor royr = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.ORANGE,
              new ConsLoColor(Color.YELLOW,
                  new ConsLoColor(Color.RED, this.mt))));
  ILoColor rrrg = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.RED,
              new ConsLoColor(Color.RED,
                  new ConsLoColor(Color.GREEN, this.mt))));

  // examples for the getExactMatchesMt method
  MtLoColor mt2 = new MtLoColor();

  // examples for the getExactMatchesCons method
  ConsLoColor g2 = new ConsLoColor(Color.GREEN, this.mt);
  ConsLoColor royg2 = new ConsLoColor(Color.RED, this.oyg);
  ConsLoColor rrrr2 = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.RED,
              new ConsLoColor(Color.RED,
                  new ConsLoColor(Color.RED, this.mt))));
  ConsLoColor rorr2 = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.ORANGE,
              new ConsLoColor(Color.RED,
                  new ConsLoColor(Color.RED, this.mt))));
  ConsLoColor royr2 = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.ORANGE,
              new ConsLoColor(Color.YELLOW,
                  new ConsLoColor(Color.RED, this.mt))));
  ConsLoColor rrrg2 = 
      new ConsLoColor(Color.RED, 
          new ConsLoColor(Color.RED,
              new ConsLoColor(Color.RED,
                  new ConsLoColor(Color.GREEN, this.mt))));

  // expected results for the drawAll method
  WorldImage mtImage = new EmptyImage();
  WorldImage gImage = new BesideImage(new CircleImage(10, "solid", Color.GREEN), this.mtImage);
  WorldImage ygImage = new BesideImage(new CircleImage(10, "solid", Color.YELLOW), this.gImage);
  WorldImage oygImage = new BesideImage(new CircleImage(10, "solid", Color.ORANGE), this.ygImage);
  WorldImage roygImage = new BesideImage(new CircleImage(10, "solid", Color.RED), this.oygImage);

  // testing the length method
  boolean testLength(Tester t) {
    return t.checkExpect(this.mt.length(), 0)
        && t.checkExpect(this.g.length(), 1)
        && t.checkExpect(this.royg.length(), 4);
  }

  // testing the append method
  boolean testAppend(Tester t) {
    return t.checkExpect(this.mt.append(this.mt), this.mt)
        && t.checkExpect(this.mt.append(this.g), this.g)
        && t.checkExpect(this.g.append(this.mt), this.g)
        && t.checkExpect(this.g.append(this.g), this.gg)
        && t.checkExpect(this.yg.append(this.g), this.ygg);
  }

  // testing the getColorAtIndex method
  boolean testGetColorAtIndex(Tester t) {
    return t.checkException(
        new RuntimeException(
            "Given index is equal to or longer than the length of the list"), 
        this.mt, "getColorAtIndex", 0)
        && t.checkException(
            new RuntimeException(
                "Given index is equal to or longer than the length of the list"), 
            this.mt, "getColorAtIndex", 1)
        && t.checkException(
            new RuntimeException(
                "Given index is equal to or longer than the length of the list"), 
            this.royg, "getColorAtIndex", 4)
        && t.checkException(
            new RuntimeException(
                "Given index is equal to or longer than the length of the list"), 
            this.royg, "getColorAtIndex", 5)
        && t.checkExpect(this.royg.getColorAtIndex(0), Color.RED)
        && t.checkExpect(this.royg.getColorAtIndex(1), Color.ORANGE)
        && t.checkExpect(this.royg.getColorAtIndex(2), Color.YELLOW)
        && t.checkExpect(this.royg.getColorAtIndex(3), Color.GREEN);
  }

  // testing the removeColor method
  boolean testRemoveColor(Tester t) {
    return t.checkExpect(this.mt.removeColor(Color.RED), this.mt)
        && t.checkExpect(this.royg.removeColor(Color.BLACK), this.royg)
        && t.checkExpect(this.royg.removeColor(Color.GREEN), this.roy)
        && t.checkExpect(this.royg.removeColor(Color.YELLOW), this.rog)
        && t.checkExpect(this.royg.removeColor(Color.ORANGE), this.ryg)
        && t.checkExpect(this.royg.removeColor(Color.RED), this.oyg);
  }

  // testing the removeLastColor method
  boolean testRemoveLastColor(Tester t) {
    return t.checkExpect(this.mt.removeLastColor(), this.mt)
        && t.checkExpect(this.g.removeLastColor(), this.mt)
        && t.checkExpect(this.yg.removeLastColor(), this.y)
        && t.checkExpect(this.oyg.removeLastColor(), this.oy)
        && t.checkExpect(this.royg.removeLastColor(), this.roy);
  }

  // testing the removeLastColorHelp method
  boolean testRemoveLastColorHelp(Tester t) {
    return t.checkExpect(this.mt.removeLastColorHelp(Color.GREEN), this.mt)
        && t.checkExpect(this.g.removeLastColorHelp(Color.YELLOW), this.y)
        && t.checkExpect(this.yg.removeLastColorHelp(Color.ORANGE), this.oy)
        && t.checkExpect(this.oyg.removeLastColorHelp(Color.RED), this.roy);
  }

  // testing the getExactMatches method
  boolean testGetExactMatches(Tester t) {
    return t.checkExpect(this.mt.getExactMatches(this.mt), 0)
        && t.checkExpect(this.mt.getExactMatches(this.g), 0)
        && t.checkExpect(this.g.getExactMatches(this.mt), 0)
        && t.checkExpect(this.g.getExactMatches(this.g), 1)
        && t.checkExpect(this.g.getExactMatches(this.royg), 0)
        && t.checkExpect(this.royg.getExactMatches(this.g), 0)
        && t.checkExpect(this.royg.getExactMatches(this.rrrr), 1)
        && t.checkExpect(this.royg.getExactMatches(this.rorr), 2)
        && t.checkExpect(this.royg.getExactMatches(this.royr), 3)
        && t.checkExpect(this.royg.getExactMatches(this.rrrg), 2);
  }

  // testing the getExactMatchesMt method
  boolean testGetExactMatchesMt(Tester t) {
    return t.checkExpect(this.mt.getExactMatchesMt(this.mt2), 0)
        && t.checkExpect(this.g.getExactMatchesMt(this.mt2), 0)
        && t.checkExpect(this.royg.getExactMatchesMt(this.mt2), 0);
  }

  // testing the getExactMatchesCons method
  boolean testGetExactMatchesCons(Tester t) {
    return t.checkExpect(this.mt.getExactMatchesCons(this.g2), 0)
        && t.checkExpect(this.g2.getExactMatchesCons(this.g2), 1)
        && t.checkExpect(this.g2.getExactMatchesCons(this.royg2), 0)
        && t.checkExpect(this.royg2.getExactMatchesCons(this.g2), 0)
        && t.checkExpect(this.royg2.getExactMatchesCons(this.rrrr2), 1)
        && t.checkExpect(this.royg2.getExactMatchesCons(this.rorr2), 2)
        && t.checkExpect(this.royg2.getExactMatchesCons(this.royr2), 3)
        && t.checkExpect(this.royg2.getExactMatchesCons(this.rrrg2), 2);
  }

  // testing the drawAll method
  boolean testDrawAll(Tester t) {
    return t.checkExpect(this.mt.drawAll(10), this.mtImage)
        && t.checkExpect(this.g.drawAll(10), this.gImage)
        && t.checkExpect(this.yg.drawAll(10), this.ygImage)
        && t.checkExpect(this.oyg.drawAll(10), this.oygImage)
        && t.checkExpect(this.royg.drawAll(10), this.roygImage);
  }
}
