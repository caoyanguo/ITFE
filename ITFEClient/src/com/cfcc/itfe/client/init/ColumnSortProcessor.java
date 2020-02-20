package com.cfcc.itfe.client.init;

import com.cfcc.jaf.ui.emf.defautvalue.IMetaDataDefaultValueProcessor;
import com.cfcc.jaf.ui.metadata.ColumnMetaData;
import com.cfcc.jaf.ui.metadata.ControlMetaData;
import com.cfcc.jaf.ui.processor.IMetaDataProcessor;

public class ColumnSortProcessor implements IMetaDataDefaultValueProcessor{

	public void inject(Object arg0) {
	if(arg0 instanceof ColumnMetaData){
		
		ColumnMetaData column = (ColumnMetaData) arg0;
		
		//column.sorter ="all";
		column.sortrule="all";
		
	}
	}

	
}
