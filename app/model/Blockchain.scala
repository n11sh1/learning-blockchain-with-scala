package model

import java.math.BigInteger
import java.security.MessageDigest

case class Blockchain(
  chain: Seq[Block],
  var currentTransactions: Seq[Transaction]
) {
  def createNewBlock(proof: Int, previousHash: Option[String]): Block = {
    val block = new Block(
                      chain.length + 1,
                      System.currentTimeMillis(),
                      currentTransactions, proof,
                      previousHash.getOrElse(sha256Hash(lastBlock.toString))
    )

    currentTransactions = Seq()
    chain :+ block
    block
  }

  def addNewTransaction(transaction: Transaction): Long = {
    currentTransactions :+ transaction
    lastBlock.index + 1
  }

  def sha256Hash(text: String): String = String.format("%064x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(text.getBytes("UTF-8"))))

  def lastBlock: Block = chain.last

  def proofOfWork(lastProof: Int): Int = {
    var proof = 0

    while (validProof(lastProof, proof)) {
      proof += 1
    }

    proof
  }

  def validProof(lastProof: Int, proof: Int): Boolean = {
    val guess = lastProof * proof
    val guessHash = sha256Hash(guess.toString)
    guessHash.endsWith("0000")
  }
}

object Blockchain {
  def apply(): Blockchain = {
    new Blockchain(Seq(), Seq())
  }
}