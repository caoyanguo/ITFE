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

/**处理授权支付清算**/
public class Process27 extends BaseTBSFileExportUtil implements IProcessHandler{
/*
国库代码,付款人全称,付款人帐号,付款人开户行,付款人开户行行号,收款人全称,收款人帐号,收款人开户行,收款人开户行行号,预算种类代码,零余额发生额,小额现金发生额,摘要代码,凭证编号,凭证日期
预算单位代码,功能科目代码,经济科目代码,帐户性质,支付金额
*/
	@SuppressWarnings("unchecked")
	public TbsFileInfo process(List<IDto> mainlist, String fileName) throws ITFEBizException {
		StringBuffer grantPayResultStr = new StringBuffer();
		TbsFileInfo fileInfo = new TbsFileInfo();
		BigDecimal totalFamt = new BigDecimal("0.00");
		for (IDto tmp : mainlist) {
			TvPayreckBankDto dto = (TvPayreckBankDto) tmp;
			grantPayResultStr.append("**" + dto.getStrecode().toString() + ","); // 国库代码
			grantPayResultStr.append(dto.getSpayername() + ","); // 付款人全称
			grantPayResultStr.append(dto.getSpayeracct() + ","); // 付款人帐号
			grantPayResultStr.append("" + ","); // 付款人开户行
			grantPayResultStr.append("" + ","); // 付款人开户行行号
			grantPayResultStr.append(dto.getSpayeename() + ","); // 收款人全称
			grantPayResultStr.append(","); // 收款人账号
			grantPayResultStr.append(","); // 收款人开户行
			grantPayResultStr.append(dto.getSagentbnkcode() + ","); // 收款人开户行行号
			grantPayResultStr.append(dto.getSbudgettype() + ","); // 预算种类代码
			grantPayResultStr.append(dto.getFamt() + ","); // 零余额发生额
			totalFamt = totalFamt.add(dto.getFamt());
			grantPayResultStr.append("" + ","); // 小额现金发生额
			grantPayResultStr.append("" + ","); // 摘要代码
			grantPayResultStr.append(generateVouno(dto.getSvouno()) + ","); // 凭证编号
			grantPayResultStr.append(TimeFacade.formatDate(dto.getDvoudate(),
					"yyyymmdd")); // 凭证日期
			grantPayResultStr.append("\n");

			List<TvPayreckBankListDto> resultsub = exportTBSForBJService.findSubInfoByMain(dto);
			for (TvPayreckBankListDto subdto : resultsub) {
				grantPayResultStr.append(subdto.getSbdgorgcode() + ",");// 预算单位代码
				grantPayResultStr.append(subdto.getSfuncbdgsbtcode() + ",");// 功能科目代码
				if (null==subdto.getSecnomicsubjectcode()|| subdto.getSecnomicsubjectcode().trim().length()<1) {
					grantPayResultStr.append("" + ",");// 经济科目代码
				}else{
					grantPayResultStr.append(subdto.getSecnomicsubjectcode().trim() + ",");// 经济科目代码
				}
				grantPayResultStr.append(subdto.getSacctprop() + ",");// 帐户性质
				grantPayResultStr.append(subdto.getFamt());// 支付金额
				grantPayResultStr.append("\n");// 换行
			}
		}
		fileInfo.setFileContent(grantPayResultStr.toString());
		fileInfo.setTotalCount(mainlist.size());
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}
}
