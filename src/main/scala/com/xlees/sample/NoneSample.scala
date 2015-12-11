package com.xlees.sample

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._
import java.util.Date

/**
 * @author Xiang
 */

case class Vehicle1(numb: String, color: Option[String], ptype: Option[String])

//class BoxSerializer extends CustomSerializer[Vehicle1](format => ({
//	
//		case jv: JValue => 
//			val numb = (jv \ "numb").extract[String] 
//			val color = (jv \ "color").extract[Option[String]]
//			val ptype = (jv \ "ptype").extract[Option[String]]
//			
//			Vehicle1(numb,color,ptype)
//	}, 
//	{ 
//		case b: Vehicle1 => 
//			("numb" -> b.numb) ~
//			("color" -> b.color) ~
//			("ptype" -> b.ptype)
//	}))


object NoneSample extends App {

    implicit val f = DefaultFormats.preservingEmptyValues //+ new BoxSerializer
    
    case class JsonTest(x: String, y: Option[String], z: Option[Int], a: Option[Double], b: Option[Boolean], c: Option[Date], d: Option[Any])
    
    val v = JsonTest("test", None, None, None, None, None, None)
    println(v.z.getClass)
    
    val test = Map("name"->"lixiaia", "age"->90, "addr"->None)
    val testJson = Extraction.decompose(test)
    println("test:\n"+write(testJson))
    
    val addr = testJson\\"addr"
    val someAddr = addr.toOption
    println(addr)
    
    // prints {"x":"test","y":null,"z":null,"a":null,"b":null,"c":null,"d":null}
    
    val str = write(testJson)
    println(write(testJson))
    
    val json = parse(str)
    println(json)
    println((json\\"a"))
    
    val json1 = ("name"->"lixiaia") ~ ("age"->(None:Option[Int]))
    val res = pretty(render(json1))
    println(write(json1))
    
    val a = None: Option[String]
    println(a.getClass)
    
    val b = Vehicle1("lkdfoe",None,None)
    val b1 = Extraction.decompose(b)
    println(write(b1))
    
    val pb = b1.extract[Vehicle1]
    println(pb)
    
    case class Person(name:String, age:Option[Int], weight:Option[Double]) {
    	
    	def toJValue = {
    		("name"->name) ~ ("age"->age) ~ ("weight"->weight)
    	}
    }
    
    val lix = Person("lix",None,None)
//    val json2 = ("name"->"lix") ~ ("age"->(None:Option[Int]))
    val json2 = render(lix.toJValue)	//Extraction.decompose(lix)		//lix.toJValue 
    println(compact(json2))

//    println((json2.asInstanceOf[JObject].extract[Person]))
    
    val tgs = ("89"->89) ~ ("flow"->89304) ~ ("tindex"->0.8945) ~ ("ttime"->(None:Option[Double]))
    val result = ("tstamp"->System.currentTimeMillis()) ~ ("data"->List(tgs)) ~ ("msg"->(None:Option[String]))
    val jvalue = render(result)
    println(pretty(jvalue))
    
    val m = ("name"->"abc")
    val bm = render(m)
    println(bm)
    
    val set = Map("abc"->90, "ldf"->90, "lix"->12)
    val aset = render(set)
    println("aset="+aset)
    
    val vset = ("name"->"faefef") ~ ("vset"->set)
    val r_vset = render(vset)
    val abc = vset\\"vset"
    
    println(pretty(r_vset))
    println("abc="+abc.extract[Map[String,Int]])
    
    val myset = Set("a","b","c","d","e")
    val r_myset = render(("name"->"lix") ~ ("age"->myset))
    println(r_myset)
    val ex= (r_myset\\"age").extract[Set[String]]
    println(ex)
}










