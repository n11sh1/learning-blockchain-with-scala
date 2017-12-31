package models

import play.api.libs.json.{Json, Reads, Writes}

case class Transaction(
  sender: String,
  recipient: String,
  amount: Int
)

case object Transaction {
  implicit val transactionWrite: Writes[Transaction] = Json.writes[Transaction]
  implicit val transactionReads: Reads[Transaction] = Json.reads[Transaction]
}