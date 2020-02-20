///*
// * 创建日期 2007-10-23
// *
// * 
// */
//package com.cfcc.tips.ties.action.actionhelper;
//
//import java.sql.Date;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//
//import com.cfcc.jaf.core.service.filetransfer.FileTransferException;
//import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
//import com.cfcc.tips.common.exception.CAException;
//import com.cfcc.tips.common.exception.FileOperateException;
//import com.cfcc.tips.common.exception.SequenceException;
//import com.cfcc.tips.common.exception.TipsException;
//import com.cfcc.tips.common.exception.XMLException;
//import com.cfcc.tips.common.util.DateUtil;
//import com.cfcc.tips.mto.common.MtoFactory;
//import com.cfcc.tips.mto.v10.common.HEAD;
//import com.cfcc.tips.persistency.constant.AllStatConstants;
//import com.cfcc.tips.persistency.constant.AllTypeConstants;
//import com.cfcc.tips.persistency.constant.FileExtNameConstant;
//import com.cfcc.tips.persistency.constant.MsgConstant;
//import com.cfcc.tips.persistency.constant.TipsCommonConstant;
//import com.cfcc.tips.persistency.facade.commonfacade.BatchRetrieverHelper;
//import com.cfcc.tips.persistency.facade.commonfacade.CommonBeanHelper;
//import com.cfcc.tips.persistency.facade.commonfacade.DatabaseFacade;
//import com.cfcc.tips.persistency.ormap.dto.TdConNodeDto;
//import com.cfcc.tips.persistency.ormap.dto.TdImposeOrgDto;
//import com.cfcc.tips.persistency.ormap.dto.TdTreasuryDto;
//import com.cfcc.tips.persistency.ormap.dto.TrBudgetIncomeDto;
//import com.cfcc.tips.persistency.ormap.dto.TrDrawbackDto;
//import com.cfcc.tips.persistency.ormap.dto.TrMoveIncomeDto;
//import com.cfcc.tips.persistency.ormap.dto.TrShareAllotDto;
//import com.cfcc.tips.persistency.ormap.dto.TrStockDto;
//import com.cfcc.tips.persistency.ormap.dto.TrTotalAllotDto;
//import com.cfcc.tips.persistency.ormap.dto.TraBudgetIncomeDto;
//import com.cfcc.tips.persistency.ormap.dto.TraDrawbackDto;
//import com.cfcc.tips.persistency.ormap.dto.TraMoveIncomeDto;
//import com.cfcc.tips.persistency.ormap.dto.TraShareAllotDto;
//import com.cfcc.tips.persistency.ormap.dto.TsSysStatusDto;
//import com.cfcc.tips.persistency.ormap.pk.TdConNodePK;
//import com.cfcc.tips.persistency.ormap.pk.TdImposeOrgPK;
//import com.cfcc.tips.persistency.ormap.pk.TdTreasuryPK;
//import com.cfcc.tips.ties.common.exception.ActionException;
//import com.cfcc.tipsmof.mto.v10.common.types.BudgetLevelCode;
//import com.cfcc.tipsmof.mto.v10.common.types.BudgetType;
//import com.cfcc.tipsmof.mto.v10.mto3128.Amount3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.AmountBill3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.BillHead3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.CFX3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.MSG3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.NrBudget3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.NrBudgetBill3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.NrDrawBack3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.NrDrawBackBill3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.NrRemove3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.NrRemoveBill3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.NrShare3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.NrShareBill3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.Stock3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.StockBill3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.TrBudget3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.TrBudgetBill3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.TrDrawBack3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.TrDrawBackBill3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.TrRemove3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.TrRemoveBill3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.TrShare3128;
//import com.cfcc.tipsmof.mto.v10.mto3128.TrShareBill3128;
//import com.cfcc.tipsmof.mto.v10.mto5001.CFX5001;
//import com.cfcc.tipsmof.mto.v10.mto5001.MSG5001;
//import com.cfcc.tipsmof.mto.v10.mto5001.RequestHead5001;
//import com.cfcc.tipsmof.mto.v10.mto5001.RequestReport5001;
//
///**
// * 生成3128
// * @author 刘敏
// *
// * 
// */
//public class CFX3128Helper {
//	private static Log log = LogFactory.getLog(CFX3128Helper.class);
//	
//	
//	public static List send3128(String s_TaxOrgCode,Date d_ChkDate,String nodeCode, String treCode, Date workdate)throws SequenceException,TipsException,JAFDatabaseException,XMLException,CAException,FileOperateException,FileTransferException{
//		TsSysStatusDto dateDto = new TsSysStatusDto();
//		dateDto.setDWorkdate(workdate);
//		
//		CFX5001 mto5001 = new CFX5001();
//      
//        HEAD head5001 = new HEAD();
//        mto5001.setHEAD(head5001);
//        head5001.setAPP(MsgConstant.MSG_HEAD_APP);
//        head5001.setVER(MsgConstant.MSG_VER_01);
//        head5001.setMsgNo(MsgConstant.MSG_TYPE_5001);
//        head5001.setSRC(nodeCode);
//        head5001.setDES(TipsCommonConstant.TIPS_NODE_CODE);
//        head5001.setMsgID("20050829192214000011");
//        head5001.setMsgRef(mto5001.getHEAD().getMsgID());
//        Date sqlWorkDate = dateDto.getDWorkdate();
//        head5001.setWorkDate(DateUtil.utilDate2CastorDate(sqlWorkDate));
//
//        MSG5001 msg5001 = new MSG5001();
//        mto5001.setMSG(msg5001);
//        
//        RequestHead5001 rh5001 = new RequestHead5001();
//        rh5001.setReportDate(DateUtil.utilDate2CastorDate(d_ChkDate));
//        rh5001.setSendOrgCode(s_TaxOrgCode);
//        
//        RequestReport5001 rr5001 = new RequestReport5001();
//        rr5001.setAmount(AllStatConstants.REPORT_DWONLOAD_YES);
//        rr5001.setNrBudget(AllStatConstants.REPORT_DWONLOAD_YES);
//        rr5001.setNrDrawBack(AllStatConstants.REPORT_DWONLOAD_YES);
//        rr5001.setNrRemove(AllStatConstants.REPORT_DWONLOAD_YES);
//        rr5001.setNrShare(AllStatConstants.REPORT_DWONLOAD_YES);
//        rr5001.setStock(AllStatConstants.REPORT_DWONLOAD_YES);
//        rr5001.setTrBudget(AllStatConstants.REPORT_DWONLOAD_YES);
//        rr5001.setTrDrawBack(AllStatConstants.REPORT_DWONLOAD_YES);
//        rr5001.setTrRemove(AllStatConstants.REPORT_DWONLOAD_YES);
//        rr5001.setTrShare(AllStatConstants.REPORT_DWONLOAD_YES);
//        
//        
//        msg5001.setRequestHead5001(rh5001);
//        msg5001.setRequestReport5001(rr5001);
//        
//		
//        return gen3128(mto5001,dateDto);
//	}
//	/**
//     * 生成3128报文
//     * @param contex
//     * @param mto5001
//     * @param dateDto
//     * @throws ActionException
//     */
//    public static List gen3128(CFX5001 mto5001,
//            TsSysStatusDto dateDto) throws SequenceException,TipsException,JAFDatabaseException,XMLException,CAException,FileOperateException,FileTransferException {
//
//        TdImposeOrgDto tdImposeOrgDto = null;
//        TdTreasuryDto tdTreasuryDto = null;
//
//            String srcNodeCode = mto5001.getHEAD().getSRC();
//            TdConNodePK conNodePk = new TdConNodePK();
//            conNodePk.setSNodecode(srcNodeCode);
//            TdConNodeDto srcNodeDto = (TdConNodeDto) DatabaseFacade.getOdb().find(conNodePk);
//            srcNodeDto.setCOfnodetype(AllTypeConstants.NODE_TYPE_TREASURY);
//            
//            List orgList = new ArrayList();;
//            
//            TdImposeOrgPK pk = new TdImposeOrgPK();
//            pk.setSTaxorgcode(mto5001.getMSG().getRequestHead5001().getSendOrgCode());
//            tdImposeOrgDto = (TdImposeOrgDto) DatabaseFacade.getOdb().find(pk);
//            TdTreasuryPK  tpk = new TdTreasuryPK();
//            tpk.setSTrecode(tdImposeOrgDto.getSSamelvtrecode());
//            tdTreasuryDto = (TdTreasuryDto) DatabaseFacade.getOdb().find(tpk);
//            
//            if(tdTreasuryDto != null){
//            	orgList.add(tdTreasuryDto);
//            }else{
//            	throw new TipsException("国库代码不存在");
//            }
//          
//            boolean isNull = true;
//            
//
//            // 获得新的3128报文流水号CommonBeanHelper.getNextMsgID(MsgConstant.MSG_TYPE_3128,dateDto.getDWorkdate())
//            String msgID = CommonBeanHelper.getNextMsgID(
//                    MsgConstant.MSG_TYPE_3128, dateDto.getDWorkdate());
//            //		4. 根据3128报文填制规则生成3128报文头
//            CFX3128 mto3128 = new CFX3128();
//            
//            HEAD head3128 = new HEAD();
//            mto3128.setHEAD(head3128);
//            head3128.setAPP(MsgConstant.MSG_HEAD_APP);
//            head3128.setVER(MsgConstant.MSG_VER_01);
//            head3128.setMsgNo(MsgConstant.MSG_TYPE_3128);
//            //		SRC = TIPS的节点代码
//            head3128.setSRC(TipsCommonConstant.TIPS_NODE_CODE);
//            //		DES = 财政机关代码对应的节点代码（步骤1获得）
//            head3128.setDES(mto5001.getHEAD().getSRC());
//            //		MsgID = TIPS重新编制（步骤2获得）
//            head3128.setMsgID(msgID);
//            //		MsgRef = 同原始的5001的MsgRef
//            head3128.setMsgRef(mto5001.getHEAD().getMsgRef());
//            //		WorkDate = 当前工作日期，在doAction中获得
//            Date sqlWorkDate = dateDto.getDWorkdate();
//            head3128.setWorkDate(DateUtil.utilDate2CastorDate(sqlWorkDate));
//
//            MSG3128 msg3128 = new MSG3128();
//            mto3128.setMSG(msg3128);
//            
//            BillHead3128 billHead3128 = new BillHead3128();
//            //modified by wangyg 2008-02-15 finOrgCode
//            billHead3128.setFinOrgCode(mto5001.getMSG().getRequestHead5001().getSendOrgCode());
//            billHead3128.setReportDate(mto5001.getMSG().getRequestHead5001().getReportDate());
//            mto3128.getMSG().setBillHead3128(billHead3128);
//            
//            //String orgCode = mto5001.getMSG().getRequestHead5001().getSendOrgCode();
//            TdImposeOrgPK tdImposeOrgPK = new TdImposeOrgPK();
//            tdImposeOrgPK.setSTaxorgcode(mto5001.getMSG().getRequestHead5001().getSendOrgCode());
//            
//            TdImposeOrgDto tdDto= (TdImposeOrgDto)DatabaseFacade.getOdb().find(tdImposeOrgPK);
//            
//            String orgCode = tdDto.getCTaxorgtype();
//			
//            //if(OtherConstants.DOWNFLAG_NEED.equals(downloadTempleteDto.getCBudincome()) && OtherConstants.DOWNFLAG_NEED.equals(mto5001.getMSG().getRequestReport5001().getNrBudget().toString())){
//            //正常期预算收入报表
//            boolean haveRight = false;
//            if(mto5001.getMSG().getRequestReport5001().getNrBudget().toString().equals(AllStatConstants.REPORT_DWONLOAD_YES))
//            {
//                	NrBudgetBill3128[] array = createNrBudget3128Report(mto5001, srcNodeDto.getCOfnodetype(), dateDto.getDWorkdate(), orgList);
//                	
//                	if(array != null && array.length >0){
//                		NrBudget3128 nrBudget3128 = new NrBudget3128();
//                        msg3128.setNrBudget3128(nrBudget3128);
//                    	nrBudget3128.setNrBudgetBill3128(array);
//                		isNull = false;
//                	}
//                	haveRight = true;
//            }
//            
//            
//            //if(OtherConstants.DOWNFLAG_NEED.equals(downloadTempleteDto.getCDrawback()) && OtherConstants.DOWNFLAG_NEED.equals(mto5001.getMSG().getRequestReport5001().getNrDrawBack().toString())){
//            if(mto5001.getMSG().getRequestReport5001().getNrDrawBack().toString().equals(AllStatConstants.REPORT_DWONLOAD_YES))
//            {
//
//                	NrDrawBackBill3128[] array = createNrDrawBack3128Report(mto5001, srcNodeDto.getCOfnodetype(), dateDto.getDWorkdate(), orgList);
//                	
//                	if(array != null && array.length >0){
//                		NrDrawBack3128 nrDrawBack3128 = new NrDrawBack3128();
//                        msg3128.setNrDrawBack3128(nrDrawBack3128);
//                        nrDrawBack3128.setNrDrawBackBill3128(array);
//                		isNull = false;
//                	}
//                	haveRight = true;
//                
//            }            
//            
//            //if(OtherConstants.DOWNFLAG_NEED.equals(downloadTempleteDto.getCMoveincome()) && OtherConstants.DOWNFLAG_NEED.equals(mto5001.getMSG().getRequestReport5001().getNrRemove().toString())){
//            if(mto5001.getMSG().getRequestReport5001().getNrRemove().toString().equals(AllStatConstants.REPORT_DWONLOAD_YES))
//            {
//
//                	NrRemoveBill3128[] array = createNrRemove3128Report(mto5001, srcNodeDto.getCOfnodetype(), dateDto.getDWorkdate(), orgList);
//                	
//                	if(array != null && array.length >0){
//                		NrRemove3128 nrRemove3128 = new NrRemove3128();
//                        msg3128.setNrRemove3128(nrRemove3128);
//                        nrRemove3128.setNrRemoveBill3128(array);
//                		isNull = false;
//                	}
//                	haveRight = true;
//                
//            }            
//            
//            //if(OtherConstants.DOWNFLAG_NEED.equals(downloadTempleteDto.getCTotalallot()) && OtherConstants.DOWNFLAG_NEED.equals(mto5001.getMSG().getRequestReport5001().getAmount().toString())){
//            if(mto5001.getMSG().getRequestReport5001().getAmount().toString().equals(AllStatConstants.REPORT_DWONLOAD_YES))
//            {
//
//                	AmountBill3128[] array = createAmount3128Report(mto5001, dateDto.getDWorkdate(), orgList);
//                	
//                	if(array != null && array.length >0){
//                		Amount3128 amount3128 = new Amount3128();
//                        msg3128.setAmount3128(amount3128);
//                        amount3128.setAmountBill3128(array);
//                		isNull = false;
//                	}
//                	haveRight = true;
//                
//            }
//            
//            //if(OtherConstants.DOWNFLAG_NEED.equals(downloadTempleteDto.getCShareallot()) && OtherConstants.DOWNFLAG_NEED.equals(mto5001.getMSG().getRequestReport5001().getNrShare().toString())){
//            if(mto5001.getMSG().getRequestReport5001().getNrShare().toString().equals(AllStatConstants.REPORT_DWONLOAD_YES))
//            {
//
//
//                	NrShareBill3128[] array = createNrShare3128Report(mto5001, dateDto.getDWorkdate(), orgList,srcNodeDto.getCOfnodetype());
//                	
//                	if(array != null && array.length >0){
//                		NrShare3128 nrShare3128 = new NrShare3128();
//                        msg3128.setNrShare3128(nrShare3128);
//                        nrShare3128.setNrShareBill3128(array);
//                		isNull = false;
//                	}
//                	haveRight = true;
//                
//            }            
//
//            
//            //if(OtherConstants.DOWNFLAG_NEED.equals(downloadTempleteDto.getCStock()) && OtherConstants.DOWNFLAG_NEED.equals(mto5001.getMSG().getRequestReport5001().getStock().toString())){
//            if(mto5001.getMSG().getRequestReport5001().getStock().toString().equals(AllStatConstants.REPORT_DWONLOAD_YES))
//            {
//
//
//                	StockBill3128[] array = createStock3128Report(mto5001, dateDto.getDWorkdate(), orgList);
//                	
//                	if(array != null && array.length >0){
//                		Stock3128 stock3128 = new Stock3128();
//                        msg3128.setStock3128(stock3128);
//                        stock3128.setStockBill3128(array);
//                		isNull = false;
//                	}
//                	haveRight = true;
//            }            
//            
//            //if(OtherConstants.DOWNFLAG_NEED.equals(downloadTempleteDto.getCAbudincome()) && OtherConstants.DOWNFLAG_NEED.equals(mto5001.getMSG().getRequestReport5001().getTrBudget().toString())){
//            if(mto5001.getMSG().getRequestReport5001().getTrBudget().toString().equals(AllStatConstants.REPORT_DWONLOAD_YES))
//            {
//
//
//                	TrBudgetBill3128[] array = createTrBudget3128Report(mto5001, srcNodeDto.getCOfnodetype(), dateDto.getDWorkdate(), orgList);
//                	
//                	if(array != null && array.length >0){
//                		TrBudget3128 trBudget3128 = new TrBudget3128();
//                        msg3128.setTrBudget3128(trBudget3128);
//                        trBudget3128.setTrBudgetBill3128(array);
//                		isNull = false;
//                	}
//                	haveRight = true;
//            }            
//            
//            //if(OtherConstants.DOWNFLAG_NEED.equals(downloadTempleteDto.getCAdrawback()) && OtherConstants.DOWNFLAG_NEED.equals(mto5001.getMSG().getRequestReport5001().getTrDrawBack().toString())){
//            if(mto5001.getMSG().getRequestReport5001().getTrDrawBack().toString().equals(AllStatConstants.REPORT_DWONLOAD_YES))
//            {
//
//
//                	TrDrawBackBill3128[] array = createTrDrawBack3128Report(mto5001, srcNodeDto.getCOfnodetype(), dateDto.getDWorkdate(), orgList);
//                	
//                	if(array != null && array.length >0){
//                		TrDrawBack3128 trDrawBack3128 = new TrDrawBack3128();
//                        msg3128.setTrDrawBack3128(trDrawBack3128);
//                        trDrawBack3128.setTrDrawBackBill3128(array);
//                		isNull = false;
//                	}
//                	haveRight = true;
//            }            
//            
//            
//            //if(OtherConstants.DOWNFLAG_NEED.equals(downloadTempleteDto.getCAmoveincome()) && OtherConstants.DOWNFLAG_NEED.equals(mto5001.getMSG().getRequestReport5001().getTrRemove().toString())){
//            if(mto5001.getMSG().getRequestReport5001().getTrRemove().toString().equals(AllStatConstants.REPORT_DWONLOAD_YES))
//            {
//
//                	TrRemoveBill3128[] array = createTrRemove3128Report(mto5001, srcNodeDto.getCOfnodetype(), dateDto.getDWorkdate(), orgList);
//                	
//                	if(array != null && array.length >0){
//                		TrRemove3128 trRemove3128 = new TrRemove3128();
//                        msg3128.setTrRemove3128(trRemove3128);
//                        trRemove3128.setTrRemoveBill3128(array);
//                		isNull = false;
//                	}
//                	haveRight = true;
//            }   
//            //调整期共享报表 modified by wangyg 2008-02-14
//            if(mto5001.getMSG().getRequestReport5001().getTrShare().toString().equals(AllStatConstants.REPORT_DWONLOAD_YES))
//            {
//
//            	TrShareBill3128[] array = createTrShare3128Report(mto5001, dateDto.getDWorkdate(), orgList, srcNodeDto.getCOfnodetype());
//                	
//                	if(array != null && array.length >0){
//                		TrShare3128 trShare3128 = new TrShare3128();
//                        msg3128.setTrShare3128(trShare3128);
//                        trShare3128.setTrShareBill3128(array);
//                		isNull = false;
//                	}
//                	haveRight = true;
//            }  
//            
//            if(haveRight == false)
//            {
//            	throw new TipsException("没有需要下载的报表");
//            }
//            else if(isNull)
//            {
//            	throw new TipsException("没有需要下载的报表");
//            }
//            else
//            {
//            	String filename = saveFile(mto3128);
//            	List la = new ArrayList();
//            	la.add(filename);
//            	return la;
//            }
//    }
//
//    /**
//     * 生成正常期预算收入报表
//     * @throws JAFDatabaseException
//     * @throws JAFDatabaseException
//     *  
//     */
//    private static NrBudgetBill3128[] createNrBudget3128Report(CFX5001 mto5001, String orgType, Date workDate, List taxOrgList) throws JAFDatabaseException {
//        int size = taxOrgList.size();
//        if (AllTypeConstants.NODE_TYPE_TAX.equals(orgType)) {
//        	ArrayList allList = new ArrayList();
//	        for (int i = 0; i < size; i++) {
//	        	TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
//
//	        	String sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//	        	ArrayList params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTaxorgcode());
//	        	
//	            List reportList = null;
//
//					log.debug("生成正常期预算收入报表");
//					//reportList = DatabaseFacade.getQdb().find(TrBudgetIncomeDto.class, sqlWhere, params);
//					reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TrBudgetIncomeDto.class);
//					//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//
//				if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//	            
//	            for (int j = 0; j < reportSize; j++) {
//	            	TrBudgetIncomeDto o = (TrBudgetIncomeDto)reportList.get(j);
//	            	NrBudgetBill3128 sub = new NrBudgetBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	        }
//	        NrBudgetBill3128[] array = new NrBudgetBill3128[allList.size()];
//	        array = (NrBudgetBill3128[])allList.toArray(array);
//	        return array;
//        }else if(AllTypeConstants.NODE_TYPE_TREASURY.equals(orgType)){
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//        		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//
//	        	String sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//	        	ArrayList params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTrecode());
//	        	
//	            List reportList = null;
//
//					log.debug("生成正常期预算收入报表");
//					//reportList = DatabaseFacade.getQdb().find(TrBudgetIncomeDto.class, sqlWhere, params);
//					reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TrBudgetIncomeDto.class);
//					//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//
//	
//				if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//	            
//	            for (int j = 0; j < reportSize; j++) {
//	            	TrBudgetIncomeDto o = (TrBudgetIncomeDto)reportList.get(j);
//	            	NrBudgetBill3128 sub = new NrBudgetBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	        }
//        	NrBudgetBill3128[] array = new NrBudgetBill3128[allList.size()];
//	        array = (NrBudgetBill3128[])allList.toArray(array);
//	        return array;
//        }
//        return null;
//    }
//    /**
//     * 正常期退库报表
//     * @param contex
//     * @param mto5001
//     * @param orgType
//     * @param workDate
//     * @param taxOrgList
//     * @return
//     * @throws JAFDatabaseException
//     */
//    private static NrDrawBackBill3128[] createNrDrawBack3128Report(CFX5001 mto5001, String orgType, Date workDate, List taxOrgList) throws JAFDatabaseException {
//        int size = taxOrgList.size();
//        if (AllTypeConstants.NODE_TYPE_TAX.equals(orgType)) {
//        	ArrayList allList = new ArrayList();
//	        for (int i = 0; i < size; i++) {
//	        	TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
//	        	
//	        	String sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//	        	ArrayList params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTaxorgcode());
//	        	
//	        	log.debug("正常期退库报表");
//	            //List reportList = DatabaseFacade.getQdb().find(TrDrawbackDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TrDrawbackDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//	            
//	            for (int j = 0; j < reportSize; j++) {
//	            	TrDrawbackDto o = (TrDrawbackDto)reportList.get(j);
//	            	NrDrawBackBill3128 sub = new NrDrawBackBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	        }
//	        NrDrawBackBill3128[] array = new NrDrawBackBill3128[allList.size()];
//	        array = (NrDrawBackBill3128[])allList.toArray(array);
//	        return array;
//        }else if(AllTypeConstants.NODE_TYPE_TREASURY.equals(orgType)){
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//        		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//
//	        	String sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//	        	ArrayList params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTrecode());
//	        	
//	        	log.debug("正常期退库报表");
//	            //List reportList = DatabaseFacade.getQdb().find(TrDrawbackDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TrDrawbackDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
////	            NrDrawBackBill3128[] array = new NrDrawBackBill3128[reportSize];
//	            for (int j = 0; j < reportSize; j++) {
//	            	TrDrawbackDto o = (TrDrawbackDto)reportList.get(j);
//	            	NrDrawBackBill3128 sub = new NrDrawBackBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	        }
//        	NrDrawBackBill3128[] array = new NrDrawBackBill3128[allList.size()];
//	        array = (NrDrawBackBill3128[])allList.toArray(array);
//	        return array;
//        }
//        return null;
//    }
//    /**
//     * 正常期调拨收入报表
//     * @param contex
//     * @param mto5001
//     * @param orgType
//     * @param workDate
//     * @param taxOrgList
//     * @return
//     * @throws JAFDatabaseException
//     */
//    private static NrRemoveBill3128[] createNrRemove3128Report(CFX5001 mto5001, String orgType, Date workDate, List taxOrgList) throws JAFDatabaseException {
//        int size = taxOrgList.size();
//        if (AllTypeConstants.NODE_TYPE_TAX.equals(orgType)) {
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//	        	TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
//
//	        	String sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//	        	ArrayList params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTaxorgcode());
//	        	
//	        	log.debug("正常期调拨收入报表");
//	           // List reportList = DatabaseFacade.getQdb().find(TrMoveIncomeDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TrMoveIncomeDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//	            for (int j = 0; j < reportSize; j++) {
//	            	TrMoveIncomeDto o = (TrMoveIncomeDto)reportList.get(j);
//	            	NrRemoveBill3128 sub = new NrRemoveBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	        }
//	        NrRemoveBill3128[] array = new NrRemoveBill3128[allList.size()];
//	        array = (NrRemoveBill3128[])allList.toArray(array);
//	        return array;
//        }else if(AllTypeConstants.NODE_TYPE_TREASURY.equals(orgType)){
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//        		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//        		
//	        	String sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//	        	ArrayList params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTrecode());
//	        	
//	        	log.debug("正常期调拨收入报表");
//	           // List reportList = DatabaseFacade.getQdb().find(TrMoveIncomeDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TrMoveIncomeDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//
//	            for (int j = 0; j < reportSize; j++) {
//	            	TrMoveIncomeDto o = (TrMoveIncomeDto)reportList.get(j);
//	            	NrRemoveBill3128 sub = new NrRemoveBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	        }
//        	NrRemoveBill3128[] array = new NrRemoveBill3128[allList.size()];
//	        array = (NrRemoveBill3128[])allList.toArray(array);
//	        return array;
//        }
//        return null;
//    }
//    /**
//     * 总额分成报表 国库才会生成这种报表
//     * @param contex
//     * @param mto5001
//     * @param workDate
//     * @param taxOrgList
//     * @return
//     * @throws JAFDatabaseException
//     */
//    private static AmountBill3128[] createAmount3128Report(CFX5001 mto5001, Date workDate, List taxOrgList) throws JAFDatabaseException {
//        int size = taxOrgList.size();
//        ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//        		
//        		String sqlWhere = "";
//        		ArrayList params = new ArrayList(2);
//        		if(taxOrgList.get(i) instanceof TdTreasuryDto)
//        		{
//            		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//            		//TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
//
//    	        	sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//    	        	params = new ArrayList(2);
//    	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//    	        	params.add(org.getSTrecode());
//        		}
//        		if(taxOrgList.get(i) instanceof TdImposeOrgDto)
//        		{
//        			return null;
////        			TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
////            		//TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
////
////    	        	//sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
////    	        	sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
////    	        	params = new ArrayList(2);
////    	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
////    	        	params.add(org.getSTaxorgcode());
//
//        		}
//
//	        	
//	        	log.debug("总额分成报表");
//	            //List reportList = DatabaseFacade.getQdb().find(TrTotalAllotDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TrTotalAllotDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//	            for (int j = 0; j < reportSize; j++) {
//	            	TrTotalAllotDto o = (TrTotalAllotDto)reportList.get(j);
//	            	AmountBill3128 sub = new AmountBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	        }
//        	AmountBill3128[] array = new AmountBill3128[allList.size()];
//	        array = (AmountBill3128[])allList.toArray(array);
//	        return array;
//    }
//    /**
//     * 正常期共享分成报表
//     * @param contex
//     * @param mto5001
//     * @param workDate
//     * @param taxOrgList
//     * @return
//     * @throws JAFDatabaseException
//     */
//    private static NrShareBill3128[] createNrShare3128Report(CFX5001 mto5001, Date workDate, List taxOrgList, String orgType) throws JAFDatabaseException {
//        int size = taxOrgList.size();
//        if (AllTypeConstants.NODE_TYPE_TAX.equals(orgType)) {
//        	return null;
//        	/*ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//	        	TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
//
//	        	String sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//	        	ArrayList params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTaxorgcode());
//	        	
//	        	log.debug("正常期共享分成报表");
//	            List reportList = DatabaseFacade.getQdb().find(TrShareAllotDto.class, sqlWhere, params);
//	            
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//	            
//	            for (int j = 0; j < reportSize; j++) {
//	            	TrShareAllotDto o = (TrShareAllotDto)reportList.get(j);
//	            	NrShareBill3128 sub = new NrShareBill3128();
//	            	sub.setTaxOrgCode(o.getSTaxorgcode());
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	            	sub.setDivideGroup(o.getSDividegroup());
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//
//	        }
//	        NrShareBill3128[] array = new NrShareBill3128[allList.size()];
//	        array = (NrShareBill3128[])allList.toArray(array);
//	        return array;*/
//        }else if(AllTypeConstants.NODE_TYPE_TREASURY.equals(orgType)){
//	        ArrayList allList = new ArrayList();	
//	        for (int i = 0; i < size; i++) {
//	        		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//	
//		        	String sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//		        	ArrayList params = new ArrayList(2);
//		        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//		        	params.add(org.getSTrecode());
//		        	
//		        	log.debug("正常期共享分成报表");
//		            //List reportList = DatabaseFacade.getQdb().find(TrShareAllotDto.class, sqlWhere, params);
//		        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TrShareAllotDto.class);
//					//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//		            if(reportList == null || reportList.size() == 0){
//		            	break;
//		            }
//		          
//		            int reportSize = reportList.size();
//		            
//		            for (int j = 0; j < reportSize; j++) {
//		            	TrShareAllotDto o = (TrShareAllotDto)reportList.get(j);
//		            	NrShareBill3128 sub = new NrShareBill3128();		            	sub.setTaxOrgCode(o.getSTaxorgcode());
//		                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//		                sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//		                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//		                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//		                sub.setDayAmt(o.getFDayamt());
//		                sub.setMonthAmt(o.getFMonthamt());
//		                sub.setDivideGroup(o.getSDividegroup());
//		                sub.setQuarterAmt(o.getFQuarteramt());
//		                sub.setTenDayAmt(o.getFTendayamt());
//		                sub.setTreCode(o.getSTrecode());
//		                sub.setYearAmt(o.getFYearamt());
//		                allList.add(sub);
//		            }
//	
//		        }
//	        NrShareBill3128[] array = new NrShareBill3128[allList.size()];
//	        array = (NrShareBill3128[])allList.toArray(array);
//	        return array;
//        }
//        return null;
//    }
//    /**
//     * 库存日报
//     * @param contex
//     * @param mto5001
//     * @param workDate
//     * @param taxOrgList
//     * @return
//     * @throws JAFDatabaseException
//     */
//    private static StockBill3128[] createStock3128Report(CFX5001 mto5001, Date workDate, List taxOrgList) throws JAFDatabaseException {
//        int size = taxOrgList.size();
//        ArrayList allList = new ArrayList();	
//        for (int i = 0; i < size; i++) {
//        		
//        		
//        	String sqlWhere = "";
//        	ArrayList params = new ArrayList(2);
//
//        	if(taxOrgList.get(i) instanceof TdTreasuryDto)
//    		{
//        		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//
//	        	sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//	        	params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTrecode());
//    		}
//        	if(taxOrgList.get(i) instanceof TdImposeOrgDto)
//    		{
//        		return null;
////        		TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
////
////	        	//sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
////        		sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
////	        	params = new ArrayList(2);
////	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
////	        	params.add(org.getSTaxorgcode());
//
//    		}
//
//	        	
//	        	log.debug("库存日报");
//	            //List reportList = DatabaseFacade.getQdb().find(TrStockDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TrStockDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//
//	            for (int j = 0; j < reportSize; j++) {
//	            	TrStockDto o = (TrStockDto)reportList.get(j);
//	            	StockBill3128 sub = new StockBill3128();
//	                sub.setAcctCode(o.getSAcctcode());
//	                sub.setAcctDate(DateUtil.utilDate2CastorDate(o.getDAcctdate()));
//	                sub.setAcctName(o.getSAcctname());
//	                sub.setTodayBalance(o.getFTodaybalance());
//	                sub.setTodayPay(o.getFTodaypay());
//	                sub.setTodayReceipt(o.getFTodayreceipt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYesterdayBalance(o.getFYesterdaybalance());
//	                allList.add(sub);
//	            }
//	        }
//        StockBill3128[] array = new StockBill3128[allList.size()];
//        array = (StockBill3128[])allList.toArray(array);
//        return array;
//    }
//    /**
//     * 调整期预算收入报表
//     * @param contex
//     * @param mto5001
//     * @param orgType
//     * @param workDate
//     * @param taxOrgList
//     * @return
//     * @throws JAFDatabaseException
//     */
//    private static TrBudgetBill3128[] createTrBudget3128Report(CFX5001 mto5001, String orgType, Date workDate, List taxOrgList) throws JAFDatabaseException {
//        int size = taxOrgList.size();
//        if (AllTypeConstants.NODE_TYPE_TAX.equals(orgType)) {
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//            	String sqlWhere = "";
//            	ArrayList params = new ArrayList(2);
//
//            	if(taxOrgList.get(i) instanceof TdImposeOrgDto)
//        		{
//            		TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
//
//    	        	sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//    	        	params = new ArrayList(2);
//    	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//    	        	params.add(org.getSTaxorgcode());
//
//        		}
//            	if(taxOrgList.get(i) instanceof TdTreasuryDto)
//        		{
//            		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//
//    	        	//sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//            		sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//    	        	params = new ArrayList(2);
//    	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//    	        	params.add(org.getSTrecode());
//        		}
//	        	
//	        	log.debug("调整期预算收入报表");
//	            //List reportList = DatabaseFacade.getQdb().find(TraBudgetIncomeDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TraBudgetIncomeDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//
//	            for (int j = 0; j < reportSize; j++) {
//	            	TraBudgetIncomeDto o = (TraBudgetIncomeDto)reportList.get(j);
//	            	TrBudgetBill3128 sub = new TrBudgetBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	            
//	        }
//        	TrBudgetBill3128[] array = new TrBudgetBill3128[allList.size()];
//            array = (TrBudgetBill3128[])allList.toArray(array);
//            return array;
//        }else if(AllTypeConstants.NODE_TYPE_TREASURY.equals(orgType)){
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//        		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//
//	        	String sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//	        	ArrayList params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTrecode());
//	        	
//	        	log.debug("调整期预算收入报表");
//	            //List reportList = DatabaseFacade.getQdb().find(TraBudgetIncomeDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TraBudgetIncomeDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//
//	            for (int j = 0; j < reportSize; j++) {
//	            	TraBudgetIncomeDto o = (TraBudgetIncomeDto)reportList.get(j);
//	            	TrBudgetBill3128 sub = new TrBudgetBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	        }
//        	TrBudgetBill3128[] array = new TrBudgetBill3128[allList.size()];
//            array = (TrBudgetBill3128[])allList.toArray(array);
//            return array;
//        }
//        return null;
//    }
//    /**
//     * 调整期退库报表
//     * @param contex
//     * @param mto5001
//     * @param orgType
//     * @param workDate
//     * @param taxOrgList
//     * @return
//     * @throws JAFDatabaseException
//     */
//    private static TrDrawBackBill3128[] createTrDrawBack3128Report(CFX5001 mto5001, String orgType, Date workDate, List taxOrgList) throws JAFDatabaseException {
//        int size = taxOrgList.size();
//        if (AllTypeConstants.NODE_TYPE_TAX.equals(orgType)) {
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//            	String sqlWhere = "";
//            	ArrayList params = new ArrayList(2);
//
//            	if(taxOrgList.get(i) instanceof TdImposeOrgDto)
//        		{
//
//            		TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
//
//    	        	sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//    	        	params = new ArrayList(2);
//    	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//    	        	params.add(org.getSTaxorgcode());
//
//        		}
//            	if(taxOrgList.get(i) instanceof TdTreasuryDto)
//        		{
//            		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//
//    	        	//sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//            		sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//    	        	params = new ArrayList(2);
//    	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//    	        	params.add(org.getSTrecode());
//        		}
//
//	        	log.debug("调整期退库报表");
//	            //List reportList = DatabaseFacade.getQdb().find(TraDrawbackDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TraDrawbackDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//
//	            for (int j = 0; j < reportSize; j++) {
//	            	TraDrawbackDto o = (TraDrawbackDto)reportList.get(j);
//	            	TrDrawBackBill3128 sub = new TrDrawBackBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	           
//	        }
//        	TrDrawBackBill3128[] array = new TrDrawBackBill3128[allList.size()];
//            array = (TrDrawBackBill3128[])allList.toArray(array);
//            return array;
//        }else if(AllTypeConstants.NODE_TYPE_TREASURY.equals(orgType)){
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
////        		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
////
////	        	String sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
////	        	ArrayList params = new ArrayList(2);
////	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
////	        	params.add(org.getSTrecode());
//        		
//            	String sqlWhere = "";
//            	ArrayList params = new ArrayList(2);
//
//            	if(taxOrgList.get(i) instanceof TdImposeOrgDto)
//        		{
//
//            		TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
//
//    	        	sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//    	        	params = new ArrayList(2);
//    	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//    	        	params.add(org.getSTaxorgcode());
//        		}
//            	if(taxOrgList.get(i) instanceof TdTreasuryDto)
//        		{
//            		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//
//    	        	//sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//            		sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//    	        	params = new ArrayList(2);
//    	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//    	        	params.add(org.getSTrecode());
//        		}
//	        	
//	        	log.debug("调整期退库报表");
//	            //List reportList = DatabaseFacade.getQdb().find(TraDrawbackDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TraDrawbackDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//
//	            for (int j = 0; j < reportSize; j++) {
//	            	TraDrawbackDto o = (TraDrawbackDto)reportList.get(j);
//	            	TrDrawBackBill3128 sub = new TrDrawBackBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	            
//	        }
//        	TrDrawBackBill3128[] array = new TrDrawBackBill3128[allList.size()];
//            array = (TrDrawBackBill3128[])allList.toArray(array);
//            return array;
//        }
//        return null;
//    }
//    /**
//     * 调整期调拨收入报表
//     * @param contex
//     * @param mto5001
//     * @param orgType
//     * @param workDate
//     * @param taxOrgList
//     * @return
//     * @throws JAFDatabaseException
//     */
//    private static TrRemoveBill3128[] createTrRemove3128Report(CFX5001 mto5001, String orgType, Date workDate, List taxOrgList) throws JAFDatabaseException {
//        int size = taxOrgList.size();
//        if (AllTypeConstants.NODE_TYPE_TAX.equals(orgType)) {
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
////	        	TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
////
////	        	String sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
////	        	ArrayList params = new ArrayList(2);
////	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
////	        	params.add(org.getSTaxorgcode());
//        		
//            	String sqlWhere = "";
//            	ArrayList params = new ArrayList(2);
//
//            	if(taxOrgList.get(i) instanceof TdImposeOrgDto)
//        		{
//
//            		TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
//
//    	        	sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//    	        	params = new ArrayList(2);
//    	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//    	        	params.add(org.getSTaxorgcode());
//        		}
//            	if(taxOrgList.get(i) instanceof TdTreasuryDto)
//        		{
//            		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//
//    	        	//sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//            		sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//    	        	params = new ArrayList(2);
//    	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//    	        	params.add(org.getSTrecode());
//        		}
//
//	        	
//	        	log.debug("调整期调拨收入报表");
//	            //List reportList = DatabaseFacade.getQdb().find(TraMoveIncomeDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TraMoveIncomeDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//
//	            for (int j = 0; j < reportSize; j++) {
//	            	TraMoveIncomeDto o = (TraMoveIncomeDto)reportList.get(j);
//	            	TrRemoveBill3128 sub = new TrRemoveBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	        }
//        	TrRemoveBill3128[] array = new TrRemoveBill3128[allList.size()];
//            array = (TrRemoveBill3128[])allList.toArray(array);
//            return array;
//        }else if(AllTypeConstants.NODE_TYPE_TREASURY.equals(orgType)){
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//        		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//
//	        	String sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//	        	ArrayList params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTrecode());
//	        	
//	        	log.debug("调整期调拨收入报表");
//	            //List reportList = DatabaseFacade.getQdb().find(TraMoveIncomeDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TraMoveIncomeDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//
//	            for (int j = 0; j < reportSize; j++) {
//	            	TraMoveIncomeDto o = (TraMoveIncomeDto)reportList.get(j);
//	            	TrRemoveBill3128 sub = new TrRemoveBill3128();
//	            	sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                allList.add(sub);
//	            }
//	            
//	        }
//        	TrRemoveBill3128[] array = new TrRemoveBill3128[allList.size()];
//            array = (TrRemoveBill3128[])allList.toArray(array);
//            return array;
//        }
//        return null;
//    }
//    /**
//     * 调整期共享分成报表
//     * @param contex
//     * @param mto5001
//     * @param workDate
//     * @param taxOrgList
//     * @return
//     * @throws JAFDatabaseException
//     */
//    private static TrShareBill3128[] createTrShare3128Report(CFX5001 mto5001, Date workDate, List taxOrgList, String orgType) throws JAFDatabaseException {
//        int size = taxOrgList.size();
//        if (AllTypeConstants.NODE_TYPE_TAX.equals(orgType)) {
//        	ArrayList allList = new ArrayList();
//        	for (int i = 0; i < size; i++) {
//	        	
//        		TdImposeOrgDto org = (TdImposeOrgDto) taxOrgList.get(i);
//
//	        	String sqlWhere = "where d_ReportDate = ? and s_TaxOrgCode = ? ";
//	        	ArrayList params = new ArrayList(2);
//	        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//	        	params.add(org.getSTaxorgcode());
//	        	
//	        	log.debug("调整期共享分成报表");
//	            //List reportList = DatabaseFacade.getQdb().find(TraShareAllotDto.class, sqlWhere, params);
//	        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TraShareAllotDto.class);
//				//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//	            if(reportList == null || reportList.size() == 0){
//	            	break;
//	            }
//	          
//	            int reportSize = reportList.size();
//
//	            for (int j = 0; j < reportSize; j++) {
//	            	TraShareAllotDto o = (TraShareAllotDto)reportList.get(j);
//	            	TrShareBill3128 sub = new TrShareBill3128();
//	            	sub.setTaxOrgCode(o.getSTaxorgcode());
//	                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//	                sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//	                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//	                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//	                sub.setDayAmt(o.getFDayamt());
//	                sub.setMonthAmt(o.getFMonthamt());
//	                sub.setQuarterAmt(o.getFQuarteramt());
//	                sub.setTenDayAmt(o.getFTendayamt());
//	                sub.setTreCode(o.getSTrecode());
//	                sub.setYearAmt(o.getFYearamt());
//	                //2005-12-19 18:18 为调整期共享分成报表 增加了预算级次 by newroc
//                    sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//                    sub.setDivideGroup(o.getSDividegroup());
//	                allList.add(sub);
//	            }
//	          
//	        }
//	        TrShareBill3128[] array = new TrShareBill3128[allList.size()];
//	        array = (TrShareBill3128[])allList.toArray(array);
//	        return array;
//        }else if(AllTypeConstants.NODE_TYPE_TREASURY.equals(orgType)){
//        ArrayList allList = new ArrayList();	
//	        for (int i = 0; i < size; i++) {
//	        		TdTreasuryDto org = (TdTreasuryDto) taxOrgList.get(i);
//	
//		        	String sqlWhere = "where d_ReportDate = ? and s_TreCode = ? ";
//		        	ArrayList params = new ArrayList(2);
//		        	params.add(DateUtil.castorDate2SqlDate(mto5001.getMSG().getRequestHead5001().getReportDate()));
//		        	params.add(org.getSTrecode());
//		        	
//		        	log.debug("调整期共享分成报表");
//		            //List reportList = DatabaseFacade.getQdb().find(TraShareAllotDto.class, sqlWhere, params);
//		        	List reportList = BatchRetrieverHelper.findDtosByQDBWhere(sqlWhere , params , TraShareAllotDto.class);
//					//以上是尹建松修改的修改，	BatchRetrieverHelper为取得所有值的，不加限制	
//		            if(reportList == null || reportList.size() == 0){
//		            	break;
//		            }
//		          
//		            int reportSize = reportList.size();
//	
//		            for (int j = 0; j < reportSize; j++) {
//		            	TraShareAllotDto o = (TraShareAllotDto)reportList.get(j);
//		            	TrShareBill3128 sub = new TrShareBill3128();
//		            	sub.setTaxOrgCode(o.getSTaxorgcode());
//		                sub.setBudgetSubjectCode(o.getSBudgetsubjectcode());
//		                sub.setBudgetSubjectName(o.getSBudgetsubjectname());
//		                sub.setBudgetType(BudgetType.valueOf(o.getCBudgettype()));
//		                sub.setDayAmt(o.getFDayamt());
//		                sub.setMonthAmt(o.getFMonthamt());
//		                sub.setQuarterAmt(o.getFQuarteramt());
//		                sub.setTenDayAmt(o.getFTendayamt());
//		                sub.setTreCode(o.getSTrecode());
//		                sub.setYearAmt(o.getFYearamt());
//		                //2005-12-19 18:18 增加了预算级次 by newroc
//		                sub.setBudgetLevelCode(BudgetLevelCode.valueOf(o.getCBudgetlevel()));
//		                //modified by wangyg 2008-02-15
//		                sub.setDivideGroup(o.getSDividegroup());
//		                allList.add(sub);
//		            }
//		          
//		        }
//	        TrShareBill3128[] array = new TrShareBill3128[allList.size()];
//	        array = (TrShareBill3128[])allList.toArray(array);
//	        return array;
//        }
//        return null;
//    }
//	/**
//	 * 生成文件
//	 * 
//	 * @param mto3128
//	 * @param mto0130
//	 *            TODO
//	 * @return
//	 * @throws FileTransferException
//	 * @throws XMLException
//	 * @throws CAException
//	 * @throws FileOperateException
//	 * @throws JAFDatabaseException
//	 * @throws JAFDatabaseException
//	 */
//	private static String saveFile(CFX3128 mto3128)
//			throws FileTransferException, XMLException, CAException,
//			FileOperateException, JAFDatabaseException {
//
//		// 生成文件相对路径
//		String fileName = CommonBeanHelper
//				.genFile(FileExtNameConstant.BATCH_TAX_EXTENCE_FILENAME_1102_XML);
//		// 取得文件公共文件存储系统根路径
//		String rootpath = CommonBeanHelper.getRoot();
//		// 文件全路径
//		String fileAllpath = rootpath + fileName;
//
//		MtoFactory.getInstance().Mto2XmlFile(mto3128, fileAllpath);
//		// //////////////////// //存储报文路径
//
//
//		// 返回文件路径
//		return fileName;
//
//	}
//}
