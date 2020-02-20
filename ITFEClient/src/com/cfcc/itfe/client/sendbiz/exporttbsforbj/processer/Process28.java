package com.cfcc.itfe.client.sendbiz.exporttbsforbj.processer;

import java.math.BigDecimal;
import java.util.List;

import com.cfcc.itfe.client.sendbiz.exporttbsforbj.BaseTBSFileExportUtil;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.IProcessHandler;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.TbsFileInfo;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackListDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**������Ȩ֧�������˻�**/
public class Process28 extends BaseTBSFileExportUtil implements IProcessHandler{
/*
�������,������ȫ��,�������ʺ�,�����˿�����,�����˿������к�,�տ���ȫ��,�տ����ʺ�,�տ��˿�����,�տ��˿������к�,Ԥ���������,��������,С���ֽ�����,ժҪ����,ƾ֤���,ƾ֤����
Ԥ�㵥λ����,���ܿ�Ŀ����,���ÿ�Ŀ����,�ʻ�����,֧�����
*/
	@SuppressWarnings("unchecked")
	public TbsFileInfo process(List<IDto> mainlist, String fileName) throws ITFEBizException {
		StringBuffer grantPayResultStr = new StringBuffer();
		TbsFileInfo fileInfo = new TbsFileInfo();
		BigDecimal totalFamt = new BigDecimal("0.00");
		for (IDto tmp : mainlist) {
			TvPayreckBankBackDto dto = (TvPayreckBankBackDto) tmp;
			grantPayResultStr.append("**" + dto.getStrecode().toString() + ","); // �������
			grantPayResultStr.append(dto.getSpayername() + ","); // ������ȫ��
			grantPayResultStr.append(dto.getSpayeracct() + ","); // �������ʺ�
			grantPayResultStr.append("" + ","); // �����˿�����
			grantPayResultStr.append("" + ","); // �����˿������к�
			grantPayResultStr.append(dto.getSpayeename() + ","); // �տ���ȫ��
			grantPayResultStr.append(dto.getSpayeeacct() + ","); // �տ����˺�
			grantPayResultStr.append("" + ","); // �տ��˿�����
			grantPayResultStr.append(dto.getSagentbnkcode() + ","); // �տ��˿������к�
			grantPayResultStr.append(dto.getSbudgettype() + ","); // Ԥ���������
			BigDecimal absFamt = dto.getFamt().abs(); //dto.getFamt().signum()==-1?dto.getFamt().negate():dto.getFamt();
			grantPayResultStr.append(absFamt + ","); // ��������
			totalFamt = totalFamt.add(absFamt);
			grantPayResultStr.append("" + ","); // С���ֽ�����
			grantPayResultStr.append("" + ","); // ժҪ����
			grantPayResultStr.append(generateVouno(dto.getSvouno()) + ","); // ƾ֤���
			grantPayResultStr.append(TimeFacade.formatDate(dto.getDvoudate(),
					"yyyymmdd")); // ƾ֤����
			grantPayResultStr.append("\n");

			List<TvPayreckBankBackListDto> resultsub = exportTBSForBJService.findSubInfoByMain(dto);
			for (TvPayreckBankBackListDto subdto : resultsub) {
				grantPayResultStr.append(subdto.getSbdgorgcode() + ",");// Ԥ�㵥λ����
				grantPayResultStr.append(subdto.getSfuncbdgsbtcode() + ",");// ���ܿ�Ŀ����
				if (null==subdto.getSecnomicsubjectcode()|| subdto.getSecnomicsubjectcode().trim().length()<1) {
					grantPayResultStr.append("" + ",");// ���ÿ�Ŀ����
				}else{
					grantPayResultStr.append(subdto.getSecnomicsubjectcode().trim() + ",");// ���ÿ�Ŀ����
				}
				grantPayResultStr.append(subdto.getSacctprop() + ",");// �ʻ�����
				grantPayResultStr.append(subdto.getFamt());// ֧�����
				grantPayResultStr.append("\n");// ����
			}
		}
		fileInfo.setFileContent(grantPayResultStr.toString());
		fileInfo.setTotalCount(mainlist.size());
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}
}
