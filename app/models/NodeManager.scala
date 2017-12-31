package models

import java.net.URL

import play.api.libs.json.{Json, Reads}

case class NodeManager(
  var nodes: Set[String]
) {
  def this() = this(Set())

  def add(newNode: Set[String]) = nodes = nodes ++ newNode

  def nodesCastURL(): Set[URL] = nodes.map(node => new URL(node))
}

case object NodeManager {
  implicit val nodeManagerRead: Reads[NodeManager] = Json.reads[NodeManager]
  def apply(): NodeManager = new NodeManager()
}