package GcsApi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import com.google.auth.oauth2.ServiceAccountCredentials
import com.google.cloud.storage.{Blob, BlobId, Storage, StorageOptions}

import java.io.FileInputStream
import scala.concurrent.Future
import scala.util.{Failure, Success}

object GCSApiApp extends App {
  implicit val system: ActorSystem = ActorSystem("GCSApiSystem")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  // Configuration: Set your GCP key file and bucket details
  val keyFilePath = "/Users/srinija/gcp_key.json" // Replace with the actual path to your JSON key file
  val bucketName = "srinijakambhampaty" // Replace with your bucket name
  val basePath = "aggregated/json/year=2024/month=12/day=12/hour=17/"
  val fileName = "part-00000-ebe65154-17fb-4060-9583-b03c3fffbdb9.c000.json" // Assuming a fixed file name.

  // Load GCP credentials and initialize the Storage client
  val storage: Storage = StorageOptions.newBuilder()
    .setCredentials(ServiceAccountCredentials.fromStream(new FileInputStream(keyFilePath)))
    .build()
    .getService

  // Function to fetch the file from GCS
  def fetchFileFromGCS: Future[String] = Future {
    val blobId = BlobId.of(bucketName, s"$basePath$fileName")
    val blob: Blob = storage.get(blobId)

    if (blob == null) {
      throw new Exception(s"File not found: gs://$bucketName/$basePath$fileName")
    }

    new String(blob.getContent(), "UTF-8")
  }

  // Define the Akka HTTP route
  val route =
    path("api" / "aggregated-data") {
      get {
        onComplete(fetchFileFromGCS) {
          case Success(jsonContent) =>
            complete(HttpEntity(ContentTypes.`application/json`, jsonContent))
          case Failure(exception) =>
            complete(HttpResponse(StatusCodes.InternalServerError, entity = s"Error: ${exception.getMessage}"))
        }
      }
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





