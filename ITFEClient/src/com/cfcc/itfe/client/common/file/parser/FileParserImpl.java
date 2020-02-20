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
	 * 报文信息处理
	 * 
	 * @param String
	 *            filepath 文件路径
	 * @param String
	 *            filename 文件名称
	 * @param String
	 *            orgcode 机构代码
	 * @param ISequenceHelperService
	 *            sequenceHelperService SEQ服务SERVER
	 * @return
	 * @throws ITFEBizException
	 */
	public FileResultDto dealFile(String filepath, String filename, String orgcode,
			ISequenceHelperService sequenceHelperService)
			throws ITFEBizException {
		
		// 根据文件名称取得文件DTO对象 - 然后取业务类型
		FileObjDto fileobj = FileOperFacade.getFileObjByFileName(filename);
		String sbiztype = fileobj.getSbiztype();
		String csourceflag = fileobj.getCsourceflag();
		
		if(MsgConstant.MSG_SOURCE_PLACE.equals(csourceflag)){
			// 地税接口数据格式
			FileResultDto rtndto = new FileResultDto();
			rtndto.setCsourceflag(csourceflag);
			rtndto.setSbiztype(sbiztype);
			rtndto.setSmsgno(MsgConstant.MSG_NO_7211);
			return rtndto;
		}
		if(MsgConstant.MSG_SOURCE_NATION.equals(csourceflag)){
			// 国税接口数据格式
			return 	FileOperFacade.dealIncomeFileForNation(filepath, filename, orgcode);
		}
		if(MsgConstant.MSG_SOURCE_TIPS.equals(csourceflag)){
			// TIPS接口数据格式
			return  FileOperFacade.dealIncomeFileTips(filepath, filename, orgcode);
		}
		
		if(BizTypeConstant.BIZ_TYPE_INCOME.equals(sbiztype)){
			// 收入业务
			return 	FileOperFacade.dealIncomeFile(filepath, filename, orgcode);
		}else if(BizTypeConstant.BIZ_TYPE_PAY_OUT.equals(sbiztype)){
			// 支出业务
			return FileOperFacade.dealPayOutFile(filepath, filename, orgcode);
		}else if(BizTypeConstant.BIZ_TYPE_DIRECT_PAY_PLAN_ZJ.equals(sbiztype)){
			// 直接支付额度业务
			return FileOperFacade.dealDirectPayFile(filepath, filename, orgcode, sequenceHelperService);
		}else if(BizTypeConstant.BIZ_TYPE_GRANT_PAY_PLAN_ZJ.equals(sbiztype)){
			// 授权支付额度业务
			return FileOperFacade.dealGrantPayFile(filepath, filename, orgcode, sequenceHelperService);
		}else{
			throw new ITFEBizException("业务类型[" + sbiztype + "]不对!");
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
