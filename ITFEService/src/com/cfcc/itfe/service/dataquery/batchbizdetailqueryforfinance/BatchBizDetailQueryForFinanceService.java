package com.cfcc.itfe.service.dataquery.batchbizdetailqueryforfinance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.HtfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailssubDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * @author Administrator
 * @time   14-11-04 16:36:35
 * codecomment: 
 */

public class BatchBizDetailQueryForFinanceService extends AbstractBatchBizDetailQueryForFinanceService {
	private static Log log = LogFactory.getLog(BatchBizDetailQueryForFinanceService.class);

	public String exportFile(IDto dtoInfo, String flag) throws ITFEBizException {
		List list=new ArrayList();
		try {
			list = CommonFacade.getODB().findRsByDto(dtoInfo);
			
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // 取得系统分割符
			String strdate = DateUtil.date2String2(new Date()); // 当前系统年月日
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep + new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+".csv";
			String splitSign = ",";// 文件记录分隔符号
			
			if(list.size()>0){
				StringBuffer filebuf = new StringBuffer();
				if(flag.equals("0")){
					for (TfPaymentDetailsmainDto dto :(List<TfPaymentDetailsmainDto>) list) {
						filebuf.append(dto.getStrecode()); //国库代码
						filebuf.append(splitSign + dto.getSstatus()); //状态
						filebuf.append(splitSign + (dto.getSdemo()==null?"":dto.getSdemo())); //描述
						filebuf.append(splitSign + dto.getSfilename()); //文件名称
						filebuf.append(splitSign + (dto.getSpackageno()==null?"":dto.getSpackageno())); //包流水号
						filebuf.append(splitSign + dto.getSadmdivcode()); //行政区划代码
						filebuf.append(splitSign + dto.getSstyear()); //业务年度
						filebuf.append(splitSign + dto.getSvtcode()); //凭证类型编号
						filebuf.append(splitSign + dto.getSvoudate()); //凭证日期
						filebuf.append(splitSign + dto.getSvoucherno()); //凭证号
						filebuf.append(splitSign + dto.getSoriginalvtcode()); //主凭证类型编号
						filebuf.append(splitSign + dto.getSoriginalvoucherno()); //主支付凭证编号
						filebuf.append(splitSign + dto.getSfundtypecode()); //资金性质编码
						filebuf.append(splitSign + dto.getSfundtypename()); //资金性质名称
						filebuf.append(splitSign + (dto.getSpaydictateno()==null?"":dto.getSpaydictateno())); //支付交易序号
						filebuf.append(splitSign + (dto.getSpaymsgno()==null?"":dto.getSpaymsgno())); //支付报文编号
						filebuf.append(splitSign + (dto.getSpayentrustdate()==null?"":dto.getSpayentrustdate())); //支付委托日期
						filebuf.append(splitSign + (dto.getSpaysndbnkno()==null?"":dto.getSpaysndbnkno())); //支付发起行行号
						filebuf.append(splitSign + dto.getNsumamt()); //汇总支付金额
						filebuf.append(splitSign + (dto.getSagencycode()==null?"":dto.getSagencycode())); //基层预算单位编码
						filebuf.append(splitSign + dto.getSagencyname()); //基层预算单位名称
						filebuf.append(splitSign + dto.getSpayacctno()); //付款人账号
						filebuf.append(splitSign + dto.getSpayacctname()); //付款人名称
						filebuf.append(splitSign + dto.getSpayacctbankname()); //付款人银行
						filebuf.append(splitSign + dto.getSpaybankcode()); //代理银行编码
						filebuf.append(splitSign + dto.getSpaybankname()); //代理银行名称
						filebuf.append(splitSign + dto.getSbusinesstypecode()); //业务类型编码
						filebuf.append(splitSign + dto.getSbusinesstypename()); //业务类型名称
						filebuf.append(splitSign + dto.getSpaytypecode()); //支付方式编码
						filebuf.append(splitSign + dto.getSpaytypename()); //支付方式名称
						filebuf.append(splitSign + (dto.getSxpaydate()==null?"":dto.getSxpaydate())); //实际支付日期
						filebuf.append(splitSign + (dto.getNxsumamt()==null?"":dto.getNxsumamt())); //实际支付汇总金额
						filebuf.append("\r\n");
						
						TfPaymentDetailssubDto subDto = new TfPaymentDetailssubDto();
						subDto.setIvousrlno(dto.getIvousrlno());
						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subDto);
						for(TfPaymentDetailssubDto tmpDto:(List<TfPaymentDetailssubDto>)sublist){
							filebuf.append(tmpDto.getSid()); //明细编号
							filebuf.append(splitSign + (tmpDto.getSorivoucherno()==null?"":tmpDto.getSorivoucherno())); //支付凭证编号
							filebuf.append(splitSign + tmpDto.getSpayeeacctno()); //收款人账号
							filebuf.append(splitSign + tmpDto.getSpayeeacctname()); //收款人名称
							filebuf.append(splitSign + tmpDto.getSpayeeacctbankname()); //收款人银行
							filebuf.append(splitSign + tmpDto.getNpayamt()); //支付金额
							filebuf.append(splitSign + (tmpDto.getSremark()==null?"":tmpDto.getSorivoucherno())); //备注
							filebuf.append(splitSign + (tmpDto.getSxpaydate()==null?"":tmpDto.getSxpaydate())); //实际支付日期
							filebuf.append(splitSign + (tmpDto.getSxagentbusinessno()==null?"":tmpDto.getSxagentbusinessno())); //银行交易流水号
							filebuf.append(splitSign + (tmpDto.getNxpayamt()==null?"":tmpDto.getNxpayamt())); //实际支付金额
							filebuf.append(splitSign + (tmpDto.getSxpayeeacctbankname()==null?"":tmpDto.getSxpayeeacctbankname())); //收款人银行
							filebuf.append(splitSign + (tmpDto.getSxpayeeacctno()==null?"":tmpDto.getSxpayeeacctno())); //收款人账号
							filebuf.append(splitSign + (tmpDto.getSxaddwordcode()==null?"":tmpDto.getSxaddwordcode())); //失败原因代码
							filebuf.append(splitSign + (tmpDto.getSxaddword()==null?"":tmpDto.getSxaddword())); //失败原因
							filebuf.append("\r\n");
						}
					}
				}else{
					for (HtfPaymentDetailsmainDto dto :(List<HtfPaymentDetailsmainDto>) list) {
						filebuf.append(dto.getStrecode()); //国库代码
						filebuf.append(splitSign + dto.getSstatus()); //状态
						filebuf.append(splitSign + (dto.getSdemo()==null?"":dto.getSdemo())); //描述
						filebuf.append(splitSign + dto.getSfilename()); //文件名称
						filebuf.append(splitSign + (dto.getSpackageno()==null?"":dto.getSpackageno())); //包流水号
						filebuf.append(splitSign + dto.getSadmdivcode()); //行政区划代码
						filebuf.append(splitSign + dto.getSstyear()); //业务年度
						filebuf.append(splitSign + dto.getSvtcode()); //凭证类型编号
						filebuf.append(splitSign + dto.getSvoudate()); //凭证日期
						filebuf.append(splitSign + dto.getSvoucherno()); //凭证号
						filebuf.append(splitSign + dto.getSoriginalvtcode()); //主凭证类型编号
						filebuf.append(splitSign + dto.getSoriginalvoucherno()); //主支付凭证编号
						filebuf.append(splitSign + dto.getSfundtypecode()); //资金性质编码
						filebuf.append(splitSign + dto.getSfundtypename()); //资金性质名称
						filebuf.append(splitSign + (dto.getSpaydictateno()==null?"":dto.getSpaydictateno())); //支付交易序号
						filebuf.append(splitSign + (dto.getSpaymsgno()==null?"":dto.getSpaymsgno())); //支付报文编号
						filebuf.append(splitSign + (dto.getSpayentrustdate()==null?"":dto.getSpayentrustdate())); //支付委托日期
						filebuf.append(splitSign + (dto.getSpaysndbnkno()==null?"":dto.getSpaysndbnkno())); //支付发起行行号
						filebuf.append(splitSign + dto.getNsumamt()); //汇总支付金额
						filebuf.append(splitSign + (dto.getSagencycode()==null?"":dto.getSagencycode())); //基层预算单位编码
						filebuf.append(splitSign + dto.getSagencyname()); //基层预算单位名称
						filebuf.append(splitSign + dto.getSpayacctno()); //付款人账号
						filebuf.append(splitSign + dto.getSpayacctname()); //付款人名称
						filebuf.append(splitSign + dto.getSpayacctbankname()); //付款人银行
						filebuf.append(splitSign + dto.getSpaybankcode()); //代理银行编码
						filebuf.append(splitSign + dto.getSpaybankname()); //代理银行名称
						filebuf.append(splitSign + dto.getSbusinesstypecode()); //业务类型编码
						filebuf.append(splitSign + dto.getSbusinesstypename()); //业务类型名称
						filebuf.append(splitSign + dto.getSpaytypecode()); //支付方式编码
						filebuf.append(splitSign + dto.getSpaytypename()); //支付方式名称
						filebuf.append(splitSign + (dto.getSxpaydate()==null?"":dto.getSxpaydate())); //实际支付日期
						filebuf.append(splitSign + (dto.getNxsumamt()==null?"":dto.getNxsumamt())); //实际支付汇总金额
						filebuf.append("\r\n");
						
						TfPaymentDetailssubDto subDto = new TfPaymentDetailssubDto();
						subDto.setIvousrlno(dto.getIvousrlno());
						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subDto);
						for(TfPaymentDetailssubDto tmpDto:(List<TfPaymentDetailssubDto>)sublist){
							filebuf.append(tmpDto.getSid()); //明细编号
							filebuf.append(splitSign + (tmpDto.getSorivoucherno()==null?"":tmpDto.getSorivoucherno())); //支付凭证编号
							filebuf.append(splitSign + tmpDto.getSpayeeacctno()); //收款人账号
							filebuf.append(splitSign + tmpDto.getSpayeeacctname()); //收款人名称
							filebuf.append(splitSign + tmpDto.getSpayeeacctbankname()); //收款人银行
							filebuf.append(splitSign + tmpDto.getNpayamt()); //支付金额
							filebuf.append(splitSign + (tmpDto.getSremark()==null?"":tmpDto.getSorivoucherno())); //备注
							filebuf.append(splitSign + (tmpDto.getSxpaydate()==null?"":tmpDto.getSxpaydate())); //实际支付日期
							filebuf.append(splitSign + (tmpDto.getSxagentbusinessno()==null?"":tmpDto.getSxagentbusinessno())); //银行交易流水号
							filebuf.append(splitSign + (tmpDto.getNxpayamt()==null?"":tmpDto.getNxpayamt())); //实际支付金额
							filebuf.append(splitSign + (tmpDto.getSxpayeeacctbankname()==null?"":tmpDto.getSxpayeeacctbankname())); //收款人银行
							filebuf.append(splitSign + (tmpDto.getSxpayeeacctno()==null?"":tmpDto.getSxpayeeacctno())); //收款人账号
							filebuf.append(splitSign + (tmpDto.getSxaddwordcode()==null?"":tmpDto.getSxaddwordcode())); //失败原因代码
							filebuf.append(splitSign + (tmpDto.getSxaddword()==null?"":tmpDto.getSxaddword())); //失败原因
							filebuf.append("\r\n");
						}
					}
				}
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString().substring(0, filebuf.toString().length()-2));
			    return fullpath.replaceAll(root, "");
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("查询出错",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("查询出错",e);
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("写文件出错",e);
		}
		return null;
	}	


}