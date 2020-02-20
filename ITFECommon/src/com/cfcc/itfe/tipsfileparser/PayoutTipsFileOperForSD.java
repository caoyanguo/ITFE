package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.CommonParamDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TdBookacctMainDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua ʵ���ʽ�
 */
public class PayoutTipsFileOperForSD extends AbstractTipsFileOper {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> dmap) throws ITFEBizException {
		try {
			int fbcount = 0; // �ְ�������
			int recordNum = 1; // ��¼��¼��
			String tmpPackNo = "";

			String fname = new File(file).getName();
			String ctrimflag = fname.substring(fname.length() - 5, fname
					.length() - 4); // �����ڱ�־
			String trecode = "";

			MulitTableDto multi = new MulitTableDto();
			multi.setBiztype(biztype);// ҵ������
			multi.setSbookorgcode(bookorgCode);// �����������
			BigDecimal famt = new BigDecimal("0.00");
			BigDecimal famtPack = new BigDecimal("0.00");

			List<IDto> listdto = new ArrayList<IDto>();
			List<IDto> packdtos = new ArrayList<IDto>();
			List<IDto> fadtos = new ArrayList<IDto>();
			File fi = new File(file);
			List<String> voulist = new ArrayList<String>();
			Set<String> bnkset = new HashSet<String>();
			Set<String> treset = new HashSet<String>();

			Map<String, TsPaybankDto> paymap = this.makeBankMap(); // ֧���кŻ���
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgCode); //���˴��뻺��
			Map<String,TsInfoconnorgaccDto> bookacctMap= this.getBookAcctMap(bookorgCode);//�������˻���Ϣ
			Map<String,TsOrganDto> bookorginfo= this.getOrganInfo();//��ȡ����������Ϣ
			Map<String,TdBookacctMainDto> finTreAcctInfo= this.getFinTreAcctFromBookAcctInfo();//��ȡ��������˻���Ϣ
			
			// �����ж����ֻ�ȡ��Կ�ķ�ʽ
			String key = "";
			String encyptMode ="";

			// �ļ�����
			List<String[]> fileContent = super.readFile(file, ",");
			
			// ȡ���Ƿ����þ��ÿ�Ŀ����
			boolean isEcnomic = false;
			if(fileContent.size()>0){
				String[] data = fileContent.get(0);
				TsTreasuryDto treasurydto = new TsTreasuryDto();
				treasurydto.setStrecode(data[0]);
				List<TsTreasuryDto> dot = CommonFacade.getODB().findRsByDtoWithUR(treasurydto);
				if (dot.size()==1) {
					if(StateConstant.COMMON_YES.equals(dot.get(0).getSisuniontre())){
						isEcnomic = true;
					}
				}else{
					multi.getErrorList().add("�������[" +data[0]+ fi.getName()
							+ "������������");
					return multi;
				}
			}
			CommonParamDto _dto = (CommonParamDto) paramdto;
			encyptMode =_dto.getEncryptMode();
			int record =fileContent.size();
			if(StateConstant.SM3_ENCRYPT.equals(encyptMode)&& record >1){
				record =record -1;
			}
			for (int i = 0; i < record; i++) {
				
				String[] singDto = fileContent.get(i);
				TbsTvPayoutDto payoutDto = new TbsTvPayoutDto();
				payoutDto.setStrecode(singDto[0]); // �������
				//�ڵ�0�л�ȡһ����Կ�����ݲ�ͬ�����ȡ��Կ
				if (i == 0) {
					if ("SHANDONG".equals(_dto.getArea())) {
						key = this.findKeyForValidate("", ""); // ����ļ�У��Key
					} else {// ���մ������Կ����ģʽȡ��Ӧ����Կ
						TsMankeyDto keydto= TipsFileDecrypt.findKeyByKeyMode(_dto.getKeyMode(),
								bookorgCode, singDto[0]);
						if (null!=keydto) {
							key  =keydto.getSkey();
							
						}else{
							multi.getErrorList().add("ʵ���ʽ��ļ�[" + fi.getName()
									+ "û�в��ҵ���Ӧ�Ľ�����Կ��");
							return multi;
						}
					}
				}
				// �����ܵ���Ҫ������֤�������ļ�У��
				if (StateConstant.NO_ENCRYPT.equals(encyptMode)) {
					if (!this.importValidator(singDto[12], singDto[7], singDto[5],
							new BigDecimal(singDto[11]), singDto[singDto.length-1], key)) {
						multi.getErrorList().add("ʵ���ʽ��ļ�[" + fi.getName()
								+ "]�е� " + (i + 1) + " ��У������֤����!");
					}
				}
				if(StateConstant.SD_ENCRYPT.equals(encyptMode)){
					if(!SM3Process.verifySM3Sign(StringUtils.join(singDto,","), key)){
						multi.getErrorList().add("ʵ���ʽ��ļ�[" + fi.getName()
								+ "]�е� " + (i + 1) + " ������ǩ��У��ʧ��!");
					}
				}
			
				
				trecode = singDto[0].trim();
				if(i == 0) {
					//У���ļ��ظ�,�������+�ļ���
					String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_5101);
					if(null != errorInfo && errorInfo.length() > 0) {
						multi.getErrorList().add(errorInfo);
						return multi;
					}
					//�����ļ���֤SM3�㷨����ȷ��
					if(StateConstant.SM3_ENCRYPT.equals(encyptMode)){
						if(!SM3Process.verifySM3SignFile(file,key)){
							multi.getErrorList().add("ʵ���ʽ��ļ�[" + fi.getName()
									+ "]SM3ǩ����֤ʧ��!");
						 return multi;
						}
					}
				}		
				if(!this.checkTreasury(trecode, bookorgCode)) {
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼�й���������룺"+trecode+" û����'����������Ϣ����'�в��ҵ�!");
				}
				treset.add(trecode);
				payoutDto.setSpayeracct(singDto[1]); // �������˺�
				
