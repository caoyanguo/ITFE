package process6103;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.stamp.StampFacade;
import com.cfcc.itfe.persistence.dto.TvFinIncomeDetailDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.util.transformer.MtoCodeTrans;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

public class Process6103Main {
	static {
		ContextFactory.setContextFile("/config/ContextLoader_02.xml");
	}

	@SuppressWarnings("unchecked")
	public static String Process6130(String FilePath,String fincode){
		String flag="";
		File file=new File(FilePath);
		List<IDto> list = new ArrayList<IDto>();
		SAXReader reader=new SAXReader();
		org.dom4j.Document doc;
		try {
			doc = reader.read(file);
		Element root=doc.getRootElement();
		Iterator i=root.elementIterator("MSG");
		String finorgcode = "";// 财政机关代码
		String trecode = "";
		String intredate = "";
		String packno ="";
		while(i.hasNext()){
			Element fooi=(Element) i.next();
			Iterator j=fooi.elementIterator("VouchHead6103");
			while(j.hasNext()){
				Element fooj=(Element) j.next();
			  /*110601	东城区
				110602	西城区
				110605	朝阳区
				110606	海淀区
				110607	丰台区
				110608	石景山区
				110609	门头沟区
				110610	房山区
				110611	通州区
				110612	昌平区
				110613	顺义区
				110614	大兴区
				110615	怀柔区
				110616	密云区
				110617	平谷区
				110618	延庆区
				110619	燕山区
				110620	开发区
				110621	西客站区*/
				finorgcode = fincode;
				trecode = (String) fooj.elementText("TreCode");// 国库代码
				intredate = (String) fooj.elementText("ExportDate");// 入库凭证日期
				// 包流水号
				 packno = SequenceGenerator
						.changePackNoForLocal(SequenceGenerator
								.getNextByDb2(
										SequenceName.FILENAME_PACKNO_REF_SEQ,
										SequenceName.TRAID_SEQ_CACHE,
										SequenceName.TRAID_SEQ_STARTWITH,
										MsgConstant.SEQUENCE_MAX_DEF_VALUE));
				}
				Iterator m=fooi.elementIterator("VouchBody6103");
				while(m.hasNext()){
					Element foom=(Element) m.next();
					Iterator z=foom.elementIterator("VouchBill6103");
					while(z.hasNext()){
						Element fooz=(Element) z.next();
						/*<VouchBill6103>
						<TaxOrgCode>010600000001</TaxOrgCode>
						<ExpTaxVouNo>0000</ExpTaxVouNo>
						<ExportVouType>0</ExportVouType>
						<BudgetType>1</BudgetType>
						<BudgetLevelCode>3</BudgetLevelCode>
						<BudgetSubjectCode>103043505</BudgetSubjectCode>
						<TraAmt>1170.00</TraAmt>
						<Origin>2</Origin>
					    </VouchBill6103>*/
						String TaxOrgCode = (String) fooz.elementText("TaxOrgCode"); // 征收机关代码
						String ExportVouType = (String) fooz.elementText("ExportVouType");// 导出凭证类型
						String ExpTaxVouNo = (String) fooz.elementText("ExpTaxVouNo");// 凭证号码
						String BudgetType = (String) fooz.elementText("BudgetType"); // 预算种类
						String BudgetLevelCode = (String) fooz.elementText("BudgetLevelCode");; // 预算级次
						String BudgetSubjectCode = (String) fooz.elementText("BudgetSubjectCode");; // 预算科目
						String TraAmt = (String) fooz.elementText("TraAmt"); // 交易金额
						String Origin = (String) fooz.elementText("Origin"); // 凭证来源
						
//						HtvFinIncomeDetailDto dto = new HtvFinIncomeDetailDto();
						TvFinIncomeDetailDto dto = new TvFinIncomeDetailDto();
						/**
						 * 组织DTO准备保存数据*****************************
						 */
						String _sseq  = StampFacade.getStampSendSeq("JS"); // 取财政申请入库流水信息业务流水
						dto.setSseq(_sseq);// 业务流水号
						dto.setCbdgkind(BudgetType.trim());// 预算种类
						dto.setCbdglevel(BudgetLevelCode.trim());// 预算级次
						if (Origin == null || Origin.trim().equals("")) {
							dto.setCvouchannel("");// 凭证来源
						} else {
							dto.setCvouchannel(Origin.trim());// 凭证来源
						}
						dto.setFamt(MtoCodeTrans.transformBigDecimal(TraAmt));// 金额
						dto.setIpkgseqno(packno.trim());// 包流水号
						dto.setSbdgsbtcode(BudgetSubjectCode.trim());// 预算科目
						dto.setSexpvouno(ExpTaxVouNo.trim());// 凭证号码
						dto.setSexpvoutype(ExportVouType.trim());// 导出凭证类型
						dto.setStaxorgcode(TaxOrgCode.trim());// 征收机关代码
						dto.setStrecode(trecode.trim());// 国库代码
						dto.setSintredate(intredate.trim());// 入库凭证日期
						dto.setSorgcode(finorgcode.trim());// 财政机关代码
		
						list.add(dto);
					}
				}
			}
			DatabaseFacade.getDb().create(CommonUtil.listTArray(list));
		} catch (DocumentException e1) {
			System.out.println("DOM4J读取XML文件失败！"+e1.getMessage());
			flag="解析文件【"+file.getName()+"】时，DOM4J读取XML文件失败！";
		} catch (JAFDatabaseException e) {
			System.out.println("保存入库流水失败！"+e.getMessage());
			flag="解析文件【"+file.getName()+"】时，保存入库流水失败！";
		} catch (SequenceException e) {
			System.out.println("取财政申请入库流水信息的业务流水SEQ出错！"+e.getMessage());
			flag="解析文件【"+file.getName()+"】时，取财政申请入库流水的SEQ出错！";
		}
		return flag+"="+list.size();
	}
		
}
