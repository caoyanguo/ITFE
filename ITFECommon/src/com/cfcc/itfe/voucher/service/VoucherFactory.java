package com.cfcc.itfe.voucher.service;

import java.io.File;
import java.io.FileFilter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TnConpaycheckbillDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TvNontaxmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.persistence.pk.TvNontaxmainPK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.transformer.IVoucherDto2Map;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.sxservice.VoucherVerifySX;
import com.cfcc.itfe.webservice.VoucherService;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.jdbc.sql.SQLResults;

/**
 * codecomment: VoucherFactory
 * 
 * @author hjr
 * @time 13-07-20 14:26:06 工厂类:凭证工厂类 模块:VoucherFactory 组件:VoucherFactory
 */
@SuppressWarnings("unchecked")
public abstract class VoucherFactory implements IVoucherFactory {
	private static Log logger = LogFactory.getLog(VoucherFactory.class);
	private static Map<String, String> vouMap = new HashMap<String, String>();
	private static boolean isread = true;
	public int voucherRead(List list) {

		/**
		 * 凭证读取
		 * 
		 * 循环凭证类型读取凭证信息readVoucher 读出的报文信息放入tv_voucherinfo表，报文体存入文件目录
		 * （在这之前报文格式要转码从Base64转换成String） 返回读取的凭证条数
		 */
		int count = 0;
		int disablecount = 0;
		TvVoucherinfoDto voucherDto = new TvVoucherinfoDto();
		SQLExecutor execDetail = null;
		try {
			if(!isread)
				return count;
			isread = false;
			VoucherService voucherService = new VoucherService();
			String currentDate = TimeFacade.getCurrentStringTime();
			String dirsep = File.separator;
			int li_Year = Integer.parseInt(currentDate.substring(0, 4));
			for (int j = 0; j < list.size(); j++) {
				byte[] voucher = null;
				List<String> vous = (List) list.get(j);
				String ls_OrgCode = vous.get(0);
				String ls_FinOrgCode = vous.get(1);
				String ls_AdmDiveCode = vous.get(2);
				String svtcode = vous.get(3);
				String TreCode = vous.get(4);
				try {
					logger.debug("===================凭证读取开始=========================");
					try {
						voucher = voucherService.readVoucher(ls_AdmDiveCode,li_Year, svtcode);

					} catch (Exception e) {
						String errInfo = (e.getMessage()==null?(e.toString()):(e.getMessage().length()>1024?e.getMessage().substring(0, 1024):e.getMessage()));
						if(errInfo.contains("EVS100#参数不合法")||errInfo.contains("未找到行政区划为")||errInfo.contains("EVS004#获取行政区划失败"))
						{	
							logger.error("凭证库中未配置行政区划为["+ls_AdmDiveCode+"],业务类型为["+svtcode+"]的业务凭证模板!");
						}else
						{
							logger.error(e);
							VoucherException.saveErrInfo(svtcode, e);
						}
						disablecount++;
						continue;
					}
					if (voucher != null && voucher.length > 0) {
						String voucherXml = new String(voucher, "GBK");
						String FileName = ITFECommonConstant.FILE_ROOT_PATH + "Voucher"
								+ dirsep + currentDate
								+ dirsep + "rev"
								+ svtcode + "_"
								+ new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis()) + ".msg";
						logger.debug("凭证读取" + svtcode + "凭证库原始报文：" + voucherXml);
						Document fxrDoc = DocumentHelper.parseText(voucherXml);
						Document fxrDocVoucher = null;
						List listNodes = fxrDoc.selectSingleNode("MOF").selectNodes("VoucherBody");
						int li_CountCurrent = Integer.parseInt(fxrDoc.selectSingleNode("MOF").selectSingleNode("VoucherCount").getText());
						count = count + li_CountCurrent;
						Map<String, String> dealnos = new HashMap<String, String>();
						for (int i = 0; i < listNodes.size(); i++) {
							String VoucherNo = "";
							try {

								String StYear = ((Element) listNodes.get(i)).attribute("StYear").getText();
								String VtCode = ((Element) listNodes.get(i)).attribute("VtCode").getText();
								VoucherNo = ((Element) listNodes.get(i)).attribute("VoucherNo").getText();
								String VoucherFlag = ((Element) listNodes.get(i)).elementText("VoucherFlag");
								String Attach = ((Element) listNodes.get(i)).elementText("Attach");

								String VoucherBase64 = ((Node) listNodes.get(i)).selectSingleNode("Voucher").getText();

								String Voucher = VoucherUtil.base64Decode(VoucherBase64);
								logger.debug("凭证读取凭证类型" + svtcode + "凭证编号" + VoucherNo + "解析报文：\r\n\t" + Voucher);

								// 删除Voucher元素
								Node node = ((Node) listNodes.get(i)).selectSingleNode("Voucher");
								((Element) listNodes.get(i)).remove(node);

								fxrDocVoucher = DocumentHelper.parseText(Voucher);

								// 添加解码后的Voucher元素
								((Element) listNodes.get(i)).add(fxrDocVoucher.getRootElement());

								Node voucherNode = fxrDocVoucher.selectSingleNode("Voucher");
								if(MsgConstant.VOUCHER_NO_3550.equals(VtCode)||MsgConstant.VOUCHER_NO_3558.equals(VtCode))
								{
									recvFj50And58(voucherService,voucherNode,VtCode,VoucherNo,ls_AdmDiveCode,StYear);
									continue;
								}
								// 5201、8207、5253、5351、2252报文无国库代码节点
								if (!(svtcode.equals(MsgConstant.VOUCHER_NO_5201)
										|| svtcode.equals(MsgConstant.VOUCHER_NO_8207)
										|| svtcode.equals(MsgConstant.VOUCHER_NO_5253)
										|| svtcode.equals(MsgConstant.VOUCHER_NO_5351) 
										|| svtcode.equals(MsgConstant.VOUCHER_NO_5407) 
										|| svtcode.equals(MsgConstant.VOUCHER_NO_2252))) {
									if(voucherNode.selectSingleNode("TreCode")!=null)
										TreCode = voucherNode.selectSingleNode("TreCode").getText();
								}
								String billOrgCode = VoucherUtil.getSpaybankcode(svtcode, voucherNode);// 出票单位
								if (billOrgCode != null	&& billOrgCode.length() > 12)
									billOrgCode = billOrgCode.substring(billOrgCode.length() - 12,billOrgCode.length());
								String Total = VoucherUtil.getTotalAmt(svtcode,voucherNode);
								voucherDto = new TvVoucherinfoDto();
								voucherDto.setNmoney(MtoCodeTrans.transformBigDecimal(Total));
								voucherDto.setSadmdivcode(ls_AdmDiveCode);
								voucherDto.setScreatdate(currentDate);
								voucherDto.setSrecvtime(new Timestamp(new java.util.Date().getTime()));
								voucherDto.setSfilename(FileName);
								voucherDto.setSorgcode(ls_OrgCode);
								if (Attach != null && new String(Attach.getBytes("GBK"),"iso-8859-1").length() > 32)
									Attach = "";
								// 云南地税特色
								if (ITFECommonConstant.SRC_NODE.equals("000087100006")
										&& (VtCode.equals(MsgConstant.VOUCHER_NO_5207) 
											|| VtCode.equals(MsgConstant.VOUCHER_NO_5209))) {
									String shold1 = voucherNode.selectSingleNode("Hold1").getText();
									if (shold1 != null && !"".equals(shold1)&& shold1.length() == 6) {
										Attach = StringUtil.replace(shold1,"TAX", "");
									}
								}
								String hold1 = voucherNode.selectSingleNode("Hold1")==null?"":voucherNode.selectSingleNode("Hold1").getText();
								String hold2 = voucherNode.selectSingleNode("Hold2")==null?"":voucherNode.selectSingleNode("Hold2").getText();
								String hold3 = voucherNode.selectSingleNode("Hold3")==null?"":voucherNode.selectSingleNode("Hold3").getText();
								if(VtCode.equals(MsgConstant.VOUCHER_NO_5408))
								{
									hold3 = voucherNode.selectSingleNode("PayBankName")==null?"":voucherNode.selectSingleNode("PayBankName").getText();
									hold3 = hold3.getBytes().length>60 ? CommonUtil.subString(hold3, 60) : hold3;
								}
								String hold4 = voucherNode.selectSingleNode("Hold4")==null?"":voucherNode.selectSingleNode("Hold4").getText();
								// 先默认一个空格，为null是前台异常
								voucherDto.setShold1(getString(hold1));
								voucherDto.setShold2(getString(hold2));
								voucherDto.setShold3(getString(hold3));
								voucherDto.setShold4(getString(hold4));
								voucherDto.setSext1(" ");// 支付方式
								voucherDto.setSext3(" ");// 业务类型
								voucherDto.setSext2(" ");// 
								voucherDto.setSext4(" ");// 预算类型
								voucherDto.setSext5(" ");// 
								if (VtCode.equals(MsgConstant.VOUCHER_NO_2301)
										|| VtCode.equals(MsgConstant.VOUCHER_NO_2302)) {
									if (voucherNode.selectNodes("PayTypeCode") != null) {
										String PayTypeCode = voucherNode.selectSingleNode("PayTypeCode").getText();
										if ("12".equals(PayTypeCode)|| PayTypeCode.startsWith("001002")) {// 财政普遍采用的是6位
											voucherDto.setSext1(MsgConstant.grantPay);// 授权支付
										} else if ("11".equals(PayTypeCode)|| PayTypeCode.startsWith("001001")) {// 财政普遍采用的是6位
											voucherDto.setSext1(MsgConstant.directPay);// 支付方式编码
											// 直接支付
										}
									}
								} else if (VtCode.equals(MsgConstant.VOUCHER_NO_2252)) {
									// 默认为直接支付 避免前台报null错误
									voucherDto.setSext1(MsgConstant.directPay);
									if (voucherNode.selectNodes("PayTypeCode") != null) {
										String PayTypeCode = voucherNode.selectSingleNode("PayTypeCode").getText();
										if (StateConstant.DIRECT_PAY_CODE.equals(PayTypeCode)) {
											voucherDto.setSext1(MsgConstant.directPay);
										} else if (StateConstant.PAYOUT_PAY_CODE.equals(PayTypeCode)) {
											voucherDto.setSext1(StateConstant.PAYOUT_PAY_CODE);
										}
									}
								}
								voucherDto.setSattach(Attach);
								if((VtCode.equals(MsgConstant.VOUCHER_NO_5287)||svtcode.equals(MsgConstant.VOUCHER_NO_5551)
										||svtcode.equals(MsgConstant.VOUCHER_NO_5552)
										||svtcode.equals(MsgConstant.VOUCHER_NO_5553)))
								{
									voucherDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
									voucherDto.setSdemo("汇总单可直接签章");
								}else
								{
									voucherDto.setSstatus(DealCodeConstants.VOUCHER_ACCEPT);
									voucherDto.setSdemo("解析报文异常");
								}
								voucherDto.setSstyear(StYear);
								voucherDto.setStrecode(TreCode);
								voucherDto.setSvoucherflag(VoucherFlag);
								voucherDto.setSvoucherno(VoucherNo);
								voucherDto.setSvtcode(VtCode);
								voucherDto.setSpaybankcode(billOrgCode); // 出票单位
								
								if (dealnos.containsKey(VoucherNo)) {
									continue;
								}else{
									String mainvou = VoucherUtil.getGrantSequence();
									dealnos.put(VoucherNo, mainvou);
									voucherDto.setSdealno(mainvou);
								}
								
								// 数据来源(接收财政或代理行的对账请求时填写数据来源，区分对账发起方)
								// (1:人行发起；2：财政发起；3：商行发起)
								if (VtCode.equals(MsgConstant.VOUCHER_NO_3507)) {
									if (voucherNode.selectNodes("Hold1") != null) {
										String from = voucherNode
												.selectSingleNode("Hold1")
												.getText();
										if (from == null
												|| from.equals("")
												|| from.startsWith(StateConstant.ORGCODE_FIN)) {
											voucherDto.setSext1("2");// 财政
										} else {
											voucherDto.setSext1("3");// 商行
											if (voucherNode.selectSingleNode("ClearAccNo") != null&& !"".equals(voucherNode.selectSingleNode("ClearAccNo").getText()))
												voucherDto.setSpaybankcode(voucherNode.selectSingleNode("ClearAccNo").getText());
											else
												voucherDto.setSpaybankcode(from);// 代理银行行号
										}
									}
								} else if (StateConstant.ACCOUNT_CHECK.contains(VtCode)) {
									voucherDto.setSext1("2");
								} else if (voucherNode.selectSingleNode("VoucherCheckNo") != null)
									voucherDto.setSext2(voucherNode.selectSingleNode("VoucherCheckNo").getText());// 针对对账回执报文保存原对账报文单号
								if (VoucherUtil.voucherDealMsgXML(voucherNode,voucherDto))
									continue;
								// 更新凭证勾兑信息(上海特色)
								VoucherUtil.updateVoucherCompareInfo(voucherDto, voucherNode);
								if (VoucherNo != null&& vouMap.get(VoucherNo+voucherDto.getStrecode()) != null) {
									voucherComfail(vouMap.get(VoucherNo+voucherDto.getStrecode()),
											"报文不规范：请核对报文规范!");
									vouMap.remove(VoucherNo+voucherDto.getStrecode());
									continue;
								}
								StringBuffer findvoucherno = new StringBuffer("");
								SQLResults rs = null;
								if(ITFECommonConstant.PUBLICPARAM.contains(",voucherno=only,"))
								{
									findvoucherno.append("select count(*) as COUNT from (");
									findvoucherno.append("select s_voucherno from TV_VOUCHERINFO where S_CREATDATE=? and S_VTCODE=? and  s_voucherno=? and S_STATUS not in(?,?,?)");
									findvoucherno.append(" union ");
									findvoucherno.append("select s_voucherno from HTV_VOUCHERINFO where S_CREATDATE=? and S_VTCODE=? and s_voucherno=? and S_STATUS not in(?,?,?)");
									findvoucherno.append(")");
									if(execDetail==null)
										execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
									execDetail.clearParams();
									execDetail.addParam(voucherDto.getScreatdate());
									execDetail.addParam(voucherDto.getSvtcode());
									execDetail.addParam(voucherDto.getSvoucherno());
									execDetail.addParam(DealCodeConstants.VOUCHER_ACCEPT);
									execDetail.addParam(DealCodeConstants.VOUCHER_RECEIVE_FAIL);
									execDetail.addParam(DealCodeConstants.VOUCHER_FAIL);
									execDetail.addParam(voucherDto.getScreatdate());
									execDetail.addParam(voucherDto.getSvtcode());
									execDetail.addParam(voucherDto.getSvoucherno());
									execDetail.addParam(DealCodeConstants.VOUCHER_ACCEPT);
									execDetail.addParam(DealCodeConstants.VOUCHER_RECEIVE_FAIL);
									execDetail.addParam(DealCodeConstants.VOUCHER_FAIL);
									rs = execDetail.runQuery(findvoucherno.toString());
									if(rs!=null&&rs.getInt(0, 0)>0)
									{
										if(ITFECommonConstant.PUBLICPARAM.contains(",voucherno=onlyone,"))
										{
											voucherComfail(voucherDto, "凭证编号不能重复,已经存在凭证编号为"+voucherDto.getSvoucherno()+"的凭证!"+rs.getInt(0, 0));
											execDetail.runQuery("update TV_VOUCHERINFO set S_STATUS=?,S_DEMO=? where S_CREATDATE=? and S_VTCODE=? and  s_voucherno=? and S_STATUS not in(?,?,?) ");
											execDetail.clearParams();
											execDetail.addParam(DealCodeConstants.VOUCHER_RECEIVE_FAIL);
											execDetail.addParam("凭证编号重复已存在凭证编号"+voucherDto.getSvoucherno()+"的凭证!");
											execDetail.addParam(voucherDto.getScreatdate());
											execDetail.addParam(voucherDto.getSvtcode());
											execDetail.addParam(voucherDto.getSvoucherno());
											execDetail.addParam(DealCodeConstants.VOUCHER_ACCEPT);
											execDetail.addParam(DealCodeConstants.VOUCHER_RECEIVE_FAIL);
											execDetail.addParam(DealCodeConstants.VOUCHER_FAIL);
										}
										continue;
									}
								}
								if(ITFECommonConstant.PUBLICPARAM.contains(",voucherno=again,"))
								{
									findvoucherno.append("select count(*) as COUNT from (");
									findvoucherno.append("select s_voucherno from TV_VOUCHERINFO where S_VTCODE=? and  s_voucherno=? and S_STATUS=?");
									findvoucherno.append(" union ");
									findvoucherno.append("select s_voucherno from HTV_VOUCHERINFO where S_VTCODE=? and s_voucherno=? and S_STATUS=?");
									findvoucherno.append(")");
									if(execDetail==null)
										execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
									execDetail.clearParams();
									execDetail.addParam(voucherDto.getSvtcode());
									execDetail.addParam(voucherDto.getSvoucherno());
									execDetail.addParam(DealCodeConstants.VOUCHER_ACCEPT);
									rs = execDetail.runQuery(findvoucherno.toString());
									if(rs!=null&&rs.getInt(0, 0)>2)
									{
										logger.debug("此凭证为非法凭证,凭证编号为"+voucherDto.getSvoucherno()+"的凭证!"+rs.getInt(0, 0));
										continue;
									}
								}
								//有些需求对账的回单回的是凭证本身，兼容这种模式，如对账单是3510，回单也是3510
								if(ITFECommonConstant.PUBLICPARAM.contains(",returnvoucher=self,"))
								{
									if("3507350835093510351135123513".contains(voucherDto.getSvtcode()))
									{
										findvoucherno.append("select * from TV_VOUCHERINFO where S_VTCODE=? and  s_voucherno=? ");
										if(execDetail==null)
											execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
										execDetail.clearParams();
										execDetail.addParam(voucherDto.getSvtcode());
										execDetail.addParam(voucherDto.getSvoucherno());
										List<TvVoucherinfoDto> qlist  = (List<TvVoucherinfoDto>)execDetail.runQuery(findvoucherno.toString(),TvVoucherinfoDto.class).getDtoCollection();
										if(qlist!=null&&qlist.size()>=0)
										{
											voucherConfirmSuccess(voucherDto);
											String diffnum = voucherNode.selectSingleNode("XDiffNum")==null?"0":voucherNode.selectSingleNode("XDiffNum").getText();
											String chresult = voucherNode.selectSingleNode("XCheckResult")==null?diffnum:voucherNode.selectSingleNode("XCheckResult").getText();
											qlist.get(0).setSstatus(DealCodeConstants.VOUCHER_READRETURN);
											qlist.get(0).setSdemo(("0".equals(chresult)?"对账成功":"对账失败"));
											DatabaseFacade.getDb().update(qlist.get(0));
										}else
										{
											DatabaseFacade.getDb().create(voucherDto);
										}
									}else
									{
										if("35803582358335873588".contains(voucherDto.getSvtcode()))
										{
											findvoucherno.append("select * from TV_VOUCHERINFO where S_VTCODE=? and  s_voucherno=? ");
											if(execDetail==null)
												execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
											execDetail.clearParams();
											execDetail.addParam(voucherDto.getSvtcode());
											execDetail.addParam(voucherDto.getSvoucherno());
											List<TvVoucherinfoDto> qlist  = (List<TvVoucherinfoDto>)execDetail.runQuery(findvoucherno.toString(),TvVoucherinfoDto.class).getDtoCollection();
											if(qlist!=null&&qlist.size()>=0)
											{
												voucherConfirmSuccess(voucherDto);
												String diffnum = voucherNode.selectSingleNode("XDiffNum")==null?"0":voucherNode.selectSingleNode("XDiffNum").getText();
												String chresult = voucherNode.selectSingleNode("XCheckResult")==null?diffnum:voucherNode.selectSingleNode("XCheckResult").getText();
												qlist.get(0).setSstatus(DealCodeConstants.VOUCHER_READRETURN);
												qlist.get(0).setSdemo(("0".equals(chresult)?"对账成功":"对账失败"));
												DatabaseFacade.getDb().update(qlist.get(0));
												continue;
											}
										}else
										{
											DatabaseFacade.getDb().create(voucherDto);
										}
									}
								}else
								{
									if("35803582358335873588".contains(voucherDto.getSvtcode()))
									{
										findvoucherno.append("select * from TV_VOUCHERINFO where S_VTCODE=? and  s_voucherno=? ");
										if(execDetail==null)
											execDetail = DatabaseFacade.getODB().getSqlExecutorFactory().getSQLExecutor();
										execDetail.clearParams();
										execDetail.addParam(voucherDto.getSvtcode());
										execDetail.addParam(voucherDto.getSvoucherno());
										List<TvVoucherinfoDto> qlist  = (List<TvVoucherinfoDto>)execDetail.runQuery(findvoucherno.toString(),TvVoucherinfoDto.class).getDtoCollection();
										if(qlist!=null&&qlist.size()>=0)
										{
											voucherConfirmSuccess(qlist.get(0).getSdealno());
											String diffnum = voucherNode.selectSingleNode("XDiffNum")==null?"0":voucherNode.selectSingleNode("XDiffNum").getText();
											String chresult = voucherNode.selectSingleNode("XCheckResult")==null?diffnum:voucherNode.selectSingleNode("XCheckResult").getText();
											qlist.get(0).setSstatus(DealCodeConstants.VOUCHER_READRETURN);
											qlist.get(0).setSdemo(("0".equals(chresult)?"对账成功":"对账失败"));
											DatabaseFacade.getDb().update(qlist.get(0));
											continue;
										}
									}else
									{
										DatabaseFacade.getDb().create(voucherDto);
									}
								}
								if (vouMap.size() > 600)
									vouMap.clear();
								if(!(VtCode.equals(MsgConstant.VOUCHER_NO_5287)||svtcode.equals(MsgConstant.VOUCHER_NO_5551)
										||svtcode.equals(MsgConstant.VOUCHER_NO_5552)
										||svtcode.equals(MsgConstant.VOUCHER_NO_5553)))
								{
									if(ITFECommonConstant.PUBLICPARAM.contains(",voucherno=gf-check,"))
										vouMap.put(VoucherNo+voucherDto.getStrecode(), voucherDto.getSdealno());
								}else
								{
									voucherConfirmSuccess(voucherDto.getSdealno());
								}
								VoucherUtil.voucherDealMsgXML(svtcode,voucherNode, voucherDto);
							} catch (JAFDatabaseException e) {
								logger.error("保存数据出现异常：" + e);
								VoucherException.saveErrInfo(svtcode, e);
								voucherService.confirmVoucherfail(null,ls_AdmDiveCode, li_Year, svtcode,new String[] { VoucherNo },new String[] { "保存数据异常: "+ e.getMessage() });
								continue;
							} catch (Exception e) {
								logger.error("凭证读取" + svtcode + "报文出现异常：" + e);
								VoucherException.saveErrInfo(svtcode, e);
								voucherService.confirmVoucherfail(null,	ls_AdmDiveCode, li_Year, svtcode,new String[] { VoucherNo },	new String[] { "报文不规范: "+ e.getMessage() });
								continue;
							}
						}
						String savexml = fxrDoc.asXML();
						FileUtil.getInstance().writeFile(FileName, savexml);
						if (svtcode.equals(MsgConstant.VOUCHER_NO_5287)
								||svtcode.equals(MsgConstant.VOUCHER_NO_5551)
								||svtcode.equals(MsgConstant.VOUCHER_NO_5552)
								||svtcode.equals(MsgConstant.VOUCHER_NO_5553)
								||svtcode.equals(MsgConstant.VOUCHER_NO_5502)
								|| svtcode.equals(MsgConstant.VOUCHER_NO_2502)
								|| svtcode.equals(MsgConstant.VOUCHER_NO_3503)
								|| svtcode.equals(MsgConstant.VOUCHER_NO_3504)
								|| svtcode.equals(MsgConstant.VOUCHER_NO_3505)
								|| svtcode.equals(MsgConstant.VOUCHER_NO_3506)
								||("3507350835093510351135123513".contains(voucherDto.getSvtcode())&&ITFECommonConstant.PUBLICPARAM.contains(",returnvoucher=self,")))
							continue;
						else
						{
							String Xml = savexml.replaceAll("&lt;&lt;", "《").replaceAll("&gt;&gt;", "》").replaceAll("&lt;","《").replaceAll("&gt;", "》");
							VoucherUtil.sendTips(voucherDto, ls_FinOrgCode,dealnos, Xml);
						}
					}
				} catch (Exception e) {
					logger.error("凭证读取" + svtcode + "报文出现异常：" + e);
					VoucherException.saveErrInfo(svtcode, e);
					continue;
				}
			}
		} catch (Exception e) {
			logger.error("凭证读取报文出现异常：" + e);
			VoucherException.saveErrInfo(null, e);
		}finally{
			isread = true;
			if(execDetail!=null)
				execDetail.closeConnection();
		}
		if (disablecount == list.size()) {
			count = -1;
			return count;
		}

