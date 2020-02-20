package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua  ֱ��֧������ļ����������
 */
public class DirectpayPlanTipsFileOper extends AbstractTipsFileOper {
	/* (non-Javadoc)
	 * @see com.cfcc.itfe.tipsfileparser.ITipsFileOper#fileParser(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, com.cfcc.jaf.persistence.jaform.parent.IDto, java.util.Map)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,String userid,
			 String biztype,
			String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap) throws ITFEBizException {

		int recordNum = 0; // ��¼��¼��
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// ҵ������
		mulitDto.setSbookorgcode(bookorgCode);// �����������
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal sonFamt = new BigDecimal("0.00");
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> sondtos = new ArrayList<IDto>();
		List<IDto> sondtos_tmp = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		List<String> voulist = new ArrayList<String>(); //ƾ֤��ż�
		File fi = new File(file);
		Map<String,BigDecimal> diremap = new HashMap<String, BigDecimal>();
		
		String strecode="";
		String mainvou ="";
		String mainvou_tmp ="";
		String svo = "";
		try {
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgCode); //���˴��뻺��
			HashMap<String, TsTreasuryDto> tremap =SrvCacheFacade.cacheTreasuryInfo(bookorgCode);//������뻺��
			HashMap<String, TsConvertfinorgDto> finmap =SrvCacheFacade.cacheFincInfo(bookorgCode);//�������뻺��
			Map<String,String> bankInfo= this.getBankInfo(bookorgCode);//���������ձ��ȡ�����������������к���Ϣ
			List<String[]> fileContent = super.readFile(file, ",");
			String trecode="";
			for (int i = 0; i < fileContent.size(); i++) {
				String[] strs = fileContent.get(i);
				if (i == 0) {
					/**
					 *  �ļ���һ��Ϊ������Ϣ	
					 */
					mainvou = SequenceGenerator.getNextByDb2(SequenceName.DIRECTPAY_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH);
					mainvou_tmp = mainvou;
					//����ˮ��Ĭ��Ϊ"",�����ļ����ʱ������
					String tmpPackNo = "";
					
