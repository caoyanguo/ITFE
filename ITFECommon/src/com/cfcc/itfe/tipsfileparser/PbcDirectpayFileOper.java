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
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.CommonParamDto;
import com.cfcc.itfe.persistence.dto.TbsTvPbcpayDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua ���а���ֱ��֧��
 */
public class PbcDirectpayFileOper extends AbstractTipsFileOper {

	public MulitTableDto fileParser(String file, String bookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {
		try {
//			String userid = ((ITFELoginInfo)loginfo).getSuserCode();
			MulitTableDto multiDto = new MulitTableDto();			
			int fbcount = 0; // �ְ�������
			int recordNum = 1; // ��¼��¼��
			BigDecimal famtPack = new BigDecimal("0.00"); //�ְ����õĽ�����
			
			multiDto.setBiztype(biztype);//ҵ������
			multiDto.setSbookorgcode(bookorgcode);//�����������
			List<IDto> listdto = new ArrayList<IDto>(); //������ݽ�������
			List<IDto> packdtos = new ArrayList<IDto>(); //��ŷְ�����
			List<IDto> fadtos = new ArrayList<IDto>(); //��ŷְ�����
			File fi = new File(file);
			
			String tmpPackNo = "";
			String trecode = "";
			// �����ж����ֻ�ȡ��Կ�ķ�ʽ
			String key = "";
			String encyptMode ="";
			List<String> voulist = new ArrayList<String>(); //���ƾ֤���
			Set<String> bnkset = new HashSet<String>(); //����տ��˿�����			
			
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgcode); //���˴��뻺��
			Map<String,TsPaybankDto> paymap = this.makeBankMap(); //֧���кŻ���
			Map<String,TsConvertfinorgDto> finmap = this.makeFincMap(bookorgcode); //�������뻺��
			
			List<String[]> fileContent = super.readFile(file, ","); //���ļ����ա�,���ָ��
			
			for(int i = 0 ; i < fileContent.size() ; i++){
				String[] strs = fileContent.get(i);
				TbsTvPbcpayDto mainDto = new TbsTvPbcpayDto();
				trecode =strs[0];
				if(!this.checkTreasury(strs[0], bookorgcode)) {
					multiDto.getErrorList().add("���а���ֱ��֧���ļ�["+fi.getName()+"] ƾ֤���Ϊ["+strs[11].trim()+"]�ļ�¼�й���������룺"+strs[0]+" ��'����������Ϣ����'�в�����!");
				}
				
				if(i == 0) {					
					CommonParamDto _dto = (CommonParamDto) paramdto;
					encyptMode =_dto.getEncryptMode();
					if ("SHANDONG".equals(_dto.getArea())) {
						key = this.findKeyForValidate("", ""); // ����ļ�У��Key
					} else {// ���մ������Կ����ģʽȡ��Ӧ����Կ
						TsMankeyDto keydto= TipsFileDecrypt.findKeyByKeyMode(_dto.getKeyMode(),
								bookorgcode, strs[0]);
						if (null!=keydto) {
							key  =keydto.getSkey();
						}else{
							multiDto.getErrorList().add("���а���ֱ��֧���ļ�[" + fi.getName()
									+ "û�в��ҵ���Ӧ�Ľ�����Կ��");
							return multiDto;
						}
					}
					/**
					 * �����ж��Ƿ�Ϊ�ظ����룬����ǣ����������������
					 */
					String errorInfo = this.checkFileExsit(bookorgcode, trecode, fi.getName(), MsgConstant.MSG_NO_5104);
					if(null != errorInfo && errorInfo.length() > 0) {
						multiDto.getErrorList().add(errorInfo);
						return multiDto;
					}
					
					//�����ļ���֤SM3�㷨����ȷ��
					if(StateConstant.SM3_ENCRYPT.equals(encyptMode)){
						if(!SM3Process.verifySM3SignFile(file,key)){
							multiDto.getErrorList().add("���а���ֱ��֧���ļ�[" + fi.getName()
									+ "]SM3ǩ����֤ʧ��!");
						 return multiDto;
						}
						fileContent.remove(fileContent.size() - 1);
					}
				}
				
				
				mainDto.setStrecode(strs[0]); // �������
				mainDto.setSbiztype(biztype);// ҵ������
				mainDto.setSpayeracct(strs[1]); // �������ʺ�
				if(null == strs[2] || "".equals(strs[2])) {
					multiDto.getErrorList().add("���а���ֱ��֧���ļ� ["+fi.getName()+"] ƾ֤���Ϊ["+strs[11].trim()+"]�ļ�¼�д���'������ȫ��'Ϊ��!");					
				}else {
					mainDto.setSpayername(strs[2]);//������ȫ��(��λ����)
				}				
				mainDto.setSpayeropnbnkno(strs[3]); // �����˿�������
				if(null == strs[4] || "".equals(strs[4])) {
					multiDto.getErrorList().add("���а���ֱ��֧���ļ� ["+fi.getName()+"] ƾ֤���Ϊ["+strs[11].trim()+"]�ļ�¼�д���'�տ����˺�'Ϊ��!");
				}else {
					mainDto.setSpayeeacct(strs[4]); // �տ����ʺ�
				}				
				if(null == strs[5] || "".equals(strs[5])) {
					multiDto.getErrorList().add("���а���ֱ��֧���ļ� ["+fi.getName()+"] ƾ֤���Ϊ["+strs[11].trim()+"]�ļ�¼�д���'�տ���ȫ��'Ϊ��!");
				}else {
					mainDto.setSpayeename(strs[5]); // �տ���ȫ��
				}									
				if(strs[6] == null || "".equals(strs[6].trim())) {
					multiDto.getErrorList().add("���а���ֱ��֧���ļ� ["+fi.getName()+"] ƾ֤���Ϊ["+strs[11].trim()+"]�ļ�¼�д���'�տλ������'Ϊ��!");
				}
				TsPaybankDto bnkpaydto = paymap.get(strs[6].trim());
				if(null == bnkpaydto) {
					multiDto.getErrorList().add("���а���ֱ��֧���ļ� ["+fi.getName()+"] ƾ֤���Ϊ["+strs[11].trim()+"]�ļ�¼�����տλ�����У�'"+strs[6].trim()+"'û����'֧���кŲ�ѯ����' ���ҵ���Ӧ�������к�!");
				}else {
					if(null != bnkpaydto.getSstate() && "0".equals(bnkpaydto.getSstate())) {
						multiDto.getErrorList().add("���а���ֱ��֧���ļ� ["+fi.getName()+"] ƾ֤���Ϊ["+strs[11].trim()+"]�ļ�¼���տλ�����У�'"+strs[6].trim()+"' ����'��Чǰ'״̬!");
					}else if (null != bnkpaydto.getSstate() && "2".equals(bnkpaydto.getSstate())) {
						multiDto.getErrorList().add("���а���ֱ��֧���ļ� ["+fi.getName()+"] ƾ֤���Ϊ["+strs[11].trim()+"]�ļ�¼���տλ�����У�'"+strs[6].trim()+"' ����'ע��'״̬!");
					}
				}
				
				bnkset.add(strs[6].trim()); 
				mainDto.setSpayeeopnbnkno(strs[6].trim()); // �տ��˿�����	
				if(null == strs[7] || "".equals(strs[7])) {
					mainDto.setCbdgkind("1"); //Ϊ����Ĭ��Ϊ1--Ԥ����
				}else {
					mainDto.setCbdgkind(strs[7]); // Ԥ���������
				}
				
				String err = this.verifySubject(bmap, strs[8], MsgConstant.MSG_NO_5104, "1", fi.getName(),strs[11]);
				if(null != err && !"".equals(err)) {
					multiDto.getErrorList().add(err);
				}
				mainDto.setSfuncsbtcode(strs[8]); //���ܿ�Ŀ����
				if(null != strs[9] && !"".equals(strs[9])) { //��Ϊ�յ������ȥ�ж�
					err = this.verifySubject(bmap, strs[9], MsgConstant.MSG_NO_5104, "2", fi.getName(),strs[11]);
					if(null != err && !"".equals(err)) {
						multiDto.getErrorList().add(err);
					}
					mainDto.setSecosbtcode(strs[9]); //���ÿ�Ŀ����
				}	
				mainDto.setSbdgorgcode(strs[10]); //Ԥ�㵥λ����
				if(!rpmap.containsKey(strs[0]+mainDto.getSbdgorgcode().trim())) {
					multiDto.getErrorList().add("���а���ֱ��֧���ļ�["+fi.getName()+"] ƾ֤���Ϊ["+strs[11].trim()+"]�ļ�¼��Ԥ�㵥λ���� '"+mainDto.getSbdgorgcode()+"' û���ڷ��˴�������ҵ�!");
				}				
				mainDto.setSvouno(strs[11]); //ƾ֤���
				voulist.add(trecode+","+strs[11]); 
				mainDto.setDvoucher(CommonUtil.strToDate(strs[12])); //ƾ֤����
				mainDto.setSbackflag(strs[13]); //�˻ر�־
				mainDto.setFamt(new BigDecimal(strs[14])); //���
				
				/**���ϳ�ɳҪ���ڽӿ��ϼ��ϸ��� 20140916**/
				if(strs.length>15 && strs[15]!=null && !"".equals(strs[15])) {
					mainDto.setSaddword(strs[15]);
				}
				
				mainDto.setSpackageno(tmpPackNo);  //����ˮ��
				mainDto.setSfilename(fi.getName()); //�ļ���
				mainDto.setSstatus(StateConstant.CONFIRMSTATE_NO); //����״̬
				mainDto.setSbookorgcode(bookorgcode);
				mainDto.setCtrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL); //�����ڱ�־Ĭ��Ϊ 0--������
				mainDto.setDaccept(TimeFacade.getCurrentDateTime()); //�������ڲ�Ϊ��(Ĭ�ϵ�ǰ����)
				listdto.add(mainDto);
			}
			
			Map<String, List<IDto>> bnkli = new HashMap<String, List<IDto>>();
			//�����������кŽ��зְ�
			  //1.���ȵõ������е�����
			for (String st : bnkset) {
				List<IDto> nlist = new ArrayList<IDto>();
				for (IDto dto : listdto) {
					TbsTvPbcpayDto bkdto = (TbsTvPbcpayDto) dto;
					if (st.equals(bkdto.getSpayeeopnbnkno())) {
						nlist.add(bkdto);
					}
				}
				bnkli.put(st, nlist);
			}
			
			//2.���ĳ�տ�����µ���ϸDTO����������TIPS�ְ�����,������ٷְ�
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
					tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
							.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
									SequenceName.TRAID_SEQ_CACHE,
									SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
					for (int j = k * 1000; j < li_TempCount; j++) {
						TbsTvPbcpayDto bkdto = (TbsTvPbcpayDto) detlist.get(j);
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
					packdto.setSorgcode(bookorgcode);
					packdto.setStrecode(trecode);
					packdto.setSfilename(fi.getName());
					TsConvertfinorgDto gd = finmap.get(trecode);
					if(null == gd) {
						multiDto.getErrorList().add("���а���ֱ��֧���ļ�["+fi.getName()+"] �и��ݺ���������룺"+bookorgcode+"�� ����������룺"+trecode+" û���� '����������Ϣά������'���ҵ���Ӧ������������!");
					}else {
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
			
			//У���ļ���ƾ֤����Ƿ��ظ�
			int oldSize = 0;
			int newSize = 0;
			Set<String> sets = new HashSet<String>();
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					multiDto.getErrorList().add("���а���ֱ��֧���ļ�["+fi.getName()+"]�д���ƾ֤����ظ�");
				}
			}
			
			multiDto.setFatherDtos(fadtos);
			multiDto.setPackDtos(packdtos);
			multiDto.setVoulist(voulist);
			multiDto.setFamt(famtPack);  //�ܽ��
			multiDto.setTotalCount(recordNum); //�ܱ���
			return multiDto;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("���а���ֱ��֧���ļ��������� \n"+e.getMessage(), e);
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
