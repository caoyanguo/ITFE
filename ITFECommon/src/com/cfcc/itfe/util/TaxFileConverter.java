package com.cfcc.itfe.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;

public class TaxFileConverter {
	private File newFileName;
	private String[] cols ;
	private void init(){
		cols = new String[12];
	}
	public static void main(String[] args) {
		TaxFileConverter tc = new TaxFileConverter();
		File f = new File("d:/tax/DS04130120010062");
		try {
			tc.convert(f, null,null,null,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String convert(File srcFile,HashMap<String, String> map,HashMap<String, String> assinmap,HashMap<String, String> taxmap,HashMap<String, TsBudgetsubjectDto> budgetmap ) throws Exception{
		BufferedReader br = null;
		BufferedWriter bw = null;
		int i=0;
		try {
			 convertToNewFile(srcFile);
			 br = new BufferedReader(new FileReader(srcFile));
			 bw = new BufferedWriter(new FileWriter(newFileName));
			String line = null;
			while((line=br.readLine()) != null){
				if(line.startsWith("@")){
					convertDetail(line,map, assinmap,taxmap,budgetmap);
					bw.write(StringUtils.join(cols, ',')+System.getProperty("line.separator"));
				}else if(line.startsWith("#")){
					
					init();
					convertMain(line);
				}
			}
			bw.flush();
			
		} catch (FileNotFoundException e) {
			throw new ITFEBizException("File not found exception",e);
		} catch (IOException e) {
			throw new ITFEBizException("IO exception",e);
		}finally{
			if(bw != null){
				try{
					bw.close();
				}catch(Exception e){}
			}
			if(br != null){
				try{
					br.close();
				}catch(Exception e){}
			}
		}
		
		return this.newFileName.getAbsolutePath();
	}

	private void convertToNewFile(File srcFile) throws Exception {
		String fileName = srcFile.getName();//BizTypeConstant.BIZ_TYPE_INCOME
		String path = srcFile.getParent();
		String date = fileName.substring(4, 10);
		String batchnum = fileName.substring(14);
		String newFile = path+System.getProperty("file.separator")+"20"+date+batchnum+BizTypeConstant.BIZ_TYPE_INCOME+"0.txt";
		this.newFileName = new File(newFile);
		if(!this.newFileName.exists())this.newFileName.createNewFile();
	}

	private void convertMain(String line) {
		byte[] bs = line.getBytes();
//		String taxorg = new String(bs,12, 10).trim();
//		this.cols[3] = taxorg;
		String svouno = new String(bs,34, 20).trim();
		this.cols[4] = svouno;
		
	}
	private void convertDetail(String line,HashMap<String, String> map,HashMap<String, String> assinmap,HashMap<String, String> taxmap,HashMap<String, TsBudgetsubjectDto> budgetmap) throws ITFEBizException {
		byte[] bs = line.getBytes();
		String budgetcode = new String(bs,1, 10).trim();
		String budgetvel = new String(bs,11, 1).trim();
		String famt = new String(bs,12, 18).trim();
		String trecode = new String(bs,141, 8).trim();
		String tcbstrecode = map.get(trecode);
		if (null==tcbstrecode) {
			throw new ITFEBizException(trecode+"没有维护国库对应关系表！");
		}
		this.cols[1] = tcbstrecode;
		this.cols[2] = tcbstrecode;
		this.cols[7] = budgetvel;
		this.cols[8] = "1";
		this.cols[9] = budgetcode;
		TsBudgetsubjectDto _dto = budgetmap.get(budgetcode);
		if ("2".equals(_dto.getSbudgettype())) {
			this.cols[8] = "2";
		} 
		//共享级次的业务需要从共享分成参数表中获取辅助标志
		if ("0".equals(budgetvel)) {
			String assign= assinmap.get(budgetcode+tcbstrecode);
			if (null==assign) {
				throw new ITFEBizException(budgetcode+"_"+tcbstrecode+"没有在辅助标志对照表中找到对应的辅助标志！");
			}
			this.cols[10] = assign;
		}
		double a =  Double.valueOf(famt);
		double b =100;
		this.cols[11] =String.valueOf((double) (Math.round(a/b*100)/100.0));
		String taxorgcode = taxmap.get(trecode);
		if (null==taxorgcode) {
			throw new ITFEBizException(trecode+"没有维护地方横联征收机关与TBS征收机关对照关系！");
		}
		this.cols[3]= taxorgcode;
	}

}
