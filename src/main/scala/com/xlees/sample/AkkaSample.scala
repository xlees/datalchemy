package com.xlees.sample

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Failure
import akka.actor.Actor
import akka.actor.ActorLogging
import akka.actor.ActorSystem
import akka.actor.Props
import akka.actor.actorRef2Scala
import akka.pattern.pipe
import com.typesafe.config.ConfigFactory
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext

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
            
        case value: String => 
            println("string: "+value)
            val map = scala.collection.mutable.Map[Short,Integer]()
            map(78) = 1
//            println(map(90))
        
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
        
        val conf = new SparkConf().setAppName("app")
        val sc = new SparkContext(conf)

//        val conf = ConfigFactory.parseResources("/dev.conf")
        val system = ActorSystem("MySystem")
        println("config version: "+system.settings.ConfigVersion)
//        val greeter = system.actorOf(Props[GreetingActor], name = "greeter")
        val greeter = system.actorOf(Props[MyActor], name = "my-actor")
        
//        greeter ! Greeting("Charlie Parker")
        greeter ! "start"
//        greeter ! "calculate"
        
//        system.shutdown()
        
        sc.stop()
    }
}