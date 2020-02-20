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

/**处理一般预算支出**/
public class Process17 extends BaseTBSFileExportUtil implements IProcessHandler{

	/*
	 字段代码	字段类型			字段名称							备注
	----------------------------------------------------------------------------
	gkdm	varchar(10)		国库代码	
	fkzh	nvarchar(32)	付款账号	
	dbyydm	nvarchar(10)    支出原因代码	
	ysdw	nvarchar(15)	预算单位代码						必须和核算系统中对应
	skzh	nvarchar(32)	收款单位账号	
	yszl	nchar(1)		预算种类（1－预算内，2－预算外）	必须为1或者2
	kmdm	nvarchar(30)	功能科目代码	
	Jjdm	Nvarchar(30)	经济科目代码	
	kjkmdm  nvarchar(32)    会计科目代码						可以为空
	zczl 	nchar(3) 		支出种类（1－一般支出，2－调拨）	
	fse		numeric(18,2)	发生额(#.00)						保留小数点后两位
	pzbh	nvarchar(8)		凭证号码							不能重复

	TODO:考虑一个实拨资金凭证带多个明细的情况(备注：跟老曹商量，暂时不考虑一对多的情况)
	 */
	@SuppressWarnings("unchecked")
	public TbsFileInfo process(List<IDto> mainlist, String fileName)
			throws ITFEBizException {
		//划分 一般支出
		StringBuffer payout = new StringBuffer("");
		TbsFileInfo fileInfo = new TbsFileInfo();
		BigDecimal totalFamt = new BigDecimal("0.00");
		for (IDto tmp : mainlist) {
			TvPayoutmsgmainDto dto = (TvPayoutmsgmainDto) tmp;
			List<TvPayoutmsgsubDto> resultsub = exportTBSForBJService.findSubInfoByMain(dto);
			TvPayoutmsgsubDto subdto = resultsub.get(0);//(不考虑多个明细的情况，默认去第一个)
			payout.append(dto.getStrecode() + ","); // 国库代码
			payout.append(dto.getSpayeracct() + ",");// 付款账号
			payout.append(",");// 支出原因代码
			payout.append(dto.getSbudgetunitcode() + ",");// 预算单位代码
			payout.append(",");// 收款单位账号(接口可为空，就不填)
			payout.append(dto.getSbudgettype() + ",");// 预算种类
			payout.append(subdto.getSfunsubjectcode() + ",");// 功能科目代码
			payout.append((subdto.getSecnomicsubjectcode()==null?"":subdto.getSecnomicsubjectcode()) + ",");// 经济科目代码
			payout.append(",");// 会计科目代码
			payout.append("1,");// 支出种类 
			payout.append(subdto.getNmoney() + ",");// 发生额
			totalFamt = totalFamt.add(subdto.getNmoney());
			payout.append(generateVouno(dto.getStaxticketno()));// 凭证号码
			payout.append("\n");
		}
		fileInfo.setFileContent(payout.toString());
		fileInfo.setTotalCount(mainlist.size());
		fileInfo.setTotalFamt(totalFamt);
		return fileInfo;
	}
}
