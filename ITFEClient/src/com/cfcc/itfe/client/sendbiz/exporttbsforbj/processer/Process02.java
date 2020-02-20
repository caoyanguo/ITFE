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

/**处理授权支付额度**/
public class Process02 extends BaseTBSFileExportUtil implements IProcessHandler {
	@SuppressWarnings("unchecked")
	public TbsFileInfo process(List<IDto> mainlist, String fileName)throws ITFEBizException {
		StringBuffer resultStr = new StringBuffer();
		TbsFileInfo fileInfo = new TbsFileInfo();
		int count = mainlist.size();
		BigDecimal totalFamt = new BigDecimal("0.00");
		for (IDto tmp : mainlist) {
			TvGrantpaymsgmainDto dto = (TvGrantpaymsgmainDto) tmp;
			resultStr.append((count>1?"**":"")+dto.getStrecode().toString() + ","); // 国库代码
			resultStr.append(generateVouno(dto.getSpackageticketno()) + ","); // 凭证编号
			resultStr.append(dto.getSpaybankno() + ","); // 代理银行代码
			resultStr.append(dto.getSbudgetunitcode() + ","); // 预算单位代码
			resultStr.append(dto.getSofmonth() + ","); // 所属月份
			resultStr.append(dto.getSbudgettype() + ","); // 预算种类
			resultStr.append(dto.getNmoney() + ","); // 零余额发生额
			totalFamt = totalFamt.add(dto.getNmoney());
			resultStr.append("0.00"); // 小额现金发生额
			resultStr.append("\n");

			List<TvGrantpaymsgsubDto> resultsub = exportTBSForBJService.findSubInfoByMain(dto);
			for (TvGrantpaymsgsubDto subdto : resultsub) {
				resultStr.append(subdto.getSfunsubjectcode() + ",");// 功能科目代码
				if (null==subdto.getSecosubjectcode()|| subdto.getSecosubjectcode().trim().length()<1) {
					resultStr.append("" + ",");// 经济科目代码
				}else{
					resultStr.append(subdto.getSecosubjectcode().trim() + ",");// 经济科目代码
				}
				resultStr.append(subdto.getNmoney() + ",");// 零余额发生额
				resultStr.append("0.00");// 小额现金发生额
				resultStr.append("\n");// 换行
			}
		}
		fileInfo.setFileContent(resultStr.toString());
		fileInfo.setTotalCount(count);
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}

}
