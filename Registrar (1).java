import java.util.function.Predicate;
import tester.Tester;

//represents a course that has a name, professor, and list of students
class Course {
  String name;
  Instructor prof;
  IList<Student> students;

  Course(String name, Instructor prof) {
    this.name = name;
    this.prof = prof;
    this.prof.courses = new ConsList<Course>(this, this.prof.courses);
    this.students = new MtList<Student>();
  }

  // adds the given student to the list of students in
  // this course
  public void addToCourse(Student s) {
    this.students = new ConsList<Student>(s, this.students);
  }
}

//represents an Instructor that has a name and a list of courses
// that they teach
class Instructor {
  String name;
  IList<Course> courses;

  Instructor(String name) {
    this.name = name;
    this.courses = new MtList<Course>();
  }

  // determines if the given student is in more thant 1 of this
  // instructors courses
  public Boolean dejavu(Student c) {
    return this.courses.filter(new StudentIn(c)).length() > 1;
  }
}

//represents a student with a name, ID and list of courses
// that they are enrolled in
class Student {
  String name;
  int id;
  IList<Course> courses;

  Student(String name, int id) {
    this.name = name;
    this.id = id;
    this.courses = new MtList<Course>();

  }

  // enrolls this student in the given course
  // and also adds this student to the courses list
  // of students
  public void enroll(Course c) {
    c.addToCourse(this);
    this.courses = new ConsList<Course>(c, this.courses);
  }

  // does this student share any courses with the given student
  public boolean classmates(Student c) {
    return this.courses.orMap(new StudentIn(c));
  }

  // Does this student share the same name and ID as the given student
  public boolean sameIdName(Student s) {
    return this.id == s.id && this.name.equals(s.name);

  }
}

//Is the student given to this function in the course?
class StudentIn implements Predicate<Course> {
  Student stud;

  StudentIn(Student stud) {
    this.stud = stud;
  }

  // returns true if the student passed into the constructor
  // appears in the given course
  public boolean test(Course t) {
    return t.students.orMap(new SameStudent(this.stud));

  }
}

//checks if the given student is the same as the student
//passed into the predicate
class SameStudent implements Predicate<Student> {
  Student stud;

  SameStudent(Student stud) {
    this.stud = stud;
  }

  // is the student passed into this predicate the same as the
  // the given student
  public boolean test(Student t) {
    return t.sameIdName(this.stud);
  }
}

//represents a list with a generic type
interface IList<T> {

  // filters out all items in this IList that do not
  // pass the predicate
  IList<T> filter(Predicate<T> pred);

  // determines the length of this IList
  int length();

  // Returns true if any of the items in this
  // List pass the predicate
  Boolean orMap(Predicate<T> pred);
}

//represents an MtList of the given generic type
class MtList<T> implements IList<T> {

  // filters this empty IList, returning an empty list
  public IList<T> filter(Predicate<T> pred) {
    return this;
  }

  // Returns false since no Items in this empty list
  // pass the given predicate
  public Boolean orMap(Predicate<T> pred) {
    return false;
  }

  // returns the length of this emtpy list, which is 0
  public int length() {
    return 0;
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

  // Returns true if any of the elements in this IList
  // pass the predicate
  public Boolean orMap(Predicate<T> pred) {
    return pred.test(this.first) || this.rest.orMap(pred);
  }

  // filters out all elements in this IList which do not
  // pass the predicate
  public IList<T> filter(Predicate<T> pred) {
    if (pred.test(this.first)) {
      return new ConsList<T>(this.first, this.rest.filter(pred));
    }
    else {
      return this.rest.filter(pred);
    }
  }

  // returns the length of this IList
  public int length() {
    return 1 + this.rest.length();
  }

}

class ExamplesSchool {

  Student lukas;
  Student ido;
  Student daniel;
  Student nerd;
  Student lusa;
  Student daniel2;

  Course fundies1;
  Course fundies2;
  Course ood;
  Course discrete;

