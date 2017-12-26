package model

case class Blockchain(
  chain: Seq[Block],
  var current_transactions: Seq[Transaction]
) {
  def createNewBlock(proof: Int, previousHash: Option[String]): Block = {
    val block = new Block(
                      chain.length + 1,
                      System.currentTimeMillis(),
                      current_transactions, proof,
                      previousHash.getOrElse(calculateHash(getLastBlock))
    )

    current_transactions = Seq()
    chain :+ block
    block
  }

  def addNewTransaction(transaction: Transaction): Long = {
    current_transactions :+ transaction
    getLastBlock.index + 1
  }

  def calculateHash(block: Block): String = {
    val blockStr =
  }

  def getLastBlock: Block = chain.last
}