implicit class EnhancedInt(val x: Double) {
  def rangeFinder():String = {
    if(x>=0.0 && x <=100.0)
        return "In the Range of 0-100"
    else if(x>100.0)
        return "In the Range of 100+"
    else 
        return "negative"
  }
}

abstract class shape{
    def area(params: Double*): Double //abstract method to claculate area with varible  number of parameters
    // def show(): Unit
    def perimeter(params: Double*): Double 
}

class square(xtype: String) extends shape{
    def this() = {
        this("Square shape")
    }

    override def area(params: Double*): Double = {
        if(params.length != 1)
            println("Pasds only one paarameter for square")
        val side = params(0)
        val a = side * side
        println(s"Area of $xtype is $a")
        a
    }

    override def perimeter(params: Double*): Double = {
        if(params.length != 1)
            println("Pasds only one paarameter for square")
        val side = params(0)
        val p = 4*(side)
        println(s"Perimeter of $xtype is $p")
        p
    }

}

class rectangle(xtype: String) extends shape{
    def this() = {
        this("Rectange shape")
    }
    override def area(params: Double*): Double = {
        if(params.length != 2)
            println("Pass two parameters for rectangle")
        val lenght = params(0)
        val breath = params(1)
        val a = lenght * breath
        println(s"Area of $xtype is $a")
        a
    }

    override def perimeter(params: Double*): Double = {
        if(params.length != 2)
            println("Pass two parameters for rectangle")
        val lenght = params(0)
        val breath = params(1)
        2*(lenght + breath)
    }

}

class circle extends shape{
    override def area(params : Double*): Double = {
        if(params.length !=1)
            println("pass one parameters for circle") 
        val radius = params(0)
        (3.14)*radius*radius
    } 
    override def perimeter(params : Double*): Double = {
        if(params.length !=1)
            println("pass one parameters for circle")
        val radius = params(0)
        2*(3.14)*radius
    }
}

class triangle extends shape{
    override def area(params: Double*): Double = {
        if(params.length != 2)
            println("pass two parameters for triangle for area")
        val base = params(0)
        val height = params(1)
        (0.5)*base*height  
    }
    override def perimeter(params: Double*): Double = {
        if(params.length != 3)
            println("pass three parameters for triangle for perimeter")
        val s1 = params(0)
        val s2 = params(1)
        val s3 = params(2)
        s1+s2+s3 
    }
}

@main def main: Unit = {
    val objsquare = new square("square plot")
    println(objsquare.area(3))
    println(objsquare.perimeter(2))
    objsquare.perimeter(4)

    println(objsquare.area(4).rangeFinder())
}