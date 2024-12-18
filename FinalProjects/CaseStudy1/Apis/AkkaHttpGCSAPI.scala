package GcsApi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.{Blob, BlobId, BlobInfo, Storage, StorageOptions}

import java.io.FileInputStream
import scala.concurrent.Future
import scala.jdk.CollectionConverters._
import scala.util.{Failure, Success}

object GCSApiApp extends App {
  implicit val system: ActorSystem = ActorSystem("GCSApiSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  // Configuration: Set your GCP key file and bucket details
  val keyFilePath = "/Users/srinija/gcp_key.json" // Replace with the actual path to your JSON key file
  val bucketName = "srinijakambhampaty" // Replace with your bucket name
  val basePath = "aggregated/json/year=2024/month=12/day=12/hour=17/"

  // Load GCP credentials and initialize the Storage client
  val storage: Storage = StorageOptions.newBuilder()
    .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(keyFilePath)))
    .build()
    .getService

  // Function to fetch the latest file from GCS
  def fetchLatestFileFromGCS: Future[String] = Future {
    val blobs = storage.list(bucketName, Storage.BlobListOption.prefix(basePath)).iterateAll().asScala

    if (blobs.isEmpty) {
      throw new Exception(s"No files found in directory: gs://$bucketName/$basePath")
    }

    val latestBlob = blobs
      .filter(_.getName.startsWith(basePath))
      .maxBy(_.getUpdateTime)

    new String(latestBlob.getContent(), "UTF-8")
  }

  // Function to fetch sensor-specific data from GCS
  def fetchSensorDataFromGCS(sensorId: String): Future[String] = Future {
    val sensorFilePath = s"$basePath$sensorId.json" // Assuming sensor-specific JSON file naming convention
    val blobId = BlobId.of(bucketName, sensorFilePath)
    val blob: Blob = storage.get(blobId)

    if (blob == null) {
      throw new Exception(s"File not found: gs://$bucketName/$sensorFilePath")
    }

    new String(blob.getContent(), "UTF-8")
  }

  // Define the Akka HTTP routes
  val route =
    pathPrefix("api" / "aggregated-data") {
      concat(
        // Route for /api/aggregated-data
        pathEndOrSingleSlash {
          get {
            onComplete(fetchLatestFileFromGCS) {
              case Success(jsonContent) =>
                complete(HttpEntity(ContentTypes.`application/json`, jsonContent))
              case Failure(exception) =>
                complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
            }
          }
        },
        // Route for /api/aggregated-data/:sensorId
        path(Segment) { sensorId =>
          get {
            onComplete(fetchSensorDataFromGCS(sensorId)) {
              case Success(jsonContent) =>
                complete(HttpEntity(ContentTypes.`application/json`, jsonContent))
              case Failure(exception) =>
                complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
            }
          }
        }
      )
    }

  // Start the Akka HTTP server
  val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

  println("Server online at http://localhost:8080/")
  bindingFuture.onComplete {
    case Success(binding) =>
      println(s"Server bound to ${binding.localAddress}")
    case Failure(exception) =>
      println(s"Failed to bind server: ${exception.getMessage}")
      system.terminate()
  }
}
