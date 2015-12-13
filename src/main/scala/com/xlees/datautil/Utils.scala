package com.xlees.datautil

import java.net.URLDecoder

object Utils {

    def getBaseDir: String = {
		
		val url = getClass.getProtectionDomain().getCodeSource().getLocation()
		val bdir = URLDecoder.decode(url.getPath, "utf-8")
		
		val pos = bdir.lastIndexOf("/")
		val root = if (bdir.endsWith(".jar")) {   // jar package
			bdir.substring(0, pos + 1)
		}
		else {
			bdir
		}
		
		return root;
	}
}