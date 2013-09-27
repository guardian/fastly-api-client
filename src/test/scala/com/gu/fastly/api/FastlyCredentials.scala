package com.gu.fastly.api

import java.io.File
import scala.io.Source

trait FastlyCredentials {

  final var apiKey: String = _
  final var serviceId: String = _

  val credentials = {
    val file = Source.fromFile(
      new File(
        System.getProperty("user.home") + "/.fastlyapiclientcconfigbeta"),
        "utf-8")

    file.getLines.foreach (
      line => {
        val propertyRegex = """^(\S+)=(\S+)$""".r
        line match {
          case propertyRegex(key, value) => {
            if (key.trim.equals("apiKey")) apiKey = value.trim
            if (key.trim.equals("serviceId")) serviceId = value.trim
          }
          case _ =>
        }
      })

    if(apiKey == null || serviceId == null) throw new Exception("error parsing ~/.fastlyapiclientcconfig, apiKey=%s serviceId=%s".format(apiKey, serviceId))
  }

}
