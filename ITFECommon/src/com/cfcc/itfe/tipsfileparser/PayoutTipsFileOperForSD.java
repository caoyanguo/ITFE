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

import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.CommonParamDto;
import com.cfcc.itfe.persistence.dto.TbsTvPayoutDto;
import com.cfcc.itfe.persistence.dto.TdBookacctMainDto;
import com.cfcc.itfe.persistence.dto.TdCorpDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsGenbankandreckbankDto;
import com.cfcc.itfe.persistence.dto.TsInfoconnorgaccDto;
import com.cfcc.itfe.persistence.dto.TsMankeyDto;
import com.cfcc.itfe.persistence.dto.TsOrganDto;
import com.cfcc.itfe.persistence.dto.TsPaybankDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.itfe.security.SM3Process;
import com.cfcc.itfe.security.TipsFileDecrypt;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.itfe.verify.VerifyParamTrans;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * author hua 实拨资金
 */
public class PayoutTipsFileOperForSD extends AbstractTipsFileOper {
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.cfcc.tas.service.biz.ITipsFileOper#fileParser(java.lang.String)
	 */
	public MulitTableDto fileParser(String file, String bookorgCode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> dmap) throws ITFEBizException {
		try {
			int fbcount = 0; // 分包记数器
			int recordNum = 1; // 记录记录器
			String tmpPackNo = "";

			String fname = new File(file).getName();
			String ctrimflag = fname.substring(fname.length() - 5, fname
					.length() - 4); // 调整期标志
			String trecode = "";

			MulitTableDto multi = new MulitTableDto();
			multi.setBiztype(biztype);// 业务类型
			multi.setSbookorgcode(bookorgCode);// 核算主体代码
			BigDecimal famt = new BigDecimal("0.00");
			BigDecimal famtPack = new BigDecimal("0.00");

			List<IDto> listdto = new ArrayList<IDto>();
			List<IDto> packdtos = new ArrayList<IDto>();
			List<IDto> fadtos = new ArrayList<IDto>();
			File fi = new File(file);
			List<String> voulist = new ArrayList<String>();
			Set<String> bnkset = new HashSet<String>();
			Set<String> treset = new HashSet<String>();

			Map<String, TsPaybankDto> paymap = this.makeBankMap(); // 支付行号缓存
			Map<String,TdCorpDto> rpmap = this.verifyCorpcode(bookorgCode); //法人代码缓存
			Map<String,TsInfoconnorgaccDto> bookacctMap= this.getBookAcctMap(bookorgCode);//付款人账户信息
			Map<String,TsOrganDto> bookorginfo= this.getOrganInfo();//获取核算主体信息
			Map<String,TdBookacctMainDto> finTreAcctInfo= this.getFinTreAcctFromBookAcctInfo();//获取财政库款账户信息
			
			// 增加判断区分获取密钥的方式
			String key = "";
			String encyptMode ="";

			// 文件解析
			List<String[]> fileContent = super.readFile(file, ",");
			
			// 取得是否启用经济科目代码
			boolean isEcnomic = false;
			if(fileContent.size()>0){
				String[] data = fileContent.get(0);
				TsTreasuryDto treasurydto = new TsTreasuryDto();
				treasurydto.setStrecode(data[0]);
				List<TsTreasuryDto> dot = CommonFacade.getODB().findRsByDtoWithUR(treasurydto);
				if (dot.size()==1) {
					if(StateConstant.COMMON_YES.equals(dot.get(0).getSisuniontre())){
						isEcnomic = true;
					}
				}else{
					multi.getErrorList().add("国库代码[" +data[0]+ fi.getName()
							+ "基础参数错误！");
					return multi;
				}
			}
			CommonParamDto _dto = (CommonParamDto) paramdto;
			encyptMode =_dto.getEncryptMode();
			int record =fileContent.size();
			if(StateConstant.SM3_ENCRYPT.equals(encyptMode)&& record >1){
				record =record -1;
			}
			for (int i = 0; i < record; i++) {
				
				String[] singDto = fileContent.get(i);
				TbsTvPayoutDto payoutDto = new TbsTvPayoutDto();
				payoutDto.setStrecode(singDto[0]); // 国库代码
				//在第0行获取一次密钥，根据不同情况获取密钥
				if (i == 0) {
					if ("SHANDONG".equals(_dto.getArea())) {
						key = this.findKeyForValidate("", ""); // 获得文件校验Key
					} else {// 按照传入的密钥设置模式取对应的密钥
						TsMankeyDto keydto= TipsFileDecrypt.findKeyByKeyMode(_dto.getKeyMode(),
								bookorgCode, singDto[0]);
						if (null!=keydto) {
							key  =keydto.getSkey();
							
						}else{
							multi.getErrorList().add("实拨资金文件[" + fi.getName()
									+ "没有查找到对应的解密密钥！");
							return multi;
						}
					}
				}
				// 不加密的需要进行验证，进行文件校验
				if (StateConstant.NO_ENCRYPT.equals(encyptMode)) {
					if (!this.importValidator(singDto[12], singDto[7], singDto[5],
							new BigDecimal(singDto[11]), singDto[singDto.length-1], key)) {
						multi.getErrorList().add("实拨资金文件[" + fi.getName()
								+ "]中第 " + (i + 1) + " 行校验码验证不符!");
					}
				}
				if(StateConstant.SD_ENCRYPT.equals(encyptMode)){
					if(!SM3Process.verifySM3Sign(StringUtils.join(singDto,","), key)){
						multi.getErrorList().add("实拨资金文件[" + fi.getName()
								+ "]中第 " + (i + 1) + " 行数字签名校验失败!");
					}
				}
			
				
				trecode = singDto[0].trim();
				if(i == 0) {
					//校验文件重复,国库代码+文件名
					String errorInfo = this.checkFileExsit(bookorgCode, trecode, fi.getName(), MsgConstant.MSG_NO_5101);
					if(null != errorInfo && errorInfo.length() > 0) {
						multi.getErrorList().add(errorInfo);
						return multi;
					}
					//按照文件验证SM3算法的正确性
					if(StateConstant.SM3_ENCRYPT.equals(encyptMode)){
						if(!SM3Process.verifySM3SignFile(file,key)){
							multi.getErrorList().add("实拨资金文件[" + fi.getName()
									+ "]SM3签名验证失败!");
						 return multi;
						}
					}
				}		
				if(!this.checkTreasury(trecode, bookorgCode)) {
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录中国库主体代码："+trecode+" 没有在'国库主体信息参数'中查找到!");
				}
				treset.add(trecode);
				payoutDto.setSpayeracct(singDto[1]); // 付款人账号
				
				//通过国库代码和付款人账号 找到对应付款人名称
				if (bookacctMap.containsKey(trecode+singDto[1].trim())) {
					TsInfoconnorgaccDto tndto = bookacctMap.get(trecode+singDto[1].trim());
					payoutDto.setSpayername(tndto.getSpayername());
				}else{
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录中根据国库代码："+trecode+" , 付款人账号："+singDto[1].trim()+" 没有在'资金拨付付款人账户维护'参数中找到对应付款人名称，请先维护");
				}
				payoutDto.setSmovefundreason(singDto[2]); // 支出原因代码或者调拨原因代码
				payoutDto.setSbdgorgcode(singDto[3]); // 预算单位代码
				//修改为按照国库代码+预算单位唯一
				if(!rpmap.containsKey(trecode+payoutDto.getSbdgorgcode().trim())) {
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录中预算单位代码 '"+payoutDto.getSbdgorgcode()+"' 没有在法人代码表中找到!");
				}else {
					if(!"1".equals(rpmap.get(trecode+payoutDto.getSbdgorgcode()).getCmayaprtfund().trim())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录中预算单位代码 '"+payoutDto.getSbdgorgcode()+"' 对应预算单位不能进行实拨资金!");
					}
				}
				payoutDto.setSbdgorgname(singDto[4]); //预算单位名称	
				if(VerifyParamTrans.isNull(singDto[4])){//预算单位代码不能为空
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录中预算单位名称不能为空!");
				}
				payoutDto.setSpayeeacct(singDto[5]); // 收款单位账号
				if(VerifyParamTrans.isNull(singDto[5])){//收款单位账号不能为空
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录中收款单位账号不能为空!");
				}
				
				payoutDto.setCbdgkind(singDto[6]); // 预算种类
				String err = this.verifySubject(dmap, singDto[7], MsgConstant.MSG_NO_5101, "1", fi.getName(),singDto[12]);
				if(!"".equals(err)) {
					multi.getErrorList().add(err);
				}
				payoutDto.setSfuncsbtcode(singDto[7]); // 功能科目代码				
				payoutDto.setSecosbtcode(singDto[8]); // 经济科目代码
				//校验经济科目代码
				if(!isEcnomic){//没有启用经济科目代码
					payoutDto.setSecosbtcode(""); // 经济科目代码
				}else{//启用经济代码的话，经济代码必须不为空
					if(null ==singDto[8]||"".equals(singDto[8].trim())){//经济科目代码不为空
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录在启用经济代码的情况下，经济科目代码没有填写!");
					}
				}	
				payoutDto.setSbooksbtcode(singDto[9]); // 会计科目代码 (可为空)
				payoutDto.setSpaybizkind(singDto[10]); // 支出种类
				payoutDto.setFamt(new BigDecimal(singDto[11])); // 发生额   数值转换方法
				payoutDto.setSvouno(singDto[12]); // 凭证编号
				if(VerifyParamTrans.isNull(singDto[12])){//凭证编号不能为空
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号不能为空!");
				}
				voulist.add(trecode.trim()+","+singDto[12].trim());
				payoutDto.setSpayeename(singDto[13]); //收款人名称
				
				//转换开户行大类(山东)
				String spayeebnkno=singDto[14].trim();
				if (null==spayeebnkno || "".equals(spayeebnkno) ) {
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录收款人开户行行号 不能为空!");
				}else if(spayeebnkno.length()<=8 && spayeebnkno.length()>2){
					TsGenbankandreckbankDto bankdto = new TsGenbankandreckbankDto();
					bankdto.setSgenbankcode(spayeebnkno);
					bankdto.setSbookorgcode(bookorgCode);
					List dot = CommonFacade.getODB().findRsByDtoWithUR(bankdto);
					if (dot == null || dot.size() == 0) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录根据收款人开户行行号：'"+singDto[14].trim()+"'没有在'银行代码与支付行号对应关系参数' 中找到相应对照关系，请先维护");
					}else {
						TsGenbankandreckbankDto bdto = (TsGenbankandreckbankDto)dot.get(0);
						spayeebnkno = bdto.getSreckbankcode();
					}					
				}				
				payoutDto.setSpayeeopnbnkno(spayeebnkno); //收款人开户行行号（开户行大类）
				
				/**
				 * 9月修改，判断如果字段数目为16，则将最后一位填入附言字段，否则忽略
				 */
				if(singDto.length == 16) {
					if(null != singDto[15] && !"".equals(singDto[15])) {
						if (singDto[15].getBytes().length>60) {
							multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录中附言长度超长：附言长度最大支持60个字符或30个汉字!");
						}
						payoutDto.setSaddword(singDto[15]);
						
					}
				}							
				//根据转换过来的开户行行号找到对应的清算行行号
				TsPaybankDto bnkpaydto = paymap.get(spayeebnkno);
				if(null == bnkpaydto) {
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录中根据收款人开户行行号：'"+spayeebnkno+"'没有在'支付行号查询参数' 中找到相应清算行行号!");
				}else {
					if(null != bnkpaydto.getSstate() && "0".equals(bnkpaydto.getSstate())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录中收款单位开户行：'"+spayeebnkno+"' 处于'生效前'状态!");
					}else if (null != bnkpaydto.getSstate() && "2".equals(bnkpaydto.getSstate())) {
						multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 凭证编号为["+singDto[12].trim()+"]的记录中收款单位开户行：'"+spayeebnkno+"' 处于'注销'状态!");
					}
					//说明是系统内调拨支出，需要判断账户的合法性
					if (StateConstant.TCBSCENTERRECKBANKNO.equals(bnkpaydto.getSpaybankno())) {
						//根据行号找到对应的核算主体
						if (bookorginfo.containsKey(spayeebnkno)) {
							String bookorg =bookorginfo.get(spayeebnkno).getSorgcode();
							if (!finTreAcctInfo.containsKey(bookorg+singDto[5])) {
								payoutDto.setSgroupid(StateConstant.COMMON_YES);//需要主管授权的标示
							 }
						}else{//找不到核算主体说明可能是省外调拨，需要主管授权
							payoutDto.setSgroupid(StateConstant.COMMON_YES);	
						}
					}
				}							
				/*
				 * 分包，此代码5.3修改成按开户行分包
				payoutDto.setSpayeebankno(bnkpaydto.getSpaybankno());
				bnkset.add(bnkpaydto.getSpaybankno()); // 根据清算行分包
				*/
				payoutDto.setSpayeebankno(spayeebnkno); //填写成开户行
				bnkset.add(spayeebnkno); // 根据开户行分包
				
				payoutDto.setSbookorgcode(bookorgCode);
				famt = famt.add(new BigDecimal(singDto[11]));
				payoutDto.setDvoucher(CommonUtil.strToDate(fi.getName().substring(0, 8))); //凭证日期
				payoutDto.setIofyear(Integer.parseInt(fi.getName().substring(0, 4))); //所属年度
				payoutDto.setSbookorgcode(bookorgCode);// 核算主体代码
				payoutDto.setDaccept(TimeFacade.getCurrentDateTime());
				if(biztype != null && !"".equals(biztype)) {
					payoutDto.setSbiztype(biztype);// 业务类型
				}else {
					payoutDto.setSbiztype("");// 业务类型
				}
				payoutDto.setCtrimflag(ctrimflag);
				
				payoutDto.setSpackageno(tmpPackNo);  //包流水号
				payoutDto.setSfilename(fi.getName());
				payoutDto.setSstatus(StateConstant.CONFIRMSTATE_NO);
				listdto.add(payoutDto);
				recordNum++;
			}
			
			Map<String,Map<String,List<IDto>>> treli = new HashMap<String,Map<String,List<IDto>>>();
			/**
			 * 为适应江苏地方横连的文件多国库的情况增加分包规则：加上国库代码  2013-03-25  by hua 
			 */
			//1、首先按照国库代码来分包
			for(String treC : treset) {
				Map<String ,List<IDto>> tmpMap = new HashMap<String ,List<IDto>>();
				//2、按照收款行分开
				for(String st : bnkset) {
					List<IDto> nlist = new ArrayList<IDto>();
					for (IDto dto : listdto) {
						TbsTvPayoutDto bkdto = (TbsTvPayoutDto) dto;
						if (treC.trim().equals(bkdto.getStrecode()) && st.equals(bkdto.getSpayeeopnbnkno())) {
							nlist.add(bkdto);
						}
					}
					if(nlist.size() > 0) {
						tmpMap.put(st, nlist);
					}					
				}
				treli.put(treC, tmpMap);
			}
			
			//3、开始 正式分包
			for(String treC : treset) {
				Map<String ,List<IDto>> bnkli = treli.get(treC); //得到相应国库的记录
				//2.如果某清算行下的明细DTO的数量大于TIPS分包限制,则进行再分包
				for (String st : bnkset) {
					List<IDto> detlist = bnkli.get(st);
					if(null == detlist || detlist.size() == 0) {
						continue;
					}
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
							TbsTvPayoutDto bkdto = (TbsTvPayoutDto) detlist.get(j);
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
						packdto.setSorgcode(bookorgCode);
						packdto.setStrecode(treC);
						packdto.setSfilename(fi.getName());
						TsConvertfinorgDto condto = new TsConvertfinorgDto();
						condto.setSorgcode(bookorgCode);
						condto.setStrecode(trecode);
						List conlist = CommonFacade.getODB().findRsByDtoWithUR(condto);
						if(conlist == null || conlist.size() == 0 ){
							multi.getErrorList().add("实拨资金文件["+fi.getName()+"] 中根据核算主体代码："+bookorgCode+"、 国库主体代码："+trecode+" 没有在 '财政机构信息维护参数'中找到对应财政机构代码!");
						}else {
							TsConvertfinorgDto gd = (TsConvertfinorgDto)conlist.get(0);
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
			}
					
			//文件中凭证编号校验
			int oldSize = 0;
			int newSize = 0;
			Set<String> sets = new HashSet<String>();
			for (String item : voulist) {
				oldSize = sets.size();
				sets.add(item.trim());
				newSize = sets.size();
				if (newSize == oldSize) {
					multi.getErrorList().add("实拨资金文件["+fi.getName()+"]中存在凭证编号 "+item.trim()+" 重复");
				}
			}
			
			multi.setFatherDtos(listdto);
			multi.setPackDtos(packdtos);
			multi.setVoulist(voulist);
			multi.setFamt(famt); //总金额
			multi.setTotalCount(recordNum); //总笔数
			return multi;
			
		} catch (Exception e) {
			log.error(e);
			throw new ITFEBizException("实拨资金文件解析出错 \n"+e.getMessage(), e);
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
