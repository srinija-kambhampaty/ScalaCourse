import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper
import org.apache.spark.sql.SparkSession
import java.io.File

object Exercise_1 {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder
      .appName("Exercise_1")
      .master("local[2]")
      .getOrCreate()

    def extractTextFromPDF(pdfPath: String): String = {
      val document = PDDocument.load(new File(pdfPath))
      val stripper = new PDFTextStripper()
      val text = stripper.getText(document)
      document.close()
      text
    }

    val pdfPath = "/Users/srinija/Git/ScalaCourse/Day15/Exercises/src/main/scala/Scala-Play-AKka-Projects.pdf"
    val text = extractTextFromPDF(pdfPath)

    val rdd = spark.sparkContext.parallelize(text.split("\n"))
    println(s"Initial number of partitions: ${rdd.getNumPartitions}")

    val rddRepartitioned = rdd.repartition(4)
    println(s"Number of partitions after repartitioning: ${rddRepartitioned.getNumPartitions}")


    val rddCoalesced = rddRepartitioned.coalesce(2)
    println(s"Number of partitions after coalescing: ${rddCoalesced.getNumPartitions}")


    val collectedData = rddCoalesced.collect()
    readLine()
    collectedData.zipWithIndex.foreach { case (line, idx) =>
      println(s"Line ${idx + 1}: $line")
    }

    spark.stop()
  }
}
