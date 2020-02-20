package com.cfcc.itfe.component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.jcr.ItemExistsException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.mule.api.MuleEventContext;
import org.mule.api.lifecycle.Callable;

import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.SrvCacheFacade;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.itfe.voucher.service.Voucher;
import com.cfcc.itfe.voucher.service.VoucherException;
import com.cfcc.jaf.core.loader.ContextFactory;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
/**
 * ���ܣ���ֽ��ƾ֤�Զ�����ƾ֤��������
 * @author �ν���
 * @time  2014-11-13
 */


public class VoucherTimerSendVoucherAttach implements Callable{
	private static Log logger = LogFactory.getLog(VoucherTimerSendVoucherAttach.class);
	//�����ļ����ϣ�keyƾ֤���ͣ�value���ͱ��ĵ�ַ
	private HashMap<String,String> sendVoucherMap=new HashMap<String, String>();
	
	public VoucherTimerSendVoucherAttach(){
		//���÷����ļ����ϣ�keyƾ֤���ͣ�value���ͱ��ĵ�ַ
		sendVoucherMap.put(MsgConstant.MSG_NO_3129, ITFECommonConstant.FILE_ROOT_PATH+
				File.separator+"MsgLog"+File.separator+"recvMsgLog");
		sendVoucherMap.put(MsgConstant.MSG_NO_3139, ITFECommonConstant.FILE_ROOT_PATH+
				File.separator+"MsgLog"+File.separator+"recvMsgLog");
	}
		
	public Object onCall(MuleEventContext eventContext)  {		
		logger.debug("======================== ƾ֤�ⶨʱ����ƾ֤���������� ========================");
		Voucher voucher=(Voucher) ContextFactory.getApplicationContext().getBean(
				MsgConstant.VOUCHER);
		List<TvVoucherinfoDto> sendVoucherList=getSendVoucher(findFile());
		for(TvVoucherinfoDto dto:sendVoucherList){
			try{
				voucher.sendData(dto);
			} catch(ITFEBizException e){
				logger.error(e);
				VoucherException.saveErrInfo(dto.getSvtcode(), e);
			}
		}									
		logger.debug("======================== ƾ֤�ⶨʱ����ƾ֤��������ر� ========================");
		return null;				
	}
	
	/**
	 * ��ȡ����
	 * @return
	 */
	private HashMap<String,List> findFile(){
		HashMap<String,List> map=new HashMap<String,List>();
		for(Iterator<String> it=sendVoucherMap.keySet().iterator();it.hasNext();){
			String svtcode=it.next();
			String filePath=sendVoucherMap.get(svtcode)+File.separator+TimeFacade.getCurrentStringTime();
			File parentFile=new File(filePath);
			if(!parentFile.exists()||!parentFile.isDirectory())
				continue;
			File[] files=parentFile.listFiles();
			for(File file:files){
				if(!file.isFile())
					continue;
				findFile(svtcode, file, map);
			}
		}				
		return map;
	}
	
	/**
	 * ���ݱ������Ʋ�����Ӧ�ı���
	 * @param svtcode
	 * @param fileName
	 * @param map
	 * @return
	 */
	private HashMap<String,List> findFile(String svtcode,File file,HashMap<String,List> map){
		//����TCBS��TIPS��ִ����ƥ�䷽ʽ
		if(file.getAbsolutePath().contains("MsgLog")){
			if(file.getName().startsWith(svtcode+"_")){
				List list=map.get(svtcode);
				if(list==null||list.size()==0)
					list=new ArrayList<String>();									
				list.add(file);
				map.put(svtcode, list);
			}			
		}
		return map;		
	}
	
	/**
	 * ��ȡ���ͱ�����Ϣ
	 * @param map
	 * @return
	 */
	private List<TvVoucherinfoDto> getSendVoucher(HashMap<String,List> map){
		List<TvVoucherinfoDto> sendVoucherList=new ArrayList();
		for(Iterator<String> it=map.keySet().iterator();it.hasNext();){
			String svtcode=it.next();
			List list=map.get(svtcode);
			for(File file:(List<File>)list){
				try{
					sendVoucherList.add(getSendVoucherInfo(svtcode, file.getAbsolutePath()));
				}catch(ITFEBizException e){
					logger.error(e);
					VoucherException.saveErrInfo(null, e);				
				}				
			}
		}return sendVoucherList;
	}
	
