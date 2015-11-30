package com.xlees.sample

/**
 * @author xiang
 */

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import scala.concurrent._
import scala.concurrent.ExecutionContext.Implicits.global
import akka.pattern.{pipe,ask}
import scala.util.Try
import scala.util.Failure

case class StatResult(r: Int)

case class MessageException( 
  akkaMessage: Any, 
  originalException: Throwable 
) extends RuntimeException("Exception due to message")

class MyActor extends Actor {
    
    def calc() = Future {
        println("calculating in '"+getClass.toString+"' ...")
        Thread.sleep(5000)
        
        val map = Map("flow"->10, "veh"->scala.collection.mutable.Set("a","b","c"))
        val flow = map("flow").asInstanceOf[Int]        // exception happen
        
//        throw new Exception("fuck!")
        val balance = 100 / 0
        
        StatResult(98)
    }
    
    def receive = {
        case Failure(exc) =>
//            println("exception happend: " + exc.getMessage)
            exc.printStackTrace
            Thread.sleep(50)
            self ! "i am ok now."
            
        case StatResult(res) =>
            println("result = "+res)
            
        case msg: Exception =>
            println("exceitpn received.")
            
//        case Try[StatResult(res)] =>
//            println("exception: "+res)
            
        case "calculate" =>
            calc().recover { 
                case exc => Failure(exc)
                
            }.pipeTo(self)
            println("task submmited.")
            
//        case msg @ _ =>
//            println("error may happen??"+msg.getClass.toString+":\n"+msg)
            
        case value: String => println(value)
        
        case _ => println("received unknown message")
    }
}

case class Greeting(who: String)

class GreetingActor extends Actor with ActorLogging {
    
    def receive = {
        case Greeting(who) => log.info("Hello " + who)
        case _ => 
            println("Unknown message.")
    }
}

object AkkaSample {

    def sayHello = {
        println("hello!")
    }

    def main(args: Array[String]) = {
        println("akka, actor.")
        sayHello

        val system = ActorSystem("MySystem")
//        val greeter = system.actorOf(Props[GreetingActor], name = "greeter")
        val greeter = system.actorOf(Props[MyActor], name = "my-actor")
        
//        greeter ! Greeting("Charlie Parker")
        greeter ! "start"
        greeter ! "calculate"
        
//        system.shutdown()
    }
}