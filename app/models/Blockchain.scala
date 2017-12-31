package models

import play.api.libs.json.Json
import utils.{HashUtil, JsonUtil}

case class Blockchain(
  var chain: Seq[Block],
  var currentTransactions: Seq[Transaction]
) {

  def this() = {
    this(Seq(), Seq())
    createNewBlock(100, Option("1"))
  }

  def createNewBlock(proof: Int): Block = createNewBlock(proof, Option.empty)

  def createNewBlock(proof: Int, previousHash: Option[String]): Block = {
    val block = new Block(
                      chain.length + 1,
                      System.currentTimeMillis(),
                      currentTransactions,
                      proof,
                      previousHash.getOrElse(hash(lastBlock))
    )

    currentTransactions = Seq()
    chain = chain :+ block
    block
  }

  def addNewTransaction(transaction: Transaction): Long = {
    currentTransactions = currentTransactions :+ transaction
    lastBlock.index + 1
  }

  def hash(block: Block): String = HashUtil.sha256(JsonUtil.sort(Json.toJson(block)).toString())

  def lastBlock: Block = chain.last

  def proofOfWork(lastProof: Int): Int = {
    var proof = 0

    while (!validProof(lastProof, proof)) {
      proof += 1
    }

    proof
  }

  def validProof(lastProof: Int, proof: Int): Boolean = {
    val guess = lastProof.toString +  proof.toString
    val guessHash = HashUtil.sha256(guess)
    guessHash.startsWith("0000")
  }
}

case object Blockchain {
  def apply(): Blockchain = {
    new Blockchain()
  }
}