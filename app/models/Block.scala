package models

import play.api.libs.json.{Json, Reads, Writes}

case class Block(
  index: Long,
  timestamp: Long,
  transactions: Seq[Transaction],
  proof: Int,
  previousHash: String
)

case object Block {
  implicit val blockWrite: Writes[Block] = Json.writes[Block]
  implicit val blockRead: Reads[Block] = Json.reads[Block]
}