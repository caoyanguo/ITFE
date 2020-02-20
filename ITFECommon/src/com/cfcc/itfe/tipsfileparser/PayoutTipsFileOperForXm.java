package com.cfcc.itfe.tipsfileparser;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua ʵ���ʽ�
 */
public class PayoutTipsFileOperForXm extends AbstractTipsFileOper {

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
			int fbcount = 0; // �ְ�������
			int recordNum = 1; // ��¼��¼��
			
			String packno = ""; 
			String tmpPackNo = "";
			String trecode = "";
			
			String fname = new File(file).getName();
			String ctrimflag = fname.substring(fname.length()-5,fname.length()-4);  //�����ڱ�־
			
			MulitTableDto multi = new MulitTableDto();
			multi.setBiztype(biztype);//ҵ������
			multi.setSbookorgcode(bookorgCode);//�����������
			BigDecimal famt = new BigDecimal("0.00");
			BigDecimal famtPack = new BigDecimal("0.00");
			
			List<IDto> listdto = new ArrayList<IDto>();
			List<IDto> packdtos = new ArrayList<IDto>();
			List<IDto> famtdtos = new ArrayList<IDto>();
			
			HashMap<String, String> map = new HashMap<String, String>();
			File fi = new File(file);
			
			List<String> voulist = new ArrayList<String>();
			Set<String> bnkset = new HashSet<String>();
			
			List<String[]> fileContent = super.readFile(file, ",");
			//�ļ�����
			if(file.endsWith("txt")) {  //��ϸ�ڽӿ�
				Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgCode); //���˴��뻺��
				for (String[] singDto : fileContent) {
					TbsTvPayoutDto payoutDto = new TbsTvPayoutDto();
					payoutDto.setStrecode(singDto[0]); // �������
					trecode = singDto[0];
					if(!this.checkTreasury(trecode, bookorgCode)) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[11].trim()+"]�ļ�¼�й���������룺"+trecode+" û����'����������Ϣ����'�в��ҵ�!");
					}
					payoutDto.setSpayeracct(singDto[1]); // �������˺�
					payoutDto.setSmovefundreason(singDto[2]); // ֧��ԭ�������ߵ���ԭ����� (����Ϊ��)
					//Ԥ�㵥λ����
					if(singDto[3]!=null && singDto[3].length() > 15) {
						payoutDto.setSbdgorgcode(singDto[3].substring(0, 15));//�տλ����
					}else {
						payoutDto.setSbdgorgcode(singDto[3]);//�տλ����
					}
					//Ԥ�㵥λ����У��
					if(!rpmap.containsKey(singDto[0]+payoutDto.getSbdgorgcode().trim())) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[11].trim()+"]�ļ�¼��Ԥ�㵥λ���� '"+payoutDto.getSbdgorgcode()+"' û���ڷ��˴�������ҵ�!");
					}else {
						if(!"1".equals(rpmap.get(singDto[0]+payoutDto.getSbdgorgcode()).getCmayaprtfund().trim())) {
							multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[11].trim()+"]�ļ�¼��Ԥ�㵥λ���� '"+payoutDto.getSbdgorgcode()+"' ��ӦԤ�㵥λ���ܽ���ʵ���ʽ�!");
						}
					}
