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
		String finorgcode = "";// �������ش���
		String trecode = "";
		String intredate = "";
		String packno ="";
		while(i.hasNext()){
			Element fooi=(Element) i.next();
			Iterator j=fooi.elementIterator("VouchHead6103");
			while(j.hasNext()){
				Element fooj=(Element) j.next();
			  /*110601	������
				110602	������
				110605	������
				110606	������
				110607	��̨��
				110608	ʯ��ɽ��
				110609	��ͷ����
				110610	��ɽ��
				110611	ͨ����
				110612	��ƽ��
				110613	˳����
				110614	������
				110615	������
				110616	������
				110617	ƽ����
				110618	������
				110619	��ɽ��
				110620	������
				110621	����վ��*/
				finorgcode = fincode;
				trecode = (String) fooj.elementText("TreCode");// �������
				intredate = (String) fooj.elementText("ExportDate");// ���ƾ֤����
				// ����ˮ��
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
						String TaxOrgCode = (String) fooz.elementText("TaxOrgCode"); // ���ջ��ش���
						String ExportVouType = (String) fooz.elementText("ExportVouType");// ����ƾ֤����
						String ExpTaxVouNo = (String) fooz.elementText("ExpTaxVouNo");// ƾ֤����
						String BudgetType = (String) fooz.elementText("BudgetType"); // Ԥ������
						String BudgetLevelCode = (String) fooz.elementText("BudgetLevelCode");; // Ԥ�㼶��
						String BudgetSubjectCode = (String) fooz.elementText("BudgetSubjectCode");; // Ԥ���Ŀ
						String TraAmt = (String) fooz.elementText("TraAmt"); // ���׽��
						String Origin = (String) fooz.elementText("Origin"); // ƾ֤��Դ
						
//						HtvFinIncomeDetailDto dto = new HtvFinIncomeDetailDto();
						TvFinIncomeDetailDto dto = new TvFinIncomeDetailDto();
						/**
						 * ��֯DTO׼����������*****************************
						 */
						String _sseq  = StampFacade.getStampSendSeq("JS"); // ȡ�������������ˮ��Ϣҵ����ˮ
						dto.setSseq(_sseq);// ҵ����ˮ��
						dto.setCbdgkind(BudgetType.trim());// Ԥ������
						dto.setCbdglevel(BudgetLevelCode.trim());// Ԥ�㼶��
						if (Origin == null || Origin.trim().equals("")) {
							dto.setCvouchannel("");// ƾ֤��Դ
						} else {
							dto.setCvouchannel(Origin.trim());// ƾ֤��Դ
						}
						dto.setFamt(MtoCodeTrans.transformBigDecimal(TraAmt));// ���
						dto.setIpkgseqno(packno.trim());// ����ˮ��
						dto.setSbdgsbtcode(BudgetSubjectCode.trim());// Ԥ���Ŀ
						dto.setSexpvouno(ExpTaxVouNo.trim());// ƾ֤����
						dto.setSexpvoutype(ExportVouType.trim());// ����ƾ֤����
						dto.setStaxorgcode(TaxOrgCode.trim());// ���ջ��ش���
						dto.setStrecode(trecode.trim());// �������
						dto.setSintredate(intredate.trim());// ���ƾ֤����
						dto.setSorgcode(finorgcode.trim());// �������ش���
		
						list.add(dto);
					}
				}
			}
			DatabaseFacade.getDb().create(CommonUtil.listTArray(list));
		} catch (DocumentException e1) {
			System.out.println("DOM4J��ȡXML�ļ�ʧ�ܣ�"+e1.getMessage());
			flag="�����ļ���"+file.getName()+"��ʱ��DOM4J��ȡXML�ļ�ʧ�ܣ�";
		} catch (JAFDatabaseException e) {
			System.out.println("���������ˮʧ�ܣ�"+e.getMessage());
			flag="�����ļ���"+file.getName()+"��ʱ�����������ˮʧ�ܣ�";
		} catch (SequenceException e) {
			System.out.println("ȡ�������������ˮ��Ϣ��ҵ����ˮSEQ����"+e.getMessage());
			flag="�����ļ���"+file.getName()+"��ʱ��ȡ�������������ˮ��SEQ����";
		}
		return flag+"="+list.size();
	}
		
}
