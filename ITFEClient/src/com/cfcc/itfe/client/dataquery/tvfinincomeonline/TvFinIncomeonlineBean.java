package com.cfcc.itfe.client.dataquery.tvfinincomeonline;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;

import com.cfcc.deptone.common.util.DateUtil;
import com.cfcc.itfe.client.ApplicationActionBarAdvisor;
import com.cfcc.itfe.constant.MsgConstant;
import com.cfcc.itfe.exception.FileOperateException;
import com.cfcc.itfe.exception.ITFEBizException;
import com.cfcc.itfe.facade.time.TimeFacade;
import com.cfcc.itfe.persistence.dto.TsConvertfinorgDto;
import com.cfcc.itfe.persistence.dto.TsTaxorgDto;
import com.cfcc.itfe.persistence.dto.TsTreasuryDto;
import com.cfcc.itfe.persistence.dto.TvFinIncomeonlineDto;
import com.cfcc.itfe.service.ITFELoginInfo;
import com.cfcc.itfe.util.FileUtil;
import com.cfcc.jaf.common.page.PageRequest;
import com.cfcc.jaf.common.page.PageResponse;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.control.table.IPageDataProvider;
import com.cfcc.jaf.rcp.control.table.PagingContext;
import com.cfcc.jaf.rcp.util.MessageDialog;

/**
 * codecomment:
 * 
 * @author t60
 * @time 12-02-24 10:44:47 ��ϵͳ: DataQuery ģ��:TvFinIncomeonline
 *       ���:TvFinIncomeonline
 */
