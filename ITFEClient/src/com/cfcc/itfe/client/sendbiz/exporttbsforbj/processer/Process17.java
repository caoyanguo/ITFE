package com.cfcc.itfe.client.sendbiz.exporttbsforbj.processer;

import java.math.BigDecimal;
import java.util.List;

import com.cfcc.itfe.client.sendbiz.exporttbsforbj.BaseTBSFileExportUtil;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.IProcessHandler;
import com.cfcc.itfe.client.sendbiz.exporttbsforbj.TbsFileInfo;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**����һ��Ԥ��֧��**/
public class Process17 extends BaseTBSFileExportUtil implements IProcessHandler{

	/*
	 �ֶδ���	�ֶ�����			�ֶ�����							��ע
	----------------------------------------------------------------------------
	gkdm	varchar(10)		�������	
	fkzh	nvarchar(32)	�����˺�	
	dbyydm	nvarchar(10)    ֧��ԭ�����	
	ysdw	nvarchar(15)	Ԥ�㵥λ����						����ͺ���ϵͳ�ж�Ӧ
	skzh	nvarchar(32)	�տλ�˺�	
	yszl	nchar(1)		Ԥ�����ࣨ1��Ԥ���ڣ�2��Ԥ���⣩	����Ϊ1����2
	kmdm	nvarchar(30)	���ܿ�Ŀ����	
	Jjdm	Nvarchar(30)	���ÿ�Ŀ����	
	kjkmdm  nvarchar(32)    ��ƿ�Ŀ����						����Ϊ��
	zczl 	nchar(3) 		֧�����ࣨ1��һ��֧����2��������	
	fse		numeric(18,2)	������(#.00)						����С�������λ
	pzbh	nvarchar(8)		ƾ֤����							�����ظ�

	TODO:����һ��ʵ���ʽ�ƾ֤�������ϸ�����(��ע�����ϲ���������ʱ������һ�Զ�����)
	 */
	@SuppressWarnings("unchecked")
	public TbsFileInfo process(List<IDto> mainlist, String fileName)
			throws ITFEBizException {
		//���� һ��֧��
		StringBuffer payout = new StringBuffer("");
		TbsFileInfo fileInfo = new TbsFileInfo();
		BigDecimal totalFamt = new BigDecimal("0.00");
		for (IDto tmp : mainlist) {
			TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) tmp;
			List<TvPayoutmsgsubDto> resultsub = exportTBSForBJService.findSubInfoByMain(dto);
			TvPayoutmsgsubDto subdto = resultsub.get(0);//(�����Ƕ����ϸ�������Ĭ��ȥ��һ��)
			payout.append(dto.getStrecode() + ","); // �������
			payout.append(dto.getSpayeracct() + ",");// �����˺�
			payout.append(",");// ֧��ԭ�����
			payout.append(dto.getSbudgetunitcode() + ",");// Ԥ�㵥λ����
			payout.append(",");// �տλ�˺�(�ӿڿ�Ϊ�գ��Ͳ���)
			payout.append(dto.getSbudgettype() + ",");// Ԥ������
			payout.append(subdto.getSfunsubjectcode() + ",");// ���ܿ�Ŀ����
			payout.append((subdto.getSecnomicsubjectcode()==null?"":subdto.getSecnomicsubjectcode()) + ",");// ���ÿ�Ŀ����
			payout.append(",");// ��ƿ�Ŀ����
			payout.append("1,");// ֧������ 
			payout.append(subdto.getNmoney() + ",");// ������
			totalFamt = totalFamt.add(subdto.getNmoney());
			payout.append(generateVouno(dto.getStaxticketno()));// ƾ֤����
			payout.append("\n");
		}
		fileInfo.setFileContent(payout.toString());
		fileInfo.setTotalCount(mainlist.size());
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}
}
