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
			dwbkLine.append(dto.getSdwbkvoucode()+","); //凭证编号
			dwbkLine.append(dto.getStaxorgcode()+","); //征收机关代码
			dwbkLine.append(dto.getSpayertrecode()+","); //收款国库代码
			dwbkLine.append(","); //目的国库代码
			dwbkLine.append(dto.getCbdglevel()+","); //预算级次（0－共享，1--中央，2--省，3--市，4--区县，5--乡镇）
			dwbkLine.append(dto.getCbdgkind()+","); //预算种类（1-预算内，2－预算外）
			dwbkLine.append(dto.getSbdgsbtcode()+","); //科目代码
			dwbkLine.append(","); //辅助标志
			dwbkLine.append(dto.getSdwbkreasoncode()==null?"":dto.getSdwbkreasoncode()+","); //退库原因代码
			dwbkLine.append(","); //退库依据
			dwbkLine.append(","); //审批机关
			dwbkLine.append(","); //退库比例
			dwbkLine.append(","); //退库总额
			dwbkLine.append(dto.getFamt()+","); //发生额(#.00)
			totalFamt = totalFamt.add(dto.getFamt());
			dwbkLine.append(dto.getCbckflag()+","); //退回标志，0-退库，1-退库退回
			dwbkLine.append(dto.getSpayeeacct()+","); //收款帐号
			dwbkLine.append(dto.getSpayeecode()+"\n"); //收款单位代码
		}
		fileInfo.setFileContent(dwbkLine.toString());
		fileInfo.setTotalCount(mainlist.size());
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}

}
