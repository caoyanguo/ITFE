package com.cfcc.itfe.tipsfileparser;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.data.MulitTableDto;
import com.cfcc.itfe.persistence.dto.TsBudgetsubjectDto;
import com.cfcc.itfe.persistence.dto.TvVoucherinfoAllocateIncomeDto;
import com.cfcc.itfe.persistence.pk.TvVoucherinfoAllocateIncomePK;
import com.cfcc.itfe.util.CommonUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;

/**
 * ���ܣ������ʽ���
 * @author hejianrong
 * @time  2014-04-02
 */
public class VoucherAllocateIncomeFileOper extends AbstractTipsFileOper{
	private static Log logger = LogFactory.getLog(VoucherAllocateIncomeFileOper.class);
			
	/**
	 * ����CSV�ļ�
	 * @param file
	 * @param sbookorgcode
	 * @param biztype
	 * @param paramdto
	 * @return
	 * @throws ITFEBizException
	 */
	public MulitTableDto fileParser(String file, String sbookorgcode,String userid, String biztype, 
			String filekind, IDto paramdto,Map<String, TsBudgetsubjectDto> bmap) throws ITFEBizException {		
		MulitTableDto mulitDto = new MulitTableDto();
		mulitDto.setBiztype(biztype);
		mulitDto.setSbookorgcode(sbookorgcode);
		List<IDto> freedtos = new ArrayList<IDto>();			
		try {
			//���տո�" "��ȡ�ļ���������ȡ��ÿ�м�¼�洢���б���
			List<String[]> fileContent = super.readFile(file, " ");
			//��ȡ�����ʽ�ҵ������vtcodeKind,���ͷ�parameterDto
			TvVoucherinfoAllocateIncomeDto parameterDto =(TvVoucherinfoAllocateIncomeDto) paramdto;
			String fundType = parameterDto.getSreportkind();
			parameterDto=null;
			//�����ʽ������ͽ��н���
			if(StateConstant.CMT100.equals(fundType)){
				freedtos = analysisCmt100Data(fileContent, paramdto, biztype);
			}else if (StateConstant.CMT108.equals(fundType)){
				freedtos = analysisCmt108Data(fileContent, paramdto, biztype);
			}else if (StateConstant.CMT103.equals(fundType)) {
				freedtos = analysisCmt103Data(fileContent, paramdto, biztype);
			}else if (StateConstant.PKG001.equals(fundType)) {
				freedtos = analysisPKG001Data(fileContent, paramdto, biztype);
			}else if (StateConstant.PKG007.equals(fundType)) {
				freedtos = analysisPKG007Data(fileContent, paramdto, biztype);
			}else if (StateConstant.TYPE999.equals(fundType)){
				freedtos = analysisType999(fileContent, paramdto, biztype);
			}else if (StateConstant.HVPS111.equals(fundType)){
				freedtos = analysisHVPS111Data(fileContent, paramdto, biztype);
			}else if (StateConstant.HVPS112.equals(fundType)){
				freedtos = analysisHVPS112Data(fileContent, paramdto, biztype);
			}else if (StateConstant.BEPS121.equals(fundType)){
				freedtos = analysisBEPS121Data(fileContent, paramdto, biztype);
			}else if (StateConstant.BEPS122.equals(fundType)){
				freedtos = analysisBEPS122Data(fileContent, paramdto, biztype);
			}
		} catch (FileOperateException e) {
			logger.error(e);
			throw new ITFEBizException("�ļ������쳣",e);
		}catch (Exception e) {
			logger.error(e);
			throw new ITFEBizException("ѡ��ĵ����ļ���ʽ�������֤��"+e.getMessage(),e);	
		}
		mulitDto.setFatherDtos(freedtos);	
		return mulitDto;
	}
	
