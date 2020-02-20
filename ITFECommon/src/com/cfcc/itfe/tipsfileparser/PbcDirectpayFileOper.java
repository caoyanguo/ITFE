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
 * author hua 人行办理直接支付
 */
public class PbcDirectpayFileOper extends AbstractTipsFileOper {

	public MulitTableDto fileParser(String file, String bookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {
		try {
//			String userid = ((ITFELoginInfo)loginfo).getSuserCode();
			MulitTableDto multiDto = new MulitTableDto();			
			int fbcount = 0; // 分包记数器
			int recordNum = 1; // 记录记录器
			BigDecimal famtPack = new BigDecimal("0.00"); //分包设置的金额计数
			
			multiDto.setBiztype(biztype);//业务类型
			multiDto.setSbookorgcode(bookorgcode);//核算主体代码
			List<IDto> listdto = new ArrayList<IDto>(); //存放数据解析对象
			List<IDto> packdtos = new ArrayList<IDto>(); //存放分包对象
			List<IDto> fadtos = new ArrayList<IDto>(); //存放分包对象
			File fi = new File(file);
			
			String tmpPackNo = "";
			String trecode = "";
			// 增加判断区分获取密钥的方式
			String key = "";
			String encyptMode ="";
			List<String> voulist = new ArrayList<String>(); //存放凭证编号
			Set<String> bnkset = new HashSet<String>(); //存放收款人开户行			
			
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgcode); //法人代码缓存
			Map<String,TsPaybankDto> paymap = this.makeBankMap(); //支付行号缓存
			Map<String,TsConvertfinorgDto> finmap = this.makeFincMap(bookorgcode); //财政代码缓存
			
			List<String[]> fileContent = super.readFile(file, ","); //将文件按照‘,’分割开来
			
			for(int i = 0 ; i < fileContent.size() ; i++){
				String[] strs = fileContent.get(i);
				TbsTvPbcpayDto mainDto = new TbsTvPbcpayDto();
				trecode =strs[0];
				if(!this.checkTreasury(strs[0], bookorgcode)) {
					multiDto.getErrorList().add("人行办理直接支付文件["+fi.getName()+"] 凭证编号为["+strs[11].trim()+"]的记录中国库主体代码："+strs[0]+" 在'国库主体信息参数'中不存在!");
				}
				
				if(i == 0) {					
					CommonParamDto _dto = (CommonParamDto) paramdto;
					encyptMode =_dto.getEncryptMode();
					if ("SHANDONG".equals(_dto.getArea())) {
						key = this.findKeyForValidate("", ""); // 获得文件校验Key
					} else {// 按照传入的密钥设置模式取对应的密钥
						TsMankeyDto keydto= TipsFileDecrypt.findKeyByKeyMode(_dto.getKeyMode(),
								bookorgcode, strs[0]);
						if (null!=keydto) {
							key  =keydto.getSkey();
						}else{
							multiDto.getErrorList().add("人行办理直接支付文件[" + fi.getName()
									+ "没有查找到对应的解密密钥！");
							return multiDto;
						}
					}
					/**
					 * 首先判断是否为重复导入，如果是，则解析操作不进行
					 */
					String errorInfo = this.checkFileExsit(bookorgcode, trecode, fi.getName(), MsgConstant.MSG_NO_5104);
					if(null != errorInfo && errorInfo.length() > 0) {
						multiDto.getErrorList().add(errorInfo);
						return multiDto;
					}
					
					//按照文件验证SM3算法的正确性
					if(StateConstant.SM3_ENCRYPT.equals(encyptMode)){
						if(!SM3Process.verifySM3SignFile(file,key)){
							multiDto.getErrorList().add("人行办理直接支付文件[" + fi.getName()
									+ "]SM3签名验证失败!");
						 return multiDto;
						}
						fileContent.remove(fileContent.size() - 1);
					}
				}
				
				
				mainDto.setStrecode(strs[0]); // 国库代码
				mainDto.setSbiztype(biztype);// 业务类型
				mainDto.setSpayeracct(strs[1]); // 付款人帐号
				if(null == strs[2] || "".equals(strs[2])) {
					multiDto.getErrorList().add("人行办理直接支付文件 ["+fi.getName()+"] 凭证编号为["+strs[11].trim()+"]的记录中存在'付款人全称'为空!");					
				}else {
					mainDto.setSpayername(strs[2]);//付款人全称(单位代码)
				}				
				mainDto.setSpayeropnbnkno(strs[3]); // 付款人开户银行
				if(null == strs[4] || "".equals(strs[4])) {
					multiDto.getErrorList().add("人行办理直接支付文件 ["+fi.getName()+"] 凭证编号为["+strs[11].trim()+"]的记录中存在'收款人账号'为空!");
				}else {
					mainDto.setSpayeeacct(strs[4]); // 收款人帐号
				}				
				if(null == strs[5] || "".equals(strs[5])) {
					multiDto.getErrorList().add("人行办理直接支付文件 ["+fi.getName()+"] 凭证编号为["+strs[11].trim()+"]的记录中存在'收款人全称'为空!");
				}else {
					mainDto.setSpayeename(strs[5]); // 收款人全称
				}									
				if(strs[6] == null || "".equals(strs[6].trim())) {
					multiDto.getErrorList().add("人行办理直接支付文件 ["+fi.getName()+"] 凭证编号为["+strs[11].trim()+"]的记录中存在'收款单位开户行'为空!");
				}
				TsPaybankDto bnkpaydto = paymap.get(strs[6].trim());
				if(null == bnkpaydto) {
					multiDto.getErrorList().add("人行办理直接支付文件 ["+fi.getName()+"] 凭证编号为["+strs[11].trim()+"]的记录根据收款单位开户行：'"+strs[6].trim()+"'没有在'支付行号查询参数' 中找到相应清算行行号!");
				}else {
					if(null != bnkpaydto.getSstate() && "0".equals(bnkpaydto.getSstate())) {
						multiDto.getErrorList().add("人行办理直接支付文件 ["+fi.getName()+"] 凭证编号为["+strs[11].trim()+"]的记录中收款单位开户行：'"+strs[6].trim()+"' 处于'生效前'状态!");
					}else if (null != bnkpaydto.getSstate() && "2".equals(bnkpaydto.getSstate())) {
						multiDto.getErrorList().add("人行办理直接支付文件 ["+fi.getName()+"] 凭证编号为["+strs[11].trim()+"]的记录中收款单位开户行：'"+strs[6].trim()+"' 处于'注销'状态!");
					}
				}
				
				bnkset.add(strs[6].trim()); 
				mainDto.setSpayeeopnbnkno(strs[6].trim()); // 收款人开户行	
				if(null == strs[7] || "".equals(strs[7])) {
					mainDto.setCbdgkind("1"); //为空则默认为1--预算内
				}else {
					mainDto.setCbdgkind(strs[7]); // 预算种类代码
				}
				
				String err = this.verifySubject(bmap, strs[8], MsgConstant.MSG_NO_5104, "1", fi.getName(),strs[11]);
				if(null != err && !"".equals(err)) {
					multiDto.getErrorList().add(err);
				}
				mainDto.setSfuncsbtcode(strs[8]); //功能科目代码
				if(null != strs[9] && !"".equals(strs[9])) { //不为空的情况下去判断
					err = this.verifySubject(bmap, strs[9], MsgConstant.MSG_NO_5104, "2", fi.getName(),strs[11]);
					if(null != err && !"".equals(err)) {
						multiDto.getErrorList().add(err);
					}
					mainDto.setSecosbtcode(strs[9]); //经济科目代码
				}	
				mainDto.setSbdgorgcode(strs[10]); //预算单位代码
				if(!rpmap.containsKey(strs[0]+mainDto.getSbdgorgcode().trim())) {
					multiDto.getErrorList().add("人行办理直接支付文件["+fi.getName()+"] 凭证编号为["+strs[11].trim()+"]的记录中预算单位代码 '"+mainDto.getSbdgorgcode()+"' 没有在法人代码表中找到!");
				}				
				mainDto.setSvouno(strs[11]); //凭证编号
				voulist.add(trecode+","+strs[11]); 
				mainDto.setDvoucher(CommonUtil.strToDate(strs[12])); //凭证日期
				mainDto.setSbackflag(strs[13]); //退回标志
				mainDto.setFamt(new BigDecimal(strs[14])); //金额
				
				/**湖南长沙要求在接口上加上附言 20140916**/
				if(strs.length>15 && strs[15]!=null && !"".equals(strs[15])) {
					mainDto.setSaddword(strs[15]);
				}
				
				mainDto.setSpackageno(tmpPackNo);  //包流水号
				mainDto.setSfilename(fi.getName()); //文件名
				mainDto.setSstatus(StateConstant.CONFIRMSTATE_NO); //交易状态
				mainDto.setSbookorgcode(bookorgcode);
				mainDto.setCtrimflag(StateConstant.TRIMSIGN_FLAG_NORMAL); //调整期标志默认为 0--正常期
				mainDto.setDaccept(TimeFacade.getCurrentDateTime()); //受理日期不为空(默认当前日期)
				listdto.add(mainDto);
			}
			
			Map<String, List<IDto>> bnkli = new HashMap<String, List<IDto>>();
			//按照清算行行号进行分包
			  //1.首先得到清算行的数量
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
			
			//2.如果某收款开户行下的明细DTO的数量大于TIPS分包限制,则进行再分包
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
						//设置包流水号
						bkdto.setSpackageno(tmpPackNo);
						//进行金额统计，用于分包
						famtPack = famtPack.add(bkdto.getFamt());
						//进行数量的统计
						fbcount ++;
						//将所有填充数据完成的DTO保存
						fadtos.add(bkdto);
					}
					TvFilepackagerefDto packdto = new TvFilepackagerefDto();
					packdto.setSorgcode(bookorgcode);
					packdto.setStrecode(trecode);
					packdto.setSfilename(fi.getName());
					TsConvertfinorgDto gd = finmap.get(trecode);
					if(null == gd) {
						multiDto.getErrorList().add("人行办理直接支付文件["+fi.getName()+"] 中根据核算主体代码："+bookorgcode+"、 国库主体代码："+trecode+" 没有在 '财政机构信息维护参数'中找到对应财政机构代码!");
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
			
			//校验文件中凭证编号是否重复
			int oldSize = 0;
			int newSize = 0;
			Set<String> sets = new HashSet<String>();
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					multiDto.getErrorList().add("人行办理直接支付文件["+fi.getName()+"]中存在凭证编号重复");
				}
			}
			
			multiDto.setFatherDtos(fadtos);
			multiDto.setPackDtos(packdtos);
			multiDto.setVoulist(voulist);
			multiDto.setFamt(famtPack);  //总金额
			multiDto.setTotalCount(recordNum); //总笔数
			return multiDto;
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("人行办理直接支付文件解析出错 \n"+e.getMessage(), e);
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
