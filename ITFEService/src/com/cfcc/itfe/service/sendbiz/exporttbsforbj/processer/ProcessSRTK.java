package com.cfcc.itfe.service.sendbiz.exporttbsforbj.processer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.BasicTBSFileProcesser;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.TBSFileResultDto;

/**TBS收入退库回执处理器**/
public class ProcessSRTK extends BasicTBSFileProcesser{

	public MulitTableDto process(String fullFileName) {
		MulitTableDto resultDto = new MulitTableDto();
		try {
			List<String[]> fileContent = readFile(fullFileName, ",");
			String fileName = new File(fullFileName).getName();
			if(fileContent==null || fileContent.size()==0) {
				resultDto.getErrorList().add(fileName+":文件内容为空!");
				return resultDto;
			}
			List<TBSFileResultDto> list = new ArrayList<TBSFileResultDto>();
			for(int i=0; i<fileContent.size(); i++) {
				String[] line = fileContent.get(i);
				boolean isUpdate = true; //是否更新
				TBSFileResultDto dto = new TBSFileResultDto();
				dto.setTableName(TvDwbkDto.tableName());
				dto.setVtcode(MsgConstant.VOUCHER_NO_5209);
				String vouno = line[0]; //凭证编号
				if(StringUtils.isBlank(vouno)) {
					resultDto.getErrorList().add(fileName+":凭证编号不能为空!"); //凭证编号为空则直接返回,不继续处理
					continue;
				}else {
					dto.setVouno(vouno.trim());
				}
				
				String acctDate = line[1]; //账务日期
				if(StringUtils.isBlank(acctDate)) {
					resultDto.getErrorList().add(fileName+":凭证编号["+vouno+"]的记录中账务日期为空!");
					isUpdate = false;
				}else {
					dto.setAcctDate(acctDate.trim());
				}
				
				String treCode = line[2]; //收款国库代码
				if(StringUtils.isBlank(treCode)){
					resultDto.getErrorList().add(fileName+":凭证编号["+vouno+"]的记录中国库代码为空!");
					isUpdate = false;
				}else {
					dto.setTreCode(treCode.trim());
				}
				
				String cbdglevel = line[3];
				dto.setCbdglevel(cbdglevel); //预算级次
				
				String famt = line[4]; //发生额
				if(StringUtils.isBlank(famt)) {
					resultDto.getErrorList().add(fileName+":凭证编号["+vouno+"]的记录中金额为空!");
					isUpdate = false;
				} else {
					dto.setFamt(new BigDecimal(famt.trim()));
				}
				
				String backFlag = line[5]; //退回标志
				if(StringUtils.isBlank(backFlag)) { 
					//如果为空，暂不处理
				}else {
					dto.setBackFlag(backFlag);
				}
				
				/**只有isUpdate为true才进行更新操作**/
				if(isUpdate) {
					list.add(dto);
				}
			}
			/**根据回执文件更新索引表状态**/
			if(list.size()>0) {
				updateVouStatusBySlip(list);
			}
			return resultDto;
		} catch (Exception e) {
			throw new RuntimeException("tbs回执文件处理异常(SRTK)",e);
		}
	}
}
