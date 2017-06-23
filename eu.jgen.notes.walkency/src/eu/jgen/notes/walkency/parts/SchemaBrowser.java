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
import java.util.List;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.ESelectionService;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.wb.swt.SWTResourceManager;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.ca.gen.jmmi.schema.AscTypeCode;
import com.ca.gen.jmmi.schema.AscTypeHelper;
import com.ca.gen.jmmi.schema.DivTypeCode;
import com.ca.gen.jmmi.schema.DivTypeHelper;
import com.ca.gen.jmmi.schema.ObjTypeCode;
import com.ca.gen.jmmi.schema.ObjTypeHelper;
import com.ca.gen.jmmi.schema.PrpTypeCode;

import eu.jgen.notes.automation.wrapper.JGenObject;

public class SchemaBrowser {
	
	private static final Object[] EMPTY_ARRAY = new Object[]{};

	static class ItemAsc {
		public ItemAsc(ObjTypeCode objTypeCode,AscTypeCode ascTypeCode) {
			super();
			this.objTypeCode = objTypeCode;
			this.ascTypeCode = ascTypeCode;
		}
		public ObjTypeCode getObjTypeCode() {
			return objTypeCode;
		}
		public AscTypeCode getAscTypeCode() {
			return ascTypeCode;
		}
		private ObjTypeCode objTypeCode;
		private AscTypeCode ascTypeCode;
	}
	
	static class ItemPrp {
		public ItemPrp(ObjTypeCode objTypeCode, PrpTypeCode prpTypeCode) {
			super();
			this.objTypeCode = objTypeCode;
			this.prpTypeCode = prpTypeCode;
		}
		public ObjTypeCode getObjTypeCode() {
			return objTypeCode;
		}
		public PrpTypeCode getPrpTypeCode() {
			return prpTypeCode;
		}
		private ObjTypeCode objTypeCode;
		private PrpTypeCode prpTypeCode;
	}
	
	
	@Inject
	private ESelectionService selectionService;

	private TreeViewer treeViewer;
	private TableViewer tableViewer;
	private Table table;

	private static class TopDivTypeCode {

		private DivTypeCode divTypeCode;

		public DivTypeCode getDivTypeCode() {
			return divTypeCode;
		}

		public TopDivTypeCode(DivTypeCode divTypeCode) {
			super();
			this.divTypeCode = divTypeCode;
		}

	}

