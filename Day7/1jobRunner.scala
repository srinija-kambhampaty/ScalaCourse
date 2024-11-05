case class jobRunner(description: String, timeInSeconds: Int)

object jobRunner{
    def apply(description: String, timeInSeconds: Int)(logic: => Unit): jobRunner = {
        for( i <- 1 to timeInSeconds){
            println(i)
        }
        logic
        new jobRunner(description,timeInSeconds)
    }
}

@main def main: Unit = {
    var time: Int = 4
    var obj:jobRunner = jobRunner("Background job",time){
        println(s"Hello, I am from inside the block of code  after $time")
    }
}


