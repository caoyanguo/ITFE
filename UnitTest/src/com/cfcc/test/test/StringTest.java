package com.cfcc.test.test;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
//
public class StringTest {
	public static void main(String[] args) {
		
		String date = "201911208";
		Date d = java.sql.Date.valueOf(date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6));
		System.out.println(d);
		
	}
}
//		/*String s = "";
//		String s1 = null;
////		if(s1.equals(s)) {
////			
////		}
//		String ss = "12345 ";
//		if(ss.equals("12345 ")) {
//			System.out.println("sss");
//		}
//		Set<String> vouset = new HashSet<String>();
//		vouset.add("111");
//		vouset.add("222");
//		vouset.add("111 ");
//		vouset.add("111,222");
//		vouset.add("111,222");
//		System.out.println(vouset);
//		List<String> voulist = new ArrayList<String>();
//		voulist.add("111");
//		voulist.add("111 ");
//		System.out.println(voulist);
//		if("111 ".equals(voulist.get(1))) {
//			System.out.println(voulist.get(1)+"222");
//		}
//		System.out.println("********************************************************************************************");
//		System.out.println("111111,2222222".split(",")[1]);
//		*/
//		Set<String> vouset = new HashSet<String>();
//		vouset.add("111");
//		vouset.add("222");
//		vouset.add("111 ");
//		vouset.add("111,222");
//		vouset.add("111,222");
//		System.out.println(vouset);
//		System.out.println(vouset.size());
//	}
//	public MulitTableDto loadFile(List filelist, String biztype,
//			String filekind, IDto paramdto) throws ITFEBizException {
//
//		// 1.���ȵõ������ļ������������ʵ��
//		ITipsFileOper fileOper = (ITipsFileOper) ContextFactory
//				.getApplicationContext().getBean(
//						MsgConstant.SPRING_FILEPRA_SERVER + filekind);
//
//		// 2.���ļ����н���
//		MulitTableDto mudto = null;
//
//		// �������Žӿ�
//		MulitTableDto allmudto = new MulitTableDto(new ArrayList<IDto>(),
//				new ArrayList<IDto>(), new ArrayList<IDto>(),
//				new ArrayList<String>());
//
//		// ���ڶ�ȶ��ļ����
//		MulitTableDto allPaydto = new MulitTableDto(new ArrayList<IDto>(),
//				new ArrayList<IDto>(), new ArrayList<IDto>(),
//				new ArrayList<String>());
//
//		MulitTableDto allmudtoforxm = new MulitTableDto();
//
//		List<IDto> li = new ArrayList<IDto>();
//
//		// �õ��ļ��ϴ�����
//		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory
//				.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
//
//		// �ļ��ϴ���·��
//		String root = sysconfig.getRoot();
//
//		// �õ���Ŀ
//		Map<String, TsBudgetsubjectDto> smap = this
//				.makeSubjectMap(getLoginInfo().getSorgcode());
//
//		StringBuffer sb; // ���÷������ϴ���·��
//
//		for (Object obj : filelist) {
//
//			// ��ǰ̨���������·�����ϸ�·��
//			sb = new StringBuffer(root);
//
//			sb.append(obj.toString().replace("/", File.separator).replace("\\",
//					File.separator));
//
//			String file = sb.toString();
//
//			File fl = new File(file);
//
//			mudto = fileOper.fileParser(file, getLoginInfo().getSorgcode(),
//					getLoginInfo().getSuserCode(), biztype,
//					ITFECommonConstant.IFNEWINTERFACE, paramdto, smap);
//
//			if (filekind.equals(MsgConstant.TUIKU_XIAMEN_DAORU)
//					|| filekind.equals(MsgConstant.SHIBO_XIAMEN_DAORU)) {
//
//				if (file.toLowerCase().endsWith("txt")) { // ��ϸ�ڽӿ�					
//
//				} else if (file.toLowerCase().endsWith("tmp")) { // �ʽ�ڽӿ�
//
//				}
//
//			} else if (filekind.equals(MsgConstant.PILIANG_DAORU)) {
//
//				this.createFileDto(mudto, filekind); // �������� ֱ�ӱ���
//
//				/**
//				 * ֱ��֧���������Ȩ֧������谴С��1000���ļ���һ����
//				 */
//			} else if (filekind.equals(MsgConstant.ZHIJIE_DAORU)
//					|| filekind.equals(MsgConstant.SHOUQUAN_DAORU)) {
//				// ����֮ǰ��У�鵱��ƾ֤����Ƿ��ظ�
//				this.checkVouRepeat(mudto, filekind, fl.getName());
//			} else {
//
//				this.checkVouRepeat(mudto, filekind, fl.getName());// ����֮ǰ��У�鵱��ƾ֤����Ƿ��ظ�
//
//				this.createFileDto(mudto, filekind); // ���ж�У����ϣ�����
//
//			}
//
//		}
//
//		/**
//		 * ����ֱ��֧���������Ȩ֧����ȣ�����ļ����ݰ���ͬ��������������
//		 */
//		if (allPaydto.getFatherDtos() != null
//				&& allPaydto.getFatherDtos().size() > 0) {
//			setPayDataOnePackage(allPaydto, filekind);
//		}
//
//		// 3.����ʵ�����˿�����Žӿڷ���ϸ�ں��ʽ�ڣ���Ҫ�����������,
//		// �����տ��˺źͽ����й���
//		if (allmudto.getFatherDtos() != null
//				&& allmudto.getFatherDtos().size() > 0) {
//			for (IDto dto : allmudto.getFatherDtos()) {
//				if (dto instanceof TbsTvPayoutDto) { // ʵ���ʽ�XM
//					TbsTvPayoutDto pdto = (TbsTvPayoutDto) dto;
//					for (IDto dto1 : allmudto.getSonDtos()) {
//						TvPayoutfinanceDto fidto = (TvPayoutfinanceDto) dto1;
//						if (pdto.getSpayeeacct().trim().equals(
//								fidto.getSpayeeacct().trim())) {
//							if (pdto.getFamt().compareTo(fidto.getFamt()) == 0) {
//								pdto.setSpayeeopnbnkno(fidto
//										.getSpayeeopnbnkno()); // �����ʽ�ڵ��տ��˿������к�
//								pdto.setSpayeename(fidto.getSpayeename()); // �����ʽ���տ�������
//								pdto.setSbdgorgname(fidto.getSpayeename()); // ����Ԥ�㵥λ����
//								pdto.setSpayeebankno(fidto.getSrcvreckbnkno()); // �����������к�
//								if ("".equals(pdto.getSbdgorgcode().trim())) { // ���Ԥ�㵥λ����Ϊ��������
//									if (fidto.getSpayeeacct().length() > 15) {
//										pdto.setSbdgorgcode(fidto
//												.getSpayeeacct().trim()
//												.substring(0, 15));
//									} else {
//										pdto.setSbdgorgcode(fidto
//												.getSpayeeacct().trim());
//									}
//								}
//								li.add(pdto);
//								break;
//							}
//						}
//					}
//				} else if (dto instanceof TbsTvDwbkDto) { // �˿�XM
//					TbsTvDwbkDto dwdto = (TbsTvDwbkDto) dto;
//					for (IDto dto1 : allmudto.getSonDtos()) {
//						TvPayoutfinanceDto fidto = (TvPayoutfinanceDto) dto1;
//						if (dwdto.getSpayeeacct().trim().equals(
//								fidto.getSpayeeacct().trim())) {
//							if (dwdto.getFamt().compareTo(fidto.getFamt()) == 0) {
//								dwdto.setSpayeeopnbnkno(fidto
//										.getSpayeeopnbnkno()); // �����ʽ�ڵ��տ��˿������к�
//								dwdto.setSpayeename(fidto.getSpayeename()); // �����ʽ���տ�������
//								li.add(dwdto);
//								break;
//							}
//						}
//					}
//				}
//			}
//			// ��������֮���dto��ĿС��û�����֮ǰ
//			if (allmudto.getFatherDtos().size() != li.size()) {
//
//				throw new ITFEBizException("ͨ����ϸ�ڸ������˺źͽ��δ�ҵ���Ӧ�ʽ�ڼ�¼,���֤");
//
//			}
//
//			allmudto.setSonDtos(null); // ������Ҫд�����ݿ���ʽ�����
//
//			if (filekind.equals(MsgConstant.SHIBO_XIAMEN_DAORU)) { // ����ʵ��
//
//				try {
//
//					allmudtoforxm = this.splitPackageWithBank(li, biztype); // ����ʵ���谴�����зְ�
//
//				} catch (JAFDatabaseException e) {
//
//					log.error(e);
//
//					throw new ITFEBizException("���ݽ����쳣", e);
//
//				} catch (ValidateException e) {
//
//					log.error(e);
//
//					throw new ITFEBizException("���ݽ����쳣", e);
//
//				} catch (SequenceException e) {
//
//					log.error(e);
//
//					throw new ITFEBizException("���ݽ����쳣", e);
//
//				}
//
//				this.createFileDto(allmudtoforxm, filekind); // ���水�����зְ��������
//
//				return allmudtoforxm;
//
//			} else if (filekind.equals(MsgConstant.TUIKU_XIAMEN_DAORU)) { // �����˿�
//
//				allmudto.setFatherDtos(li); // ����������ݵ�List����
//
//				this.createFileDto(allmudto, filekind); // ��������
//
//				return allmudto;
//
//			}
//		}
//
//		return mudto;
//	}
//}