				//ͨ���������͸������˺� �ҵ���Ӧ����������
				if (bookacctMap.containsKey(trecode+singDto[1].trim())) {
					TsInfoconnorgaccDto tndto = bookacctMap.get(trecode+singDto[1].trim());
					payoutDto.setSpayername(tndto.getSpayername());
				}else{
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼�и��ݹ�����룺"+trecode+" , �������˺ţ�"+singDto[1].trim()+" û����'�ʽ𲦸��������˻�ά��'�������ҵ���Ӧ���������ƣ�����ά��");
				}
				payoutDto.setSmovefundreason(singDto[2]); // ֧��ԭ�������ߵ���ԭ�����
				payoutDto.setSbdgorgcode(singDto[3]); // Ԥ�㵥λ����
				//�޸�Ϊ���չ������+Ԥ�㵥λΨһ
				if(!rpmap.containsKey(trecode+payoutDto.getSbdgorgcode().trim())) {
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼��Ԥ�㵥λ���� '"+payoutDto.getSbdgorgcode()+"' û���ڷ��˴�������ҵ�!");
				}else {
					if(!"1".equals(rpmap.get(trecode+payoutDto.getSbdgorgcode()).getCmayaprtfund().trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼��Ԥ�㵥λ���� '"+payoutDto.getSbdgorgcode()+"' ��ӦԤ�㵥λ���ܽ���ʵ���ʽ�!");
					}
				}
				payoutDto.setSbdgorgname(singDto[4]); //Ԥ�㵥λ����	
				if(VerifyParamTrans.isNull(singDto[4])){//Ԥ�㵥λ���벻��Ϊ��
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼��Ԥ�㵥λ���Ʋ���Ϊ��!");
				}
				payoutDto.setSpayeeacct(singDto[5]); // �տλ�˺�
				if(VerifyParamTrans.isNull(singDto[5])){//�տλ�˺Ų���Ϊ��
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼���տλ�˺Ų���Ϊ��!");
				}
				
				payoutDto.setCbdgkind(singDto[6]); // Ԥ������
				String err = this.verifySubject(dmap, singDto[7], MsgConstant.MSG_NO_5101, "1", fi.getName(),singDto[12]);
				if(!"".equals(err)) {
					multi.getErrorList().add(err);
				}
				payoutDto.setSfuncsbtcode(singDto[7]); // ���ܿ�Ŀ����				
				payoutDto.setSecosbtcode(singDto[8]); // ���ÿ�Ŀ����
				//У�龭�ÿ�Ŀ����
				if(!isEcnomic){//û�����þ��ÿ�Ŀ����
					payoutDto.setSecosbtcode(""); // ���ÿ�Ŀ����
				}else{//���þ��ô���Ļ������ô�����벻Ϊ��
					if(null ==singDto[8]||"".equals(singDto[8].trim())){//���ÿ�Ŀ���벻Ϊ��
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼�����þ��ô��������£����ÿ�Ŀ����û����д!");
					}
				}	
				payoutDto.setSbooksbtcode(singDto[9]); // ��ƿ�Ŀ���� (��Ϊ��)
				payoutDto.setSpaybizkind(singDto[10]); // ֧������
				payoutDto.setFamt(new BigDecimal(singDto[11])); // ������   ��ֵת������
				payoutDto.setSvouno(singDto[12]); // ƾ֤���
				if(VerifyParamTrans.isNull(singDto[12])){//ƾ֤��Ų���Ϊ��
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤��Ų���Ϊ��!");
				}
				voulist.add(trecode.trim()+","+singDto[12].trim());
				payoutDto.setSpayeename(singDto[13]); //�տ�������
				
				//ת�������д���(ɽ��)
				String spayeebnkno=singDto[14].trim();
				if (null==spayeebnkno || "".equals(spayeebnkno) ) {
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼�տ��˿������к� ����Ϊ��!");
				}else if(spayeebnkno.length()<=8 && spayeebnkno.length()>2){
					TsGenbankandreckbankDto bankdto = new TsGenbankandreckbankDto();
					bankdto.setSgenbankcode(spayeebnkno);
					bankdto.setSbookorgcode(bookorgCode);
					List dot = CommonFacade.getODB().findRsByDtoWithUR(bankdto);
					if (dot == null || dot.size() == 0) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼�����տ��˿������кţ�'"+singDto[14].trim()+"'û����'���д�����֧���кŶ�Ӧ��ϵ����' ���ҵ���Ӧ���չ�ϵ������ά��");
					}else {
						TsGenbankandreckbankDto bdto = (TsGenbankandreckbankDto)dot.get(0);
						spayeebnkno = bdto.getSreckbankcode();
					}					
				}				
				payoutDto.setSpayeeopnbnkno(spayeebnkno); //�տ��˿������кţ������д��ࣩ
				
