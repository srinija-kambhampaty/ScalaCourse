import org.apache.spark.sql.SparkSession

object RemoveDuplicate {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("Union and Remove Duplicates")
      .master("local[1]")
      .getOrCreate()

    val rdd1 = spark.sparkContext.parallelize(Seq(1, 2, 3, 4, 5))
    val rdd2 = spark.sparkContext.parallelize(Seq(4, 5, 6, 7, 8))
    val unionRDD = rdd1.union(rdd2)
    val distinctRDD = unionRDD.distinct()
    distinctRDD.collect().foreach(println)
    spark.stop()
  }
}

