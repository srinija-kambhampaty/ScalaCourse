package Cleanup

import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.{FileSystem, Path}
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object DailySparkJob {
  def main(args: Array[String]): Unit = {
    // Initialize SparkSession
    val spark = SparkSession.builder
      .appName("DailySparkJob")
      .config("spark.hadoop.fs.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFileSystem")
      .config("spark.hadoop.fs.AbstractFileSystem.gs.impl", "com.google.cloud.hadoop.fs.gcs.GoogleHadoopFS")
      .config("spark.hadoop.google.cloud.auth.service.account.enable", "true")
      .config("spark.hadoop.google.cloud.auth.service.account.json.keyfile", "/Users/srinija/gcp_key.json")
      .master("local[*]")
      .getOrCreate()

    val hadoopConf = spark.sparkContext.hadoopConfiguration

    // GCS Paths
    val rawDataPath = "gs://srinijakambhampaty/raw/sensor-data"

    // Calculate cutoff date for deletion (7 days ago)
    val cutoffDate = LocalDateTime.now().minusDays(7)
    val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH")

    // Initialize FileSystem for GCS
    val fs = FileSystem.get(new URI(rawDataPath), hadoopConf)

    try {
      val rawDataBasePath = new Path(rawDataPath)

      if (fs.exists(rawDataBasePath)) {
        val yearDirs = fs.listStatus(rawDataBasePath).filter(_.isDirectory)

        yearDirs.foreach(yearDir => {
          val yearPath = yearDir.getPath
          val monthDirs = fs.listStatus(yearPath).filter(_.isDirectory)

          monthDirs.foreach(monthDir => {
            val monthPath = monthDir.getPath
            val dayDirs = fs.listStatus(monthPath).filter(_.isDirectory)

            dayDirs.foreach(dayDir => {
              val dayPath = dayDir.getPath
              val hourDirs = fs.listStatus(dayPath).filter(_.isDirectory)

              hourDirs.foreach(hourDir => {
                val hourPath = hourDir.getPath
                val hourString = hourPath.toString.split("/").takeRight(4).mkString("/")
                val fileTime = LocalDateTime.parse(hourString, formatter)

                if (fileTime.isBefore(cutoffDate)) {
                  println(s"Deleting old directory: $hourPath")
                  fs.delete(hourPath, true)
                }
              })
            })
          })
        })
      } else {
        println(s"Raw data path does not exist: $rawDataPath")
      }
    } catch {
      case e: Exception => println(s"Error while deleting files: ${e.getMessage}")
    } finally {
      fs.close()
      spark.stop()
    }
  }
}
