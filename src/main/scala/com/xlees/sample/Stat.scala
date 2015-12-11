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
        
        val cnt = data.count()
//        
        println("num of data: "+cnt)
        
        Thread.sleep(5)
        
//        data.take(5).foreach(println)
        
        sc.stop()
    }
}