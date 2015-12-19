package com.xlees.strat

import scala.collection.mutable.ListBuffer
import scala.io.Source
import org.apache.spark.Partitioner
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import org.apache.spark.rdd.RDD.rddToPairRDDFunctions
import org.joda.time.DateTime
import scala.collection.mutable.ArrayBuffer


case class Row(date: String, code: String, time:String,
				dtype: Int, price: Double, volume: Int, amount: Long)

// 需要确定key(date,code,time)
class MyPartitioner extends Partitioner {
	
//	val fstream = getClass.getClassLoader.getResourceAsStream("codes.csv")	// 不好序列化
	val fpath = getClass.getClassLoader.getResource("codes.csv").getPath
	val codes = Source.fromFile(fpath,"utf-8").getLines.map(x => { x.split(",")(0) }).toArray

	override def getPartition(key: Any): Int = {
		val _key = key.asInstanceOf[(String,String,String)]
		codes.indexOf(_key._2)
	}
	
	override def numPartitions: Int = codes.size
}


object Stat {
	
	val foo = (x: Int) => {
		x+1
	}
	
	def calcTimeSpan(tseries: Array[Row]): (String,Double) = {
	    val open_time = "09:30:00"
	    
	    val _tseries = tseries.sortBy(_.time)
	    
		val index = locPeek(_tseries.map(_.price))
		if (index < 0)
			("15:00:00",1440.0)
		else if (index == 0)
		    (open_time, 0.0)
		else {
			
			val row = _tseries(index)
//			println("row="+row)
			
			val datetime = new DateTime(row.date+"T"+row.time).getMillis
			val base = new DateTime(row.date+"T"+open_time).getMillis
			
			val time = (datetime-base) / 60000.0
			if (time>120.0) ("11:30:00",120.0) else (row.time,time)
		}
	}
	
	def testCalc() = {
	    val code = "002468"
	    val fname = getClass.getClassLoader.getResource(s"sdata/$code.csv").getPath
	    val dataSet = Source.fromFile(fname).getLines()
	                        .map(x => {
	                            val arr = x.split(",")
  						 	  
      						 	  Row(arr(0), arr(1), arr(2),
      						 	 	  arr(3).toInt, arr(4).toDouble,
      						 	 	  arr(5).toInt, arr(6).toLong)
	                        })
	                        .filter(_.date=="2015-12-17")
	                        .toArray
	                        
        val result = calcTimeSpan(dataSet)
        println(code+": "+result)
	}
	
	def locPeek(tick: Array[Double]): Int = {
		// test
//	    tick.take(20).foreach(println)
	    
	    val trade = tick.last
	    
//		val diff = tick.sliding(2)
//						.map(x => Math.abs(x(1)-x(0)))
//						.toList
	    val diff = tick.map(x => Math.abs(x-trade)).zipWithIndex
		
		val index = for {
		    e <- diff
		    if (e._1 < 0.001)
		} yield {
		    e._2
		}
		
		var beg = 0
		var cur = index(0)-1
		val top = new ListBuffer[(Int,Int)]()
		for (x <- index.zipWithIndex) {
		    if ((cur+1) == x._1)
		        cur += 1
		    else {
		        if (x._2 > (beg+1))
		            top += ((index(beg), index(x._2-1)))
		        beg = x._2
		        cur = x._1
		    }
		}
		top += ((index(beg),cur))
		
//		println("arr size: "+top.size)
		
		if (top.size > 0)
			top.toList.map(x=>(x._2-x._1, x._1)).max._2
		else
			-1
	}
	
