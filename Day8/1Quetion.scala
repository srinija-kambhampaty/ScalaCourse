trait GetStarted{
    def prepare(): Unit = {
        println("from the getstarted prepare")
    }
}

trait Cook extends GetStarted{
    override def prepare(): Unit = {
        super.prepare()
        println("From the cook prepare")

    }

}

trait Seasoning{
    def applySeasoning(): Unit = {
        println("From the Seasoning applyseasoning")
    }
}

class Food extends Cook with Seasoning{
    def prepareFood(): Unit = {
        prepare()
        applySeasoning()
    }
}

@main def main: Unit = {
    var obj = new Food()
    obj.prepareFood()

}

//I have only used the override keyword only to override the function prepare.
//No need of abstract keyword.