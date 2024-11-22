import org.apache.spark.sql.SparkSession

object FilterEven {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("FilterEven")
      .master("local[1]")
      .getOrCreate()

    val numbers = List(0,1,2,3,4,5,6)
    val rddNumbers = spark.sparkContext.parallelize(numbers)
    val result = rddNumbers.filter(x => x % 2 == 0)

    result.collect().foreach(println)
    spark.stop()
  }
}