  Instructor leena;
  Instructor john;

  IList<Course> mtC = new MtList<Course>();
  IList<Student> mtS = new MtList<Student>();

  // initializes data
  void initData() {
    this.lukas = new Student("lukas", 1);
    this.ido = new Student("ido", 2);
    this.daniel = new Student("daniel", 3);
    this.nerd = new Student("nerd", 4);
    this.lusa = new Student("lusa", 5);
    this.daniel2 = new Student("daniel", 6); // for test of sameIdName
    this.leena = new Instructor("Leena");
    this.john = new Instructor("John");
    this.fundies1 = new Course("fundies1", this.john);
    this.fundies2 = new Course("fundies2", this.leena);
    this.ood = new Course("OOD", this.leena);
    this.discrete = new Course("Discrete", this.john);

  }

  // tests Enroll(Student)
  void testEnroll(Tester t) {
    this.initData();
    // tests initial data
    t.checkExpect(this.lukas.courses, this.mtC);
    t.checkExpect(this.ido.courses, this.mtC);
    t.checkExpect(this.fundies2.students, this.mtS);
    t.checkExpect(this.discrete.students, this.mtS);
    t.checkExpect(this.leena.courses,
        new ConsList<Course>(this.ood, new ConsList<Course>(this.fundies2, this.mtC)));
    // enrolles students
    this.lukas.enroll(this.discrete);
    this.ido.enroll(this.fundies2);
    // tests effects of enrollment
    t.checkExpect(this.lukas.courses, new ConsList<Course>(this.discrete, this.mtC));
    t.checkExpect(this.ido.courses, new ConsList<Course>(this.fundies2, this.mtC));
    t.checkExpect(this.fundies2.students, new ConsList<Student>(this.ido, this.mtS));
    t.checkExpect(this.discrete.students, new ConsList<Student>(this.lukas, this.mtS));
    t.checkExpect(this.leena.courses,
        new ConsList<Course>(this.ood, new ConsList<Course>(this.fundies2, this.mtC)));
    // enrolls another student
    this.lusa.enroll(this.discrete);
    // checksEffect
    t.checkExpect(this.discrete.students,
        new ConsList<Student>(this.lusa, new ConsList<Student>(this.lukas, this.mtS)));
  }

  // tests classmates
  void testClassmatesEnroll(Tester t) {
    this.initData();
    this.lukas.enroll(this.discrete);
    this.ido.enroll(this.fundies2);
    this.ido.enroll(this.ood);
    this.lukas.enroll(this.ood);
    this.nerd.enroll(this.discrete);
    this.nerd.enroll(this.fundies2);
    this.nerd.enroll(this.ood);
    this.nerd.enroll(this.fundies1);
    this.lusa.enroll(this.fundies1);

    t.checkExpect(this.lukas.courses,
        new ConsList<Course>(this.ood, new ConsList<Course>(this.discrete, this.mtC)));
    t.checkExpect(this.ido.courses,
        new ConsList<Course>(this.ood, new ConsList<Course>(this.fundies2, this.mtC)));
    t.checkExpect(this.nerd.courses,
        new ConsList<Course>(this.fundies1, new ConsList<Course>(this.ood,
            new ConsList<Course>(this.fundies2, new ConsList<Course>(this.discrete, this.mtC)))));

    t.checkExpect(this.nerd.classmates(this.daniel), false);
    t.checkExpect(this.nerd.classmates(this.ido), true);
    t.checkExpect(this.ido.classmates(this.nerd), true);
    t.checkExpect(this.lukas.classmates(this.ido), true);
    t.checkExpect(this.lusa.classmates(this.nerd), true);
    t.checkExpect(this.lusa.classmates(this.lukas), false);
    t.checkExpect(this.lukas.classmates(this.lusa), false);

    t.checkExpect(this.leena.dejavu(this.ido), true);
    t.checkExpect(this.leena.dejavu(this.nerd), true);
    t.checkExpect(this.leena.dejavu(this.lukas), false);
    t.checkExpect(this.john.dejavu(this.ido), false);
    t.checkExpect(this.john.dejavu(this.lukas), false);
    t.checkExpect(this.john.dejavu(this.nerd), true);
    t.checkExpect(this.leena.dejavu(this.lusa), false);
    t.checkExpect(this.john.dejavu(this.lusa), false);
  }