//					payoutDto.setSbdgorgcode(singDto[3]); // Ԥ�㵥λ����
					payoutDto.setSpayeeacct(singDto[4]); // �տλ�˺�(����Ϊ��)
					payoutDto.setCbdgkind(singDto[5]); // Ԥ������
					payoutDto.setSfuncsbtcode(singDto[6]); // ���ܿ�Ŀ����
					payoutDto.setSecosbtcode(singDto[7]); // ���ÿ�Ŀ����
					payoutDto.setSbooksbtcode(singDto[8]); // ��ƿ�Ŀ���� (��Ϊ��)
					payoutDto.setSpaybizkind(singDto[9]); // ֧������
					payoutDto.setFamt(new BigDecimal(singDto[10])); // ������ ��ֵת������
					famt = famt.add(new BigDecimal(singDto[10]));	
					payoutDto.setSvouno(singDto[11]); // ƾ֤���
					if(VerifyParamTrans.isNull(singDto[11])){//ƾ֤��Ų���Ϊ��
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤��Ų���Ϊ��!");
					}
					voulist.add(trecode.trim()+","+singDto[11].trim());
					
					//ͨ���������͸������˺� �ҵ���Ӧ����������
					TsInfoconnorgaccDto accdto = new TsInfoconnorgaccDto();
					accdto.setSorgcode(bookorgCode);
					accdto.setStrecode(trecode);
					accdto.setSpayeraccount(singDto[1].trim());
					IDto ito = DatabaseFacade.getODB().find(accdto);
					if(ito == null) {
						multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"] ƾ֤���Ϊ["+singDto[11].trim()+"]�ļ�¼���ݹ�����룺"+trecode+" , �������˺ţ�"+singDto[1].trim()+" û����'�������˻���Ϣά������'���ҵ���Ӧ���������ƣ�����ά��");
					}else {
						TsInfoconnorgaccDto tndto = (TsInfoconnorgaccDto)ito;
						payoutDto.setSpayername(tndto.getSpayername());
					}
					payoutDto.setSbookorgcode(bookorgCode);// �����������
					payoutDto.setDaccept(TimeFacade.getCurrentDateTime());//��������
					payoutDto.setIofyear(Integer.parseInt(fi.getName().substring(0, 4))); //�������
					payoutDto.setDvoucher(CommonUtil.strToDate(fi.getName().substring(0, 8))); //ƾ֤����
					
					if(biztype != null && !"".equals(biztype)) {
						payoutDto.setSbiztype(biztype);// ҵ������ ����Ϊ��
					}else {
						payoutDto.setSbiztype("");// ҵ������
					}
					payoutDto.setCtrimflag(ctrimflag); //�����ڱ�־����Ϊ��
					
					payoutDto.setSpackageno(tmpPackNo);  //����ˮ��
					payoutDto.setSfilename(fi.getName());
					payoutDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
					fbcount++;
					recordNum++;
					listdto.add(payoutDto);
				}
			}else if(file.endsWith("tmp")){ //�ʽ�ڽӿ�
				String srcvreno = fi.getName().substring(0, 12);
				for (int i = 0; i < fileContent.size(); i++) {
					TvPayoutfinanceDto dto = new TvPayoutfinanceDto();
					dto.setSrcvreckbnkno(srcvreno);
					String[] strs = fileContent.get(i);
					if("<CA>".equals(strs[0].toUpperCase())) {
						break ;
					}
					dto.setSsndbnkno(strs[0]);   //�������к�
					dto.setSrcvbnkno(strs[1]);   //�������к�
					dto.setFamt(new BigDecimal(strs[2]));  //���
					famt = famt.add(new BigDecimal(strs[2]));
					famtPack = famtPack.add(new BigDecimal(strs[2]));
					dto.setSrcvreckbnkno(srcvreno); //������
					dto.setSpayeropnbnkno(strs[3]);    //�����˿������к�
					dto.setSpayeracct(strs[4]);   //�������˺�
					dto.setSpayername(strs[5]);   //����������
					dto.setSpayeraddr(strs[6]);   //�����˵�ַ
					dto.setSpayeeopnbnkno(strs[7]);   //�տ��˿������к�
					dto.setSpayeeacct(strs[8]);     //�տ����˺�	
					dto.setSpayeename(strs[9]);		//�տ�������
					dto.setSpayeeaddr(strs[10]);	//�տ��˵�ַ
					dto.setSpaybizkind(strs[11]);   //֧��ҵ������
					dto.setSbiztype(strs[12]);   //ҵ�����ͺ�
					dto.setSaddword(strs[13]);      //����
					
					famtdtos.add(dto);
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
					multi.getErrorList().add("ʵ���ʽ��ļ�["+fi.getName()+"]�д���ƾ֤����ظ�");
				}
			}
			
			multi.setFatherDtos(listdto);
			multi.setSonDtos(famtdtos);
			multi.setPackDtos(packdtos);
			multi.setVoulist(voulist);
			multi.setFamt(famt);//�ܽ��
			multi.setTotalCount(recordNum); //�ܱ���
			return multi;
			
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("ʵ���ļ��������� \n"+e.getMessage(), e);
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
