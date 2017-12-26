package model

case class Transaction(
  sender: String,
  recipient: String,
  amount: Int
)