					TbsTvDirectpayplanMainDto mainDto = new TbsTvDirectpayplanMainDto();
					mainDto.setIvousrlno(Long.parseLong(mainvou));
					strecode =strs[0];
					if(!this.checkTreasury(strs[0], bookorgCode)) {
						mulitDto.getErrorList().add("ֱ��֧������ļ�["+fi.getName()+"] ƾ֤���Ϊ["+strs[9].trim()+"]�ļ�¼�й���������룺"+strs[0]+" ��'����������Ϣ����'�в�����!");
					}
				    trecode =strs[0];
					mainDto.setStrecode(strs[0]); // �������
					String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_5102);
					if(null != errorInfo && errorInfo.length() > 0) {
						mulitDto.getErrorList().add(errorInfo);
					}
					mainDto.setSbiztype(biztype);// ҵ������
					mainDto.setSpayeracct(strs[1]); // �������ʺ�
					//������ȫ��(��λ����)
					mainDto.setSpayeropnbnkno(strs[3]); // �����˿�������
					mainDto.setSpayeeacct(strs[4]); // �տ����ʺ�
					mainDto.setSpayeename(strs[5]); // �տ���ȫ��
					mainDto.setSpayeeopnbnkno(strs[6]); // �տ��˿�����
					mainDto.setCbdgkind(strs[7]); // Ԥ���������
					mainDto.setFamt(new BigDecimal(strs[8])); // �ϼƽ��
					mainDto.setSvouno(strs[9]); // ƾ֤���
					svo = strs[9];
					voulist.add(trecode.trim()+","+strs[9].trim());
					mainDto.setScrpvoucode(strs[10]); // ��Ӧƾ֤���
					mainDto.setDvoucher(CommonUtil.strToDate(strs[11]));// ƾ֤����
					famt = famt.add(new BigDecimal(strs[8]));
					
					//�����ж��Ƿ����Ϻ��Ĳ�¼����
					if(ITFECommonConstant.ISMATCHBANKNAME.equals(StateConstant.IF_MATCHBNKNAME_YES)) {
						if(strs.length > 12 && null != strs[12] && !"".equals(strs[12])) {
							mainDto.setSpayeeopnbnkname(strs[12]);
							if(null != strs[6] && !"".equals(strs[6])) {
								mainDto.setSpayeeopnbnkno(strs[6]);
								mainDto.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);
							}else {
								if(null != bankInfo.get(strs[12]) && !"".equals(bankInfo.get(strs[12]))) {
									mainDto.setSpayeeopnbnkno(bankInfo.get(strs[12]));
									mainDto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								}else {
									mainDto.setSpayeeopnbnkno("");
									mainDto.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								}
							}
						}else {
							mulitDto.getErrorList().add("ֱ��֧������ļ�["+fi.getName()+"] ƾ֤���Ϊ["+strs[9].trim()+"]�ļ�¼�� �տ��˿��������� Ϊ�գ����֤!");
						}
					}else {
						mainDto.setSpayeeopnbnkno(strs[6]); // �տ��˿�����
					}
					
					mainDto.setIofyear(Integer.parseInt(fi.getName().substring(0,4))); //������Ȳ�Ϊ��
					mainDto.setDaccept(TimeFacade.getCurrentDateTime()); //�������ڲ�Ϊ��(Ĭ�ϵ�ǰ����)
					mainDto.setSstatus(StateConstant.CONFIRMSTATE_NO);  //����״̬
					mainDto.setSpackageno(tmpPackNo);
					mainDto.setSbookorgcode(bookorgCode);// �����������
					mainDto.setSfilename(fi.getName());
					//�ļ��������Ӧ��ϵ
					TvFilepackagerefDto packdto = new TvFilepackagerefDto();
					packdto.setSorgcode(bookorgCode);
					packdto.setStrecode(strs[0]);
					packdto.setSfilename(fi.getName());
					if (finmap.containsKey(strs[0])) {
						packdto.setStaxorgcode(finmap.get(strs[0]).getSfinorgcode());
					}else{
						mulitDto.getErrorList().add("ֱ��֧������ļ�["+fi.getName()+"] ƾ֤���Ϊ["+strs[9].trim()+"]�ļ�¼�и��ݺ���������룺"+bookorgCode+"�� ����������룺"+strs[0]+" �� '����������Ϣά������'�в�����!");
					}				
					packdto.setScommitdate(TimeFacade.getCurrentStringTime());
					packdto.setSaccdate(TimeFacade.getCurrentStringTime());
					packdto.setSpackageno(tmpPackNo);
					packdto.setSoperationtypecode(biztype);
					packdto.setIcount(1);
					packdto.setNmoney(famt);
					packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
					packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
					packdto.setSusercode(userid);
					packdto.setImodicount(0);
					
					packdtos.add(packdto);
					fatherdtos.add(mainDto);
				} else {
					//sub��Ϣ
					if(strs.length>1){
						TbsTvDirectpayplanSubDto subDto = new TbsTvDirectpayplanSubDto();
						String err = this.verifySubject(dmap, strs[0], MsgConstant.MSG_NO_5102, "1", fi.getName(),svo);
						if(!"".equals(err)) {
							mulitDto.getErrorList().add(err);
						}
						subDto.setSfuncsbtcode(strs[0]); // ���ܿ�Ŀ����
						
						if(null != tremap.get(trecode)) {
							if (StateConstant.COMMON_YES.equals(tremap.get(trecode).getSisuniontre())) { //���õ�������жϾ��ô��벻��Ϊ��
								err = this.verifySubject(dmap, strs[1], MsgConstant.MSG_NO_5102, "2", fi.getName(),svo);
								if(!"".equals(err)) {
									mulitDto.getErrorList().add(err);
								}
								subDto.setSecosbtcode(strs[1]); // ���ÿ�Ŀ����
							} else { //�����õ�����£������ļ����Ƿ��о��ô��룬����д��ֵ
								subDto.setSecosbtcode("");
							}
						}
						
						subDto.setSbdgorgcode(strs[2]); // Ԥ�㵥λ����
						//�޸�Ϊ���չ������+���˴������ά��
						if(!rpmap.containsKey(trecode+subDto.getSbdgorgcode().trim())) {
							mulitDto.getErrorList().add("ֱ��֧������ļ�["+fi.getName()+"] ƾ֤���Ϊ["+svo+"]�ļ�¼��Ԥ�㵥λ���� '"+subDto.getSbdgorgcode()+"' �ڷ��˴�����в�����!");
						}
						subDto.setFamt(new BigDecimal(strs[3]));// ������
						sonFamt = sonFamt.add(new BigDecimal(strs[3]));
						subDto.setSbookorgcode(bookorgCode); //�����������
						subDto.setIvousrlno(Long.parseLong(mainvou)); //ƾ֤��ˮ��
						if(diremap.containsKey(strs[0]+","+strs[1]+","+strs[2])) { //����ͬԤ���Ŀ�����ܴ��롢���ô�����л���
							diremap.put(strs[0]+","+strs[1]+","+strs[2], diremap.get(strs[0]+","+strs[1]+","+strs[2]).add(new BigDecimal(strs[3])));
						}else {
							diremap.put(strs[0]+","+strs[1]+","+strs[2], new BigDecimal(strs[3]));
						}
						sondtos_tmp.add(subDto);
						recordNum++; 
					}
				}
			}
		}catch(ArrayIndexOutOfBoundsException e)
		{
			log.error(e);
			throw new ITFEBizException("ֱ��֧������ļ�"+fi.getName()+"�������� \n"+e.getMessage(), e);
		}catch(FileOperateException e)
		{
			log.error(e);
			throw new ITFEBizException("ֱ��֧������ļ�"+fi.getName()+"�������� \n"+e.getMessage(), e);
		}catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("ֱ��֧������ļ�"+fi.getName()+"��������\n"+e.getMessage(), e);
		}
		if (famt.compareTo(new BigDecimal("0.00")) == 0) {
			mulitDto.getErrorList().add("ֱ��֧������ļ�["+fi.getName()+"]�������Ϊ0");
		}
		if (famt.compareTo(sonFamt) != 0) {
			mulitDto.getErrorList().add("ֱ��֧������ļ�["+fi.getName()+"]�л��ܽ��[" + famt + "]����ϸ�ܽ��[" + sonFamt
					+ "]����");
		}
		
		//�ļ���ƾ֤���У��
		int oldSize = 0;
		int newSize = 0;
		Set<String> sets = new HashSet<String>();
		for (String item : voulist) {
			oldSize = sets.size();
			sets.add(item);
			newSize = sets.size();
			if (newSize == oldSize) {
				mulitDto.getErrorList().add("ֱ��֧������ļ�["+fi.getName()+"]�д���ƾ֤����ظ�");
			}
		}
		//���չ��ܡ����á�Ԥ�㵥λ����
		if(recordNum >499) {
			for(String single : diremap.keySet()) {
				String[] strs = single.split(",");
				TbsTvDirectpayplanSubDto diredto = new TbsTvDirectpayplanSubDto();
				diredto.setIvousrlno(Long.parseLong(mainvou_tmp));
				diredto.setSfuncsbtcode(strs[0]);
				diredto.setSecosbtcode(strs[1]);
				diredto.setSbdgorgcode(strs[2]);
				diredto.setFamt(diremap.get(single));
				diredto.setSbookorgcode(bookorgCode);
				sondtos.add(diredto);
			}
		}else {
			sondtos.addAll(sondtos_tmp);
		}
		if(sondtos.size() > 499) {
			mulitDto.getErrorList().add("ֱ��֧������ļ�["+fi.getName()+"]����ϸ������Ϣ���չ������Ŀ���롢�������Ŀ���롢Ԥ�㵥λ������л��ܺ��ܱ���Ϊ["+sondtos.size()+"],����499�ʣ����ֹ��������!");			
		}
		for(int i = 1 ; i <= sondtos.size() ; i++) {
			TbsTvDirectpayplanSubDto d = (TbsTvDirectpayplanSubDto) sondtos.get(i-1);	
			d.setIdetailseqno(i);
		}
		mulitDto.setFatherDtos(fatherdtos);
		mulitDto.setSonDtos(sondtos);
		mulitDto.setPackDtos(packdtos);
		mulitDto.setVoulist(voulist);
		mulitDto.setFamt(famt);

		return mulitDto;

	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

}
