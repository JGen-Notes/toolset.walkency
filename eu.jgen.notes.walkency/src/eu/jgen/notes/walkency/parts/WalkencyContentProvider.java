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
import java.util.Vector;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.ca.gen.jmmi.schema.AscTypeCode;
import com.ca.gen.jmmi.schema.AscTypeHelper;
import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.ca.gen.jmmi.schema.ObjTypeHelper;

import eu.jgen.notes.automation.wrapper.JGenModel;
import eu.jgen.notes.automation.wrapper.JGenObject;
import eu.jgen.notes.walkency.parts.SelectAndExpand.StartingPoint;

public class WalkencyContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private JGenModel genModel;

	public WalkencyContentProvider(JGenModel genModel) {
		this.genModel = genModel;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof JGenModel) {
			return new Object[] { genModel };
		} else if (parentElement instanceof JGenObject) {
			return findAssociations((JGenObject) parentElement);
		} else if (parentElement instanceof AssociationNodeOne) {
			AssociationNodeOne associationNodeOne = (AssociationNodeOne) parentElement;
			return new JGenObject[] { associationNodeOne.getToGenObject() };
		} else if (parentElement instanceof AssociationNodeMany) {
			return ((AssociationNodeMany) parentElement).getFromGenObject()
					.findAssociationMany((int) AscTypeHelper.getCode(((AssociationNodeMany) parentElement).getAscTypeCode()));
		} 
		return new Object[] {};
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return true;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof JGenModel) {
			return new Object[] { genModel.findTypeObjects(ObjTypeHelper.getCode(ObjTypeCode.PCROOT))[0] };
		} else if (inputElement instanceof ObjTypeCode) {
			return genModel.findTypeObjects(ObjTypeHelper.getCode((ObjTypeCode) inputElement));
		} else if (inputElement instanceof JGenObject) {
			return getChildren(inputElement);
		} else if (inputElement instanceof AssociationNode) {
			AssociationNode associationNode = (AssociationNode) inputElement;
			return new Object[] {associationNode};
		} else if (inputElement instanceof StartingPoint)  {
			return ((StartingPoint) inputElement).getObjects();
		}
		return new Object[] {};
	}

	private Object[] findAssociations(JGenObject fromGenObject) {
		ObjTypeCode objTypeCode = ObjTypeHelper.valueOf((short) fromGenObject.getObjTypeCode());
		List<AscTypeCode> list = ObjTypeHelper
				.getAssociations(ObjTypeHelper.valueOf((short) fromGenObject.getObjTypeCode()));
		List<AssociationNode> nodes = new Vector<AssociationNode>();
		for (AscTypeCode ascTypeCode : list) {
			int code = AscTypeHelper.getCode(ascTypeCode);
			if (AscTypeHelper.isOneToOne(objTypeCode, ascTypeCode)) {
				JGenObject toGenObject = fromGenObject.findAssociationOne(code);
				if(toGenObject != null) {
					AssociationNodeOne node = new AssociationNodeOne(fromGenObject, toGenObject, ascTypeCode);
					nodes.add(node);
				}
			} else {
				JGenObject[] array = fromGenObject.findAssociationMany(code);
				if (array.length != 0) {
					AssociationNodeMany node = new AssociationNodeMany(fromGenObject, null, ascTypeCode);
					nodes.add(node);
				}
			}
		}
		return nodes.toArray();
	}

}
