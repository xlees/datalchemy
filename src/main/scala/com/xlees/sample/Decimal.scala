package com.xlees.sample

/**
 * @author Xiang
 */
object Decimal {
  
    def main(args: Array[String]) {
        val a: Any = new java.math.BigDecimal(90.0)
        val o = Option(a)
        println(a.getClass())
        
        val b = BigDecimal(a.asInstanceOf[java.math.BigDecimal])
        println(b.toDouble)
        
        val oo = o.map(x => BigDecimal(x.asInstanceOf[java.math.BigDecimal]).toDouble)
        println(oo)
    }
}