		return count;

	}
	/**
	 * 凭证生成
	 * 
	 * @throws ITFEBizException
	 * 
	 */
	public List voucherGenerate(List list) throws ITFEBizException {
		Integer count = new Integer(0);
		List resultList = new ArrayList();
		List<TvVoucherinfoDto> vList = list;
		IVoucherDto2Map voucher = (IVoucherDto2Map) ContextFactory.getApplicationContext().getBean(MsgConstant.VOUCHER_DTO2MAP + vList.get(0).getSvtcode());
		StringBuffer error = new StringBuffer("");
		for (TvVoucherinfoDto vDto : vList) {
			try {
				if (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3504)
						|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3208)
						||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3268)
						|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3505)
						|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3503)
						|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3551)
						|| vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3554)) {
					return voucher.voucherGenerate(vDto);
				}
				List lists = null;
				lists = voucher.voucherGenerate(vDto);
				List<IDto> dtolist = null;
				IDto[] dtos = null;
				if (lists != null && lists.size() > 0) {
					for (List mapList : (List<List>) lists) {
						dtolist = new ArrayList<IDto>();
						if (count == 0 && VoucherUtil.isVoucherRepeat(vDto)) {
							resultList.add(-1 + "");
							resultList.add(vDto.getStrecode());
							resultList.add(count + "");
							return resultList;
						}
						VoucherUtil.sendTips((TvVoucherinfoDto) mapList.get(1),(HashMap) mapList.get(0));
						dtolist.add((TvVoucherinfoDto) mapList.get(1));
						if (mapList.size()==3 && !mapList.get(2).getClass().isArray()&& mapList.get(2) instanceof TvVoucherinfoAllocateIncomeDto) {
							DatabaseFacade.getODB().update((TvVoucherinfoAllocateIncomeDto)mapList.get(2));
							mapList.remove(2);
						}
						if(ITFECommonConstant.PUBLICPARAM.contains(",bussreport=insert,"))
						{
							if (mapList.size() > 2)
									dtolist.addAll((List<IDto>) mapList.get(2));
						}
						List<IDto> tempList = null;
						Map<String, List<IDto>> dataMap = new HashMap<String, List<IDto>>();
						for (IDto idto : dtolist) {
							if (dataMap.get(idto.getClass().getName()) == null) {
								tempList = new ArrayList<IDto>();
								tempList.add(idto);
								dataMap.put(idto.getClass().getName(),tempList);
							} else
								dataMap.get(idto.getClass().getName()).add(idto);
						}
						for (String mapkey : dataMap.keySet()) {
							tempList = dataMap.get(mapkey);
							dtos = new IDto[tempList.size()];
							tempList.toArray(dtos);
							DatabaseFacade.getODB().create(dtos);
						}
						count++;
					}
					if (MsgConstant.VOUCHER_NO_2507.equals(vDto.getSvtcode())|| MsgConstant.VOUCHER_NO_5507.equals(vDto.getSvtcode())) {
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_3507);
						vDto.setSstatus(DealCodeConstants.VOUCHER_GENRETURN);
						vDto.setSdemo("生成回单成功");
						DatabaseFacade.getODB().update(vDto);
					} else if (MsgConstant.VOUCHER_NO_5508.equals(vDto.getSvtcode())) {
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_3508);
						vDto.setSstatus(DealCodeConstants.VOUCHER_GENRETURN);
						DatabaseFacade.getODB().update(vDto);
					} else if (MsgConstant.VOUCHER_NO_5509.equals(vDto.getSvtcode())) {
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_3509);
						vDto.setSstatus(DealCodeConstants.VOUCHER_GENRETURN);
						DatabaseFacade.getODB().update(vDto);
					} else if (MsgConstant.VOUCHER_NO_5510.equals(vDto.getSvtcode())) {
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_3510);
						vDto.setSstatus(DealCodeConstants.VOUCHER_GENRETURN);
						DatabaseFacade.getODB().update(vDto);
					} else if (MsgConstant.VOUCHER_NO_5511.equals(vDto.getSvtcode())) {
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_3511);
						vDto.setSstatus(DealCodeConstants.VOUCHER_GENRETURN);
						DatabaseFacade.getODB().update(vDto);
					} else if (MsgConstant.VOUCHER_NO_5512.equals(vDto.getSvtcode())) {
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_3512);
						vDto.setSstatus(DealCodeConstants.VOUCHER_GENRETURN);
						DatabaseFacade.getODB().update(vDto);
					} else if (MsgConstant.VOUCHER_NO_5513.equals(vDto.getSvtcode())) {
						vDto.setSvtcode(MsgConstant.VOUCHER_NO_3513);
						vDto.setSstatus(DealCodeConstants.VOUCHER_GENRETURN);
						DatabaseFacade.getODB().update(vDto);
					}
				}
			} catch (JAFDatabaseException e) {
				logger.error(e.getCause());
				error.append(e.getCause());
				continue;
			}catch (ITFEBizException e) {
				logger.error(e.getCause());
				error.append(e.getCause());
				continue;
			}catch (Exception e) {
				logger.error(e.getCause());
				error.append(e.getCause());
				continue;
			}finally{
				if(error.length()>5)
				{
					logger.error(error.toString());
					throw new ITFEBizException(error.toString());
				}
			}
		}
		resultList.add(count.intValue() + "");
		return resultList;

	}

	/**
	 * 凭证校验
	 * 
	 */
	public int voucherVerify(List lists, String vtcode) throws ITFEBizException {
		VoucherVerify voucherVerify = new VoucherVerify();
		return voucherVerify.verify(lists, vtcode);
	}

	public Integer sendMsg(List lists, TvVoucherinfoDto vDto, Integer count)
			throws ITFEBizException, JAFDatabaseException {
		if (lists != null && lists.size() > 0) {
			List<IDto> dtolist = new ArrayList<IDto>();
			IDto[] dtos = null;
			for (List mapList : (List<List>) lists) {
				VoucherUtil.sendTips((TvVoucherinfoDto) mapList.get(1),
						(HashMap) mapList.get(0));
				dtolist.add((TvVoucherinfoDto) mapList.get(1));
				if (mapList.size() > 2)
					dtolist.addAll((List<IDto>) mapList.get(2));
				List<IDto> tempList = null;
				Map<String, List<IDto>> dataMap = new HashMap<String, List<IDto>>();
				for (IDto idto : dtolist) {
					if (dataMap.get(idto.getClass().getName()) == null) {
						tempList = new ArrayList<IDto>();
						tempList.add(idto);
						dataMap.put(idto.getClass().getName(), tempList);
					} else
						dataMap.get(idto.getClass().getName()).add(idto);
				}
				for (String mapkey : dataMap.keySet()) {
					tempList = dataMap.get(mapkey);
					dtos = new IDto[tempList.size()];
					tempList.toArray(dtos);
					DatabaseFacade.getODB().create(dtos);
				}
				count++;
			}
		}
		return count;
	}

	/**
	 * 凭证校验(用于陕西特色业务--接收财政系统)
	 * 
	 * @author sunyan
	 */
	public int voucherVerifyForSX(List lists, String vtcode)
			throws ITFEBizException {
		VoucherVerifySX voucherVerify = new VoucherVerifySX();
		return voucherVerify.verify(lists, vtcode);
	}

	/**
	 * 凭证提交
	 * 
	 * @throws ITFEBizException
	 * @throws ITFEBizException
	 * 
	 */
	public int voucherCommit(List voucherList) throws ITFEBizException {
		int count = 0;
		for (List<TvVoucherinfoDto> list : (List<List>) voucherList) {
			count += VoucherPackageGenerate.packageGenerateByVtcode(list, list.get(0).getSvtcode());
		}
		logger.debug("===================凭证发送TIPS成功====================");

		return count;

	}

	/**
	 * 凭证签章
	 * 
	 */
	public int voucherStamp(List lists) throws ITFEBizException {
		int count = 0;
		VoucherService voucherService = new VoucherService();
		String stampXML = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		for (List list : (List<List>) lists) {
			// 签章用户信息
			TsUsersDto uDto = (TsUsersDto) list.get(0);
			// 国库信息
			TsTreasuryDto tDto = (TsTreasuryDto) list.get(1);
			// 签章位置信息
			TsStamppositionDto sDto = (TsStamppositionDto) list.get(2);
			int size = Integer.parseInt(list.get(3) + "");
			List vList = (List) list.get(4);
			for (List sinList : (List<List>) vList) {
				String err = null;
				vDto = (TvVoucherinfoDto) sinList.get(0);
				String certID = "";
				String stampID = "";
				if (sDto.getSstamptype().equals(
						MsgConstant.VOUCHERSAMP_OFFICIAL)) {
					if(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS_BACK)){
						certID = tDto.getSattachcertid();
						stampID = tDto.getSattachid();
						size = 1;
					}else{
						certID = tDto.getScertid();
						stampID = tDto.getSstampid();
					}
				} else if (sDto.getSstamptype().equals(
						MsgConstant.VOUCHERSAMP_ROTARY)) {
					if(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS_BACK)){
						certID = tDto.getSattachcertid();
						stampID = tDto.getSattachid();
						size = 1;
					}else{
						certID = tDto.getSrotarycertid();
						stampID = tDto.getSrotaryid();
					}
				} else if (sDto.getSstamptype().equals(
						MsgConstant.VOUCHERSAMP_ATTACH)) {
					if(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_FAIL_TCBS)){
						size = 1;
						certID = vDto.getStrecode();
						stampID = tDto.getSattachid();
						vDto.setSadmdivcode(vDto.getSadmdivcode()+MsgConstant.VOUCHERSAMP_ATTACH);
					}else
					{
						certID = vDto.getStrecode();;
						stampID = tDto.getSattachid();
					}
				}else if (sDto.getSstamptype().equals(
						MsgConstant.VOUCHERSAMP_BUSS)) {
					if(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_FAIL_TCBS)){
						size = 1;
						certID = vDto.getStrecode();
						stampID = tDto.getSattachcertid();
					}else
					{
						certID =vDto.getStrecode();
						stampID = tDto.getSattachcertid();
					}
				} else {
					certID = uDto.getScertid();
					stampID = uDto.getSstampid();
				}
				if (sinList.size() == 1) {
					// 服务器签章
					String admdivcode = vDto.getSadmdivcode();
					admdivcode = admdivcode.replace(MsgConstant.VOUCHERSAMP_ATTACH, "");
					err = voucherService.signStampByNos(certID, admdivcode, Integer.parseInt(vDto
							.getSstyear()), vDto.getSvtcode(), VoucherUtil
							.getVoucherStampXML(vDto).getBytes(), VoucherUtil
							.getStampPotisionXML(sDto.getSstampposition(),
									stampID), vDto.getSvoucherno(), sDto
							.getSstampname());
				} else {
					stampXML = sinList.get(1) + "";
					err = stampXML.split("@")[0];// 客户端签章辅助标志：@或空值 表示客户端签章失败
					if (stampXML.equals(""))
						err = sDto.getSstampname() + "签章(名)异常";
					else {
						if (stampXML.indexOf("@") < 0) {
							// 客户端调用签名
							if (sDto.getSstamptype().endsWith(
									MsgConstant.VOUCHERSIGN_PRIVATE)) {
								err = voucherService.saveSignVoucher(certID,
										vDto.getSadmdivcode(), Integer
												.parseInt(vDto.getSstyear()),
										vDto.getSvtcode(), stampXML.getBytes());
							} else {
								err = voucherService.saveStampVoucher(certID,
										vDto.getSadmdivcode(), Integer
												.parseInt(vDto.getSstyear()),
										vDto.getSvtcode(), stampXML.getBytes(),
										sDto.getSstampname());

							}
						} else {
							// 客户端签章失败
							err = sDto.getSstampname()
									+ "签章异常:"
									+ (err.indexOf("Exception:") == -1 ? err
											: err.substring(err
													.indexOf("Exception:")
													+ "Exception:".length(),
													err.length()));
						}
					}
				}// 签章成功
				if(err!=null&&(err.contains("位置已签章")||err.contains("位置已盖章")||((!err.contains("业务逻辑不合法"))&&err.contains("已发送"))))
					err="";
				if (StringUtils.isBlank(err)) {
					String stampid = vDto.getSstampid();
					if (stampid == null || stampid.equals("")) {
						vDto.setSdemo("已签章：" + sDto.getSstampname());
						// 签章位置只有1个时凭证状态为签章成功
						if (size == 1)
							vDto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
					} else {
						// 签章位置多个时更新凭证状态
						if ((stampid.split(",").length + 1) == size) {
							vDto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
						}
						vDto.setSdemo(vDto.getSdemo() + "、"
								+ sDto.getSstampname());
					}
					// 转讫章签章成功更新签章用户痕迹
					if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_OFFICIAL))
						vDto.setSstampuser(vDto.getSstampuser() == null ? (""
								+ uDto.getSusercode() + "#,") : vDto
								.getSstampuser()
								+ uDto.getSusercode() + "#,");
					// 转讫章签章成功更新签章用户痕迹
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_ROTARY))
						vDto.setSstampuser(vDto.getSstampuser() == null ? (""
								+ uDto.getSusercode() + "!,") : vDto
								.getSstampuser()
								+ uDto.getSusercode() + "!,");
					// 附件章签章成功更新签章用户痕迹
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_ATTACH))
						vDto.setSstampuser(vDto.getSstampuser() == null ? (""
								+ uDto.getSusercode() + "^,") : vDto
								.getSstampuser()
								+ uDto.getSusercode() + "^,");
					// 业务专用章签章成功更新签章用户痕迹
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_BUSS))
						vDto.setSstampuser(vDto.getSstampuser() == null ? (""
								+ uDto.getSusercode() + "&,") : vDto
								.getSstampuser()
								+ uDto.getSusercode() + "&,");
					// 私章签章成功更新签章用户痕迹
					else
						vDto.setSstampuser(vDto.getSstampuser() == null ? (""
								+ uDto.getSusercode() + ",") : vDto
								.getSstampuser()
								+ uDto.getSusercode() + ",");
					// 签章成功更新印章痕迹
					vDto.setSstampid(vDto.getSstampid() == null ? (""
							+ sDto.getSstamptype() + ",") : vDto.getSstampid()
							+ sDto.getSstamptype() + ",");
					vDto.setSreturnerrmsg(null);
					List<TvVoucherinfoDto> vouList = new ArrayList<TvVoucherinfoDto>();
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0&&MsgConstant.VOUCHERSAMP_ATTACH.equals(sDto.getSstamptype()))
						vDto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
					vouList.add(vDto);
					// 签章操作成功更新凭证状态
					VoucherUtil.updateVoucherState(vouList);
					logger
							.debug("===============================凭证签章成功==============================");
					count++;
				} else {
					// 签章操作失败新凭证状态
					vDto.setSreturnerrmsg(err);
					List<TvVoucherinfoDto> vouList = new ArrayList<TvVoucherinfoDto>();
					vouList.add(vDto);
					VoucherUtil.updateVoucherState(vouList);
					logger.error(err);
				}
			}
		}
		return count;
	}

	/**
	 * 取消指定位置签章
	 * 
	 */
	public int voucherStampCancle(List lists) throws ITFEBizException {
		int count = 0;
		try{
			VoucherService voucherService = new VoucherService();
			for (List list : (List<List>) lists) {
				TsTreasuryDto tDto = (TsTreasuryDto) list.get(0);
				TsStamppositionDto sDto = (TsStamppositionDto) list.get(1);
				List vList = (List) list.get(2);
				for (List sinList : (List<List>) vList) {
					String certID = "";
					String err = null;
					TsUsersDto uDto = (TsUsersDto) sinList.get(1);
					if (sDto.getSstamptype().equals(MsgConstant.VOUCHERSAMP_ROTARY))
						certID = tDto.getSrotarycertid();
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_OFFICIAL))
						certID = tDto.getScertid();
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_ATTACH))
						certID = tDto.getSattachid();
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_BUSS))
						certID = tDto.getSattachcertid();
					else
						certID = uDto.getScertid();
					TvVoucherinfoDto vDto = (TvVoucherinfoDto) sinList.get(0);
					// 撤销指定位置凭证签章
					err = voucherService.cancelStampWithPosition(certID, vDto
							.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),
							vDto.getSvtcode(), vDto.getSvoucherno(), sDto
									.getSstampposition());
					// 撤销凭证签章成功
					if (err == null || err.equals("")) {
						if(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_STAMP)){
							vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
						}
						if("7173".contains(vDto.getSstatus()))//处理成功或签章成功
						{
							if (vDto.getSstampid().split(",").length == 1)
								vDto.setSdemo("处理成功");
							else {
								if ((vDto.getSdemo().indexOf(sDto.getSstampname()) + sDto
										.getSstampname().length()) == vDto.getSdemo()
										.length())
									vDto.setSdemo(vDto.getSdemo().substring(
											0,
											vDto.getSdemo().indexOf(
													sDto.getSstampname()) - 1));
								else if(vDto.getSdemo().contains(sDto.getSstampname()))
									vDto.setSdemo(vDto.getSdemo().substring(
											0,
											vDto.getSdemo().indexOf(
													sDto.getSstampname()))
											+ vDto.getSdemo().substring(
													vDto.getSdemo().indexOf(
															sDto.getSstampname())
															+ (sDto.getSstampname())
																	.length() + 1,
													vDto.getSdemo().length()));
								else
									vDto.setSdemo("撤消"+sDto.getSstampname()+"签章成功!");
							}
						}
						// 撤销签章成功更新印章痕迹
						vDto.setSstampid(vDto.getSstampid().substring(
								0,
								vDto.getSstampid().indexOf(
										sDto.getSstamptype() + ","))
								+ vDto.getSstampid().substring(
										vDto.getSstampid().indexOf(
												sDto.getSstamptype() + ",")
												+ (sDto.getSstamptype() + ",")
														.length(),
										vDto.getSstampid().length()));
						// 撤销签章成功更新签章用户痕迹
						if (sDto.getSstamptype().equals(
								MsgConstant.VOUCHERSAMP_OFFICIAL))
							vDto.setSstampuser(vDto.getSstampuser().substring(
									0,
									vDto.getSstampuser().indexOf(
											uDto.getSusercode() + "#,"))
									+ vDto.getSstampuser().substring(
											vDto.getSstampuser().indexOf(
													uDto.getSusercode() + "#,")
													+ (uDto.getSusercode() + "#,")
															.length(),
											vDto.getSstampuser().length()));
						else if (sDto.getSstamptype().equals(
								MsgConstant.VOUCHERSAMP_ROTARY))
							vDto.setSstampuser(vDto.getSstampuser().substring(
									0,
									vDto.getSstampuser().indexOf(
											uDto.getSusercode() + "!,"))
									+ vDto.getSstampuser().substring(
											vDto.getSstampuser().indexOf(
													uDto.getSusercode() + "!,")
													+ (uDto.getSusercode() + "!,")
															.length(),
											vDto.getSstampuser().length()));
						else if (sDto.getSstamptype().equals(
								MsgConstant.VOUCHERSAMP_ATTACH))
							vDto.setSstampuser(vDto.getSstampuser().substring(
									0,
									vDto.getSstampuser().indexOf(
											uDto.getSusercode() + "^,"))
									+ vDto.getSstampuser().substring(
											vDto.getSstampuser().indexOf(
													uDto.getSusercode() + "^,")
													+ (uDto.getSusercode() + "^,")
															.length(),
											vDto.getSstampuser().length()));
						else if (sDto.getSstamptype().equals(
								MsgConstant.VOUCHERSAMP_BUSS))
							vDto.setSstampuser(vDto.getSstampuser().substring(
									0,
									vDto.getSstampuser().indexOf(
											uDto.getSusercode() + "&,"))
									+ vDto.getSstampuser().substring(
											vDto.getSstampuser().indexOf(
													uDto.getSusercode() + "&,")
													+ (uDto.getSusercode() + "&,")
															.length(),
											vDto.getSstampuser().length()));
						else
							vDto.setSstampuser(vDto.getSstampuser().substring(
									0,
									vDto.getSstampuser().indexOf(
											uDto.getSusercode() + ","))
									+ vDto.getSstampuser().substring(
											vDto.getSstampuser().indexOf(
													uDto.getSusercode() + ",")
													+ (uDto.getSusercode() + ",")
															.length(),
											vDto.getSstampuser().length()));
						vDto.setSreturnerrmsg(null);
						List<TvVoucherinfoDto> vouList = new ArrayList<TvVoucherinfoDto>();
						vouList.add(vDto);
						// 撤销签章操作失败新凭证状态
						count += VoucherUtil.updateVoucherState(vouList);
						logger
								.debug("===============================凭证撤销签章操作成功===============================");
					} else {
						// 撤销签章操作失败新凭证状态
						vDto.setSreturnerrmsg(err);
						List<TvVoucherinfoDto> vouList = new ArrayList<TvVoucherinfoDto>();
						vouList.add(vDto);
						VoucherUtil.updateVoucherState(vouList);
						logger.error(err);
					}
				}
			}
		}catch(Exception e)
		{
			logger.error("撤-消-签-章-发生异常:", e);
			throw new ITFEBizException(e);
		}
		return count;

	}

	/**
	 * 发送回单
	 * 
	 * 一、 已签章成功凭证发送电子凭证库： 直接发送电子凭证库 二、 系统自主生成不签章凭证发送电子凭证库： 1 凭证签名 2 发送电子凭证库 三、
	 * 回单状态为失败等状态的凭证重新发送电子凭证库： 直接发送电子凭证库 四、 回单状态为已退回的凭证重新发送电子凭证库： 1 凭证作废 2
	 * 发送电子凭证库,
	 * 修改记录采用：采用服务器带位置自动签名的凭证进行了单独判断和发送，不再采用公共的发送，发送后更新状态语句，放在了发送的if语句外cyg
	 * 20141226。
	 */
	public int voucherSendReturnSuccess(List list) throws ITFEBizException {
		int count = 0;
		String err = null;
		VoucherService voucherService = new VoucherService();
		for (List<TvVoucherinfoDto> voucherList : (List<List>) list) {
			for (TvVoucherinfoDto vDto : voucherList) {
				boolean signByNosFlag = false;// 凭证签名判断标志 true 签名成功
				boolean discardVoucherFlag = false;// 凭证作废判断标志 true 作废成功
				String repeatVoucherFlag = (StringUtils.isBlank(vDto
						.getSreturnerrmsg())) ? "0" : (vDto.getSreturnerrmsg()
						.indexOf("$") == -1 ? "0"
						: vDto.getSreturnerrmsg().substring(
								vDto.getSreturnerrmsg().lastIndexOf("$") + 1));
				// 重新发送凭证辅助标志： 0 不重发 1 回单状态为失败等状态的凭证 2 回单状态为已退回的凭证
				// 3 [财政机构]回单状态为失败等状态的凭证 4 [代理银行]回单状态为失败等状态的凭证
				// 5 [财政机构]和[代理银行]回单状态均为失败等状态的凭证
				// 凭证签名用于上海的签名
				if (StringUtils.isBlank(vDto.getSstampid())) {
					HashMap<String, String> map = null;
					try {
						// 签名发送正常流程
						if ("0".equals(repeatVoucherFlag)) {
							map = VoucherUtil.getStampPotisionXML(vDto);
							// 所有对账和报表及其他采用服务器端带位置签名的报文均默认先发送到财政凭证库
							err = voucherService
									.signByNosAndSend(
											map.get("signcertid"),
											vDto.getSadmdivcode(),
											Integer.parseInt(vDto.getSstyear()),
											vDto.getSvtcode(),
											VoucherUtil
													.getVoucherStampXML(vDto)
													.getBytes(),
											map.get("signposxml"),
											StateConstant.ORGCODE_RH,
											ITFECommonConstant.PUBLICPARAM.contains(","+vDto.getSvtcode()+"sendtype=center,")?"999":StateConstant.ORGCODE_FIN);
							// 判断凭证编号如果是3507的，需要同时发送给对应的代理银行的电子凭证库
							if (vDto.getSvtcode().equals(
									MsgConstant.VOUCHER_NO_3507)
									&& !"011".equals(vDto.getSpaybankcode())
									&& !"".equals(vDto.getSpaybankcode())) {
								err = voucherService.sendVoucherFullSigns(map
										.get("signcertid"), vDto
										.getSadmdivcode(), VoucherUtil
										.getSpaybankType(vDto.getStrecode(),
												vDto.getSpaybankcode()),
										Integer.parseInt(vDto.getSstyear()),
										vDto.getSvtcode(), // 发送银行电子凭证库
										new String[] { vDto.getSvoucherno() });
							}
							// 确保程序不能继续执行其他if语句
							signByNosFlag = false;
							repeatVoucherFlag = "stop";
						} else {// 如果已经发送过，可以调用重新发送
							signByNosFlag = true;
						}
					} catch (Exception e) {
						logger.error(e);
						throw new ITFEBizException("获取服务器端签名位置出错！", e);
					}
				}
				// 凭证作废
				if (repeatVoucherFlag.equals("2")) {
					err = voucherService.discardVoucher(null, vDto
							.getSadmdivcode(), Integer.parseInt(vDto
							.getSstyear()), vDto.getSvtcode(),
							new String[] { vDto.getSvoucherno() });
					if (StringUtils.isBlank(err))
						discardVoucherFlag = true;
				}
				// 发送凭证
				if ((StringUtils.isBlank(vDto.getSstampid()) && signByNosFlag)
						|| (vDto.getSstatus().equals(
								DealCodeConstants.VOUCHER_STAMP) && StringUtils
								.isNotBlank(vDto.getSstampid()))
						|| (repeatVoucherFlag.equals("2") && discardVoucherFlag)
						|| repeatVoucherFlag.equals("1")
						|| repeatVoucherFlag.equals("3")
						|| repeatVoucherFlag.equals("4")
						|| repeatVoucherFlag.equals("5")) {
					if(ITFECommonConstant.PUBLICPARAM.contains(",sendvouchersort,"))
					{
						if (!(ITFECommonConstant.PUBLICPARAM.contains(",sh,")
								&& (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301) || vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)
										||(vDto.getStrecode().startsWith("06")&&vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408)))
								&& StringUtils.isBlank(vDto.getSpaybankcode()))) {
							if (!("011".equals(vDto.getSpaybankcode()))) {
								if (!(StateConstant.ACCOUNT_CHECK.contains(vDto.getSvtcode()) || vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207))) {
									err = voucherService.sendVoucher(null, vDto
											.getSadmdivcode(), VoucherUtil
											.getDecOrgType(vDto), Integer
											.parseInt(vDto.getSstyear()), vDto
											.getSvtcode(), new String[] { vDto
											.getSvoucherno() });// 发送财政或银行电子凭证库
								}
							}
						}
					}
					/*
					 * 2301 2302 3551 3507 等需要发送财政和银行的签章报文发送财政
					 * 
					 * 1、已签章成功凭证发送财政电子凭证库 2、[财政机构]回单状态为失败等状态的凭证重发财政电子凭证库
					 * 3、回单状态为已退回的凭证重发财政电子凭证库
					 */
					if (((vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)
							||(vDto.getStrecode().startsWith("06")&&vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408))
							||vDto.getSvtcode().equals(
									MsgConstant.VOUCHER_NO_2302)
							|| vDto.getSvtcode().equals(
									MsgConstant.VOUCHER_NO_3551)
							|| vDto.getSvtcode().equals(
									MsgConstant.VOUCHER_NO_3507) || StateConstant.ACCOUNT_CHECK
							.contains(vDto.getSvtcode())) && (vDto.getSstatus()
							.equals(DealCodeConstants.VOUCHER_STAMP) || (repeatVoucherFlag
							.equals("2") && discardVoucherFlag)))
							|| repeatVoucherFlag.equals("3")
							|| repeatVoucherFlag.equals("5")
							|| vDto.getSvtcode().equals(
									MsgConstant.VOUCHER_NO_8207)) {
						/*
						 * 上海系统自动生成的2301和2302只发财政，所以只有授权2301、2302需要给商行发送
						 * 系统生成的2301和2302时，索引表S_PAYBANKCODE 为空
						 */
						String tmporg;
						if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
								&& (vDto.getSvtcode().equals(
										MsgConstant.VOUCHER_NO_2301) || vDto
										.getSvtcode().equals(
												MsgConstant.VOUCHER_NO_2302))
								&& StringUtils.isBlank(vDto.getSpaybankcode())) {
							tmporg = StateConstant.ORGCODE_FIN;
						} else {
							tmporg = (vDto.getSvtcode()
									.equals(MsgConstant.VOUCHER_NO_8207)||"35122".equals(vDto.getSext5())) ? VoucherUtil
									.getSpaybankType(vDto.getStrecode(), vDto
											.getSpaybankcode())
									: StateConstant.ORGCODE_FIN;
							if((ITFECommonConstant.PUBLICPARAM.contains(",send3508=more,")&&vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3508))||(ITFECommonConstant.PUBLICPARAM.contains(",send3510=more,")&&vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3510)))
							{
								if(vDto.getSext5()!=null&&vDto.getSext5().length()==3)
									tmporg = vDto.getSext5();
							}
						}
						if(!(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0))
						{
							err = voucherService.sendVoucherFullSigns(null, vDto.getSadmdivcode(), tmporg,
							Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(),new String[] { vDto.getSvoucherno() });// 发送财政电子凭证库
							if (StringUtils.isNotBlank(err)) {
								// 发送财政操作失败更新凭证状态
								vDto.setSreturnerrmsg(err);
								VoucherUtil.updateVoucherState(vDto);
								continue;
							}
						}
					}
					// [财政机构]回单状态为失败等状态的凭证已重发，不再发送财政电子凭证库
					if (repeatVoucherFlag.equals("3")) {
						count += 1;
						continue;
					}
					/*
					 * 签章/签名成功报文发送电子凭证库
					 * 
					 * 1、已签章成功凭证发送电子凭证库 2、系统自主生成不签章已签名成功凭证发送电子凭证库
					 * 3、回单状态为失败等状态的凭证重发电子凭证库 4、[代理银行]回单状态为失败等状态的凭证重发银行电子凭证库
					 * 5、回单状态为已退回的凭证重发财政电子凭证库
					 */
					if(!ITFECommonConstant.PUBLICPARAM.contains(",sendvouchersort,"))
					{
						if (!(ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0
								&& (vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301) || vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)
										|| ((vDto.getStrecode().startsWith("06") && vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408))))
								&& StringUtils.isBlank(vDto.getSpaybankcode()))) {
							if (!("011".equals(vDto.getSpaybankcode()))) {
								if (!(StateConstant.ACCOUNT_CHECK.contains(vDto.getSvtcode()) || vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_8207))) {
									err = voucherService.sendVoucher(null, vDto
											.getSadmdivcode(), VoucherUtil
											.getDecOrgType(vDto), Integer
											.parseInt(vDto.getSstyear()), vDto
											.getSvtcode(), new String[] { vDto
											.getSvoucherno() });// 发送财政或银行电子凭证库
								}
							}
						}
					}
					// else {
					// if ((ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)
					// && (vDto.getSvtcode().equals(
					// MsgConstant.VOUCHER_NO_2301) || vDto
					// .getSvtcode().equals(
					// MsgConstant.VOUCHER_NO_2302))
					// && StringUtils.isNotBlank(vDto
					// .getSpaybankcode())) {
					// err = voucherService.sendVoucher(null, vDto
					// .getSadmdivcode(), VoucherUtil
					// .getDecOrgType(vDto), Integer.parseInt(vDto
					// .getSstyear()), vDto.getSvtcode(),
					// new String[] { vDto.getSvoucherno() });// 发送财政或银行电子凭证库
					// }
					// }
				}
				if(err!=null&&err.contains("EVS700#业务逻辑不合法")&&err.contains("已发送"))
				{
					err = "智能纠错,更新状态为已回单!";
				}
				if (err == null || err.equals("")||err.equals("智能纠错,更新状态为已回单!")) {
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)
					{
						vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_BACK);
						vDto.setSdemo("退票成功");
					}else
					{
						vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS);
						vDto.setSdemo("发送电子凭证库成功");
					}
					vDto.setSvoucherflag("1");
					vDto.setSreturnerrmsg(err);
					vDto.setSconfirusercode(TimeFacade.getCurrentStringTime());// 设置凭证回单/发送电子凭证库日期
					// 发送电子凭证库操作成功更新凭证状态
					count += VoucherUtil.updateVoucherState(vDto);
					if(vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5408)||vDto.getSvtcode().equals(MsgConstant.VOUCHER_NO_5671))
					{
						TvNontaxmainPK maink = new TvNontaxmainPK();
						maink.setSdealno(vDto.getSdealno());
						try {
							TvNontaxmainDto maindto = (TvNontaxmainDto)DatabaseFacade.getDb().find(maink);
							if(maindto!=null)
							{
								maindto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS);
								DatabaseFacade.getDb().update(maindto);
							}
						} catch (JAFDatabaseException e) {
						}
					}
					paymentCalculation(vDto);
					logger
							.debug("===============================发送电子凭证库成功===============================");
					continue;
				}else
				{
					if(err!=null&&(err.contains("最大版本号凭证已签收")||err.contains("无需重新发送")||err.contains("成功签收")||err.contains("重复发送")))
					{
						vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS);
						vDto.setSdemo("发送电子凭证库成功");
						vDto.setSvoucherflag("1");
						vDto.setSconfirusercode(TimeFacade.getCurrentStringTime());// 设置凭证回单/发送电子凭证库日期
						count += VoucherUtil.updateVoucherState(vDto);
						paymentCalculation(vDto);
					}else{
						if(ITFECommonConstant.PUBLICPARAM.contains(",sendvoucher=searchstatus,"))
						{
							Map<String, Object[]> map = voucherService.batchQueryAllVoucherStatus("", vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(), new String[]{vDto.getSvoucherno()});
							String msg = String.valueOf(map==null?"":map.get(vDto.getSvoucherno()));
							if(!msg.contains("13")){
								vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS);
								vDto.setSdemo("发送电子凭证库成功");
								vDto.setSvoucherflag("1");
								vDto.setSconfirusercode(TimeFacade.getCurrentStringTime());// 设置凭证回单/发送电子凭证库日期
								count += VoucherUtil.updateVoucherState(vDto);
								paymentCalculation(vDto);
							}
						}
					}
				}
				// 发送电子凭证库操作失败更新凭证状态
				vDto.setSreturnerrmsg(err);
				VoucherUtil.updateVoucherState(vDto);
			}
		}
		return count;
	}
	public void paymentCalculation(TvVoucherinfoDto dto)
	{
		try
		{
			if(dto!=null&&ITFECommonConstant.PUBLICPARAM.contains("paymentCalculation=true"))
			{
				if(",5106,5108,2301,2302,".contains(dto.getSvtcode()))
				{
					TnConpaycheckbillDto querydto = new TnConpaycheckbillDto();//
					if(MsgConstant.VOUCHER_NO_5108.equals(dto.getSvtcode()))
						querydto.setCamtkind(StateConstant.Conpay_Direct);//直接支付额度
					else if(MsgConstant.VOUCHER_NO_5106.equals(dto.getSvtcode()))
						querydto.setCamtkind(StateConstant.Conpay_Grant);//授权支付额度
					else
						querydto.setCamtkind(dto.getSext1());//支付方式
				}
			}
		}catch(Exception e)
		{
			logger.debug(dto==null?"":dto.getSvtcode()+"前置计算集中支付数据==================================="+dto==null?"":dto.getSdealno());
		}
	}
	/**
	 * 凭证退回
	 * 
	 */
	public int voucherReturnBack(List list) throws ITFEBizException {

		VoucherService voucherService = new VoucherService();
		int count = 0;
		for (List<TvVoucherinfoDto> voucherList : (List<List>) list) {
			String status = voucherList.get(0).getSstatus();
			String sdemo = voucherList.get(0).getSdemo();
			TvVoucherinfoDto vDto = null;
			String[] voucherNos = new String[voucherList.size()];
			String[] failInfos = new String[voucherList.size()];
			for (int i = 0; i < voucherList.size(); i++) {
				vDto = voucherList.get(i);
				voucherNos[i] = vDto.getSvoucherno();
				failInfos[i] = vDto.getSdemo().length() > 50 ? vDto.getSdemo()
						.substring(0, 50) : vDto.getSdemo();
				vDto.setSstatus(DealCodeConstants.VOUCHER_FAIL);
				// vDto.setSdemo("退回成功");
				// 退回后 保留demo原因 有利用户排查原因
				vDto.setSdemo(vDto.getSdemo().length() >= 1000 ? vDto
						.getSdemo() : "退回原因：" + vDto.getSdemo());
				vDto.setSreturnerrmsg(null);
				vDto.setSconfirusercode(TimeFacade.getCurrentStringTime());// 设置凭证退回日期
			}
			try{
				voucherService.confirmVoucherfail(null, vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(), voucherNos, failInfos);
			}catch(Exception e){
			}
			String err = voucherService.returnVoucherBack(null, vDto
					.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),
					vDto.getSvtcode(), voucherNos, failInfos);
			if(err!=null&&err.contains("已退回或"))
				err = null;
			// 更新凭证状态
			if (err == null) {
				count += VoucherUtil.updateVoucherState(voucherList);
				VoucherUtil.voucherReturnBackUpdateStatus(voucherList);
				logger
						.debug("===============================凭证退回成功===============================");

			} else {
				for (int i = 0; i < voucherList.size(); i++) {
					vDto = voucherList.get(i);
					voucherNos[i] = vDto.getSvoucherno();
					vDto.setSstatus(status);
					vDto.setSdemo(sdemo);
					vDto.setSreturnerrmsg(err);
				}
				VoucherUtil.updateVoucherState(voucherList);
			}

		}
		return count;

	}

	/**
	 * 查找发送回单后凭证状态
	 * 
	 */
	public int voucherReturnQueryStatus(List voucherList)
			throws ITFEBizException {
		int count = 0;
		Map map = null;
		for (List<TvVoucherinfoDto> list : (List<List>) voucherList) {

			VoucherService voucherService = new VoucherService();
			String sdemo = "";
			for (TvVoucherinfoDto dto : (List<TvVoucherinfoDto>) list) {
				if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3405)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3406)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3507)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3508)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3509)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3510)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3511)
						|| (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3512)&&!"35122".equals(dto.getSext5()))
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3513)) {
					map = voucherService.queryVoucherStatusByOrgType(null, dto
							.getSadmdivcode(), Integer.parseInt(dto
							.getSstyear()), dto.getSvtcode(),
							new String[] { dto.getSvoucherno() },
							StateConstant.ORGCODE_FIN);
					Iterator it = map.keySet().iterator();
					while (it.hasNext()) {
						sdemo = "[财政机构]"
								+ VoucherUtil
										.getVoucherReturnStatus((Object[]) map
												.get(it.next() + ""));
					}
					// 如果是上海，系统自动生成的2302,代理行行号为空；不需给商行转发 不需要查询商行的回单状态
					if ((ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)
							&& StringUtils.isBlank(dto.getSpaybankcode())) {
						if (dto.getSstatus().equals(
								DealCodeConstants.VOUCHER_SUCCESS)) {
							dto.setSdemo(sdemo);
							dto.setSreturnerrmsg(null);
						} else {
							dto.setSreturnerrmsg(sdemo);
						}
						List<TvVoucherinfoDto> vList = new ArrayList<TvVoucherinfoDto>();
						vList.add(dto);
						count += VoucherUtil.updateVoucherState(vList);
						continue;
					}
				}
				String banktype = VoucherUtil.getDecOrgType(dto);
				map = voucherService.queryVoucherStatusByOrgType(null, dto
						.getSadmdivcode(), Integer.parseInt(dto.getSstyear()),
						dto.getSvtcode(), new String[] { dto.getSvoucherno() },
						banktype);
				Iterator it = map.keySet().iterator();
				if(it!=null)
				{
					while (it.hasNext()) {
						if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)
								|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)
								|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3507)
								||(dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3512)&&"35122".equals(dto.getSext5()))) {
							sdemo = sdemo
									+ "  [代理银行]"
									+ VoucherUtil
											.getVoucherReturnStatus((Object[]) map
													.get(it.next() + ""));
						} else {
							sdemo = VoucherUtil
									.getVoucherReturnStatus((Object[]) map.get(it
											.next()
											+ ""));
						}
						if (dto.getSstatus().equals(
								DealCodeConstants.VOUCHER_SUCCESS)) {
							dto.setSdemo(sdemo);
							dto.setSreturnerrmsg(null);
						} else {
							dto.setSreturnerrmsg(sdemo);
						}
						List<TvVoucherinfoDto> vList = new ArrayList<TvVoucherinfoDto>();
						vList.add(dto);
						count += VoucherUtil.updateVoucherState(vList);
						logger
								.debug("===============================凭证查询已回单凭证更新凭证状态成功===============================");
	
					}
				}
			}
		}
		return count;
	}
	/**
	 * 查找发送回单后凭证状态
	 * 
	 */
	public Map<String,TvVoucherinfoDto> queryVoucherReturnStatus(List voucherList)	throws ITFEBizException {
		Map map = null;
		Map<String,TvVoucherinfoDto> getmap = new HashMap<String,TvVoucherinfoDto>();
		for (TvVoucherinfoDto dto : (List<TvVoucherinfoDto>)voucherList)
		{
			VoucherService voucherService = new VoucherService();
			String sdemo = "";
			if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3401) || dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3402)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3405)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3406)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3507)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3508)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3509)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3510)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3511)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3512)
				|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3513))
			{
				map = voucherService.queryVoucherStatusByOrgType(null, dto.getSadmdivcode(), Integer.parseInt(dto.getSstyear()), dto.getSvtcode(),
						new String[] { dto.getSvoucherno() },StateConstant.ORGCODE_FIN);
				Iterator it = map.keySet().iterator();
				while (it.hasNext()) {
					sdemo = "[财政机构]"+ VoucherUtil.getVoucherReturnStatus((Object[]) map.get(it.next() + ""));
				}
				getmap.put(dto.getSvoucherno(), dto);
					// 如果是上海，系统自动生成的2302,代理行行号为空；不需给商行转发 不需要查询商行的回单状态
				if ((ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") >= 0)&& StringUtils.isBlank(dto.getSpaybankcode())) {
					if (dto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS)) {
							dto.setSdemo(sdemo);
							dto.setSreturnerrmsg(null);
					} else {
						dto.setSreturnerrmsg(sdemo);
					}
					getmap.put(dto.getSvoucherno(), dto);
					continue;
				}
			}
			String banktype = VoucherUtil.getDecOrgType(dto);
			map = voucherService.queryVoucherStatusByOrgType(null, dto.getSadmdivcode(), Integer.parseInt(dto.getSstyear()),dto.getSvtcode(), new String[] { dto.getSvoucherno() },banktype);
			Iterator it = map.keySet().iterator();
			if(it!=null)
			{
				while (it.hasNext()) {
					if (dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2301)|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_2302)
						|| dto.getSvtcode().equals(MsgConstant.VOUCHER_NO_3507)) {
						sdemo = sdemo+ "  [代理银行]"
									+ VoucherUtil.getVoucherReturnStatus((Object[]) map.get(it.next() + ""));
					} else {
						sdemo = VoucherUtil.getVoucherReturnStatus((Object[]) map.get(it.next()+ ""));
					}
					if (dto.getSstatus().equals(DealCodeConstants.VOUCHER_SUCCESS)) {
						dto.setSdemo(sdemo);
						dto.setSreturnerrmsg(null);
					} else {
						dto.setSreturnerrmsg(sdemo);
					}
					getmap.put(dto.getSvoucherno(), dto);	
				}
			}
		}
		return getmap;
	}
	/**
	 * 根据TCBS返回报文更新凭证状态
	 * 
	 */
	public void VoucherReceiveTCBS(String strecode, String vtcode,
			String packno, String voucherdate, String voucherno,
			BigDecimal amt, String sstatus, String ls_Description)
			throws Exception {
		SQLExecutor updateExce = DatabaseFacade.getDb().getSqlExecutorFactory()
				.getSQLExecutor();
		String updateVoucherSql = "";
		if (MsgConstant.VOUCHER_NO_2301.equals(vtcode)
				|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)) {
			if (ITFECommonConstant.ISITFECOMMIT
					.equals(StateConstant.COMMON_YES)) {
				updateVoucherSql = "update "
						+ TvVoucherinfoDto.tableName()
						+ " set S_STATUS = ? , S_DEMO = ? "
						+ " where S_TRECODE = ? and S_VTCODE = ? and S_PACKNO = ? and S_VOUCHERNO = ? and N_MONEY = ? and S_STATUS in (?,?,?,?)";
			} else {
				updateVoucherSql = "update "
						+ TvVoucherinfoDto.tableName()
						+ " set S_STATUS = ? , S_DEMO = ? "
						+ " where S_TRECODE = ? and S_VTCODE = ? and S_VOUCHERNO = ? and N_MONEY = ? and S_STATUS in (?,?,?,?)";
			}
		} else if (MsgConstant.VOUCHER_NO_5209.equals(vtcode)) {
			updateVoucherSql = "update "
					+ TvVoucherinfoDto.tableName()
					+ " set S_STATUS = ? , S_DEMO = ? "
					+ " where S_VTCODE = ?  and S_PACKNO = ? and S_VOUCHERNO = ? and (S_STATUS = ? or S_STATUS = ? or S_STATUS = ?)";
		} else if (MsgConstant.VOUCHER_NO_5106.equals(vtcode)) {
			updateVoucherSql = "update " + TvVoucherinfoDto.tableName()
					+ " set S_STATUS = ? , S_DEMO = ? "
					+ " where S_DEALNO = ? and (S_STATUS = ? or S_STATUS = ? or S_STATUS = ?)";
		} else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode)) {
			updateVoucherSql = "update "
					+ TvVoucherinfoDto.tableName()
					+ " set S_STATUS = ? , S_DEMO = ? "
					+ " where S_TRECODE = ? and S_VTCODE = ?  and S_STATUS = ? AND S_CHECKVOUCHERTYPE = ? AND S_HOLD3 = ? ";
		} else if (MsgConstant.VOUCHER_NO_5201.equals(vtcode)) {
			updateVoucherSql = "update "
					+ TvVoucherinfoDto.tableName()
					+ " set S_STATUS = ? , S_DEMO = ? "
					+ " where S_TRECODE = ? and S_VTCODE = ?  and S_PACKNO = ? and S_VOUCHERNO = ? and (S_STATUS = ? or S_STATUS = ?) and (S_HOLD4 = ? ";
		} else if(MsgConstant.VOUCHER_NO_5207.equals(vtcode)) {
			updateVoucherSql = "update "
				+ TvVoucherinfoDto.tableName()
				+ " set S_STATUS = ? , S_DEMO = ? "
				+ " where S_TRECODE = ? and (S_VTCODE = ? or S_VTCODE='5267')  and S_PACKNO = ? and S_VOUCHERNO = ? and (S_STATUS = ? or S_STATUS = ? or S_STATUS = ?)"; 
		}else {
			updateVoucherSql = "update "
					+ TvVoucherinfoDto.tableName()
					+ " set S_STATUS = ? , S_DEMO = ? "
					+ " where S_TRECODE = ? and S_VTCODE = ?  and S_PACKNO = ? and S_VOUCHERNO = ? and (S_STATUS = ? or S_STATUS = ? or S_STATUS = ?)";
		}
		try {
			if (DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(sstatus)) {
				updateExce.addParam(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				updateExce.addParam("处理成功");
			} else {
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TCBS);
				updateExce.addParam(ls_Description);
			}
			if (MsgConstant.VOUCHER_NO_2301.equals(vtcode)
					|| MsgConstant.VOUCHER_NO_2302.equals(vtcode)) {
				updateExce.addParam(strecode);
				updateExce.addParam(vtcode);
				if (ITFECommonConstant.ISITFECOMMIT
						.equals(StateConstant.COMMON_YES)) {
					updateExce.addParam(packno);
				}
				updateExce.addParam(voucherno);
				updateExce.addParam(amt);
				updateExce.addParam(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TIPS);//交易重复或者tips处理失败
			} else if (MsgConstant.VOUCHER_NO_5209.equals(vtcode)) {// 收入退付
				updateExce.addParam(vtcode);
				updateExce.addParam(packno);
				updateExce.addParam(voucherno);
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);// 已收妥
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);// 已发送
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TIPS);//tips处理失败
			} else if (MsgConstant.VOUCHER_NO_5106.equals(vtcode)) {// 授权支付额度
				updateExce.addParam(voucherno);// 该变量是凭证序列号
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);// 已收妥
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);// 已发送
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TIPS);//tips处理失败
			} else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode)) {// 批量业务支付明细
				updateExce.addParam(strecode);
				updateExce.addParam(MsgConstant.VOUCHER_NO_8207);
				updateExce.addParam(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);// 校验成功，因为8207不发送TIPS
				updateExce.addParam(packno);// 8207对应的5207或5201凭证类型
				updateExce.addParam(voucherno);// 8207对应的5207或5201凭证编号
			} else if (MsgConstant.VOUCHER_NO_5201.equals(vtcode)) {// 直接支付
				updateExce.addParam(strecode);
				updateExce.addParam(vtcode);
				updateExce.addParam(packno);
				updateExce.addParam(voucherno);
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);// 已收妥
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);// 已发送
				if (voucherdate.equals(StateConstant.BIZTYPE_CODE_BATCH)) {
					updateVoucherSql += ")";
					updateExce.addParam(voucherdate);// 业务类型编码
				} else {
					updateVoucherSql += " or S_HOLD4 = ?)";
					updateExce.addParam(StateConstant.BIZTYPE_CODE_SINGLE);
					updateExce.addParam(StateConstant.BIZTYPE_CODE_SALARY);
				}
			} else {
				updateExce.addParam(strecode);
				updateExce.addParam(vtcode);
				updateExce.addParam(packno);
				updateExce.addParam(voucherno);
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);// 已收妥
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);// 已发送
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TIPS);//tips处理失败
			}
			updateExce.runQueryCloseCon(updateVoucherSql);
			logger
					.debug("===============================凭证接收TCBS回执报文更新凭证状态成功===============================");

		} catch (Exception e) {
			try {
				logger.error(e);
				VoucherException.saveErrInfo(vtcode, e);
			} catch (Exception e1) {
				logger.error(e1);
				VoucherException.saveErrInfo(null, e1);
			}
		} finally {
			if (updateExce != null) {
				updateExce.closeConnection();
			}
		}

	}

	/**
	 * 根据Tips返回报文更新凭证状态
	 * 
	 */
	public void VoucherReceiveTIPS(String spackno, String state, String addword)
			throws Exception {
		List<TvVoucherinfoDto> list = null;
		try {
			if (spackno == null || spackno.equals(""))
				return;
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setSpackno(spackno);
			// 查找该包流水号下[已发送]状态的记录进行更新
			list = CommonFacade.getODB().findRsByDtoForWhere(
					vDto,
					" AND S_STATUS = '" + DealCodeConstants.VOUCHER_SENDED
							+ "'");
			String sdemo = "";
			if (list != null && list.size() > 0) {
				if (state.equals(DealCodeConstants.DEALCODE_ITFE_FAIL)) {
					state = DealCodeConstants.VOUCHER_FAIL_TIPS;
					sdemo = addword;
				} else {
					state = DealCodeConstants.VOUCHER_RECIPE;
					sdemo = "已收妥";
				}
				for (TvVoucherinfoDto dto : list) {
					dto.setSstatus(state);
					dto.setSdemo(sdemo);
				}
				VoucherUtil.updateVoucherState(list);
				logger
						.debug("===============================凭证接收Tips报文更新凭证状态成功===============================");
			}
		} catch (Exception e) {
			try {
				logger.error(e);
				VoucherException.saveErrInfo(list.get(0).getSvtcode(), e);
			} catch (Exception e1) {
				logger.error(e1);
				VoucherException.saveErrInfo(null, e1);
			}

		}
	}
	
	/**
	 * 根据Tips返回报文更新凭证状态
	 * 
	 */
	public void VoucherRecTipsByBizno(String spackno, String state, String addword)
			throws Exception {
		List<TvVoucherinfoDto> list = null;
		try {
			if (spackno == null || spackno.equals(""))
				return;
			TvPayoutmsgsubDto _dto =new TvPayoutmsgsubDto();
			_dto.setSpackageno(spackno);
			List <TvPayoutmsgsubDto> subList = CommonFacade.getODB().findRsByDto(_dto);
			List <IDto> updList = new ArrayList();
			if (subList.size()> 0 ) {
				TvVoucherinfoDto vDto = new TvVoucherinfoDto();
				vDto.setSdealno(subList.get(0).getSbizno());
				// 查找该包流水号下[已发送]状态的记录进行更新
				list = CommonFacade.getODB().findRsByDtoForWhere(
						vDto,
						" AND S_STATUS = '" + DealCodeConstants.VOUCHER_SENDED
								+ "'");
				String sdemo = "";
				if (list != null && list.size() > 0) {
					if (state.equals(DealCodeConstants.DEALCODE_ITFE_FAIL)) {
						state = DealCodeConstants.VOUCHER_FAIL_TIPS;
						sdemo = addword;
						for (TvPayoutmsgsubDto updto : subList) {
							updto.setSxpaydate(TimeFacade.getCurrentStringTime());
							updto.setSxpayamt(BigDecimal.ZERO);
							updto.setSxagentbusinessno(0+"");
							updto.setSxaddword(addword);
							updto.setSstatus(state);
							updList.add(updto);
						}
						if (updList.size()> 0) {
							DatabaseFacade.getODB().update(CommonUtil.listTArray(updList));
						}
					} else {
						state = DealCodeConstants.VOUCHER_RECIPE;
						sdemo = "已收妥";
					}
					for (TvVoucherinfoDto dto : list) {
						dto.setSstatus(state);
						dto.setSdemo(sdemo);
					}
					
					VoucherUtil.updateVoucherState(list);
					
					
					logger
							.debug("===============================凭证接收Tips报文更新凭证状态成功===============================");
				}
			}
			
			
		} catch (Exception e) {
			try {
				logger.error(e);
				VoucherException.saveErrInfo(list.get(0).getSvtcode(), e);
			} catch (Exception e1) {
				logger.error(e1);
				VoucherException.saveErrInfo(null, e1);
			}

		}
	}
	
	
	
	
	

	/**
	 * 根据Tips返回报文更新凭证状态
	 * 
	 */
	public void VoucherReceiveTIPSResult(String spackno, String state,
			String addword, String msgno) throws Exception {
		List<TvVoucherinfoDto> list = null;
		try {
			if (spackno == null || spackno.equals(""))
				return;
			TvVoucherinfoDto vDto = new TvVoucherinfoDto();
			vDto.setSpackno(spackno);
			// 查找该包流水号下[已发送][已收妥]状态的记录进行更新
			list = CommonFacade.getODB().findRsByDtoForWhere(
					vDto,
					" AND (S_STATUS = '" + DealCodeConstants.VOUCHER_SENDED
							+ "' OR S_STATUS = '"
							+ DealCodeConstants.VOUCHER_RECIPE + "')");
			String sdemo = "";
			if (list != null && list.size() > 0) {
				if (state.equals(DealCodeConstants.DEALCODE_ITFE_FAIL)) {
					state = DealCodeConstants.VOUCHER_FAIL_TIPS;
					sdemo = addword;
				} else {
					state = DealCodeConstants.VOUCHER_RECIPE;
					sdemo = "TIPS处理成功";
				}
				for (TvVoucherinfoDto dto : list) {
					dto.setSstatus(state);
					dto.setSdemo(sdemo);
				}
				VoucherUtil.updateVoucherState(list);
				logger.debug("===============================凭证接收Tips" + msgno
						+ "报文更新凭证状态成功===============================");
			}
		} catch (Exception e) {
			try {
				logger.error(e);
				VoucherException.saveErrInfo(list.get(0).getSvtcode(), e);
			} catch (Exception e1) {
				logger.error(e1);
				VoucherException.saveErrInfo(null, e1);
			}

		}
	}

	/**
	 * 签收失败
	 * 
	 * @return
	 */
	public static void voucherComfail(String sdealno, String errMsg) {
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		errMsg = errMsg == null ? "报文不规范" : errMsg;
		try {
			errMsg = errMsg.length() > 100 ? errMsg.substring(0, 100) : errMsg;
			VoucherService voucherService = new VoucherService();

			vDto.setSdealno(sdealno);
			vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto)
					.get(0);
			String err = voucherService.confirmVoucherfail(null, vDto
					.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),
					vDto.getSvtcode(), new String[] { vDto.getSvoucherno() },
					new String[] { errMsg });
			
			if (err == null) {
				if (VoucherFactory.getVouMap() != null
						&& VoucherFactory.getVouMap().get(vDto.getSvoucherno()+vDto.getStrecode()) != null)
					VoucherFactory.getVouMap().remove(vDto.getSvoucherno()+vDto.getStrecode());
				vDto.setSstatus(DealCodeConstants.VOUCHER_RECEIVE_FAIL);
				vDto.setSdemo(errMsg);
				vDto.setSreturnerrmsg(null);
				DatabaseFacade.getODB().update(vDto);
				logger
						.debug("===============================签收失败成功===============================");
			} else if(!"230123025106510852075209".contains(vDto.getSvtcode())){
				logger.debug("===============================签收失败发生异常==============================="+err);
				vDto.setSreturnerrmsg(err);
				DatabaseFacade.getODB().update(vDto);
				VoucherUtil.delRepeateData(vDto, false);
				err = voucherService.returnVoucherBack(null, vDto
						.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),
						vDto.getSvtcode(), new String[] { vDto.getSvoucherno() },
						new String[] { errMsg });
				if(err==null)
				{
						vDto.setSstatus(DealCodeConstants.VOUCHER_FAIL);
						vDto.setSdemo(errMsg);
						vDto.setSreturnerrmsg(null);
						logger.debug("===============================签收失败发生异常调用退回成功===============================");
				}else
				{
					vDto.setSreturnerrmsg(err);
					logger.debug("===============================签收失败发生异常调用退回异常==============================="+err);
				}
				DatabaseFacade.getODB().update(vDto);
			}
		} catch (Exception e1) {
			logger.error(e1);
			VoucherException.saveErrInfo(vDto.getSvtcode(), e1);
		}
	}
	/**
	 * 签收失败
	 * 
	 * @return
	 */
	public static void voucherComfail(TvVoucherinfoDto vDto, String errMsg) {
		if(vDto==null)
			return;
		errMsg = errMsg == null ? "报文不规范" : errMsg;
		try {
			errMsg = errMsg.length() > 100 ? errMsg.substring(0, 100) : errMsg;
			VoucherService voucherService = new VoucherService();
			String err = voucherService.confirmVoucherfail(null, vDto
					.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),
					vDto.getSvtcode(), new String[] { vDto.getSvoucherno() },
					new String[] { errMsg });
			
			if (err == null)
			{
				if(VoucherFactory.getVouMap()!=null && VoucherFactory.getVouMap().get(vDto.getSvoucherno()+vDto.getStrecode()) != null)
					VoucherFactory.getVouMap().remove(vDto.getSvoucherno()+vDto.getStrecode());
				logger.debug("===============================签收失败成功===============================");
			}else
			{
				logger.debug("===============================签收失败时发生异常==============================="+err);
//				err = voucherService.returnVoucherBack(null, vDto
//						.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),
//						vDto.getSvtcode(), new String[] { vDto.getSvoucherno() },
//						new String[] { errMsg });
//				if(err==null)
//				{
//						vDto.setSstatus(DealCodeConstants.VOUCHER_FAIL);
						vDto.setSdemo(errMsg);
						vDto.setSreturnerrmsg(null);
//						logger.debug("===============================签收失败发生异常调用退回成功===============================");
//				}else
//				{
//					vDto.setSreturnerrmsg(err);
//					logger.debug("===============================签收失败发生异常调用退回异常==============================="+err);
//				}
				DatabaseFacade.getODB().update(vDto);
			}
		} catch (Exception e1) {
			logger.error(e1);
			VoucherException.saveErrInfo(vDto.getSvtcode(), e1);
		}
	}
	/**
	 * 签收失败 (用于陕西特色业务---接收财政系统)
	 * 
	 * @author sunyan
	 * 
	 */
	public static void voucherComfailForSX(String sdealno, String errMsg) {
		TvVoucherinfoSxDto vDto = new TvVoucherinfoSxDto();
		errMsg = errMsg == null ? "报文不规范" : errMsg;
		try {
			errMsg = errMsg.length() > 100 ? errMsg.substring(0, 100) : errMsg;
			vDto.setSdealno(sdealno);
			vDto = (TvVoucherinfoSxDto) CommonFacade.getODB().findRsByDto(vDto)
					.get(0);
			vDto.setSstatus(DealCodeConstants.VOUCHER_RECEIVE_FAIL);
			vDto.setSdemo(errMsg);
			DatabaseFacade.getODB().update(vDto);
			logger.debug("========================签收失败成功[陕西特色]========================");
		} catch (Exception e1) {
			logger.error(e1);
			VoucherException.saveErrInfo(vDto.getSvtcode(), e1);
		}
	}

	/**
	 * 签收成功
	 * 
	 * @return
	 * @throws ITFEBizException
	 * @throws ITFEBizException
	 */
	public static void voucherConfirmSuccess(String sdealno)
			throws ITFEBizException {
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		try {
			VoucherService voucherService = new VoucherService();
			vDto.setSdealno(sdealno);
			vDto = (TvVoucherinfoDto) CommonFacade.getODB().findRsByDto(vDto)
					.get(0);
			String err = voucherService.confirmVoucher(null, vDto
					.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),
					vDto.getSvtcode(), new String[] { vDto.getSvoucherno() });
			if (err == null) {
				if(vDto.getSvtcode().equals("5287"))
					vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
				else
					vDto.setSstatus(DealCodeConstants.VOUCHER_RECEIVE_SUCCESS);
				vDto.setSdemo("签收成功");
				vDto.setSreturnerrmsg(null);
				DatabaseFacade.getODB().update(vDto);
				logger
						.debug("===================== 签收成功========================");
				if (VoucherFactory.getVouMap() != null&& VoucherFactory.getVouMap().get(vDto.getSvoucherno()+vDto.getStrecode()) != null)
					VoucherFactory.getVouMap().remove(vDto.getSvoucherno()+vDto.getStrecode());
			} else {
				vDto.setSreturnerrmsg(err);
				vDto.setSdemo("已读取");
				DatabaseFacade.getODB().update(vDto);
				VoucherUtil.delRepeateData(vDto, true);
				err = " 签收成功异常：" + err;
				err = err.getBytes().length>100 ? CommonUtil.subString(err, 60) : err;
				voucherComfail(vDto,err);
				throw new ITFEBizException("凭证：" + vDto.getSvoucherno()
						+ " 签收成功异常：" + err);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("凭证：" + vDto.getSvoucherno()
					+ " 签收成功异常！");
		}
	}
	/**
	 * 签收成功
	 * 
	 * @return
	 * @throws ITFEBizException
	 * @throws ITFEBizException
	 */
	public static void voucherConfirmSuccess(TvVoucherinfoDto findto)
			throws ITFEBizException {
		try {
			VoucherService voucherService = new VoucherService();
			String err = voucherService.confirmVoucher(null, findto
					.getSadmdivcode(), Integer.parseInt(findto.getSstyear()),
					findto.getSvtcode(), new String[] { findto.getSvoucherno() });
			if (err == null) {
				logger
						.debug("===================== 签收成功========================"+findto.getSvoucherno());
				if (VoucherFactory.getVouMap() != null&& VoucherFactory.getVouMap().get(findto.getSvoucherno()+findto.getStrecode()) != null)
					VoucherFactory.getVouMap().remove(findto.getSvoucherno()+findto.getStrecode());
			} else {
				throw new ITFEBizException("凭证：" + findto.getSvoucherno()
						+ " 签收成功异常：" + err);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("凭证：" + findto.getSvoucherno()
					+ " 签收成功异常！");
		}
	}
	/**
	 * 发送凭证附件(绿色通道)
	 */
	public void sendData(TvVoucherinfoDto dto) throws ITFEBizException {
		String filePath = ITFECommonConstant.FILE_ROOT_PATH
				+ dto.getSfilename();
		String fileName = new File(filePath).getName();
		try {
			List<byte[]> fileList = FileUtil.getInstance().cutFile(filePath,
					1024 * 1024 * 3);// 文件分割大小：3M
			if (fileList.size() == 0)
				throw new ITFEBizException("文件：" + fileName
						+ " 为空文件，不能发送电子凭证库！");
			for (byte[] bytes : fileList) {
				// 组装XML报文
				Document successDoc = DocumentHelper.createDocument();
				successDoc.setXMLEncoding("GBK");
				Element File = successDoc.addElement("File");
				Element FileName = File.addElement("FileName");// 文件名
				FileName.setText(fileName);
				Element FileBlockCount = File.addElement("FileBlockCount");// 文件分包总数：文件按2M分包
				FileBlockCount.setText(fileList.size() + "");
				Element AfewBlock = File.addElement("AfewBlock");// 文件顺序号
				AfewBlock.setText((fileList.indexOf(bytes) + 1) + "");
				Element Attach = File.addElement("Attach");// 附言
				Attach.setText("");
				Element vtCode = File.addElement("VtCode");// 附言
				vtCode.setText(dto.getSvtcode());
				Element voucherNo = File.addElement("VoucherNo");// 附言
				voucherNo.setText(dto.getSvoucherno());
				Element FileData = File.addElement("FileData");// 文件数据流
				FileData.setText(VoucherUtil.base64Encode(new String(bytes,
						"GBK")));
				logger.error("发送绿色通道报文:"+successDoc.asXML());
				VoucherService voucherService = new VoucherService();
				voucherService.sendData("001", dto.getSadmdivcode(),
						StateConstant.ORGCODE_RH, StateConstant.ORGCODE_FIN,
						Integer.parseInt(dto.getSstyear()), VoucherUtil
								.base64Encode(successDoc.asXML()).getBytes());
			}
		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException(e.getMessage());
		}
		logger.debug("===================== 发送凭证附件：" + fileName
				+ " 成功========================");
	}

	/**
	 * 实拨资金退款凭证生成
	 * 
	 * @throws ITFEBizException
	 * 
	 */
	public static List voucherGenerateForPayoutBack(List list)
			throws ITFEBizException {
		Integer count = new Integer(0);
		List resultList = new ArrayList();
		List<TvVoucherinfoDto> vList = list;
		IVoucherDto2Map voucher = (IVoucherDto2Map) ContextFactory
				.getApplicationContext()
				.getBean(
						MsgConstant.VOUCHER_DTO2MAP + vList.get(0).getSvtcode());
		for (TvVoucherinfoDto vDto : vList) {
			try {
				List lists = voucher.voucherGenerate(vDto);
				if (lists != null && lists.size() > 0) {
					for (List mapList : (List<List>) lists) {
						VoucherUtil.sendTips((TvVoucherinfoDto) mapList.get(1),
								(HashMap) mapList.get(0));
						DatabaseFacade.getODB().create(
								(TvVoucherinfoDto) mapList.get(1));
						count++;
					}
				}
			} catch (JAFDatabaseException e) {
				logger.error(e);
				throw new ITFEBizException(e.getMessage(), e);
			}
		}
		resultList.add(count.intValue() + "");
		return resultList;

	}
	
	public byte[] exportPDF(TvVoucherinfoDto dto)throws ITFEBizException{
		VoucherService voucherService = new VoucherService();
		return voucherService.exportPDF("001", dto.getSadmdivcode(), Integer.parseInt(dto.getSstyear()), dto.getSvtcode(), 1, dto.getSvoucherno());
	}
	
	public static Map<String, String> getVouMap() {
		return vouMap;
	}

	public static void setVouMap(Map<String, String> vouMap) {
		VoucherFactory.vouMap = vouMap;
	}
	private void recvFj50And58(VoucherService voucherService,Node voucherNode,String vtcode,String voucherno,String admdivcode,String year)
	{
		
		try {
			Node tempNode = voucherNode.selectSingleNode("XCheckResult");
			String xcheckresult = tempNode==null?"":tempNode.getText();
			tempNode = voucherNode.selectSingleNode("XCheckReason");
			String xcheckreason = tempNode==null?"":tempNode.getText();
			xcheckreason = xcheckreason.getBytes().length > 470 ? CommonUtil.subString(xcheckreason, 470) : xcheckreason;
			tempNode = voucherNode.selectSingleNode("XAcctDate");
			String xacctdate = tempNode==null?"":tempNode.getText();
			tempNode = voucherNode.selectSingleNode("DetailList");
			TvVoucherinfoDto findto = new TvVoucherinfoDto();
			findto.setSvtcode(vtcode);
			findto.setSvoucherno(voucherno);
			findto.setSadmdivcode(admdivcode);
			findto.setSstyear(year);
			findto = (TvVoucherinfoDto) DatabaseFacade.getDb().find(findto) ;
			if(tempNode!=null&&findto!=null)
			{
				Node detailnode = tempNode.selectSingleNode("Detail");
				if(detailnode==null)
				{
					voucherService.confirmVoucherfail(null, admdivcode, Integer.parseInt(year),	vtcode, new String[] {voucherno},new String[] { "报文节点Detail不存在!"});
				}else
				{
//					tempNode = detailnode.selectSingleNode("XCurReckMoney");String xCurReckMoney = tempNode==null?"":tempNode.getText();tempNode = detailnode.selectSingleNode("XCurDateMoney");String xCurDateMoney = tempNode==null?"":tempNode.getText();
					findto.setSstatus(DealCodeConstants.VOUCHER_READRETURN);
					findto.setSdemo("对账日期:"+xacctdate+"对账结果:"+("1".equals(xcheckresult)?"相符":"不相符")+xcheckreason);
					DatabaseFacade.getDb().update(findto);
					voucherService.confirmVoucher(null, admdivcode, Integer.valueOf(year), vtcode, new String[]{voucherno});	//签收成功
				}
			}else
			{
				voucherService.confirmVoucherfail(null, admdivcode, Integer.parseInt(year),	vtcode, new String[] {voucherno},new String[] {"报文节点DetailList不存在!"});
			}
			
		} catch (JAFDatabaseException e) {
			voucherService.confirmVoucherfail(null, admdivcode, Integer.parseInt(year),	vtcode, new String[] {voucherno},new String[] { "接收失败:"+e.toString() });
		}
	}
	private String getString(String value)
	{
		if(value==null||"".equals(value)||"null".equals(value.toLowerCase()))
			return "";
		else
			return value;
	}
}

class ReadFileFilter implements FileFilter {
	public boolean accept(File pathname) {
		if (pathname.getPath().split("-").length > 4
				&& pathname.getPath().indexOf("rev") > 0) {
			return true;
		} else {
			return false;
		}
	}
}
