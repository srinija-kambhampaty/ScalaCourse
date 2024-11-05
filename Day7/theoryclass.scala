//Tring out either type
def roomavailability(x: Int): Either[String,Int]= {
    if(x >= 0){
        Right(x)
    }
    else{
        Left("No rooms available")
    }
}

//trying out block code while calling function
/*
block executes on spot, func:Unit is just a block cannot call teh function
a function block executes on call, func: => Unit is a function and works upon call
*/

def func(desc: String)(logic: => Unit): Unit = {
    println(s"Description: $desc")
}

def func2(desc: String)(logic: Unit): Unit = {
    println(s"Description: $desc")
}

@main def main: Unit = {
    roomavailability(2) match {
        case Right(value) => println(value)
        case Left(error) => println(error)
    }

    //trying out optional type
    val opt: Option[Int] = Some(33)
    val opt2: Option[Int] =  None

    opt2 match {
        case Some(value) => println(value)
        case None => println(null)
    }

    func("from the logic as function"){println("Hello from the block")}
    func2("from the logic as unit block"){println("hello from the unit block")}

}