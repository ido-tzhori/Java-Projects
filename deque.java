import tester.Tester;
import java.util.function.Predicate;

class Deque<T> {
  Sentinel<T> header;
  
  Deque() {
    this.header = new Sentinel<T>();
  }
  
  Deque(Sentinel<T> header) {
    this.header = header;
  }
  
  public int size() {
   return this.header.sizeHelp();
  }
  
  public void addAtHead(T t) {
    new Node<T>(t).addInBetween(this.header, this.header.next);
  }
  
  public void addAtTail(T t) {
    new Node<T>(t).addInBetween(this.header.prev, this.header);
  }
  
  public T removeFromHead() {
    return this.header.next.remove();
  }
  
  public T removeFromTail() {
    return this.header.prev.remove();
  }
  
  public ANode<T> find(Predicate<T> pred) {
    return this.header.next.findHelp(pred);
  }
  
  public void removeNode(ANode<T> node) {
    node.helpRemove();
  }
}


abstract class ANode<T> {
  ANode<T> next, prev;
  /*
  ANode(ANode<T> next, ANode<T> prev) {
    this.next = next;
    this.prev = prev;
  }*/
  
  
  public abstract int sizeHelp2();
  
  public void addInBetween(ANode<T> first, ANode<T> last) {
    this.next = last;
    this.prev = first;
    last.prev = this;
    first.next = this;
  }
  
  public abstract T remove();
  
  public abstract ANode<T> findHelp(Predicate<T> pred);
  
  public abstract void helpRemove();

  
}

class Sentinel<T> extends ANode<T> {
  
  Sentinel() {
    this.next = this;
    this.prev = this;
  }
  
  public int sizeHelp() {
    return this.next.sizeHelp2();
  }
  
  public int sizeHelp2() {
    return 0;
  }
  
  public T remove() {
    throw new RuntimeException("cant remove from empty list");
  }

  public ANode<T> findHelp(Predicate<T> pred) {
   
    return this;
  }

  
  public void helpRemove() {
    
    throw new RuntimeException("Cant remove sentinel");
  }
  
}

class Node<T> extends ANode<T> {
  
  T data;
  
  Node(T data) {
    this.data = data;
    this.next = null;
    this.prev = null;
  }
  
  Node(T data, ANode<T> next, ANode<T> prev) {
    this.data = data;
    if (next == null || prev == null) {
      throw new IllegalArgumentException("Can't be constructed");
    }
    else {
      this.next = next;
      this.prev = prev;
      this.prev.next = this;
      this.next.prev = this;
    }
  }
  
  
  public int sizeHelp2() {
    return 1 + this.next.sizeHelp2();
  }
  
  public T remove() {
    this.prev.next = this.next;
    this.next.prev = this.prev;
    return this.data;
  }

 
  public ANode<T> findHelp(Predicate<T> pred) {
   
    if(pred.test(this.data)) {
      return this;
    }
    else {
      return this.next.findHelp(pred);
    }
  }

  @Override
  public void helpRemove() {
    this.prev.next = this.next;
    this.next.prev = this.prev;
    
  }
  
  
}

class firstD implements Predicate<String> {

  
  public boolean test(String t) {
    
    return t.equals("abc");
  }
  
}



class ExamplesDeque {
  Deque<String> deque1;
  Sentinel<String> sent2;
  Deque<String> deque2;
  ANode<String> node21;
  ANode<String> node22;
  ANode<String> node23;
  ANode<String> node24;

  

  
  
  
  
  void initData() {
    this.deque1 = new Deque<String>();
    
    this.sent2 = new Sentinel<String>();
  
    this.deque2 = new Deque<String>(this.sent2);
   
    
    /*
    Sentinel<String> sent3 = new Sentinel<String>();
  
    Deque<String> deque3 = new Deque<String>(this.sent3);
  */
  
  //list 2
    this.node22 = new Node<String>("bcd");
    this.node21 = new Node<String>("abc", this.node22, this.sent2);
    this.node24 = new Node<String>("def", this.sent2, this.node22);
    this.node23 = new Node<String>("cde", this.node24, this.node22);

    

  
    /*
  //list 3
  ANode<String> node32 = new Node<String>("qwe");
  ANode<String> node31 = new Node<String>("adf", this.node32, this.sent3);
  ANode<String> node34 = new Node<String>("hgr", this.sent3, this.node32);
  ANode<String> node33 = new Node<String>("jtyjh", this.node34, this.node32);
  */
  }
  
  Sentinel<String> sentAH = new Sentinel<String>();
  Deque<String> dequetestAH = new Deque<String>(this.sentAH);
  
  ANode<String> nodeah2 = new Node<String>("bcd");
  ANode<String> nodeah1 = new Node<String>("abc", this.nodeah2, this.sentAH);
  ANode<String> nodeah4 = new Node<String>("def", this.sentAH, this.nodeah2);
  ANode<String> nodeah3 = new Node<String>("cde", this.nodeah4, this.nodeah2);
  ANode<String> nodeADD = new Node<String>("zzz", this.nodeah1, this.sentAH);
  
  //tests size
  void testSize(Tester t) {
    this.initData();
    t.checkExpect(this.deque2.size(), 4);
    t.checkExpect(this.deque1.size(), 0);
    
  }
  
  void testAddatHeader(Tester t) {
    this.initData();
    this.deque2.addAtHead("zzz");
    t.checkExpect(this.deque2, this.dequetestAH);
  }
  
  void testRemoveFromHead(Tester t) {
    this.initData();
    t.checkExpect(this.deque2.removeFromHead(), "abc");
    //t.checkExpect(this.deque2, null);
  }
  
  void testRemoveFromTail(Tester t) {
    this.initData();
    t.checkExpect(this.deque2.removeFromTail(), "def");
   // t.checkExpect(this.deque2, null);
  }
  
  void testFind(Tester t) {
    this.initData();
    t.checkExpect(this.deque2.find(new firstD()), this.node21);
  }
  
  void testRemoveNode(Tester t) {
    this.initData();
    this.deque2.removeNode(node21);
    t.checkExpect(this.deque2, null);
  }
}









