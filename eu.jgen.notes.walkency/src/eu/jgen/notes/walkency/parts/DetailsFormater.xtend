/**
 * [The "BSD license"]
 * Copyright (c) 2016,JGen Notes
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions 
 *    and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *    and the following disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, 
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS 
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */
package eu.jgen.notes.walkency.parts

import com.ca.gen.jmmi.schema.PrpTypeCode
import com.ca.gen.jmmi.schema.PrpTypeHelper
import com.ca.gen.jmmi.schema.ObjTypeCode
import com.ca.gen.jmmi.util.PrpFormat
import com.ca.gen.jmmi.schema.AscTypeCode
import com.ca.gen.jmmi.schema.AscTypeHelper

class DetailsFormater {

	def static String formatProperty(ObjTypeCode objTypeCode, PrpTypeCode prpTypeCode) {
		var buffer = new StringBuffer()
		buffer.append("format=")
		buffer.append(PrpTypeHelper.getFormat(objTypeCode, prpTypeCode).toString)
		buffer.append(", default=")
		switch (PrpTypeHelper.getFormat(objTypeCode, prpTypeCode)) {
			case PrpFormat.ART: {
				buffer.append(PrpTypeHelper.getDefaultTxtValue(objTypeCode, prpTypeCode))
			}
			case PrpFormat.CHAR: {    
				buffer.append("'")
				buffer.append(PrpTypeHelper.getDefaultChrValue(objTypeCode, prpTypeCode))
				buffer.append("'")
			}
			case PrpFormat.INT: {
				buffer.append(PrpTypeHelper.getDefaultIntValue(objTypeCode, prpTypeCode))
			}
			case PrpFormat.LOADNAME: {
				buffer.append(PrpTypeHelper.getDefaultTxtValue(objTypeCode, prpTypeCode))
			}
			case PrpFormat.NAME: {
				buffer.append(PrpTypeHelper.getDefaultTxtValue(objTypeCode, prpTypeCode))
			}   
			case PrpFormat.SINT: {
				buffer.append(PrpTypeHelper.getDefaultIntValue(objTypeCode, prpTypeCode))
			}
			case PrpFormat.TEXT: {
				if (PrpTypeHelper.getDefaultTxtValue(objTypeCode, prpTypeCode) === null) {
					buffer.append("NULL")
				} else {
					buffer.append("\"")
					buffer.append(PrpTypeHelper.getDefaultTxtValue(objTypeCode, prpTypeCode))
					buffer.append("\"")
				}
			}  
			default: {
			}
		}
		buffer.append(", length=")
		buffer.append((PrpTypeHelper.getLength(objTypeCode, prpTypeCode)))
		return buffer.toString
	}

	def static String formatAssociation(ObjTypeCode objTypeCode, AscTypeCode ascTypeCode) {
		var buffer = new StringBuffer()
		if (AscTypeHelper.isOneToMany(objTypeCode, ascTypeCode)) {
			buffer.append("1:M")
		} else {
			buffer.append("1:1")
		}
		buffer.append(", target=")
		buffer.append(AscTypeHelper.getTargetDivision(objTypeCode, ascTypeCode))		
		buffer.append(", opt=")
		if (AscTypeHelper.isIgnorable(objTypeCode, ascTypeCode)) {
			buffer.append("Y")
		} else {
			buffer.append("N")
		}   
		buffer.append(", ordered=")
		if (AscTypeHelper.isOrdered(objTypeCode, ascTypeCode)) {
			buffer.append("Y")
		} else {
			buffer.append("N")
		}	
		buffer.append(", mod=")
		if (AscTypeHelper.isModifying(objTypeCode, ascTypeCode)) {
			buffer.append("Y")
		} else {
			buffer.append("N")
		}
		buffer.append(", inverse=")
		buffer.append(AscTypeHelper.getInverse(objTypeCode, ascTypeCode))				
		return buffer.toString
	}
}
