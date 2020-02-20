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
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.TSystemFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpaySubDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TbsTvFreeDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsConverttaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
/**
 * author leilianjie  ��ֵ�
 */
public class TaxFreeTipsFileOper extends AbstractTipsFileOper {

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {
		int recordNum = 0; // ��¼��¼��
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// ҵ������
		mulitDto.setSbookorgcode(sbookorgcode);// �����������
		BigDecimal famt = new BigDecimal("0.00");
		List<IDto> freedtos = new ArrayList<IDto>();
		Map<String, List<TbsTvFreeDto>> pkgs = new HashMap<String, List<TbsTvFreeDto>>();
		Set<String> taxcodes = new HashSet<String>();
		List<IDto> packdtos = new ArrayList<IDto>();
		List<String> voulist = new ArrayList<String>(); //ƾ֤��ż�
		
		try {
			HashMap<String, TsTreasuryDto> tremap =SrvCacheFacade.cacheTreasuryInfo(sbookorgcode);
			// ȡ�ø�����־���ձ�
			DwbkTipsFileOperForSD sd = new DwbkTipsFileOperForSD();
			HashMap viceMap = sd.getViceSignMap(sbookorgcode);
			File f = new File(file);
			List<String[]> fileContent = super.readFile(file, ",");
			for (int i = 0; i < fileContent.size(); i++) {
				String[] cols = fileContent.get(i);
				if(i == 0) {
					//У���ļ��ظ�,�������+�ļ���
					String errorInfo = this.checkFileExsit(sbookorgcode, cols[8], f.getName(), MsgConstant.MSG_NO_1106);
					if(null != errorInfo && errorInfo.length() > 0) {
						mulitDto.getErrorList().add(errorInfo);
						return mulitDto;
					}
				}
				
				recordNum++;
				TbsTvFreeDto freedto = new TbsTvFreeDto();
				
				//freedto.setIvousrlno(Long.parseLong(SequenceGenerator.getNextByDb2(SequenceName.TAXFREE_SEQ,SequenceName.TRAID_SEQ_CACHE,SequenceName.TRAID_SEQ_STARTWITH)));
				
				String taxorgcode = cols[0];//���ջ��ش���
				TsConverttaxorgDto convertTaxOrgDto = new TsConverttaxorgDto();
				// �����������
				convertTaxOrgDto.setSorgcode(sbookorgcode);
				// �����������
				convertTaxOrgDto.setStrecode(cols[8]);
				// TBS���ջ��ش���
				convertTaxOrgDto.setStbstaxorgcode(cols[0]);
				List dtoL = CommonFacade.getODB().findRsByDtoWithUR(
						convertTaxOrgDto);
				String strTipsTaxOrg = "";
				if (dtoL == null || dtoL.size() == 0) {
					mulitDto.getErrorList().add(
							"��ֵ��ļ�[" + f.getName() + "]�й���������룺" + cols[8]
									+ ",���ջ��ش��룺" + cols[0]
									+ " û��ά��'���ջ��ض���'!");
				} else {
					TsConverttaxorgDto taxogdto = (TsConverttaxorgDto) dtoL
							.get(0);
					// Tips���ջ��ش���
					strTipsTaxOrg = taxogdto.getStcbstaxorgcode();
					// ���ջ��ش���
					freedto.setStaxorgcode(strTipsTaxOrg);
				}
				
				taxcodes.add(cols[0].trim());
				
				//��ֵ�����ƾ֤����Ҫ���н�ȡ��8λ
				if(null != cols[3] && !"".equals(cols[3]) && cols[3].length()>8) {
					freedto.setStrano(cols[3].substring(cols[3].length()-8));
				}else {
					freedto.setStrano(cols[3]);
				}
				
				try{
					freedto.setDbilldate(new java.sql.Date(TimeFacade.parseDate(cols[1], "yyyyMMdd").getTime()));
				}catch(Exception e){
					mulitDto.getErrorList().add("��ֵ��ļ�["+f.getName()+"] ƾ֤���Ϊ["+cols[2].trim()+"]�ļ�¼�п�Ʊ���ڸ�ʽ�Ƿ�");
				}
				freedto.setSfreevouno(cols[2]);
				voulist.add(cols[2]);
				
				freedto.setCfreeplutype(cols[4]);//��ֵ���Ԥ������
				String psubject = cols[5];
				if(!bmap.containsKey(psubject)){
					mulitDto.getErrorList().add("��ֵ��ļ�["+f.getName()+"] ƾ֤���Ϊ["+cols[2].trim()+"]�ļ�¼����ֵ���Ԥ���Ŀ����"+psubject+"δά��");;
				}
				freedto.setSfreeplusubjectcode(psubject);
				freedto.setCfreeplulevel(cols[6]);
				
				if (null != cols[7] && !"".equals(cols[7])) { 
					if (!viceMap.containsKey(cols[8] + cols[5] + cols[7])) {
						if (!viceMap.containsKey(cols[5] + cols[7])) {
							if (!viceMap.containsKey(cols[8] + cols[7])) {
								if (!viceMap.containsKey(cols[7])) {
									mulitDto.getErrorList().add(
											"��ֵ��ļ�[" + f.getName() + "] ƾ֤���Ϊ["+cols[2].trim()+"]����ֵ���������־�� "+ cols[7]+ " û����'������־����ά��'������ά��!");
								}
							}
						}
					}
				}
				freedto.setSfreeplusign(cols[7]); // ��ֵ���������־
				String trecode = cols[8];
				if(!this.checkTreasury(trecode, sbookorgcode)) {
					mulitDto.getErrorList().add("��ֵ��ļ�["+f.getName()+"] ƾ֤���Ϊ["+cols[2].trim()+"]�ļ�¼����ֵ����տ������룺"+trecode+" û����'����������Ϣ����'�в��ҵ�!");
				}
				freedto.setSfreepluptrecode(trecode);
				try{
					BigDecimal pamt = new BigDecimal(cols[9]);
					freedto.setFfreepluamt(pamt);
					famt.add(pamt);
				}catch(Exception e){
					mulitDto.getErrorList().add("��ֵ��ļ�["+f.getName()+"] ƾ֤���Ϊ["+cols[2].trim()+"]�ļ�¼����ֵ������׽��Ƿ�");
				}
				freedto.setCfreemikind(cols[10]);
				String msubject = cols[11];
				if(!bmap.containsKey(msubject)){
					mulitDto.getErrorList().add("��ֵ��ļ�["+f.getName()+"] ƾ֤���Ϊ["+cols[2].trim()+"]�ļ�¼����ֵ���Ԥ���Ŀ����"+msubject+"δά��");;
				}
				freedto.setSfreemisubject(msubject);
				freedto.setCfreemilevel(cols[12]);
				
				if (null != cols[13] && !"".equals(cols[13])) { 
					if (!viceMap.containsKey(cols[14] + cols[11] + cols[13])) {
						if (!viceMap.containsKey(cols[11] + cols[13])) {
							if (!viceMap.containsKey(cols[14] + cols[13])) {
								if (!viceMap.containsKey(cols[13])) {
									mulitDto.getErrorList().add(
											"��ֵ��ļ�[" + f.getName() + "] ƾ֤���Ϊ["+cols[2].trim()+"]����ֵ���������־�� "+ cols[7]+ " û����'������־����ά��'������ά��!");
								}
							}
						}
					}
				}
				freedto.setSfreemisign(cols[13]); // ��ֵ���������־
				String mtrecode = cols[14];
				if(!this.checkTreasury(mtrecode, sbookorgcode)) {
					mulitDto.getErrorList().add("��ֵ��ļ�["+f.getName()+"]ƾ֤���Ϊ["+cols[2].trim()+"]�ļ�¼����ֵ����տ������룺"+mtrecode+" û����'����������Ϣ����'�в��ҵ�!");
				}
				freedto.setSfreemiptre(mtrecode);
				try{
					BigDecimal mamt = new BigDecimal(cols[15]);
					freedto.setFfreemiamt(mamt);
					famt.add(mamt);
				}catch(Exception e){
					mulitDto.getErrorList().add("��ֵ��ļ�["+f.getName()+"] ƾ֤���Ϊ["+cols[2].trim()+"]�ļ�¼����ֵ������׽��Ƿ�");
				}
				freedto.setScorpcode(cols[17]);
				freedto.setSstatus(StateConstant.CONFIRMSTATE_NO);
				freedto.setCtrimflag(cols[16]);
				freedto.setSbiztype(biztype);
				freedto.setSfilename(f.getName());
				freedto.setSbookorgcode(sbookorgcode);
				freedto.setDacceptdate(TimeFacade.getCurrentDateTime());
				freedto.setTssysupdate(TSystemFacade.getDBSystemTime());
				List<TbsTvFreeDto> pkg = pkgs.get(taxorgcode);
				if(null == pkg || pkg.size() == 0){
					pkg = new ArrayList<TbsTvFreeDto>();
					pkg.add(freedto);
					pkgs.put(taxorgcode, pkg);
				}else {
					pkg.add(freedto);
				}
				
			}
			
			for(String taxcode : taxcodes){
				List<TbsTvFreeDto> pkg = pkgs.get(taxcode);
				int li_Detail = (pkg.size()-1) / 1000;
				for (int k = 0; k <= li_Detail; k++) {
					int li_TempCount = 0;
					if (li_Detail == k) {
						li_TempCount = pkg.size();
					} else {
						li_TempCount = (k + 1) * 1000;
					}
					String pkgno = SequenceGenerator.changePackNoForLocal(SequenceGenerator
							.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
									SequenceName.TRAID_SEQ_CACHE,
									SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
					BigDecimal pkgFamt = new BigDecimal("0.00");
					String trecode =  pkg.get(0).getSfreepluptrecode();
					
					int count = 0;
					for (int j = k * 1000; j < li_TempCount; j++) {
						TbsTvFreeDto bkdto = (TbsTvFreeDto) pkg.get(j);
						//���ð���ˮ��
						bkdto.setSpackno(pkgno);
						//����������ͳ��
						count ++;
						//ͳ�ƽ��
						pkgFamt = pkgFamt.add(bkdto.getFfreemiamt()).add(bkdto.getFfreepluamt());
						//���������������ɵ�DTO����
						freedtos.add(bkdto);
					}
					
					TvFilepackagerefDto packdto = new TvFilepackagerefDto();
					packdto.setSorgcode(sbookorgcode);
					packdto.setStrecode(trecode);
					packdto.setSfilename(f.getName());
					packdto.setStaxorgcode(taxcode);
					packdto.setScommitdate(TimeFacade.getCurrentStringTime());
					packdto.setSaccdate(TimeFacade.getCurrentStringTime());
					packdto.setSpackageno(pkgno);
					packdto.setSoperationtypecode(biztype);
					packdto.setIcount(count);
					packdto.setNmoney(pkgFamt);
					packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_WAIT_DEALING);
					packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
					packdto.setSusercode(userid);
					packdto.setImodicount(0);
					packdto.setSchkstate(StateConstant.CONFIRMSTATE_NO);
					packdtos.add(packdto);
				}
			}
			
			int oldSize = 0;
			int newSize = 0;
			Set<String> sets = new HashSet<String>();
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					mulitDto.getErrorList().add("��ֵ��ļ�["+f.getName()+"] �д���ƾ֤��� ["+item.trim()+"] �ظ�");
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("��ֵ��ļ���������\n"+e.getMessage(), e);
		}

		
		mulitDto.setFatherDtos(freedtos);
		mulitDto.setPackDtos(packdtos);
		mulitDto.setVoulist(voulist);
		mulitDto.setFamt(famt);
		mulitDto.setTotalCount(recordNum); 
		
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
