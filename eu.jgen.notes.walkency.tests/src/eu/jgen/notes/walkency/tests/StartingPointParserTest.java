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
package eu.jgen.notes.walkency.tests;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.jgen.notes.automation.wrapper.JGenEncyclopedia;
import eu.jgen.notes.automation.wrapper.JGenFactory;
import eu.jgen.notes.automation.wrapper.JGenModel;
import eu.jgen.notes.walkency.parts.SelectAndExpand.StartingPoint;
import eu.jgen.notes.walkency.parts.StartingPointsParser;
 


public class StartingPointParserTest {
	
	private static JGenModel genModel;
	
	private String[] args1 = {"xxx","-clean", "ActionDiagram -m 24117253 -o 29360134 -o 20971956 -o -1 -o 31457403 -o 72351777 -o 29360134 -o -1 -o 26214614 -o 205520927 -o 205520928 -o 39845921 -o 20972032 -o -1 -o 37749151 -o -1 -o 20972025 -o -1 -o 31457404 -o 72351744 -o 26214614 -o -1"};
	private String[] args2 = {"xxx", "ActionDiagram -m 24117253 -o 29360134 -o 20971956 -o -1 -o 31457403 -o 72351777 -o 29360134 -o -1 -o 26214614 -o 205520927 -o 205520928 -o 39845921 -o 20972032 -o -1 -o 37749151 -o -1 -o 20972025 -o -1 -o 31457404 -o 72351744 -o 26214614 -o -1"};
	private String[] args3 = {"xxx", "ActionDiagram -m 24117253 "};
	private String[] args4 = {"xxx", "ActionDiagram -m"};
	private String[] args5 = {"xxx", "ActionDiagram"};
	private String[] args6 = {"xxx", "ActionDiagram -m 24117253 -o 29360134 -o 20971956"};
	private String[] args7 = {"xxx", "ActionDiagram -m 24117253 -o 29360134 -o"};
	private String[] args8 = {"xxx", "-clean","ActionDiagram -m 24117253 -o 29360134 -o"};
	private String[] args9 = {"xxx", "ActionDiagram -m 2411725a "};
	private String[] args10 = {"xxx", "-clean","ActionDiagram -m 2411725a -o 29360134 -o"};

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {		
		JGenFactory factory = JGenFactory.eINSTANCE;
		JGenEncyclopedia ency = factory.createEncyclopedia();
		ency.connect();
		genModel = ency.findModels()[0];
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test1() {		 
		StartingPoint startingPoint = StartingPointsParser.parse(genModel, args1);	
		assertEquals(16, startingPoint.getObjects().length);
	}
	
	@Test
	public void test2() {				
		StartingPoint startingPoint = StartingPointsParser.parse(genModel, args2);		
		assertEquals(16, startingPoint.getObjects().length);
	}

	@Test
	public void test3() {				
		StartingPoint startingPoint = StartingPointsParser.parse(genModel, args3);		
		assertEquals(1, startingPoint.getObjects().length);
	}
	
	@Test
	public void test4() {				
		StartingPoint startingPoint = StartingPointsParser.parse(genModel, args4);		
		assertEquals(0, startingPoint.getObjects().length);
	}	
	
	@Test
	public void test5() {				
		StartingPoint startingPoint = StartingPointsParser.parse(genModel, args5);		
		assertEquals(0, startingPoint.getObjects().length);
	}
	
	@Test
	public void test6() {				
		StartingPoint startingPoint = StartingPointsParser.parse(genModel, args6);		
		assertEquals(3, startingPoint.getObjects().length);
	}
	
	@Test
	public void test7() {				
		StartingPoint startingPoint = StartingPointsParser.parse(genModel, args7);		
		assertEquals(2, startingPoint.getObjects().length);
	}
	
	@Test
	public void test8() {				
		StartingPoint startingPoint = StartingPointsParser.parse(genModel, args8);		
		assertEquals(2, startingPoint.getObjects().length);
	}
	
	@Test
	public void test9() {				
		StartingPoint startingPoint = StartingPointsParser.parse(genModel, args9);		
		assertEquals(0, startingPoint.getObjects().length);
	}	
	
	@Test
	public void test10() {				
		StartingPoint startingPoint = StartingPointsParser.parse(genModel, args10);		
		assertEquals(1, startingPoint.getObjects().length);
	}
}
