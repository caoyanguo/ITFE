package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.ShiboDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsFinmovepaysubDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua ʵ���ʽ�
 */
public class PayoutTipsFileOperForFj extends AbstractTipsFileOper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {
		try {
			ShiboDto sbdto = (ShiboDto)paramdto;
			int fbcount = 0; // �ְ�������
			int recordNum = 1; // ��¼��¼��
			
			String tmpPackNo = "";			
			String trecode = "";
			
			MulitTableDto multi = new MulitTableDto();
			multi.setBiztype(biztype);//ҵ������
			multi.setSbookorgcode(bookorgCode);//�����������
			
			BigDecimal famt = new BigDecimal("0.00");
			BigDecimal famtPack = new BigDecimal("0.00");
			BigDecimal famthead = new BigDecimal("0.00");
			int recordhead = 0; 

			List<String[]> fileContent = super.readFile(file, ",");
			List<IDto> listdto = new ArrayList<IDto>();
			List<IDto> packdtos = new ArrayList<IDto>();
			
			List<IDto> fadtos = new ArrayList<IDto>();
			
			File fi = new File(file);
			List<String> voulist = new ArrayList<String>();
			Set<String> bnkset = new HashSet<String>();
			Map<String, TsFinmovepaysubDto> movesubs = this.getMovepaysub(bookorgCode);	//��������֧��Ԥ���Ŀ����Ϣ
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgCode); //���˴��뻺��
			Map<String,TsPaybankDto> paymap = this.makeBankMap(); //֧���кŻ���
			for (int i = 0; i < fileContent.size(); i++) {
				String[] singDto = fileContent.get(i);
				if(i == 0) {
					//�����ļ�ͷ
					recordhead = Integer.parseInt(singDto[2]);
					famthead = new BigDecimal(singDto[3]);
				}else if( i > 0 && i<fileContent.size() -1){
					TbsTvPayoutDto payoutDto = new TbsTvPayoutDto();
					//���
					if(singDto[1] == null || "".equals(singDto[1].trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�д���'�������'Ϊ��!");
					}
					payoutDto.setStrecode(singDto[1]+"00");//�������
					trecode = singDto[1]+"00";
					if(i == 0) {
						//У���ļ��ظ�,�������+�ļ���
						String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_5101);
						if(null != errorInfo && errorInfo.length() > 0) {
							multi.getErrorList().add(errorInfo);
							return multi;
						}
					}
					if(!this.checkTreasury(trecode, bookorgCode)) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�й���������룺"+trecode+" û����'����������Ϣ����'�в��ҵ�!");
					}
					if(singDto[2] == null || "".equals(singDto[2].trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�д���'�������'Ϊ��!");
					}
					payoutDto.setSbillorg(singDto[2]);//�������
					
					if(singDto[3] == null || "".equals(singDto[3].trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�д���'�տλ����'Ϊ��!");
					}
					if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_FALSE)) {
						if(singDto[3]!=null && singDto[3].length() > 15) {
							payoutDto.setSbdgorgcode(singDto[3].substring(0, 15));//�տλ����
						}else {
							payoutDto.setSbdgorgcode(singDto[3]);//�տλ����
						}
					}else if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_TRUE)){
						payoutDto.setSbdgorgcode(singDto[3]);//�տλ����
					}
					//�޸�Ϊ���չ������+Ԥ�㵥λΨһ
					if(!rpmap.containsKey(singDto[1]+"00"+payoutDto.getSbdgorgcode().trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼��Ԥ�㵥λ���� '"+payoutDto.getSbdgorgcode()+"' û���ڷ��˴�������ҵ�!");
					}else {
						if(!"1".equals(rpmap.get(singDto[1]+"00"+payoutDto.getSbdgorgcode()).getCmayaprtfund().trim())) {
							multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼��Ԥ�㵥λ���� '"+payoutDto.getSbdgorgcode()+"' ��ӦԤ�㵥λ���ܽ���ʵ���ʽ�!");
						}
					}
					if(singDto[4] == null || "".equals(singDto[4].trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�д���'�տλ����'Ϊ��!");
					}
					
					if(singDto[4].getBytes().length > 60) {
						payoutDto.setSbdgorgname(singDto[4].substring(0,30)); //Ԥ�㵥λ����
						payoutDto.setSpayeename(singDto[4].substring(0,30));  //�տ�������
					}else {
						payoutDto.setSbdgorgname(singDto[4]);//Ԥ�㵥λ����
						payoutDto.setSpayeename(singDto[4]);//�տ�������
					}
					
					if(singDto[5] == null || "".equals(singDto[5].trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�д���'�տλ�˺�'Ϊ��!");
					}
					
					if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_FALSE)) {
						//�������½ӿڣ�����Ҫ�����ַ���ȡ
						if(singDto[5].length() > 32) {
							payoutDto.setSpayeeacct(singDto[5].substring(0,32));//�տλ�˺�
						}else {
							payoutDto.setSpayeeacct(singDto[5]);//�տλ�˺�
						}
					}else if(filekind != null && filekind.equals(StateConstant.IFNEWINTERFACE_TRUE)){
						payoutDto.setSpayeeacct(singDto[5]);//�տλ�˺�
					}	
					
					if(singDto[6] == null || "".equals(singDto[6].trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�д���'�տλ������'Ϊ��!");
					}
					
					TsPaybankDto bnkpaydto = paymap.get(singDto[6].trim());
					if(null == bnkpaydto) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�����տλ�����У�'"+singDto[6].trim()+"'û����'֧���кŲ�ѯ����' ���ҵ���Ӧ�������к�!");
					}else {
						if(null != bnkpaydto.getSstate() && "0".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼���տλ�����У�'"+singDto[6].trim()+"' ����'��Чǰ'״̬!");
						}else if (null != bnkpaydto.getSstate() && "2".equals(bnkpaydto.getSstate())) {
							multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼���տλ�����У�'"+singDto[6].trim()+"' ����'ע��'״̬!");
						}	
					}									
					payoutDto.setSpayeeopnbnkno(singDto[6].trim());//�տλ������
					/*
					 * �ְ����˴���5.3�޸ĳɰ������зְ�
					payoutDto.setSpayeebankno(bnkpaydto.getSpaybankno());
					bnkset.add(bnkpaydto.getSpaybankno()); // ���������зְ�
					*/
					payoutDto.setSpayeebankno(singDto[6].trim()); //��д�ɿ�����
					bnkset.add(payoutDto.getSpayeeopnbnkno()); // ���ݿ����зְ�
					if(singDto[7] == null || "".equals(singDto[7].trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�д���'����ƾ֤��'Ϊ��!");
					}
					payoutDto.setSvouno(singDto[7]);//����ƾ֤��
					voulist.add(trecode.trim()+","+singDto[7].trim());
					payoutDto.setSpaylevel(singDto[8]);//����
					if(singDto[9] == null || "".equals(singDto[9].trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�д���'֧�����ܿ�Ŀ����'Ϊ��!");
					}
					String err = this.verifySubject(dmap, singDto[9], MsgConstant.MSG_NO_5101, "1", fi.getName(),singDto[7]);
					if (!"".equals(err)) {
						if (null != dmap.get(singDto[9])) {
							multi.getErrorList().add(err);
						}else{
							if(null == movesubs.get(singDto[9])){
								multi.getErrorList().add("ʵ���ʽ��ļ�[" + fi.getName() + "]�й��ܿ�Ŀ���� " + singDto[9] 	+ "û����'Ԥ���Ŀ����'��'��������֧��Ԥ���Ŀ��'���ҵ�!");
							}
						} 
					}
					payoutDto.setSfuncsbtcode(singDto[9]);//֧�����ܿ�Ŀ����
					if (singDto[10].getBytes().length>20) {
						singDto[10] = singDto[10].substring(0,10);
					}
					payoutDto.setSmovefundreason(singDto[10]);//֧��ԭ��
					if(singDto[11] == null || "".equals(singDto[11].trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�д���'֧�����'Ϊ��!");
					}
					payoutDto.setFamt(new BigDecimal(singDto[11])); //֧�����
					if(singDto[12] == null || "".equals(singDto[12].trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[7].trim()+"]�ļ�¼�д���'��������'Ϊ��!");
					}
//					payoutDto.setDacct(CommonUtil.strToDate(singDto[12]));//��������
					payoutDto.setDvoucher(CommonUtil.strToDate(singDto[12])); //��������(ƾ֤����)
					payoutDto.setSpaybizkind(singDto[13].substring(0,1)); //��������
					err = this.verifySubject(dmap, singDto[14], MsgConstant.MSG_NO_5101, "2", fi.getName(),singDto[7]);
					if(!"".equals(err)) {
						multi.getErrorList().add(err);
					}
					if(singDto.length == 14) {
						payoutDto.setSecosbtcode(""); //֧�����ÿ�Ŀ����
					}else {
						payoutDto.setSecosbtcode(singDto[14]);//֧�����ÿ�Ŀ����
					}
					
					payoutDto.setIofyear(Integer.parseInt(fi.getName().substring(0, 4))); // �������
					payoutDto.setSpayeracct(sbdto.getPayeracct()); //�������˺Ų���Ϊ��
					payoutDto.setSpayername(sbdto.getPayername()); //����������
					payoutDto.setCbdgkind(MsgConstant.BDG_KIND_IN); //Ԥ�����಻��Ϊ�գ�Ĭ��Ԥ����
					payoutDto.setSbookorgcode(bookorgCode);// �����������
					payoutDto.setDaccept(TimeFacade.getCurrentDateTime()); //��������
					famt = famt.add(new BigDecimal(singDto[11]));
					if(biztype != null && !"".equals(biztype)) {
						payoutDto.setSbiztype(biztype);// ҵ������
					}else {
						payoutDto.setSbiztype(""); //ҵ������
					}
					payoutDto.setCtrimflag("0"); //�����ڱ�־ Ĭ��Ϊ0
					
					payoutDto.setSpackageno(tmpPackNo);  //����ˮ��
					payoutDto.setSfilename(fi.getName());
					payoutDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					recordNum++;
					listdto.add(payoutDto);
				}else {
					break;
				}
				
			}	
			Map<String, List<IDto>> bnkli = new HashMap<String, List<IDto>>();
			//�����������кŽ��зְ�
			  //1.���ȵõ������е�����
			for (String st : bnkset) {
				List<IDto> nlist = new ArrayList<IDto>();
				for (IDto dto : listdto) {
					TbsTvPayoutDto bkdto = (TbsTvPayoutDto) dto;
					if (st.equals(bkdto.getSpayeeopnbnkno())) {
						nlist.add(bkdto);
					}
				}
				bnkli.put(st, nlist);
			}
			
			//2.���ĳ�������µ���ϸDTO����������TIPS�ְ�����,������ٷְ�
			for (String st : bnkset) {
				List<IDto> detlist = bnkli.get(st);
				int li_Detail = (detlist.size()-1) / 1000;
				for (int k = 0; k <= li_Detail; k++) {
					int li_TempCount = 0;
					if (li_Detail == k) {
						li_TempCount = detlist.size();
					} else {
						li_TempCount = (k + 1) * 1000;
					}
					//���ɰ���ˮ��
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
					packdto.setStrecode(trecode);
					packdto.setSfilename(fi.getName());
					
					TsConvertfinorgDto condto = new TsConvertfinorgDto();
					condto.setSorgcode(bookorgCode);
					condto.setStrecode(trecode);
					List conlist = CommonFacade.getODB().findRsByDtoWithUR(condto);
					if(conlist == null || conlist.size() == 0 ){
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] �и��ݺ���������룺"+bookorgCode+"�� ����������룺"+trecode+" û���� '����������Ϣά������'���ҵ���Ӧ������������!");
					} else {
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
					packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
					famtPack = new BigDecimal("0.00");
					fbcount = 0 ;
					packdtos.add(packdto);
				}
			}
			if (famt.compareTo(famthead) != 0) {
				multi.getErrorList().add("���ܽ��[" + famt + "]���ļ�ͷ�ܽ��[" + famthead
						+ "]����");
			}
			
			//У���ļ���ƾ֤����Ƿ��ظ�
			int oldSize = 0;
			int newSize = 0;
			Set<String> sets = new HashSet<String>();
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+item.trim()+"]�ļ�¼�д���ƾ֤����ظ�");
				}
			}
			
			multi.setFatherDtos(fadtos);
			multi.setPackDtos(packdtos);
			multi.setVoulist(voulist);
			multi.setFamt(famt);//�ܽ��
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
