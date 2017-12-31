package models.api

import models.Block
import play.api.libs.json.{Json, Reads, Writes}

case class ChainApi(
  chain: Seq[Block],
  length: Int
)

case object ChainApi {
  implicit val chainApiWrite: Writes[ChainApi] = Json.writes[ChainApi]
  implicit val chainApiReads: Reads[ChainApi] = Json.reads[ChainApi]
}