  void testaddToCourse(Tester t) {
    this.initData();
    t.checkExpect(this.fundies2.students, this.mtS);
    t.checkExpect(this.fundies1.students, this.mtS);

    this.fundies1.addToCourse(this.lukas);
    this.fundies2.addToCourse(this.lukas);
    this.fundies2.addToCourse(this.ido);

    t.checkExpect(this.fundies1.students, new ConsList<Student>(this.lukas, this.mtS));
    t.checkExpect(this.fundies2.students,
        new ConsList<Student>(this.ido, new ConsList<Student>(this.lukas, this.mtS)));
    t.checkExpect(this.ood.students, this.mtS);
  }

  void testsameIdName(Tester t) {
    this.initData();
    t.checkExpect(this.daniel.sameIdName(lukas), false);
    t.checkExpect(this.daniel.sameIdName(daniel2), false);
    t.checkExpect(this.lukas.sameIdName(lukas), true);
  }

  void testStudentIn(Tester t) {
    this.initData();
    t.checkExpect(new StudentIn(this.lukas).test(this.discrete), false);
    t.checkExpect(new StudentIn(this.lukas).test(this.fundies2), false);
    t.checkExpect(new StudentIn(this.nerd).test(this.discrete), false);

    this.lukas.enroll(this.discrete);
    this.ido.enroll(this.fundies2);
    this.ido.enroll(this.ood);
    this.lukas.enroll(this.ood);
    this.nerd.enroll(this.discrete);
    this.nerd.enroll(this.fundies2);
    this.nerd.enroll(this.ood);
    this.nerd.enroll(this.fundies1);
    this.lusa.enroll(this.fundies2);

    t.checkExpect(new StudentIn(this.lukas).test(this.discrete), true);
    t.checkExpect(new StudentIn(this.lukas).test(this.fundies2), false);
    t.checkExpect(new StudentIn(this.nerd).test(this.discrete), true);

    // test list abstractions

    t.checkExpect(this.lukas.courses.filter(new StudentIn(this.daniel)), this.mtC);
    t.checkExpect(this.nerd.courses.filter(new StudentIn(this.ido)),
        new ConsList<Course>(this.ood, new ConsList<Course>(this.fundies2, this.mtC)));
    t.checkExpect(this.nerd.courses.filter(new StudentIn(this.lukas)),
        new ConsList<Course>(this.ood, new ConsList<Course>(this.discrete, this.mtC)));
    t.checkExpect(this.ido.courses.filter(new StudentIn(this.lusa)),
        new ConsList<Course>(this.fundies2, this.mtC));
    t.checkExpect(this.mtC.filter(new StudentIn(this.daniel)), this.mtS);

    t.checkExpect(this.fundies1.students.orMap(new SameStudent(this.daniel)), false);
    t.checkExpect(this.discrete.students.orMap(new SameStudent(this.lukas)), true);
    t.checkExpect(this.ood.students.orMap(new SameStudent(this.nerd)), true);
    t.checkExpect(this.mtS.orMap(new SameStudent(this.lukas)), false);

    t.checkExpect(this.ood.students.length(), 3);
    t.checkExpect(this.leena.courses.length(), 2);
    t.checkExpect(this.nerd.courses.length(), 4);
    t.checkExpect(this.mtC.length(), 0);
  }

  void testsameStudent(Tester t) {
    this.initData();
    t.checkExpect(new SameStudent(this.ido).test(this.ido), true);
    t.checkExpect(new SameStudent(this.daniel2).test(this.daniel), false);
    t.checkExpect(new SameStudent(this.lukas).test(this.ido), false);
  }
}
