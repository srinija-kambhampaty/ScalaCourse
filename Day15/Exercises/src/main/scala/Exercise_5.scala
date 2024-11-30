import org.apache.spark.sql.SparkSession

object Exercise5 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Exercise5 - Partitioning Impact on Performance")
      .master("local[2]")
      .getOrCreate()

    val sc = spark.sparkContext

    val rdd = sc.textFile("/Users/srinija/Git/ScalaCourse/Day15/Exercises/src/main/scala/movie.csv")

    println(s"Number of partitions: ${rdd.getNumPartitions}")

    val partitionsSeq = Seq(2, 4, 8)
    partitionsSeq.foreach { partition =>
      val repartitionedRDD = rdd.repartition(partition)

      val count = repartitionedRDD.count()
      println(s"Row count: $count")

      val sortedRDD = repartitionedRDD.sortBy(row => row)

      val fp = s"src/main/scala/Day15/exercises/output_partitions_$partition"
      sortedRDD.saveAsTextFile(fp)

    }

    println("Application is running. Press Enter to exit.")
    scala.io.StdIn.readLine()

    sc.stop()
  }
}