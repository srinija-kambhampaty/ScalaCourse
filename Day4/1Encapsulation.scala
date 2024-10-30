class Encapsulation(var number: Double){  //Class creation in Scala with properties and methods
    //primary constructor in the header of the class creation 

    def this(number: Double, xtype: Int) = {  // Defining the variables by constructor
        this(number)
        this.xtype = xtype
    }

    def add(x: Double): Unit  = number += x

    def subtract(x: Double): Unit = number -= x

    def multiply(x: Double): Unit = number *= x

    def Getnumber: Double = number

    def divisibility(digit: Int): Boolean = {
        if(number % digit == 0) 
            true
        else
            false
    }

    private var num  = 0  //Instance Variables
    def getnum: Int = num // Accesing the instance variables by a method.

    var xtype: Int = 0 //Instance variable

}

//Object in Scala
@main def main(): Unit = {
    println("inside the main function")
    var obj =  new Encapsulation(3000.0) //Object creation in Scala
    var obj1 = new Encapsulation(1500.0,2) //Object creation with two paraeters after the secondary constructor
    // println(obj.num) // will not possible as num is a private variable
    val x = obj.getnum
    // println(obj.y) // cannot access this as we havent mentioned teh tyoe
    println(obj.number)
    println(obj1.xtype) //second object 
    println(s"The num value through a method : $x")
    println(s"Checking the divisibily with 7: ${obj.divisibility(7)}")
    obj.add(3)
    println(s"Getting the current value of the number: ${obj.Getnumber}")
    println(s"Again Checking the divisibily with 7: ${obj.divisibility(7)}")
}


