package com.cfcc.itfe.service.sendbiz.exporttbsforbj.processer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.BasicTBSFileProcesser;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.TBSFileResultDto;

/**TBS实拨资金回执处理器**/
public class ProcessYSZC extends BasicTBSFileProcesser{

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
				dto.setTableName(TvPayoutmsgmainDto.tableName());
				dto.setVtcode(MsgConstant.VOUCHER_NO_5207);
				String vouno = line[2]; //凭证编号
				if(StringUtils.isBlank(vouno)) {
					resultDto.getErrorList().add(fileName+":凭证编号不能为空!"); //凭证编号为空则直接返回,不继续处理
					continue;
				}else {
					dto.setVouno(vouno.trim());
				}
				String treCode = line[0]; //国库代码
				if(StringUtils.isBlank(treCode)){
					resultDto.getErrorList().add(fileName+":凭证编号["+vouno+"]的记录中国库代码为空!");
					isUpdate = false;
				}else {
					dto.setTreCode(treCode.trim());
				}
				String acctDate = line[1]; //账务日期
				if(StringUtils.isBlank(acctDate)) {
					resultDto.getErrorList().add(fileName+":凭证编号["+vouno+"]的记录中账务日期为空!");
					isUpdate = false;
				}else {
					dto.setAcctDate(acctDate.trim());
				}
				
				String payerAcct = line[3]; //付款人账号
				dto.setPayerAcct(StringUtils.isBlank(payerAcct)?"":payerAcct.trim());
				
				String payKind = line[4]; //支出种类
				
				if(StringUtils.isBlank(payKind)) {
					dto.setPayKind("1");// 如果支出种类为空,则默认为一般预算支出
					dto.setBizType(BizTypeConstant.BIZ_TYPE_PAY_OUT);
				} else {
					dto.setPayKind(payKind.trim());
					dto.setBizType(payKind.trim().equals("1")?BizTypeConstant.BIZ_TYPE_PAY_OUT:BizTypeConstant.BIZ_TYPE_PAY_OUT2);
				} 
				String famt = line[5]; //金额
				if(StringUtils.isBlank(famt)) {
					resultDto.getErrorList().add(fileName+":凭证编号["+vouno+"]的记录中金额为空!");
					isUpdate = false;
				} else {
					dto.setFamt(new BigDecimal(famt.trim()));
				}
				dto.setPayReason(line[6]); //支出原因不校验
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
			throw new RuntimeException("tbs回执文件处理异常(YSZC)",e);
		}
	}
}
