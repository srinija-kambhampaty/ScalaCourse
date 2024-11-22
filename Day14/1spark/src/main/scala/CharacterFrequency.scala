import org.apache.spark.sql.SparkSession

object CharacterFrequency {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder
      .appName("CharacterFrequency")
      .master("local[1]")
      .getOrCreate()

    val data = Seq(
      "Hello world",
      "Spark is powerful",
      "RDDs are great"
    )

    val rdd = spark.sparkContext.parallelize(data)
    val charactersRdd = rdd.flatMap(line => line.toLowerCase.replaceAll("[^a-z]", "").toCharArray)
    val characterPairs = charactersRdd.map(c => (c, 1))
    val characterCount = characterPairs.reduceByKey(_ + _)
    characterCount.collect().foreach{ case (char, count) =>
      println(s"Character: '$char', Count: $count")
    }

    spark.stop()
  }
}

