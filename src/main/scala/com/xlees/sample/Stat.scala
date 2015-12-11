package com.xlees.sample

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object Stat {
  
    def main(args: Array[String]) {
        
        val conf = new SparkConf().setAppName("BigData")
        val sc = new SparkContext(conf)
        
        val fpath = getClass.getResource("/data").getPath
        println("fpath= "+fpath)
        
        val data = sc.textFile(fpath, 100)
        data.cache()
        
        val cnt = data.count()
//        
        println("num of data: "+cnt)
        
        data.take(5).foreach(println)
        
        sc.stop()
    }
}