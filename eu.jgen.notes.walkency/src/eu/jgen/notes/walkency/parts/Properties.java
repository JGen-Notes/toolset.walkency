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

import javax.inject.Inject;
import javax.inject.Named;

import java.net.URL;
import java.util.List;
import java.util.Vector;

import javax.annotation.PostConstruct;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.ca.gen.jmmi.schema.AscTypeCode;
import com.ca.gen.jmmi.schema.AscTypeHelper;
import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.ca.gen.jmmi.schema.ObjTypeHelper;
import com.ca.gen.jmmi.schema.PrpTypeCode;
import com.ca.gen.jmmi.schema.PrpTypeHelper;

import eu.jgen.notes.automation.wrapper.JGenObject;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.jface.viewers.ColumnPixelData;

public class Properties {

	private static final String LABEL_MODIFYING = "Modifying";

	private static final String LABEL_ORDER = "Order";

	private static final String LABEL_OPTIONALITY = "Optionality";

	private static final String LABEL_INVERSE_TYPE_CODE = "Inverse Type Code";

	private static final String LABEL_INVERSE_TYPE_MNEMONIC = "Inverse Type Mnemonic";

	private static final String LABEL_CARDINALITY = "Cardinality";

	private static final String LABEL_ASSOCIATION_TYPE_CODE = "Association Type Code";

	private static final String LABEL_DESTINATION_OBJECT = "Destination Object:";

	private static final String LABEL_ASSOCIATION = "Association:";

	private static final String LABEL_SOURCE_OBJECT = "Source Object:";

	private static final String LABEL_PROPERTIES = "Properties:";

	private static final String LABEL_OBJECT_TYPE_CODE = "Object Type Code";

	private static final String LABEL_NAME = "Name";

	private static final String LABEL_MNEMONIC = "Mnemonic";

	private static final String LABEL_ID = "Id";

	private static final String LABEL_OBJECT = "Object:";

	private static final String INDENT = "     ";

	private final Image OBJECT = getImage("object.gif");
	private final Image FORWARD = getImage("forward.gif");
	private final Image BACKWARD = getImage("backward.gif");
	private final Image PROPERTY = getImage("variable.gif");

	class Row {

		private String property;
		private String value;
		private Image image;

		public Row(String description, String value, Image image) {
			super();
			this.property = description;
			this.value = value;
			this.image = image;
		}

		public String getProperty() {
			return property;
		}

		public void setProperty(String description) {
			this.property = description;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public Image getImage() {
			return image;
		}

		public void setImage(Image image) {
			this.image = image;
		}
	}

	private List<Row> rows = new Vector<Row>();

	private Table table;
	private TableViewer tableViewer;

	@Inject
	public Properties() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(1, false));

		Group grpPropertiesOfThe = new Group(composite, SWT.NONE);
		grpPropertiesOfThe.setToolTipText(
				"This view displays details for the object currently selected \r\nfrom the model expansion tree. \r\n\r\nIt shows the following information:\r\n\t- details of the association with the parent,\r\n\t- details of the object, \r\n\t- list of properties.");
		grpPropertiesOfThe.setFont(SWTResourceManager.getFont("Cambria", 12, SWT.BOLD));
		GridData gd_grpPropertiesOfThe = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_grpPropertiesOfThe.widthHint = 435;
		grpPropertiesOfThe.setLayoutData(gd_grpPropertiesOfThe);
		grpPropertiesOfThe.setText("Details and Properties of the selected object");
		grpPropertiesOfThe.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite compositeScroll = new Composite(grpPropertiesOfThe, SWT.BORDER);
		compositeScroll.setLayout(new FillLayout(SWT.HORIZONTAL));

		Composite compositeTable = new Composite(compositeScroll, SWT.NONE);
		TableColumnLayout tcl_compositeTable = new TableColumnLayout();
		compositeTable.setLayout(tcl_compositeTable);

