package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import model.{Blockchain, Transaction}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

@Singleton
class BlockchainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val blockchain = Blockchain()
  val uuid = UUID.randomUUID.toString.replace("-", "")

  def mine() = Action { implicit request: Request[AnyContent] =>
    val lastBlock = blockchain.lastBlock
    val lastProof = lastBlock.proof
    val proof = blockchain.proofOfWork(lastProof)

    blockchain.addNewTransaction(new Transaction("0", uuid, 1))

    val previousHash = blockchain.sha256Hash(lastBlock.toString)
    val block = blockchain.createNewBlock(proof, Option(previousHash))

    val response = Json.obj(
      "message" -> "New Block Forged",
      "index" -> block.index,
      "transactions" -> Json.toJson(block.transactions),
      "proof" -> block.proof,
      "previous_hash" -> block.previousHash
    )

    Ok(response)
  }

  def newTransaction() = Action { implicit request: Request[AnyContent] =>
    Ok
  }

  def fullChain() = Action { implicit  request: Request[AnyContent] =>
    val response = Json.obj(
      "chain" -> Json.toJson(blockchain.chain),
      "length" -> blockchain.chain.length
    )
    Ok(response)
  }
}
