package com.xlees.sample

import com.xlees.datautil.Oracle
import scala.util.Failure
import scala.util.Success
import scala.util.Try




/**
 * @author Xiang
 */
object Dao {
  
    def main(args: Array[String]) = {
        
        val rs = Oracle.query("select t.id,t.length,t.name from t_tfm_link t")
        rs.recover {
            case exc: Exception => 
                println(exc.getMessage)
                exc.printStackTrace
                
        }
        
        
        val a = 9
        
        val int = Try(Integer.parseInt("90")).map(_+8)
        println(int)
        
//        rs match {
//            case Success(result) =>
//                result.foreach(x => {
//                    x.foreach(y => println(y._2.getClass))
//                })
////                println(result.size)
//            case Failure(exc) =>
//                println(exc.getMessage)
//        }
    }
}