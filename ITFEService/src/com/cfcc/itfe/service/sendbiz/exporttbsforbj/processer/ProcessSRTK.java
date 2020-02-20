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

/**TBS�����˿��ִ������**/
public class ProcessSRTK extends BasicTBSFileProcesser{

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
				dto.setTableName(TvDwbkDto.tableName());
				dto.setVtcode(MsgConstant.VOUCHER_NO_5209);
				String vouno = line[0]; //ƾ֤���
				if(StringUtils.isBlank(vouno)) {
					resultDto.getErrorList().add(fileName+":ƾ֤��Ų���Ϊ��!"); //ƾ֤���Ϊ����ֱ�ӷ���,����������
					continue;
				}else {
					dto.setVouno(vouno.trim());
				}
				
				String acctDate = line[1]; //��������
				if(StringUtils.isBlank(acctDate)) {
					resultDto.getErrorList().add(fileName+":ƾ֤���["+vouno+"]�ļ�¼����������Ϊ��!");
					isUpdate = false;
				}else {
					dto.setAcctDate(acctDate.trim());
				}
				
				String treCode = line[2]; //�տ�������
				if(StringUtils.isBlank(treCode)){
					resultDto.getErrorList().add(fileName+":ƾ֤���["+vouno+"]�ļ�¼�й������Ϊ��!");
					isUpdate = false;
				}else {
					dto.setTreCode(treCode.trim());
				}
				
				String cbdglevel = line[3];
				dto.setCbdglevel(cbdglevel); //Ԥ�㼶��
				
				String famt = line[4]; //������
				if(StringUtils.isBlank(famt)) {
					resultDto.getErrorList().add(fileName+":ƾ֤���["+vouno+"]�ļ�¼�н��Ϊ��!");
					isUpdate = false;
				} else {
					dto.setFamt(new BigDecimal(famt.trim()));
				}
				
				String backFlag = line[5]; //�˻ر�־
				if(StringUtils.isBlank(backFlag)) { 
					//���Ϊ�գ��ݲ�����
				}else {
					dto.setBackFlag(backFlag);
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
			throw new RuntimeException("tbs��ִ�ļ������쳣(SRTK)",e);
		}
	}
}
