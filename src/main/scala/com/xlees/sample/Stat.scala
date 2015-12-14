package com.xlees.sample

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object Stat {
  
    def main(args: Array[String]) {
        
        val conf = new SparkConf().setAppName("BigData")
        val sc = new SparkContext(conf)
        
        val path = getClass.getProtectionDomain().getCodeSource().getLocation().getPath()
        println("path="+path)
        
        val root = getClass.getClassLoader.getResource(".").getPath
        println("root="+root+"\n")
        
        val fpath = getClass.getClassLoader.getResource("data").getPath
        println("fpath= "+fpath)
        
        val data = sc.textFile(fpath, 100)
        data.cache()
        
        data.map(x => {
        	val line = x.split(",")
        	val key = (line(0),line(1))
        	val value = (line(3), line(5).toInt,line(6).toInt)
        	
        	(key, value)
        }).aggregateByKey((0,0), 1)((x:(Int,Int),value:(String,Int,Int))=> {
        	if (value._1 == "1")
        		(x._1, x._2+value._3)
        	else if (value._1 == "2")
        		(x._1+value._3, x._2)
        	else
        		(x._1, x._2)
        }, (x:(Int,Int), y:(Int,Int)) => {
        	(x._1+y._1, x._2+y._2)
        }).map(x => {
        	val net = x._2._1-x._2._2
        	val value = (x._2._1,x._2._2,net)
        	(x._1, value)
        })
          .sortBy(x=>x._2._3, false)
          .saveAsTextFile("result")
        
//        val cnt = data.count()
//        
//        println("num of data: "+cnt)
        
        Thread.sleep(5)
        
//        data.take(5).foreach(println)
        
        sc.stop()
    }
}