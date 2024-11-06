trait Task{
    def dotask: Unit = {
        println("From trait Task")
    }
}

trait Cook extends Task{
    override def dotask: Unit = {
        println("From trait Cook")
    }
}

trait Garnish extends Cook{
    override def dotask: Unit = {
        println("From trait Garnish")
    }
}

trait Pack extends Garnish{
    override def dotask: Unit = {
        println("From trait Pack")
    }
}

class Activity extends Task {
    def doactivity: Unit = {
        println("From class Acitivity")
        dotask
    }
}

@main def main: Unit = {
    var obj = new Activity()
    obj.doactivity
    println("break")
    val obj2:Task = new Activity with Cook with Pack with Garnish 
    obj2.dotask
}