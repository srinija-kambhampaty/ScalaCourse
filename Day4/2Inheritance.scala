// Define a trait for Canine behaviors
trait canine {
  def test(): String = {
    "I am from the trait canine!"
  }
  
  def test_lamda: Int => Double = i => i.toDouble
}

trait mammals {
  def dummy: String = {
    "I am from the mammal trait"
  }
}

// Define the superclass
abstract class Animal(val name: String) extends canine {
  // Method in the superclass
  def speak(): String

  def test_lamda1: Int => String = i => i + ""
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
    override def speak(): String = {
      s"$name says: eehaww!"
    }
}

class crossbreed(name: String) extends Animal(name) with canine with mammals {
  override def speak(): String = {
    s"$name says: bow Meow"
  }
}

@main def main: Unit = {
val myDog = new Dog("Buddy")
val myCat = new Cat("Whiskers")
val mycrossbreed = new crossbreed("Coco")

println(mycrossbreed.dummy)
println(mycrossbreed.speak())

println(mycrossbreed.test_lamda(4))
println(mycrossbreed.test_lamda1(66))

println(myDog.speak())  
println(myCat.speak())  
println(myDog.test())

val myzebra = new zebra("stripes")
println(myzebra.test())

// Show that both Dog and Cat are instances of Animal
println(myDog.isInstanceOf[Animal]) 
println(myCat.isInstanceOf[Animal])  
}