package utils

import play.api.libs.json.{JsObject, JsValue}

object JsonUtil {
  def sort(js: JsValue): JsValue = js match {
    case JsObject(fields) => JsObject(fields.toSeq.sortBy(_._1).map { case (k, v) => (k, sort(v)) })
    case _ => js
  }
}
