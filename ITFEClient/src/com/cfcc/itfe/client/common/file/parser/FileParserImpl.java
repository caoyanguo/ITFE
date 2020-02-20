package com.cfcc.itfe.client.common.file.parser;

import com.cfcc.itfe.client.common.file.FileOperFacade;
import com.cfcc.itfe.client.common.file.core.IFileParser;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.FileObjDto;
import com.cfcc.itfe.persistence.dto.FileResultDto;
import com.cfcc.itfe.service.commonsubsys.commondbaccess.ISequenceHelperService;

public class FileParserImpl implements IFileParser{

	
	/**
	 * ������Ϣ����
	 * 
	 * @param String
	 *            filepath �ļ�·��
	 * @param String
	 *            filename �ļ�����
	 * @param String
	 *            orgcode ��������
	 * @param ISequenceHelperService
	 *            sequenceHelperService SEQ����SERVER
	 * @return
	 * @throws ITFEBizException
	 */
	public FileResultDto dealFile(String filepath, String filename, String orgcode,
			ISequenceHelperService sequenceHelperService)
			throws ITFEBizException {
		
		// �����ļ�����ȡ���ļ�DTO���� - Ȼ��ȡҵ������
		FileObjDto fileobj = FileOperFacade.getFileObjByFileName(filename);
		String sbiztype = fileobj.getSbiztype();
		String csourceflag = fileobj.getCsourceflag();
		
		if(MsgConstant.MSG_SOURCE_PLACE.equals(csourceflag)){
			// ��˰�ӿ����ݸ�ʽ
			FileResultDto rtndto = new FileResultDto();
			rtndto.setCsourceflag(csourceflag);
			rtndto.setSbiztype(sbiztype);
			rtndto.setSmsgno(MsgConstant.MSG_NO_7211);
			return rtndto;
		}
		if(MsgConstant.MSG_SOURCE_NATION.equals(csourceflag)){
			// ��˰�ӿ����ݸ�ʽ
			return 	FileOperFacade.dealIncomeFileForNation(filepath, filename, orgcode);
		}
		if(MsgConstant.MSG_SOURCE_TIPS.equals(csourceflag)){
			// TIPS�ӿ����ݸ�ʽ
			return  FileOperFacade.dealIncomeFileTips(filepath, filename, orgcode);
		}
		
		if(BizTypeConstant.BIZ_TYPE_INCOME.equals(sbiztype)){
			// ����ҵ��
			return 	FileOperFacade.dealIncomeFile(filepath, filename, orgcode);
		}else if(BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(sbiztype)){
			// ֧��ҵ��
			return FileOperFacade.dealPayOutFile(filepath, filename, orgcode);
		}else if(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN_ZJ.equals(sbiztype)){
			// ֱ��֧�����ҵ��
			return FileOperFacade.dealDirectPayFile(filepath, filename, orgcode, sequenceHelperService);
		}else if(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN_ZJ.equals(sbiztype)){
			// ��Ȩ֧�����ҵ��
			return FileOperFacade.dealGrantPayFile(filepath, filename, orgcode, sequenceHelperService);
		}else{
			throw new ITFEBizException("ҵ������[" + sbiztype + "]����!");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
