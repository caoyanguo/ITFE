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

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.BizTypeConstant;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.CommonParamDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpayMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvBnkpaySubDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanMainDto;
import com.cfcc.itfe.persistence.dto.TbsTvDirectpayplanSubDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsPayacctinfoDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author zhang ���а���֧�����������ļ����������
 */
public class ApplypayTipsFileOper extends AbstractTipsFileOper {

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {
		int recordNum = 0; // ��¼��¼��
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// ҵ������
		mulitDto.setSbookorgcode(sbookorgcode);// �����������
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal sonFamt = new BigDecimal("0.00");
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> sondtos = new ArrayList<IDto>();
		List<IDto> sondtos_tmp = new ArrayList<IDto>();
		List<IDto> packdtos = new ArrayList<IDto>();
		List<String> voulist = new ArrayList<String>(); // ƾ֤��ż�
		File fi = new File(file);
		Map<String, BigDecimal> diremap = new HashMap<String, BigDecimal>();
		String strecode = "";
		String mainvou = "";
		String mainvou_tmp = "";
		String svo = "";
		try {
			Map<String, TdCorpDto> rpmap = this.verifyCorpcode(sbookorgcode); // ���˴��뻺��
			HashMap<String, TsTreasuryDto> tremap = SrvCacheFacade
					.cacheTreasuryInfo(sbookorgcode);
			List<String[]> fileContent = super.readFile(file, ",");
			String trecode = "";
			CommonParamDto _dto = (CommonParamDto) paramdto;
			String encyptMode = _dto.getEncryptMode();
			int record = fileContent.size();
			if (StateConstant.SM3_ENCRYPT.equals(encyptMode) && record > 1) {
				record = record - 1;
			}
			HashMap<String, TsGenbankandreckbankDto> mapGenBankByBankCode = SrvCacheFacade
					.cacheGenBankInfo(null);
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(sbookorgcode); // ��ȡ�ո�������Ϣ
			Map<String, TsInfoconnorgaccDto> bookacctMap = this
					.getBookAcctMap(sbookorgcode);// �������˻���Ϣ
			Map<String, String> bankInfo = this.getBankInfo(sbookorgcode);// ���������ձ��ȡ�����������������к���Ϣ
			String key = "";
			for (int i = 0; i < record; i++) {

				String[] strs = fileContent.get(i);
				String reckbankcode = "aaaaaaaaaaaa";
				if (i == 0) {
					if (!strs[0].contains("**")) {
						strs[0] = "**" + strs[0];
					}

					// ���մ������д���ȡ��Ӧ��֧���кţ�����ȡ��Ӧ����Կ

					
					if (mapGenBankByBankCode
							.containsKey(sbookorgcode + strs[7])) {
						reckbankcode = mapGenBankByBankCode.get(
								sbookorgcode + strs[7]).getSreckbankcode();
					}else
					{
						String mytrecode = "";
						if(strs[0].contains("**"))
							mytrecode = strs[0].replace("**", "").trim();
						else
							mytrecode = strs[0];
						if (mapGenBankByBankCode.containsKey(sbookorgcode+mytrecode+strs[7]))
							reckbankcode =mapGenBankByBankCode.get(sbookorgcode+mytrecode+strs[7]).getSreckbankcode();
					}

					TsMankeyDto keydto = TipsFileDecrypt.findKeyByKeyMode(_dto
							.getKeyMode(), sbookorgcode, reckbankcode);
					if (null != keydto) {
						key = keydto.getSkey();
						if (StateConstant.SM3_ENCRYPT.equals(encyptMode)) {
							if (!SM3Process.verifySM3SignFile(file, key)) {
								mulitDto.getErrorList().add(
										"���������ʽ��ļ�[" + fi.getName()
												+ "]SM3ǩ����֤ʧ��!");
								return mulitDto;
							}
						}
					} else {
						mulitDto.getErrorList().add(
								"ʵ���ʽ��ļ�[" + fi.getName() + "û�в��ҵ���Ӧ�Ľ�����Կ��");
						return mulitDto;
					}
				}

				if (strs[0].contains("**")) {
					// ����ˮ��
					String tmpPackNo = SequenceGenerator
							.changePackNoForLocal(SequenceGenerator
									.getNextByDb2(
											SequenceName.FILENAME_PACKNO_REF_SEQ,
											SequenceName.TRAID_SEQ_CACHE,
											SequenceName.TRAID_SEQ_STARTWITH,
											MsgConstant.SEQUENCE_MAX_DEF_VALUE));
					;
					/**
					 * s �ļ���һ��Ϊ������Ϣ
					 */
					strs[0] = strs[0].replace("**", "").trim();
					mainvou = SequenceGenerator.getNextByDb2(
							SequenceName.HKSQ_SEQ,
							SequenceName.TRAID_SEQ_CACHE,
							SequenceName.TRAID_SEQ_STARTWITH);
					mainvou_tmp = mainvou;

					TbsTvBnkpayMainDto mainDto = new TbsTvBnkpayMainDto();
					mainDto.setIvousrlno(Long.parseLong(mainvou));
					strecode = strs[0];
					if (!this.checkTreasury(strs[0], sbookorgcode)) {
						mulitDto.getErrorList().add(
								"���а���֧�����������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
										+ strs[13].trim() + "]�ļ�¼�й���������룺"
										+ strs[0] + " ��'����������Ϣ����'�в�����!");
					}
					trecode = strs[0];
					mainDto.setStrecode(strs[0]); // �������
					String errorInfo = this.checkFileExsit(sbookorgcode,
							trecode, fi.getName(), MsgConstant.APPLYPAY_DAORU);
					if (null != errorInfo && errorInfo.length() > 0) {
						mulitDto.getErrorList().add(errorInfo);
					}
					mainDto.setSbiztype(biztype);// ҵ������
					mainDto.setSpayername(strs[1]);// ������ȫ��(��λ����)
					mainDto.setSpayeracct(strs[2]); // �������ʺ�
					if (!bookacctMap.containsKey(strs[0] + strs[2])) {
						mulitDto.getErrorList().add(
								"���а���֧�����������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
										+ strs[13].trim() + "]�ļ�¼�и������˺ţ�"
										+ strs[2].trim()
										+ "�����û����'�ʽ𲦸��������˻�ά��'������ά��!");
					}
					mainDto.setSsndbnkno(strs[3]);// �����˿�������
					mainDto.setSpayeropnbnkno(strs[4]); // �����˿��������к�
					mainDto.setSpayeename(strs[5]); // �տ���ȫ��
					mainDto.setSpayeeacct(strs[6]); // �տ����ʺ�
					mainDto.setSrcvbnkno(strs[7]);// �տ��˿�����
					/**
					 * �����ж��Ƿ���Ҫ��¼�к�
					 */
					if (ITFECommonConstant.ISMATCHBANKNAME
							.equals(StateConstant.IF_MATCHBNKNAME_YES)) {
						if (strs.length > 16 && null != strs[16]
								&& !"".equals(strs[16])) {
							mainDto.setSpayeeaddr(strs[16]); // ���տ��˿��������������տ��˵�ַ��
							if (null != strs[8] && !"".equals(strs[8])) {
								mainDto.setSpayeeopnbnkno(strs[8]);
								mainDto
										.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);
							} else {
								if (null != bankInfo.get(strs[16])
										&& !"".equals(bankInfo.get(strs[16]))) {
									mainDto.setSpayeeopnbnkno(bankInfo
											.get(strs[16]));
									mainDto
											.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								} else {
									mainDto.setSpayeeopnbnkno("");
									mainDto
											.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								}
							}
						} else {
							mulitDto.getErrorList().add(
									"���л��������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
											+ strs[13].trim()
											+ "]�ļ�¼�� �տ��˿��������� Ϊ�գ����֤!");
						}
					} else {
						// �տ��˿����д�����Ҳ���Ǵ�������Ҫת����֧���к�
						if (null != reckbankcode && !"aaaaaaaaaaaa".equals(reckbankcode)) {
							if (!reckbankcode.equals(strs[8])) {
								mulitDto.getErrorList().add(
										"���л��������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
												+ strs[13].trim()
												+ "]�ļ�¼���տ��˿������кţ�" + strs[8]
												+ " �����д�����֧���кŶ�Ӧ��ϵ�л�ȡ��֧���к�"
												+ reckbankcode + "��һ��!");
							}
						} else {
							mulitDto.getErrorList().add(
									"���л��������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
											+ strs[13].trim()
											+ "]�ļ�¼���տ��˿������кţ�" + strs[8]
											+ " û�������д�����֧���кŶ�Ӧ��ϵά��!");
						}

						mainDto.setSpayeeopnbnkno(strs[8]); // �տ��˿������к�
					}

					//�ո�������Ϣ��֤    ���� �տ��˿������к� ���ո�������Ϣ
					if (StringUtils.isBlank(mainDto.getSpayeeopnbnkno())) {
						mulitDto.getErrorList().add("���л��������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim()	+ "]�ļ�¼��û���ҵ��տ��˿������к���Ϣ!");
					} else {
						TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap.get((mainDto.getStrecode()+mainDto.getSpayeeopnbnkno()+mainDto.getSpayeracct()+mainDto.getSpayeeacct()).trim());
						if (null == tmppayacctinfoDto) {
							mulitDto.getErrorList().add("���л��������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim() + "]�ļ�¼�и��ݹ�����룺"+mainDto.getStrecode()+", �տ��˿������кţ�"+ mainDto.getSpayeeopnbnkno()+", �������˺ţ�"+mainDto.getSpayeracct()+", �տ����˺ţ�"+mainDto.getSpayeeacct()+ " û���ҵ��ո�������Ϣ!");
						}else{
							if(!mainDto.getSpayeracct().equals(tmppayacctinfoDto.getSpayeracct())){	// �������ʻ�
								mulitDto.getErrorList().add("���л��������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim() + "]�ļ�¼��,�������ʻ�:"  + mainDto.getSpayeracct() + "��ά�����ո�������Ϣ�в�һ��!");
							}
							if(!mainDto.getSpayername().equals(tmppayacctinfoDto.getSpayername())){	// ����������
								mulitDto.getErrorList().add("���л��������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim() + "]�ļ�¼��,����������:"  + mainDto.getSpayername() + "��ά�����ո�������Ϣ�в�һ��!");
							}
							if(!mainDto.getSpayeeacct().equals(tmppayacctinfoDto.getSpayeeacct())){	// �տ����˻�
								mulitDto.getErrorList().add("���л��������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim() + "]�ļ�¼��,�տ����˻�:"  + mainDto.getSpayeeacct() + "��ά�����ո�������Ϣ�в�һ��!");
							}
							if(strs[5].equals(tmppayacctinfoDto.getSpayeename())) {
								System.out.println("һ����������");
							}
							if(!mainDto.getSpayeename().equals(tmppayacctinfoDto.getSpayeename())){	// �տ�������
								mulitDto.getErrorList().add("���л��������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim() + "]�ļ�¼��,�տ�������:"  + mainDto.getSpayeename() + "��ά�����ո�������Ϣ�в�һ��!");
							}
							System.out.println("strs[5] = "+strs[5]);
							System.out.println("mainDto.getSpayeename() = "+mainDto.getSpayeename());
							System.out.println("tmppayacctinfoDto.getSpayeename() = "+tmppayacctinfoDto.getSpayeename());
						}
						
					}
					mainDto.setCbdgkind(strs[9]);// Ԥ���������
					if (strs[10] != null && !strs[10].trim().equals("")) {
						mainDto.setFzerosumamt(new BigDecimal(strs[10].trim())); // ��������
					} else {
						mainDto.setFzerosumamt(new BigDecimal("0.00")); // ��������
					}
					if (strs[11] != null && !strs[11].trim().equals("")) { // С���ֽ�����
						mainDto
								.setFsmallsumamt(new BigDecimal(strs[11].trim()));
					} else {
						mainDto.setFsmallsumamt(new BigDecimal("0.00"));
					}
					famt = famt.add(mainDto.getFzerosumamt()).add(
							mainDto.getFsmallsumamt());
					mainDto.setSsrlacctbriefcode(strs[12]);
					mainDto.setSvouno(strs[13]); // ƾ֤���
					svo = strs[13];
					voulist.add(trecode.trim() + "," + strs[13].trim());
					mainDto.setDvoucher(CommonUtil.strToDate(strs[14]));// ƾ֤����
					mainDto.setIofyear(Integer.parseInt(fi.getName().substring(
							0, 4))); // ������Ȳ�Ϊ��
					mainDto.setSagentbnkcode(strs[8]);// ���������кŲ���Ϊ��
					mainDto.setDaccept(TimeFacade.getCurrentDateTime()); // �������ڲ�Ϊ��(Ĭ�ϵ�ǰ����)
					mainDto.setSbookorgcode(sbookorgcode);// �����������
					mainDto.setSfilename(fi.getName());
					mainDto.setSpackno(tmpPackNo);
					mainDto.setCtrimflag(fi.getName().substring(
							fi.getName().indexOf(".") - 1,
							fi.getName().indexOf(".")));
					if (strs.length > 15) {
						mainDto.setSaddword(strs[15]);
					}
					fatherdtos.add(mainDto);
				} else {
					// sub��Ϣ
					TbsTvBnkpaySubDto subDto = new TbsTvBnkpaySubDto();
					String err = this.verifySubject(bmap, strs[1],
							MsgConstant.APPLYPAY_DAORU, "1", fi.getName(), svo);
					if (!"".equals(err)) {
						mulitDto.getErrorList().add(err);
					}
					subDto.setSfuncsbtcode(strs[1]); // ���ܿ�Ŀ����

					if (null != tremap.get(trecode)) {
						if (StateConstant.COMMON_YES.equals(tremap.get(trecode)
								.getSisuniontre())) { // ���õ�������жϾ��ô��벻��Ϊ��
							err = this.verifySubject(bmap, strs[2],
									MsgConstant.APPLYPAY_DAORU, "2", fi
											.getName(), svo);
							if (!"".equals(err)) {
								mulitDto.getErrorList().add(err);
							}
							subDto.setSecosbtcode(strs[2]); // ���ÿ�Ŀ����
						} else { // �����õ�����£������ļ����Ƿ��о��ô��룬����д��ֵ
							subDto.setSecosbtcode("");
						}
					}

					subDto.setSbdgorgcode(strs[0]); // Ԥ�㵥λ����
					// �޸�Ϊ���չ������+���˴������ά��
					if (!rpmap.containsKey(trecode
							+ subDto.getSbdgorgcode().trim())) {
						mulitDto.getErrorList().add(
								"���а���֧�����������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
										+ svo + "]�ļ�¼��Ԥ�㵥λ���� '"
										+ subDto.getSbdgorgcode()
										+ "' �ڷ��˴�����в�����!");
					}
					subDto.setCacctprop(strs[3]);// �˻�����
					subDto.setFamt(new BigDecimal(strs[4]));// ������
					sonFamt = sonFamt.add(new BigDecimal(strs[4]));
					subDto.setSbookorgcode(sbookorgcode); // �����������
					subDto.setIvousrlno(Long.parseLong(mainvou)); // ƾ֤��ˮ��
					subDto.setIgrpinnerseqno(recordNum + 1); // ƾ֤��ˮ��
					if (diremap.containsKey(strs[0]+","+strs[1]+","+strs[2]+","+strs[3])) { // ����ͬԤ���Ŀ�����ܴ��롢���ô�����л���
						diremap.put(strs[0] + "," + strs[1] + "," + strs[2]+","+strs[3],diremap.get(strs[0] + "," + strs[1] + ","+ strs[2]+","+strs[3]).add(new BigDecimal(strs[4])));
					} else {
						diremap.put(strs[0] + "," + strs[1] + "," + strs[2]+","+strs[3],new BigDecimal(strs[4]));
					}
					sondtos_tmp.add(subDto);
					recordNum++;
				}

			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("���а���֧�����������ļ���������\n" + e.getMessage(),
					e);
		}
		if (famt.compareTo(new BigDecimal("0.00")) == 0) {
			mulitDto.getErrorList().add(
					"���а���֧�����������ļ�[" + fi.getName() + "]�������Ϊ0");
		}
		if (famt.compareTo(sonFamt) != 0) {
			mulitDto.getErrorList().add(
					"���а���֧�����������ļ�[" + fi.getName() + "]�л��ܽ��[" + famt
							+ "]����ϸ�ܽ��[" + sonFamt + "]����");
		}

		// �ļ���ƾ֤���У��
		int oldSize = 0;
		int newSize = 0;
		Set<String> sets = new HashSet<String>();
		for (String item : voulist) {
			oldSize = sets.size();
			sets.add(item);
			newSize = sets.size();
			if (newSize == oldSize) {
				mulitDto.getErrorList().add(
						"���а���֧�����������ļ�[" + fi.getName() + "]�д���ƾ֤����ظ�");
			}
		}
		// ���չ��ܡ����á�Ԥ�㵥λ����
		if (recordNum > 499) {
			int Igrpinnerseqno = 0;
			for (String single : diremap.keySet()) {
				String[] strs = single.split(",");
				TbsTvBnkpaySubDto diredto = new TbsTvBnkpaySubDto();
				diredto.setIvousrlno(Long.parseLong(mainvou_tmp));
				diredto.setSfuncsbtcode(strs[1]);
				diredto.setSecosbtcode(strs[2]);
				diredto.setIgrpinnerseqno(++Igrpinnerseqno);
				diredto.setCacctprop(strs[3]);
				diredto.setSbdgorgcode(strs[0]);
				diredto.setFamt(diremap.get(single));
				diredto.setSbookorgcode(sbookorgcode);
				sondtos.add(diredto);
			}
		} else {
			sondtos.addAll(sondtos_tmp);
		}
		if (sondtos.size() > 499) {
			mulitDto.getErrorList().add(
					"���а���֧�����������ļ�[" + fi.getName()
							+ "]����ϸ������Ϣ���չ������Ŀ���롢�������Ŀ���롢Ԥ�㵥λ������л��ܺ��ܱ���Ϊ["
							+ sondtos.size() + "],����499�ʣ����ֹ��������!");
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