	/**
	 * �������Ļ�ȡ���ͱ�����Ϣ
	 * @param svtcode
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	private TvVoucherinfoDto getSendVoucherInfo(String svtcode,String fileName) throws ITFEBizException{		
		try {
			return getSendVoucherInfo(getAdmdivcode(DocumentHelper.parseText
					(FileUtil.getInstance().readFile(fileName)), svtcode), 
					TimeFacade.getCurrentStringTime().substring(0, 4), fileName,svtcode);
		} catch (FileOperateException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		} catch (DocumentException e) {
			logger.error(e);
			throw new ITFEBizException("��������["+fileName+"]�쳣!",e);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException(e);
		}		
		
	}
	
	/**
	 * ��ȡ���ͱ�����Ϣ
	 * @param svtcode
	 * @param fileName
	 * @return
	 * @throws ITFEBizException
	 */
	private TvVoucherinfoDto getSendVoucherInfo(String admdivcode,String workdate,String fileName,String svtcode) {
		TvVoucherinfoDto dto=new TvVoucherinfoDto();
		dto.setSvtcode(svtcode);
		dto.setSadmdivcode(admdivcode);
		dto.setSstyear(workdate.substring(0,4));
		dto.setSfilename(fileName.substring(ITFECommonConstant.FILE_ROOT_PATH.length(), 
				fileName.length()));
		return dto;
	}
	
	/**
	 * ���ݲ�ͬ��ƾ֤���ͻ�ȡ��������
	 * @param fxrDoc
	 * @param svtcode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	private String getAdmdivcode(Document fxrDoc,String svtcode) throws JAFDatabaseException, ITFEBizException{
		String admdivcode=null;
		if(svtcode.equals(MsgConstant.MSG_NO_3129))
			admdivcode=getAdmdivcodeBySfinorgcode(getSfinorgcode(fxrDoc, svtcode));
		else if(svtcode.equals(MsgConstant.MSG_NO_3139))
			admdivcode=getAdmdivcodeByStrecode(getStrecode(fxrDoc, svtcode));
		if(StringUtils.isBlank(admdivcode))
			throw new ITFEBizException("���ݱ�����Ϣ���ҵ���������Ϊ��!");
		return admdivcode;
	}
	
	/**
	 * ���ݲ�ͬ��ƾ֤���ͽ������Ļ�ȡ�������
	 * @param fxrDoc
	 * @param svtcode
	 * @return
	 * @throws ITFEBizException
	 */
	private String getStrecode(Document fxrDoc,String svtcode) throws ITFEBizException{
		String strecode=null;
		if(svtcode.equals(MsgConstant.MSG_NO_3139))
			strecode=fxrDoc.selectSingleNode("CFX").selectSingleNode("MSG").
				selectSingleNode("BillHead3139").selectSingleNode("TreCode").getText();		
		if(StringUtils.isBlank(strecode))
			throw new ITFEBizException("���Ĺ������Ϊ��!");
		return strecode;
	}	
	
	/**
	 * ���ݲ�ͬ��ƾ֤���ͽ������Ļ�ȡ�������ش���
	 * @param fxrDoc
	 * @param svtcode
	 * @return
	 * @throws ITFEBizException
	 */
	private String getSfinorgcode(Document fxrDoc,String svtcode) throws ITFEBizException{
		String sfinorgcode=null;
		if(svtcode.equals(MsgConstant.MSG_NO_3129))
			sfinorgcode=fxrDoc.selectSingleNode("CFX").selectSingleNode("MSG").
				selectSingleNode("BatchHead3129").selectSingleNode("FinOrgCode").getText();		
		if(StringUtils.isBlank(sfinorgcode))
			throw new ITFEBizException("���Ĳ������ش���Ϊ��!");
		return sfinorgcode;
	}
	
	/**
	 * ���ݹ�����������������
	 * @param strecode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public String getAdmdivcodeByStrecode(String strecode) throws JAFDatabaseException, ITFEBizException{		
		String admdivcode=null;
		HashMap<String, TsConvertfinorgDto> finMap = SrvCacheFacade.cacheFincInfoByFinc(null);
		for(Iterator<TsConvertfinorgDto> it=finMap.values().iterator();it.hasNext();){
			TsConvertfinorgDto finDto=it.next();
			if(finDto.getStrecode().equals(strecode)){
				if(StringUtils.isBlank(finDto.getSadmdivcode()))
					throw new ITFEBizException("�������ش���["+finDto.getSfinorgcode()+"]" +
							"��Ӧ�����������ڲ����������ά����δά��!");
				admdivcode=finDto.getSadmdivcode();
				break;
			}				
		}
		if(StringUtils.isBlank(admdivcode))
			throw new ITFEBizException("�������["+strecode+"]�ڲ����������ά����δά��!");
		return admdivcode;
	}
	
	/**
	 * ���ݲ������������������
	 * @param strecode
	 * @return
	 * @throws JAFDatabaseException
	 * @throws ITFEBizException
	 */
	public String getAdmdivcodeBySfinorgcode(String sfinorgcode) throws JAFDatabaseException, ITFEBizException{		
		String admdivcode=null;
		HashMap<String, TsConvertfinorgDto> finMap = SrvCacheFacade.cacheFincInfoByFinc(null);
		TsConvertfinorgDto finDto=finMap.get(sfinorgcode);
		admdivcode=finDto.getSadmdivcode();
		if(StringUtils.isBlank(admdivcode))
			throw new ITFEBizException("�������ش���["+finDto.getSfinorgcode()+"]" +
					"��Ӧ�����������ڲ����������ά����δά��!");		
		return admdivcode;
	}
	
}
