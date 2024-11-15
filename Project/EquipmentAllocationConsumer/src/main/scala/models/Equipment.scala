package models

import play.api.libs.json._

case class Equipment(
                      id: Long,
                      productId: Long,
                      conditionStatus: String, // Possible values: "maintenance", "good", "removed"
                      availableStatus: Boolean // True if available, False if not
                    )

object Equipment {
  implicit val equipmentFormat: Format[Equipment] = Json.format[Equipment]
}
