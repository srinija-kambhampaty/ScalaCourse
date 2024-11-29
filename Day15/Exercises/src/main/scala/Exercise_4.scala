import org.apache.spark.sql.SparkSession

object Exercise_4 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Exercise_4")
      .master("local[2]")
      .getOrCreate()

    val sc = spark.sparkContext
    val numbers = sc.parallelize(1 to 1000)
    val evenNumbers = numbers.filter(_ % 2 == 0)
    val multipliedNumbers = evenNumbers.map(_ * 10)
    val keyValuePairs = multipliedNumbers.map(num => (num % 100, num))
    val groupedSums = keyValuePairs.reduceByKey(_ + _)
    val results = groupedSums.collect()
    results.foreach { case (key, sum) =>
      println(s"Remainder $key: Sum = $sum")
    }
    Thread.sleep(30000)

    spark.stop()

  }
}
