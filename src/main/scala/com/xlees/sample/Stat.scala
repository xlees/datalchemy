package com.xlees.sample

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

object Stat {
  
    def main(args: Array[String]) {
        
        val conf = new SparkConf().setAppName("stock")
        val sc = new SparkContext(conf)
        
        val fpath = getClass.getClassLoader.getResource("data/000524.csv").getPath
        println("fpath= "+fpath)
        
        val data = sc.textFile("000524", 100)
//        val cnt = data.count()
//        
//        println("num of data: "+cnt)
        
        sc.stop()
    }
}