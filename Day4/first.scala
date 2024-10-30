object first {
  def main(args: Array[String]): Unit = {
    var count = 0

    // Using do-while loop
    do {
      println(s"Count: $count")
      count += 1
    } while (count < 5)
  }
}
