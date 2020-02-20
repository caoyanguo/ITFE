package com.cfcc.itfe.client.sendbiz.exporttbsforbj.processer;

import java.math.BigDecimal;
import java.util.List;

import com.cfcc.itfe.client.sendbiz.exporttbsforbj.BaseTBSFileExportUtil;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.IProcessHandler;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.TbsFileInfo;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgsubDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**ֱ��֧����ȵĴ�����**/
public class Process01 extends BaseTBSFileExportUtil implements IProcessHandler{
	public TbsFileInfo process(List<IDto> mainlist,String fileName) throws ITFEBizException {
		StringBuffer resultStr = new StringBuffer();
		TbsFileInfo fileInfo = new TbsFileInfo();
		int count = mainlist.size();
		BigDecimal totalFamt = new BigDecimal("0.00");
		for (IDto tmp : mainlist) {
			TvDirectpaymsgmainDto dto = (TvDirectpaymsgmainDto) tmp;
			resultStr.append((count>1?"**":"") + dto.getStrecode() + ","); // �������
			resultStr.append(dto.getSpayacctno() + ","); // �������ʺ�
			resultStr.append(dto.getSpayacctname() + ","); // ������ȫ��
			resultStr.append("" + ","); // �����˿�������
			resultStr.append(dto.getSpayeeacctno() + ","); // �տ����ʺ�
			resultStr.append(dto.getSpayeeacctname() + ","); // �տ���ȫ��
			resultStr.append(dto.getSpaybankno() + ","); // �տ��˿�����
			resultStr.append(dto.getSbudgettype() + ","); // Ԥ���������
			resultStr.append(dto.getNmoney() + ","); // �ϼƽ��
			totalFamt = totalFamt.add(dto.getNmoney());
			resultStr.append(generateVouno(dto.getStaxticketno()) + ","); // ƾ֤���
			resultStr.append("" + ","); // ��Ӧƾ֤���
			resultStr.append(dto.getSgenticketdate()); // ƾ֤����
			resultStr.append("\n");

			List<TvDirectpaymsgsubDto> resultsub = exportTBSForBJService.findSubInfoByMain(dto);
			for (TvDirectpaymsgsubDto subdto : resultsub) {
				resultStr.append(subdto.getSfunsubjectcode() + ",");// ���ܿ�Ŀ����
				if (null==subdto.getSecosubjectcode()|| subdto.getSecosubjectcode().trim().length()<1) {
					resultStr.append("" + ",");// ���ÿ�Ŀ����
				}else{
					resultStr.append(subdto.getSecosubjectcode().trim() + ",");// ���ÿ�Ŀ����
				}
				resultStr.append(subdto.getSagencycode() + ",");// Ԥ�㵥λ����
				resultStr.append(subdto.getNmoney());// ������
				resultStr.append("\n");// ����
			}
		}
		fileInfo.setFileContent(resultStr.toString());
		fileInfo.setTotalCount(count);
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}
}
