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
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvGrantpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua ��Ȩ֧������ļ����������
 */
public class GrantpayPlanTipsFileOper extends AbstractTipsFileOper {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {

		MulitTableDto mulitDto = new MulitTableDto();
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal sonfamt = new BigDecimal("0.00");
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> sondtos = new ArrayList<IDto>();
		List<IDto> sondtos_tmp = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		File fi = new File(file);
		int recordNum = 0; // ��¼��¼��
		List<String> voulist = new ArrayList<String>();
		Map<String,BigDecimal> grantmap = new HashMap<String, BigDecimal>();
		Map<String,BigDecimal> zeromap = new HashMap<String, BigDecimal>();
		Map<String,BigDecimal> smallmap = new HashMap<String, BigDecimal>();
		String mainvou = "";
		String mainvou_tmp = "";
		String trecode = "";
		String svo = "";
		try {
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgCode); //���˴��뻺��
			HashMap<String, TsTreasuryDto> tremap =SrvCacheFacade.cacheTreasuryInfo(bookorgCode);
			HashMap<String, TsConvertfinorgDto> finmap =SrvCacheFacade.cacheFincInfo(bookorgCode);//�������뻺��
			List<String[]> fileContent = super.readFile(file, ",");
			for (int i = 0; i < fileContent.size(); i++) {
				String[] strs = fileContent.get(i);
				if (i == 0) {
					//��һ��Ϊ�ļ���������Ϣ
					mainvou = SequenceGenerator.getNextByDb2(SequenceName.GRANTPAY_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH);
					mainvou_tmp = mainvou;
					//����ˮ��Ĭ��Ϊ"",�����ļ����ʱ������
					String tmpPackNo = "";
					
					TbsTvGrantpayplanMainDto mainDto = new TbsTvGrantpayplanMainDto();
					mainDto.setIvousrlno(Long.parseLong(mainvou)); //ƾ֤��ˮ��
					trecode = strs[0];
					mainDto.setStrecode(trecode); // �������
					String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_5103);
					if(null != errorInfo && errorInfo.length() > 0) {
						mulitDto.getErrorList().add(errorInfo);
					}
					if(!this.checkTreasury(strs[0].trim(), bookorgCode)) {
						mulitDto.getErrorList().add("��Ȩ֧������ļ�["+fi.getName()+"] ƾ֤���Ϊ["+strs[1].trim()+"]�ļ�¼�й���������룺"+strs[0].trim()+" ��'����������Ϣ����'�в�����!");
					}
					mainDto.setSvouno(strs[1]); // ƾ֤���
					svo = strs[1];
					voulist.add(trecode.trim()+","+strs[1].trim());
					mainDto.setSbnkno(strs[2]); // �������д���
					mainDto.setSbdgorgcode(strs[3]); // Ԥ�㵥λ����
					if(!rpmap.containsKey(strs[0]+mainDto.getSbdgorgcode().trim())) {
						mulitDto.getErrorList().add("��Ȩ֧������ļ�["+fi.getName()+"] ƾ֤���Ϊ["+strs[1].trim()+"]�ļ�¼��Ԥ�㵥λ���� '"+mainDto.getSbdgorgcode()+"' �ڷ��˴�����в�����!");
					}
					if (strs[4] != null && !strs[4].trim().equals("")) {
						mainDto.setIofmonth(new Integer(strs[4].trim())); // �����·�
					}
					mainDto.setCbdgkind(strs[5]); // Ԥ������
					if (strs[6] != null && !strs[6].trim().equals("")) {
						mainDto.setFzerosumamt(new BigDecimal(strs[6].trim())); // ��������
					} else {
						mainDto.setFzerosumamt(new BigDecimal("0.00")); // ��������
					}
					if (strs[7] != null && !strs[7].trim().equals("")) { // С���ֽ�����
						mainDto.setFsmallsumamt(new BigDecimal(strs[7].trim()));
					} else {
						mainDto.setFsmallsumamt(new BigDecimal("0.00"));
					}
					famt = famt.add(mainDto.getFzerosumamt()).add(
							mainDto.getFsmallsumamt());
					mainDto.setDaccept(TimeFacade.getCurrentDateTime()); //�������ڲ���Ϊ��
					mainDto.setSbiztype(biztype);// ҵ������
					mainDto.setSfilename(fi.getName());
					mainDto.setSpackageno(tmpPackNo); //����ˮ��
					
					//�ļ��������Ӧ��ϵ
					TvFilepackagerefDto packdto = new TvFilepackagerefDto();
					packdto.setSorgcode(bookorgCode);					
					packdto.setStrecode(strs[0]);
					packdto.setSfilename(fi.getName());
					if (finmap.containsKey(strs[0])) {
						packdto.setStaxorgcode(finmap.get(strs[0]).getSfinorgcode());
					}else{
						mulitDto.getErrorList().add("��Ȩ֧������ļ�["+fi.getName()+"] ƾ֤���Ϊ["+strs[1].trim()+"]�ļ�¼�и��ݺ���������룺"+bookorgCode+"�� ����������룺"+strs[0]+" �� '����������Ϣά������'�в�����!");
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
					packdto.setSorgcode(bookorgCode);					
					mainDto.setSbookorgcode(bookorgCode);// �����������
					mainDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					packdtos.add(packdto);
					fatherdtos.add(mainDto);

				} else {
					if(strs.length>1){
						TbsTvGrantpayplanSubDto subDto = new TbsTvGrantpayplanSubDto();
						subDto.setIvousrlno(Long.parseLong(mainvou));
						
						String err = this.verifySubject(dmap, strs[0], MsgConstant.MSG_NO_5103, "1", fi.getName(),svo);
						if(!"".equals(err)) {
							mulitDto.getErrorList().add(err);
						}
						subDto.setSfuncsbtcode(strs[0]); // ���ܿ�Ŀ����
						if(null != tremap.get(trecode)) {
							if (StateConstant.COMMON_YES.equals(tremap.get(trecode).getSisuniontre())) { //���õ�������жϾ��ô��벻��Ϊ��
								err = this.verifySubject(dmap, strs[1], MsgConstant.MSG_NO_5103, "2", fi.getName(),svo);
								if(!"".equals(err)) {
									mulitDto.getErrorList().add(err);
								}
								subDto.setSecosbtcode(strs[1]); // ���ÿ�Ŀ����
							} else { //�����õ�����£������ļ����Ƿ��о��ô��룬����д��ֵ
								subDto.setSecosbtcode("");
							}
						}					
						subDto.setFzerosumamt(new BigDecimal(strs[2])); // ��������
						subDto.setFsmallsumamt(new BigDecimal(strs[3]));// С���ֽ�����
						subDto.setIvousrlno(Long.parseLong(mainvou));
						sonfamt = sonfamt.add(new BigDecimal(strs[2])).add(
								new BigDecimal(strs[3]));
						if(grantmap.containsKey(strs[0]+","+strs[1])) { //����ͬ���ܴ��롢���ô�����л���
							grantmap.put(strs[0]+","+strs[1], grantmap.get(strs[0]+","+strs[1]).add(new BigDecimal(strs[2])).add(new BigDecimal(strs[3])));
							zeromap.put(strs[0]+","+strs[1], zeromap.get(strs[0]+","+strs[1]).add(new BigDecimal(strs[2])));
							smallmap.put(strs[0]+","+strs[1], smallmap.get(strs[0]+","+strs[1]).add(new BigDecimal(strs[3])));
						}else {
							grantmap.put(strs[0]+","+strs[1], new BigDecimal(strs[2]).add(new BigDecimal(strs[3])));
							zeromap.put(strs[0]+","+strs[1], new BigDecimal(strs[2]));
							smallmap.put(strs[0]+","+strs[1], new BigDecimal(strs[3]));
						}
						sondtos_tmp.add(subDto);
						recordNum ++;
//						sondtos.add(subDto);
					}
				}
			}	
		}catch(ArrayIndexOutOfBoundsException e)
		{
			log.error(e);
			throw new ITFEBizException("��Ȩ֧������ļ�"+fi.getName()+"�������� \n"+e.getMessage(), e);
		}catch(FileOperateException e)
		{
			log.error(e);
			throw new ITFEBizException("��Ȩ֧������ļ�"+fi.getName()+"�������� \n"+e.getMessage(), e);
		}catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("��Ȩ֧������ļ�"+fi.getName()+"�������� \n"+e.getMessage(), e);
		}

		if (famt.compareTo(new BigDecimal(0)) == 0) {
			mulitDto.getErrorList().add("��Ȩ֧������ļ�["+fi.getName()+"]�������С���ֽ������ͬʱΪ 0 ");
		}
		if (famt.compareTo(sonfamt) != 0) {
			mulitDto.getErrorList().add("��Ȩ֧������ļ�["+fi.getName()+"]�л��ܽ��[" + famt + "]����ϸ�ܽ��[" + sonfamt
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
				mulitDto.getErrorList().add("��Ȩ֧������ļ�["+fi.getName()+"]�д���ƾ֤����ظ�");
			}
		}
		
		//���չ��ܡ����û���
		if(recordNum >499) {
			for(String single : grantmap.keySet()) {
				String[] strs = single.split(",");
				TbsTvGrantpayplanSubDto grantdto = new TbsTvGrantpayplanSubDto();
				grantdto.setIvousrlno(Long.parseLong(mainvou_tmp));
				grantdto.setSfuncsbtcode(strs[0]);
				if(strs.length == 1) {
					grantdto.setSecosbtcode("");
				}else {
					grantdto.setSecosbtcode(strs[1]);
				}
				
				grantdto.setFzerosumamt(zeromap.get(single));
				grantdto.setFsmallsumamt(smallmap.get(single));
				sondtos.add(grantdto);
			}
		}else {
			sondtos.addAll(sondtos_tmp);
		}
		
		if(sondtos.size() > 499) {
			mulitDto.getErrorList().add("��Ȩ֧������ļ�["+fi.getName()+"]����ϸ������Ϣ���չ������Ŀ���롢�������Ŀ������л��ܺ��ܱ���Ϊ["+sondtos.size()+"],����499�ʣ����ֹ��������!");			
		}
		for(int i = 1 ; i <= sondtos.size() ; i++) {
			TbsTvGrantpayplanSubDto d = (TbsTvGrantpayplanSubDto) sondtos.get(i-1);	
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
