package model

case class Block(
  index: Long,
  timestamp: Long,
  transactions: Seq[Transaction],
  proof: Int,
  previousHash: String
)
