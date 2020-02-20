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
import org.eclipse.swt.internal.LONG;

import com.cfcc.deptone.common.util.DateUtil;
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
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author zhang ���а���֧�����������˻��ļ����������
 */
public class ApplypayBackTipsFileOper extends AbstractTipsFileOper {

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		int recordNum = 0; // ��¼��¼��
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);// ҵ������
		mulitDto.setSbookorgcode(sbookorgcode);// �����������
		BigDecimal famt = new BigDecimal("0.00");
		BigDecimal sonFamt = new BigDecimal("0.00");
		List<IDto> fatherdtos = new ArrayList<IDto>();
		List<IDto> sondtos = new ArrayList<IDto>();

		List<String> voulist = new ArrayList<String>(); // ƾ֤��ż�
		List<String> oriVouList = new ArrayList<String>(); // ԭ2ƾ֤��ż�
		File fi = new File(file);
		HashMap<String, TbsTvBnkpayMainDto> mainMap = new HashMap<String, TbsTvBnkpayMainDto>();
		HashMap<String, String> vouMap = null;
		int j = 0;
		String strecode = null;
		String mainvou = null;
		String svo = null;
		String key = null;
		TvPayreckBankBackDto paydto = (TvPayreckBankBackDto) idto;
		try {
			Map<String, TdCorpDto> rpMap = this.verifyCorpcode(sbookorgcode); // ���˴��뻺��
			HashMap<String, TsTreasuryDto> treMap = SrvCacheFacade
					.cacheTreasuryInfo(sbookorgcode);
			List<String[]> fileContent = super.readFile(file, ",");
			CommonParamDto _dto = (CommonParamDto) paramdto;
			String encyptMode = _dto.getEncryptMode();
			int record = fileContent.size();
			if (StateConstant.SM3_ENCRYPT.equals(encyptMode) && record > 1) {
				record = record - 1;
			}
			HashMap<String, TsGenbankandreckbankDto> mapGenBankByBankCode = SrvCacheFacade
					.cacheGenBankInfo(null);
			Map<String, TsInfoconnorgaccDto> bookacctMap = this
					.getBookAcctMap(sbookorgcode);// �տ����˻���Ϣ
			HashMap<String, TsPayacctinfoDto> payacctinfoMap = SrvCacheFacade
					.cachePayacctInfo(sbookorgcode); // ��ȡ�ո�������Ϣ
			Map<String, String> bankInfo = this.getBankInfo(sbookorgcode);// ���������ձ��ȡ�����������������к���Ϣ
			TbsTvBnkpayMainDto mainDto = null;
			for (int i = 0; i < record; i++) {

				String[] strs = fileContent.get(i);
				if (i == 0) {
					if (!strs[0].contains("**")) {
						strs[0] = "**" + strs[0];
					}
					// ���մ������д���ȡ��Ӧ��֧���кţ�����ȡ��Ӧ����Կ
					String reckbankcode = "aaaaaaaaaaaa";
					if (mapGenBankByBankCode
							.containsKey(sbookorgcode + strs[3])) {
						reckbankcode = mapGenBankByBankCode.get(
								sbookorgcode + strs[3]).getSreckbankcode();
					}else
					{
						String mytrecode = "";
						if(strs[0].contains("**"))
							mytrecode = strs[0].replace("**", "").trim();
						else
							mytrecode = strs[0];
						if (mapGenBankByBankCode.containsKey(sbookorgcode+mytrecode+strs[3]))
							reckbankcode =mapGenBankByBankCode.get(sbookorgcode+mytrecode+strs[3]).getSreckbankcode();
					}
					TsMankeyDto keydto = TipsFileDecrypt.findKeyByKeyMode(_dto
							.getKeyMode(), sbookorgcode, reckbankcode);
					if (null != keydto) {
						key = keydto.getSkey();
						if (StateConstant.SM3_ENCRYPT.equals(encyptMode)) {
							if (!SM3Process.verifySM3SignFile(file, key)) {
								mulitDto.getErrorList().add(
										"���������˿�[" + fi.getName()
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
				if (strs[0].contains("**")) { // �����ͺŵ�Ϊ�ļ���������Ϣ
					j = 0;
					mainDto = new TbsTvBnkpayMainDto();
					strecode = strs[0].replace("**", "").trim();
					if (!this.checkTreasury(strecode, sbookorgcode)) {
						mulitDto.getErrorList().add(
								"���а���֧�����������˻��ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
										+ strs[13].trim() + "]�ļ�¼�й���������룺"
										+ strs[0] + " ��'����������Ϣ����'�в�����!");
					}
					mainDto.setStrecode(strecode); // �������
					String errorInfo = this.checkFileExsit(sbookorgcode,
							strecode, fi.getName(),
							MsgConstant.APPLYPAY_BACK_DAORU);
					if (null != errorInfo && errorInfo.length() > 0) {
						mulitDto.getErrorList().add(errorInfo);
					}
					mainDto.setSbiztype(biztype);// ҵ������
					// ������ȫ��(��λ����)����Ϊ��
					if (StringUtils.isNotBlank(strs[1])) {
						mainDto.setSpayername(strs[1]);// ������ȫ��(��λ����)
					} else {
						mulitDto.getErrorList().add(
								"���а���֧�����������˻��ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
										+ strs[13].trim() + "]�ļ�¼������ȫ�Ʋ���Ϊ��!");
					}
					mainDto.setSpayeracct(strs[2]); // �������ʺ�
					mainDto.setSsndbnkno(strs[3]);// �����˿�������

					/**
					 * �����ж��Ƿ���Ҫ��¼�к�
					 */
					if (ITFECommonConstant.ISMATCHBANKNAME
							.equals(StateConstant.IF_MATCHBNKNAME_YES)) {
						if (strs.length > 15 && null != strs[15]
								&& !"".equals(strs[15])) {
							mainDto.setSpayeeaddr(strs[15]); // ���տ��˿��������������տ��˵�ַ��
							if (null != strs[4] && !"".equals(strs[4])) {
								mainDto.setSpayeropnbnkno(strs[4]);
								mainDto
										.setSifmatch(StateConstant.IF_MATCHBNKNAME_NO);
							} else {
								if (null != bankInfo.get(strs[15])
										&& !"".equals(bankInfo.get(strs[15]))) {
									mainDto.setSpayeropnbnkno(bankInfo
											.get(strs[15]));
									mainDto
											.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								} else {
									mainDto.setSpayeropnbnkno("");
									mainDto
											.setSifmatch(StateConstant.IF_MATCHBNKNAME_YES);
								}
							}
						} else {
							mulitDto.getErrorList().add(
									"���л����˻������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
											+ strs[13].trim()
											+ "]�ļ�¼�� �տ��˿��������� Ϊ�գ����֤!");
						}
					}else {
						mainDto.setSpayeropnbnkno(strs[4]); // �����˿��������к�	
						//�տ��˿����д�����Ҳ���Ǵ�������Ҫת����֧���к�
						TsGenbankandreckbankDto tmpDto = mapGenBankByBankCode.get(sbookorgcode+strs[3]);
						if(tmpDto==null)
							tmpDto = mapGenBankByBankCode.get(sbookorgcode+strecode+strs[3]);
						if(null != tmpDto) {
							String tcbsbnk =tmpDto.getSreckbankcode();

							if (!tcbsbnk.equals(strs[4])) {
								mulitDto.getErrorList().add(
										"���л����˻������ļ�[" + fi.getName()
												+ "] ƾ֤���Ϊ[" + strs[13].trim()
												+ "]�ļ�¼���տ��˿������кţ�" + strs[4]
												+ " �����д�����֧���кŶ�Ӧ��ϵ�л�ȡ��֧���к�"
												+ tcbsbnk + "��һ��!");
							}
						}

						if ((ITFECommonConstant.SRC_NODE.equals("000001900000")&&!paydto.getSpaysndbnkno().substring(0, 3).equals(strs[4].substring(0, 3)))||!paydto.getSpaysndbnkno().equals(strs[4])) {
							mulitDto.getErrorList()
									.add(
											"���л����˻������ļ�[" + fi.getName()
													+ "] ƾ֤���Ϊ["
													+ strs[13].trim()
													+ "]�ļ�¼���տ��˿������кţ�"
													+ strs[4] + " �����ѡ���֧���к�"
													+ paydto.getSpaysndbnkno()
													+ "��һ��!");
						}
					}

					// �տ���ȫ�� ����Ϊ��
					if (StringUtils.isNotBlank(strs[5])) {
						mainDto.setSpayeename(strs[5]);
					} else {
						mulitDto.getErrorList().add(
								"���а���֧�����������˻��ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
										+ strs[13].trim() + "]�ļ�¼�տ���ȫ�Ʋ���Ϊ��!");
					}
					if (!bookacctMap.containsKey(strecode + strs[6])) {
						mulitDto.getErrorList().add(
								"���а���֧�����������ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
										+ strs[13].trim() + "]�ļ�¼���տ����˺ţ�"
										+ strs[6].trim()
										+ "�����û����'�ʽ𲦸��������˻�ά��'������ά��!");
					}
					mainDto.setSpayeeacct(strs[6]); // �տ����ʺ�

					mainDto.setSrcvbnkno(strs[7]);// �տ��˿�����
					mainDto.setSpayeeopnbnkno(strs[8]); // �տ��˿������к�

					
					//�ո�������Ϣ��֤	���� �����˿������к� ���ո�������Ϣ
					if (StringUtils.isBlank(mainDto.getSpayeropnbnkno())) {
						mulitDto.getErrorList().add("���а���֧�����������˻��ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim()	+ "]�ļ�¼��û���ҵ������˿������к���Ϣ!");
					} else {
						TsPayacctinfoDto tmppayacctinfoDto = payacctinfoMap.get((mainDto.getStrecode()+mainDto.getSpayeropnbnkno()+mainDto.getSpayeracct()+mainDto.getSpayeeacct()).trim());
						if (null == tmppayacctinfoDto) {
							mulitDto.getErrorList().add("���а���֧�����������˻��ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim() + "]�ļ�¼�и��� ������룺"+mainDto.getStrecode()+",�����˿������кţ�"+ mainDto.getSpayeropnbnkno()+ ", �������˺ţ�"+mainDto.getSpayeracct()+", �տ����˺ţ�"+mainDto.getSpayeeacct()+ " û���ҵ��ո�������Ϣ!");
						}else{
							if(!mainDto.getSpayeracct().equals(tmppayacctinfoDto.getSpayeracct())){	// �������ʻ�
								mulitDto.getErrorList().add("���а���֧�����������˻��ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim() + "]�ļ�¼��,�������ʻ�:"  + mainDto.getSpayeracct() + "��ά�����ո�������Ϣ�в�һ��!");
							}
							if(!mainDto.getSpayername().equals(tmppayacctinfoDto.getSpayername())){	// ����������
								mulitDto.getErrorList().add("���а���֧�����������˻��ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim() + "]�ļ�¼��,����������:"  + mainDto.getSpayername() + "��ά�����ո�������Ϣ�в�һ��!");
							}
							if(!mainDto.getSpayeeacct().equals(tmppayacctinfoDto.getSpayeeacct())){	// �տ����˻�
								mulitDto.getErrorList().add("���а���֧�����������˻��ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim() + "]�ļ�¼��,�տ����˻�:"  + mainDto.getSpayeeacct() + "��ά�����ո�������Ϣ�в�һ��!");
							}
							if(!mainDto.getSpayeename().equals(tmppayacctinfoDto.getSpayeename())){	// �տ�������
								mulitDto.getErrorList().add("���а���֧�����������˻��ļ�[" + fi.getName() + "] ƾ֤���Ϊ["+ strs[13].trim() + "]�ļ�¼��,�տ�������:"  + mainDto.getSpayeename() + "��ά�����ո�������Ϣ�в�һ��!");
							}
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
					voulist.add(strecode.trim() + "," + strs[13].trim());
					mainDto.setDvoucher(CommonUtil.strToDate(strs[14]));// ƾ֤����
					mainDto.setIofyear(Integer.parseInt(fi.getName().substring(
							0, 4))); // ������Ȳ�Ϊ��
					mainDto.setSagentbnkcode(strs[4]);// ���������кŲ���Ϊ��
					mainDto.setDaccept(TimeFacade.getCurrentDateTime()); // �������ڲ�Ϊ��(Ĭ�ϵ�ǰ����)
					mainDto.setSbookorgcode(sbookorgcode);// �����������
					mainDto.setSfilename(fi.getName());
					mainDto.setDpayentrustdate(paydto.getDpayentrustdate());
					mainDto.setSpaydictateno(paydto.getSpaydictateno());
					mainDto.setSpaymsgno(strs[8]);
					mainDto.setSpaymsgno(paydto.getSpaymsgno());
					mainDto.setSpaysndbnkno(paydto.getSpaysndbnkno());
					mainDto.setCtrimflag(fi.getName().substring(
							fi.getName().indexOf(".") - 1,
							fi.getName().indexOf(".")));
					// ���ܼ�¼�������һ��ƾ֤��Ϣ
					vouMap = new HashMap<String, String>();
				} else {// ��ϸ�е���Ϣ��Ҫ����ԭƾ֤��Ž��зְ�
					TbsTvBnkpaySubDto subDto = new TbsTvBnkpaySubDto();
					List<IDto> sondtolist = new ArrayList<IDto>();
					// ԭƾ֤��š�ԭƾ֤���� ��һ���̶���ֵ��Ϊ�˹���Ĵ���ķ����ԣ�����Ҳ���ԭƾ֤��Ϣ����������ڷְ�
					if (StateConstant.COMMON_YES
							.equals(ITFECommonConstant.IFAUTOGENORIVOUNO)) {
						strs[0] = svo;
						strs[1] = TimeFacade.getCurrentStringTimebefor();
					}
					subDto.setSorivouno(strs[0]);// ԭƾ֤���
					subDto.setDorivoudate(CommonUtil.strToDate(strs[1]));// ԭƾ֤����
					subDto.setSbdgorgcode(strs[2]); // Ԥ�㵥λ����
					// �޸�Ϊ���չ������+���˴������ά��
					if (!rpMap.containsKey(strecode
							+ subDto.getSbdgorgcode().trim())) {
						mulitDto.getErrorList().add(
								"���а���֧�����������˻��ļ�[" + fi.getName() + "] ƾ֤���Ϊ["
										+ svo + "]�ļ�¼��Ԥ�㵥λ���� '"
										+ subDto.getSbdgorgcode()
										+ "' �ڷ��˴�����в�����!");
					}
					subDto.setSfuncsbtcode(strs[3]); // ���ܿ�Ŀ����
					String err = this.verifySubject(bmap, strs[3],
							MsgConstant.APPLYPAY_BACK_DAORU, "1", fi.getName(),
							svo);
					if (!"".equals(err)) {
						mulitDto.getErrorList().add(err);
					}
					if (null != treMap.get(strecode)) {
						if (StateConstant.COMMON_YES.equals(treMap
								.get(strecode).getSisuniontre())) { // ���õ�������жϾ��ô��벻��Ϊ��
							err = this.verifySubject(bmap, strs[4],
									MsgConstant.APPLYPAY_BACK_DAORU, "2", fi
											.getName(), svo);
							if (!"".equals(err)) {
								mulitDto.getErrorList().add(err);
							}
							subDto.setSecosbtcode(strs[4]); // ���ÿ�Ŀ����
						} else { // �����õ�����£������ļ����Ƿ��о��ô��룬����д��ֵ
							subDto.setSecosbtcode(null);
						}
					}
					subDto.setCacctprop(strs[5]);// �˻�����
					subDto.setFamt(new BigDecimal(strs[6].trim()));// ������
					sonFamt = sonFamt.add(new BigDecimal(strs[6].trim()));// ������ϸ�ϼƽ��
					subDto.setIgrpinnerseqno(recordNum + 1);// �������
					subDto.setSbookorgcode(sbookorgcode); // �����������
					// ˵����ϸ���Ѱ���ԭƾ֤���+ԭƾ֤������ͬ�ļ�¼
					String keyWord = strs[0] + strs[1];
					if (vouMap.containsKey(keyWord)) {
						if (subDto.getCacctprop().equals(
								StateConstant.CONPAY_ZEROBALANCE)) {
							mainMap.get(keyWord).setFzerosumamt(
									mainMap.get(keyWord).getFzerosumamt().add(
											subDto.getFamt()));
						} else {
							mainMap.get(keyWord).setFsmallsumamt(
									mainMap.get(keyWord).getFsmallsumamt().add(
											subDto.getFamt()));
						}
						subDto
								.setIvousrlno(Long.parseLong(vouMap
										.get(keyWord)));
					} else {// ����һ���°�,�൱�������һ����ϸ
						j++;
						TbsTvBnkpayMainDto cremainDto = (TbsTvBnkpayMainDto) mainDto
								.clone();
						// ����һ���µİ���ˮ��,ͬʱ����һ���µ���ƾ֤��ˮ����Ϊ������Ϣ
						mainvou = SequenceGenerator.getNextByDb2(
								SequenceName.HKSQ_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH);
						String tmpPackNo = SequenceGenerator
								.changePackNoForLocal(SequenceGenerator
										.getNextByDb2(
												SequenceName.FILENAME_PACKNO_REF_SEQ,
												SequenceName.TRAID_SEQ_CACHE,
												SequenceName.TRAID_SEQ_STARTWITH,
												MsgConstant.SEQUENCE_MAX_DEF_VALUE));
						vouMap.put(keyWord, mainvou);
						cremainDto.setSpackno(tmpPackNo);
						// ƾ֤��ˮ����Ҫ��ԭƾ֤��ˮ�ŵĻ���������ĩβ���֣�����ְ���ƾ֤������¸�ֵ ��Ҫ���⴦��
						String snewvouno;
						if (StateConstant.COMMON_YES
								.equals(ITFECommonConstant.IFAUTOGENORIVOUNO)) {
							snewvouno = mainDto.getSvouno();
						} else {
							snewvouno = mainDto.getSvouno() + j;
							if (snewvouno.length() > 20) {
								snewvouno = snewvouno.substring(snewvouno
										.length() - 20, snewvouno.length());
							}
						}
						cremainDto.setSvouno(snewvouno);
						// �ְ�����µ�������Ϣ��ƾ֤��ˮ��
						cremainDto.setIvousrlno(Long.parseLong(mainvou));
						// ������ԭƾ֤�ำֵ
						cremainDto.setSorivouno(strs[0]);
						cremainDto
								.setDorivoudate(CommonUtil.strToDate(strs[1]));
						if (subDto.getCacctprop().equals(
								StateConstant.CONPAY_ZEROBALANCE)) {
							cremainDto.setFzerosumamt(subDto.getFamt());
						} else {
							cremainDto.setFsmallsumamt(subDto.getFamt());
						}
						// ����MAP��
						mainMap.put(keyWord, cremainDto);
						subDto.setIvousrlno(Long.parseLong(mainvou)); // ƾ֤��ˮ��
						fatherdtos.add(cremainDto);
					}

					recordNum++;
					sondtos.add(subDto);
				}
			}
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException(
					"���а���֧�����������˻��ļ���������\n" + e.getMessage(), e);
		}
		if (famt.compareTo(new BigDecimal("0.00")) == 0) {
			mulitDto.getErrorList().add(
					"���а���֧�����������˻��ļ�[" + fi.getName() + "]�������Ϊ0");
		}
		if (famt.compareTo(paydto.getFamt()) != 0) {
			mulitDto.getErrorList().add(
					"¼��������ʽ��ܶ�[" + paydto.getFamt() + "]�뵼���ļ�[" + fi.getName()
							+ "]�е��ܽ��[" + famt + "]��һ�£�");
		}
		if (famt.compareTo(sonFamt) != 0) {
			mulitDto.getErrorList().add(
					"���а���֧�����������˻��ļ�[" + fi.getName() + "]�л��ܽ��[" + famt
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
						"���а���֧�����������˻��ļ�[" + fi.getName() + "]�д���ƾ֤����ظ�");
			}
		}

		mulitDto.setFatherDtos(fatherdtos);
		mulitDto.setSonDtos(sondtos);

		mulitDto.setVoulist(voulist);

		return mulitDto;
	}

	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {
		// TODO Auto-generated method stub
		return null;
	}

}
