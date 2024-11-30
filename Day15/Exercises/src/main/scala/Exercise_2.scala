import org.apache.spark.sql.SparkSession

object Exercise_2 {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("Exercise_2")
      .master("local[2]")
      .getOrCreate()

    val sc = spark.sparkContext
    val numbersRDD = sc.parallelize(1 to 1000)

    val squaredRDD = numbersRDD.map(num => num * num)
    val filteredRDD = squaredRDD.filter(num => num % 2 == 0)

    val keyValueRDD = filteredRDD.map(num => (num % 10, num))
    val groupedRDD = keyValueRDD.groupByKey()

    groupedRDD.saveAsTextFile("output/narrow_vs_wide_results")

    println("Application completed. Visit the Spark UI to observe the DAG.")
    readLine()
    Thread.sleep(30000)
    spark.stop()
  }
}
