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

/**直接支付额度的处理类**/
public class Process01 extends BaseTBSFileExportUtil implements IProcessHandler{
	public TbsFileInfo process(List<IDto> mainlist,String fileName) throws ITFEBizException {
		StringBuffer resultStr = new StringBuffer();
		TbsFileInfo fileInfo = new TbsFileInfo();
		int count = mainlist.size();
		BigDecimal totalFamt = new BigDecimal("0.00");
		for (IDto tmp : mainlist) {
			TvDirectpaymsgmainDto dto = (TvDirectpaymsgmainDto) tmp;
			resultStr.append((count>1?"**":"") + dto.getStrecode() + ","); // 国库代码
			resultStr.append(dto.getSpayacctno() + ","); // 付款人帐号
			resultStr.append(dto.getSpayacctname() + ","); // 付款人全称
			resultStr.append("" + ","); // 付款人开户银行
			resultStr.append(dto.getSpayeeacctno() + ","); // 收款人帐号
			resultStr.append(dto.getSpayeeacctname() + ","); // 收款人全称
			resultStr.append(dto.getSpaybankno() + ","); // 收款人开户行
			resultStr.append(dto.getSbudgettype() + ","); // 预算种类代码
			resultStr.append(dto.getNmoney() + ","); // 合计金额
			totalFamt = totalFamt.add(dto.getNmoney());
			resultStr.append(generateVouno(dto.getStaxticketno()) + ","); // 凭证编号
			resultStr.append("" + ","); // 对应凭证编号
			resultStr.append(dto.getSgenticketdate()); // 凭证日期
			resultStr.append("\n");

			List<TvDirectpaymsgsubDto> resultsub = exportTBSForBJService.findSubInfoByMain(dto);
			for (TvDirectpaymsgsubDto subdto : resultsub) {
				resultStr.append(subdto.getSfunsubjectcode() + ",");// 功能科目代码
				if (null==subdto.getSecosubjectcode()|| subdto.getSecosubjectcode().trim().length()<1) {
					resultStr.append("" + ",");// 经济科目代码
				}else{
					resultStr.append(subdto.getSecosubjectcode().trim() + ",");// 经济科目代码
				}
				resultStr.append(subdto.getSagencycode() + ",");// 预算单位代码
				resultStr.append(subdto.getNmoney());// 发生额
				resultStr.append("\n");// 换行
			}
		}
		fileInfo.setFileContent(resultStr.toString());
		fileInfo.setTotalCount(count);
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}
}