	def peek(sc: SparkContext) = {
		
		val fpath = getClass.getClassLoader.getResource("sdata").getPath
		val data = sc.textFile(fpath,4)
		
//		val file2 = Thread.currentThread().getContextClassLoader.getResourceAsStream("sdata")
  		val dframe = data.map( x => {
  						 	  val arr = x.split(",")
  						 	  
  						 	  Row(arr(0), arr(1), arr(2),
  						 	 	  arr(3).toInt, arr(4).toDouble,
  						 	 	  arr(5).toInt, arr(6).toLong)
  						  })
  		
  		
		val part = new MyPartitioner()
//		val bcast = sc.broadcast(part)
		
		println("part: "+part.numPartitions)
		
//		val rdd = dframe.map(x => {
//			val key = (x.date,x.code,x.time)
//			val value = (x.dtype,x.price,x.volume,x.amount)
//			
//			(key,value)
//		})
//		// 去掉空分区
//			.partitionBy(part)
//			.glom()
//			.filter(_.size>0)
		
		val rdd = dframe.coalesce(10).map(x => {
			val key = (x.date,x.code)
			(key,x)
		}).aggregateByKey(new ArrayBuffer[Row]())(_ += _, _ ++ _)
		.map(x => {
		    (x._1, calcTimeSpan(x._2.toArray))
		})
		.sortBy(x => x._1._1)
		rdd.cache()
		
//		rdd.collect().foreach(println)
		rdd.map(x => {
		  (x._1._1, (x._1._2,x._2._1,x._2._2))
		})
		    .aggregateByKey(new ListBuffer[(String,String,Double)]())(_+=_,_++_)
		    .map(x=> { (x._1, x._2.filter(_._3<120.0).toList) })
		    .sortBy(_._1, true, 1)
		    .saveAsTextFile("result")
		
		// do something
//		val result = rdd.map(x => {
//				
//			val arr = x.map(e => {
//				Row(e._1._1, e._1._2, e._1._3,
//					e._2._1, e._2._2, e._2._3, e._2._4)
//			})
//			
//			((x(0)._1._2,x(0)._1._1), calcTimeSpan(arr))
//		})
//		
//		val r = result.collect()
//		println("size of result: "+r.size)
//		r.foreach(println)
		
//		println("num of partitions: "+rdd.partitions.size)
//		println(rdd.partitioner.get.getClass)
//		
//		Thread.sleep(5000)
//
//		val counter = sc.accumulator(0)
//  		
//  		// job 2
//  		rdd.foreachPartition(x => {
////  			val arr = x.toArray
////  			if (arr.size > 0)
////  				println(x.size)
//			
////  			val writer = new PrintWriter("result/"+x.size+".txt", "utf-8")
////  			writer.append("=="+x.size+"==")
////  			
////  			writer.close()
//  			
//  			if (x.size > 0)
//  				counter += 1
//  		})
//  		
//  		println("non empty: "+counter.value)
//  		Thread.sleep(2000)
//  		
//  		val rdd1 = rdd.repartition(counter.value)  //rdd.coalesce(counter.value, true)		//
//  		
//  		println("print elements of each partition:\n")
//  		Thread.sleep(5000)
//  		
//  		val res = rdd1.mapPartitions(x => {
//  			val size = x.size
//  			List(size).toIterator
//  		}).collect
//  		
//  		println("size: "+rdd1.count())
//		res.foreach(x=>println(x))
	}
  
    def main(args: Array[String]) {
        
        val conf = new SparkConf().setAppName("BusinessAnalysis")
        val sc = new SparkContext(conf)
        
        val path = getClass.getProtectionDomain().getCodeSource().getLocation().getPath()
        println("path="+path)
        
        val root = getClass.getClassLoader.getResource(".").getPath
        println("root="+root+"\n")
        
        val fpath = getClass.getClassLoader.getResource("sdata").getPath
        println("fpath= "+fpath)
        
        peek(sc)
//        testCalc
        
//        val data = sc.textFile(fpath, 100)
//        data.cache()
//        
//        data.map(x => {
//        	val line = x.split(",")
//        	val key = (line(0),line(1))
//        	val value = (line(3), line(5).toInt,line(6).toInt)
//        	
//        	(key, value)
//        }).aggregateByKey((0,0), 1)((x:(Int,Int),value:(String,Int,Int))=> {
//        	if (value._1 == "1")
//        		(x._1, x._2+value._3)
//        	else if (value._1 == "2")
//        		(x._1+value._3, x._2)
//        	else
//        		(x._1, x._2)
//        }, (x:(Int,Int), y:(Int,Int)) => {
//        	(x._1+y._1, x._2+y._2)
//        }).map(x => {
//        	val net = x._2._1-x._2._2
//        	val value = (x._2._1,x._2._2,net)
//        	(x._1, value)
//        })
//          .sortBy(x=>x._2._3, false)
//          .saveAsTextFile("result")
        
//        val cnt = data.count()
//        
//        println("num of data: "+cnt)
        
        Thread.sleep(5)
        
        sc.stop()
    }
}