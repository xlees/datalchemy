package com.xlees.sample

/**
 * @author Xiang
 */

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.native.JsonMethods._
import org.json4s.Xml.{toJson, toXml}
import java.util.Date
import org.json4s.native.Serialization._

case class Project(name: String, startDate: Date, lang: Option[Language], teams: List[Team])
case class Language(name: String, version: Double)
case class Team(role: String, members: List[Employee])
case class Employee(name: String, experience: Double)
  

object JsonProcess {

    implicit val formats = DefaultFormats
    case class Mailserver(url: String, username: String, password: String)

    val json = parse(
        """
        { 
          "url": "imap.yahoo.com",
          "username": "myusername",
          "password": "mypassword",
        }
        """)
        
    val personDSL =
        ("person" ->
          ("name" -> "Joe") ~
          ("age" -> 35) ~
          ("spouse" ->
            ("person" ->
              ("name" -> "Marilyn") ~
              ("age" -> 33)
            )
          )
        )

    def main(args: Array[String]) {
        
        val l2 = List[Option[Int]](Some(45), None, None,Some(90))
        println(Extraction.decompose(l2))
        
        val tmp1 = Map("flow"->Option(None), "tindex"->Some(1.2))
        println(write(Extraction.decompose(tmp1)))
        
        
        val tmp = ("name"->"llll") ~ ("addr"->Set(3,4,5)) ~ ("flow"->(None:Option[Int]))
        val addr = (tmp \\ "addr")
        println(addr.extract[Seq[Int]])
        println((tmp\\"name").extract[String])
        println((tmp\\"flow").extract[Option[Int]])
        println(write(tmp))
        println("abc=>"+tmp\\"abc")
        
        
        
        val e1 = Employee("lixiang",90.0)
        val e2 = Employee("lixiang",90.0)
        println(e1.eq(e2))
        
        val m = Map(e1->90,e2->80)
        println(m)
        
        print(json.getClass)
        val server = json.extract[Mailserver]
        println(server.getClass)
        println(pretty(render(json)))
        
        println("if jvalue: "+json.isInstanceOf[JObject])
        println(write(json))
        println(json.getClass)
        
        val url = json findField {
            case JField("url",_) => true
            case _ => false
        }
        println(url)
        
        val l = 1 to 20
        val js = render(l)
        println(pretty(js))
        
        val map = Map("001"->(1 until 12), "002"->90, "003"->67)
//        println(compact(render(map)))
        val ser = write(map)    // to string
//        println(ser)
//        println(parse(ser))
        
        // json1 JObject
        var json1 = ("name","luca") ~ 
                    ("id","1q2w3e4r5t") ~ 
                    ("age",26) ~ 
                    ("url","http://www.nosqlnocry.wordpress.com") ~
                    ("addr", Map("1"->34,"2"->90))
        println("json1 type: "+json1.getClass)
        val age = json1 \\ "addr"
        println(age.values.asInstanceOf[Map[String,Int]])
        
        val map1 = Set(9,3,4,5,1)
        val js1 = Extraction.decompose(("name"->map1))
//        val j = ("map", map1) ~ ("name", "lll")
        println(js1 \ "name")
//        val obj = read(ser)
//        println(obj)
        
        val dset = List(Map("name"->"lx","age"->20), Map("name"->"lx2","age"->23))
        val js2 = Extraction.decompose(dset)
        println(js2.children(0) \ "name")
        
        val map2 = Map("90"->9, "23"->4, "78"->34)
        val r2 = map2.map(x => { Map(x._1->x._2) })
        r2.foreach(println)
        
//        val obj = new JObject()
        
//        val m = json.extract[Mailserver]
//        println(m.url)
//        println(m.username)
//        println(m.password)
    }

}