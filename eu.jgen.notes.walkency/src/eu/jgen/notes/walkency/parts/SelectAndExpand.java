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

import java.util.Arrays;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.core.runtime.Platform;
import org.eclipse.e4.core.services.log.Logger;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.swt.SWTResourceManager;

import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.ca.gen.jmmi.schema.ObjTypeHelper;
import com.ca.gen.jmmi.schema.PrpTypeCode;
import com.ca.gen.jmmi.schema.PrpTypeHelper;

import eu.jgen.notes.automation.wrapper.JGMSG;
import eu.jgen.notes.automation.wrapper.JGenEncyclopedia;
import eu.jgen.notes.automation.wrapper.JGenFactory;
import eu.jgen.notes.automation.wrapper.JGenModel;
import eu.jgen.notes.automation.wrapper.JGenObject;

@SuppressWarnings("restriction")
public class SelectAndExpand {

	public static class StartingPoint {

		private Object[] objects;

		public Object[] getObjects() {
			return objects;
		}

		public StartingPoint(Object[] objects) {
			super();
			this.objects = objects;
		}

	}

	private static final String EMPTY_STRING = "";
	private JGenModel genModel;
	private Text textObjectId;
	private Text textName;
	private Combo comboObjectType;
	private TreeViewer treeViewer;

	@Inject
	private ESelectionService selectionService;

	@Inject
	private Logger logger;

