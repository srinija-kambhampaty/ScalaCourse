abstract class vehicle {
    var mileage: Int
    // var fuelcap: Int
    var model: String

    def fuelEfficiency(): Double //abstract method
    def setMileage(x:Int): Unit
    def describe(): String 
}

class car(var mileage: Int, var model: String) extends vehicle {
    def fuelEfficiency(): Double = {
        mileage / 40.0
    }

    def setMileage(x:Int): Unit = mileage = x

    def describe(): String = s"Car with mileage $mileage and model is $model"
}

class bike(var mileage: Int,var model: String) extends vehicle {
    def fuelEfficiency(): Double = {
        mileage / 18.0
    }

    def setMileage(x:Int): Unit = mileage = x

    def describe(): String = s"Bike with $mileage and model $model"
}

class auto(var mileage: Int,var model: String) extends vehicle {
    def fuelEfficiency(): Double = {
        mileage / 9.0
    }

    def setMileage(x:Int): Unit = mileage = x

    def describe(): String = s"Auto with $mileage and model $model"
}


@main def main(): Unit = {
  val hyndaiCar = new car(20,"Hyndai")
  println(hyndaiCar.describe())
  println(hyndaiCar.fuelEfficiency())
  
  val audi = new car(18,"Audi")
  println(audi.describe())

}