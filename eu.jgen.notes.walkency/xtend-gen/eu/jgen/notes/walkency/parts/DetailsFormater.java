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
 */
package eu.jgen.notes.walkency.parts;

import com.ca.gen.jmmi.schema.AscTypeCode;
import com.ca.gen.jmmi.schema.AscTypeHelper;
import com.ca.gen.jmmi.schema.DivTypeCode;
import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.ca.gen.jmmi.schema.PrpTypeCode;
import com.ca.gen.jmmi.schema.PrpTypeHelper;
import com.ca.gen.jmmi.util.PrpFormat;
import com.google.common.base.Objects;

@SuppressWarnings("all")
public class DetailsFormater {
  public static String formatProperty(final ObjTypeCode objTypeCode, final PrpTypeCode prpTypeCode) {
    StringBuffer buffer = new StringBuffer();
    buffer.append("format=");
    PrpFormat _format = PrpTypeHelper.getFormat(objTypeCode, prpTypeCode);
    String _string = _format.toString();
    buffer.append(_string);
    buffer.append(", default=");
    PrpFormat _format_1 = PrpTypeHelper.getFormat(objTypeCode, prpTypeCode);
    if (_format_1 != null) {
      switch (_format_1) {
        case ART:
          String _defaultTxtValue = PrpTypeHelper.getDefaultTxtValue(objTypeCode, prpTypeCode);
          buffer.append(_defaultTxtValue);
          break;
        case CHAR:
          buffer.append("\'");
          char _defaultChrValue = PrpTypeHelper.getDefaultChrValue(objTypeCode, prpTypeCode);
          buffer.append(_defaultChrValue);
          buffer.append("\'");
          break;
        case INT:
          long _defaultIntValue = PrpTypeHelper.getDefaultIntValue(objTypeCode, prpTypeCode);
          buffer.append(_defaultIntValue);
          break;
        case LOADNAME:
          String _defaultTxtValue_1 = PrpTypeHelper.getDefaultTxtValue(objTypeCode, prpTypeCode);
          buffer.append(_defaultTxtValue_1);
          break;
        case NAME:
          String _defaultTxtValue_2 = PrpTypeHelper.getDefaultTxtValue(objTypeCode, prpTypeCode);
          buffer.append(_defaultTxtValue_2);
          break;
        case SINT:
          long _defaultIntValue_1 = PrpTypeHelper.getDefaultIntValue(objTypeCode, prpTypeCode);
          buffer.append(_defaultIntValue_1);
          break;
        case TEXT:
          String _defaultTxtValue_3 = PrpTypeHelper.getDefaultTxtValue(objTypeCode, prpTypeCode);
          boolean _equals = Objects.equal(_defaultTxtValue_3, null);
          if (_equals) {
            buffer.append("NULL");
          } else {
            buffer.append("\"");
            String _defaultTxtValue_4 = PrpTypeHelper.getDefaultTxtValue(objTypeCode, prpTypeCode);
            buffer.append(_defaultTxtValue_4);
            buffer.append("\"");
          }
          break;
        default:
          break;
      }
    } else {
    }
    buffer.append(", length=");
    short _length = PrpTypeHelper.getLength(objTypeCode, prpTypeCode);
    buffer.append(_length);
    return buffer.toString();
  }
  
  public static String formatAssociation(final ObjTypeCode objTypeCode, final AscTypeCode ascTypeCode) {
    StringBuffer buffer = new StringBuffer();
    boolean _isOneToMany = AscTypeHelper.isOneToMany(objTypeCode, ascTypeCode);
    if (_isOneToMany) {
      buffer.append("1:M");
    } else {
      buffer.append("1:1");
    }
    buffer.append(", target=");
    DivTypeCode _targetDivision = AscTypeHelper.getTargetDivision(objTypeCode, ascTypeCode);
    buffer.append(_targetDivision);
    buffer.append(", opt=");
    boolean _isIgnorable = AscTypeHelper.isIgnorable(objTypeCode, ascTypeCode);
    if (_isIgnorable) {
      buffer.append("Y");
    } else {
      buffer.append("N");
    }
    buffer.append(", ordered=");
    boolean _isOrdered = AscTypeHelper.isOrdered(objTypeCode, ascTypeCode);
    if (_isOrdered) {
      buffer.append("Y");
    } else {
      buffer.append("N");
    }
    buffer.append(", mod=");
    boolean _isModifying = AscTypeHelper.isModifying(objTypeCode, ascTypeCode);
    if (_isModifying) {
      buffer.append("Y");
    } else {
      buffer.append("N");
    }
    buffer.append(", inverse=");
    AscTypeCode _inverse = AscTypeHelper.getInverse(objTypeCode, ascTypeCode);
    buffer.append(_inverse);
    return buffer.toString();
  }
}
