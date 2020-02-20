package com.cfcc.itfe.client.recbiz.backvoucher;

import itferesourcepackage.ReportBussReadReturnEnumFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.client.dialog.AdminConfirmDialogFacade;
import com.cfcc.itfe.client.dialog.DisplayCursor;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeBackVoucherOcx;
import com.cfcc.itfe.client.sendbiz.bizcertsend.ActiveXCompositeVoucherOcx;
import com.cfcc.itfe.constant.DealCodeConstants;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.HtvDwbkDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TfDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundmainDto;
import com.cfcc.itfe.persistence.dto.TfPaybankRefundsubDto;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsStamppositionDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TsUsersDto;
import com.cfcc.itfe.persistence.dto.TsVouchercommitautoDto;
import com.cfcc.itfe.persistence.dto.TvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvPayoutbackmsgMainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgsubDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankBackDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.persistence.pk.TsTreasuryPK;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoPK;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.common.util.StringUtil;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.mvc.editors.MVCUtils;
import com.cfcc.jaf.rcp.mvc.editors.ModelChangedEvent;
import com.cfcc.jaf.rcp.util.MessageDialog;
import com.cfcc.jaf.ui.util.Mapper;

/**
 * codecomment: 
 * @author Tyler
 * @time   14-03-27 14:55:46
 * ��ϵͳ: RecBiz
 * ģ��:backVoucher
 * ���:BackVoucher
 */
public class BackVoucherBean extends AbstractBackVoucherBean implements IPageDataProvider {

    private static Log log = LogFactory.getLog(BackVoucherBean.class);
	
    //���Tabƾ֤���ɽ�����ز���-----start
    private List voucherTypeGList=null;
    private String voucherGType=null;
    private List voucherVtcodeList=null;
    private String strecode=null;
    private String svoucherno="";
    private String voudate=null;
    TfPaybankRefundsubDto refundsubDto= null;
    private List<TvPayoutmsgsubDto> subList = null;
    //�����˿�ƾ֤�����б�
    private List batchVouTypeGList=null;
    //���Tabƾ֤���ɽ�����ز���-----end
    
    //�Ҳ�Tabƾ֤���ɽ�����ز���-----start
    List<TvVoucherinfoDto> checkList=null; 
	private ITFELoginInfo loginInfo;
	private String stamp;
	private List stampList=null;
	private String voucherType=null;
	private List voucherTypeList=null;
	private String voucherTcbsType=null;
	private String vtcode=null;
	//�Ҳ�Tabƾ֤���ɽ�����ز���-----end
	
	List backcheckList = new ArrayList();
	PagingContext backpagingcontext = null;
	TvPayoutbackmsgMainDto backmaindto = null;
	
	
	//TCBSʵ���ʽ��˿��Tabƾ֤���ɽ�����ز���-----start
    List<TvVoucherinfoDto> checkPayoutBackList=null; 
    PagingContext payoutBackPagingContext = null;
	//TCBSʵ���ʽ��˿��Tabƾ֤���ɽ�����ز���-----end
	
    private BigDecimal sbtk;
    private BigDecimal tktk;
    
    //ʵ���ʽ��˻�ԭ��
    private String payoutBackReason = null;
    //�˿��˻�ԭ��
    private String dwbkBackReason = null;
    private List recvDeptList = null;
    private String recvDept = null;
    public BackVoucherBean() {
      super();
      loginInfo = (ITFELoginInfo)ApplicationActionBarAdvisor.getDefault().getLoginInfo();
      //���Tabƾ֤���ɽ�����ز���-----start
      tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
	  tvPayoutmsgsubDto = new TvPayoutmsgsubDto();
	  tfDirectpaymsgmainDto = new TfDirectpaymsgmainDto();
	  backmaindto = new TvPayoutbackmsgMainDto();
	  tvDwbkDto = new TvDwbkDto();
	  voucherTypeGList = getMaplist();
	  batchVouTypeGList = getBatchMaplist();	 
	  leftdto = new TvVoucherinfoDto();
	  vtcode=MsgConstant.VOUCHER_NO_3208;
	  voucherGType = MsgConstant.VOUCHER_NO_3208;
	  voucherType = MsgConstant.VOUCHER_NO_3208;
	  voucherTcbsType = MsgConstant.VOUCHER_NO_3208;
	  voucherVtcodeList = getVtcodeMapList();
	  TsTreasuryDto tsTreasuryDto = new TsTreasuryDto();
	  tsTreasuryDto.setSorgcode(loginInfo.getSorgcode());
	  List<TsTreasuryDto> list = null;
	  try {
		    list = (List<TsTreasuryDto>)commonDataAccessService.findRsByDto(tsTreasuryDto);
	      } catch (ITFEBizException e) 
	      {
		    log.error(e);
	      }
	      tsTreasuryDto = list.get(0);
	  strecode = tsTreasuryDto.getStrecode();
	  
	  //���ڼ�¼�鵽��ԭʵ��ƾ֤��ϸ�б�
	  subList = new ArrayList<TvPayoutmsgsubDto>();
	  
	  //���Tabƾ֤���ɽ�����ز���-----end
	  
	  //�Ҳ�Tabƾ֤���ɽ�����ز���-----start
      dto = new TvVoucherinfoDto();
      checkList=new ArrayList();
      dto.setScreatdate(TimeFacade.getCurrentStringTime());
      dto.setSorgcode(loginInfo.getSorgcode());
      dto.setSvtcode(MsgConstant.VOUCHER_NO_3208);
      backmaindto.setSorgcode(loginInfo.getSorgcode());
      backmaindto.setSvoudate(TimeFacade.getCurrentStringTime());
      pagingcontext = new PagingContext(this); 
      backpagingcontext = new PagingContext(this); 
      voucherTypeList= getMaplist();
      //�Ҳ�Tabƾ֤���ɽ�����ز���-----end
      
      //TCBSʵ���ʽ��˿��Tabƾ֤���ɽ�����ز���-----start
      indexDto = new TvVoucherinfoDto();
      indexDto.setScreatdate(TimeFacade.getCurrentStringTime());
      indexDto.setSorgcode(loginInfo.getSorgcode());
      indexDto.setSvtcode(MsgConstant.VOUCHER_NO_3208);
      if(list!=null && list.size() > 0){
    	  indexDto.setStrecode(list.get(0).getStrecode());
	  }
      checkPayoutBackList= new ArrayList<TvVoucherinfoDto>(); 
      payoutBackPagingContext = new PagingContext(this);
  	  //TCBSʵ���ʽ��˿��Tabƾ֤���ɽ�����ز���-----end
      
      payoutBackReason = "";//ʵ���ʽ��˻�ԭ��
      dwbkBackReason = "";//�˿��˻�ԭ��
    }
    
