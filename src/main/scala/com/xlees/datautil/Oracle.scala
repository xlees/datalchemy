package com.xlees.datautil

import java.util.Properties
import java.io.FileInputStream
import java.sql.DriverManager
import org.slf4j.LoggerFactory
import java.sql.ResultSet
import org.specs2.internal.scalaz.std.effect.sql.resultSet
import java.sql.ResultSetMetaData
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import scala.util.{Try, Success, Failure}
import java.sql.Connection
import scala.collection.mutable.ListBuffer


/**
 * @author Xiang
 */



object Oracle {
    val logger = LoggerFactory.getLogger(this.getClass)
    
    val prop = new Properties
    prop.load(getClass.getResourceAsStream("/conf.properties"))
    
    Class.forName("oracle.jdbc.driver.OracleDriver")
    val uri = prop.getProperty("uri")
    val user = prop.getProperty("usr")
    val pwd = prop.getProperty("pwd")
    
    def getConnection: Connection = {
        logger.debug("{},{},{}", uri,user,pwd)
        
        return DriverManager.getConnection(uri,user,pwd)
    }
    
    private def toJson(rs: ResultSet): JSONArray = {    // 需要try
        
        val jsonArray = new JSONArray
                
        val metaData = rs.getMetaData
        val cols = metaData.getColumnCount
        
        rs.setFetchSize(2000)       // default 10, improve perfermance.
        while (rs.next()) {
            
            val obj = new JSONObject
            for (i <- 0 until cols) {
                obj.put(metaData.getColumnLabel(i+1).toLowerCase,
                        rs.getObject(i+1))
            }
            
            jsonArray.add(obj)
        }
        
        rs.close
        
        return jsonArray
    }
    
    def query(sql: String): Try[List[Map[String,Option[Any]]]] = {    // 需要try
        
        return Try {
            val stmt = getConnection.createStatement
            val rs = stmt.executeQuery(sql)
            
            val ret = new ListBuffer[Map[String,Option[Any]]]()
            val metaData = rs.getMetaData
            val cols = metaData.getColumnCount
            
            rs.setFetchSize(2000)       // default 10, improve perfermance.
            while (rs.next()) {
                
                val obj = scala.collection.mutable.Map[String,Option[Any]]()
                for (i <- 0 until cols) {
                    obj += metaData.getColumnLabel(i+1).toLowerCase -> Option(rs.getObject(i+1))
                }
                
                ret += obj.toMap
            }
            
            rs.close
            stmt.close
            
            ret.toList
        }
    }
    
    def main(args: Array[String]) = {
        val res = query("select t.* from T_TIAP_BAYONET_RELATED t")
        logger.info("size={}", res.get.size)
        
//        val conn = getConnection
//        val stmt = conn.createStatement
//        val rs = stmt.executeQuery("select count(0) cnt from T_TIAP_BAYONET_RELATED")
//        while (rs.next()) {
//            println(rs.getInt("cnt"))
//        }
        
    }
}