package com.cfcc.itfe.service.util;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.constant.SequenceName;
import com.cfcc.itfe.constant.StateConstant;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.exception.SequenceException;
import com.cfcc.itfe.facade.CommonFacade;
import com.cfcc.itfe.facade.DatabaseFacade;
import com.cfcc.itfe.facade.SequenceGenerator;
import com.cfcc.itfe.persistence.dto.TbsTvDwbkDto;
import com.cfcc.itfe.persistence.dto.TvFilepackagerefDto;
import com.cfcc.jaf.persistence.dao.exception.JAFDatabaseException;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.persistence.jdbc.sql.SQLExecutor;
import com.cfcc.jaf.persistence.util.ValidateException;

public class SetFail1104 {
	
	private static Log logger = LogFactory.getLog(SetFail5101.class);
	public void setFail(IDto dto, String orgcode)throws ITFEBizException{
		if(dto instanceof TbsTvDwbkDto){
			TbsTvDwbkDto paydto = (TbsTvDwbkDto)dto;
		SQLExecutor sqlExec = null;
		try {
			TvFilepackagerefDto ref = new TvFilepackagerefDto();
			ref.setSorgcode(orgcode);
			ref.setSpackageno(paydto.getSpackageno());
			List refl = CommonFacade.getODB().findRsByDtoWithUR(ref);
			if (refl != null && refl.size() >0) { // ֻ��һ����¼ ��ֻ���¼�¼״̬Ϊ����
				TvFilepackagerefDto newRef = new TvFilepackagerefDto();
				newRef = (TvFilepackagerefDto) refl.get(0);
				if (newRef.getIcount() == 1) {
					String newName = paydto.getSfilename() + "_" + paydto.getSpackageno();
					paydto.setSfilename(newName);
					paydto.setSstatus("0");
					DatabaseFacade.getODB().update(paydto);
					DatabaseFacade.getODB().delete(newRef);//ɾ������Ϊ�ļ����������޷��޸�(update)��ֻ����ɾ���󴴽�
					newRef.setSfilename(newName); //��ֻ��һ�ʼ�¼�İ���ˮ��Ӧ��ϵ���ļ���ͬʱ�޸ģ����������ŵ����鱨���˶�����ԭ�ļ�������
					DatabaseFacade.getODB().create(newRef);
					return;
				}
				// 1.���İ���ˮ��Ӧ��ϵ
				sqlExec = DatabaseFacade.getDb().getSqlExecutorFactory()
						.getSQLExecutor();

				String movedataSql = "UPDATE TV_FILEPACKAGEREF SET N_MONEY=N_MONEY-? ,I_COUNT=I_COUNT-1 WHERE S_ORGCODE= ? AND S_FILENAME= ? AND S_PACKAGENO= ? ";
				// ƾ֤���
				sqlExec.addParam(paydto.getFamt());
				// �����������
				sqlExec.addParam(orgcode);
				// �ļ���
				sqlExec.addParam(paydto.getSfilename());
				// ����ˮ��
				sqlExec.addParam(paydto.getSpackageno());
				sqlExec.runQueryCloseCon(movedataSql);
				String tmpPackNo = SequenceGenerator
						.changePackNoForLocal(SequenceGenerator.getNextByDb2(
								SequenceName.FILENAME_PACKNO_REF_SEQ,
								SequenceName.TRAID_SEQ_CACHE,
								SequenceName.TRAID_SEQ_STARTWITH,
								MsgConstant.SEQUENCE_MAX_DEF_VALUE));
				String newFileName = paydto.getSfilename() + "_" + tmpPackNo;
				paydto.setSpackageno(tmpPackNo); // ��������ˮ��
				paydto.setSfilename(newFileName);
				paydto.setSstatus("0");
				newRef.setSorgcode(orgcode);
				newRef.setSpackageno(tmpPackNo);
				newRef.setIcount(1);
				newRef.setSfilename(newFileName);
				newRef.setNmoney(paydto.getFamt());
				DatabaseFacade.getODB().create(newRef); // �����µĶ�Ӧ��ϵ
				DatabaseFacade.getODB().update(paydto); // ������Ϣ
			}else{
				throw new ITFEBizException("�޷��ҵ���Ӧ����Ϣ");
			}
		} catch (Exception e) {
			logger.error("�˿�1104ҵ���쳣!", e);
			throw new ITFEBizException("�˿�1104ҵ���쳣!", e);
		}
	}
	}

}
