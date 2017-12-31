package utils

import java.math.BigInteger
import java.security.MessageDigest

object HashUtil {
  def sha256(text: String): String = String.format("%064x", new BigInteger(1, MessageDigest.getInstance("SHA-256").digest(text.getBytes("UTF-8"))))
}