	@Inject
	public SelectAndExpand() {
	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		JGenFactory factory = JGenFactory.eINSTANCE;
		JGenEncyclopedia ency = factory.createEncyclopedia();
		ency.connect();
		if (ency.getReturnCode() == JGMSG.MODEL_NOT_FOUND_RC) {
			logger.error("Connect to the CA Gen model cannot be completed. rc=" + ency.getReturnCode());
			MessageDialog.openConfirm(parent.getShell(), "Connect Error",
					"Check if CA Gen Toolset is runnting and model is opened.");
			return;
		}
		genModel = ency.findModels()[0];

		logger.info("Connect to the Gen model: " + genModel.getName());
		parent.getShell().setText(parent.getShell().getText() + ": " + genModel.getName());
		parent.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(parent, SWT.BORDER);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setLayout(new GridLayout(1, false));

		Group grpSelection = new Group(composite, SWT.NONE);
		grpSelection.setFont(SWTResourceManager.getFont("Cambria", 12, SWT.BOLD));
		GridData gd_grpSelection = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_grpSelection.heightHint = 110;
		grpSelection.setLayoutData(gd_grpSelection);
		grpSelection.setText("Selecting Starting Point");
		grpSelection.setLayout(new GridLayout(3, false));

		Label lblObjectType = new Label(grpSelection, SWT.NONE);
		lblObjectType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblObjectType.setText("Object Type:");

		comboObjectType = new Combo(grpSelection, SWT.READ_ONLY);
		comboObjectType.setItems(populateCombo());
		comboObjectType.setVisibleItemCount(50);
		comboObjectType.setText("PCROOT");
		GridData gd_comboObjectType = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_comboObjectType.widthHint = 221;
		comboObjectType.setLayoutData(gd_comboObjectType);

		Button btnSearchByType = new Button(grpSelection, SWT.NONE);
		btnSearchByType.setText("Search By Type");
		btnSearchByType.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textName.setText(EMPTY_STRING);
				textObjectId.setText(EMPTY_STRING);
				treeViewer.setInput(ObjTypeCode.valueOf(comboObjectType.getText()));
				selectionService.setSelection(ObjTypeCode.valueOf(comboObjectType.getText()));
			}
		});

		Label lblObjectId = new Label(grpSelection, SWT.NONE);
		lblObjectId.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblObjectId.setText("Object Id:");

		textObjectId = new Text(grpSelection, SWT.BORDER);
		textObjectId.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textObjectId.addListener(SWT.Verify, new Listener() {
			public void handleEvent(Event e) {
				String string = e.text;
				char[] chars = new char[string.length()];
				string.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if (!('0' <= chars[i] && chars[i] <= '9')) {
						e.doit = false;
						return;
					}
				}
			}
		});

		Button btnSearchById = new Button(grpSelection, SWT.NONE);
		btnSearchById.setText("Search By Id");
		btnSearchById.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int id = Integer.valueOf(textObjectId.getText());
				JGenObject genObject = genModel.getEncy().findObjectById(id);
				if(genObject != null) {
					treeViewer.setInput(new StartingPoint(new Object[]{genObject}));
					selectionService.setSelection(genObject);
					comboObjectType.setText(ObjTypeHelper.getMnemonic(ObjTypeHelper.valueOf((short)genObject.getObjTypeCode())));
				}
			}
		});

		Label lblName = new Label(grpSelection, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");

		textName = new Text(grpSelection, SWT.BORDER);
		textName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Button btnSearchByName = new Button(grpSelection, SWT.NONE);
		btnSearchByName.setText("Search By Name");
		btnSearchByName.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {	
				textObjectId.setText(EMPTY_STRING);
				JGenObject[] objects= genModel.findNamedObjects(ObjTypeHelper.getCode(ObjTypeCode.valueOf(comboObjectType.getText())), PrpTypeHelper.getCode(PrpTypeCode.NAME), textName.getText());
					treeViewer.setInput(new StartingPoint(objects));				
			}
		});

		Group grpModelExpansionTree = new Group(composite, SWT.NONE);
		grpModelExpansionTree.setFont(SWTResourceManager.getFont("Cambria", 12, SWT.BOLD));
		grpModelExpansionTree.setText("Model Expansion Tree");
		grpModelExpansionTree.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_grpModelExpansionTree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_grpModelExpansionTree.widthHint = 456;
		grpModelExpansionTree.setLayoutData(gd_grpModelExpansionTree);

		Composite compositeExpantionTree = new Composite(grpModelExpansionTree, SWT.H_SCROLL | SWT.V_SCROLL);
		compositeExpantionTree.setLayout(new TreeColumnLayout());

		treeViewer = new TreeViewer(compositeExpantionTree, SWT.BORDER);
		treeViewer.setContentProvider(new WalkencyContentProvider(genModel));
		treeViewer.setLabelProvider(new WalkencyLabelProvider());
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(false);
		tree.setLinesVisible(true);
		treeViewer.setInput(genModel);

		getAppliactionArgs();

		treeViewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				IStructuredSelection iStructuredSelection = (IStructuredSelection) event.getSelection();
				Object selectedObject = iStructuredSelection.getFirstElement();
				treeViewer.setInput(selectedObject);
				updateStartingPoint(selectedObject);
			}

		});
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				Object object = selection.getFirstElement();
				selectionService.setSelection(object);
				if (object instanceof JGenObject) {
					JGenObject genObject = (JGenObject) object;
					ObjTypeCode objTypeCode = ObjTypeHelper.valueOf((short) genObject.getObjTypeCode());
					comboObjectType.setText(ObjTypeHelper.getMnemonic(objTypeCode));
					textObjectId.setText(Integer.toString(genObject.getId()));
					textName.setText(getObjectNameIfAny(genObject));
				}
			}
		});
	}

	private void getAppliactionArgs() {
		treeViewer.setInput(StartingPointsParser.parse(genModel, Platform.getApplicationArgs()));
	}

	protected void updateStartingPoint(Object selectedObject) {
		if (selectedObject instanceof JGenObject) {
			JGenObject genObject = (JGenObject) selectedObject;
			comboObjectType
					.setText(ObjTypeHelper.getMnemonic(ObjTypeHelper.valueOf((short) genObject.getObjTypeCode())));
			textObjectId.setText(String.valueOf(genObject.getId()));
		} else if (selectedObject instanceof AssociationNode) {
			AssociationNode associationNode = (AssociationNode) selectedObject;
			comboObjectType.setText(ObjTypeHelper
					.getMnemonic(ObjTypeHelper.valueOf((short) associationNode.getFromGenObject().getObjTypeCode())));
			textObjectId.setText(String.valueOf(associationNode.getFromGenObject().getId()));
		}
	}

	private String[] populateCombo() {
		ObjTypeCode[] array = ObjTypeCode.values();
		String[] textArray = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			textArray[i] = array[i].name();
		}
		Arrays.sort(textArray);
		return textArray;
	}

	@Focus
	public void onFocus() {
		if (comboObjectType != null) {
			comboObjectType.setFocus();
		}
	}

	private String getObjectNameIfAny(JGenObject genObject) {
		String name = genObject.findTextProperty(PrpTypeHelper.getCode(PrpTypeCode.NAME));
		return name.length() > 0 ? name : "";
	}
}