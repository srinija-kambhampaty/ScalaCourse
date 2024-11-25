import org.apache.spark.sql.SparkSession

object Exercise_3 {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("Analyze Tasks and Executors")
      .master("local[2]")
      .config("spark.executor.instances", "2")
      .getOrCreate()

    val sc = spark.sparkContext
    val lines = sc.parallelize(Seq.fill(1000000)("lorem ipsum dolor sit amet"), numSlices = 8)
    val words = lines.flatMap(line => line.split(" "))
    val wordPairs = words.map(word => (word, 1))

    val wordCounts = wordPairs.reduceByKey(_ + _)

    wordCounts.collect()
    println("Job completed. Visit Spark UI to analyze task and executor details.")
    Thread.sleep(30000)

    spark.stop()
  }
}
