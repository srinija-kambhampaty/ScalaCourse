// Define a trait for Canine behaviors
trait canine {
  def test(): String = {
    "I am from the trait canine!"
  }
}

// Define the superclass
class Animal(val name: String) extends canine {
  // Method in the superclass
  def speak(): String = {
    "The animal makes a sound."
  }
}

class Dog(name: String) extends Animal(name) with canine { //subclass
  override def speak(): String = {
    s"$name says: Woof!"
  }
}

class Cat(name: String) extends Animal(name) { //subclass
  override def speak(): String = {
    s"$name says: Meow!"
  }
}

class zebra(name: String) extends Animal(name){
    //mimicing multi-level inheretance by first make abstract class inhert trait 
    //and then inhereting abstract class
}

@main def main: Unit = {
val myDog = new Dog("Buddy")
val myCat = new Cat("Whiskers")

println(myDog.speak())  
println(myCat.speak())  
println(myDog.test())

val myzebra = new zebra("stripes")
println(myzebra.test())

// Show that both Dog and Cat are instances of Animal
println(myDog.isInstanceOf[Animal]) 
println(myCat.isInstanceOf[Animal])  
}