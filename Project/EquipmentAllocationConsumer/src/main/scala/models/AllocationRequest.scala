package models

import java.time.LocalDate
import play.api.libs.json._

case class AllocationRequest(
                              id: Long,
                              userId: Long,
                              equipmentId: Long,
                              purpose: String,
                              requestDate: LocalDate,
                              expectedReturnDate: LocalDate,
                              returnStatus: String, // "pending", "returned", "overdue"
                              returnDate: Option[LocalDate] = None, // Updated upon return
                              returnCondition: Option[String] = None // "good", "maintenance"
                            )

object AllocationRequest {
  implicit val allocationRequestFormat: Format[AllocationRequest] = Json.format[AllocationRequest]
}
