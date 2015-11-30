package com.xlees.sample

import scala.util.Failure
import scala.util.Success
import scala.util.Try

/**
 * @author Xiang
 */

class MyException extends Exception
class HandleException extends Exception

object TryRecover {
    
    def calc = {
        val traveltime = 9/0
    }
    
    def foo(x: Int) = {
        if (x >= 0)
            1
        else
            -1
    }
  
    def main(args: Array[String]) = {
        
        val c = Try(calc).recover {
            case e:Exception => println("fuck:\n"+e)
        }
        
        println(foo(89))
        
        println(c.get.getClass())
        
//        val t = Failure(new MyException)
//        val bt = t.transform(s => {
//            println("success"+s)
//        }, e => {
//            println("failure"+e)
//        })
    }
}