	private static class TreeContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof TopDivTypeCode) {
				return getChildren(inputElement);
			}
			return EMPTY_ARRAY;
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof DivTypeCode) {
				List<DivTypeCode> list = DivTypeHelper.getChildren((DivTypeCode) parentElement);
				return list.toArray();
			} else if (parentElement instanceof TopDivTypeCode) {
				return new Object[] { ((TopDivTypeCode) parentElement).getDivTypeCode() };
			}
			return EMPTY_ARRAY;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof DivTypeCode) {
				List<DivTypeCode> list = DivTypeHelper.getChildren((DivTypeCode) element);
				return list.size() > 0;
			}
			return false;
		}

		@Override
		public Object getParent(Object element) {
			if (element instanceof DivTypeCode && !DivTypeHelper.isRoot((DivTypeCode) element)) {
				return DivTypeHelper.getParent((DivTypeCode) element);
			}
			return null;
		}
	}

	private static class TreeLabelProvider extends LabelProvider {
		public Image getImage(Object element) {
			if(element instanceof DivTypeCode) {
				DivTypeCode divTypeCode = (DivTypeCode) element;
				if(DivTypeHelper.hasChildren(divTypeCode)) {
					return SchemaBrowser.getImage("division.gif");
				} else {
					return SchemaBrowser.getImage("class.gif");
				}
			}
			return null;
		}

		public String getText(Object element) {
			if (element instanceof DivTypeCode) {
				return DivTypeHelper.toString((DivTypeCode) element);
			}
			return super.getText(element);
		}
	}

	@Inject
	public SchemaBrowser() {

	}

	@PostConstruct
	public void postConstruct(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		Group grpSchemabrowser = new Group(parent, SWT.BORDER | SWT.SHADOW_NONE);
		grpSchemabrowser.setFont(SWTResourceManager.getFont("Calibri", 12, SWT.BOLD));
		grpSchemabrowser.setText("SchemaBrowser");
		grpSchemabrowser.setLayout(new FillLayout(SWT.HORIZONTAL));

		SashForm sashForm = new SashForm(grpSchemabrowser, SWT.NONE);

		Composite compositeLeft = new Composite(sashForm, SWT.BORDER | SWT.V_SCROLL);
		compositeLeft.setLayout(new TreeColumnLayout());

		treeViewer = new TreeViewer(compositeLeft, SWT.BORDER);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(false);
		tree.setLinesVisible(true);
		treeViewer.setContentProvider(new TreeContentProvider());
 		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.setInput(this);  
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
			      IStructuredSelection selection = (IStructuredSelection) event.getSelection();
			      selectionService.setSelection(selection.getFirstElement());				
			}			
		});

		Composite compositeRight = new Composite(sashForm, SWT.BORDER);
		TableColumnLayout tcl_compositeRight = new TableColumnLayout();
		compositeRight.setLayout(tcl_compositeRight);

		tableViewer = new TableViewer(compositeRight, SWT.BORDER | SWT.FULL_SELECTION);

		table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		TableViewerColumn tableViewerColumnValue = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnValue = tableViewerColumnValue.getColumn();
		tcl_compositeRight.setColumnData(tblclmnValue, new ColumnPixelData(150, true, true));
		tblclmnValue.setText("Association and Properties");
		tableViewerColumnValue.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof ItemAsc) {
					ItemAsc itemAsc = (ItemAsc) element;
					return itemAsc.getAscTypeCode().toString();
				} else if (element instanceof ItemPrp) {
					ItemPrp itemPrp = (ItemPrp) element;
					return itemPrp.getPrpTypeCode().toString();
				}
				return super.getText(element);
			}
			
			@Override
			public Image getImage(Object element) {
				if (element instanceof ItemAsc) {
					return SchemaBrowser.getImage("association.gif");
				} else if (element instanceof ItemPrp) {
					return SchemaBrowser.getImage("property.gif");
				}
				return super.getImage(element);
			}
		});

		
		TableViewerColumn tableViewerDescription = new TableViewerColumn(tableViewer, SWT.NONE);
		TableColumn tblclmnNewColumnDecsription = tableViewerDescription.getColumn();
		tcl_compositeRight.setColumnData(tblclmnNewColumnDecsription, new ColumnPixelData(450, true, true));
		tblclmnNewColumnDecsription.setText("Details");
		tableViewerDescription.setLabelProvider(new ColumnLabelProvider() {

			@Override
			public String getText(Object element) {
				if (element instanceof ItemPrp) {
					ItemPrp itemPrp = (ItemPrp) element;
					return DetailsFormater.formatProperty(itemPrp.getObjTypeCode(), itemPrp.getPrpTypeCode());
				} else if (element instanceof ItemAsc) {
					ItemAsc itemAsc = (ItemAsc) element;
					return DetailsFormater.formatAssociation(itemAsc.objTypeCode, itemAsc.getAscTypeCode());
				}
				return super.getText(element);
			}
			
			@Override
			public Image getImage(Object element) {
				if (element instanceof ItemAsc) {
					ItemAsc itemAsc = (ItemAsc) element;
					if(AscTypeHelper.isForward(itemAsc.getObjTypeCode(), itemAsc.getAscTypeCode())) {
						return SchemaBrowser.getImage("forward.gif");
					} else {
						return SchemaBrowser.getImage("backward.gif");
					}

				} else if (element instanceof ItemPrp) {
					return SchemaBrowser.getImage("variable.gif");
				}
				return super.getImage(element);
			}
		});
		tableViewer.setContentProvider(new ArrayContentProvider());
		sashForm.setWeights(new int[] { 1, 1 });
	}
	
	@Inject
	public void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) ObjTypeCode objTypeCode) {
		if (objTypeCode != null) {
			DivTypeCode divTypeCode = ObjTypeHelper.getDivision(objTypeCode);
			DivTypeCode root = findRoot(divTypeCode);
			treeViewer.setInput(new TopDivTypeCode(root));
			StructuredSelection selection = new StructuredSelection(divTypeCode);
			treeViewer.setSelection(selection);
			selectionService.setSelection(selection);
			tableViewer.setInput(null);
		}
	}

	@Inject
	public void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) JGenObject genObject) {
		if (genObject != null) {
			ObjTypeCode objTypeCode = ObjTypeHelper.valueOf((short) genObject.getObjTypeCode());
			DivTypeCode divTypeCode = ObjTypeHelper.getDivision(objTypeCode);
			DivTypeCode root = findRoot(divTypeCode);
			treeViewer.setInput(new TopDivTypeCode(root));
			StructuredSelection selection = new StructuredSelection(divTypeCode);
			treeViewer.setSelection(selection);
			selectionService.setSelection(selection);
			tableViewer.setInput(null);
		}
	}
	
	@Inject
	public void setSelection(@Optional @Named(IServiceConstants.ACTIVE_SELECTION) DivTypeCode divTypeCode) {
		if (divTypeCode != null) {
			if(!DivTypeHelper.hasChildren(divTypeCode)) {
				ObjTypeCode objTypeCode = DivTypeHelper.getObject(divTypeCode);
				List<AscTypeCode>  listAssociations = ObjTypeHelper.getAssociations(objTypeCode);
				List<PrpTypeCode>  listProperties = ObjTypeHelper.getProperties(objTypeCode);
				List<Object> combined = new Vector<Object>();
			 	for (AscTypeCode ascTypeCode : listAssociations) {
			 		combined.add(new ItemAsc(objTypeCode,ascTypeCode));
			 	}
			 	for (PrpTypeCode prpTypeCode : listProperties) {
			 		combined.add(new ItemPrp(objTypeCode,prpTypeCode));
			 	}			 	
				tableViewer.setInput(combined.toArray());
			} else {
				tableViewer.setInput(EMPTY_ARRAY);
			}
		}
	}

	private DivTypeCode findRoot(DivTypeCode divTypeCode) {
		if (divTypeCode == DivTypeCode.INVALID) {
			return null;
		}
		if (DivTypeHelper.isRoot(divTypeCode)) {
			return divTypeCode;
		}
		return findRoot(DivTypeHelper.getParent(divTypeCode));
	}

	private static Image getImage(String file) {
		Bundle bundle = FrameworkUtil.getBundle(WalkencyLabelProvider.class);
		URL url = FileLocator.find(bundle, new Path("icons/" + file), null);
		ImageDescriptor image = ImageDescriptor.createFromURL(url);
		return image.createImage();
	}
}