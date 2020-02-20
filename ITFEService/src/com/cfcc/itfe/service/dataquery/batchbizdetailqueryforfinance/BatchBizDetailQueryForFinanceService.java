package com.cfcc.itfe.service.dataquery.batchbizdetailqueryforfinance;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.config.ITFECommonConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.persistence.dto.HtfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailsmainDto;
import com.cfcc.itfe.persistence.dto.TfPaymentDetailssubDto;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.util.ValidateException;
/**
 * @author Administrator
 * @time   14-11-04 16:36:35
 * codecomment: 
 */

public class BatchBizDetailQueryForFinanceService extends AbstractBatchBizDetailQueryForFinanceService {
	private static Log log = LogFactory.getLog(BatchBizDetailQueryForFinanceService.class);

	public String exportFile(IDto dtoInfo, String flag) throws ITFEBizException {
		List list=new ArrayList();
		try {
			list = CommonFacade.getODB().findRsByDto(dtoInfo);
			
			String root = ITFECommonConstant.FILE_ROOT_PATH;
			String dirsep = File.separator; // ȡ��ϵͳ�ָ��
			String strdate = DateUtil.date2String2(new Date()); // ��ǰϵͳ������
			String fullpath = root + "exportFile" + dirsep + strdate + dirsep + new SimpleDateFormat("yyyyMMddhhmmssSSS").format(System.currentTimeMillis())+".csv";
			String splitSign = ",";// �ļ���¼�ָ�����
			
			if(list.size()>0){
				StringBuffer filebuf = new StringBuffer();
				if(flag.equals("0")){
					for (TfPaymentDetailsmainDto dto :(List<TfPaymentDetailsmainDto>) list) {
						filebuf.append(dto.getStrecode()); //�������
						filebuf.append(splitSign + dto.getSstatus()); //״̬
						filebuf.append(splitSign + (dto.getSdemo()==null?"":dto.getSdemo())); //����
						filebuf.append(splitSign + dto.getSfilename()); //�ļ�����
						filebuf.append(splitSign + (dto.getSpackageno()==null?"":dto.getSpackageno())); //����ˮ��
						filebuf.append(splitSign + dto.getSadmdivcode()); //������������
						filebuf.append(splitSign + dto.getSstyear()); //ҵ�����
						filebuf.append(splitSign + dto.getSvtcode()); //ƾ֤���ͱ��
						filebuf.append(splitSign + dto.getSvoudate()); //ƾ֤����
						filebuf.append(splitSign + dto.getSvoucherno()); //ƾ֤��
						filebuf.append(splitSign + dto.getSoriginalvtcode()); //��ƾ֤���ͱ��
						filebuf.append(splitSign + dto.getSoriginalvoucherno()); //��֧��ƾ֤���
						filebuf.append(splitSign + dto.getSfundtypecode()); //�ʽ����ʱ���
						filebuf.append(splitSign + dto.getSfundtypename()); //�ʽ���������
						filebuf.append(splitSign + (dto.getSpaydictateno()==null?"":dto.getSpaydictateno())); //֧���������
						filebuf.append(splitSign + (dto.getSpaymsgno()==null?"":dto.getSpaymsgno())); //֧�����ı��
						filebuf.append(splitSign + (dto.getSpayentrustdate()==null?"":dto.getSpayentrustdate())); //֧��ί������
						filebuf.append(splitSign + (dto.getSpaysndbnkno()==null?"":dto.getSpaysndbnkno())); //֧���������к�
						filebuf.append(splitSign + dto.getNsumamt()); //����֧�����
						filebuf.append(splitSign + (dto.getSagencycode()==null?"":dto.getSagencycode())); //����Ԥ�㵥λ����
						filebuf.append(splitSign + dto.getSagencyname()); //����Ԥ�㵥λ����
						filebuf.append(splitSign + dto.getSpayacctno()); //�������˺�
						filebuf.append(splitSign + dto.getSpayacctname()); //����������
						filebuf.append(splitSign + dto.getSpayacctbankname()); //����������
						filebuf.append(splitSign + dto.getSpaybankcode()); //�������б���
						filebuf.append(splitSign + dto.getSpaybankname()); //������������
						filebuf.append(splitSign + dto.getSbusinesstypecode()); //ҵ�����ͱ���
						filebuf.append(splitSign + dto.getSbusinesstypename()); //ҵ����������
						filebuf.append(splitSign + dto.getSpaytypecode()); //֧����ʽ����
						filebuf.append(splitSign + dto.getSpaytypename()); //֧����ʽ����
						filebuf.append(splitSign + (dto.getSxpaydate()==null?"":dto.getSxpaydate())); //ʵ��֧������
						filebuf.append(splitSign + (dto.getNxsumamt()==null?"":dto.getNxsumamt())); //ʵ��֧�����ܽ��
						filebuf.append("\r\n");
						
						TfPaymentDetailssubDto subDto = new TfPaymentDetailssubDto();
						subDto.setIvousrlno(dto.getIvousrlno());
						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subDto);
						for(TfPaymentDetailssubDto tmpDto:(List<TfPaymentDetailssubDto>)sublist){
							filebuf.append(tmpDto.getSid()); //��ϸ���
							filebuf.append(splitSign + (tmpDto.getSorivoucherno()==null?"":tmpDto.getSorivoucherno())); //֧��ƾ֤���
							filebuf.append(splitSign + tmpDto.getSpayeeacctno()); //�տ����˺�
							filebuf.append(splitSign + tmpDto.getSpayeeacctname()); //�տ�������
							filebuf.append(splitSign + tmpDto.getSpayeeacctbankname()); //�տ�������
							filebuf.append(splitSign + tmpDto.getNpayamt()); //֧�����
							filebuf.append(splitSign + (tmpDto.getSremark()==null?"":tmpDto.getSorivoucherno())); //��ע
							filebuf.append(splitSign + (tmpDto.getSxpaydate()==null?"":tmpDto.getSxpaydate())); //ʵ��֧������
							filebuf.append(splitSign + (tmpDto.getSxagentbusinessno()==null?"":tmpDto.getSxagentbusinessno())); //���н�����ˮ��
							filebuf.append(splitSign + (tmpDto.getNxpayamt()==null?"":tmpDto.getNxpayamt())); //ʵ��֧�����
							filebuf.append(splitSign + (tmpDto.getSxpayeeacctbankname()==null?"":tmpDto.getSxpayeeacctbankname())); //�տ�������
							filebuf.append(splitSign + (tmpDto.getSxpayeeacctno()==null?"":tmpDto.getSxpayeeacctno())); //�տ����˺�
							filebuf.append(splitSign + (tmpDto.getSxaddwordcode()==null?"":tmpDto.getSxaddwordcode())); //ʧ��ԭ�����
							filebuf.append(splitSign + (tmpDto.getSxaddword()==null?"":tmpDto.getSxaddword())); //ʧ��ԭ��
							filebuf.append("\r\n");
						}
					}
				}else{
					for (HtfPaymentDetailsmainDto dto :(List<HtfPaymentDetailsmainDto>) list) {
						filebuf.append(dto.getStrecode()); //�������
						filebuf.append(splitSign + dto.getSstatus()); //״̬
						filebuf.append(splitSign + (dto.getSdemo()==null?"":dto.getSdemo())); //����
						filebuf.append(splitSign + dto.getSfilename()); //�ļ�����
						filebuf.append(splitSign + (dto.getSpackageno()==null?"":dto.getSpackageno())); //����ˮ��
						filebuf.append(splitSign + dto.getSadmdivcode()); //������������
						filebuf.append(splitSign + dto.getSstyear()); //ҵ�����
						filebuf.append(splitSign + dto.getSvtcode()); //ƾ֤���ͱ��
						filebuf.append(splitSign + dto.getSvoudate()); //ƾ֤����
						filebuf.append(splitSign + dto.getSvoucherno()); //ƾ֤��
						filebuf.append(splitSign + dto.getSoriginalvtcode()); //��ƾ֤���ͱ��
						filebuf.append(splitSign + dto.getSoriginalvoucherno()); //��֧��ƾ֤���
						filebuf.append(splitSign + dto.getSfundtypecode()); //�ʽ����ʱ���
						filebuf.append(splitSign + dto.getSfundtypename()); //�ʽ���������
						filebuf.append(splitSign + (dto.getSpaydictateno()==null?"":dto.getSpaydictateno())); //֧���������
						filebuf.append(splitSign + (dto.getSpaymsgno()==null?"":dto.getSpaymsgno())); //֧�����ı��
						filebuf.append(splitSign + (dto.getSpayentrustdate()==null?"":dto.getSpayentrustdate())); //֧��ί������
						filebuf.append(splitSign + (dto.getSpaysndbnkno()==null?"":dto.getSpaysndbnkno())); //֧���������к�
						filebuf.append(splitSign + dto.getNsumamt()); //����֧�����
						filebuf.append(splitSign + (dto.getSagencycode()==null?"":dto.getSagencycode())); //����Ԥ�㵥λ����
						filebuf.append(splitSign + dto.getSagencyname()); //����Ԥ�㵥λ����
						filebuf.append(splitSign + dto.getSpayacctno()); //�������˺�
						filebuf.append(splitSign + dto.getSpayacctname()); //����������
						filebuf.append(splitSign + dto.getSpayacctbankname()); //����������
						filebuf.append(splitSign + dto.getSpaybankcode()); //�������б���
						filebuf.append(splitSign + dto.getSpaybankname()); //������������
						filebuf.append(splitSign + dto.getSbusinesstypecode()); //ҵ�����ͱ���
						filebuf.append(splitSign + dto.getSbusinesstypename()); //ҵ����������
						filebuf.append(splitSign + dto.getSpaytypecode()); //֧����ʽ����
						filebuf.append(splitSign + dto.getSpaytypename()); //֧����ʽ����
						filebuf.append(splitSign + (dto.getSxpaydate()==null?"":dto.getSxpaydate())); //ʵ��֧������
						filebuf.append(splitSign + (dto.getNxsumamt()==null?"":dto.getNxsumamt())); //ʵ��֧�����ܽ��
						filebuf.append("\r\n");
						
						TfPaymentDetailssubDto subDto = new TfPaymentDetailssubDto();
						subDto.setIvousrlno(dto.getIvousrlno());
						List sublist = CommonFacade.getODB().findRsByDtoWithUR(subDto);
						for(TfPaymentDetailssubDto tmpDto:(List<TfPaymentDetailssubDto>)sublist){
							filebuf.append(tmpDto.getSid()); //��ϸ���
							filebuf.append(splitSign + (tmpDto.getSorivoucherno()==null?"":tmpDto.getSorivoucherno())); //֧��ƾ֤���
							filebuf.append(splitSign + tmpDto.getSpayeeacctno()); //�տ����˺�
							filebuf.append(splitSign + tmpDto.getSpayeeacctname()); //�տ�������
							filebuf.append(splitSign + tmpDto.getSpayeeacctbankname()); //�տ�������
							filebuf.append(splitSign + tmpDto.getNpayamt()); //֧�����
							filebuf.append(splitSign + (tmpDto.getSremark()==null?"":tmpDto.getSorivoucherno())); //��ע
							filebuf.append(splitSign + (tmpDto.getSxpaydate()==null?"":tmpDto.getSxpaydate())); //ʵ��֧������
							filebuf.append(splitSign + (tmpDto.getSxagentbusinessno()==null?"":tmpDto.getSxagentbusinessno())); //���н�����ˮ��
							filebuf.append(splitSign + (tmpDto.getNxpayamt()==null?"":tmpDto.getNxpayamt())); //ʵ��֧�����
							filebuf.append(splitSign + (tmpDto.getSxpayeeacctbankname()==null?"":tmpDto.getSxpayeeacctbankname())); //�տ�������
							filebuf.append(splitSign + (tmpDto.getSxpayeeacctno()==null?"":tmpDto.getSxpayeeacctno())); //�տ����˺�
							filebuf.append(splitSign + (tmpDto.getSxaddwordcode()==null?"":tmpDto.getSxaddwordcode())); //ʧ��ԭ�����
							filebuf.append(splitSign + (tmpDto.getSxaddword()==null?"":tmpDto.getSxaddword())); //ʧ��ԭ��
							filebuf.append("\r\n");
						}
					}
				}
				File f = new File(fullpath);
				if (f.exists()) {
					FileUtil.getInstance().deleteFiles(fullpath);
				}
				FileUtil.getInstance().writeFile(fullpath, filebuf.toString().substring(0, filebuf.toString().length()-2));
			    return fullpath.replaceAll(root, "");
			}
		} catch (JAFDatabaseException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����",e);
		} catch (ValidateException e) {
			log.error(e);
			throw new ITFEBizException("��ѯ����",e);
		} catch (FileOperateException e) {
			log.error(e);
			throw new ITFEBizException("д�ļ�����",e);
		}
		return null;
	}	


}