		tableViewer = new TableViewer(compositeTable, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setColumnProperties(new String[] {});
		tableViewer.setContentProvider(new ArrayContentProvider());
		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		TableViewerColumn tableViewerColumnDesc = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnDescription = tableViewerColumnDesc.getColumn();
		tcl_compositeTable.setColumnData(tblclmnDescription, new ColumnPixelData(150, true, true));
		tblclmnDescription.setText("Description");
		tableViewerColumnDesc.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Row) {
					Row row = (Row) element;
					return row.getProperty();
				}
				return super.getText(element);
			}
		});

		TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
		tcl_compositeTable.setColumnData(tblclmnValue, new ColumnPixelData(252, true, true));
		tblclmnValue.setText("Value");
		tableViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof Row) {
					Row row = (Row) element;
					return row.getValue();
				}
				return super.getText(element);
			}

			@Override
			public Image getImage(Object element) {
				if (element instanceof Row) {
					Row row = (Row) element;
					return row.getImage();
				}
				return super.getImage(element);
			}
		});

	}

	@Inject
	void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) JGenObject genObject) {
		if (genObject != null) {
			rows.clear();
			rows.add(new Row(LABEL_OBJECT, "", null));
			rows.add(new Row(INDENT + LABEL_ID, String.valueOf(genObject.getId()), OBJECT));
			rows.add(new Row(INDENT + LABEL_MNEMONIC,
					ObjTypeHelper.getMnemonic(ObjTypeHelper.valueOf((short) genObject.getObjTypeCode())), OBJECT));
			String name = genObject.findTextProperty(PrpTypeHelper.getCode(PrpTypeCode.NAME));
			if (name.length() > 0) {
				rows.add(new Row(INDENT + LABEL_NAME, name, OBJECT));
			}
			rows.add(new Row(INDENT + LABEL_OBJECT_TYPE_CODE, String.valueOf(genObject.getObjTypeCode()), OBJECT));
			addObjectProperies(genObject);
			tableViewer.setInput(rows);
		}
	}
	
	@Inject
	void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) AssociationNodeOne associationNodeOne) {	
	
		if (associationNodeOne != null) {
			ObjTypeCode objTypeCode = ObjTypeHelper.valueOf((short) associationNodeOne.getFromGenObject().getObjTypeCode());
			Image image = AscTypeHelper.isForward(objTypeCode, associationNodeOne.getAscTypeCode()) ? FORWARD : BACKWARD;
			rows.clear();
			// source
			rows.add(new Row(LABEL_SOURCE_OBJECT, "", null));
			rows.add(new Row(INDENT + LABEL_ID, String.valueOf(associationNodeOne.getFromGenObject().getId()), OBJECT));
			rows.add(new Row(INDENT + LABEL_MNEMONIC,
					ObjTypeHelper.getMnemonic(ObjTypeHelper.valueOf((short) associationNodeOne.getFromGenObject().getObjTypeCode())), OBJECT));
			String name = associationNodeOne.getFromGenObject().findTextProperty(PrpTypeHelper.getCode(PrpTypeCode.NAME));
			if (name.length() > 0) {
				rows.add(new Row(INDENT + LABEL_NAME, name, OBJECT));
			}
			rows.add(new Row(INDENT + LABEL_OBJECT_TYPE_CODE, String.valueOf(associationNodeOne.getFromGenObject().getObjTypeCode()), OBJECT));
			// association
			rows.add(new Row(LABEL_ASSOCIATION, "", null));
			rows.add(new Row(INDENT + LABEL_MNEMONIC, associationNodeOne.getAscTypeCode().name(), image));
			rows.add(new Row(INDENT + LABEL_ASSOCIATION_TYPE_CODE, String.valueOf(associationNodeOne.getAscTypeCode().getCode()), image));
			rows.add(new Row(INDENT + LABEL_CARDINALITY, AscTypeHelper.isOneToOne(objTypeCode, associationNodeOne.getAscTypeCode()) ? "1" : "M", image));
 			AscTypeCode inverseTypeCode = AscTypeHelper.getInverse(objTypeCode, associationNodeOne.getAscTypeCode());
			rows.add(new Row(INDENT + LABEL_INVERSE_TYPE_MNEMONIC, inverseTypeCode.name(), image));
 			rows.add(new Row(INDENT + LABEL_INVERSE_TYPE_CODE,  String.valueOf(AscTypeHelper.getCode(inverseTypeCode)), image));
			rows.add(new Row(INDENT + LABEL_OPTIONALITY, AscTypeHelper.isIgnorable(objTypeCode, associationNodeOne.getAscTypeCode()) ? "O" : "M", image));
			rows.add(new Row(INDENT + LABEL_ORDER, AscTypeHelper.isOrdered(objTypeCode, associationNodeOne.getAscTypeCode()) ? "Y" : "N", image));
			rows.add(new Row(INDENT + LABEL_MODIFYING, AscTypeHelper.isModifying(objTypeCode, associationNodeOne.getAscTypeCode()) ? "Y" : "N", image));
			// destination
			rows.add(new Row(LABEL_DESTINATION_OBJECT, "", null));
			rows.add(new Row(INDENT + LABEL_ID, String.valueOf(associationNodeOne.getToGenObject().getId()), OBJECT));
			rows.add(new Row(INDENT + LABEL_MNEMONIC,
					ObjTypeHelper.getMnemonic(ObjTypeHelper.valueOf((short) associationNodeOne.getToGenObject().getObjTypeCode())), OBJECT));
			name = associationNodeOne.getToGenObject().findTextProperty(PrpTypeHelper.getCode(PrpTypeCode.NAME));
			if (name.length() > 0) {
				rows.add(new Row(INDENT + LABEL_NAME, name, OBJECT));
			}
			rows.add(new Row(INDENT + LABEL_OBJECT_TYPE_CODE, String.valueOf(associationNodeOne.getToGenObject().getObjTypeCode()), OBJECT));
			//
			tableViewer.setInput(rows);
		}
	}
	
	@Inject
	void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) AssociationNodeMany associationNodeMany) {		
		if (associationNodeMany != null) {
			ObjTypeCode objTypeCode = ObjTypeHelper.valueOf((short) associationNodeMany.getFromGenObject().getObjTypeCode());
			Image image = AscTypeHelper.isForward(objTypeCode, associationNodeMany.getAscTypeCode()) ? FORWARD : BACKWARD;
			rows.clear();
			rows.add(new Row(LABEL_SOURCE_OBJECT, "", null));
			rows.add(new Row(INDENT + LABEL_ID, String.valueOf(associationNodeMany.getFromGenObject().getId()), OBJECT));
			rows.add(new Row(INDENT + LABEL_MNEMONIC,
					ObjTypeHelper.getMnemonic(ObjTypeHelper.valueOf((short) associationNodeMany.getFromGenObject().getObjTypeCode())), OBJECT));
			String name = associationNodeMany.getFromGenObject().findTextProperty(PrpTypeHelper.getCode(PrpTypeCode.NAME));
			if (name.length() > 0) {
				rows.add(new Row(INDENT + LABEL_NAME, name, OBJECT));
			}
			rows.add(new Row(INDENT + LABEL_OBJECT_TYPE_CODE, String.valueOf(associationNodeMany.getFromGenObject().getObjTypeCode()), OBJECT));
			// association
			rows.add(new Row(LABEL_ASSOCIATION, "", null));
			rows.add(new Row(INDENT + LABEL_MNEMONIC, associationNodeMany.getAscTypeCode().name(), image));
			rows.add(new Row(INDENT + LABEL_ASSOCIATION_TYPE_CODE, String.valueOf(associationNodeMany.getAscTypeCode().getCode()), image));
			rows.add(new Row(INDENT + LABEL_CARDINALITY, AscTypeHelper.isOneToOne(objTypeCode, associationNodeMany.getAscTypeCode()) ? "1" : "M", image));
 			AscTypeCode inverseTypeCode = AscTypeHelper.getInverse(objTypeCode, associationNodeMany.getAscTypeCode());
			rows.add(new Row(INDENT + LABEL_INVERSE_TYPE_MNEMONIC, inverseTypeCode.name(), image));
 			rows.add(new Row(INDENT + LABEL_INVERSE_TYPE_CODE,  String.valueOf(AscTypeHelper.getCode(inverseTypeCode)), image));
			rows.add(new Row(INDENT + LABEL_OPTIONALITY, AscTypeHelper.isIgnorable(objTypeCode, associationNodeMany.getAscTypeCode()) ? "O" : "M", image));
			rows.add(new Row(INDENT + LABEL_ORDER, AscTypeHelper.isOrdered(objTypeCode, associationNodeMany.getAscTypeCode()) ? "Y" : "N", image));
			rows.add(new Row(INDENT + LABEL_MODIFYING, AscTypeHelper.isModifying(objTypeCode, associationNodeMany.getAscTypeCode()) ? "Y" : "N", image));
			// destination
			rows.add(new Row(LABEL_DESTINATION_OBJECT, "[*]", null));

			tableViewer.setInput(rows);
		}
	}

	private void addObjectProperies(JGenObject genObject) {
		rows.add(new Row(LABEL_PROPERTIES, "", null));
		ObjTypeCode objTypeCode = ObjTypeHelper.valueOf((short) genObject.getObjTypeCode());
		List<PrpTypeCode> list = ObjTypeHelper.getProperties(ObjTypeHelper.valueOf((short) genObject.getObjTypeCode()));
		for (PrpTypeCode prpTypeCode : list) {
			String mnemonic = PrpTypeHelper.getMnemonic(prpTypeCode);
			int code = PrpTypeHelper.getCode(prpTypeCode);
			switch (PrpTypeHelper.getFormat(objTypeCode, prpTypeCode)) {
 			case ART:
			case TEXT:
			case LOADNAME:
			case NAME:
				addObjectTextProperies(genObject, mnemonic, code);
				break;
			case CHAR:
				addObjectCharacterProperies(genObject, mnemonic, code);
				break;
 			case SINT:
			case INT:
 				addObjectNumberProperies(genObject, mnemonic, code);
				break;
			default:
				break;
			}
		}
	}

	private void addObjectNumberProperies(JGenObject genObject, String mnemonic, int code) {
		String text = String.valueOf(genObject.findNumericProperty(code));
		rows.add(new Row(INDENT + mnemonic, text, PROPERTY));
	}

	private void addObjectCharacterProperies(JGenObject genObject, String mnemonic, int code) {
		String text = String.valueOf(genObject.findCharacterProperty(code));
		rows.add(new Row(INDENT + mnemonic, text, PROPERTY));
	}

	private void addObjectTextProperies(JGenObject genObject, String mnemonic, int code) {
		String text = genObject.findTextProperty(code);
		rows.add(new Row(INDENT + mnemonic, text, PROPERTY));
	}

	private static Image getImage(String file) {
		Bundle bundle = FrameworkUtil.getBundle(WalkencyLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}

}