    private List<Mapper> getVtcodeMapList()
    {
    	List<Mapper> maplist = new ArrayList<Mapper>();
		Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3208, "ʵ���ʽ��˿�");
		maplist.add(map1);
		if(loginInfo.getPublicparam().indexOf(",collectpayment=1,")>=0)
		{
			Mapper map3268 = new Mapper(MsgConstant.VOUCHER_NO_3268, "ʵ���ʽ�ר���˿�");
			maplist.add(map3268);
		}
		if(loginInfo.getPublicparam().indexOf(",sh,")>=0){
			Mapper map2 = new Mapper(MsgConstant.MSG_NO_2202, "ֱ��֧���˿�");
	  	    maplist.add(map2);
		}
  	    return maplist;
    }
    
    private List<Mapper> getMaplist()
    {
    	List<Mapper> maplist = new ArrayList<Mapper>();
		Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3208, "ʵ���ʽ��˿�");
		maplist.add(map1);
		if(loginInfo.getPublicparam().indexOf(",collectpayment=1,")>=0)
		{
			Mapper map3268 = new Mapper(MsgConstant.VOUCHER_NO_3268, "ʵ���ʽ�ר���˿�");
			maplist.add(map3268);
		}
		if(loginInfo.getPublicparam().indexOf(",sh,")>=0){
			Mapper map2 = new Mapper(MsgConstant.VOUCHER_NO_2203, "ֱ��֧���˿�");
			maplist.add(map2);
		}
        Mapper map3;
        if (loginInfo.getPublicparam().indexOf(",jilin,")!= -1) {
        	map3 = new Mapper(MsgConstant.VOUCHER_NO_3251, "�����˸��˿�");
			
		}else {
			map3 = new Mapper(MsgConstant.VOUCHER_NO_3210, "�����˸��˿�");
		}
  	    maplist.add(map3);
  	    return maplist;
    }
    
    
    private List<Mapper> getBatchMaplist()
    {
    	 List<Mapper> maplist = new ArrayList<Mapper>();
		 Mapper map1 = new Mapper(MsgConstant.VOUCHER_NO_3208, "ʵ���ʽ��˿�");//(����)
		 maplist.add(map1);
		 if(loginInfo.getPublicparam().indexOf(",collectpayment=1,")>=0)
			{
				Mapper map3268 = new Mapper(MsgConstant.VOUCHER_NO_3268, "ʵ���ʽ�ר���˿�");
				maplist.add(map3268);
			}
		 if(loginInfo.getPublicparam().indexOf(",sh,")>=0){
			 Mapper map2 = new Mapper(MsgConstant.VOUCHER_NO_5201, "ֱ��֧���˿�");
			 maplist.add(map2);
		 }
		 Mapper map3;
	        if (loginInfo.getPublicparam().indexOf(",jilin,")!= -1) {
	        	map3 = new Mapper(MsgConstant.VOUCHER_NO_3251, "�����˸��˿�");
				
			}else {
				map3 = new Mapper(MsgConstant.VOUCHER_NO_3210, "�����˸��˿�");
			}
	     maplist.add(map3);
  	     return maplist;
    }
    
    //���Tabƾ֤���ɽ�����ط���-----start
    /**
	 * Direction: ��ѯ 
	 */
    public String voucherGSearch(Object o)
    {
    	if(voucherGType == null || voucherGType.equals(""))
    	{
    		MessageDialog.openMessageDialog(null, "����ѡ��ƾ֤���ͣ�");
    		return "";
    	}else if(strecode == null || strecode.equals(""))
    	{
    		MessageDialog.openMessageDialog(null, "����ѡ����⣡");
    		return "";
    	}
    	else if(svoucherno == null || svoucherno.equals(""))
    	{
    		MessageDialog.openMessageDialog(null, "������дԭƾ֤���룡");
    		return "";
    	}
    	else if(StringUtils.isBlank(voudate)&&(voucherGType.equals(MsgConstant.VOUCHER_NO_3208)||
    			voucherGType.equals(MsgConstant.VOUCHER_NO_3210))){
    		MessageDialog.openMessageDialog(null, "������дԭƾ֤���ڣ�");
    		return "";
    	}
    	setSbtk(null);
    	setTktk(null);
    	setPayoutBackReason("");
    	if(voucherGType.equals(MsgConstant.VOUCHER_NO_3208)||voucherGType.equals(MsgConstant.VOUCHER_NO_3268))
    	{
    		try
			{
				findPayoutmsgDto();
			} catch (ITFEBizException e)
			{
				tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
				tvPayoutmsgsubDto = new TvPayoutmsgsubDto();
				subList = new ArrayList<TvPayoutmsgsubDto>();
				tvDwbkDto = new TvDwbkDto();
				editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, e.getMessage());
        		return "";
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
    	}
    	else if(voucherGType.equals(MsgConstant.VOUCHER_NO_3210) || voucherGType.equals(MsgConstant.VOUCHER_NO_3251))
    	{
    		try
			{
				findTvDwbkDto();
			} catch (ITFEBizException e)
			{
				tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
				tvPayoutmsgsubDto = new TvPayoutmsgsubDto();
				subList = new ArrayList<TvPayoutmsgsubDto>();
				tvDwbkDto = new TvDwbkDto();
				editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
				MessageDialog.openMessageDialog(null, e.getMessage());
        		return "";
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
    	}else if(voucherGType.equals(MsgConstant.VOUCHER_NO_5201)){
    		try{
    			TvVoucherinfoDto dto = new TvVoucherinfoDto();
    			dto.setSvtcode(voucherGType);
    	    	dto.setStrecode(strecode);
    	    	dto.setSvoucherno(svoucherno);
    	    	dto.setScreatdate(voudate);
    	    	dto.setNmoney(leftdto.getNmoney());
    	    	List list=backVoucherService.findMainDto(dto);
    	    	if(list==null||list.size()==0){
    	    		tfDirectpaymsgmainDto = new TfDirectpaymsgmainDto();
    	    		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);	
    	    		MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
    	    		return null;
    	    	}
    	    	if(voucherGType.equals(MsgConstant.VOUCHER_NO_5201))
    	    		tfDirectpaymsgmainDto=(TfDirectpaymsgmainDto) list.get(0);  
			} catch (ITFEBizException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, e.getMessage());
			} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
				log.error(e);
				MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			}
    	}
    	editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);	
        return super.voucherGSearch(o);
    }
    
    private void findPayoutmsgDto() throws ITFEBizException
    {
    	TvPayoutmsgmainDto dto = new TvPayoutmsgmainDto();
    	dto.setStrecode(strecode);//�������
    	dto.setStaxticketno(svoucherno);//ƾ֤���
    	dto.setSgenticketdate(voudate);//ƾ֤����
    	dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS); //����ɹ�
    	if(leftdto.getNmoney() != null)
    	{
    		dto.setNmoney(leftdto.getNmoney());//���
    	}
    	
    	try
		{
			List<TvPayoutmsgmainDto> list = null;
			TsTreasuryPK trecodeDto = new TsTreasuryPK();
			trecodeDto.setStrecode(strecode);
			trecodeDto.setSorgcode(loginInfo.getSorgcode());
			TsTreasuryDto tredto = (TsTreasuryDto)commonDataAccessService.find(trecodeDto);
			if(loginInfo.getPublicparam().indexOf(",collectpayment=1,")>=0&&(!tredto.getStreattrib().equals("2")))
			{
				if(MsgConstant.VOUCHER_NO_3208.equals(voucherGType))
				{
					list = (List<TvPayoutmsgmainDto>)commonDataAccessService.findRsByDtoWithWhere(dto, " and S_PAYERACCT = '"+loginInfo.getSorgcode()+"27101'");
				}
				else
				{
					list = (List<TvPayoutmsgmainDto>)commonDataAccessService.findRsByDtoWithWhere(dto, " and S_PAYERACCT <>'"+loginInfo.getSorgcode()+"27101'");
				}
			}else
			{
				list = (List<TvPayoutmsgmainDto>)commonDataAccessService.findRsByDto(dto);
			}
			if(list !=null && list.size()>0)
			{
				tvPayoutmsgmainDto = list.get(0);
				TvPayoutmsgsubDto qDto = new TvPayoutmsgsubDto();
				qDto.setSbizno(tvPayoutmsgmainDto.getSbizno());
				List<TvPayoutmsgsubDto> list2 = (List<TvPayoutmsgsubDto>)commonDataAccessService.findRsByDto(qDto);
				tvPayoutmsgsubDto = list2.get(0);
				setSubList(list2);
			}else
			{
				HtvPayoutmsgmainDto dto2 = new HtvPayoutmsgmainDto();
				dto2.setStrecode(strecode);//�������
		    	dto2.setStaxticketno(svoucherno);//ƾ֤���
		    	dto2.setSgenticketdate(voudate);//ƾ֤����
		    	dto2.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS); //����ɹ�
		    	if(leftdto.getNmoney() != null)
		    	{
		    	dto2.setNmoney(leftdto.getNmoney());//���
		    	}
		    	
		    	List<HtvPayoutmsgmainDto> list2 = null;
		    	if(loginInfo.getPublicparam().indexOf(",collectpayment=1,")>=0&&(!tredto.getStreattrib().equals("2")))
				{
					if(MsgConstant.VOUCHER_NO_3208.equals(voucherGType))
						list2 = (List<HtvPayoutmsgmainDto>)commonDataAccessService.findRsByDtoWithWhere(dto2, " and S_PAYERACCT = '"+loginInfo.getSorgcode()+"27101'");
					else
						list2 = (List<HtvPayoutmsgmainDto>)commonDataAccessService.findRsByDtoWithWhere(dto2, " and S_PAYERACCT <>'"+loginInfo.getSorgcode()+"27101'");
				}else
				{
					list2 = (List<HtvPayoutmsgmainDto>)commonDataAccessService.findRsByDto(dto2);
				}
				if(list2 !=null && list2.size()>0)
				{
					tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
					copyProperties(tvPayoutmsgmainDto,list2.get(0));
					HtvPayoutmsgsubDto qdto = new HtvPayoutmsgsubDto();
					qdto.setSbizno(tvPayoutmsgmainDto.getSbizno());
					List<HtvPayoutmsgsubDto> list3 = (List<HtvPayoutmsgsubDto>)commonDataAccessService.findRsByDto(qdto);
					tvPayoutmsgsubDto = new TvPayoutmsgsubDto();
					copyProperties(tvPayoutmsgsubDto,list3.get(0));
					if(list3!=null&&list3.size()>0)
					{
						TvPayoutmsgsubDto sdto = null;
						List<TvPayoutmsgsubDto> sdtoList = new ArrayList<TvPayoutmsgsubDto>();
						for(HtvPayoutmsgsubDto hsdto:list3)
						{
							sdto = new TvPayoutmsgsubDto();
							copyProperties(sdto,hsdto);
							sdtoList.add(sdto);
						}
						setSubList(sdtoList);
					}
				}else
				{
					throw new ITFEBizException("û����ص�ҵ����Ϣ���ƾ֤��״̬�Ǵ����У�");
				}
			}
			if(tvPayoutmsgmainDto!=null)
				setSbtk(tvPayoutmsgmainDto.getNmoney());
		} catch (ITFEBizException e)
		{
			log.error(e);
			throw new ITFEBizException(e.getMessage());
		} 
    }
    
    @SuppressWarnings("unchecked")
	private void findTvDwbkDto() throws ITFEBizException
    {
    	TvDwbkDto dto = new TvDwbkDto();
    	dto.setSpayertrecode(strecode);//�������
    	dto.setSelecvouno(svoucherno);//ƾ֤���
    	dto.setDvoucher(new java.sql.Date(TimeFacade.strToDate(voudate).getTime()));//ƾ֤����
    	dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS); //����ɹ�
    	if(leftdto.getNmoney() != null)
    	{
    		dto.setFamt(leftdto.getNmoney());//���
    	}
    	
    	try
		{
			List<TvDwbkDto> list = (List<TvDwbkDto>)commonDataAccessService.findRsByDto(dto);
			if(list !=null && list.size()>0)
			{
				tvDwbkDto = list.get(0);
			}else
			{   
				HtvDwbkDto dto2 = new HtvDwbkDto();
				dto2.setSpayertrecode(strecode);//�������
				dto2.setSelecvouno(svoucherno);//ƾ֤���
				dto2.setDvoucher( new java.sql.Date(TimeFacade.strToDate(voudate).getTime()));//ƾ֤����
				dto2.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS); //����ɹ�
		    	if(leftdto.getNmoney() != null)
		    	{
		    		dto2.setFamt(leftdto.getNmoney());//���
		    	}
		    	
		    	List<HtvDwbkDto> list2 = (List<HtvDwbkDto>)commonDataAccessService.findRsByDto(dto2);
				if(list2 !=null && list2.size()>0)
				{
					tvDwbkDto = new TvDwbkDto();
					copyProperties(tvDwbkDto,list2.get(0));
				}else
				{
					throw new ITFEBizException("û����ص�ҵ����Ϣ���ƾ֤��״̬�Ǵ����У�");
				}
			}
			if(tvDwbkDto!=null)
				setTktk(tvDwbkDto.getFamt());
		} catch (ITFEBizException e)
		{
			log.error(e);
			throw new ITFEBizException(e.getMessage());
		} 
    }
    
    private void findTfdirectpaymsgMainDto() throws ITFEBizException
    {
    	TvDwbkDto dto = new TvDwbkDto();
    	dto.setSpayertrecode(strecode);//�������
    	dto.setSelecvouno(svoucherno);//ƾ֤���
    	dto.setDvoucher(new java.sql.Date(TimeFacade.strToDate(voudate).getTime()));//ƾ֤����
    	dto.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS); //����ɹ�
    	if(leftdto.getNmoney() != null)
    	{
    		dto.setFamt(leftdto.getNmoney());//���
    	}
    	
    	try
		{
			List<TvDwbkDto> list = (List<TvDwbkDto>)commonDataAccessService.findRsByDto(dto);
			if(list !=null && list.size()>0)
			{
				tvDwbkDto = list.get(0);
			}else
			{   
				HtvDwbkDto dto2 = new HtvDwbkDto();
				dto2.setSpayertrecode(strecode);//�������
				dto2.setSelecvouno(svoucherno);//ƾ֤���
				dto2.setDvoucher( new java.sql.Date(TimeFacade.strToDate(voudate).getTime()));//ƾ֤����
				dto2.setSstatus(DealCodeConstants.DEALCODE_ITFE_SUCCESS); //����ɹ�
		    	if(leftdto.getNmoney() != null)
		    	{
		    		dto2.setFamt(leftdto.getNmoney());//���
		    	}
		    	
		    	List<HtvDwbkDto> list2 = (List<HtvDwbkDto>)commonDataAccessService.findRsByDto(dto2);
				if(list2 !=null && list2.size()>0)
				{
					tvDwbkDto = new TvDwbkDto();
					copyProperties(tvDwbkDto,list2.get(0));
				}else
				{
					throw new ITFEBizException("û����ص�ҵ����Ϣ���ƾ֤��״̬�Ǵ����У�");
				}
			}
			if(tvDwbkDto!=null)
				setTktk(tvDwbkDto.getFamt());
		} catch (ITFEBizException e)
		{
			log.error(e);
			throw new ITFEBizException(e.getMessage());
		} 
    }
    
    private void clearMSG()
    {
		tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
		tvPayoutmsgsubDto = new TvPayoutmsgsubDto();
		tfDirectpaymsgmainDto = new TfDirectpaymsgmainDto();
		tvDwbkDto = new TvDwbkDto();
		leftdto = new TvVoucherinfoDto();
		svoucherno = null;
		voudate = null;
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    }
    
    
    private void copyProperties (Object dest, Object orig) throws ITFEBizException
	{
		Field[] origFields = orig.getClass().getDeclaredFields();
		for(Field field: origFields)
		{
			try 
			{
				field.setAccessible(true);
				Object origValue = field.get(orig);
				String origFieldName = field.getName();
				Field inField = dest.getClass().getDeclaredField(origFieldName);
				inField.setAccessible(true);
				inField.set(dest,origValue);
			}catch(Exception e)
			{
				log.error("��ѯ��ʷ�����:"+e);
				throw new ITFEBizException("��ѯ��ʷ�����");
			}
		}
	}
    
	/**
	 * Direction: ��ѯ�˻�ƾ֤��Ϣ��(�˻�ƾ֤���ɣ�TCBS�·���)��
	 * ename: querybackvoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String querybackvoucher(Object o){
    	if(strecode == null || "".equals(strecode)){
    		MessageDialog.openMessageDialog(null, "��ѡ�������룡");
    	}    	
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieveForTCBS(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼��");
		}
		backpagingcontext.setPage(pageResponse);
		backcheckList.clear();
		editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
        return "";
    }
    
    
    private String getSQL2202(TvVoucherinfoDto dto){
		 return " s_paymode ='"+MsgConstant.directPay+"' " +
		 		" AND S_STATUS='"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' "+
		 		(StringUtils.isBlank(dto.getScreatdate())?"":" AND D_VOUDATE ='"+CommonUtil.strToDate(dto.getScreatdate())+"'")+
		 		(StringUtils.isBlank(dto.getSvoucherno())?"":" AND S_VOUNO='"+dto.getSvoucherno()+"'") +
		 		(dto.getNmoney()==null?"":" AND F_AMT="+dto.getNmoney()) +		 		 
		 		" AND EXISTS (SELECT 1 FROM TF_PAYBANK_REFUNDMAIN WHERE " +
		 		" TF_PAYBANK_REFUNDMAIN.I_VOUSRLNO = TV_PAYRECK_BANK_BACK.I_VOUSRLNO) "+
		 		" UNION ALL"+
		 		" SELECT * FROM TV_PAYRECK_BANK_BACK WHERE  s_paymode ='"+MsgConstant.directPay+"' " +
		 		" AND S_STATUS='"+DealCodeConstants.DEALCODE_ITFE_SUCCESS+"' "+
		 		(StringUtils.isBlank(dto.getScreatdate())?"":" AND D_VOUDATE ='"+CommonUtil.strToDate(dto.getScreatdate())+"'")+
		 		(StringUtils.isBlank(dto.getSvoucherno())?"":" AND S_VOUNO='"+dto.getSvoucherno()+"'") +
		 		(dto.getNmoney()==null?"":" AND F_AMT="+dto.getNmoney()) +
                " AND NOT EXISTS (SELECT 1 FROM TV_VOUCHERINFO WHERE s_dealno = I_VOUSRLNO) ";		   
	  }
    
    public PageResponse retrieveForTCBS(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	PageResponse page = null;    		
    	try {
    		if(dto.getSvtcode().equals(MsgConstant.MSG_NO_2202)){
        		TvVoucherinfoDto dto = new TvVoucherinfoDto();
    			dto.setSvtcode(MsgConstant.MSG_NO_2202);
    	    	dto.setStrecode(strecode);
    	    	dto.setSvoucherno(backmaindto.getSvouno());
    	    	dto.setScreatdate(backmaindto.getSvoudate());
    	    	dto.setNmoney(backmaindto.getNmoney());
    	    	page= commonDataAccessService.findRsByDtoPaging(TvPayreckBankBackDto.class.newInstance(),
    	    			pageRequest,getSQL2202(dto), null);
        	}else
        	{
        		if(loginInfo.getPublicparam().indexOf(",collectpayment=1,")>=0)
        		{
        			if(MsgConstant.VOUCHER_NO_3208.equals(dto.getSvtcode()))
        				page =  commonDataAccessService.findRsByDtoPaging(backmaindto,pageRequest, " S_PAYEEACCT = '"+loginInfo.getSorgcode()+"27101'", "");
        			else
        				page =  commonDataAccessService.findRsByDtoPaging(backmaindto,pageRequest, " S_PAYEEACCT <> '"+loginInfo.getSorgcode()+"27101'", "");
        		}else
        		{
        			page =  commonDataAccessService.findRsByDtoPaging(backmaindto,pageRequest, "1=1", "");
        		}
        	}
        		
    		return page;
    		
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}	catch (Throwable e) {	
			log.error(e);
			Exception e1=new Exception("��ѯ�����쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return super.retrieve(pageRequest);
	}
    
    
    
    public PageResponse retrieveBatchBack(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	PageResponse page = null;
    	try {
    		page =  commonDataAccessService.findRsByDtoPaging(refundsubDto,pageRequest, "1=1", "");
    		return page;
    		
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}	catch (Throwable e) {	
			log.error(e);
			Exception e1=new Exception("��ѯ�����쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return super.retrieve(pageRequest);
	}
    
    
	/**
	 * Direction: ����ƾ֤��(�˻�ƾ֤���ɣ�TCBS�·���)��
	 * ename: voucherGeneratorForTCBS
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherGeneratorForTCBS(Object o){
    	if(backcheckList==null || backcheckList.size()==0){
    		 MessageDialog.openMessageDialog(null, "��ѡ����Ҫ����ƾ֤�ļ�¼��");
    		 return "";
    	}
    	for(int i = 0; i < backcheckList.size(); i++){
    		String isReturn = "";
    		if(vtcode.equals(MsgConstant.VOUCHER_NO_3208)||vtcode.equals(MsgConstant.VOUCHER_NO_3268)){
    			TvPayoutbackmsgMainDto dto = (TvPayoutbackmsgMainDto) backcheckList.get(i);
    			isReturn = dto.getSisreturn();
    			TvPayoutmsgmainDto findto = new TvPayoutmsgmainDto();
    			findto.setStaxticketno(dto.getSorivouno());
    			findto.setSbackflag(StateConstant.COMMON_YES);
    			List tempList = null;
    			try {
					tempList = commonDataAccessService.findRsByDto(findto);
					if(tempList!=null&&tempList.size()>0)
					{
						if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
								.getCurrentComposite().getShell(), "��ʾ", "ѡ�еļ�¼�д����Ѿ�����ƾ֤�ļ�¼,ȷ���ظ�������?")) {
							return "";
						}
					}else
					{
						HtvPayoutmsgmainDto hfindto = new HtvPayoutmsgmainDto();
						hfindto.setStaxticketno(dto.getSorivouno());
						hfindto.setSbackflag(StateConstant.COMMON_YES);
						tempList = commonDataAccessService.findRsByDto(hfindto);
						if(tempList!=null&&tempList.size()>0)
						{
							if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
									.getCurrentComposite().getShell(), "��ʾ", "ѡ�еļ�¼�д����Ѿ�����ƾ֤�ļ�¼,ȷ���ظ�������?")) {
								return "";
							}
						}
					}
				} catch (ITFEBizException e) {
					MessageDialog.openMessageDialog(null, "����ʧ�ܣ�"+e.toString());
	        		return "";
				}
    			
    		}else if(vtcode.equals(MsgConstant.MSG_NO_2202)){
    			TvPayreckBankBackDto dto = (TvPayreckBankBackDto) backcheckList.get(i);
    			isReturn = dto.getSbackflag();
    		}    		
    		if(StateConstant.COMMON_YES.equals(isReturn)){
    			if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
						.getCurrentComposite().getShell(), "��ʾ", "ѡ�еļ�¼�д����Ѿ�����ƾ֤�ļ�¼,ȷ���ظ�������?")) {
					return "";
				}
    		}
    	}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "ȷ��Ҫ��ѡ�еļ�¼���ɵ���ƾ֤��?")) {
			return "";
		}
    	backmaindto.setStrecode(strecode);
        List list = new ArrayList();
        list.add(vtcode);
        list.add(backmaindto);
        list.add(backcheckList);
        int successcount=0;
        int failcount=0;
    	List<String> errorList = new ArrayList();
        try {
        	MulitTableDto mulitTableDto = backVoucherService.voucherGenerateForTCBS(list);
        	successcount = mulitTableDto.getTotalCount();
    		errorList = mulitTableDto.getErrorList();
    		String msg = "";
    		if(errorList.size()>0){
    			for(int i=0; i<errorList.size(); i++){
    				msg += errorList.get(i)+"\r\n";
    			}
    		}
			MessageDialog.openMessageDialog(null, "ƾ֤���ɲ�����ɣ�����ƾ֤�ɹ�����Ϊ��"+successcount+ " ʧ�ܱ���Ϊ��"+mulitTableDto.getErrorCount()+" ��\r\n"+msg);
			this.querybackvoucher(o);
			return null;
		} catch (ITFEBizException e) {
			MessageDialog.openMessageDialog(null, e.getMessage());
    		return "";
		}
    }
    
    /**
     * ƾ֤����
     * @return
     * @throws ITFEBizException
     */
	@SuppressWarnings("unchecked")
	private String voucherIsRepeat(List<TvVoucherinfoDto> list) throws ITFEBizException{		
    	//��ž�������µ��ظ�ƾ֤
    	List<TvVoucherinfoDto> lists=new ArrayList<TvVoucherinfoDto>();
    	//����ƾ֤�б�
    	for (TvVoucherinfoDto tvVoucherinfoDto : list) {
    		List<TvVoucherinfoDto> voucherList = commonDataAccessService.findRsByDto(tvVoucherinfoDto);
			if(voucherList!=null&&voucherList.size()>0){
				lists.add(tvVoucherinfoDto);
			}
		}
    	StringBuffer sbf=new StringBuffer();
    	if(lists!=null&&lists.size()>0){
    		for (TvVoucherinfoDto tvVoucherinfoDto : lists) {
    			sbf.append("����Ϊ��")
        		.append(tvVoucherinfoDto.getStrecode())
        		.append(", ҵ������Ϊ��")
        		.append(tvVoucherinfoDto.getSvtcode())
        		.append(", ƾ֤����Ϊ��")
        		.append(tvVoucherinfoDto.getScreatdate())
        		.append(" ƾ֤������,")
        		.append("\r\n");
			}
    	}
		return sbf.toString();
    	
    }
    
    /**
     * ����ƾ֤
     */
     @SuppressWarnings("unchecked")
	public String voucherGenerator(Object o)
     {
    	 try
 		{
    		 if(loginInfo.getPublicparam().contains(",itfegenerator=true,"))
			 {
					if (!AdminConfirmDialogFacade.open("��Ҫ������Ȩ�ſ�����ƾ֤��")) {
						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
						return null;
					}
			 }
    		 if(isHasGenerator())
    		 {
    			 if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
							.getCurrentComposite().getShell(), "��ʾ", "ѡ�еļ�¼�д����Ѿ�����ƾ֤�ļ�¼,ȷ���ظ�������?")) {
						return "";
					}
    		 }
    		 if(!isQueried())
    		 {
    			 MessageDialog.openMessageDialog(null, "���Ȳ�ѯҵ����Ϣ������ƾ֤��");
 	    		 return "";
    		 }
    		 
    		 TvVoucherinfoDto vDto = new TvVoucherinfoDto();
    		 List<Object> prams = new ArrayList<Object>();
    		 
    		 TsConvertfinorgDto cforgDto = new TsConvertfinorgDto();
    		 cforgDto.setStrecode(this.strecode);
    		 List<TsConvertfinorgDto> list = (List<TsConvertfinorgDto>)commonDataAccessService.findRsByDto(cforgDto);
    		 cforgDto = list.get(0);
    		 
			 if(voucherGType.equals(MsgConstant.VOUCHER_NO_3208)||voucherGType.equals(MsgConstant.VOUCHER_NO_3268))
		     	{
				 	 if(getSbtk()==null||"".equals(getSbtk()))
				 	 {
				 		MessageDialog.openMessageDialog(null, "�˿����Ϊ�գ�");
				 		return "";
				 	 }else if(tvPayoutmsgmainDto.getNmoney().compareTo(getSbtk())<0)
				 	 {
				 		MessageDialog.openMessageDialog(null, "�˿���ɴ��ڲ����");
				 		return "";
				 	 }
				 	if(getSbtk()!=null&&!"".equals(getSbtk())&&String.valueOf(getSbtk()).length()>20)
				 	 {
				 		MessageDialog.openMessageDialog(null, "�˻ؽ�������");
				 		return "";
				 	 }
				 	 if(StringUtils.isBlank(payoutBackReason)){
		    			 MessageDialog.openMessageDialog(null, "ʵ���ʽ��˻�ԭ����Ϊ��!");
		    			 return "";
		    		 }
				 	if(loginInfo.getPublicparam().indexOf(",sh,")>=0&&StringUtils.isNotBlank(tvPayoutmsgmainDto.getSbusinesstypecode().trim())&&tvPayoutmsgmainDto.getSbusinesstypecode().trim().equals(StateConstant.BIZTYPE_CODE_BATCH)){
						 MessageDialog.openMessageDialog(null, "���ж˲�������ʵ���ʽ������˿�!");
		    			 return "";
					 }
					prams.add(voucherGType);
					prams.add(cforgDto);
					prams.add(getSbtk());//prams.add(this.tvPayoutmsgmainDto.getNmoney());
					vDto = backVoucherService.getTvVoucherinfoDto(prams);
					if(loginInfo.getPublicparam().indexOf(",send3208=more,")>=0)
					{
						TvVoucherinfoDto findvdto = new TvVoucherinfoDto();
						findvdto.setSdealno(tvPayoutmsgmainDto.getSbizno());
						List vouchlist = commonDataAccessService.findRsByDto(findvdto);
						if(vouchlist!=null&&vouchlist.size()>0)
						{
							findvdto = (TvVoucherinfoDto)vouchlist.get(0);
							if(!findvdto.getSattach().equals("") && findvdto.getSattach() != null&& findvdto.getSattach().trim().length() == 3)
								vDto.setSext5(findvdto.getSattach());
						}
					}
					 this.tvPayoutmsgmainDto.setSbackflag("1");
					 this.tvPayoutmsgmainDto.setShold2(String.valueOf(getSbtk()));//ʵ���˿�
		    		 List<Object> prams1 = new ArrayList<Object>();
		    		 prams1.add(voucherGType);
		    		 prams1.add(tvPayoutmsgmainDto);
		    		 prams1.add(vDto);
		    		 List<TvPayoutmsgsubDto> subs = new ArrayList<TvPayoutmsgsubDto>();
		    		 if(this.tvPayoutmsgmainDto.getNmoney().compareTo(getSbtk())==0)
		    			 subs.addAll(getSubList());
		    		 else
		    			 subs.add(tvPayoutmsgsubDto);
		    		 prams1.add(subs);
		    		 prams1.add(payoutBackReason);//��ʵ���˿��˻�ԭ�����̨
					 backVoucherService.voucherGenerate(prams1);
//					 commonDataAccessService.updateData(this.tvPayoutmsgmainDto);��̨���ɸ���
					 tvPayoutmsgmainDto = new TvPayoutmsgmainDto();
		     	}else if(voucherGType.equals(MsgConstant.VOUCHER_NO_3210)|| voucherGType.equals(MsgConstant.VOUCHER_NO_3251)){
		     		if(getTktk()==null||"".equals(getTktk()))
				 	 {
				 		MessageDialog.openMessageDialog(null, "�˻ؽ���Ϊ�գ�");
				 		 return "";
				 	 }
		     		if(getTktk()!=null&&!"".equals(getTktk())&&String.valueOf(getTktk()).length()>20)
				 	 {
				 		MessageDialog.openMessageDialog(null, "�˻ؽ�������");
				 		 return "";
				 	 }
				 	 if(StringUtils.isBlank(dwbkBackReason)){
		    			 MessageDialog.openMessageDialog(null, "�����˸��˻�ԭ����Ϊ��!");
		    			 return "";
		    		 }
		     		prams.add(voucherGType);
					prams.add(cforgDto);
					prams.add(getTktk());//prams.add(this.tvDwbkDto.getFamt());
		     		vDto = backVoucherService.getTvVoucherinfoDto(prams);
		     		//������Sdetailhold1��������
		     		if(commonDataAccessService.getSrcNode().equals("000057400006")){
		     			tvDwbkDto.setSdetailhold1(String.valueOf(getTktk()));
		     		}else{
		     			tvDwbkDto.setShold2(String.valueOf(getTktk()));
		     		}
			   		tvDwbkDto.setCbckflag("1");
		     		List<Object> prams1 = new ArrayList<Object>();
		     		prams1.add(voucherGType);
			   		prams1.add(tvDwbkDto);
			   		prams1.add(vDto);
			   		prams1.add(dwbkBackReason);//�������˸��˻�ԭ�����̨
					backVoucherService.voucherGenerate(prams1);
					commonDataAccessService.updateData(tvDwbkDto);
					tvDwbkDto = new TvDwbkDto();
		     	}else if(voucherGType.equals(MsgConstant.VOUCHER_NO_5201)){
		     		if(tfDirectpaymsgmainDto.getNbackmoney()==null){
				 		MessageDialog.openMessageDialog(null, "�˻ؽ���Ϊ�գ�");
				 		 return "";
				 	}
		     		if(tfDirectpaymsgmainDto.getNbackmoney().compareTo(tfDirectpaymsgmainDto.getNpayamt())>0){
				 		MessageDialog.openMessageDialog(null, "�˻ؽ��ܴ���ƾ֤��");
				 		 return "";
				 	}
		     		if(StringUtils.isBlank(tfDirectpaymsgmainDto.getSbckreason())){
				 		MessageDialog.openMessageDialog(null, "�˻�ԭ����Ϊ�գ�");
				 		 return "";
				 	}
		     		if(loginInfo.getPublicparam().indexOf(",sh,")>=0&&StringUtils.isNotBlank(tfDirectpaymsgmainDto.getSbusinesstypecode().trim())&&tfDirectpaymsgmainDto.getSbusinesstypecode().trim().equals(StateConstant.BIZTYPE_CODE_BATCH)){
						 MessageDialog.openMessageDialog(null, "���ж˲�������ֱ��֧�������˿�!");
		    			 return "";
					 }
		     		if(tfDirectpaymsgmainDto.getNbackmoney().compareTo(tfDirectpaymsgmainDto.getNpayamt())==0)
		     			tfDirectpaymsgmainDto.setSrefundtype("2");
		     		else 
		     			tfDirectpaymsgmainDto.setSrefundtype("1");
		     		prams.add(MsgConstant.VOUCHER_NO_2203);
					prams.add(tfDirectpaymsgmainDto);
					prams.add(leftdto);
					backVoucherService.voucherGenerate(prams);
					tfDirectpaymsgmainDto = new TfDirectpaymsgmainDto();
		     	}			 
		} catch (ITFEBizException e1)
		{
			MessageDialog.openMessageDialog(null, e1.getMessage());
    		return "";
		} catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			return "";
		}finally{
			setSbtk(null);
	    	setTktk(null);
		}
    	 MessageDialog.openMessageDialog(null, "ƾ֤���ɲ����ɹ���");
 		 voucherGSearch(null);
 		 editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
         return super.voucherGenerator(o);
    }
     
    private boolean isHasGenerator() throws ITFEBizException
    {
    	boolean flag = false;
    	if(MsgConstant.VOUCHER_NO_3208.equals(voucherGType)||MsgConstant.VOUCHER_NO_3268.equals(voucherGType))
    	{
    		if("1".equals(this.tvPayoutmsgmainDto.getSbackflag()))
    		{
    			flag = true;
    		}else
    		{
    			TvPayoutbackmsgMainDto dto = new TvPayoutbackmsgMainDto();
    			dto.setSorivouno(this.tvPayoutmsgmainDto.getStaxticketno());
    			dto.setSbackflag(StateConstant.COMMON_YES);
    			List tempList = commonDataAccessService.findRsByDto(dto);
				if(tempList!=null&&tempList.size()>0)
					return true;
    		}
    	}else if(MsgConstant.VOUCHER_NO_3210.equals(voucherGType)|| MsgConstant.VOUCHER_NO_3251.equals(voucherGType)){
    		if("1".equals(this.tvDwbkDto.getCbckflag()))
    		{
    			flag = true;
    		}
    	}else if(MsgConstant.VOUCHER_NO_5201.equals(voucherGType)){
    		return "1".equals(this.tfDirectpaymsgmainDto.getSbackflag());
    	}
    	return flag;
    } 
    
    private boolean isQueried() throws ITFEBizException
    {
    	boolean flag = false;
    	if(MsgConstant.VOUCHER_NO_3208.equals(voucherGType)||MsgConstant.VOUCHER_NO_3268.equals(voucherGType))
    	{
    		if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(this.tvPayoutmsgmainDto.getSstatus()))
    		{
    			flag = true;
    		}
    	}else if(MsgConstant.VOUCHER_NO_3210.equals(voucherGType)||MsgConstant.VOUCHER_NO_3251.equals(voucherGType))
    	{
    		if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(this.tvDwbkDto.getSstatus()))
    		{
    			flag = true;
    		}
    	}else if(MsgConstant.VOUCHER_NO_5201.equals(voucherGType)){
    		if(DealCodeConstants.DEALCODE_ITFE_SUCCESS.equals(this.tfDirectpaymsgmainDto.getSstatus()))
    		{
    			flag = true;
    		}
    	}
    	return flag;
    }
    
    public String getVoucherGType()
	{
		return voucherGType;
	}
    public void setVoucherGType(String voucherGType) 
    {
		this.voucherGType = voucherGType;
		List contreaNamesVisibleFalse = new ArrayList();//�ر���ʾƾ֤һ����
		List contreaNamesVisibbleTrue = new ArrayList();//��ʾƾ֤һ����
		//��̬��ʾ
		if (MsgConstant.VOUCHER_NO_3208.equals(voucherGType)||MsgConstant.VOUCHER_NO_3268.equals(voucherGType)) {
			contreaNamesVisibleFalse.add("�����˸��˿�ƾ֤��Ϣһ����");
			contreaNamesVisibleFalse.add("ֱ��֧���˿�ƾ֤��Ϣһ����");
			contreaNamesVisibbleTrue.add("ʵ���ʽ��˿�ƾ֤��Ϣһ����");
		}else if(MsgConstant.VOUCHER_NO_3210.equals(voucherGType) || MsgConstant.VOUCHER_NO_3251.equals(voucherGType)){
			contreaNamesVisibleFalse.add("ʵ���ʽ��˿�ƾ֤��Ϣһ����");
			contreaNamesVisibleFalse.add("ֱ��֧���˿�ƾ֤��Ϣһ����");
			contreaNamesVisibbleTrue.add("�����˸��˿�ƾ֤��Ϣһ����");
		}else if(MsgConstant.VOUCHER_NO_5201.equals(voucherGType)){
			contreaNamesVisibleFalse.add("�����˸��˿�ƾ֤��Ϣһ����");
			contreaNamesVisibleFalse.add("ʵ���ʽ��˿�ƾ֤��Ϣһ����");
			contreaNamesVisibbleTrue.add("ֱ��֧���˿�ƾ֤��Ϣһ����");
		}
		clearMSG();
		MVCUtils.setContentAreaVisible(editor, contreaNamesVisibleFalse, false);
		MVCUtils.setContentAreaVisible(editor, contreaNamesVisibbleTrue, true);
		pagingcontext.setPage(new PageResponse());
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged();
	}
    
	public String getStrecode()
	{
		return strecode;
	}
	public void setStrecode(String strecode)
	{
		this.strecode = strecode;
	}
	public String getSvoucherno()
	{
		return svoucherno;
	}
	public void setSvoucherno(String svoucherno)
	{
		this.svoucherno = svoucherno.replace("'", "");
	}
	public String getVoudate()
	{
		return voudate;
	}
	public void setVoudate(String voudate)
	{
		this.voudate = voudate;
	}
	public List getVoucherTypeGList()
	{
		return voucherTypeGList;
	}
	public void setVoucherTypeGList(List voucherTypeGList)
	{
		this.voucherTypeGList = voucherTypeGList;
	}
    //���Tabƾ֤���ɽ�����ط���-----end
    
	//�Ҳ�Tabƾ֤�˻ش��������ط���-----start
	/**
     * ��ȡOCX�ؼ�url
     * @return
     */
    public String getOcxVoucherServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("��ȡOCX�ؼ�URL��ַ���������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }
    
    /**
     * ��ȡǩ�·����ַ
     * @return
     */
    public String getOCXStampServerURL(){
    	String ls_URL = "";
    	try {
    		ls_URL = voucherLoadService.getOCXStampServerURL();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("��ȡOCX�ؼ�ǩ�·���URL��ַ���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return ls_URL;
    }
    
    /**
	 * Direction: ��ѯ 
	 * ename: search
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherSearch(Object o){
    	if(dto.getSvtcode()==null||dto.getSvtcode().equals("")){
    		MessageDialog.openMessageDialog(null, "����ѡ��ƾ֤���ͣ�");
    		return "";
    	}
    	
    	refreshTable();
        return super.voucherSearch(o);
    }
    
    private void init() {
		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieve(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼����������ƾ֤��");
		}
		pagingcontext.setPage(pageResponse);
	}
	/**
	 * Direction: ȫѡ
	 * ename: selectAll
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAll(Object o){
    	 if(checkList==null||checkList.size()==0){
         	checkList = new ArrayList();
         	checkList.addAll(pagingcontext.getPage().getData());
         }
         else
         	checkList.clear();
         this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
         return super.selectAll(o);
    }
    
	/**
	 * Direction: ȫѡ(�˻�ƾ֤���ɣ�TCBS�·���)
	 * ename: selectAllBack
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String selectAllBack(Object o){
    	if(backcheckList==null||backcheckList.size()==0){
    		backcheckList = new ArrayList();
    		backcheckList.addAll(backpagingcontext.getPage().getData());
         }
         else
        	 backcheckList.clear();
         this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
        return super.selectAllBack(o);
    }
    
    /**
	 * Direction: ɾ��ƾ֤
	 * ename: deletegenvoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String delgenvoucher(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫɾ������ƾ֤���ݼ�¼��");
    		return "";
    	}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ɾ��������?")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
			if(!DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus().trim())){
				MessageDialog.openMessageDialog(null, "ֻ��״̬Ϊ����ɹ������ݿ���ɾ����");
        		return "";
			}
    	}
    	try {
	    	for(TvVoucherinfoDto infoDto:checkList)
	    	{
	    		commonDataAccessService.delete(infoDto);
	    	}
	    	refreshTable();
    	} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("ɾ������ʧ�ܣ�",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}
        return "";
    }
	/**
	 * Direction: ���͵���ƾ֤
	 * ename: sendReturnVoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherSend(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ���͵���ƾ֤�ļ�¼��");
    		return "";
    	}
    	int count=0;
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ�з��͵���ƾ֤������?")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
			if(!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus().trim()))){
				MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ \"ǩ�³ɹ�\" �ļ�¼��");
        		return "";
			}
			if(loginInfo.getPublicparam().contains(",send3208=more,"))
			{
				if(infoDto.getSext5()==null||infoDto.getSext5().length()!=3)
				{
					if(recvDept==null||"".equals(recvDept)||recvDept.length()!=3)
					{
						MessageDialog.openMessageDialog(null, "��ѡ����ƾ֤�Ľ��շ���");
		        		return "";
					}else
					{
						infoDto.setSext5(recvDept);
					}
				}
			}
	    }		
    	try {   		
    		 count=voucherLoadService.voucherReturnSuccess(checkList);
    		 MessageDialog.openMessageDialog(null, "���͵���ƾ֤   "+checkList.size()+" �����ɹ�����Ϊ��"+count+" ��");
    		 refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("���͵���ƾ֤����������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("���͵���ƾ֤����������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			
			return "";
		}	
    	return super.voucherSend(o);
         
    }

	/**
	 * Direction: ƾ֤�鿴
	 * ename: voucherView
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherView(Object o){
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴�ļ�¼��");
    		return "";
    	}
    	try{
        	ActiveXCompositeBackVoucherOcx.init(0);
        	
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
    		log.error(e);
    		Exception e1=new Exception("ƾ֤�鿴�쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
    	}
        return super.voucherView(o);
    }
    
    /**
	 * Direction: ǩ��
	 * ename: transferCompletedStamp
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherStamp(Object o){    	
    	boolean ocxflag=false;
    	List<TvVoucherinfoDto> checkList=new ArrayList<TvVoucherinfoDto>();
    	String stamp=null;
    	TvVoucherinfoDto dto=new TvVoucherinfoDto();    
    	if(o instanceof List){
			List  ocxStampList=(List) o;
    		String stampname=(String) ocxStampList.get(0);       		
    		dto=(TvVoucherinfoDto) ocxStampList.get(1);    		
    		TsStamppositionDto stampPostionDto=new TsStamppositionDto();
    		stampPostionDto.setSorgcode(dto.getSorgcode());
    		stampPostionDto.setStrecode(dto.getStrecode());
    		stampPostionDto.setSvtcode(dto.getSvtcode());
    		stampPostionDto.setSstampname(stampname);
				try {
					stampPostionDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampPostionDto).get(0);
				} catch (ITFEBizException e) {
					log.error(e); 
					Exception e1=new Exception("ǩ�³����쳣��",e);			
					MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
					return "";
				}			
			stamp=stampPostionDto.getSstamptype();
			this.stamp=stampPostionDto.getSstamptype();
			checkList.add(dto);
			ocxflag=true;			
    	}
    	if(!ocxflag){
    		stamp=this.stamp;
    		checkList=this.checkList;
    		dto=this.dto;
    	}
    	int count=0;
    	if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ��ǩ�����ͣ�");
			return null;
		}
    	
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫǩ�µļ�¼��");
    		return "";
    	}
    	
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ǩ�²�����")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
	    		if(!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus().trim()))){
	    			MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ����ɹ��ļ�¼��");	    			
	        		return "";
	    		}
	    	}		
    	try {
    		if(!((TvVoucherinfoDto)checkList.get(0)).getSvtcode().equals(dto.getSvtcode())){
    			MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");    			
    			return "";       		
    		}    		
    		TsUsersDto uDto=new TsUsersDto();
    		uDto.setSorgcode(loginInfo.getSorgcode());
    		uDto.setSusercode(loginInfo.getSuserCode());
    		uDto=(TsUsersDto) commonDataAccessService.findRsByDto(uDto).get(0);
    		TsStamppositionDto stampDto=new TsStamppositionDto();
    		stampDto.setSvtcode(dto.getSvtcode());
    		stampDto.setSorgcode(loginInfo.getSorgcode());
    		stampDto.setSstamptype(stamp);
    		stampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampDto).get(0);
    		String permission= uDto.getSstamppermission();
    		boolean flag=true;
    		if(permission==null||permission.equals("")){
    			flag=false;
    		}else{
    			if(permission.indexOf(",")<0){
        			if(!permission.equals(stamp)){
        				flag=false;
        			}       			
        		}else{
        			flag=false;
        			String[] permissions=permission.split(",");
        			for(int i=0;i<permissions.length;i++){
        				if(permissions[i].equals(stamp)){
        					flag=true;
        					break;
        				}
        			}        			
        		}
    		}    		
    		if(flag==false){
				MessageDialog.openMessageDialog(null, "��ǰ�û���  \""+stampDto.getSstampname()+"\"  ǩ��Ȩ�ޣ�");    			
    			return "";
			}
    		TsTreasuryDto tDto=new TsTreasuryDto();
    		TsStamppositionDto sDto=new TsStamppositionDto();
    		Map map=new HashMap();
    		String usercode=uDto.getSusercode();
    		String stampuser="";
    		String stampid="";
    		for(TvVoucherinfoDto vDto:checkList){
    			map.put(vDto.getStrecode(), "");
    			stampid=vDto.getSstampid();
    			if(stampid!=null&&!stampid.equals("")){
					String[] stampids=stampid.split(",");
					for(int i=0;i<stampids.length;i++){
						if(stamp.equals(stampids[i])){
							MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"+vDto.getSvoucherno()+" ��ǩ  \""+stampDto.getSstampname()+"\" ��ͬһƾ֤�����ظ�ǩ�£�");			    			
			    			return "";
						}
					}
    			}
    			if(!stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)&&!stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)&&!stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH)){
    				stampuser=vDto.getSstampuser();
    				if(stampuser!=null&&!stampuser.equals("")){
						String[] stampusers=stampuser.split(",");
						for(int i=0;i<stampusers.length;i++){							
							if(usercode.equals(stampusers[i])){
								TsStamppositionDto tstampDto=new TsStamppositionDto();
								tstampDto.setSorgcode(loginInfo.getSorgcode());
								tstampDto.setSvtcode(dto.getSvtcode());
								String[]  stampids=vDto.getSstampid().split(",");
								for(int j=0;j<stampids.length;j++){
									if(!stampids[i].equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
										tstampDto.setSstamptype(stampids[i]);
										break;
									}
								}
								tstampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(tstampDto).get(0);
								MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"+vDto.getSvoucherno()+" ��ǰ�û���ǩ  \""+tstampDto.getSstampname()+"\" ��ͬһ�û�ֻ��ǩһ��˽�£���ѡ�������û���");    			    			
    			    			return "";
							}
						}
					
    				}
        		}
    		}    		
    		Iterator it=map.keySet().iterator();    		
    		List lists=new ArrayList();
    		List list=null;
    		List sinList=null;
    		List<TsStamppositionDto> sList=null;
    		List vList=null;
    		String strecode="";
    		String xml="";
    		while(it.hasNext()){
    			 strecode=it.next()+"";
    			 vList=new ArrayList();
    			 tDto=new TsTreasuryDto();
    			 sDto=new TsStamppositionDto();
    			 sList=new ArrayList<TsStamppositionDto>();
    			 list=new ArrayList();
    			try{
    				tDto.setSorgcode(loginInfo.getSorgcode());
    				tDto.setStrecode(strecode);
        			tDto=(TsTreasuryDto) commonDataAccessService.findRsByDto(tDto).get(0);
    			}catch(Exception e){
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ�����в����ڣ�");
    				
    				return "";
    			}    			
    			try{
    				sDto.setSorgcode(loginInfo.getSorgcode());
        			sDto.setStrecode(strecode);
        			sDto.setSvtcode(dto.getSvtcode());
        			sList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(sDto);
        			sDto.setSstamptype(stamp);
        			sDto= (TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
        			
    			}catch(Exception e){
    			
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "����������룺"+strecode+" "+stampDto.getSstampname()+" ����δά���� ");
    				
    				return "";
    			}    			
    			if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
    				if(tDto.getSrotarycertid()==null||tDto.getSrotarycertid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"֤��ID ����δά���� ");
        				
        				return "";
        			}else if(tDto.getSrotaryid()==null||tDto.getSrotaryid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"ӡ��ID ����δά���� ");
        				
        				return "";
        			}
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){

    				if(tDto.getScertid()==null||tDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"֤��ID ����δά���� ");
        				
        				return "";
        			}else if(tDto.getSstampid()==null||tDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"ӡ��ID ����δά���� ");
        				
        				return "";
        			}
    			
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
				{
    				if(tDto.getSattachcertid()==null||tDto.getSattachcertid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"֤��ID ����δά���� ");
        				
        				return "";
        			}else if(tDto.getSattachid()==null||tDto.getSattachid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"ӡ��ID ����δά���� ");
        				
        				return "";
        			}
				}else{
    				if(uDto.getScertid()==null||uDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "��ǰ�û�  "+stampDto.getSstampname()+"  ֤��ID����δά���� ");        				
        				return "";
        			}else if(uDto.getSstampid()==null||uDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "��ǰ�û�   "+stampDto.getSstampname()+"  ӡ��ID����δά���� ");        				
        				return "";
        			}    		    			
    			}
    			for(TvVoucherinfoDto vDto:checkList){
    				if(vDto.getStrecode().equals(strecode)){    					
    					sinList=new ArrayList();
    					sinList.add(vDto);
						stampid=vDto.getSstampid();	
						String seq=sDto.getSstampsequence();   						
						if(seq!=null&&!seq.equals("")){
							List<String> seqList=new ArrayList<String>();
    						for(int i=0;i<sList.size();i++){
    							TsStamppositionDto tsDto=(TsStamppositionDto) sList.get(i);
    							if(tsDto.getSstampsequence()!=null&&!tsDto.getSstampsequence().equals("")){
    								seqList.add(tsDto.getSstampsequence());
    							}
    						}
    						if(seqList!=null&&seqList.size()>0){
    							String[] seqs=seqList.toArray(new String[seqList.size()]);
    							ActiveXCompositeVoucherOcx.sortStringArray(seqs);    							
    							String temp="";
    							for(int i=seqs.length-1;i>-1;i--){
    								if(Integer.parseInt(seqs[i])<Integer.parseInt(seq)){
    									temp=seqs[i];
    									break;
    								}
    							}
    							if(!temp.equals("")){
    								List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
    								TsStamppositionDto tsDto=new TsStamppositionDto();
    								tsDto.setSorgcode(loginInfo.getSorgcode());
    								tsDto.setStrecode(strecode);
    								tsDto.setSvtcode(vDto.getSvtcode());
    								tsDto.setSstampsequence(temp);
    			        			tsList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(tsDto);
    			        			String err="";
    			        			for(TsStamppositionDto tstampDto:tsList){
    			        				err=tstampDto.getSstampname()+" , "+err;
    			        			}
    			        			err=err.substring(0,err.lastIndexOf(","));
    			        			if(stampid==null||stampid.equals("")){
        								MessageDialog.openMessageDialog(null, "������룺"+vDto.getStrecode()+" ƾ֤����: "+vDto.getSvtcode()+vDto.getSvoucherno()+" \""+stampDto.getSstampname()+"\"ǩ��ǰ���� \""+err+"\"ǩ�£�");
            			    			
            			    			return "";
        							
    			        			}else{
    			        				err="";
    			        				String[] stampids=stampid.split(",");
			        					List<TsStamppositionDto> tsList1=new ArrayList<TsStamppositionDto>();
			        					for(int j=0;j<tsList.size();j++){
			        						for(int i=0;i<stampids.length;i++){
    			        						if(stampids[i].equals(tsList.get(j).getSstamptype())){
    			        							tsList1.add(tsList.get(j));
    			        						}
    			        					}
		        						}
			        					tsList.removeAll(tsList1);
			        					if(tsList.size()>0){
			        						for(TsStamppositionDto tstampDto:tsList){
		    			        				err=tstampDto.getSstampname()+" , "+err;
		    			        			}
			        						err=err.substring(0,err.lastIndexOf(","));
			        						MessageDialog.openMessageDialog(null, "������룺"+vDto.getStrecode()+" ƾ֤����: "+vDto.getSvtcode()+" \""+stampDto.getSstampname()+"\"ǩ��ǰ���� \""+err+"\"ǩ�£�");
	            			    			
	            			    			return "";
			        					}
    			        			}
    			        		
    							}
    						}
						}
						if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_OFFICIALSTAMP)){								
								sinList.add(ActiveXCompositeBackVoucherOcx.getVoucherStamp(vDto, tDto.getScertid(), sDto.getSstampposition(), tDto.getSstampid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP)){								
								sinList.add(ActiveXCompositeBackVoucherOcx.getVoucherStamp(vDto, tDto.getSrotarycertid(), sDto.getSstampposition(), tDto.getSrotaryid()));   	    												
							}
						}else if(stamp.equals(MsgConstant.VOUCHERSAMP_ATTACH))
						{
							if(!voucherLoadService.getOfficialStamp().equals(MsgConstant.VOUCHER_ROTARYSTAMP))
								sinList.add(ActiveXCompositeBackVoucherOcx.getVoucherStamp(vDto, tDto.getSattachcertid(), sDto.getSstampposition(), tDto.getSattachid()));
						}else{
							if(!loginInfo.getPublicparam().contains(",jbrstamp=server,"))
							{
								sinList.add(ActiveXCompositeBackVoucherOcx.getVoucherStamp(vDto, uDto.getScertid(), sDto.getSstampposition(),uDto.getSstampid()));
							}
						}
    					vList.add(sinList);
    				}
    			}
    			list.add(uDto);
    			list.add(tDto);
    			list.add(sDto);
    			list.add(sList.size());
    			list.add(vList);    			
    			lists.add(list);
    		}
    		count=voucherLoadService.voucherStamp(lists);
    		if(ocxflag){
    			
    			return count+"";
    		}
    		MessageDialog.openMessageDialog(null, "ƾ֤ǩ��   "+checkList.size()+" �����ɹ�����Ϊ��"+count+" ��");
    		refreshTable();
    	}catch (ITFEBizException e) {
    		log.error(e);    		
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("ǩ�²��������쳣��",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
			
		}		
        return super.voucherStamp(o);
    }
    
    /**
	 * Direction:ǩ�³���
	 * ename: transferCompletedStampCancle
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String voucherStampCancle(Object o){
    	int count=0;        	
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫǩ�³����ļ�¼��");
    		return "";
    	}
    	if ((null == stamp || stamp.trim().length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ��ǩ�����ͣ�");
			return null;
		}
    	if (!org.eclipse.jface.dialogs.MessageDialog.openConfirm(this.editor
				.getCurrentComposite().getShell(), "��ʾ", "��ȷ��Ҫ��ѡ�еļ�¼ִ��ǩ�³���������")) {
			return "";
		}
    	for(TvVoucherinfoDto infoDto:checkList){
	    		if(!(DealCodeConstants.VOUCHER_SUCCESS_NO_BACK.equals(infoDto.getSstatus()))&&!(DealCodeConstants.VOUCHER_STAMP.equals(infoDto.getSstatus()))){
	    			MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ����ɹ���ǩ�³ɹ��ļ�¼��");
	    			
	        		return "";
	    		}
	    	}
		
    	try {
    		if(!((TvVoucherinfoDto)checkList.get(0)).getSvtcode().equals(dto.getSvtcode())){
    			MessageDialog.openMessageDialog(null, "ѡ��Ĳ���[ƾ֤����]��У�����ݲ�����");
    			
    			return "";
        		
    		}
   
    		TsUsersDto uDto=new TsUsersDto();
    		uDto.setSorgcode(loginInfo.getSorgcode());
    		uDto.setSusercode(loginInfo.getSuserCode());
    		uDto=(TsUsersDto) commonDataAccessService.findRsByDto(uDto).get(0);
    		TsStamppositionDto stampDto=new TsStamppositionDto();
    		stampDto.setSvtcode(dto.getSvtcode());
    		stampDto.setSorgcode(loginInfo.getSorgcode());
    		stampDto.setSstamptype(stamp);
    		stampDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(stampDto).get(0);
    		String permission= uDto.getSstamppermission();
    		boolean flag=true;
    		if(permission==null||permission.equals("")){
    			flag=false;
    		}else{
    			if(permission.indexOf(",")<0){
        			if(!permission.equals(stamp)){
        				flag=false;
        			}
        			
        		}else{
        			flag=false;
        			String[] permissions=permission.split(",");
        			for(int i=0;i<permissions.length;i++){
        				if(permissions[i].equals(stamp)){
        					flag=true;
        					break;
        				}
        			}
        			
        		}
    		}
    		boolean managerPermission=false;
    		if(flag==false){
				MessageDialog.openMessageDialog(null, "��ǰ�û���  \""+stampDto.getSstampname()+"\"  ǩ��Ȩ�ޣ�����ͨ��������Ȩ����ǩ��");
				String msg = "��Ҫ������Ȩ���ܲ��ܳ���ǩ�£�";
				if(!AdminConfirmDialogFacade.open("B_267", "ƾ֤�˻ش���", "��Ȩ�û�"+loginInfo.getSuserName()+"����ǩ��", msg)){
					DisplayCursor.setCursor(SWT.CURSOR_ARROW);
					return null;
				}else{
					managerPermission=true;
				}
				
			}
    		TsTreasuryDto tDto=new TsTreasuryDto();
    		TsStamppositionDto sDto=new TsStamppositionDto();
    		Map map=new HashMap();
    		String usercode=uDto.getSusercode();
    		String stampuser="";
    		String stampid="";    		
    		for(TvVoucherinfoDto vDto:checkList){
    			usercode=uDto.getSusercode();
    			map.put(vDto.getStrecode(), "");
    			stampid=vDto.getSstampid();
    			int j=-1;
    			if(stampid==null||stampid.equals("")){
    				flag=false;
    			}else{
    				flag=false;
					String[] stampids=stampid.split(",");
					for(int i=0;i<stampids.length;i++){
						if(stamp.equals(stampids[i])){
							flag=true;
							j=i;
							break;
							
						}
					}
    			
    			}if(flag==false){
					MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"+vDto.getSvoucherno()+" δǩ  \""+stampDto.getSstampname()+"\" ��");	    			
	    			return "";
				}
    			if(managerPermission==false){
    				if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
						usercode=usercode+"!";
					}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){
						usercode=usercode+"#";
					}
    				String stampuserboolean=vDto.getSstampuser().split(",")[j];
    				if(!stampuserboolean.equals(usercode)){
    					MessageDialog.openMessageDialog(null, "ƾ֤��ţ�"+vDto.getSvoucherno()+"   \""+stampDto.getSstampname()+"\" ���ǵ�ǰ�û���ǩ����ͨ��������Ȩ����ǩ��");
    					String msg = "��Ҫ������Ȩ���ܲ��ܳ���ǩ�£�";
    					if(!AdminConfirmDialogFacade.open("B_267", "ƾ֤�˻ش���", "��Ȩ�û�"+loginInfo.getSuserName()+"����ǩ��", msg)){
    						DisplayCursor.setCursor(SWT.CURSOR_ARROW);
    						return null;
    					}else{
    						managerPermission=true;
    					}    				
    				}   				
    			}
    		}
    		
    		Iterator it=map.keySet().iterator();
    		
    		List lists=new ArrayList();
    		List list=null;
    		List sinList=null;
    		List<TsStamppositionDto> sList=null;
    		List vList=null;
    		
    		String strecode="";
    		String xml="";
    		while(it.hasNext()){
    			 strecode=it.next()+"";
    			 vList=new ArrayList<TvVoucherinfoDto>();
    			 tDto=new TsTreasuryDto();
    			 sDto=new TsStamppositionDto();
    			 sList=new ArrayList<TsStamppositionDto>();
    			 list=new ArrayList();
    			try{
    				tDto.setSorgcode(loginInfo.getSorgcode());
    				tDto.setStrecode(strecode);
        			tDto=(TsTreasuryDto) commonDataAccessService.findRsByDto(tDto).get(0);
    			}catch(Exception e){
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ�����в����ڣ�");
    				
    				return "";
    			}
    			
    			try{
    				sDto.setSorgcode(loginInfo.getSorgcode());
        			sDto.setStrecode(strecode);
        			sDto.setSvtcode(dto.getSvtcode());
        			sList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(sDto);
        			sDto.setSstamptype(stamp);
        			sDto= (TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
        			
    			}catch(Exception e){
    			
    				log.error(e);
    				MessageDialog.openMessageDialog(null, "����������룺"+strecode+" "+stampDto.getSstampname()+" ����δά���� ");
    				
    				return "";
    			}
    			
    			if(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)){
    				if(tDto.getSrotarycertid()==null||tDto.getSrotarycertid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"֤��ID ����δά���� ");
        				
        				return "";
        			}else if(tDto.getSrotaryid()==null||tDto.getSrotaryid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"ӡ��ID ����δά���� ");
        				
        				return "";
        			}
    			}else if(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)){

    				if(tDto.getScertid()==null||tDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"֤��ID ����δά���� ");
        				
        				return "";
        			}else if(tDto.getSstampid()==null||tDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "�����������"+strecode+"�ڹ���������Ϣ������ "+stampDto.getSstampname()+"ӡ��ID ����δά���� ");
        				
        				return "";
        			}
    			
    			}else{
    				if(uDto.getScertid()==null||uDto.getScertid().equals("")){
        				MessageDialog.openMessageDialog(null, "��ǰ�û�  "+stampDto.getSstampname()+"  ֤��ID����δά���� ");
        				
        				return "";
        			}else if(uDto.getSstampid()==null||uDto.getSstampid().equals("")){
        				MessageDialog.openMessageDialog(null, "��ǰ�û�   "+stampDto.getSstampname()+"  ӡ��ID����δά���� ");
        				
        				return "";
        			}
    		    			
    			}
 
    			for(TvVoucherinfoDto vDto:checkList){
    				if(vDto.getStrecode().equals(strecode)){
    					
    					sinList=new ArrayList();
    					sinList.add(vDto);
						stampid=vDto.getSstampid();	
						String seq=sDto.getSstampsequence();   						
						if(seq!=null&&!seq.equals("")){
							List<String> seqList=new ArrayList<String>();
    						for(int i=0;i<sList.size();i++){
    							TsStamppositionDto tsDto=(TsStamppositionDto) sList.get(i);
    							if(tsDto.getSstampsequence()!=null&&!tsDto.getSstampsequence().equals("")){
    								seqList.add(tsDto.getSstampsequence());
    							}
    						}
    						if(seqList!=null&&seqList.size()>0){
    							String[] seqs=seqList.toArray(new String[seqList.size()]);
    							ActiveXCompositeVoucherOcx.sortStringArray(seqs);
    							
    							String temp="";
    							for(int i=0;i<seqs.length;i++){
    								if(Integer.parseInt(seqs[i])>Integer.parseInt(seq)){
    									temp=seqs[i];
    									break;
    								}
    							}
    							if(!temp.equals("")){
    								List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
    								TsStamppositionDto tsDto=new TsStamppositionDto();
    								tsDto.setSorgcode(loginInfo.getSorgcode());
    								tsDto.setStrecode(strecode);
    								tsDto.setSvtcode(vDto.getSvtcode());
    								tsDto.setSstampsequence(temp);
    			        			tsList=(List<TsStamppositionDto>) commonDataAccessService.findRsByDto(tsDto);
    			        			String err="";
    			      
			        				String[] stampids=stampid.split(",");
		        					List<TsStamppositionDto> tsList1=new ArrayList<TsStamppositionDto>();
		        					for(int j=0;j<tsList.size();j++){
		        						for(int i=0;i<stampids.length;i++){
			        						if(stampids[i].equals(tsList.get(j).getSstamptype())){
			        							err=tsList.get(j).getSstampname()+" "+err;
			        						}
			        					}
	        						}
		        					if(!err.trim().equals("")){
		        						for(TsStamppositionDto tstampDto:tsList){
	    			        				err=tstampDto.getSstampname()+" , "+err;
	    			        			}
		        						err=err.substring(0,err.lastIndexOf(","));
		        						MessageDialog.openMessageDialog(null, "������룺"+vDto.getStrecode()+" ƾ֤����: "+vDto.getSvtcode()+" \""+stampDto.getSstampname()+"\"����ǩ��ǰ���ȳ��� \""+err+"\"ǩ�£�");
            			    			
            			    			return "";
		        					}    			        		
    							}
    						}
						}
						int j=-1;
						String[] stampids=stampid.split(",");
						for(int i=0;i<stampids.length;i++){
							if(stamp.equals(stampids[i])){								
								j=i;
								break;
								
							}
						}
						TsUsersDto userDto=new TsUsersDto();
						userDto.setSorgcode(loginInfo.getSorgcode());
						String user=vDto.getSstampuser().split(",")[j];
						
						userDto.setSusercode(stamp.equals(MsgConstant.VOUCHERSAMP_ROTARY)?user.substring(0, (user.length()-1)):(stamp.equals(MsgConstant.VOUCHERSAMP_OFFICIAL)?user.substring(0, (user.length()-1)):user));
						userDto=(TsUsersDto) commonDataAccessService.findRsByDto(userDto).get(0);
						sinList.add(userDto);
						vList.add(sinList);
    				}
    			}
    			
    			list.add(tDto);
    			list.add(sDto);
    			
    			list.add(vList);
    			
    			lists.add(list);
    		}
    		count=voucherLoadService.voucherStampCancle(lists);
    		MessageDialog.openMessageDialog(null, "ƾ֤����ǩ��   "+checkList.size()+" �����ɹ�����Ϊ��"+count+" ��");
    		refreshTable();
    	}catch (ITFEBizException e) {
    		log.error(e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);
			return "";
			
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("ǩ�²������������쳣��",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
			return "";
			
		}
        return super.voucherStampCancle(o);
    }
    
    /**
	 * Direction: �ص�״̬��ѯ
	 * ename: queryStatusReturnVoucher
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryStatusReturnVoucher(Object o){
    	int count=0;
    	if(checkList==null||checkList.size()==0){
    		MessageDialog.openMessageDialog(null, "��ѡ��Ҫ�鿴���͵���ƾ֤��ɹ�״̬�ļ�¼��");
    		return "";
    	}
    	for(TvVoucherinfoDto infoDto:checkList){
	    		if(!(DealCodeConstants.VOUCHER_SUCCESS.equals(infoDto.getSstatus().trim()))){
	    			MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤״̬Ϊ���͵���ƾ֤��ɹ��ɹ��ļ�¼��");
	        		return "";
	    		}
	    }
    	try {
			count=voucherLoadService.queryStatusReturnVoucher(checkList);
			MessageDialog.openMessageDialog(null, "ƾ֤�鿴�ص�״̬"+checkList.size()+" �����ɹ�����Ϊ��"+count+" , ��鿴��");
			refreshTable();
		} catch (ITFEBizException e) {
			log.error(e);						
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e);
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
			log.error(e);
			Exception e1=new Exception("ƾ֤�鿴�ص�״̬���������쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}		
        return super.queryStatusReturnVoucher(o);
    }
    
    /**
	 * Direction: ��ѯƾ֤��ӡ����
	 * ename: queryVoucherPrintCount
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public String queryVoucherPrintCount(TvVoucherinfoDto vDto){
    	String err=null;
    	try {
			err=voucherLoadService.queryVoucherPrintCount(vDto);
			
		} catch (ITFEBizException e) {			
			log.error(e);
			return "��ѯ�쳣";
		}
    	return err;
    }
    
    /**
	 * Direction: ��ѯƾ֤����
	 * ename: queryVoucherPrintCount
	 * ���÷���: 
	 * viewers: * messages: 
	 */
    public int queryVoucherJOintCount(TvVoucherinfoDto vDto){
    	int count=0;
    	TsVouchercommitautoDto tDto=new TsVouchercommitautoDto();
    	tDto.setSorgcode(vDto.getSorgcode());
    	tDto.setStrecode(vDto.getStrecode());
    	tDto.setSvtcode(vDto.getSvtcode());
    	try {
			List<TsVouchercommitautoDto> list= (List) commonDataAccessService.findRsByDto(tDto);
			if(list==null||list.size()==0)
				return -1;
			tDto=list.get(0);
			if(tDto.getSjointcount()==null){
				return -1;
			}			
		} catch (ITFEBizException e) {
			log.error(e);
			return -2;
		}catch(Exception e){
			log.error(e);
			return -1;
		}
    	return tDto.getSjointcount();
    }

    public List<Mapper> getStampEnums(TvVoucherinfoDto vDto){
		List<Mapper> maplist = new ArrayList<Mapper>();
		
		List<TsStamppositionDto> enumList = new ArrayList<TsStamppositionDto>();
		TsStamppositionDto tDto=new TsStamppositionDto();
		tDto.setSorgcode(vDto.getSorgcode());
		tDto.setStrecode(vDto.getStrecode());
		tDto.setSvtcode(vDto.getSvtcode());
		try {
			enumList =commonDataAccessService.findRsByDto(tDto);
			if(enumList!=null&&enumList.size()>0){
				for (TsStamppositionDto emuDto : enumList) {
					Mapper map = new Mapper(emuDto.getSstamptype(), emuDto.getSstampname());
					maplist.add(map);
				}
			}
			
		} catch (ITFEBizException e) {
			log.error(e);
			Exception e1=new Exception("��ȡƾ֤ǩ���б�����쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);
		}		
		return maplist;
	}
    public String getVoucherXMl(TvVoucherinfoDto vDto) throws ITFEBizException{
    	
    	return voucherLoadService.voucherStampXml(vDto);
    }
    
    public void refreshTable(){
    	init();
		checkList.clear();
		refreshStampType();
    }
    
    /**
     * ˢ��ǩ������
     * 
     */
    public void refreshStampType(){
    	TsStamppositionDto tsDto=new TsStamppositionDto();
        tsDto.setSorgcode(dto.getSorgcode());
      	tsDto.setSvtcode(dto.getSvtcode());
      	tsDto.setStrecode(dto.getStrecode());
      	Set set=new HashSet();
      	TsStamppositionDto sDto=new TsStamppositionDto();
      	List<TsStamppositionDto> tList=null;
      	stampList=new ArrayList();
      	List<TsStamppositionDto> tsList=new ArrayList<TsStamppositionDto>();
      	try{
          	tList= commonDataAccessService.findRsByDto(tsDto);
          	if(tList.size()>0){
          		for(TsStamppositionDto sdto:tList){
          			set.add(sdto.getSstamptype());
          		}
          		for(String stamptype:(Set<String>)set){
          			sDto=new TsStamppositionDto();
          			sDto.setSorgcode(dto.getSorgcode());
          			sDto.setSvtcode(dto.getSvtcode());
          			sDto.setSstamptype(stamptype);
          			sDto=(TsStamppositionDto) commonDataAccessService.findRsByDto(sDto).get(0);
          			tsList.add(sDto);
          		}
          		stampList.addAll(tsList);
          		if(stampList.size()==1){
          			stamp = ((TsStamppositionDto)stampList.get(0)).getSstamptype();     
          		}
          	}      		
      	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch(Exception e){
      		log.error(e);
      		Exception e1=new Exception("ƾ֤ˢ�²��������쳣��",e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);			   			
      	}
		editor.fireModelChanged(ModelChangedEvent.REFRESH_ALL_EVENT);
    }
    
    public PageResponse retrieve(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	
    	PageResponse page = null;
    	
    	try {
    		page =  commonDataAccessService.findRsByDtoWithWherePaging(dto,
					pageRequest, "1=1");
    		return page;
    				
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}catch (Throwable e) {
			log.error(e);
			Exception e1=new Exception("ƾ֤��ѯ�����쳣��",e);
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e1);

		}
		return super.retrieve(pageRequest);
	}
    
    
    
    
    /**
	 * Direction: ��ѯ(TCBSʵ���ʽ��˿��)
	 * ename: search
	 * ���÷���: 
	 * viewers: * messages: 
	 */
	public String search(Object o) {
		if(MsgConstant.VOUCHER_NO_2203.equals(voucherTcbsType)||MsgConstant.VOUCHER_NO_3208.equals(voucherTcbsType)||MsgConstant.VOUCHER_NO_3268.equals(voucherTcbsType)||MsgConstant.VOUCHER_NO_3210.equals(voucherTcbsType)||MsgConstant.VOUCHER_NO_3251.equals(voucherGType)){
			this.indexDto.setShold4("1"); 	//��ʶ�ñ�ΪTC�ʽ���
		}
		refreshPayoutBackTable();
		return super.search(o);
	}
	
	/**
     * ˢ��TCBSʵ���ʽ��˿�ҳ��
     */
    public void refreshPayoutBackTable(){
    	PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse=new PageResponse();
		pageResponse = retrieveForPayoutBack(pageRequest);
		if(pageResponse.getTotalCount()==0){
			MessageDialog.openMessageDialog(null, "û�в�ѯ�����������ļ�¼����������ƾ֤��");
		}
		this.indexDto.setShold4(null);
		payoutBackPagingContext.setPage(pageResponse);
		checkPayoutBackList.clear();
		this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
    }

    public PageResponse retrieveForPayoutBack(PageRequest pageRequest) {
    	pageRequest.setPageSize(50);
    	PageResponse page = null;
    	try {
    		page =  commonDataAccessService.findRsByDtoPaging(indexDto,pageRequest, "1=1", "");
    		return page;
    		
    	}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
		}	catch (Throwable e) {	
			log.error(e);
			Exception e1=new Exception("��ѯ�����쳣��",e);			
			MessageDialog.openErrorDialog(Display.getDefault().getActiveShell(),e1);
		}
		return super.retrieve(pageRequest);
	}
    
    
	/**
	 * Direction: ȫѡ(TCBSʵ���ʽ��˿��)
	 * ename: selectAllPayoutBack
	 * ���÷���: 
	 * viewers: * messages: 
	 */
	public String selectAllPayoutBack(Object o) {
		 if(checkPayoutBackList==null||checkPayoutBackList.size()==0){
			 checkPayoutBackList = new ArrayList();
			 checkPayoutBackList.addAll(payoutBackPagingContext.getPage().getData());
	         }
	         else
	        	 checkPayoutBackList.clear();
	         this.editor.fireModelChanged(ModelChangedEvent.REFRESH_TABLE_EVENT);
		return super.selectAllPayoutBack(o);
	}

	/**
	 * Direction: ����ƾ֤��TCBSʵ���ʽ��˿�룩
	 * ename: voucherGeneratorPayoutBack
	 * ���÷���: 
	 * viewers: * messages: 
	 */
	public String voucherGeneratorPayoutBack(Object o) {
		if(StringUtils.isBlank(indexDto.getScreatdate())){
    		MessageDialog.openMessageDialog(null, "��������ƾ֤���ڣ�");
    		return "";
    	}    	
		if(StringUtils.isBlank(voucherTcbsType)){
			MessageDialog.openMessageDialog(null, "��ѡ��ƾ֤���ͣ�");
			return "";
		}    	
    	List<TvVoucherinfoDto> list=new ArrayList<TvVoucherinfoDto>();
    	try {
    		if(StringUtils.isBlank(indexDto.getStrecode())){
    			List strecodeList=findStrecode();
    			if(strecodeList==null||strecodeList.size()==0){
    				MessageDialog.openMessageDialog(null, "�������������[����������Ϣ����]��δά����");
    				return "";
    			}
    			for(TsTreasuryDto tsDto:(List<TsTreasuryDto>)strecodeList){
    				TvVoucherinfoDto vDto=(TvVoucherinfoDto) indexDto.clone();		    	
    				vDto.setStrecode(tsDto.getStrecode());
    				list.add(vDto);	
    			}
    		}else
    			list.add(indexDto);	
    		List resultList=new ArrayList();
    		if(list.size()>0){
    			String sbuf=voucherIsRepeat(list);
    			if(!StringUtils.isBlank(sbuf)){
            		if(!org.eclipse.jface.dialogs.MessageDialog.openQuestion(null, "��Ϣ��ʾ", sbuf.toString()+"\r\nȷ������������")){
                		return "";
                	}
            	}
    			resultList=backVoucherService.voucherGenerateForPayoutBack(list);
    		}
    		int	count=Integer.parseInt(resultList.get(0)+"");
			if(count==0){
    			MessageDialog.openMessageDialog(null, "��ǰ�����µ��޷������������˿���Ϣ����ȫ�����ɣ�");
				return "";
    		}
			MessageDialog.openMessageDialog(null, "ƾ֤���ɲ����ɹ����ɹ�����Ϊ��"+count+" ��");
		} catch (ITFEBizException e) {
			log.error(e);			
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),e);		
		}catch(com.cfcc.jaf.core.invoker.http.HttpInvokerException e){
			log.error(e);
			MessageDialog.openMessageDialog(null, "�����涨ʱ��δ����ҵ��,�Ự��ʧЧ\r\n�����µ�¼��");
			return "";
		}catch(Exception e){
			log.error(e);		
			MessageDialog.openErrorDialog(
					Display.getDefault().getActiveShell(),new Exception("����ƾ֤���������쳣��",e));			
			return "";
		}
		refreshPayoutBackTable();
		return super.voucherGeneratorPayoutBack(o);
	}
	
	
	 /**
     * ��ѯ�������
     * @return
     * @throws ITFEBizException
     */
    private List findStrecode() throws ITFEBizException{
    	TsTreasuryDto dto=new TsTreasuryDto();
		dto.setSorgcode(loginInfo.getSorgcode());
		return commonDataAccessService.findRsByDto(dto);
    }

	public List<TvVoucherinfoDto> getCheckList()
	{
		return checkList;
	}
	public void setCheckList(List<TvVoucherinfoDto> checkList)
	{
		this.checkList = checkList;
	}
	public ITFELoginInfo getLoginInfo()
	{
		return loginInfo;
	}
	public void setLoginInfo(ITFELoginInfo loginInfo)
	{
		this.loginInfo = loginInfo;
	}
	public String getStamp()
	{
		return stamp;
	}
	public void setStamp(String stamp)
	{
		this.stamp = stamp;
	}
	public List getStampList()
	{
		return stampList;
	}
	public void setStampList(List stampList)
	{
		this.stampList = stampList;
	}
	public String getVoucherType()
	{
		return voucherType;
	}
	public void setVoucherType(String voucherType)
	{
		if (StringUtils.isBlank(voucherType)) {
			MessageDialog.openMessageDialog(null, "ƾ֤���Ͳ���Ϊ�գ�");
			return;
		}
		this.voucherType = voucherType;
		this.dto.setSvtcode(voucherType);
		refreshStampType();
		pagingcontext.setPage(new PageResponse());
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged();
	}
	public List getVoucherTypeList()
	{
		return voucherTypeList;
	}
	public void setVoucherTypeList(List voucherTypeList)
	{
		this.voucherTypeList = voucherTypeList;
	}
	public TvVoucherinfoDto getDto(TvVoucherinfoDto dto) throws ITFEBizException{
    	TvVoucherinfoPK pk=new TvVoucherinfoPK();
    	pk.setSdealno(dto.getSdealno());    	   	
    	return (TvVoucherinfoDto) commonDataAccessService.find(pk);
    }
    //�Ҳ�Tabƾ֤�˻ش��������ط���-----end

	public List getBackcheckList() {
		return backcheckList;
	}

	public void setBackcheckList(List backcheckList) {
		this.backcheckList = backcheckList;
	}

	public PagingContext getBackpagingcontext() {
		return backpagingcontext;
	}

	public void setBackpagingcontext(PagingContext backpagingcontext) {
		this.backpagingcontext = backpagingcontext;
	}

	public TvPayoutbackmsgMainDto getBackmaindto() {
		return backmaindto;
	}

	public void setBackmaindto(TvPayoutbackmsgMainDto backmaindto) {
		this.backmaindto = backmaindto;
	}

	public List<TvVoucherinfoDto> getCheckPayoutBackList() {
		return checkPayoutBackList;
	}

	public void setCheckPayoutBackList(List<TvVoucherinfoDto> checkPayoutBackList) {
		this.checkPayoutBackList = checkPayoutBackList;
	}

	public PagingContext getPayoutBackPagingContext() {
		return payoutBackPagingContext;
	}

	public void setPayoutBackPagingContext(PagingContext payoutBackPagingContext) {
		this.payoutBackPagingContext = payoutBackPagingContext;
	}

	public String getVoucherTcbsType() {
		return voucherTcbsType;
	}

	public void setVoucherTcbsType(String voucherTcbsType) {
		if (StringUtils.isBlank(voucherTcbsType)) {
			MessageDialog.openMessageDialog(null, "ƾ֤���Ͳ���Ϊ�գ�");
			return;
		}
		this.voucherTcbsType = voucherTcbsType;
		//��̬��ʾ
		if (MsgConstant.VOUCHER_NO_3208.equals(voucherTcbsType)||MsgConstant.VOUCHER_NO_3268.equals(voucherTcbsType)) 
		{
			List contreaNames = new ArrayList();
			contreaNames.add("ƾ֤��ѯһ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("ʵ���ʽ��˻�ƾ֤��Ϣһ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			clearMSG();
		}else if(MsgConstant.VOUCHER_NO_3210.equals(voucherTcbsType) || MsgConstant.VOUCHER_NO_3251.equals(voucherGType))
		{
			List contreaNames = new ArrayList();
			contreaNames.add("ʵ���ʽ��˻�ƾ֤��Ϣһ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames, false);
			List contreaNames1 = new ArrayList();
			contreaNames1.add("ƾ֤��ѯһ����");
			MVCUtils.setContentAreaVisible(editor, contreaNames1, true);
			clearMSG();
		}else if(MsgConstant.VOUCHER_NO_2203.equals(voucherTcbsType)){
			this.indexDto.setShold4("1");
		}
		this.indexDto.setSvtcode(voucherTcbsType);
		refreshStampType();
		payoutBackPagingContext.setPage(new PageResponse());
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged();
	}

	public BigDecimal getSbtk() {
		return sbtk;
	}

	public void setSbtk(BigDecimal sbtk) {
		this.sbtk = sbtk;
	}

	public BigDecimal getTktk() {
		return tktk;
	}

	public void setTktk(BigDecimal tktk) {
		this.tktk = tktk;
	}

	public List getBatchVouTypeGList() {
		return batchVouTypeGList;
	}

	public void setBatchVouTypeGList(List batchVouTypeGList) {
		this.batchVouTypeGList = batchVouTypeGList;
	}

	public List<TvPayoutmsgsubDto> getSubList() {
		return subList;
	}

	public void setSubList(List<TvPayoutmsgsubDto> subList) {
		this.subList = subList;
	}

	public String getVtcode() {
		return vtcode;
	}

	public void setVtcode(String vtcode) {
		this.vtcode = vtcode;
		this.dto.setSvtcode(vtcode);
		List contreaNamesVisibleFalse = new ArrayList();//�ر���ʾƾ֤һ����
		List contreaNamesVisibbleTrue = new ArrayList();//��ʾƾ֤һ����
		//��̬��ʾ
		if (MsgConstant.VOUCHER_NO_3208.equals(vtcode)||MsgConstant.VOUCHER_NO_3268.equals(vtcode)) {
			contreaNamesVisibleFalse.add("ֱ��֧���˻�ƾ֤��Ϣһ����");
			contreaNamesVisibbleTrue.add("ʵ���ʽ��˻�ƾ֤��Ϣһ����");
		}else if(MsgConstant.MSG_NO_2202.equals(vtcode)){
			contreaNamesVisibleFalse.add("ʵ���ʽ��˻�ƾ֤��Ϣһ����");
			contreaNamesVisibbleTrue.add("ֱ��֧���˻�ƾ֤��Ϣһ����");
		}
		clearMSG();		
		backpagingcontext.setPage(new PageResponse());
		MVCUtils.setContentAreaVisible(editor, contreaNamesVisibleFalse, false);
		MVCUtils.setContentAreaVisible(editor, contreaNamesVisibbleTrue, true);				
		MVCUtils.reopenCurrentComposite(editor);
		editor.fireModelChanged();
	}

	public List getVoucherVtcodeList() {
		return voucherVtcodeList;
	}

	public void setVoucherVtcodeList(List voucherVtcodeList) {
		this.voucherVtcodeList = voucherVtcodeList;
	}
	
	public String getPayoutBackReason() {
		return payoutBackReason;
	}

	public void setPayoutBackReason(String payoutBackReason) {
		this.payoutBackReason = payoutBackReason;
	}

	public String getDwbkBackReason() {
		return dwbkBackReason;
	}

	public void setDwbkBackReason(String dwbkBackReason) {
		this.dwbkBackReason = dwbkBackReason;
	}

	public List getRecvDeptList() {
		recvDeptList = new ReportBussReadReturnEnumFactory().getEnums("0470");
		if(recvDeptList==null||recvDeptList.size()<=0)
		{
			recvDeptList = new ArrayList();
			Mapper map = new Mapper("011", "����");
			Mapper map1 = new Mapper("012", "��˰");
			recvDeptList.add(map);
			recvDeptList.add(map1);
		}
		return recvDeptList;
	}

	public void setRecvDeptList(List recvDeptList) {
		this.recvDeptList = recvDeptList;
	}

	public String getRecvDept() {
		return recvDept;
	}

	public void setRecvDept(String recvDept) {
		this.recvDept = recvDept;
	}
}