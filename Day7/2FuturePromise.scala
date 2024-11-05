import scala.concurrent.{Future, Promise}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.util.Random
import java.util.concurrent.atomic.AtomicBoolean

object AsyncAwait {
def main(args: Array[String]): Unit = {

    def performAsyncOperation(): Future[String] = {
        val promise = Promise[String]()
        val isCompleted = new AtomicBoolean(false)

        def startThread(threadnumber: String): Thread = new Thread(new Runnable {
            def run(): Unit = {
                val random = new Random() 
                while(!isCompleted.get() && !promise.isCompleted){
                    val randomInt = random.nextInt(2000)
                    // println(s"$threadnumber has generated $randomInt")
                    if(randomInt == 1567 && !isCompleted.getAndSet(true)){
                        promise.success(s"$threadnumber has generated 1567")
                    }
                }
            }
        })


        val firstThread = startThread("First Thread")
        val secondThread = startThread("Second Thread")
        val thirdThread =  startThread("Third Thread")
        firstThread.start()
        secondThread.start()
        thirdThread.start()
        promise.future
    }
    
    val futureresult: Future[String] = performAsyncOperation()

    futureresult.onComplete {
        case Success(message) => println(message)
        case Failure(exception) => println(s"error has occured")
    }
    Thread.sleep(15000)
}
}