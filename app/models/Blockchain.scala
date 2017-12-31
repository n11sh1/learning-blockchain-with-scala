package models

import com.softwaremill.sttp._
import models.api.ChainApi
import play.api.libs.json.Json
import utils.{HashUtil, JsonUtil}

case class Blockchain(
  var chain: Seq[Block],
  var currentTransactions: Seq[Transaction],
  var nodeManager: NodeManager
) {

  def this() = {
    this(Seq(), Seq(), NodeManager())
    createNewBlock(100, Option("1"))
  }

  def createNewBlock(proof: Int): Block = createNewBlock(proof, None)

  private def createNewBlock(proof: Int, previousHash: Option[String]): Block = {
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

  def addNewNode(hostAddresses: Set[String]) = nodeManager.add(hostAddresses)

  def validChain(chain: Seq[Block]): Boolean = {
    var lastBlock = chain.head
    var currentIndex = 1

    while (currentIndex < chain.length) {
      val block = chain(currentIndex)

      if (!block.previousHash.equals(hash(lastBlock))) {
        return false
      }

      if (!validProof(lastBlock.proof, block.proof)) {
        return false
      }

      lastBlock = block
      currentIndex += 1
    }

    return true
  }

  def resolveConflicts(): Boolean = {
    val neighbours = nodeManager.nodesCastURL()
    var newChain: Option[Seq[Block]] = None

    var maxLength = chain.length

    neighbours.foreach(node => {
      try {
        val request = sttp.get(uri"http://${node.getAuthority}/chain")
        implicit val backend = HttpURLConnectionBackend()
        val response = request.send()

        if (response.code == 200) {
          val chainApi = Json.parse(response.unsafeBody).validate[ChainApi]

          chainApi.map(chainApi =>
            if (chainApi.length > maxLength && validChain(chainApi.chain)) {
              maxLength = chainApi.length
              newChain = Option(chainApi.chain)
            }
          )
        }
      } catch {
        case e: Exception =>
      }
    })

    if (newChain.nonEmpty) {
      chain = newChain.get
      return true
    }
    false
  }
}

case object Blockchain {
  def apply(): Blockchain = new Blockchain()
}