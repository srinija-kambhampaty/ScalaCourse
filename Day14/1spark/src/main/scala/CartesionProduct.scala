import org.apache.spark.sql.SparkSession

object CartesionProduct {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("CartesionProduct")
      .master("local[1]")
      .getOrCreate()

    val array1 = Array(1,2,3,4,5)
    val array2 = Array(6,7,8,9,10)

    val rdd1 = spark.sparkContext.parallelize(array1)
    val rdd2 = spark.sparkContext.parallelize(array2)

    // println(rdd1)
    val result = rdd1.cartesian(rdd2)
    result.collect().foreach(println)
    spark.stop()

  }
}
