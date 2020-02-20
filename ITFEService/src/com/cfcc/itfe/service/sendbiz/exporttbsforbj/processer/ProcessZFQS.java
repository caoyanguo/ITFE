package com.cfcc.itfe.service.sendbiz.exporttbsforbj.processer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.BasicTBSFileProcesser;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.TBSFileResultDto;

/**TBS����֧�������ִ������**/
public class ProcessZFQS extends BasicTBSFileProcesser{

	public MulitTableDto process(String fullFileName) {
		MulitTableDto resultDto = new MulitTableDto();
		try {
			List<String[]> fileContent = readFile(fullFileName, ",");
			String fileName = new File(fullFileName).getName();
			if(fileContent==null || fileContent.size()==0) {
				resultDto.getErrorList().add(fileName+":�ļ�����Ϊ��!");
				return resultDto;
			}
			List<TBSFileResultDto> list = new ArrayList<TBSFileResultDto>();
			for(int i=0; i<fileContent.size(); i++) {
				String[] line = fileContent.get(i);
				boolean isUpdate = true; //�Ƿ����
				TBSFileResultDto dto = new TBSFileResultDto();
				dto.setTableName(TvPayreckBankDto.tableName());
				dto.setVtcode(MsgConstant.VOUCHER_NO_2301);
				String vouno = line[2]; //ƾ֤���
				if(StringUtils.isBlank(vouno)) {
					resultDto.getErrorList().add(fileName+":ƾ֤��Ų���Ϊ��!"); //ƾ֤���Ϊ����ֱ�ӷ���,����������
					continue;
				}else {
					dto.setVouno(vouno.trim());
				}
				String treCode = line[0]; //�������
				if(StringUtils.isBlank(treCode)){
					resultDto.getErrorList().add(fileName+":ƾ֤���["+vouno+"]�ļ�¼�й������Ϊ��!");
					isUpdate = false;
				}else {
					dto.setTreCode(treCode.trim());
				}
				String acctDate = line[1]; //��������
				if(StringUtils.isBlank(acctDate)) {
					resultDto.getErrorList().add(fileName+":ƾ֤���["+vouno+"]�ļ�¼����������Ϊ��!");
					isUpdate = false;
				}else {
					dto.setAcctDate(acctDate.trim());
				}
				
				String vouDate = line[3]; //ƾ֤����
				
				String sagentbnkcode = line[4]; //���������к�
				if(StringUtils.isBlank(sagentbnkcode)) {
					resultDto.getErrorList().add(fileName+":ƾ֤���["+vouno+"]�ļ�¼�д�������Ϊ��!");
					isUpdate = false;
				}else {
					dto.setSagentbnkcode(sagentbnkcode.trim());
				}
				
				String payerAcct = line[5];
				dto.setPayerAcct(payerAcct); //�������˺�
				
				String payKind = line[6]; //֧������
				if(StringUtils.isBlank(payKind)){
					dto.setBizType(BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
				}else {
					dto.setBizType(payKind.trim().equals("1") ? BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY
									: payKind.trim().equals("2") ? BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_GRANT_PAY
																	: BizTypeConstant.BIZ_TYPE_PAYRECK_BANK_DIRECT_PAY);
				}
				
				String famt = line[7]; //���
				if(StringUtils.isBlank(famt)) {
					resultDto.getErrorList().add(fileName+":ƾ֤���["+vouno+"]�ļ�¼�н��Ϊ��!");
					isUpdate = false;
				} else {
					dto.setFamt(new BigDecimal(famt.trim()));
				}
				/**ֻ��isUpdateΪtrue�Ž��и��²���**/
				if(isUpdate) {
					list.add(dto);
				}
			}
			/**���ݻ�ִ�ļ�����������״̬**/
			if(list.size()>0) {
				updateVouStatusBySlip(list);
			}
			return resultDto;
		} catch (Exception e) {
			throw new RuntimeException("tbs��ִ�ļ������쳣(ZFQS)",e);
		}
	}
}
