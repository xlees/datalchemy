package com.xlees.sample

import org.json4s._
import org.json4s.JsonDSL._
import org.json4s.jackson.JsonMethods._
import org.json4s.jackson.Serialization._
import java.util.Date

case class MyClass(id: Int) {
    lazy val blah = "lazy string"
}

case class Vehicle(plate: String, color: String)

//class StringTupleSerializer extends CustomSerializer[(String, String)](format => ( {
//  case JObject(List(JField(k, JString(v)))) => (k, v)
//}, {
//  case (s: String, t: String) => (s -> t)
//}))

//class MyClassSerializer extends Serializer[MyClass] {
//    
//  private val MyClassClass = classOf[MyClass]
//
//  def deserialize(implicit format: Formats): PartialFunction[(TypeInfo, JValue), MyClass] = {
//    case (TypeInfo(MyClassClass, _), json) => json match {
//      case JObject(JField("id", JInt(id)) :: _) =>
//        MyClass(id)
//      case x => throw new MappingException("Can't convert " + x + " to MyClass")
//    }
//  }
//
//  def serialize(implicit formats: Formats): PartialFunction[Any, JValue] = {
//    case x: MyClass => 
//      import JsonDSL._
//      ("id" -> x.id) ~ ("blah" -> x.blah)
//  }
//}

object MySerializer {
    implicit val formats = DefaultFormats.preservingEmptyValues
    
    val jsonStr = """
     {
      "data1": {
        "field1": "data1",
        "field2": 1.0,
        "field3": true
      },
      "data211": {
        "field1": "data211",
        "field2": 4343.0,
        "field3": false
      },
      "data344": {
        "field1": "data344",
        "field2": 436778.51,
        "field3": true
      },
      "data41": {
        "field1": "data41",
        "field2": 14348.0,
        "field3": true
      }
    }
  """
    
    case class RegionInfo(flow: Int, cumFlow: Int, vehSet: Set[Int])
    
    val areas = Map(
                "001"->RegionInfo(90,83,Set(3,4)),
                "002"->RegionInfo(40,81,Set(3,4)),
                "003"->RegionInfo(99,83,Set(3,4,9))
                  )

    def main(args: Array[String]) = {
        val pkg = ("areas"->Extraction.decompose(areas)) ~ ("name"->"abc") ~ ("veh"->None)
        println(writePretty(pkg))
        
        val _areas = (pkg\\"areas").extract[Map[String,RegionInfo]]
        println("areas:"+_areas)
        _areas.foreach(println)
        println(write(pkg))
        
        val map1 = Map(420105 -> Some(0.02207505518763797), 420104 -> None, 420103 -> None)
        println(Extraction.decompose(map1))

//        val data = Map(
//            "testNone" -> None,
//            "testNull" -> null,
//            "testSomeNull" -> Some(null),
//            "testJNull" -> JNull,
//            "testSomeJNull" -> Some(JNull))

        val arr = ("name" -> JString("pppp"))
        val a = JObject(("name",JString("opppp")))
//        println(pretty(a))
        println(write(a))
        
        val data = List(
                Map("flow"->90, "name"->"abc", "set"->scala.collection.mutable.Set(1,2,4)),
                Map("flow"->30, "name"->"from", "set"->scala.collection.mutable.Set(1,6,9,2))
                )
                
        
        val data1 = scala.collection.mutable.Map[Short,Long]()
        val r = scala.util.Random
        for (i <- 1 to 30) {
            data1 += (i.toShort->r.nextInt)
        }
                
        val json = Extraction.decompose(data1)
        val arr1 = 1 to 20
        val json2 = ("vehs", arr1)
//        val json = ("age",90) ~ ("name","89") ~ ("height",34.5)
        val json1 = ("flow", 90)
//        println((json \ "name").extract[String])
        println(write(json1))
        
//        println(Extraction.decompose(data1))
        val A = write(data1)
        println(A)
        
        val veh = Vehicle("lkfoe","02")
        println("veh="+write(Extraction.decompose(veh)))
        
        println("json="+json)
//        data1.foreach(x => println(write(x)))
        
        val json3 = ("name"->"lixiaia") ~ ("age"->(None:Option[Int]))
        val json4 = Extraction.decompose(json3)
        val res = render(json4)
        println(compact(res))
    }
}














