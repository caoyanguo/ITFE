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
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.PiLiangDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TvBatchpayDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.persistence.dto.TvPayoutfinanceDto;
import com.cfcc.itfe.util.ChinaTest;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua ���������ļ����������
 */
@SuppressWarnings("unchecked")
public class QuantityTipsFileOper extends AbstractTipsFileOper {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {
		if("000077100005".equals(ITFECommonConstant.SRC_NODE))
			return fileParserGx(file,bookorgCode,userid,biztype,filekind,paramdto,dmap);
		else
			return fileParserOther(file,bookorgCode,userid,biztype,filekind,paramdto,dmap);
	}
	public MulitTableDto fileParserOther(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {
		PiLiangDto pldto = (PiLiangDto)paramdto;
		MulitTableDto multi = new MulitTableDto();
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal famtPack = new BigDecimal("0.00");
		
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		File fi = new File(file);
		String finame =  fi.getName().replaceAll(".tmp", ".pas") ;
		int fbcount = 0; // �ְ�������
		int recordNum = 1; // ��¼��¼��
		String tmpPackNo = "";
//		String srcvreno = fi.getName().substring(0, 12);
		try {
			List<String[]> fileContent = super.readFile(file, ",");
			String[] castr = fileContent.get(fileContent.size()-1);
			if("</CA>".equals(castr[0].toUpperCase())) {
				//�������������ļ��й���CA�Ĳ���remove��
				fileContent.remove(fileContent.size()-1);
				fileContent.remove(fileContent.size()-1);
				fileContent.remove(fileContent.size()-1);
			}
			
			/*
			 * ��ʼ�ְ� ����ʼ����������
			 */
			Map<String, Map<String,List<String[]>>> splitAllMap = new HashMap<String, Map<String,List<String[]>>>(); //����Ϊ��<�������˺�,Map<������,��Ӧ��¼>
			Set<String> recvSet = new HashSet<String>(); //��Ž������к�
			Set<String> paySet = new HashSet<String>(); //��Ÿ������˺�
			/*1.����ȡ�ø������˺ż��������к�����*/
			for(String[] strBuff : fileContent) {
				paySet.add(strBuff[4].trim()); //�������˺�
				recvSet.add(strBuff[1].trim()); //�������к�
			}
			/*2.Ȼ���Ȱ��ո������˺ţ��������кŷֿ�*/
			for(String payAcct : paySet) {
				Map<String,List<String[]>> recvSplitMap = new HashMap<String, List<String[]>>(); //��Ž����м���Ӧ��¼��ϵ
				for(String rbank : recvSet) {
					List<String[]> tempList = new ArrayList<String[]>(); //ͬ�����ˡ������м�¼
					for(String[] dtoStr : fileContent) {
						if(payAcct.trim().equals(dtoStr[4].trim()) && rbank.trim().equals(dtoStr[1].trim())) {
							tempList.add(dtoStr);
						}
					}
					recvSplitMap.put(rbank, tempList);
				}
				splitAllMap.put(payAcct, recvSplitMap);
			}
			/*3.�����Ǿ���ķְ����̣�����equals���������м�¼���߼��Ϸָ��Ȼ��ֱ�������Ӧ����ˮ��*/
			for(String payAt : paySet) {
				Map<String,List<String[]>> recvSplitMap = splitAllMap.get(payAt); //�õ�ѭ���еĸ����˶�Ӧ��¼
				for(String rebank : recvSet) {
					List<String[]> list = recvSplitMap.get(rebank);
					if(list != null && list.size() > 0) {
						int li_Detail = (list.size()-1) / StateConstant.QUANTITY_PACKAGE_COUNT;
						for (int k = 0; k <= li_Detail; k++) {
							int li_TempCount = 0;
							if (li_Detail == k) {
								li_TempCount = list.size();
							} else {
								li_TempCount = (k + 1) * StateConstant.QUANTITY_PACKAGE_COUNT;
							}
							tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
									.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
											SequenceName.TRAID_SEQ_CACHE,
											SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
							for (int j = k * StateConstant.QUANTITY_PACKAGE_COUNT; j < li_TempCount; j++) {
								String[] strs = (String[]) list.get(j);
								if("<CA>".equals(strs[0].toUpperCase())) {
									break ;
								}
								TvPayoutfinanceDto dto = new TvPayoutfinanceDto();
								dto.setStrecode(pldto.getTrecode());//�������
								dto.setSfuncsbtcode(pldto.getFunccode()); //�������Ŀ����
								dto.setSbudgettype(pldto.getBudgetype()); //Ԥ������

								dto.setSsndbnkno(strs[0]);   //�������к�
								dto.setSrcvbnkno(strs[1]);   //�������к�

								/*5.3�޸�Ϊ����������к�
								 * dto.setSrcvreckbnkno(srcvreno);
								 */
								dto.setSrcvreckbnkno(strs[1].trim());
								
								dto.setFamt(new BigDecimal(strs[2]));  //���
								famt = famt.add(new BigDecimal(strs[2]));
								famtPack = famtPack.add(new BigDecimal(strs[2]));
								dto.setSpayeropnbnkno(strs[3]);    //�����˿������к�
								if(strs[4].length() < 16) {
									dto.setSpayeracct(bookorgCode+strs[4]);   //�������˺�(С��16λ��ǰ��Ӻ�������)
								}else {
									dto.setSpayeracct(strs[4]);//�������˺�
								}					
								dto.setSpayername(strs[5]);   //����������
								dto.setSpayeraddr(strs[6]);   //�����˵�ַ
								dto.setSpayeeopnbnkno(strs[7]);   //�տ��˿������к�
								if(ChinaTest.isChinese(strs[8])) {
									throw new ITFEBizException("���������ļ�["+finame+"] ���տ����˺�'"+strs[8]+"' ���������ַ������֤!");
								}
								dto.setSpayeeacct(strs[8]);     //�տ����˺�	
								String errChi_9 = VerifyParamTrans.verifyNotUsableChinese(strs[9]);
								if(null != errChi_9 && !"".equals(errChi_9)) {
									throw new ITFEBizException("���������ļ�["+finame+"] ���տ������ƴ��ڷǷ��ַ���"+errChi_9);
								}
								dto.setSpayeename(strs[9]);		//�տ�������
								dto.setSpayeeaddr(strs[10]);	//�տ��˵�ַ
								dto.setSpaybizkind(strs[11]);   //֧��ҵ������
								dto.setSbiztype(strs[12]);   //ҵ�����ͺ�
								if(null != strs[13] && !"".equals(strs[13])) {
									String errChi_13 = VerifyParamTrans.verifyNotUsableChinese(strs[13]);
									if(null != errChi_13 && !"".equals(errChi_13)) {
										throw new ITFEBizException("���������ļ�["+finame+"] �и��Դ��ڷǷ��ַ���"+errChi_13);
									}
								}
								if(pldto.getType() == null||"".equals(pldto.getType())){
									dto.setSaddword(strs[13]);      //����
								}else{
									dto.setSaddword("��");      //����(ɽ����������ר��)
								}
								dto.setDentrust(TimeFacade.getCurrentDateTime()); //ί�����ڲ���Ϊ��
								dto.setDaccept(TimeFacade.getCurrentDateTime()); //��������
								if(pldto.getSbdgorgcode()!=null&&!pldto.getSbdgorgcode().equals("")){
									dto.setSbdgorgcode(pldto.getSbdgorgcode());
								}else{
									if(strs[4].length() > 15) {
										dto.setSbdgorgcode(strs[4].substring(11));//Ԥ�㵥λ���벻��Ϊ��(��丶�����˺�)
									}else {
										dto.setSbdgorgcode(strs[4]);//Ԥ�㵥λ���벻��Ϊ��(��丶�����˺�)
									}
								}
								dto.setSpackageno(tmpPackNo);  //����ˮ��
								dto.setSbookorgcode(bookorgCode); //�����������
								dto.setSfilename(finame); //�����ļ���
								dto.setSstatus(StateConstant.CONFIRMSTATE_NO);  //����״̬
								fbcount ++;
								fatherdtos.add(dto);
							}
							TvFilepackagerefDto packdto = new TvFilepackagerefDto();
							packdto.setSorgcode(bookorgCode);
							packdto.setStrecode(pldto.getTrecode()); // �����������
							packdto.setSfilename(finame);
							TsConvertfinorgDto condto = new TsConvertfinorgDto();
							condto.setSorgcode(bookorgCode);
							condto.setStrecode(pldto.getTrecode());
							List conlist = CommonFacade.getODB().findRsByDtoWithUR(condto);
							if(conlist == null || conlist.size() == 0 ){
								throw new ITFEBizException("���������ļ�["+finame+"] �и��ݺ���������룺"+bookorgCode+"�� ����������룺"+pldto.getTrecode()+" û���� '����������Ϣά������'���ҵ���Ӧ�����������룬���֤");
							}
							TsConvertfinorgDto gd = (TsConvertfinorgDto)conlist.get(0);
							packdto.setStaxorgcode(gd.getSfinorgcode());
							packdto.setScommitdate(TimeFacade.getCurrentStringTime());
							packdto.setSaccdate(TimeFacade.getCurrentStringTime());
							packdto.setSpackageno(tmpPackNo);
							packdto.setSoperationtypecode(biztype);
							packdto.setIcount(fbcount);
							packdto.setNmoney(famtPack);
							packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND);
							packdto.setSusercode(userid);
							packdto.setImodicount(0);
							fbcount = 0;
							famtPack = new BigDecimal("0.00");
							packdtos.add(packdto);
						}
					
					}					
				}
			}
			if(pldto.getType()!=null&&"ftppljz".equals(pldto.getType()))
			{
				TvBatchpayDto dto = pldto.getIdtoMap().get(fi.getName());
				if(dto!=null)
				{
					dto.setSstatus(StateConstant.FTPFILESTATE_ADDLOAD);
					multi.addUpdateIdtoList(dto);
				}
			}
			multi.setFatherDtos(fatherdtos);
			multi.setPackDtos(packdtos);
			multi.setFamt(famt); //�ܽ��
			multi.setTotalCount(recordNum); //�ܱ���
			return multi;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("���������ļ��������� \n" +e.getMessage(), e);
		}
	}
	public MulitTableDto fileParserGx(String file, String bookorgCode,
			String userid,  String biztype,
			 String filekind ,IDto paramdto,Map<String,TsBudgetsubjectDto> dmap)
			throws ITFEBizException {
		PiLiangDto pldto = (PiLiangDto)paramdto;
		MulitTableDto multi = new MulitTableDto();
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal famtPack = new BigDecimal("0.00");
		
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		File fi = new File(file);
		String finame =  fi.getName().replaceAll(".tmp", ".pas") ;
		int fbcount = 0; // �ְ�������
		int recordNum = 1; // ��¼��¼��
		String tmpPackNo = "";
		try {
			List<String[]> fileContent = super.readFile(file, ",");
			String[] castr = fileContent.get(fileContent.size()-1);
			if("</CA>".equals(castr[0].toUpperCase())) {
				//�������������ļ��й���CA�Ĳ���remove��
				fileContent.remove(fileContent.size()-1);
				fileContent.remove(fileContent.size()-1);
				fileContent.remove(fileContent.size()-1);
			}
			
			/*
			 * ��ʼ�ְ� ����ʼ����������
			 */
			Map<String, Map<String,Map<String,List<String[]>>>> splitAllMap = new HashMap<String, Map<String,Map<String,List<String[]>>>>(); //����Ϊ��<�������˺�,Map<������,Map<����,��Ӧ��¼>>>
			Set<String> recvSet = new HashSet<String>(); //��Ž������к�
			Set<String> paySet = new HashSet<String>(); //��Ÿ������˺�
			Set<String> addwordSet = new HashSet<String>();//���������������Էְ�
			/*1.����ȡ�ø������˺ż��������к�����*/
			for(String[] strBuff : fileContent) {
				paySet.add(strBuff[4].trim()); //�������˺�
				recvSet.add(strBuff[1].trim()); //�������к�
				addwordSet.add(strBuff[13]==null?"--@#@--":strBuff[13].trim());//����
			}
			/*2.Ȼ���Ȱ��ո������˺ţ��������к�,���Էֿ�*/
			for(String payAcct : paySet) 
			{
				Map<String,Map<String,List<String[]>>> recvSplitMap = new HashMap<String,Map<String,List<String[]>>>();//��Ž����м����Զ�Ӧ��¼��ϵ
				for(String rbank : recvSet)
				{
					Map<String,List<String[]>> addwordMap = new HashMap<String, List<String[]>>(); //��Ÿ��Լ���Ӧ��¼��ϵ
					for(String addword:addwordSet)
					{
						List<String[]> tempList = new ArrayList<String[]>(); //ͬ�����ˡ������С����Լ�¼
						for(String[] dtoStr : fileContent) {
							if(payAcct.trim().equals(dtoStr[4].trim()) && rbank.trim().equals(dtoStr[1].trim())&&addword.equals(dtoStr[13]==null?"--@#@--":dtoStr[13].trim())) {
								tempList.add(dtoStr);
							}
						}
						addwordMap.put(addword, tempList);
					}
					recvSplitMap.put(rbank, addwordMap);
				}
				splitAllMap.put(payAcct, recvSplitMap);
			}
			/*3.�����Ǿ���ķְ����̣�����equals���������м�¼���߼��Ϸָ��Ȼ��ֱ�������Ӧ����ˮ��*/
			for(String payAt : paySet) {
				Map<String,Map<String,List<String[]>>> recvSplitMap = splitAllMap.get(payAt); //�õ�ѭ���еĸ����˶�Ӧ��¼
				for(String rebank : recvSet) {
					Map<String,List<String[]>> addwordSplitMap = recvSplitMap.get(rebank);//�õ����Զ�Ӧ��¼
					for(String addword:addwordSet)
					{
						List<String[]> list = addwordSplitMap.get(addword);
						if(list != null && list.size() > 0) {
							int li_Detail = (list.size()-1) / StateConstant.QUANTITY_PACKAGE_COUNT;
							for (int k = 0; k <= li_Detail; k++) {
								int li_TempCount = 0;
								if (li_Detail == k) {
									li_TempCount = list.size();
								} else {
									li_TempCount = (k + 1) * StateConstant.QUANTITY_PACKAGE_COUNT;
								}
								tmpPackNo = SequenceGenerator.changePackNoForLocal(SequenceGenerator
										.getNextByDb2(SequenceName.FILENAME_PACKNO_REF_SEQ,
												SequenceName.TRAID_SEQ_CACHE,
												SequenceName.TRAID_SEQ_STARTWITH,MsgConstant.SEQUENCE_MAX_DEF_VALUE));
								for (int j = k * StateConstant.QUANTITY_PACKAGE_COUNT; j < li_TempCount; j++) {
									String[] strs = (String[]) list.get(j);
									if("<CA>".equals(strs[0].toUpperCase())) {
										break ;
									}
									TvPayoutfinanceDto dto = new TvPayoutfinanceDto();
									dto.setStrecode(pldto.getTrecode());//�������
									dto.setSfuncsbtcode(pldto.getFunccode()); //�������Ŀ����
									dto.setSbudgettype(pldto.getBudgetype()); //Ԥ������
	
									dto.setSsndbnkno(strs[0]);   //�������к�
									dto.setSrcvbnkno(strs[1]);   //�������к�
	
									/*5.3�޸�Ϊ����������к�
									 * dto.setSrcvreckbnkno(srcvreno);
									 */
									dto.setSrcvreckbnkno(strs[1].trim());
									
									dto.setFamt(new BigDecimal(strs[2]));  //���
									famt = famt.add(new BigDecimal(strs[2]));
									famtPack = famtPack.add(new BigDecimal(strs[2]));
									dto.setSpayeropnbnkno(strs[3]);    //�����˿������к�
									if(strs[4].length() < 16) {
										dto.setSpayeracct(bookorgCode+strs[4]);   //�������˺�(С��16λ��ǰ��Ӻ�������)
									}else {
										dto.setSpayeracct(strs[4]);//�������˺�
									}					
									dto.setSpayername(strs[5]);   //����������
									dto.setSpayeraddr(strs[6]);   //�����˵�ַ
									dto.setSpayeeopnbnkno(strs[7]);   //�տ��˿������к�
									if(ChinaTest.isChinese(strs[8])) {
										throw new ITFEBizException("���������ļ�["+finame+"] ���տ����˺�'"+strs[8]+"' ���������ַ������֤!");
									}
									dto.setSpayeeacct(strs[8]);     //�տ����˺�	
									String errChi_9 = VerifyParamTrans.verifyNotUsableChinese(strs[9]);
									if(null != errChi_9 && !"".equals(errChi_9)) {
										throw new ITFEBizException("���������ļ�["+finame+"] ���տ������ƴ��ڷǷ��ַ���"+errChi_9);
									}
									dto.setSpayeename(strs[9]);		//�տ�������
									dto.setSpayeeaddr(strs[10]);	//�տ��˵�ַ
									dto.setSpaybizkind(strs[11]);   //֧��ҵ������
									dto.setSbiztype(strs[12]);   //ҵ�����ͺ�
									if(null != strs[13] && !"".equals(strs[13])) {
										String errChi_13 = VerifyParamTrans.verifyNotUsableChinese(strs[13]);
										if(null != errChi_13 && !"".equals(errChi_13)) {
											throw new ITFEBizException("���������ļ�["+finame+"] �и��Դ��ڷǷ��ַ���"+errChi_13);
										}
									}
									if(pldto.getType() == null||"".equals(pldto.getType())){
										dto.setSaddword(strs[13]);      //����
									}else{
										dto.setSaddword("��");      //����(ɽ����������ר��)
									}
									dto.setDentrust(TimeFacade.getCurrentDateTime()); //ί�����ڲ���Ϊ��
									dto.setDaccept(TimeFacade.getCurrentDateTime()); //��������
									if(pldto.getSbdgorgcode()!=null&&!pldto.getSbdgorgcode().equals("")){
										dto.setSbdgorgcode(pldto.getSbdgorgcode());
									}else{
										if(strs[4].length() > 15) {
											dto.setSbdgorgcode(strs[4].substring(11));//Ԥ�㵥λ���벻��Ϊ��(��丶�����˺�)
										}else {
											dto.setSbdgorgcode(strs[4]);//Ԥ�㵥λ���벻��Ϊ��(��丶�����˺�)
										}
									}
									dto.setSpackageno(tmpPackNo);  //����ˮ��
									dto.setSbookorgcode(bookorgCode); //�����������
									dto.setSfilename(finame); //�����ļ���
									dto.setSstatus(StateConstant.CONFIRMSTATE_NO);  //����״̬
									fbcount ++;
									fatherdtos.add(dto);
								}
								TvFilepackagerefDto packdto = new TvFilepackagerefDto();
								packdto.setSorgcode(bookorgCode);
								packdto.setStrecode(pldto.getTrecode()); // �����������
								packdto.setSfilename(finame);
								TsConvertfinorgDto condto = new TsConvertfinorgDto();
								condto.setSorgcode(bookorgCode);
								condto.setStrecode(pldto.getTrecode());
								List conlist = CommonFacade.getODB().findRsByDtoWithUR(condto);
								if(conlist == null || conlist.size() == 0 ){
									throw new ITFEBizException("���������ļ�["+finame+"] �и��ݺ���������룺"+bookorgCode+"�� ����������룺"+pldto.getTrecode()+" û���� '����������Ϣά������'���ҵ���Ӧ�����������룬���֤");
								}
								TsConvertfinorgDto gd = (TsConvertfinorgDto)conlist.get(0);
								packdto.setStaxorgcode(gd.getSfinorgcode());
								packdto.setScommitdate(TimeFacade.getCurrentStringTime());
								packdto.setSaccdate(TimeFacade.getCurrentStringTime());
								packdto.setSpackageno(tmpPackNo);
								packdto.setSoperationtypecode(biztype);
								packdto.setIcount(fbcount);
								packdto.setNmoney(famtPack);
								packdto.setSretcode(DealCodeConstants.DEALCODE_ITFE_NO_SEND);
								packdto.setSusercode(userid);
								packdto.setImodicount(0);
								fbcount = 0;
								famtPack = new BigDecimal("0.00");
								packdtos.add(packdto);
							}
						}
					}					
				}
			}
			if(pldto.getType()!=null&&"ftppljz".equals(pldto.getType()))
			{
				TvBatchpayDto dto = pldto.getIdtoMap().get(fi.getName());
				if(dto!=null)
				{
					dto.setSstatus(StateConstant.FTPFILESTATE_ADDLOAD);
					multi.addUpdateIdtoList(dto);
				}
			}
			multi.setFatherDtos(fatherdtos);
			multi.setPackDtos(packdtos);
			multi.setFamt(famt); //�ܽ��
			multi.setTotalCount(recordNum); //�ܱ���
			return multi;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("���������ļ��������� \n" +e.getMessage(), e);
		}
	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		return null;
	}
}
