package com.cfcc.itfe.client.sendbiz.exporttbsforbj.processer;

import java.math.BigDecimal;
import java.util.List;

import com.cfcc.itfe.client.sendbiz.exporttbsforbj.BaseTBSFileExportUtil;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.IProcessHandler;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.TbsFileInfo;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgsubDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**������Ȩ֧�����**/
public class Process02 extends BaseTBSFileExportUtil implements IProcessHandler {
	@SuppressWarnings("unchecked")
	public TbsFileInfo process(List<IDto> mainlist, String fileName)throws ITFEBizException {
		StringBuffer resultStr = new StringBuffer();
		TbsFileInfo fileInfo = new TbsFileInfo();
		int count = mainlist.size();
		BigDecimal totalFamt = new BigDecimal("0.00");
		for (IDto tmp : mainlist) {
			TvGrantpaymsgmainDto dto = (TvGrantpaymsgmainDto) tmp;
			resultStr.append((count>1?"**":"")+dto.getStrecode().toString() + ","); // �������
			resultStr.append(generateVouno(dto.getSpackageticketno()) + ","); // ƾ֤���
			resultStr.append(dto.getSpaybankno() + ","); // �������д���
			resultStr.append(dto.getSbudgetunitcode() + ","); // Ԥ�㵥λ����
			resultStr.append(dto.getSofmonth() + ","); // �����·�
			resultStr.append(dto.getSbudgettype() + ","); // Ԥ������
			resultStr.append(dto.getNmoney() + ","); // ��������
			totalFamt = totalFamt.add(dto.getNmoney());
			resultStr.append("0.00"); // С���ֽ�����
			resultStr.append("\n");

			List<TvGrantpaymsgsubDto> resultsub = exportTBSForBJService.findSubInfoByMain(dto);
			for (TvGrantpaymsgsubDto subdto : resultsub) {
				resultStr.append(subdto.getSfunsubjectcode() + ",");// ���ܿ�Ŀ����
				if (null==subdto.getSecosubjectcode()|| subdto.getSecosubjectcode().trim().length()<1) {
					resultStr.append("" + ",");// ���ÿ�Ŀ����
				}else{
					resultStr.append(subdto.getSecosubjectcode().trim() + ",");// ���ÿ�Ŀ����
				}
				resultStr.append(subdto.getNmoney() + ",");// ��������
				resultStr.append("0.00");// С���ֽ�����
				resultStr.append("\n");// ����
			}
		}
		fileInfo.setFileContent(resultStr.toString());
		fileInfo.setTotalCount(count);
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}

}
