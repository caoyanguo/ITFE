package com.cfcc.model;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

import com.cfcc.devplatform.model.JComponent;
import com.cfcc.devplatform.model.JUIComponent;
import com.cfcc.devplatform.model.ModelPackage;
import com.cfcc.devplatform.utils.ide.EMFResourceUitl;
import com.cfcc.jaf.ui.util.FileUtil;

public abstract class AbstractModelGenerator implements IModelGenerator {

	protected ResourceSet resourceSet = new ResourceSetImpl();
	protected File modelFileDir;

	protected JUIComponent findUICompinent(String uicomponentename) {
		for (TreeIterator<?> i = resourceSet.getAllContents(); i.hasNext();) {
			Object child = i.next();
			if (child instanceof JComponent) {
				JUIComponent ui = (JUIComponent) child;
				if (ui.getEname().equals(uicomponentename)) {
					return ui;
				}
			}

		}
		return null;
	}

	protected JUIComponent findUICompinent() {
		for (TreeIterator<?> i = resourceSet.getAllContents(); i.hasNext();) {
			Object child = i.next();
			if (child instanceof JComponent) {
				return (JUIComponent) child;
			}
		}
		return null;
	}

	protected void initpath(String modelpath) {
		modelFileDir = new File(modelpath);
		registerResource();
		loadResource(getSystemFile(modelFileDir));
	}

	protected void initfile(String amodelfile) {
		File modelfile = new File(amodelfile);
		URI uri = URI.createFileURI(modelfile.getPath());
		resourceSet = new ResourceSetImpl();
		registerResource();
		resourceSet.getResource(uri, true);
	}

	private void loadResource(List<File> fileList) {
		for (Iterator<File> iterator = fileList.iterator(); iterator.hasNext();) {
			File file = iterator.next();
			URI uri = URI.createFileURI(file.getPath());
			resourceSet.getResource(uri, true);
		}
	}

	private List<File> getSystemFile(File modelFileDir2) {
		// TODO Auto-generated method stub
		return FileUtil.getModelFilesInDir(modelFileDir);
	}

	private void registerResource() {
		// TODO Auto-generated method stub
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(Resource.Factory.Registry.DEFAULT_EXTENSION,
						new XMIResourceFactoryImpl());
		resourceSet.getPackageRegistry().put(ModelPackage.eNS_URI,
				ModelPackage.eINSTANCE);
		resourceSet.getPackageRegistry().put(
				com.cfcc.devplatform.model.ModelPackage.eNS_URI,
				com.cfcc.devplatform.model.ModelPackage.eINSTANCE);
	}

	protected void save(JUIComponent ui) throws IOException {
		ui.eResource().save(EMFResourceUitl.saveoptions);
	}

}