				/**
				 * 9���޸ģ��ж�����ֶ���ĿΪ16�������һλ���븽���ֶΣ��������
				 */
				if(singDto.length == 16) {
					if(null != singDto[15] && !"".equals(singDto[15])) {
						if (singDto[15].getBytes().length>60) {
							multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼�и��Գ��ȳ��������Գ������֧��60���ַ���30������!");
						}
						payoutDto.setSaddword(singDto[15]);
						
					}
				}							
				//����ת�������Ŀ������к��ҵ���Ӧ���������к�
				TsPaybankDto bnkpaydto = paymap.get(spayeebnkno);
				if(null == bnkpaydto) {
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼�и����տ��˿������кţ�'"+spayeebnkno+"'û����'֧���кŲ�ѯ����' ���ҵ���Ӧ�������к�!");
				}else {
					if(null != bnkpaydto.getSstate() && "0".equals(bnkpaydto.getSstate())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼���տλ�����У�'"+spayeebnkno+"' ����'��Чǰ'״̬!");
					}else if (null != bnkpaydto.getSstate() && "2".equals(bnkpaydto.getSstate())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[12].trim()+"]�ļ�¼���տλ�����У�'"+spayeebnkno+"' ����'ע��'״̬!");
					}
					//˵����ϵͳ�ڵ���֧������Ҫ�ж��˻��ĺϷ���
					if (StateConstant.TCBSCENTERRECKBANKNO.equals(bnkpaydto.getSpaybankno())) {
						//�����к��ҵ���Ӧ�ĺ�������
						if (bookorginfo.containsKey(spayeebnkno)) {
							String bookorg =bookorginfo.get(spayeebnkno).getSorgcode();
							if (!finTreAcctInfo.containsKey(bookorg+singDto[5])) {
								payoutDto.setSgroupid(StateConstant.COMMON_YES);//��Ҫ������Ȩ�ı�ʾ
							 }
						}else{//�Ҳ�����������˵��������ʡ���������Ҫ������Ȩ
							payoutDto.setSgroupid(StateConstant.COMMON_YES);	
						}
					}
				}							
				/*
				 * �ְ����˴���5.3�޸ĳɰ������зְ�
				payoutDto.setSpayeebankno(bnkpaydto.getSpaybankno());
				bnkset.add(bnkpaydto.getSpaybankno()); // ���������зְ�
				*/
				payoutDto.setSpayeebankno(spayeebnkno); //��д�ɿ�����
				bnkset.add(spayeebnkno); // ���ݿ����зְ�
				
				payoutDto.setSbookorgcode(bookorgCode);
				famt = famt.add(new BigDecimal(singDto[11]));
				payoutDto.setDvoucher(CommonUtil.strToDate(fi.getName().substring(0, 8))); //ƾ֤����
				payoutDto.setIofyear(Integer.parseInt(fi.getName().substring(0, 4))); //�������
				payoutDto.setSbookorgcode(bookorgCode);// �����������
				payoutDto.setDaccept(TimeFacade.getCurrentDateTime());
				if(biztype != null && !"".equals(biztype)) {
					payoutDto.setSbiztype(biztype);// ҵ������
				}else {
					payoutDto.setSbiztype("");// ҵ������
				}
				payoutDto.setCtrimflag(ctrimflag);
				
				payoutDto.setSpackageno(tmpPackNo);  //����ˮ��
				payoutDto.setSfilename(fi.getName());
				payoutDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
				listdto.add(payoutDto);
				recordNum++;
			}
			
			Map<String,Map<String,List<IDto>>> treli = new HashMap<String,Map<String,List<IDto>>>();
			/**
			 * Ϊ��Ӧ���յط��������ļ�������������ӷְ����򣺼��Ϲ������  2013-03-25  by hua 
			 */
			//1�����Ȱ��չ���������ְ�
			for(String treC : treset) {
				Map<String ,List<IDto>> tmpMap = new HashMap<String ,List<IDto>>();
				//2�������տ��зֿ�
				for(String st : bnkset) {
					List<IDto> nlist = new ArrayList<IDto>();
					for (IDto dto : listdto) {
						TbsTvPayoutDto bkdto = (TbsTvPayoutDto) dto;
						if (treC.trim().equals(bkdto.getStrecode()) && st.equals(bkdto.getSpayeeopnbnkno())) {
							nlist.add(bkdto);
						}
					}
					if(nlist.size() > 0) {
						tmpMap.put(st, nlist);
					}					
				}
				treli.put(treC, tmpMap);
			}
			
			//3����ʼ ��ʽ�ְ�
			for(String treC : treset) {
				Map<String ,List<IDto>> bnkli = treli.get(treC); //�õ���Ӧ����ļ�¼
				//2.���ĳ�������µ���ϸDTO����������TIPS�ְ�����,������ٷְ�
				for (String st : bnkset) {
					List<IDto> detlist = bnkli.get(st);
					if(null == detlist || detlist.size() == 0) {
						continue;
					}
					int li_Detail = (detlist.size()-1) / 1000;
					for (int k = 0; k <= li_Detail; k++) {
						int li_TempCount = 0;
						if (li_Detail == k) {
							li_TempCount = detlist.size();
						} else {
							li_TempCount = (k + 1) * 1000;
						}
						tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
								.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
										SequenceName.TRAID_SEQ_CACHE,
										SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
						for (int j = k * 1000; j < li_TempCount; j++) {
							TbsTvPayoutDto bkdto = (TbsTvPayoutDto) detlist.get(j);
							//���ð���ˮ��
							bkdto.setSpackageno(tmpPackNo);
							//���н��ͳ�ƣ����ڷְ�
							famtPack = famtPack.add(bkdto.getFamt());
							//����������ͳ��
							fbcount ++;
							//���������������ɵ�DTO����
							fadtos.add(bkdto);
						}
						TvFilepackagerefDto packdto = new TvFilepackagerefDto();
						packdto.setSorgcode(bookorgCode);
						packdto.setStrecode(treC);
						packdto.setSfilename(fi.getName());
						TsConvertfinorgDto condto = new TsConvertfinorgDto();
						condto.setSorgcode(bookorgCode);
						condto.setStrecode(trecode);
						List conlist = CommonFacade.getODB().findRsByDtoWithUR(condto);
						if(conlist == null || conlist.size() == 0 ){
							multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] �и��ݺ���������룺"+bookorgCode+"�� ����������룺"+trecode+" û���� '����������Ϣά������'���ҵ���Ӧ������������!");
						}else {
							TsConvertfinorgDto gd = (TsConvertfinorgDto)conlist.get(0);
							packdto.setStaxorgcode(gd.getSfinorgcode());
						}				
						packdto.setScommitdate(TimeFacade.getCurrentStringTime());
						packdto.setSaccdate(TimeFacade.getCurrentStringTime());
						packdto.setSpackageno(tmpPackNo);
						packdto.setSoperationtypecode(biztype);
						packdto.setIcount(fbcount);
						packdto.setNmoney(famtPack);
						packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
						packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
						packdto.setSusercode(userid);
						packdto.setImodicount(0);
						fbcount = 0;
						famtPack = new BigDecimal("0.00");
						packdtos.add(packdto);
					}
				}
			}
					
			//�ļ���ƾ֤���У��
			int oldSize = 0;
			int newSize = 0;
			Set<String> sets = new HashSet<String>();
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"]�д���ƾ֤��� "+item.trim()+" �ظ�");
				}
			}
			
			multi.setFatherDtos(listdto);
			multi.setPackDtos(packdtos);
			multi.setVoulist(voulist);
			multi.setFamt(famt); //�ܽ��
			multi.setTotalCount(recordNum); //�ܱ���
			return multi;
			
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("ʵ���ʽ��ļ��������� \n"+e.getMessage(), e);
		}
	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}


}
