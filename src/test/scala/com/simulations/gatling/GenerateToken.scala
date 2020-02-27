package com.simulations.gatling

import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.security.{InvalidKeyException, NoSuchAlgorithmException}
import java.util.Base64


import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object GenerateToken {
  def main(args: Array[String]): Unit = {

    val c = GetSASToken("EDDV1EVENTHUBS01.servicebus.windows.net", "ingestion-producer-policy", "YnMYr+p/rDlGnOa6YyicDy4fBxLDLQjYWE5JXf/Nr2w=")
    println("Minimum Value = " + c)


  }
  def GetSASToken(resourceUri: String, keyName: String, key: String): String = {
    val epoch = System.currentTimeMillis / 1000L
    val week = 60 * 60 * 24 * 7
    val expiry = (epoch + week).toLong
    println("epoch " + epoch + " week " + week + " expiry " + expiry)

    val stringToSign = URLEncoder.encode(resourceUri, "UTF-8") + "\n" + expiry
    val signature = getHMAC256(key, stringToSign)
    var sasToken = "SharedAccessSignature sr=" + URLEncoder.encode(resourceUri, "UTF-8") + "&sig=" + URLEncoder.encode(signature, "UTF-8") + "&se=" + expiry + "&skn=" + keyName

    sasToken
  }

  def getHMAC256(key: String, input: String): String = {
    var sha256_HMAC = Mac.getInstance("HmacSHA256")
    val secret_key = new SecretKeySpec(key.getBytes, "HmacSHA256")
    sha256_HMAC.init(secret_key)
    val encoder = Base64.getEncoder
    var hash = new String(encoder.encode(sha256_HMAC.doFinal(input.getBytes("UTF-8"))))
    hash
  }
}
