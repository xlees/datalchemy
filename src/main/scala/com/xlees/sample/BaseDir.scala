package com.xlees.sample

import com.xlees.datautil.Utils
import scala.io.Source
import java.io.File

object BaseDir extends App {
    
    val url = getClass.getProtectionDomain().getCodeSource().getLocation()
    val file = new File(url.toURI())
    println("file="+file)
    println("parent file="+file.getParent)
    println("file??"+file.isFile())
    
    println("user.dir="+System.getProperty("user.dir"))
    println("user.home="+System.getProperty("user.home"))
    
    val root = Utils.getBaseDir
    println("root="+root)
    
    val root_getresource = getClass.getClassLoader.getResource("test.txt")
    println("getResource="+root_getresource.getPath+"\n")
    
//    Source.fromFile(root_getresource, "utf-8").take(5).foreach { println }
    Source.fromInputStream(root_getresource.openStream, "utf-8").take(10).foreach(println)
}