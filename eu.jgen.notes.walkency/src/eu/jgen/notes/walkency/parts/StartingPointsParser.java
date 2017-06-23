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
package eu.jgen.notes.walkency.parts;

import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import eu.jgen.notes.automation.wrapper.JGenModel;
import eu.jgen.notes.automation.wrapper.JGenObject;
import eu.jgen.notes.walkency.parts.SelectAndExpand.StartingPoint;

public class StartingPointsParser {

	private static final Object[] EMPTY = new Object[]{};

	public static StartingPoint parse(JGenModel genModel, String[] args) { 
		if(args.length <= 1) {
			return new StartingPoint(EMPTY);
		}		
		String param = args[args.length - 1];      // assume that last has string with the pairs 
		List<JGenObject> list = new Vector<JGenObject>();
		StringTokenizer line = new StringTokenizer(param);
		int count = line.countTokens();		
		if(count < 3) {
			return new StartingPoint(EMPTY);       // something wrong - do nothing
		}
		@SuppressWarnings("unused")
		String diagranName = line.nextToken();     // skip - no use of diagram name
		String m = line.nextToken();               // position on the first pair
		while (m.equals("-o") || m.equals("-m")) {
			if(line.hasMoreTokens()) {
				String text = line.nextToken();
				int id;
				try {
					id = Integer.parseInt(text);
					if(id >= 0) {
						JGenObject genObject = genModel.getEncy().findObjectById(id);
						if(genObject != null) {
							list.add(genObject);
						}
					}	
				} catch (NumberFormatException e) {
				}				
			}
			if(line.hasMoreTokens()) {
				m = line.nextToken();
			} else {
				break;
			}			
		}
		return new StartingPoint(list.toArray());
	}
}
