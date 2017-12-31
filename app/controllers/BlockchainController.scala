package controllers

import java.util.UUID
import javax.inject.{Inject, Singleton}

import models.{Blockchain, Transaction}
import play.api.libs.json._
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class BlockchainController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {
  val blockchain = Blockchain()
  val nodeIdentifier = UUID.randomUUID.toString.replace("-", "")

  def validateJson[A : Reads] = parse.json.validate(
    _.validate[A].asEither.left.map(e => BadRequest(JsError.toJson(e)))
  )

  def mine() = Action { implicit request: Request[AnyContent] =>
    val lastBlock = blockchain.lastBlock
    val lastProof = lastBlock.proof
    val proof = blockchain.proofOfWork(lastProof)

    blockchain.addNewTransaction(new Transaction("0", nodeIdentifier, 1))

    val block = blockchain.createNewBlock(proof)

    val response = Json.obj(
      "message" -> "New Block Forged",
      "index" -> block.index,
      "transactions" -> Json.toJson(block.transactions),
      "proof" -> block.proof,
      "previous_hash" -> block.previousHash
    )

    Ok(response)
  }

  def newTransaction() = Action(validateJson[Transaction]) { implicit request =>
    val index = blockchain.addNewTransaction(request.body)

    val response = Json.obj(
      "message" -> f"Transaction will be added to Block $index"
    )
    Ok(response)
  }

  def fullChain() = Action { implicit  request: Request[AnyContent] =>
    val response = Json.obj(
      "chain" -> Json.toJson(blockchain.chain),
      "length" -> blockchain.chain.length
    )
    Ok(response)
  }
}
