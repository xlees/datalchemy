package com.xlees.sample

import scala.concurrent.duration._
import org.joda.time.DateTime

/**
 * @author "xiang"
 */

object DelaySample {
	private val calcInterval = 5 minutes
  
	def calcDelay = {
		val curtime = System.currentTimeMillis
		val interval = calcInterval.toMillis
		
		println(curtime)
		
		(interval - curtime % interval).millis
	}
	
	def main(args: Array[String]) = {
		
		val time = new DateTime(1495782331000L)
		println(time.toString("yyyy-MM-dd HH:mm:ss"))
		
		val curtime = new DateTime(System.currentTimeMillis())
		println("  delayed time: "+calcDelay+" millis.")
	}
}