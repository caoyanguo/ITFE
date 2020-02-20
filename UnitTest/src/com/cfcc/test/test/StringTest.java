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
//		// 1.首先得到进行文件解析服务的类实例
//		ITipsFileOper fileOper = (ITipsFileOper) ContextFactory
//				.getApplicationContext().getBean(
//						MsgConstant.SPRING_FILEPRA_SERVER + filekind);
//
//		// 2.对文件进行解析
//		MulitTableDto mudto = null;
//
//		// 用于厦门接口
//		MulitTableDto allmudto = new MulitTableDto(new ArrayList<IDto>(),
//				new ArrayList<IDto>(), new ArrayList<IDto>(),
//				new ArrayList<String>());
//
//		// 用于额度多文件组包
//		MulitTableDto allPaydto = new MulitTableDto(new ArrayList<IDto>(),
//				new ArrayList<IDto>(), new ArrayList<IDto>(),
//				new ArrayList<String>());
//
//		MulitTableDto allmudtoforxm = new MulitTableDto();
//
//		List<IDto> li = new ArrayList<IDto>();
//
//		// 得到文件上传配置
//		FileSystemConfig sysconfig = (FileSystemConfig) ContextFactory
//				.getApplicationContext().getBean("fileSystemConfig.ITFE.ID");
//
//		// 文件上传根路径
//		String root = sysconfig.getRoot();
//
//		// 得到科目
//		Map<String, TsBudgetsubjectDto> smap = this
//				.makeSubjectMap(getLoginInfo().getSorgcode());
//
//		StringBuffer sb; // 放置服务器上传根路径
//
//		for (Object obj : filelist) {
//
//			// 将前台传来的相对路径加上根路径
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
//				if (file.toLowerCase().endsWith("txt")) { // 明细岗接口					
//
//				} else if (file.toLowerCase().endsWith("tmp")) { // 资金岗接口
//
//				}
//
//			} else if (filekind.equals(MsgConstant.PILIANG_DAORU)) {
//
//				this.createFileDto(mudto, filekind); // 批量拨付 直接保存
//
//				/**
//				 * 直接支付额度与授权支付额度需按小于1000个文件组一个包
//				 */
//			} else if (filekind.equals(MsgConstant.ZHIJIE_DAORU)
//					|| filekind.equals(MsgConstant.SHOUQUAN_DAORU)) {
//				// 保存之前先校验当日凭证编号是否重复
//				this.checkVouRepeat(mudto, filekind, fl.getName());
//			} else {
//
//				this.checkVouRepeat(mudto, filekind, fl.getName());// 保存之前先校验当日凭证编号是否重复
//
//				this.createFileDto(mudto, filekind); // 所有都校验完毕，保存
//
//			}
//
//		}
//
//		/**
//		 * 对于直接支付额度与授权支付额度，多个文件数据按不同国库主体代码组包
//		 */
//		if (allPaydto.getFatherDtos() != null
//				&& allPaydto.getFatherDtos().size() > 0) {
//			setPayDataOnePackage(allPaydto, filekind);
//		}
//
//		// 3.由于实拨和退库的厦门接口分明细岗和资金岗，需要进行数据填充,
//		// 根据收款账号和金额进行关联
//		if (allmudto.getFatherDtos() != null
//				&& allmudto.getFatherDtos().size() > 0) {
//			for (IDto dto : allmudto.getFatherDtos()) {
//				if (dto instanceof TbsTvPayoutDto) { // 实拨资金XM
//					TbsTvPayoutDto pdto = (TbsTvPayoutDto) dto;
//					for (IDto dto1 : allmudto.getSonDtos()) {
//						TvPayoutfinanceDto fidto = (TvPayoutfinanceDto) dto1;
//						if (pdto.getSpayeeacct().trim().equals(
//								fidto.getSpayeeacct().trim())) {
//							if (pdto.getFamt().compareTo(fidto.getFamt()) == 0) {
//								pdto.setSpayeeopnbnkno(fidto
//										.getSpayeeopnbnkno()); // 填入资金岗的收款人开户行行号
//								pdto.setSpayeename(fidto.getSpayeename()); // 填入资金岗收款人名称
//								pdto.setSbdgorgname(fidto.getSpayeename()); // 填入预算单位名称
//								pdto.setSpayeebankno(fidto.getSrcvreckbnkno()); // 填入清算行行号
//								if ("".equals(pdto.getSbdgorgcode().trim())) { // 如果预算单位代码为空则填入
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
//				} else if (dto instanceof TbsTvDwbkDto) { // 退库XM
//					TbsTvDwbkDto dwdto = (TbsTvDwbkDto) dto;
//					for (IDto dto1 : allmudto.getSonDtos()) {
//						TvPayoutfinanceDto fidto = (TvPayoutfinanceDto) dto1;
//						if (dwdto.getSpayeeacct().trim().equals(
//								fidto.getSpayeeacct().trim())) {
//							if (dwdto.getFamt().compareTo(fidto.getFamt()) == 0) {
//								dwdto.setSpayeeopnbnkno(fidto
//										.getSpayeeopnbnkno()); // 填入资金岗的收款人开户行行号
//								dwdto.setSpayeename(fidto.getSpayeename()); // 填入资金岗收款人名称
//								li.add(dwdto);
//								break;
//							}
//						}
//					}
//				}
//			}
//			// 如果填充完之后的dto数目小于没有填充之前
//			if (allmudto.getFatherDtos().size() != li.size()) {
//
//				throw new ITFEBizException("通过明细岗付款人账号和金额未找到对应资金岗记录,请查证");
//
//			}
//
//			allmudto.setSonDtos(null); // 将不需要写入数据库的资金岗清空
//
//			if (filekind.equals(MsgConstant.SHIBO_XIAMEN_DAORU)) { // 厦门实拨
//
//				try {
//
//					allmudtoforxm = this.splitPackageWithBank(li, biztype); // 厦门实拨需按清算行分包
//
//				} catch (JAFDatabaseException e) {
//
//					log.error(e);
//
//					throw new ITFEBizException("数据解析异常", e);
//
//				} catch (ValidateException e) {
//
//					log.error(e);
//
//					throw new ITFEBizException("数据解析异常", e);
//
//				} catch (SequenceException e) {
//
//					log.error(e);
//
//					throw new ITFEBizException("数据解析异常", e);
//
//				}
//
//				this.createFileDto(allmudtoforxm, filekind); // 保存按清算行分包后的数据
//
//				return allmudtoforxm;
//
//			} else if (filekind.equals(MsgConstant.TUIKU_XIAMEN_DAORU)) { // 厦门退库
//
//				allmudto.setFatherDtos(li); // 将填充完数据的List放入
//
//				this.createFileDto(allmudto, filekind); // 保存数据
//
//				return allmudto;
//
//			}
//		}
//
//		return mudto;
//	}
//}
