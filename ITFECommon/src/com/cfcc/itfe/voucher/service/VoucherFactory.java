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
 * @time 13-07-20 14:26:06 ������:ƾ֤������ ģ��:VoucherFactory ���:VoucherFactory
 */
@SuppressWarnings("unchecked")
public abstract class VoucherFactory implements IVoucherFactory {
	private static Log logger = LogFactory.getLog(VoucherFactory.class);
	private static Map<String, String> vouMap = new HashMap<String, String>();
	private static boolean isread = true;
	public int voucherRead(List list) {

		/**
		 * ƾ֤��ȡ
		 * 
		 * ѭ��ƾ֤���Ͷ�ȡƾ֤��ϢreadVoucher �����ı�����Ϣ����tv_voucherinfo������������ļ�Ŀ¼
		 * ������֮ǰ���ĸ�ʽҪת���Base64ת����String�� ���ض�ȡ��ƾ֤����
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
					logger.debug("===================ƾ֤��ȡ��ʼ=========================");
					try {
						voucher = voucherService.readVoucher(ls_AdmDiveCode,li_Year, svtcode);

					} catch (Exception e) {
						String errInfo = (e.getMessage()==null?(e.toString()):(e.getMessage().length()>1024?e.getMessage().substring(0, 1024):e.getMessage()));
						if(errInfo.contains("EVS100#�������Ϸ�")||errInfo.contains("δ�ҵ���������Ϊ")||errInfo.contains("EVS004#��ȡ��������ʧ��"))
						{	
							logger.error("ƾ֤����δ������������Ϊ["+ls_AdmDiveCode+"],ҵ������Ϊ["+svtcode+"]��ҵ��ƾ֤ģ��!");
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
						logger.debug("ƾ֤��ȡ" + svtcode + "ƾ֤��ԭʼ���ģ�" + voucherXml);
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
								logger.debug("ƾ֤��ȡƾ֤����" + svtcode + "ƾ֤���" + VoucherNo + "�������ģ�\r\n\t" + Voucher);

								// ɾ��VoucherԪ��
								Node node = ((Node) listNodes.get(i)).selectSingleNode("Voucher");
								((Element) listNodes.get(i)).remove(node);

								fxrDocVoucher = DocumentHelper.parseText(Voucher);

								// ��ӽ�����VoucherԪ��
								((Element) listNodes.get(i)).add(fxrDocVoucher.getRootElement());

								Node voucherNode = fxrDocVoucher.selectSingleNode("Voucher");
								if(MsgConstant.VOUCHER_NO_3550.equals(VtCode)||MsgConstant.VOUCHER_NO_3558.equals(VtCode))
								{
									recvFj50And58(voucherService,voucherNode,VtCode,VoucherNo,ls_AdmDiveCode,StYear);
									continue;
								}
								// 5201��8207��5253��5351��2252�����޹������ڵ�
								if (!(svtcode.equals(MsgConstant.VOUCHER_NO_5201)
										|| svtcode.equals(MsgConstant.VOUCHER_NO_8207)
										|| svtcode.equals(MsgConstant.VOUCHER_NO_5253)
										|| svtcode.equals(MsgConstant.VOUCHER_NO_5351) 
										|| svtcode.equals(MsgConstant.VOUCHER_NO_5407) 
										|| svtcode.equals(MsgConstant.VOUCHER_NO_2252))) {
									if(voucherNode.selectSingleNode("TreCode")!=null)
										TreCode = voucherNode.selectSingleNode("TreCode").getText();
								}
								String billOrgCode = VoucherUtil.getSpaybankcode(svtcode, voucherNode);// ��Ʊ��λ
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
								// ���ϵ�˰��ɫ
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
								// ��Ĭ��һ���ո�Ϊnull��ǰ̨�쳣
								voucherDto.setShold1(getString(hold1));
								voucherDto.setShold2(getString(hold2));
								voucherDto.setShold3(getString(hold3));
								voucherDto.setShold4(getString(hold4));
								voucherDto.setSext1(" ");// ֧����ʽ
								voucherDto.setSext3(" ");// ҵ������
								voucherDto.setSext2(" ");// 
								voucherDto.setSext4(" ");// Ԥ������
								voucherDto.setSext5(" ");// 
								if (VtCode.equals(MsgConstant.VOUCHER_NO_2301)
										|| VtCode.equals(MsgConstant.VOUCHER_NO_2302)) {
									if (voucherNode.selectNodes("PayTypeCode") != null) {
										String PayTypeCode = voucherNode.selectSingleNode("PayTypeCode").getText();
										if ("12".equals(PayTypeCode)|| PayTypeCode.startsWith("001002")) {// �����ձ���õ���6λ
											voucherDto.setSext1(MsgConstant.grantPay);// ��Ȩ֧��
										} else if ("11".equals(PayTypeCode)|| PayTypeCode.startsWith("001001")) {// �����ձ���õ���6λ
											voucherDto.setSext1(MsgConstant.directPay);// ֧����ʽ����
											// ֱ��֧��
										}
									}
								} else if (VtCode.equals(MsgConstant.VOUCHER_NO_2252)) {
									// Ĭ��Ϊֱ��֧�� ����ǰ̨��null����
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
									voucherDto.setSdemo("���ܵ���ֱ��ǩ��");
								}else
								{
									voucherDto.setSstatus(DealCodeConstants.VOUCHER_ACCEPT);
									voucherDto.setSdemo("���������쳣");
								}
								voucherDto.setSstyear(StYear);
								voucherDto.setStrecode(TreCode);
								voucherDto.setSvoucherflag(VoucherFlag);
								voucherDto.setSvoucherno(VoucherNo);
								voucherDto.setSvtcode(VtCode);
								voucherDto.setSpaybankcode(billOrgCode); // ��Ʊ��λ
								
								if (dealnos.containsKey(VoucherNo)) {
									continue;
								}else{
									String mainvou = VoucherUtil.getGrantSequence();
									dealnos.put(VoucherNo, mainvou);
									voucherDto.setSdealno(mainvou);
								}
								
								// ������Դ(���ղ���������еĶ�������ʱ��д������Դ�����ֶ��˷���)
								// (1:���з���2����������3�����з���)
								if (VtCode.equals(MsgConstant.VOUCHER_NO_3507)) {
									if (voucherNode.selectNodes("Hold1") != null) {
										String from = voucherNode
												.selectSingleNode("Hold1")
												.getText();
										if (from == null
												|| from.equals("")
												|| from.startsWith(StateConstant.ORGCODE_FIN)) {
											voucherDto.setSext1("2");// ����
										} else {
											voucherDto.setSext1("3");// ����
											if (voucherNode.selectSingleNode("ClearAccNo") != null&& !"".equals(voucherNode.selectSingleNode("ClearAccNo").getText()))
												voucherDto.setSpaybankcode(voucherNode.selectSingleNode("ClearAccNo").getText());
											else
												voucherDto.setSpaybankcode(from);// ���������к�
										}
									}
								} else if (StateConstant.ACCOUNT_CHECK.contains(VtCode)) {
									voucherDto.setSext1("2");
								} else if (voucherNode.selectSingleNode("VoucherCheckNo") != null)
									voucherDto.setSext2(voucherNode.selectSingleNode("VoucherCheckNo").getText());// ��Զ��˻�ִ���ı���ԭ���˱��ĵ���
								if (VoucherUtil.voucherDealMsgXML(voucherNode,voucherDto))
									continue;
								// ����ƾ֤������Ϣ(�Ϻ���ɫ)
								VoucherUtil.updateVoucherCompareInfo(voucherDto, voucherNode);
								if (VoucherNo != null&& vouMap.get(VoucherNo+voucherDto.getStrecode()) != null) {
									voucherComfail(vouMap.get(VoucherNo+voucherDto.getStrecode()),
											"���Ĳ��淶����˶Ա��Ĺ淶!");
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
											voucherComfail(voucherDto, "ƾ֤��Ų����ظ�,�Ѿ�����ƾ֤���Ϊ"+voucherDto.getSvoucherno()+"��ƾ֤!"+rs.getInt(0, 0));
											execDetail.runQuery("update TV_VOUCHERINFO set S_STATUS=?,S_DEMO=? where S_CREATDATE=? and S_VTCODE=? and  s_voucherno=? and S_STATUS not in(?,?,?) ");
											execDetail.clearParams();
											execDetail.addParam(DealCodeConstants.VOUCHER_RECEIVE_FAIL);
											execDetail.addParam("ƾ֤����ظ��Ѵ���ƾ֤���"+voucherDto.getSvoucherno()+"��ƾ֤!");
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
										logger.debug("��ƾ֤Ϊ�Ƿ�ƾ֤,ƾ֤���Ϊ"+voucherDto.getSvoucherno()+"��ƾ֤!"+rs.getInt(0, 0));
										continue;
									}
								}
								//��Щ������˵Ļص��ص���ƾ֤������������ģʽ������˵���3510���ص�Ҳ��3510
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
											qlist.get(0).setSdemo(("0".equals(chresult)?"���˳ɹ�":"����ʧ��"));
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
												qlist.get(0).setSdemo(("0".equals(chresult)?"���˳ɹ�":"����ʧ��"));
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
											qlist.get(0).setSdemo(("0".equals(chresult)?"���˳ɹ�":"����ʧ��"));
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
								logger.error("�������ݳ����쳣��" + e);
								VoucherException.saveErrInfo(svtcode, e);
								voucherService.confirmVoucherfail(null,ls_AdmDiveCode, li_Year, svtcode,new String[] { VoucherNo },new String[] { "���������쳣: "+ e.getMessage() });
								continue;
							} catch (Exception e) {
								logger.error("ƾ֤��ȡ" + svtcode + "���ĳ����쳣��" + e);
								VoucherException.saveErrInfo(svtcode, e);
								voucherService.confirmVoucherfail(null,	ls_AdmDiveCode, li_Year, svtcode,new String[] { VoucherNo },	new String[] { "���Ĳ��淶: "+ e.getMessage() });
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
							String Xml = savexml.replaceAll("&lt;&lt;", "��").replaceAll("&gt;&gt;", "��").replaceAll("&lt;","��").replaceAll("&gt;", "��");
							VoucherUtil.sendTips(voucherDto, ls_FinOrgCode,dealnos, Xml);
						}
					}
				} catch (Exception e) {
					logger.error("ƾ֤��ȡ" + svtcode + "���ĳ����쳣��" + e);
					VoucherException.saveErrInfo(svtcode, e);
					continue;
				}
			}
		} catch (Exception e) {
			logger.error("ƾ֤��ȡ���ĳ����쳣��" + e);
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
	 * ƾ֤����
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
						vDto.setSdemo("���ɻص��ɹ�");
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
	 * ƾ֤У��
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
	 * ƾ֤У��(����������ɫҵ��--���ղ���ϵͳ)
	 * 
	 * @author sunyan
	 */
	public int voucherVerifyForSX(List lists, String vtcode)
			throws ITFEBizException {
		VoucherVerifySX voucherVerify = new VoucherVerifySX();
		return voucherVerify.verify(lists, vtcode);
	}

	/**
	 * ƾ֤�ύ
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
		logger.debug("===================ƾ֤����TIPS�ɹ�====================");

		return count;

	}

	/**
	 * ƾ֤ǩ��
	 * 
	 */
	public int voucherStamp(List lists) throws ITFEBizException {
		int count = 0;
		VoucherService voucherService = new VoucherService();
		String stampXML = "";
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		for (List list : (List<List>) lists) {
			// ǩ���û���Ϣ
			TsUsersDto uDto = (TsUsersDto) list.get(0);
			// ������Ϣ
			TsTreasuryDto tDto = (TsTreasuryDto) list.get(1);
			// ǩ��λ����Ϣ
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
					// ������ǩ��
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
					err = stampXML.split("@")[0];// �ͻ���ǩ�¸�����־��@���ֵ ��ʾ�ͻ���ǩ��ʧ��
					if (stampXML.equals(""))
						err = sDto.getSstampname() + "ǩ��(��)�쳣";
					else {
						if (stampXML.indexOf("@") < 0) {
							// �ͻ��˵���ǩ��
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
							// �ͻ���ǩ��ʧ��
							err = sDto.getSstampname()
									+ "ǩ���쳣:"
									+ (err.indexOf("Exception:") == -1 ? err
											: err.substring(err
													.indexOf("Exception:")
													+ "Exception:".length(),
													err.length()));
						}
					}
				}// ǩ�³ɹ�
				if(err!=null&&(err.contains("λ����ǩ��")||err.contains("λ���Ѹ���")||((!err.contains("ҵ���߼����Ϸ�"))&&err.contains("�ѷ���"))))
					err="";
				if (StringUtils.isBlank(err)) {
					String stampid = vDto.getSstampid();
					if (stampid == null || stampid.equals("")) {
						vDto.setSdemo("��ǩ�£�" + sDto.getSstampname());
						// ǩ��λ��ֻ��1��ʱƾ֤״̬Ϊǩ�³ɹ�
						if (size == 1)
							vDto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
					} else {
						// ǩ��λ�ö��ʱ����ƾ֤״̬
						if ((stampid.split(",").length + 1) == size) {
							vDto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
						}
						vDto.setSdemo(vDto.getSdemo() + "��"
								+ sDto.getSstampname());
					}
					// ת����ǩ�³ɹ�����ǩ���û��ۼ�
					if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_OFFICIAL))
						vDto.setSstampuser(vDto.getSstampuser() == null ? (""
								+ uDto.getSusercode() + "#,") : vDto
								.getSstampuser()
								+ uDto.getSusercode() + "#,");
					// ת����ǩ�³ɹ�����ǩ���û��ۼ�
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_ROTARY))
						vDto.setSstampuser(vDto.getSstampuser() == null ? (""
								+ uDto.getSusercode() + "!,") : vDto
								.getSstampuser()
								+ uDto.getSusercode() + "!,");
					// ������ǩ�³ɹ�����ǩ���û��ۼ�
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_ATTACH))
						vDto.setSstampuser(vDto.getSstampuser() == null ? (""
								+ uDto.getSusercode() + "^,") : vDto
								.getSstampuser()
								+ uDto.getSusercode() + "^,");
					// ҵ��ר����ǩ�³ɹ�����ǩ���û��ۼ�
					else if (sDto.getSstamptype().equals(
							MsgConstant.VOUCHERSAMP_BUSS))
						vDto.setSstampuser(vDto.getSstampuser() == null ? (""
								+ uDto.getSusercode() + "&,") : vDto
								.getSstampuser()
								+ uDto.getSusercode() + "&,");
					// ˽��ǩ�³ɹ�����ǩ���û��ۼ�
					else
						vDto.setSstampuser(vDto.getSstampuser() == null ? (""
								+ uDto.getSusercode() + ",") : vDto
								.getSstampuser()
								+ uDto.getSusercode() + ",");
					// ǩ�³ɹ�����ӡ�ºۼ�
					vDto.setSstampid(vDto.getSstampid() == null ? (""
							+ sDto.getSstamptype() + ",") : vDto.getSstampid()
							+ sDto.getSstamptype() + ",");
					vDto.setSreturnerrmsg(null);
					List<TvVoucherinfoDto> vouList = new ArrayList<TvVoucherinfoDto>();
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0&&MsgConstant.VOUCHERSAMP_ATTACH.equals(sDto.getSstamptype()))
						vDto.setSstatus(DealCodeConstants.VOUCHER_STAMP);
					vouList.add(vDto);
					// ǩ�²����ɹ�����ƾ֤״̬
					VoucherUtil.updateVoucherState(vouList);
					logger
							.debug("===============================ƾ֤ǩ�³ɹ�==============================");
					count++;
				} else {
					// ǩ�²���ʧ����ƾ֤״̬
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
	 * ȡ��ָ��λ��ǩ��
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
					// ����ָ��λ��ƾ֤ǩ��
					err = voucherService.cancelStampWithPosition(certID, vDto
							.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),
							vDto.getSvtcode(), vDto.getSvoucherno(), sDto
									.getSstampposition());
					// ����ƾ֤ǩ�³ɹ�
					if (err == null || err.equals("")) {
						if(vDto.getSstatus().equals(DealCodeConstants.VOUCHER_STAMP)){
							vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK);
						}
						if("7173".contains(vDto.getSstatus()))//����ɹ���ǩ�³ɹ�
						{
							if (vDto.getSstampid().split(",").length == 1)
								vDto.setSdemo("����ɹ�");
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
									vDto.setSdemo("����"+sDto.getSstampname()+"ǩ�³ɹ�!");
							}
						}
						// ����ǩ�³ɹ�����ӡ�ºۼ�
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
						// ����ǩ�³ɹ�����ǩ���û��ۼ�
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
						// ����ǩ�²���ʧ����ƾ֤״̬
						count += VoucherUtil.updateVoucherState(vouList);
						logger
								.debug("===============================ƾ֤����ǩ�²����ɹ�===============================");
					} else {
						// ����ǩ�²���ʧ����ƾ֤״̬
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
			logger.error("��-��-ǩ-��-�����쳣:", e);
			throw new ITFEBizException(e);
		}
		return count;

	}

	/**
	 * ���ͻص�
	 * 
	 * һ�� ��ǩ�³ɹ�ƾ֤���͵���ƾ֤�⣺ ֱ�ӷ��͵���ƾ֤�� ���� ϵͳ�������ɲ�ǩ��ƾ֤���͵���ƾ֤�⣺ 1 ƾ֤ǩ�� 2 ���͵���ƾ֤�� ����
	 * �ص�״̬Ϊʧ�ܵ�״̬��ƾ֤���·��͵���ƾ֤�⣺ ֱ�ӷ��͵���ƾ֤�� �ġ� �ص�״̬Ϊ���˻ص�ƾ֤���·��͵���ƾ֤�⣺ 1 ƾ֤���� 2
	 * ���͵���ƾ֤��,
	 * �޸ļ�¼���ã����÷�������λ���Զ�ǩ����ƾ֤�����˵����жϺͷ��ͣ����ٲ��ù����ķ��ͣ����ͺ����״̬��䣬�����˷��͵�if�����cyg
	 * 20141226��
	 */
	public int voucherSendReturnSuccess(List list) throws ITFEBizException {
		int count = 0;
		String err = null;
		VoucherService voucherService = new VoucherService();
		for (List<TvVoucherinfoDto> voucherList : (List<List>) list) {
			for (TvVoucherinfoDto vDto : voucherList) {
				boolean signByNosFlag = false;// ƾ֤ǩ���жϱ�־ true ǩ���ɹ�
				boolean discardVoucherFlag = false;// ƾ֤�����жϱ�־ true ���ϳɹ�
				String repeatVoucherFlag = (StringUtils.isBlank(vDto
						.getSreturnerrmsg())) ? "0" : (vDto.getSreturnerrmsg()
						.indexOf("$") == -1 ? "0"
						: vDto.getSreturnerrmsg().substring(
								vDto.getSreturnerrmsg().lastIndexOf("$") + 1));
				// ���·���ƾ֤������־�� 0 ���ط� 1 �ص�״̬Ϊʧ�ܵ�״̬��ƾ֤ 2 �ص�״̬Ϊ���˻ص�ƾ֤
				// 3 [��������]�ص�״̬Ϊʧ�ܵ�״̬��ƾ֤ 4 [��������]�ص�״̬Ϊʧ�ܵ�״̬��ƾ֤
				// 5 [��������]��[��������]�ص�״̬��Ϊʧ�ܵ�״̬��ƾ֤
				// ƾ֤ǩ�������Ϻ���ǩ��
				if (StringUtils.isBlank(vDto.getSstampid())) {
					HashMap<String, String> map = null;
					try {
						// ǩ��������������
						if ("0".equals(repeatVoucherFlag)) {
							map = VoucherUtil.getStampPotisionXML(vDto);
							// ���ж��˺ͱ����������÷������˴�λ��ǩ���ı��ľ�Ĭ���ȷ��͵�����ƾ֤��
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
							// �ж�ƾ֤��������3507�ģ���Ҫͬʱ���͸���Ӧ�Ĵ������еĵ���ƾ֤��
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
										vDto.getSvtcode(), // �������е���ƾ֤��
										new String[] { vDto.getSvoucherno() });
							}
							// ȷ�������ܼ���ִ������if���
							signByNosFlag = false;
							repeatVoucherFlag = "stop";
						} else {// ����Ѿ����͹������Ե������·���
							signByNosFlag = true;
						}
					} catch (Exception e) {
						logger.error(e);
						throw new ITFEBizException("��ȡ��������ǩ��λ�ó���", e);
					}
				}
				// ƾ֤����
				if (repeatVoucherFlag.equals("2")) {
					err = voucherService.discardVoucher(null, vDto
							.getSadmdivcode(), Integer.parseInt(vDto
							.getSstyear()), vDto.getSvtcode(),
							new String[] { vDto.getSvoucherno() });
					if (StringUtils.isBlank(err))
						discardVoucherFlag = true;
				}
				// ����ƾ֤
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
											.getSvoucherno() });// ���Ͳ��������е���ƾ֤��
								}
							}
						}
					}
					/*
					 * 2301 2302 3551 3507 ����Ҫ���Ͳ��������е�ǩ�±��ķ��Ͳ���
					 * 
					 * 1����ǩ�³ɹ�ƾ֤���Ͳ�������ƾ֤�� 2��[��������]�ص�״̬Ϊʧ�ܵ�״̬��ƾ֤�ط���������ƾ֤��
					 * 3���ص�״̬Ϊ���˻ص�ƾ֤�ط���������ƾ֤��
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
						 * �Ϻ�ϵͳ�Զ����ɵ�2301��2302ֻ������������ֻ����Ȩ2301��2302��Ҫ�����з���
						 * ϵͳ���ɵ�2301��2302ʱ��������S_PAYBANKCODE Ϊ��
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
							Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(),new String[] { vDto.getSvoucherno() });// ���Ͳ�������ƾ֤��
							if (StringUtils.isNotBlank(err)) {
								// ���Ͳ�������ʧ�ܸ���ƾ֤״̬
								vDto.setSreturnerrmsg(err);
								VoucherUtil.updateVoucherState(vDto);
								continue;
							}
						}
					}
					// [��������]�ص�״̬Ϊʧ�ܵ�״̬��ƾ֤���ط������ٷ��Ͳ�������ƾ֤��
					if (repeatVoucherFlag.equals("3")) {
						count += 1;
						continue;
					}
					/*
					 * ǩ��/ǩ���ɹ����ķ��͵���ƾ֤��
					 * 
					 * 1����ǩ�³ɹ�ƾ֤���͵���ƾ֤�� 2��ϵͳ�������ɲ�ǩ����ǩ���ɹ�ƾ֤���͵���ƾ֤��
					 * 3���ص�״̬Ϊʧ�ܵ�״̬��ƾ֤�ط�����ƾ֤�� 4��[��������]�ص�״̬Ϊʧ�ܵ�״̬��ƾ֤�ط����е���ƾ֤��
					 * 5���ص�״̬Ϊ���˻ص�ƾ֤�ط���������ƾ֤��
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
											.getSvoucherno() });// ���Ͳ��������е���ƾ֤��
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
					// new String[] { vDto.getSvoucherno() });// ���Ͳ��������е���ƾ֤��
					// }
					// }
				}
				if(err!=null&&err.contains("EVS700#ҵ���߼����Ϸ�")&&err.contains("�ѷ���"))
				{
					err = "���ܾ���,����״̬Ϊ�ѻص�!";
				}
				if (err == null || err.equals("")||err.equals("���ܾ���,����״̬Ϊ�ѻص�!")) {
					if(ITFECommonConstant.PUBLICPARAM.indexOf(",payoutstampmode=true,")>=0&&vDto.getSstampid()!=null&&vDto.getSstampid().indexOf(MsgConstant.VOUCHERSAMP_ATTACH)>=0)
					{
						vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS_BACK);
						vDto.setSdemo("��Ʊ�ɹ�");
					}else
					{
						vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS);
						vDto.setSdemo("���͵���ƾ֤��ɹ�");
					}
					vDto.setSvoucherflag("1");
					vDto.setSreturnerrmsg(err);
					vDto.setSconfirusercode(TimeFacade.getCurrentStringTime());// ����ƾ֤�ص�/���͵���ƾ֤������
					// ���͵���ƾ֤������ɹ�����ƾ֤״̬
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
							.debug("===============================���͵���ƾ֤��ɹ�===============================");
					continue;
				}else
				{
					if(err!=null&&(err.contains("���汾��ƾ֤��ǩ��")||err.contains("�������·���")||err.contains("�ɹ�ǩ��")||err.contains("�ظ�����")))
					{
						vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS);
						vDto.setSdemo("���͵���ƾ֤��ɹ�");
						vDto.setSvoucherflag("1");
						vDto.setSconfirusercode(TimeFacade.getCurrentStringTime());// ����ƾ֤�ص�/���͵���ƾ֤������
						count += VoucherUtil.updateVoucherState(vDto);
						paymentCalculation(vDto);
					}else{
						if(ITFECommonConstant.PUBLICPARAM.contains(",sendvoucher=searchstatus,"))
						{
							Map<String, Object[]> map = voucherService.batchQueryAllVoucherStatus("", vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(), new String[]{vDto.getSvoucherno()});
							String msg = String.valueOf(map==null?"":map.get(vDto.getSvoucherno()));
							if(!msg.contains("13")){
								vDto.setSstatus(DealCodeConstants.VOUCHER_SUCCESS);
								vDto.setSdemo("���͵���ƾ֤��ɹ�");
								vDto.setSvoucherflag("1");
								vDto.setSconfirusercode(TimeFacade.getCurrentStringTime());// ����ƾ֤�ص�/���͵���ƾ֤������
								count += VoucherUtil.updateVoucherState(vDto);
								paymentCalculation(vDto);
							}
						}
					}
				}
				// ���͵���ƾ֤�����ʧ�ܸ���ƾ֤״̬
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
						querydto.setCamtkind(StateConstant.Conpay_Direct);//ֱ��֧�����
					else if(MsgConstant.VOUCHER_NO_5106.equals(dto.getSvtcode()))
						querydto.setCamtkind(StateConstant.Conpay_Grant);//��Ȩ֧�����
					else
						querydto.setCamtkind(dto.getSext1());//֧����ʽ
				}
			}
		}catch(Exception e)
		{
			logger.debug(dto==null?"":dto.getSvtcode()+"ǰ�ü��㼯��֧������==================================="+dto==null?"":dto.getSdealno());
		}
	}
	/**
	 * ƾ֤�˻�
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
				// vDto.setSdemo("�˻سɹ�");
				// �˻غ� ����demoԭ�� �����û��Ų�ԭ��
				vDto.setSdemo(vDto.getSdemo().length() >= 1000 ? vDto
						.getSdemo() : "�˻�ԭ��" + vDto.getSdemo());
				vDto.setSreturnerrmsg(null);
				vDto.setSconfirusercode(TimeFacade.getCurrentStringTime());// ����ƾ֤�˻�����
			}
			try{
				voucherService.confirmVoucherfail(null, vDto.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()), vDto.getSvtcode(), voucherNos, failInfos);
			}catch(Exception e){
			}
			String err = voucherService.returnVoucherBack(null, vDto
					.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),
					vDto.getSvtcode(), voucherNos, failInfos);
			if(err!=null&&err.contains("���˻ػ�"))
				err = null;
			// ����ƾ֤״̬
			if (err == null) {
				count += VoucherUtil.updateVoucherState(voucherList);
				VoucherUtil.voucherReturnBackUpdateStatus(voucherList);
				logger
						.debug("===============================ƾ֤�˻سɹ�===============================");

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
	 * ���ҷ��ͻص���ƾ֤״̬
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
						sdemo = "[��������]"
								+ VoucherUtil
										.getVoucherReturnStatus((Object[]) map
												.get(it.next() + ""));
					}
					// ������Ϻ���ϵͳ�Զ����ɵ�2302,�������к�Ϊ�գ����������ת�� ����Ҫ��ѯ���еĻص�״̬
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
									+ "  [��������]"
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
								.debug("===============================ƾ֤��ѯ�ѻص�ƾ֤����ƾ֤״̬�ɹ�===============================");
	
					}
				}
			}
		}
		return count;
	}
	/**
	 * ���ҷ��ͻص���ƾ֤״̬
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
					sdemo = "[��������]"+ VoucherUtil.getVoucherReturnStatus((Object[]) map.get(it.next() + ""));
				}
				getmap.put(dto.getSvoucherno(), dto);
					// ������Ϻ���ϵͳ�Զ����ɵ�2302,�������к�Ϊ�գ����������ת�� ����Ҫ��ѯ���еĻص�״̬
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
						sdemo = sdemo+ "  [��������]"
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
	 * ����TCBS���ر��ĸ���ƾ֤״̬
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
				updateExce.addParam("����ɹ�");
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
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TIPS);//�����ظ�����tips����ʧ��
			} else if (MsgConstant.VOUCHER_NO_5209.equals(vtcode)) {// �����˸�
				updateExce.addParam(vtcode);
				updateExce.addParam(packno);
				updateExce.addParam(voucherno);
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);// ������
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);// �ѷ���
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TIPS);//tips����ʧ��
			} else if (MsgConstant.VOUCHER_NO_5106.equals(vtcode)) {// ��Ȩ֧�����
				updateExce.addParam(voucherno);// �ñ�����ƾ֤���к�
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);// ������
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);// �ѷ���
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TIPS);//tips����ʧ��
			} else if (MsgConstant.VOUCHER_NO_8207.equals(vtcode)) {// ����ҵ��֧����ϸ
				updateExce.addParam(strecode);
				updateExce.addParam(MsgConstant.VOUCHER_NO_8207);
				updateExce.addParam(DealCodeConstants.VOUCHER_VALIDAT_SUCCESS);// У��ɹ�����Ϊ8207������TIPS
				updateExce.addParam(packno);// 8207��Ӧ��5207��5201ƾ֤����
				updateExce.addParam(voucherno);// 8207��Ӧ��5207��5201ƾ֤���
			} else if (MsgConstant.VOUCHER_NO_5201.equals(vtcode)) {// ֱ��֧��
				updateExce.addParam(strecode);
				updateExce.addParam(vtcode);
				updateExce.addParam(packno);
				updateExce.addParam(voucherno);
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);// ������
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);// �ѷ���
				if (voucherdate.equals(StateConstant.BIZTYPE_CODE_BATCH)) {
					updateVoucherSql += ")";
					updateExce.addParam(voucherdate);// ҵ�����ͱ���
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
				updateExce.addParam(DealCodeConstants.VOUCHER_RECIPE);// ������
				updateExce.addParam(DealCodeConstants.VOUCHER_SENDED);// �ѷ���
				updateExce.addParam(DealCodeConstants.VOUCHER_FAIL_TIPS);//tips����ʧ��
			}
			updateExce.runQueryCloseCon(updateVoucherSql);
			logger
					.debug("===============================ƾ֤����TCBS��ִ���ĸ���ƾ֤״̬�ɹ�===============================");

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
	 * ����Tips���ر��ĸ���ƾ֤״̬
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
			// ���Ҹð���ˮ����[�ѷ���]״̬�ļ�¼���и���
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
					sdemo = "������";
				}
				for (TvVoucherinfoDto dto : list) {
					dto.setSstatus(state);
					dto.setSdemo(sdemo);
				}
				VoucherUtil.updateVoucherState(list);
				logger
						.debug("===============================ƾ֤����Tips���ĸ���ƾ֤״̬�ɹ�===============================");
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
	 * ����Tips���ر��ĸ���ƾ֤״̬
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
				// ���Ҹð���ˮ����[�ѷ���]״̬�ļ�¼���и���
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
						sdemo = "������";
					}
					for (TvVoucherinfoDto dto : list) {
						dto.setSstatus(state);
						dto.setSdemo(sdemo);
					}
					
					VoucherUtil.updateVoucherState(list);
					
					
					logger
							.debug("===============================ƾ֤����Tips���ĸ���ƾ֤״̬�ɹ�===============================");
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
	 * ����Tips���ر��ĸ���ƾ֤״̬
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
			// ���Ҹð���ˮ����[�ѷ���][������]״̬�ļ�¼���и���
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
					sdemo = "TIPS����ɹ�";
				}
				for (TvVoucherinfoDto dto : list) {
					dto.setSstatus(state);
					dto.setSdemo(sdemo);
				}
				VoucherUtil.updateVoucherState(list);
				logger.debug("===============================ƾ֤����Tips" + msgno
						+ "���ĸ���ƾ֤״̬�ɹ�===============================");
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
	 * ǩ��ʧ��
	 * 
	 * @return
	 */
	public static void voucherComfail(String sdealno, String errMsg) {
		TvVoucherinfoDto vDto = new TvVoucherinfoDto();
		errMsg = errMsg == null ? "���Ĳ��淶" : errMsg;
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
						.debug("===============================ǩ��ʧ�ܳɹ�===============================");
			} else if(!"230123025106510852075209".contains(vDto.getSvtcode())){
				logger.debug("===============================ǩ��ʧ�ܷ����쳣==============================="+err);
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
						logger.debug("===============================ǩ��ʧ�ܷ����쳣�����˻سɹ�===============================");
				}else
				{
					vDto.setSreturnerrmsg(err);
					logger.debug("===============================ǩ��ʧ�ܷ����쳣�����˻��쳣==============================="+err);
				}
				DatabaseFacade.getODB().update(vDto);
			}
		} catch (Exception e1) {
			logger.error(e1);
			VoucherException.saveErrInfo(vDto.getSvtcode(), e1);
		}
	}
	/**
	 * ǩ��ʧ��
	 * 
	 * @return
	 */
	public static void voucherComfail(TvVoucherinfoDto vDto, String errMsg) {
		if(vDto==null)
			return;
		errMsg = errMsg == null ? "���Ĳ��淶" : errMsg;
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
				logger.debug("===============================ǩ��ʧ�ܳɹ�===============================");
			}else
			{
				logger.debug("===============================ǩ��ʧ��ʱ�����쳣==============================="+err);
//				err = voucherService.returnVoucherBack(null, vDto
//						.getSadmdivcode(), Integer.parseInt(vDto.getSstyear()),
//						vDto.getSvtcode(), new String[] { vDto.getSvoucherno() },
//						new String[] { errMsg });
//				if(err==null)
//				{
//						vDto.setSstatus(DealCodeConstants.VOUCHER_FAIL);
						vDto.setSdemo(errMsg);
						vDto.setSreturnerrmsg(null);
//						logger.debug("===============================ǩ��ʧ�ܷ����쳣�����˻سɹ�===============================");
//				}else
//				{
//					vDto.setSreturnerrmsg(err);
//					logger.debug("===============================ǩ��ʧ�ܷ����쳣�����˻��쳣==============================="+err);
//				}
				DatabaseFacade.getODB().update(vDto);
			}
		} catch (Exception e1) {
			logger.error(e1);
			VoucherException.saveErrInfo(vDto.getSvtcode(), e1);
		}
	}
	/**
	 * ǩ��ʧ�� (����������ɫҵ��---���ղ���ϵͳ)
	 * 
	 * @author sunyan
	 * 
	 */
	public static void voucherComfailForSX(String sdealno, String errMsg) {
		TvVoucherinfoSxDto vDto = new TvVoucherinfoSxDto();
		errMsg = errMsg == null ? "���Ĳ��淶" : errMsg;
		try {
			errMsg = errMsg.length() > 100 ? errMsg.substring(0, 100) : errMsg;
			vDto.setSdealno(sdealno);
			vDto = (TvVoucherinfoSxDto) CommonFacade.getODB().findRsByDto(vDto)
					.get(0);
			vDto.setSstatus(DealCodeConstants.VOUCHER_RECEIVE_FAIL);
			vDto.setSdemo(errMsg);
			DatabaseFacade.getODB().update(vDto);
			logger.debug("========================ǩ��ʧ�ܳɹ�[������ɫ]========================");
		} catch (Exception e1) {
			logger.error(e1);
			VoucherException.saveErrInfo(vDto.getSvtcode(), e1);
		}
	}

	/**
	 * ǩ�ճɹ�
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
				vDto.setSdemo("ǩ�ճɹ�");
				vDto.setSreturnerrmsg(null);
				DatabaseFacade.getODB().update(vDto);
				logger
						.debug("===================== ǩ�ճɹ�========================");
				if (VoucherFactory.getVouMap() != null&& VoucherFactory.getVouMap().get(vDto.getSvoucherno()+vDto.getStrecode()) != null)
					VoucherFactory.getVouMap().remove(vDto.getSvoucherno()+vDto.getStrecode());
			} else {
				vDto.setSreturnerrmsg(err);
				vDto.setSdemo("�Ѷ�ȡ");
				DatabaseFacade.getODB().update(vDto);
				VoucherUtil.delRepeateData(vDto, true);
				err = " ǩ�ճɹ��쳣��" + err;
				err = err.getBytes().length>100 ? CommonUtil.subString(err, 60) : err;
				voucherComfail(vDto,err);
				throw new ITFEBizException("ƾ֤��" + vDto.getSvoucherno()
						+ " ǩ�ճɹ��쳣��" + err);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("ƾ֤��" + vDto.getSvoucherno()
					+ " ǩ�ճɹ��쳣��");
		}
	}
	/**
	 * ǩ�ճɹ�
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
						.debug("===================== ǩ�ճɹ�========================"+findto.getSvoucherno());
				if (VoucherFactory.getVouMap() != null&& VoucherFactory.getVouMap().get(findto.getSvoucherno()+findto.getStrecode()) != null)
					VoucherFactory.getVouMap().remove(findto.getSvoucherno()+findto.getStrecode());
			} else {
				throw new ITFEBizException("ƾ֤��" + findto.getSvoucherno()
						+ " ǩ�ճɹ��쳣��" + err);
			}

		} catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("ƾ֤��" + findto.getSvoucherno()
					+ " ǩ�ճɹ��쳣��");
		}
	}
	/**
	 * ����ƾ֤����(��ɫͨ��)
	 */
	public void sendData(TvVoucherinfoDto dto) throws ITFEBizException {
		String filePath = ITFECommonConstant.FILE_ROOT_PATH
				+ dto.getSfilename();
		String fileName = new File(filePath).getName();
		try {
			List<byte[]> fileList = FileUtil.getInstance().cutFile(filePath,
					1024 * 1024 * 3);// �ļ��ָ��С��3M
			if (fileList.size() == 0)
				throw new ITFEBizException("�ļ���" + fileName
						+ " Ϊ���ļ������ܷ��͵���ƾ֤�⣡");
			for (byte[] bytes : fileList) {
				// ��װXML����
				Document successDoc = DocumentHelper.createDocument();
				successDoc.setXMLEncoding("GBK");
				Element File = successDoc.addElement("File");
				Element FileName = File.addElement("FileName");// �ļ���
				FileName.setText(fileName);
				Element FileBlockCount = File.addElement("FileBlockCount");// �ļ��ְ��������ļ���2M�ְ�
				FileBlockCount.setText(fileList.size() + "");
				Element AfewBlock = File.addElement("AfewBlock");// �ļ�˳���
				AfewBlock.setText((fileList.indexOf(bytes) + 1) + "");
				Element Attach = File.addElement("Attach");// ����
				Attach.setText("");
				Element vtCode = File.addElement("VtCode");// ����
				vtCode.setText(dto.getSvtcode());
				Element voucherNo = File.addElement("VoucherNo");// ����
				voucherNo.setText(dto.getSvoucherno());
				Element FileData = File.addElement("FileData");// �ļ�������
				FileData.setText(VoucherUtil.base64Encode(new String(bytes,
						"GBK")));
				logger.error("������ɫͨ������:"+successDoc.asXML());
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
		logger.debug("===================== ����ƾ֤������" + fileName
				+ " �ɹ�========================");
	}

	/**
	 * ʵ���ʽ��˿�ƾ֤����
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
					voucherService.confirmVoucherfail(null, admdivcode, Integer.parseInt(year),	vtcode, new String[] {voucherno},new String[] { "���Ľڵ�Detail������!"});
				}else
				{
//					tempNode = detailnode.selectSingleNode("XCurReckMoney");String xCurReckMoney = tempNode==null?"":tempNode.getText();tempNode = detailnode.selectSingleNode("XCurDateMoney");String xCurDateMoney = tempNode==null?"":tempNode.getText();
					findto.setSstatus(DealCodeConstants.VOUCHER_READRETURN);
					findto.setSdemo("��������:"+xacctdate+"���˽��:"+("1".equals(xcheckresult)?"���":"�����")+xcheckreason);
					DatabaseFacade.getDb().update(findto);
					voucherService.confirmVoucher(null, admdivcode, Integer.valueOf(year), vtcode, new String[]{voucherno});	//ǩ�ճɹ�
				}
			}else
			{
				voucherService.confirmVoucherfail(null, admdivcode, Integer.parseInt(year),	vtcode, new String[] {voucherno},new String[] {"���Ľڵ�DetailList������!"});
			}
			
		} catch (JAFDatabaseException e) {
			voucherService.confirmVoucherfail(null, admdivcode, Integer.parseInt(year),	vtcode, new String[] {voucherno},new String[] { "����ʧ��:"+e.toString() });
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
