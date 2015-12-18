package com.xlees.sample

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

/**
 * @author xiang
 */
object JodaSample extends App {
  
	val dt = new DateTime(2015,12,12,12,38,0)
	println(dt.toString("yyyy-mm-dd HH:mm:ss"))
	
	val dt1 = new DateTime()
	dt1.plusDays(5)
	println(dt1)
	println(dt1.plusDays(6).getDayOfWeek)
	
	println("millis: "+dt.getMillis)
	
	val format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");  
	val time = DateTime.parse("2012-12-21 23:22:45", format)
	println(time.toString("yyyy-MM-dd HH:mm:ss"))
	
	val dt4 = new DateTime("2012-05-20T20:09:10+00:00")
	println(dt4.toString("yyyy-MM-dd HH:mm:ss"))
}