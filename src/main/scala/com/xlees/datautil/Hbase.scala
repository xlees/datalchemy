package com.xlees.datautil

import java.text.SimpleDateFormat

import org.apache.hadoop.hbase.Cell
import org.apache.hadoop.hbase.CellUtil
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.HColumnDescriptor
import org.apache.hadoop.hbase.HTableDescriptor
import org.apache.hadoop.hbase.InvalidFamilyOperationException
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.client.ConnectionFactory
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.client.Put
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.util.Bytes

/**
 * @author Xiang
 */


object HbaseTest {
    
    def main(args: Array[String]) = {
        val quorum = "ehl-test-02,ehl-test-03,oracle-109"
        val port = "2181"
        val master = "ehl-test-01:600000"
        
        val client = new Hbase(port,quorum,master)
        client.getTableNames().foreach(println)
        
        val tbl = "index-test"
        if (!client.isTableEnabled(tbl))
            client.enableTable(tbl)
        
        println(Bytes.toString(client.get(tbl, Bytes.toBytes("ehl-001"), "cf", "c3")))
        
        
//        client.deleteTable(Constants.TR_STATS)
//        client.addColumn(tbl, "flow")
//        client.put(tbl, Bytes.toBytes("ehl-009"), "flow", "cum", "8934502")
//        client.put(tbl, Bytes.toBytes("ehl-009"), "flow", "cur", "20192")
        // 加1
//        client.incr(tbl, "ehl-009", "flow", "count", 1)
    }
}

/**
 * @author Xiang
 * Hbase 接口
 */
trait THbase {
  
    def enableTable(tName: String)
    
    def disableTable(tName: String)
    
    def isTableEnabled(tName: String): Boolean
    
    def getTableNames(): Array[String]
    
    def addColumn(tName: String, family: String)
    
    def deleteColumn(tName: String, family: String)
    
    def exists(tName: String): Boolean
    
//    def getColumnDescriptors(tName: String): Map[String,HColumnDescriptor]
    
    def createTable(tName: String, families: Array[String])
    
    def deleteTable(tName: String)
    
    def get(tName: String, rowkey: Array[Byte], family: String, qualifier: String): Array[Byte]
    
    def get(tName: String, rowkey: Array[Byte]): Result
    
    def put(tName: String, rowKey: Array[Byte], family: String, qualifier: String, value: String)
    
    def put(tName: String, rowkey: Array[Byte], cells: Seq[Cell])
    
    // 不能给已经存在的列进行累加
    def incr(tName: String, rowKey: String, family: String, qualifier: String, value: Long)
}

class Hbase(port: String, quorum: String, master: String) extends THbase {
    
    private val conf = HBaseConfiguration.create()
    conf.set("hbase.zookeeper.property.clientPort", port)
    conf.set("hbase.zookeeper.quorum", quorum)
    conf.set("hbase.master", master)
        
    private val conn = ConnectionFactory.createConnection(conf)
    private val admin = conn.getAdmin()
    
    private val frmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    
    
    def enableTable(tName: String) = {
        admin.enableTable(TableName.valueOf(tName))
    }
    
    def disableTable(tName: String) = {
        admin.disableTable(TableName.valueOf(tName))
    }
    
    def isTableEnabled(tName: String): Boolean = admin.isTableEnabled(TableName.valueOf(tName))
    
    def getTableNames(): Array[String] = admin.listTableNames().toArray.map(_.toString)
    
    def addColumn(tName: String, family: String) = {
        val tbl = TableName.valueOf(tName)
        
        admin.disableTable(tbl)
        try { 
            admin.addColumn(tbl, new HColumnDescriptor(family))
        } 
        catch {
            case e:InvalidFamilyOperationException =>
                println("error to add family. msg:\n"+e.getMessage)
            case _: Throwable =>
                println("unknown error.")
                System.exit(-1)
        } 
        finally {
            admin.enableTable(tbl)
        }
    }
    
    def deleteColumn(tName: String, family: String) = {
        val tbl = TableName.valueOf(tName)
        
        admin.disableTable(tbl)
        admin.deleteColumn(tbl, Bytes.toBytes(family))
        admin.enableTable(tbl)
    }
    
    def exists(tName: String): Boolean = admin.tableExists(TableName.valueOf(tName))
    
//    def getColumnDescriptors(tName: String): Map[String,HColumnDescriptor] = {
////        admin.
//    }
    
    def createTable(tName: String, cf: Array[String]) = {
        val tbl = TableName.valueOf(tName)
        
        if (!admin.tableExists(tbl)) {
            println(tName + " not exists, and will create it first!")
            
            val tableDesc = new HTableDescriptor(tbl)
            for (family <- cf)
                tableDesc.addFamily(new HColumnDescriptor(family.getBytes))
                
            admin.createTable(tableDesc)
        }
        else {
            println(tName + " has already exists!")
        }
    }
    
    def deleteTable(tName: String) = {
        val tbl = TableName.valueOf(tName)
        
        admin.disableTable(tbl)
        admin.deleteTable(tbl)
    }
    
    def get(tName: String, rowkey: Array[Byte], family: String, qualifier: String): Array[Byte] = {
        val tbl = conn.getTable(TableName.valueOf(tName))
        
        tbl.get(new Get(rowkey))  //get(new Get(Bytes.toBytes(rowkey)))
           .getValue(Bytes.toBytes(family), Bytes.toBytes(qualifier))
    }
    
    def get(tName: String, rowkey: Array[Byte]): Result = {
        val tbl = conn.getTable(TableName.valueOf(tName))
        
        tbl.get(new Get(rowkey))
    }
    
    def put(tName: String, rowkey: Array[Byte], family: String, qualifier: String, value: String) = {
        val tbl = conn.getTable(TableName.valueOf(tName))
        
        val _put = new Put(rowkey)
//        _put.add(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value))
//        _put.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value))
        _put.add(CellUtil.createCell(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value)))
        
        tbl.put(_put)
    }
    
    def put(tName: String, rowkey: Array[Byte], cells: Seq[Cell]) = {
        val tbl = conn.getTable(TableName.valueOf(tName))
        val _put = new Put(rowkey)
        
        cells.foreach(_put.add(_))
        
        tbl.put(_put)
    }
    
    def incr(tName: String, rowkey: String, family: String, qualifier: String, value: Long) = {
        
        conn.getTable(TableName.valueOf(tName))
            .incrementColumnValue(Bytes.toBytes(rowkey),
                                  Bytes.toBytes(family),
                                  Bytes.toBytes(qualifier),
                                  value)
    }
        
}






