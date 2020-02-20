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

/**TBSʵ���ʽ��ִ������**/
public class ProcessYSZC extends BasicTBSFileProcesser{

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
				dto.setTableName(TvPayoutmsgmainDto.tableName());
				dto.setVtcode(MsgConstant.VOUCHER_NO_5207);
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
				
				String payerAcct = line[3]; //�������˺�
				dto.setPayerAcct(StringUtils.isBlank(payerAcct)?"":payerAcct.trim());
				
				String payKind = line[4]; //֧������
				
				if(StringUtils.isBlank(payKind)) {
					dto.setPayKind("1");// ���֧������Ϊ��,��Ĭ��Ϊһ��Ԥ��֧��
					dto.setBizType(BizTypeConstant.BIZ_TYPE_PAY_OUT);
				} else {
					dto.setPayKind(payKind.trim());
					dto.setBizType(payKind.trim().equals("1")?BizTypeConstant.BIZ_TYPE_PAY_OUT:BizTypeConstant.BIZ_TYPE_PAY_OUT2);
				} 
				String famt = line[5]; //���
				if(StringUtils.isBlank(famt)) {
					resultDto.getErrorList().add(fileName+":ƾ֤���["+vouno+"]�ļ�¼�н��Ϊ��!");
					isUpdate = false;
				} else {
					dto.setFamt(new BigDecimal(famt.trim()));
				}
				dto.setPayReason(line[6]); //֧��ԭ��У��
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
			throw new RuntimeException("tbs��ִ�ļ������쳣(YSZC)",e);
		}
	}
}
