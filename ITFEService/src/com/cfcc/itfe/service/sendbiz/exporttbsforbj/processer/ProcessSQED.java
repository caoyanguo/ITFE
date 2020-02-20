package com.cfcc.itfe.service.sendbiz.exporttbsforbj.processer;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.BasicTBSFileProcesser;
import com.cfcc.itfe.service.sendbiz.exporttbsforbj.TBSFileResultDto;

/**TBS��Ȩ֧����Ȼ�ִ������**/
public class ProcessSQED extends BasicTBSFileProcesser{

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
				dto.setTableName(TvGrantpaymsgmainDto.tableName());
				dto.setVtcode(MsgConstant.VOUCHER_NO_5106);
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
				
				String sagentbnkcode = line[3]; //���������к�
				if(StringUtils.isBlank(sagentbnkcode)) {
					resultDto.getErrorList().add(fileName+":ƾ֤���["+vouno+"]�ļ�¼�д�������Ϊ��!");
					isUpdate = false;
				}else {
					dto.setSagentbnkcode(sagentbnkcode.trim());
				}
				String famt = line[4]; //���
				if(StringUtils.isBlank(famt)) {
					resultDto.getErrorList().add(fileName+":ƾ֤���["+vouno+"]�ļ�¼�н��Ϊ��!");
					isUpdate = false;
				} else {
					dto.setFamt(new BigDecimal(famt.trim()));
				}
				String smallFamt = line[5]; //С���ֽ�����
				dto.setSmallFamt(new BigDecimal(smallFamt));
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
			throw new RuntimeException("tbs��ִ�ļ������쳣(SQED)",e);
		}
	}
}