	/**
	 * ɾ���ظ�����
	 * @param dto
	 * @throws ITFEBizException
	 */
	public void deleteRepeateData2(TvVoucherinfoAllocateIncomeDto dto) throws ITFEBizException{
		TvVoucherinfoAllocateIncomePK pk=new TvVoucherinfoAllocateIncomePK();
		pk.setSdealno(dto.getSdealno());
		try {
			DatabaseFacade.getODB().delete(pk);
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("ɾ���ظ������쳣",e);
		}
	}
	/**
	 * ɾ���ظ�����
	 * @param dto
	 * @throws ITFEBizException
	 */
	private boolean isRepeateData(TvVoucherinfoAllocateIncomeDto dto) throws ITFEBizException{
		TvVoucherinfoAllocateIncomePK pk=new TvVoucherinfoAllocateIncomePK();
		pk.setSdealno(dto.getSdealno());
		
		TvVoucherinfoAllocateIncomeDto tmp;
		try {
			tmp = (TvVoucherinfoAllocateIncomeDto) DatabaseFacade.getODB().find(pk);
			if(tmp!=null)
			{
				throw new ITFEBizException("�ļ����������ظ���������,��ţ�"+dto.getSdealno());
			}
		} catch (JAFDatabaseException e) {
			logger.error(e);
			throw new ITFEBizException("ɾ���ظ������쳣",e);
		}
		return null!=tmp?true:false;
	}
	
