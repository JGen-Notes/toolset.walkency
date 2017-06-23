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

import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.ca.gen.jmmi.schema.AscTypeHelper;
import com.ca.gen.jmmi.schema.ObjTypeHelper;
import com.ca.gen.jmmi.schema.PrpTypeCode;
import com.ca.gen.jmmi.schema.PrpTypeHelper;

import eu.jgen.notes.automation.wrapper.JGenObject;

public class WalkencyLabelProvider extends LabelProvider {

	private static final String MARKER_MANY = " [*]";
	private final Image OBJECT = getImage("object.gif");
	private final Image FORWARD = getImage("forward.gif");
	private final Image BACKWARD = getImage("backward.gif");

	@Override
	public String getText(Object element) {
		if (element instanceof JGenObject) {
			JGenObject object = (JGenObject) element;
			return ObjTypeHelper.getMnemonic(ObjTypeHelper.valueOf((short) (object.getObjTypeCode()))) + genObjectNameIfAny(object);
		} else if (element instanceof AssociationNodeOne) {
			AssociationNodeOne associationNodeOne = (AssociationNodeOne) element;
			return associationNodeOne.getAscTypeCode().toString();
		} else if (element instanceof AssociationNodeMany) {
			AssociationNodeMany associationNodeMany = (AssociationNodeMany) element;
			return associationNodeMany.getAscTypeCode().toString() + MARKER_MANY;
		}
		return super.getText(element);
	}

	private String genObjectNameIfAny(JGenObject genObject) {
		String name = genObject.findTextProperty(PrpTypeHelper.getCode(PrpTypeCode.NAME));
		return name.length() > 0 ? " - " + name : "";
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof JGenObject) {
			return OBJECT;
		} else if (element instanceof AssociationNode) {
			AssociationNode associationNode = (AssociationNode) element;
			if (AscTypeHelper.isForward(
					ObjTypeHelper.valueOf((short) (associationNode.getFromGenObject().getObjTypeCode())),
					associationNode.getAscTypeCode())) {
				return FORWARD;
			} else {
				return BACKWARD;
			}				
		}
		return super.getImage(element);
	}

	private static Image getImage(String file) {
		Bundle bundle = FrameworkUtil.getBundle(WalkencyLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}
}
