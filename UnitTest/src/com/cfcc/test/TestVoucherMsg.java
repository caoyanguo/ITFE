package com.cfcc.test;

import java.io.File;
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
//import org.mule.DefaultMuleMessage;
//import org.mule.api.MuleEventContext;
//import org.mule.api.MuleException;
//import org.mule.api.MuleMessage;
//import org.mule.module.client.MuleClient;
//
//import com.cfcc.itfe.component.VoucherMessageComponent;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MessagePropertyKeys;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoSxDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.util.encrypt.Base64;
import com.cfcc.itfe.util.transformer.IVoucherDto2Map;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.itfe.voucher.service.VoucherUtil;
import com.cfcc.itfe.voucher.sxservice.VoucherVerifySX;
import com.cfcc.itfe.webservice.VoucherService;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;

public class TestVoucherMsg {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_01.xml");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String ls_AdmDiveCode = "330000";
		String FileName = "c:/1.msg";
		String ls_OrgCode = "110000000002";
		String TreCode = "";
		String ls_FinOrgCode = "330000";
		HashMap<String, String> dealnos = new HashMap<String, String>();
		TvVoucherinfoDto voucherDto = new TvVoucherinfoDto();
		try {
			String voucherXml = FileUtil.getInstance().readFile(FileName);
			Document fxrDoc = DocumentHelper.parseText(voucherXml);
			List listNodes = fxrDoc.selectSingleNode("MOF").selectNodes(
					"VoucherBody");
			int li_CountCurrent = Integer.parseInt(fxrDoc.selectSingleNode(
					"MOF").selectSingleNode("VoucherCount").getText());
			for (int i = 0; i < listNodes.size(); i++) {
				String bodyVoucher = ((Node) listNodes.get(i)).asXML();
				Element e = (Element) listNodes.get(i);
				Document fxrDocVoucher = DocumentHelper.parseText(bodyVoucher);

				Node voucherNode = fxrDocVoucher
						.selectSingleNode("VoucherBody").selectSingleNode(
								"Voucher");
				String voucher = voucherNode.asXML();
				String StYear = e.attribute("StYear").getText();
				String VtCode = e.attribute("VtCode").getText();
				String VoucherNo = e.attribute("VoucherNo").getText();
				String VoucherFlag = ((Element) listNodes.get(i))
						.elementText("VoucherFlag");
				String Attach = ((Element) listNodes.get(i))
						.elementText("Attach");

				// 上海无纸化报文无国库代码节点
				if (ITFECommonConstant.PUBLICPARAM.indexOf(",sh,") < 0
						|| VtCode.equals(MsgConstant.VOUCHER_NO_2252)) {
					TreCode = voucherNode.selectSingleNode("TreCode").getText();
				}
				String billOrgCode = VoucherUtil.getSpaybankcode(VtCode,
						voucherNode);// 出票单位
				billOrgCode = (VtCode.equals(MsgConstant.VOUCHER_NO_5201) && (StringUtils
						.isBlank(billOrgCode))) ? ls_FinOrgCode : billOrgCode;// 5201出票单位取财政代码
				String Total = VoucherUtil.getTotalAmt(VtCode, voucherNode);
				String mainvou = VoucherUtil.getGrantSequence();
				dealnos.put(VoucherNo, mainvou);
				voucherDto = new TvVoucherinfoDto();
				voucherDto.setSdealno(mainvou);
				voucherDto.setNmoney(MtoCodeTrans.transformBigDecimal(Total));
				voucherDto.setSadmdivcode(ls_AdmDiveCode);
				voucherDto.setScreatdate(TimeFacade.getCurrentStringTime());
				voucherDto.setSrecvtime(new Timestamp(new java.util.Date()
						.getTime()));
				voucherDto.setSfilename(FileName);
				voucherDto.setSorgcode(ls_OrgCode);
				voucherDto.setSattach(Attach);
				voucherDto.setSstatus(DealCodeConstants.VOUCHER_ACCEPT);
				voucherDto.setSdemo("解析报文异常");
				voucherDto.setSstyear(TimeFacade.getCurrentStringTime()
						.substring(0, 4));
				voucherDto.setStrecode(TreCode);
				voucherDto.setSvoucherflag("1");
				voucherDto.setSvoucherno(VoucherNo);
				voucherDto.setSvtcode(VtCode);
				voucherDto.setSpaybankcode(billOrgCode); // 出票单位

				// DatabaseFacade.getDb().create(voucherDto);

				String savexml = fxrDocVoucher.asXML();

				String Xml = savexml.replaceAll("&lt;&lt;", "《").replaceAll(
						"&gt;&gt;", "》").replaceAll("&lt;", "《").replaceAll(
						"&gt;", "》");
//				VoucherUtil.sendTips(voucherDto, ls_FinOrgCode, dealnos,
//						savexml);
			}
		} catch (FileOperateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		catch (ITFEBizException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
//		} catch (MuleException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
