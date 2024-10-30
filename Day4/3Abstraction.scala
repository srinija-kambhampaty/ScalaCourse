abstract class vehicle {
    //protected variables can be used in the subclasses
    protected val carfuelcap = 40.0
    protected val bikefuelcap = 18.0
    protected val autofuelcap = 9.0

    //private variables can be used in the methods of the class
    private val xtest: String = "I am a private variable of class vehicle"

    //public by default
    var xtype: String = "I am a public variable of class vehicle"
    var mileage: Int
    var model: String

    def printprivate: String = s"$xtest"
    def printprotected: String = s"$carfuelcap"

    def fuelEfficiency(): Double //abstract methods
    def setMileage(x:Int): Unit
    def describe(): String 
}

class car(var mileage: Int, var model: String) extends vehicle {
    def fuelEfficiency(): Double = {
        mileage / carfuelcap
    }

    // def printprotected1: String = s"$xtest" // we cannot access the private variables of the parent class
    def setMileage(x:Int): Unit = mileage = x

    def describe(): String = s"Car with mileage $mileage and model is $model"
}

class bike(var mileage: Int,var model: String) extends vehicle {
    def fuelEfficiency(): Double = {
        mileage / bikefuelcap
    }

    def setMileage(x:Int): Unit = mileage = x

    def describe(): String = s"Bike with $mileage and model $model"
}

class auto(var mileage: Int,var model: String) extends vehicle {
    def fuelEfficiency(): Double = {
        mileage / autofuelcap
    }

    def setMileage(x:Int): Unit = mileage = x

    def describe(): String = s"Auto with $mileage and model $model"
}


@main def main(): Unit = {
  val hyndaiCar = new car(20,"Hyndai")
  println(hyndaiCar.xtype) // public varible of the abstract class vehicle
  // println(hyndaiCar.carfuelcap) //protected variable of the bastaract class can be accessed here 
  println(hyndaiCar.printprivate) //printing the private variable through a method in vehicle
  println(hyndaiCar.printprotected) //printing the protected variable through a method in vehicle
  
  
  println(hyndaiCar.describe()) 
  println(hyndaiCar.fuelEfficiency())
  
  val audi = new car(18,"Audi")
  println(audi.describe())

}