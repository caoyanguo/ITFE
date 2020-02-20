package com.cfcc.itfe.client.sendbiz.exporttbsforbj.processer;

import java.math.BigDecimal;
import java.util.List;

import com.cfcc.itfe.client.sendbiz.exporttbsforbj.IProcessHandler;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.TbsFileInfo;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class Process13 implements IProcessHandler {
	public TbsFileInfo process(List<IDto> mainlist, String fileName)
			throws ITFEBizException {
		StringBuffer dwbkLine = new StringBuffer();
		TbsFileInfo fileInfo = new TbsFileInfo();
		BigDecimal totalFamt = new BigDecimal("0.00");
		for (IDto tmp : mainlist) {
			TvDwbkDto dto = (TvDwbkDto) tmp;
			dwbkLine.append(dto.getSdwbkvoucode()+","); //ƾ֤���
			dwbkLine.append(dto.getStaxorgcode()+","); //���ջ��ش���
			dwbkLine.append(dto.getSpayertrecode()+","); //�տ�������
			dwbkLine.append(","); //Ŀ�Ĺ������
			dwbkLine.append(dto.getCbdglevel()+","); //Ԥ�㼶�Σ�0������1--���룬2--ʡ��3--�У�4--���أ�5--����
			dwbkLine.append(dto.getCbdgkind()+","); //Ԥ�����ࣨ1-Ԥ���ڣ�2��Ԥ���⣩
			dwbkLine.append(dto.getSbdgsbtcode()+","); //��Ŀ����
			dwbkLine.append(","); //������־
			dwbkLine.append(dto.getSdwbkreasoncode()==null?"":dto.getSdwbkreasoncode()+","); //�˿�ԭ�����
			dwbkLine.append(","); //�˿�����
			dwbkLine.append(","); //��������
			dwbkLine.append(","); //�˿����
			dwbkLine.append(","); //�˿��ܶ�
			dwbkLine.append(dto.getFamt()+","); //������(#.00)
			totalFamt = totalFamt.add(dto.getFamt());
			dwbkLine.append(dto.getCbckflag()+","); //�˻ر�־��0-�˿⣬1-�˿��˻�
			dwbkLine.append(dto.getSpayeeacct()+","); //�տ��ʺ�
			dwbkLine.append(dto.getSpayeecode()+"\n"); //�տλ����
		}
		fileInfo.setFileContent(dwbkLine.toString());
		fileInfo.setTotalCount(mainlist.size());
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}

}
