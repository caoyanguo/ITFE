package com.cfcc.itfe.client.sendbiz.exporttbsforbj.processer;

import java.math.BigDecimal;
import java.util.List;

import com.cfcc.itfe.client.sendbiz.exporttbsforbj.BaseTBSFileExportUtil;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.IProcessHandler;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.TbsFileInfo;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankListDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**����ֱ��֧������**/
public class Process25 extends BaseTBSFileExportUtil implements IProcessHandler{
/*
�������,������ȫ��,�������ʺ�,�����˿�����,�����˿������к�,�տ���ȫ��,�տ����ʺ�,�տ��˿�����,�տ��˿������к�,Ԥ���������,��������,С���ֽ�����,ժҪ����,ƾ֤���,ƾ֤����
Ԥ�㵥λ����,���ܿ�Ŀ����,���ÿ�Ŀ����,�ʻ�����,֧�����
*/
	@SuppressWarnings("unchecked")
	public TbsFileInfo process(List<IDto> mainlist, String fileName) throws ITFEBizException {
		StringBuffer directpayresultStr = new StringBuffer();
		TbsFileInfo fileInfo = new TbsFileInfo();
		BigDecimal totalFamt = new BigDecimal("0.00");
		for (IDto tmp : mainlist) {
			TvPayreckBankDto dto = (TvPayreckBankDto) tmp;
			directpayresultStr.append("**" + dto.getStrecode().toString() + ","); // �������
			directpayresultStr.append(","); // ������ȫ��
			directpayresultStr.append(dto.getSpayeracct() + ","); // �������ʺ�
			directpayresultStr.append("" + ","); // �����˿�����
			directpayresultStr.append("" + ","); // �����˿������к�
			directpayresultStr.append(dto.getSpayeename() + ","); // �տ���ȫ��
			directpayresultStr.append(dto.getSpayeeacct() + ","); // �տ����˺�
			directpayresultStr.append("" + ","); // �տ��˿�����
			directpayresultStr.append(dto.getSagentbnkcode() + ","); // �տ��˿������к�(��������)
			directpayresultStr.append(dto.getSbudgettype() + ","); // Ԥ���������
			directpayresultStr.append(dto.getFamt() + ","); // ��������
			totalFamt = totalFamt.add(dto.getFamt());
			directpayresultStr.append("" + ","); // С���ֽ�����
			directpayresultStr.append("" + ","); // ժҪ����
			directpayresultStr.append(generateVouno(dto.getSvouno()) + ","); // ƾ֤���
			directpayresultStr.append(TimeFacade.formatDate(dto.getDvoudate(),
					"yyyymmdd")); // ƾ֤����
			directpayresultStr.append("\n");

			List<TvPayreckBankListDto> resultsub = exportTBSForBJService.findSubInfoByMain(dto);
			for (TvPayreckBankListDto subdto : resultsub) {
				directpayresultStr.append(subdto.getSbdgorgcode() + ",");// Ԥ�㵥λ����
				directpayresultStr.append(subdto.getSfuncbdgsbtcode() + ",");// ���ܿ�Ŀ����
				if (null==subdto.getSecnomicsubjectcode()|| subdto.getSecnomicsubjectcode().trim().length()<1) {
					directpayresultStr.append("" + ",");// ���ÿ�Ŀ����
				}else{
					directpayresultStr.append(subdto.getSecnomicsubjectcode().trim() + ",");// ���ÿ�Ŀ����
				}
				directpayresultStr.append(subdto.getSacctprop() + ",");// �ʻ�����
				directpayresultStr.append(subdto.getFamt());// ֧�����
				directpayresultStr.append("\n");// ����
			}
		}
		fileInfo.setFileContent(directpayresultStr.toString());
		fileInfo.setTotalCount(mainlist.size());
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}
}