public class TvFinIncomeonlineBean extends AbstractTvFinIncomeonlineBean
		implements IPageDataProvider {

	private static Log log = LogFactory.getLog(TvFinIncomeonlineBean.class);
	private ITFELoginInfo loginfo = (ITFELoginInfo) ApplicationActionBarAdvisor
			.getDefault().getLoginInfo();
	private String staxpayname = null;
	private List<TvFinIncomeonlineDto> excheckList;// ѡ�м�¼
	private List<TsConvertfinorgDto> finddtolist = null;
	private List<TsTaxorgDto> querydtolist = null;
	// ���ջ��ش����б�
	private List<TsTaxorgDto> taxorgList;

	public TvFinIncomeonlineBean() {
		super();
		dto = new TvFinIncomeonlineDto();
		dto.setSapplydate(TimeFacade.getCurrentStringTime());// ��������Ĭ�ϵ���
		dto.setSacct(TimeFacade.getCurrentStringTime());// ͳ������Ĭ�ϵ���
		excheckList = new ArrayList<TvFinIncomeonlineDto>();
		staxpayname = "";
		pagingcontext = new PagingContext(this);
		init();
	}

	private void init() {
		// ��ѯ�����Ӧ���ջ��ش�����dto
		taxorgList = new ArrayList<TsTaxorgDto>();
		TsTaxorgDto taxorgdto = new TsTaxorgDto();
		// taxorgdto.setStrecode(sleTreCode);// �������
		taxorgdto.setSorgcode(loginfo.getSorgcode());
		try {
			TsTaxorgDto orgdto0 = new TsTaxorgDto();
			orgdto0.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto0.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);
			orgdto0.setStaxorgname("�������ջ���");
			orgdto0.setStaxprop(MsgConstant.MSG_TAXORG_SHARE_PROP);
			taxorgList.add(orgdto0);

			TsTaxorgDto orgdto1 = new TsTaxorgDto();
			orgdto1.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto1.setStaxorgcode(MsgConstant.MSG_TAXORG_NATION_CLASS);
			orgdto1.setStaxorgname("��˰");
			orgdto1.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
			taxorgList.add(orgdto1);

			TsTaxorgDto orgdto2 = new TsTaxorgDto();
			orgdto2.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto2.setStaxorgcode(MsgConstant.MSG_TAXORG_PLACE_CLASS);
			orgdto2.setStaxorgname("��˰");
			orgdto2.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
			taxorgList.add(orgdto2);

			TsTaxorgDto orgdto3 = new TsTaxorgDto();
			orgdto3.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto3.setStaxorgcode(MsgConstant.MSG_TAXORG_CUSTOM_CLASS);
			orgdto3.setStaxorgname("����");
			orgdto3.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
			taxorgList.add(orgdto3);

			TsTaxorgDto orgdto4 = new TsTaxorgDto();
			orgdto4.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto4.setStaxorgcode(MsgConstant.MSG_TAXORG_FINANCE_CLASS);
			orgdto4.setStaxorgname("����");
			orgdto4.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
			taxorgList.add(orgdto4);

			TsTaxorgDto orgdto5 = new TsTaxorgDto();
			orgdto5.setSorgcode(loginfo.getSorgcode());// ��������
			orgdto5.setStaxorgcode(MsgConstant.MSG_TAXORG_OTHER_CLASS);
			orgdto5.setStaxorgname("����");
			orgdto5.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
			taxorgList.add(orgdto5);
			taxorgList.addAll(commonDataAccessService.findRsByDto(taxorgdto));
		} catch (Throwable e) {
			MessageDialog.openErrorDialog(null, e);
			return;
		}
		// ��ʼ�����ջ��ش���Ĭ��ֵ
		if (taxorgList.size() > 0) {
			dto.setStaxorgcode(MsgConstant.MSG_TAXORG_SHARE_CLASS);
		}
	}

	/**
	 * Direction: ��ѡ ename: singleSelect ���÷���: viewers: * messages:
	 */
	public String singleSelect(Object o) {
		return super.singleSelect(o);
	}

	/**
	 * Direction: ��ѯ ename: query ���÷���: viewers: ����˰Ʊ��Ϣ��ѯ��� messages:
	 */
	public String query(Object o) {

		// ���ѡ��Ȩ��
		excheckList = new ArrayList<TvFinIncomeonlineDto>();
		// ����ҳ��
		String returnpage = null;

		// ���Ļ�������
		String centerorg = null;
		try {
			centerorg = itfeCacheService.cacheGetCenterOrg();
		} catch (ITFEBizException e) {
			MessageDialog.openErrorDialog(null, e);
		}

		// �жϵ�ǰ��¼��ѯ������롢���������Ƿ��ں���������
		TsConvertfinorgDto finddto = new TsConvertfinorgDto();

		if (!loginfo.getSorgcode().equals(centerorg)) {

			finddto.setSorgcode(loginfo.getSorgcode()); // �����������
			if (dto.getSorgcode() != null && !"".equals(dto.getSorgcode())) {
				finddto.setSfinorgcode(dto.getSorgcode()); // ��������
			}
			if (dto.getStrecode() != null && !"".equals(dto.getStrecode())) {
				finddto.setStrecode(dto.getStrecode()); // �����������
			}
		}
		
			try {
			finddtolist = commonDataAccessService.findRsByDtocheckUR(finddto,
					"1");
		} catch (ITFEBizException e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if (null == finddtolist || finddtolist.size() == 0) {
			MessageDialog.openMessageDialog(null, " Ȩ�޲��㣡����д��ȷ�Ĳ���������߹�����룡");
			return null;
		}

		PageRequest pageRequest = new PageRequest();
		PageResponse pageResponse = retrieve(pageRequest);
		pagingcontext.setPage(pageResponse);
		if (pageResponse == null || pageResponse.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѯ�޼�¼��");
			returnpage = "����˰Ʊ��Ϣ��ѯ����";
		} else {
			returnpage = super.query(o);
		}
		return returnpage;

	}

	/**
	 * Direction: ���� ename: goBack ���÷���: viewers: ����˰Ʊ��Ϣ��ѯ���� messages:
	 */
	public String goBack(Object o) {
		return super.goBack(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.cfcc.jaf.rcp.control.table.IPageDataProvider#retrieve(com.cfcc.jaf
	 * .common.page.PageRequest)
	 */
	public PageResponse retrieve(PageRequest pageRequest) {

		try {
			TvFinIncomeonlineDto tmpsearchDto = (TvFinIncomeonlineDto) dto
					.clone();
			String strwhere = "1=1 ";

			// ��������Ϊ�գ��������Ϊ�գ���ȫ��
			if ((dto.getStrecode() == null || "".equals(dto.getStrecode()))
					&& (dto.getSorgcode() == null || "".equals(dto
							.getSorgcode())) && finddtolist != null
					&& finddtolist.size() != 0) {

				strwhere += " and ( S_TRECODE='"
						+ finddtolist.get(0).getStrecode() + "' ";
				for (int i = 1; i < finddtolist.size(); i++) {
					strwhere += " or S_TRECODE= '"
							+ finddtolist.get(i).getStrecode() + "' ";
				}
				strwhere += " ) ";
			}

			if (staxpayname != null && !"".equals(staxpayname)) {
				strwhere += " and S_TAXPAYNAME like '%" + staxpayname.trim()
						+ "%' ";
			}

			strwhere += " And S_ACCT >='" + tmpsearchDto.getSapplydate()
					+ "' and S_ACCT <='" + tmpsearchDto.getSacct() + "'";
			tmpsearchDto.setSacct(null);
			tmpsearchDto.setSapplydate(null);
			
			// /////////////////////////////////////////////////////////////////////////
			// ����Ǵ�������ݴ�������ջ������ʲ���
			boolean bool = false;
			TsTaxorgDto taxorgdto = new TsTaxorgDto();
			taxorgdto.setSorgcode(loginfo.getSorgcode());
			if (MsgConstant.MSG_TAXORG_SHARE_CLASS.equals(dto.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_NATION_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_PLACE_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_CUSTOM_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_FINANCE_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_OTHER_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
				bool = true;
			}
			
				StringBuffer sql = new StringBuffer();
				if (bool) {
					List<TsTaxorgDto> list = commonDataAccessService.findRsByDto(taxorgdto);
					if(null != list && list.size() > 0){
						sql.append(" and S_TAXORGCODE in (");
						for(TsTaxorgDto tmpdto : list ){
							sql.append("'" + tmpdto.getStaxorgcode() + "',");
						}
						sql.append("'') ");
						strwhere = strwhere + sql.toString();
					}
				}
				// /////////////////////////////////////////////////////////////////////////
			
			
			
			
			
			return commonDataAccessService.findRsByDtoWithWherePaging(
					tmpsearchDto, pageRequest, strwhere, "", tmpsearchDto
							.tableName());

		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return super.retrieve(pageRequest);
	}

	/**
	 * Direction: У����˰��������Ӧ��ϵ ename: checkTaxPayCodeOrTrecode ���÷���: viewers: *
	 * messages:
	 */
	public String checkTaxPayCodeOrTrecode(Object o) {

		if (null == this.excheckList || excheckList.size() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ�����ļ�¼��");
			return null;
		}
		String msg = null;
		try {
			msg = tvFinIncomeonlineService
					.checkTaxPayCodeOrTrecode(excheckList);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if ("".equals(msg)) {
			MessageDialog.openMessageDialog(null, " У��ɹ���");
		} else {
			String fPath = "C:/client/checkmsg/" + loginfo.getSorgcode() + "_"
					+ dto.getStrecode() + "_" + loginfo.getSuserCode() + "_"
					+ DateUtil.date2String3(new Date()) + ".txt";
			try {
				// �ļ���д
				FileUtil.getInstance().writeFile(fPath, msg);
			} catch (Throwable e) {
				MessageDialog.openMessageDialog(null, " У����Ϣд���ļ�����ʧ�ܣ�");
				return null;
			}
			MessageDialog.openMessageDialog(null, " У��ʧ�ܣ�ʧ����Ϣ�ѱ��浽��" + fPath);
		}
		return super.checkTaxPayCodeOrTrecode(o);
	}

	/**
	 * Direction: �ֶ�������ֳ� ename: makeDivide ���÷���: viewers: * messages:
	 */
	public String makeDivide(Object o) {

		if (null == this.excheckList || excheckList.size() == 0) {
			MessageDialog.openMessageDialog(null, " ��ѡ��Ҫ�����ļ�¼��");
			return null;
		}

		String msg = null;
		try {
			msg = tvFinIncomeonlineService.makeDivide(excheckList, dto
					.getSapplydate());
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		if ("".equals(msg)) {
			MessageDialog.openMessageDialog(null, " �ֹ�����ɹ���");
		} else {
			MessageDialog.openMessageDialog(null, " �ֹ�������δִ����Ϣ��ԭ��" + msg);
		}

		return super.makeDivide(o);
	}

	/**
	 * Direction: ���� ename: exportTable ���÷���: viewers: * messages:
	 */
	public String exportTable(Object o) {

		PageResponse page = this.pagingcontext.getPage();
		if (page == null || page.getTotalCount() == 0) {
			MessageDialog.openMessageDialog(null, "�б�������,�޷�ִ�е�������������ѯ��");
			return null;
		}
		// ѡ�񱣴�·��
		DirectoryDialog path = new DirectoryDialog(Display.getCurrent()
				.getActiveShell());
		String filePath = path.open();
		List<TvFinIncomeonlineDto> filelist = new ArrayList<TvFinIncomeonlineDto>();
		if ((null == filePath) || (filePath.length() == 0)) {
			MessageDialog.openMessageDialog(null, "��ѡ�񵼳��ļ�·����");
			return "";
		}
		String fileName = filePath + File.separator + "3129����˰Ʊ("
				+ dto.getSapplydate() + "-" + dto.getSacct() + ").CSV";

		try {
			TvFinIncomeonlineDto tmpsearchDto = (TvFinIncomeonlineDto) dto
					.clone();
			String strwhere = "";

			// ��������Ϊ�գ��������Ϊ�գ���ȫ��
			if ((dto.getStrecode() == null || "".equals(dto.getStrecode()))
					&& (dto.getSorgcode() == null || "".equals(dto
							.getSorgcode())) && finddtolist != null
					&& finddtolist.size() != 0) {

				strwhere += " and ( S_TRECODE='"
						+ finddtolist.get(0).getStrecode() + "' ";
				for (int i = 1; i < finddtolist.size(); i++) {
					strwhere += " or S_TRECODE= '"
							+ finddtolist.get(i).getStrecode() + "' ";
				}
				strwhere += " ) ";
			}

			if (staxpayname != null && !"".equals(staxpayname)) {
				strwhere += " and S_TAXPAYNAME like '%" + staxpayname.trim()
						+ "%' ";
			}

			strwhere += " And S_ACCT >='" + tmpsearchDto.getSapplydate()
					+ "' and S_ACCT <='" + tmpsearchDto.getSacct() + "'";
			tmpsearchDto.setSacct(null);
			tmpsearchDto.setSapplydate(null);
			// /////////////////////////////////////////////////////////////////////////
			// ����Ǵ�������ݴ�������ջ������ʲ���
			boolean bool = false;
			TsTaxorgDto taxorgdto = new TsTaxorgDto();
			taxorgdto.setSorgcode(loginfo.getSorgcode());
			if (MsgConstant.MSG_TAXORG_SHARE_CLASS.equals(dto.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_NATION_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_NATION_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_PLACE_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_PLACE_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_CUSTOM_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_CUSTOM_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_FINANCE_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_FINANCE_PROP);
				bool = true;
			} else if (MsgConstant.MSG_TAXORG_OTHER_CLASS.equals(dto
					.getStaxorgcode())) {
				tmpsearchDto.setStaxorgcode(null);
				taxorgdto.setStaxprop(MsgConstant.MSG_TAXORG_OTHER_PROP);
				bool = true;
			}
			
				StringBuffer sql = new StringBuffer();
				if (bool) {
					List<TsTaxorgDto> list = commonDataAccessService.findRsByDto(taxorgdto);
					if(null != list && list.size() > 0){
						sql.append(" and S_TAXORGCODE in (");
						for(TsTaxorgDto tmpdto : list ){
							sql.append("'" + tmpdto.getStaxorgcode() + "',");
						}
						sql.append("'') ");
						strwhere = strwhere + sql.toString();
					}
				}
				// /////////////////////////////////////////////////////////////////////////
			
			filelist = tvFinIncomeonlineService.exportTable(tmpsearchDto, strwhere);
			exportTableForWhere(filelist, fileName, ",");
			MessageDialog.openMessageDialog(null, "���ݵ����ɹ�\n" + fileName);
		} catch (Throwable e) {
			log.error(e);
			MessageDialog.openErrorDialog(null, e);
		}
		return "";
	}

	/**
	 * ��������������
	 * 
	 * @param list
	 * @param filename
	 * @return
	 * @throws FileOperateException
	 */
	public String exportTableForWhere(List<TvFinIncomeonlineDto> templist,
			String fileName, String splitSign) throws FileOperateException {
		String sql = "�������ش���,��������,����ˮ��,�������,��������,���ջ��ش���,�������к�,������ˮ��,ԭ���ı��,���׽��,�����˿������к�,�����������,�ɿλ����,�������˺�,˰Ʊ����,��Ʊ����,��˰�˱��,��˰������,Ԥ������,�����ڱ�־,��ҵ����,��ҵ����,��ҵ����,Ԥ���Ŀ����,Ԥ���Ŀ����,�޽�����,˰�ִ���,˰������,Ԥ�㼶��,Ԥ�㼶������,˰������������,˰����������ֹ,������־,˰������,��������,����״̬,��ע,��ע1,��ע2,¼��Ա����,ϵͳ����ʱ��,ҵ����ˮ��	";
		StringBuffer filebuf = new StringBuffer(sql + "\r\n");
		File file=new File(fileName);
		if(file.exists()){
			file.delete();
		}
		int count = 0;
		for (TvFinIncomeonlineDto _dto : templist) {
			filebuf.append(_dto.getSorgcode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSapplydate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSpackno());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStrecode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStrename());
			filebuf.append(splitSign);

			filebuf.append(_dto.getStaxorgcode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSpaybnkno());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStrano());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSorimsgno());
			filebuf.append(splitSign);
			filebuf.append(_dto.getFtraamt());
			filebuf.append(splitSign);

			filebuf.append(_dto.getSpayeropnbnkno());
			filebuf.append(splitSign);
			filebuf.append(_dto.getPayeropbkname());
			filebuf.append(splitSign);
			filebuf.append(_dto.getShandorgname());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSpayacct());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxvouno());
			filebuf.append(splitSign);

			filebuf.append(_dto.getSbilldate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxpaycode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxpayname());
			filebuf.append(splitSign);
			filebuf.append(_dto.getCbudgettype());
			filebuf.append(splitSign);
			filebuf.append(_dto.getCtrimflag());
			filebuf.append(splitSign);

			filebuf.append(_dto.getSetpcode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSetpname());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSetptype());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSbdgsbtcode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSbdgsbtname());
			filebuf.append(splitSign);

			filebuf.append(_dto.getSlimit());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxtypecode());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxkindname());
			filebuf.append(splitSign);
			filebuf.append(_dto.getCbdglevel());
			filebuf.append(splitSign);
			filebuf.append(_dto.getCbdglevelname());
			filebuf.append(splitSign);

			filebuf.append(_dto.getStaxstartdate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getStaxenddate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSastflag());
			filebuf.append(splitSign);
			filebuf.append(_dto.getCtaxtype());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSacct());
			filebuf.append(splitSign);

			filebuf.append(_dto.getStrastate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSremark());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSremark1());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSremark2());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSinputerid());
			filebuf.append(splitSign);

			filebuf.append(_dto.getTssysupdate());
			filebuf.append(splitSign);
			filebuf.append(_dto.getSseq());
			filebuf.append(splitSign);
			filebuf.append("\r\n");
			count ++ ;
			if(count > 5000){
				FileUtil.getInstance().writeFile(fileName, filebuf.toString(),true);
				filebuf = new StringBuffer();
				count = 0;
			}

		}
		FileUtil.getInstance().writeFile(fileName, filebuf.toString(),true);
		return staxpayname;

	}

	/**
	 * Direction: ȫѡ/��ѡ ename: selectAllOrNone ���÷���: viewers: * messages:
	 */
	public String selectAllOrNone(Object o) {
		if (this.pagingcontext == null) {
			return super.selectAllOrNone(o);
		}
		PageResponse page = this.pagingcontext.getPage();
		if (page == null || page.getTotalCount() == 0) {
			return super.selectAllOrNone(o);
		}
		List<TvFinIncomeonlineDto> templist = page.getData();
		if (templist != null && this.excheckList != null) {
			if (excheckList.size() != 0 && excheckList.containsAll(templist)) {
				excheckList.removeAll(templist);
			} else {
				for (int i = 0; i < templist.size(); i++) {
					if (excheckList.contains(templist.get(i))) {
						excheckList.set(excheckList.indexOf(templist.get(i)),
								templist.get(i));
					} else {
						excheckList.add(i, templist.get(i));
					}
				}
			}
		}
		this.editor.fireModelChanged();
		return super.selectAllOrNone(o);
	}

	public static Log getLog() {
		return log;
	}

	public static void setLog(Log log) {
		TvFinIncomeonlineBean.log = log;
	}

	public ITFELoginInfo getLoginfo() {
		return loginfo;
	}

	public void setLoginfo(ITFELoginInfo loginfo) {
		this.loginfo = loginfo;
	}

	public String getStaxpayname() {
		return staxpayname;
	}

	public void setStaxpayname(String staxpayname) {
		this.staxpayname = staxpayname;
	}

	public List<TvFinIncomeonlineDto> getExcheckList() {
		return excheckList;
	}

	public void setExcheckList(List<TvFinIncomeonlineDto> excheckList) {
		this.excheckList = excheckList;
	}

	public List<TsConvertfinorgDto> getFinddtolist() {
		return finddtolist;
	}

	public void setFinddtolist(List<TsConvertfinorgDto> finddtolist) {
		this.finddtolist = finddtolist;
	}

	public List<TsTaxorgDto> getQuerydtolist() {
		return querydtolist;
	}

	public void setQuerydtolist(List<TsTaxorgDto> querydtolist) {
		this.querydtolist = querydtolist;
	}

	public List<TsTaxorgDto> getTaxorgList() {
		return taxorgList;
	}

	public void setTaxorgList(List<TsTaxorgDto> taxorgList) {
		this.taxorgList = taxorgList;
	}

}