	/**
	 * ��ȡ������
	 * @param cols
	 * @return
	 */
	public String[] getColumnsContent(String[] cols){
		StringBuffer rows = new StringBuffer();
		for(int i = 0 ; i < cols.length ; i ++){
			if(cols[i].trim().length() != 0){
				if(!cols[i].trim().equals("\"")){
					rows.append(cols[i].trim().replaceAll("\"", "").replaceAll(",", "").trim()).append(",");
				}
			}
		}
		return rows.toString().trim().split(",");
	}
	
	
	
	
	/**
	 * �����ʽ�������Ϊ[CMT100]���ļ�
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisCmt100Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			dto.setSpaydealno(cols[1]);//֧���������
			cols= getColumnsContent(fileContent.get(++i));
			if(!cols[1].equalsIgnoreCase(StateConstant.CMT100NAME)){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[CMT100]");
			}
			dto.setSreportkind(StateConstant.CMT100);//��������CMT100
			dto.setSvtcodedes(cols[1]);//��Ԥ���ֶ�1�洢�����������䵱��ע
			dto.setStradekind(cols[3]+" "+cols[4]);//��������
			dto.setSvtcodekind(cols[cols.length-1]);//ҵ������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//�������к�
			dto.setSpayacctbankname(cols[3]);//�����˿������к�
			dto.setScommitdate(cols[cols.length-1].replaceAll("-", ""));//ί������
			dto.setSforwardbankname(getColumnsContent(fileContent.get(++i))[1]);//����������		
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//�������˺�
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//����������				
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//�������к�
			dto.setSpayeeacctbankname(cols[3]);//�տ��˿������кţ�
			dto.setSreceivedate(cols[cols.length-1].replaceAll("-", ""));//�ձ�����
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//�տ����˺�
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//�տ�������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[cols.length-1]));//������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSdemo(cols.length==3?"":cols[1]);//����
			dto.setSescortmarks(cols[cols.length-1]);//��Ѻ��־
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[1]);//��
//			dto.setSsecretsign(cols.length==3?null:cols[cols.length-1]);
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//��
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//��ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ��ʽ�������Ϊ[CMT100]���ļ�����"+e.getMessage());
//			}
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	/**
	 * �����ʽ�������Ϊ[CMT108]���ļ�
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisCmt108Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols= getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("��������"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.CMT108NAME)){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[CMT108]");
			}
			dto.setSreportkind(StateConstant.CMT108);//��������CMT108
			dto.setSvtcodedes(cols[1]);//��Ԥ���ֶ�1�洢�����������䵱��ע
			dto.setStradekind(cols[3]+" "+cols[4]);//��������
			dto.setSpaydealno(cols[cols.length-1]);//֧���������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//�������к�
			dto.setSpayacctbankname("");//�����˿������к�
			dto.setScommitdate(cols[cols.length-1].replaceAll("-", ""));//ί������
			dto.setSforwardbankname(getColumnsContent(fileContent.get(++i))[1]);//����������	
			dto.setShold2(getColumnsContent(fileContent.get(++i))[1]);//ԭί������
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//�������˺�
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//����������				
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//�������к�
			dto.setSpayeeacctbankname("");//�տ��к�����
			dto.setSreceivedate(cols[cols.length-1].replaceAll("-", ""));//�ձ�����
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//�տ����˺�
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//�տ�������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[cols.length-1]));//������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSdemo(cols.length==3?"":cols[1]);//����
			dto.setSescortmarks(cols[cols.length-1]);//��Ѻ��־
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[1]);//��
//			dto.setSsecretsign(cols.length==3?null:cols[cols.length-1]);
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//��
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//��ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ��ʽ�������Ϊ[CMT108]���ļ�����"+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	/**
	 * �����ʽ�������Ϊ[CMT103]���ļ�
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisCmt103Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=3) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols= getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("��������"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.CMT103NAME)){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[CMT103]");
			}
			dto.setSreportkind(StateConstant.CMT103);//��������CMT103
			dto.setSvtcodedes(cols[1]);//��Ԥ���ֶ�1�洢�����������䵱��ע
			dto.setStradekind(cols[3]+" "+cols[4]);//��������
			dto.setSvtcodekind(cols[6]);//ҵ������
			dto.setSpaydealno(cols[cols.length-1]);//֧���������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//�������к�
			dto.setSpayacctbankname(cols[3]);//�����˿������к�
			dto.setScommitdate(cols[cols.length-1].replaceAll("-", ""));//ί������
			dto.setSforwardbankname(getColumnsContent(fileContent.get(++i))[1]);//����������		
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//�������˺�
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//����������				
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//�������к�
			dto.setSpayeeacctbankname(cols[3]);//�տ��˿������кţ�
			dto.setSreceivedate(cols[cols.length-1].replaceAll("-", ""));//�ձ�����
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//�տ����˺�
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//�տ�������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[cols.length-1]));//������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSdemo(cols.length==3?"":cols[1]);//����
			dto.setSescortmarks(cols[cols.length-1]);//��Ѻ��־
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[1]);//��
//			dto.setSsecretsign(cols.length==3?null:cols[cols.length-1]);
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//��
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[0]);//��ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ��ʽ�������Ϊ[CMT103]���ļ�����"+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}
		return freedtos;
	}
	
	/**
	 * �����ʽ�������Ϊ[PKG001]���ļ�
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisPKG001Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols= getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("��������"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.PKG001NAME)){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[PKG001]");
			}
			dto.setSreportkind(StateConstant.PKG001);//��������PKG001
			dto.setSvtcodedes(cols[1]);//��Ԥ���ֶ�1�洢�����������䵱��ע
			dto.setStradekind(cols[3]);//��������
			dto.setSvtcodekindno(cols[cols.length-1]);//ҵ�����ͺ�
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpaydealno(cols[1]);//֧���������
			dto.setSvtcodekind(cols[3]);//ҵ������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//�������к�
			dto.setSpayacctbankname(cols[3]);//�����˿������к�
			dto.setScommitdate(trasforDate(cols[cols.length-1]));//ί������
			dto.setSforwardbankname(getColumnsContent(fileContent.get(++i))[1]);//����������		
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//�������˺�
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//����������				
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//�������к�
			dto.setSpayeeacctbankname(cols[3]);//�տ��˿������кţ�
			dto.setSreceivedate(trasforDate(cols[cols.length-1]));//�ձ�����
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//�տ����˺�
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//�տ�������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[cols.length-1]));//������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSdemo(cols.length==3?"":cols[1]);//����
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[2]);//��
			//dto.setSsecretsign(cols.length==3?null:cols[cols.length-1]);
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//��
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//��ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ��ʽ�������Ϊ[PKG001]���ļ�����"+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}
		return freedtos;
	}
	
	/**
	 * �����ʽ�������Ϊ[PKG007]���ļ�
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisPKG007Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols= getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("��������"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.PKG007NAME)){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[PKG007]");
			}
			dto.setSreportkind(StateConstant.PKG007);//��������PKG007
			dto.setSvtcodedes(cols[1]);//��������˵��
			dto.setStradekind(cols[3]);//��������
			dto.setSvtcodekindno(cols[5]);//ҵ�����ͺ�
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpaydealno(cols[cols.length-1]);//֧���������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setScommitdate(trasforDate(cols[cols.length-1]));//ί������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//�������к�
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayacctbankname(cols[1]);//����������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayacctno(cols[1]);//�������˺�
			dto.setSoricommitdate(trasforDate(cols[3]));//ԭί������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayacctname(cols[1]);//����������
			dto.setSoripaydealno(cols[3]);//ԭ֧���������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//�������к�
			dto.setSreceivedate(trasforDate(cols[cols.length-1]));//�ձ�����
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//�տ����˺�
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//�տ�������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[cols.length-1]));//������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSdemo(cols.length==3?"":cols[1]);//����
			dto.setSescortmarks(cols[cols.length-1]);//��Ѻ��־
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[1]);//��
			//dto.setSsecretsign(cols.length==3?null:cols[cols.length-1]);
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//��
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//��ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ��ʽ�������Ϊ[PKG007]���ļ�����"+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}
		return freedtos;
	}
	
	
	/**
	 * �����ʽ�ҵ������Ϊ[ 999-����������������]���ļ�
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisType999(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		
		for (int i = 0; i < fileContent.size(); i++) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols =getColumnsContent(fileContent.get(i));
			if(!(cols!=null&&cols.length>1&&cols[0].contains("ҵ������")))
			{
				continue;
			}
			//�����ж��Ƿ��Ǻ����������������ļ�
			if(!cols[0].contains("ҵ������")){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[����������������]");
			}
			dto.setSvtcodedes("��������");//��Ԥ���ֶ�1�洢ҵ���������䵱��ע
			dto.setSvtcodekind(MsgConstant.FUND_BIZ_TYPE_ORGCODEINNERINCOME);//�á�00�����桰ϵͳ�ڲ�ͬ��������������롱��ҵ������
			dto.setScommitdate(trasforDate(cols[3]));//��������
			cols =getColumnsContent(fileContent.get(++i));
			dto.setSpayacctbankname(cols[3]);//��������������
			dto.setSforwardbankname(cols[1]);//���������������
			dto.setSforwardbankno(dto.getSpayacctbankname());
			cols =getColumnsContent(fileContent.get(++i));
			dto.setShold1(cols[1]);//���պ�����������
			dto.setSpayeeacctbankname(cols[3]);//���պ����������
			cols =getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("����"))
			{
				dto.setSpayacctno(cols[3]);//�������˺�
				dto.setSpayacctname(cols[1]);//����������
			}else
			{
				dto.setSpayacctno(cols[1]);//�������˺�
				dto.setSpayacctname(cols[3]);//����������
			}
			cols =getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("����"))
			{
				dto.setSpayeeacctno(cols[3]);//�տ����˺�
				dto.setSpayeeacctname(cols[1]);//�տ�������
			}else
			{
				dto.setSpayeeacctno(cols[1]);//�տ����˺�
				dto.setSpayeeacctname(cols[3]);//�տ�������
			}
			cols =getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("ժҪ����"))
			{
				dto.setSforwardbankno(cols[3]);//��ˮ��ժҪ����
				dto.setSforwardbankname(cols[1]);//��ˮ��ժҪ����
				cols =getColumnsContent(fileContent.get(++i));
				if(cols[0].contains("���"))
					dto.setNmoney(new BigDecimal(cols.length==4?cols[3]:cols[1]));//���
				
			}else if(cols[0].contains("���"))
			{
				dto.setNmoney(new BigDecimal(cols.length==4?cols[3]:cols[1]));//���
				cols =getColumnsContent(fileContent.get(++i));
				if(cols[0].contains("ժҪ����"))
				{
					dto.setSforwardbankno(cols[3]);//��ˮ��ժҪ����
					dto.setSforwardbankname(cols[1]);//��ˮ��ժҪ����
				}
			}
			if(!cols[0].contains("��"))
				cols =getColumnsContent(fileContent.get(++i));
			dto.setSborrow(cols[1]);//��
			cols =getColumnsContent(fileContent.get(++i));
			dto.setSlend(cols[1]);//��
			cols =getColumnsContent(fileContent.get(++i));
			StringBuffer sbf=new StringBuffer();
			for (int j = 1; j < cols.length; j++) {
				sbf.append(cols[j]);
			}
			dto.setSdemo(sbf.toString());//����
			cols =getColumnsContent(fileContent.get(++i));
			dto.setSdealno(cols[1]);//������ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
			dto.setStradekind("��������");//��������
			dto.setSpaydealno(cols[1].substring(cols[1].length()-8,cols[1].length()));//֧���������
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ�ҵ������Ϊ[����������������]���ļ�����"+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	/**
	 * �����ʽ�������Ϊ[HVPS111]���ļ�
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisHVPS111Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("��������"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.HVPS111NAME)){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[HVPS111]");
			}
			dto.setSreportkind(StateConstant.HVPS111);//��������HVPS111
			dto.setSvtcodekind(cols[5]);//ҵ������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setStradekind(cols[1]+cols[2]+cols[3]);//��������
			dto.setSpaydealno(cols[5].substring(8));//֧���������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivedate(cols[1].replaceAll("��", "").replaceAll("��", "").replaceAll("��", ""));//�ձ�����
			dto.setScommitdate(cols[3].replaceAll("��", "").replaceAll("��", "").replaceAll("��", ""));//ί������
			
			cols = getColumnsContent(fileContent.get(i+1));
			if(cols!=null&&cols[0].contains("��ǩ��־"))
			{
				cols= getColumnsContent(fileContent.get(++i));
				dto.setSescortmarks(cols[1]==null?"":cols[1].replace("��ǩ", ""));//��ǩ��־
			}
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//�������к�
			dto.setSpayacctbankname(cols[3]);//�����˿������к�
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankname(cols.length>3?cols[1]:cols.length>1?cols[1]:"");//����������
			
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//�������˺�
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//����������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//�������к�
			dto.setSpayeeacctbankname(cols[3]);//�տ��˿������к�
			
			dto.setShold1(getColumnsContent(fileContent.get(++i))[1]);//��Hold1�����տ�������
			if(getColumnsContent(fileContent.get(i+1))[0].contains("������"))
				++i;//�տ�������
			if(getColumnsContent(fileContent.get(i+1))[0].contains("������"))
				++i;//�������к�
			dto.setSpayeeacctno(getColumnsContent(fileContent.get(++i))[1]);//�տ����˺�
			dto.setSpayeeacctname(getColumnsContent(fileContent.get(++i))[1]);//�տ�������
			dto.setNmoney(new BigDecimal(getColumnsContent(fileContent.get(++i))[3]));//���
			cols = getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("����")&&cols.length==1)//��ǰ��ֻ�и������ֻ����Ǿ���ԭ��
			{
				cols= getColumnsContent(fileContent.get(++i));
				if(cols!=null&&cols[0].contains("��:"))
					i--;
				else
					dto.setSdemo(cols.length>1?cols[1]:""); //����
				if(dto.getSdemo()!=null&&dto.getSdemo().getBytes().length>83)
					dto.setSdemo(CommonUtil.subString(dto.getSdemo(), 83));
			}else if(cols[0].contains("����")&&cols.length==2)
			{
				dto.setSdemo(cols.length>1?cols[1]:""); //����
				if(dto.getSdemo().getBytes().length>83)
					dto.setSdemo(CommonUtil.subString(dto.getSdemo(), 83));
			}else if(cols[0].contains("����")&&cols.length>2)
			{
				StringBuffer fy = new StringBuffer("");
				for(int num=1;num<cols.length;num++)
				{
					fy.append(cols[num]);
				}	
				dto.setSdemo(fy.toString().getBytes().length>83 ? CommonUtil.subString(fy.toString(), 83) : fy.toString()); //����
			}else if(cols[0].contains("��"))
			{
				--i;
			}else
			{
				cols = getColumnsContent(fileContent.get(++i));
				if(cols[0].contains("�˻�ԭ��"))
					dto.setSdemo(cols[1]);
			}
			dto.setSborrow(getColumnsContent(fileContent.get(++i))[1]);//��
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//��
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//��ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ��ʽ�������Ϊ[HVPS111]���ļ�����"+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	/**
	 * �����ʽ�������Ϊ[HVPS112]���ļ�
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisHVPS112Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("��������"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.HVPS112NAME)){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[HVPS112]");
			}
			dto.setSreportkind(StateConstant.HVPS112);//��������HVPS112
			dto.setSvtcodekind(cols[5]);//ҵ������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setStradekind(cols[1]+cols[3]);//��������
			dto.setSpaydealno(cols[5].substring(8));//֧���������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivedate(cols[1].replaceAll("��", "").replaceAll("��", "").replaceAll("��", ""));//�ձ�����
			dto.setScommitdate(cols[3].replaceAll("��", "").replaceAll("��", "").replaceAll("��", ""));//ί������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSescortmarks(cols[1]);//��ǩ��־
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//�������к�
			dto.setSpayacctbankname(cols[3]);//�����˿������к�
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankname(cols[1]);//����������
			
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//�������˺�
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//����������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//�������к�
			dto.setSpayeeacctbankname(cols[3]);//�տ��˿������кţ�
			
			dto.setShold1(getColumnsContent(fileContent.get(++i))[1]);//��Hold1�����տ�������
			
			dto.setSpayeeacctno(getColumnsContent(fileContent.get(++i))[1]);//�տ����˺�
			dto.setSpayeeacctname(getColumnsContent(fileContent.get(++i))[1]);//�տ�������
			dto.setNmoney(new BigDecimal(getColumnsContent(fileContent.get(++i))[3]));//���
			cols= getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("����")&&cols.length==1)//��ǰ��ֻ�и������ֻ����Ǿ���ԭ��
			{
				cols= getColumnsContent(fileContent.get(++i));
				if(cols!=null&&cols[0].contains("��:"))
					i--;
				else
					dto.setSdemo(cols.length>1?cols[1]:""); //����
			}else if(cols[0].contains("����")&&cols.length==2)
			{
				dto.setSdemo(cols.length>1?cols[1]:""); //����
			}else if(cols[0].contains("��"))
			{
				--i;
			}
			dto.setSborrow(getColumnsContent(fileContent.get(++i))[1]);//��
			dto.setSlend(getColumnsContent(fileContent.get(++i))[1]);//��
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//��ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ��ʽ�������Ϊ[HVPS112]���ļ�����"+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	/**
	 * �����ʽ�������Ϊ[BEPS121]���ļ�
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisBEPS121Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		//��ȡ�ʽ��ļ��ĵ�һ��
		String[] firstCols = null;
		for(int i=0;i<fileContent.size();i++)
		{
			firstCols = getColumnsContent(fileContent.get(i));
			if(firstCols==null||firstCols.length<5)
				continue;
			else
				break;
		}
		
		//ȡ���ʽ�ҵ������
		String vtcodekind = firstCols[5].trim();
		List<IDto> freedtos = new ArrayList<IDto>();
		if(null!=vtcodekind&&(vtcodekind.equals("A100")||vtcodekind.equals("A102")||vtcodekind.equals("A108"))){
			//�����ʽ�������[BEPS121]��ҵ������Ϊ[A100 - ��ͨ���]���ʽ���
			freedtos = analysisBEPS121DataForA100(fileContent, paramdto, biztype);
		}else if(null!=vtcodekind&&vtcodekind.equals("A105")){
			//�����ʽ�������[BEPS121]��ҵ������Ϊ[A105 - �˻�]���ʽ���
			freedtos = analysisBEPS121DataForA105(fileContent, paramdto, biztype);
		}
		return freedtos;
	}
	
	
	/**
	 * �����ʽ�������[BEPS121]��ҵ������Ϊ[A100 - ��ͨ���]���ʽ���
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisBEPS121DataForA100(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		
		for (int i = 0; i < fileContent.size(); i++) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			if(!(cols!=null&&cols.length>1&&cols[0].contains("��������")))
			{
				continue;
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.BEPS121NAME)){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[BEPS121]");
			}
			dto.setSreportkind(StateConstant.BEPS121);//��������BEPS121
			dto.setSvtcodekind(cols[5]);//ҵ������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setStradekind(cols[1]+cols[2]+cols[3]);//��������
			dto.setSpaydealno(cols[5].substring(8));//֧���������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSescortmarks(cols[1].replace("��ǩ", ""));//��ǩ��־
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivedate(cols[1].replaceAll("��", "").replaceAll("��", "").replaceAll("��", ""));//�ձ�����
			dto.setScommitdate(cols[3].replaceAll("��", "").replaceAll("��", "").replaceAll("��", ""));//ί������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//�������к�
			dto.setSpayacctbankname(cols[3]);//�����˿������к�
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankname(cols.length>3?cols[1]:"");//����������
			if(cols[0].contains("����������"))
				dto.setSforwardbankname(cols[1]);//����������
			
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//�������˺�
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//����������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//�������к�
			dto.setSpayeeacctbankname(cols[3]);//�տ��˿������кţ�
			
			dto.setShold1(getColumnsContent(fileContent.get(++i))[1]);//��Hold1�����տ�������
			
			dto.setSpayeeacctno(getColumnsContent(fileContent.get(++i))[1]);//�տ����˺�
			dto.setSpayeeacctname(getColumnsContent(fileContent.get(++i))[1]);//�տ�������
			dto.setNmoney(new BigDecimal(getColumnsContent(fileContent.get(++i))[3]));//���
			cols= getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("����")&&cols.length==1)//��ǰ��ֻ�и������ֻ����Ǿ���ԭ��
			{
				cols= getColumnsContent(fileContent.get(++i));
				if(cols!=null&&cols[0].contains("��:"))
					i--;
				else
					dto.setSdemo(cols.length>1?cols[1]:""); //����
			}else if(cols[0].contains("����")&&cols.length==2)
			{
				dto.setSdemo(cols.length>1?cols[1]:""); //����
			}else if(cols[0].contains("��"))
			{
				--i;
			}
			dto.setSborrow(getColumnsContent(fileContent.get(++i))[1]);//��
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols.length>1){
				dto.setSlend(cols[1]);//��
			}
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//��ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ��ʽ�������Ϊ[BEPS121]���ļ�����"+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	
	
	
	/**
	 * �����ʽ�������[BEPS121]��ҵ������Ϊ[A105 - �˻�]���ʽ���
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisBEPS121DataForA105(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			if(!(cols!=null&&cols.length>1&&cols[0].contains("��������")))
			{
				continue;
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.BEPS121NAME)){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[BEPS121]");
			}
			dto.setSreportkind(StateConstant.BEPS121);//��������BEPS121
			dto.setSvtcodekind(cols[5]);//ҵ������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setStradekind(cols[1]+cols[2]+cols[3]);//��������
			dto.setSpaydealno(cols[5].substring(8));//֧���������
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols[1]!=null&&cols[1].contains("��ǩ"))
				cols[1] = cols[1].replace("��ǩ", "");
			dto.setSescortmarks(cols[1]);//��ǩ��־
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivedate(cols[1].replaceAll("��", "").replaceAll("��", "").replaceAll("��", ""));//�ձ�����
			dto.setScommitdate(cols[3].replaceAll("��", "").replaceAll("��", "").replaceAll("��", ""));//ί������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//�������к�
			dto.setSpayacctbankname(cols[3]);//�����˿������к�
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankname(cols.length>3?cols[1]:"");//����������
			
			cols= getColumnsContent(fileContent.get(++i));
			
			dto.setSpayacctno(cols[1]);//�������˺�
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayacctname(cols[1]);//����������

			cols= getColumnsContent(fileContent.get(++i));//����ֱ�Ӳ�������ͽ���ֱ�Ӳ������
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//�������к�
			dto.setSpayeeacctbankname(cols[3]);//�տ��˿������кţ�
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setShold1(cols[1]);//��Hold1�����տ�������
			
			cols= getColumnsContent(fileContent.get(++i));//ԭ�����Ӳ��������ԭ���ı�ʶ��
			if(cols.length>3)
				dto.setSsecretsign(cols[3]);//��Ѻ�ֶδ��ԭ���ı��
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctno(cols[1]);//�տ����˺�
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSpayeeacctname(cols[1]);//�տ�������
			
			cols= getColumnsContent(fileContent.get(++i));//ԭҵ�����ͺ�ԭ���ı��
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setNmoney(new BigDecimal(cols[3]));//���
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("����")&&cols.length==1)//��ǰ��ֻ�и������ֻ����Ǿ���ԭ��
			{
				cols= getColumnsContent(fileContent.get(++i));
				if(cols!=null&&cols[0].contains("��:"))
					i--;
				else
					dto.setSdemo(cols.length>1?cols[1]:""); //����
			}else if(cols[0].contains("����")&&cols.length==2)
			{
				dto.setSdemo(cols.length>1?cols[1]:""); //����
			}else if(cols[0].contains("�˻�"))
			{
				--i;
			}
			
			cols= getColumnsContent(fileContent.get(++i));//�˻�ԭ��
			if(cols!=null&&cols[0].contains("ԭ��")&&cols.length>1)
				dto.setSdemo(cols[1]+dto.getSdemo());
			else if(cols!=null&&cols[0].contains("��:")&&cols.length>1)
				dto.setSborrow(cols[1]);//��
			else
				cols= getColumnsContent(fileContent.get(++i));
			
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols!=null&&cols[0].contains("��:")&&cols.length>1)
			{
				dto.setSborrow(cols[1]);//��
				cols= getColumnsContent(fileContent.get(++i));
			}
			if(cols!=null&&cols[0].contains("��:")&&cols.length>1){
				dto.setSlend(cols[1]);//��
			}
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//��ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ��ʽ�������Ϊ[BEPS121]���ļ�����"+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	
	
	/**
	 * �����ʽ�������Ϊ[BEPS122]���ļ�
	 * @param fileContent
	 * @param paramdto
	 * @param biztype
	 * @return
	 * @throws ITFEBizException
	 */
	public List<IDto> analysisBEPS122Data(List<String[]> fileContent,IDto paramdto,String biztype) throws ITFEBizException{
		List<IDto> freedtos = new ArrayList<IDto>();
		for (int i = 0; i < fileContent.size(); i+=2) {
			TvVoucherinfoAllocateIncomeDto dto = (TvVoucherinfoAllocateIncomeDto) ((TvVoucherinfoAllocateIncomeDto)paramdto).clone();
			String[] cols = getColumnsContent(fileContent.get(i));
			while(!cols[0].contains("��������"))
			{
				i++;
				if(i >= fileContent.size())
					return freedtos;
				cols = getColumnsContent(fileContent.get(i));
			}
			if(!cols[1].equalsIgnoreCase(StateConstant.BEPS122NAME)){
				throw new ITFEBizException("������ļ��ʽ������Ͳ���[BEPS122]");
			}
			dto.setSreportkind(StateConstant.BEPS122);//��������BEPS122
			dto.setSvtcodekind(cols[5]);//ҵ������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setStradekind(cols[1]);//��������
			dto.setSpaydealno(cols[5].substring(8));//֧���������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSescortmarks(cols[1]);//��ǩ��־
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivedate(cols[1].replaceAll("��", "").replaceAll("��", "").replaceAll("��", ""));//�ձ�����
			dto.setScommitdate(cols[3].replaceAll("��", "").replaceAll("��", "").replaceAll("��", ""));//ί������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankno(cols[1]);//�������к�
			dto.setSpayacctbankname(cols[3]);//�����˿������к�
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSforwardbankname(cols[1]);//����������
			
			dto.setSpayacctno(getColumnsContent(fileContent.get(++i))[1]);//�������˺�
			dto.setSpayacctname(getColumnsContent(fileContent.get(++i))[1]);//����������
			
			cols= getColumnsContent(fileContent.get(++i));
			dto.setSreceivebankno(cols[1]);//�������к�
			dto.setSpayeeacctbankname(cols[3]);//�տ��˿������кţ�
			
			dto.setShold1(getColumnsContent(fileContent.get(++i))[1]);//��Hold1�����տ�������
			
			dto.setSpayeeacctno(getColumnsContent(fileContent.get(++i))[1]);//�տ����˺�
			dto.setSpayeeacctname(getColumnsContent(fileContent.get(++i))[1]);//�տ�������
			dto.setNmoney(new BigDecimal(getColumnsContent(fileContent.get(++i))[3]));//���
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols[0].contains("����")&&cols.length==1)//��ǰ��ֻ�и������ֻ����Ǿ���ԭ��
			{
				cols= getColumnsContent(fileContent.get(++i));
				if(cols!=null&&cols[0].contains("��:"))
					i--;
				else
					dto.setSdemo(cols.length>1?cols[1]:""); //����
			}else if(cols[0].contains("����")&&cols.length==2)
			{
				dto.setSdemo(cols.length>1?cols[1]:""); //����
			}else if(cols[0].contains("��"))
			{
				--i;
			}
			dto.setSborrow(getColumnsContent(fileContent.get(++i))[1]);//��
			
			cols= getColumnsContent(fileContent.get(++i));
			if(cols.length>1){
				dto.setSlend(cols[1]);//��
			}
			dto.setSdealno(getColumnsContent(fileContent.get(++i))[1]);//��ˮ��
			dto.setSvtcode(biztype);//ƾ֤����
//			try {
//				deleteRepeateData(dto);//ɾ���ظ�����
//			} catch (ITFEBizException e) {
//				logger.error(e);
//				throw new ITFEBizException("�����ʽ��ʽ�������Ϊ[BEPS122]���ļ�����"+e.getMessage());
//			}
//			freedtos.add(dto);
			if(!isRepeateData(dto)){
				freedtos.add(dto);
			}
		}	
		return freedtos;
	}
	
	
	public MulitTableDto fileParser(String file, String sbookorgcode,
			String userid, String biztype, String filekind, IDto paramdto,
			Map<String, TsBudgetsubjectDto> bmap, IDto idto)
			throws ITFEBizException {
		return null;
	}
	
	private String trasforDate(String oriDate){
		return oriDate.replace("��", "").replace("��", "").replace("��", "");
	}
}
