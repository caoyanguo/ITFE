package com.cfcc.itfe.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cfcc.itfe.persistence.dto.HtvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.HtvPayreckBankDto;
import com.cfcc.itfe.persistence.dto.TvDirectpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvGrantpaymsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayoutmsgmainDto;
import com.cfcc.itfe.persistence.dto.TvPayreckBankDto;
import com.cfcc.jaf.common.util.Base64Util;
import com.cfcc.jaf.persistence.jaform.parent.IDto;
import com.cfcc.jaf.rcp.mvc.IModel;


public class CommonUtil {

	private static Log log = LogFactory.getLog(CommonUtil.class);

	/**
	 * ���ݱ���ת����Dto��
	 * 
	 * @param tabName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class tableConverDto(String tabName)
			throws ClassNotFoundException {

		return Class.forName(tableToDtoName(tabName));
	}

	/**
	 * ���ݱ���ʵ����Ϊdto
	 * @param tabName
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static IDto tableCrtDto(String tabName)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return (IDto)Class.forName(tableToDtoName(tabName)).newInstance();
	}

	public static IDto[] listTArray(List<IDto> list) {
		return list.toArray(new IDto[list.size()]);
	}
/**
 * ���ݱ���ת����ȫСд�ַ� Tm_User to tmuser
 * @param table
 * @return
 */
	public static String tableFormat1(String table){
	return table.toLowerCase().replaceAll("_", "");
}
	/**
	 * ���ݱ���ת����Dto����
	 * 
	 * @param tabName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static String tableToDtoName(String tabName) {
		StringBuffer buf = new StringBuffer(
				"com.cfcc.itfe.persistence.dto.");

		buf.append(tableFormat(tabName));
		buf.append("Dto");
		return buf.toString();
	}

	/**
	 * ���ݱ���ת����Dto���� ���磺 TM_USER TO tmuserDto
	 * 
	 * @param tabName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static String tableToDtoProp(String tabName) {
		StringBuffer buf = new StringBuffer();

		buf.append(tableFormat(tabName));
		String name = buf.toString();
		name = name.toLowerCase();
		return name + "Dto";
	}

	/**
	 * ���ݱ���ת����DtoList���� ���磺 TM_USER TO tmuserList
	 * 
	 * @param tabName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static String tableToDtoListProp(String tabName) {
		StringBuffer buf = new StringBuffer();

		buf.append(tableFormat(tabName));
		String name = buf.toString();
		name = name.toLowerCase();
		return name + "List";
	}

	/**
	 * ���ݱ���ת����Info����
	 * 
	 * @param tabName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static String tableToInfoName(String tabName) {
		StringBuffer buf = new StringBuffer(
				"com.cfcc.itfe.persistence.info.");

		buf.append(tableFormat(tabName));
		buf.append("Info");
		return buf.toString();
	}

	/**
	 * ���ݱ���ת����Bean����
	 * 
	 * @param tabName
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static String tableToBeanName(String tabName) {
		StringBuffer pkg = new StringBuffer(
				"com.cfcc.itfe.client.paramsauditsubsys.");
		String name = tableFormat(tabName);
		String lowName = name.toLowerCase();
		pkg.append(lowName + "audit.");
		pkg.append(name + "AuditBean");
		return pkg.toString();
	}

	/**
	 * ҵ��ල����תbean����
	 * 
	 * @param tabName
	 * @return
	 */

	public static String bizTableToBeanName(String tabName) {
		StringBuffer pkg = new StringBuffer("com.cfcc.itfe.client.querySubSys.");
		String name = tableFormat(tabName);
		String lowName = name.toLowerCase();
		pkg.append(lowName + "compareresult.");
		pkg.append(name + "CompareResult");
		return pkg.toString();
	}

	private static String tableFormat(String tabName) {
		tabName = tabName.toLowerCase();
		StringBuffer buf = new StringBuffer("");
		String tmp = (tabName.substring(0, 1)).toUpperCase();
		buf.append(tmp);
		for (int i = 1; i < tabName.length(); i++) {
			String temp = tabName.substring(i, i + 1);
			if (temp.equalsIgnoreCase("_")) {
				i++;
				temp = tabName.substring(i, i + 1).toUpperCase();

			}
			buf.append(temp);
		}
		return buf.toString();
	}

	/**
	 * ����Dto����ת���ɱ���
	 * 
	 * @param idto
	 * @return
	 * @throws Exception
	 */
	public static String idtoToTableName(IDto idto) throws Exception {

		Method m = idto.getClass().getDeclaredMethod("tableName");
		return m.invoke(idto).toString();

	}

	public static void main(String[] args) {
		//TasTvGroupHeadDto headdto = new TasTvGroupHeadDto();
		try {
		//	Object dto = createTbsObjByTasDto(headdto);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static List<String> mapValueToList(HashMap map) {
		if (map == null || map.size() <= 0) {
			return null;
		}
		Object[] o = map.values().toArray();
		if (o == null || o.length <= 0) {
			return null;
		}
		List<String> l = new ArrayList<String>();
		for (int i = 0; i < o.length; i++) {
			l.add((String) o[i]);
		}

		return l;
	}

	/**
	 * ����TAS���¼�����ݿ����õĲ�ѯ��������ȡTBSDto
	 * 
	 * @param tasdto
	 * @param propList
	 *            ���ݿ����õĲ�ѯҪ��
	 * @param tasdto
	 * @param propList
	 * @param biztype
	 *            ҵ������
	 * @return
	 * @throws Exception
	 */
	public static IDto createTbsDtoByTas(IDto tasdto, List<String> propList,
			String biztype, Long grpseqno) throws Exception {
		Object tbsObj = createTbsObjByTasDto(tasdto);
		if (propList == null || propList.size() <= 0) {
			log.error("���ݿ�û�����øñ�Ĳ�ѯ����" + tasdto.getClass().getName() + ",ҵ������="
					+ biztype + ",�����=" + grpseqno.toString());
		}
		for (String prop : propList) {
			Object value = getPropertyUtils().getSimpleProperty(tasdto, prop);
			copyProperty(tbsObj, prop, value);
		}
		copyProperty(tbsObj, "sbiztype", biztype);
		// �����ж�����Ŷ��ڶ�ȼƻ�û����ŵ����������Ŵ�null
		if (grpseqno != null) {
			copyProperty(tbsObj, "igrpseqno", grpseqno);
		}
		return (IDto) tbsObj;
	}

	/**
	 * ����tasdto������Ӧ��ȫҪ��tbsdto
	 * 
	 * @param tasdto
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static IDto createTbsDtoByTas(IDto tasdto)
			throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {

		Object tbsObj = createTbsObjByTasDto(tasdto);
		copyProperties(tbsObj, tasdto);
		IDto tbsdto = (IDto) tbsObj;
		return tbsdto;
	}

	/**
	 * ��YYYYDDMM�ַ�����ʽ����YYYY-MM-DD��Date
	 * 
	 * @param str
	 * @return
	 */
	public static Date strToDate(String str) {
		if (str.length() == 10) {
			return Date.valueOf(str);
		} else if (str.length() == 8) {
			str = str.substring(0, 4) + "-" + str.substring(4, 6) + "-"
					+ str.substring(6, 8);
			return Date.valueOf(str);
		}
		return null;
	}

	/**
	 * ��������
	 * 
	 * @param dest
	 * @param orig
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public static void copyProperties(Object dest, Object orig)
			throws IllegalAccessException, InvocationTargetException {

		// Validate existence of the specified beans
		if (dest == null) {
			throw new IllegalArgumentException("No destination bean specified");
		}
		if (orig == null) {
			throw new IllegalArgumentException("No origin bean specified");
		}
		if (log.isDebugEnabled()) {
			log.debug("BeanUtils.copyProperties(" + dest + ", " + orig + ")");
		}
		// Copy the properties, converting as necessary
		if (orig instanceof DynaBean) {
			DynaProperty origDescriptors[] = ((DynaBean) orig).getDynaClass()
					.getDynaProperties();
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if (getPropertyUtils().isWriteable(dest, name)) {
					Object value = ((DynaBean) orig).get(name);
					copyProperty(dest, name, value);
				}
			}
		} else if (orig instanceof Map) {
			Iterator names = ((Map) orig).keySet().iterator();
			while (names.hasNext()) {
				String name = (String) names.next();
				if (getPropertyUtils().isWriteable(dest, name)) {
					Object value = ((Map) orig).get(name);
					copyProperty(dest, name, value);
				}
			}
		} else /* if (orig is a standard JavaBean) */{
			PropertyDescriptor origDescriptors[] = getPropertyUtils()
					.getPropertyDescriptors(orig);
			for (int i = 0; i < origDescriptors.length; i++) {
				String name = origDescriptors[i].getName();
				if ("class".equals(name) || "children".equals(name)) {
					continue; // No point in trying to set an object's class
				}
				if (getPropertyUtils().isReadable(orig, name)
						&& getPropertyUtils().isWriteable(dest, name)) {
					try {
						Object value = getPropertyUtils().getSimpleProperty(
								orig, name);
						if (value == null) {
							continue;
						}
						if (origDescriptors[i].getPropertyType().equals(
								Date.class)
								&& value == null) {
							continue;
						}
						if (origDescriptors[i].getPropertyType().equals(
								Timestamp.class)
								&& value == null) {
							continue;
						}
						if (origDescriptors[i].getPropertyType().equals(
								BigDecimal.class)
								&& value == null) {
							continue;
						}
						copyProperty(dest, name, value);
					} catch (NoSuchMethodException e) {
						log.error(e); // Should not happen
					}
				}
			}
		}

	}

	public static PropertyUtilsBean getPropertyUtils() {
		return BeanUtilsBean.getInstance().getPropertyUtils();
	}

	public static void copyProperty(Object bean, String name, Object value)
			throws IllegalAccessException, InvocationTargetException {
		BeanUtilsBean.getInstance().copyProperty(bean, name, value);
	}

	/**
	 * ����TASDto new һ��TBSDto
	 * 
	 * @param tasDto
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public static Object createTbsObjByTasDto(IDto tasDto)
			throws ClassNotFoundException, IllegalArgumentException,
			SecurityException, InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		String tasName = tasDto.getClass().getName();
		String tbsName = StringUtils.replace(tasName, "Tas", "Tbs", 1);
		return Class.forName(tbsName).newInstance();
	}

	public static IDto getIdto(IModel model, String table)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		return (IDto) PropertyUtils.getProperty(model, tableToDtoProp(table));
	}

	public static IDto createDtoByName(String fulldtoname)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {
		return (IDto) Class.forName(fulldtoname).newInstance();

	}

	public static IDto createDtoByTable(String table)
			throws IllegalArgumentException, SecurityException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException,
			ClassNotFoundException {
		return (IDto) Class.forName(tableToDtoName(table)).newInstance();

	}

	/**
	 * MD5��֤����
	 * 
	 * @param String
	 *            str
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static String MD5Authentication(String str) {
		String key = null;
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(str.getBytes());
			// key = new String(md5.digest());
			 
			key = new String(Base64Util.encodeBytes(md5.digest()));
		} catch (NoSuchAlgorithmException e) {
			log.error(e);
		}
		return key;
	}
	
	  /**
	 * ��ȡ���к��ֵ��ַ���
	 * 
	 * @param str
	 *            ԭʼ�ַ���
	 * @param length
	 *            ��ȡ��λ��
	 * @return
	 * 			��ȡ����ַ���
	 */
	public static String subString(String str, int length) {
		char[] charArray = str.toCharArray();
		String subStr = "";
		int index = 0;
		for (int i = 0, j = 0; i < length;) {
			String c = String.valueOf(charArray[j]);
			if (c.getBytes().length > 1) {
				// �������ַ���һ�����ֻ����ķ���
				i = i + 2;
				if (i > length) {
					// ���num���ý�ȡ��������ֵ�ʱ�������˴�forѭ����
					// ���num���ý�ȡ��һ�������ĺ��ֵ�ʱ�򣬼���ִ�������index++����䡣
					if (i == (length + 1)) {
						continue;
					}
				}
			} else {
				i++;
			}
			index++;
			j++;
		}
		subStr = str.substring(0, index);
		return subStr;
	}
	
	/**
	 * ����������������ӱ��Ŀ�����ѯ����
	 */
	public static String getFuncCodeSql(String sqlwhere, IDto dto,String expfunccode,String payamt){
		if(sqlwhere==null || "".equals(sqlwhere)){
			sqlwhere = "";
		}
		String subsql = "";
		if(dto instanceof TvDirectpaymsgmainDto){//ֱ��֧�����
			if(expfunccode!=null && !"".equals(expfunccode)){
				subsql = subsql + " and S_FUNSUBJECTCODE='"+expfunccode+"'";
			}
			if(payamt!=null && !"".equals(payamt)){
				subsql = subsql + " and N_MONEY='"+payamt+"'";
			}
			if(!subsql.equals("")){
				sqlwhere = sqlwhere + " and I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM TV_DIRECTPAYMSGSUB WHERE "+subsql.replaceFirst("and", "")+" ) ";
			}
		}else if(dto instanceof HtvDirectpaymsgmainDto){
			if(expfunccode!=null && !"".equals(expfunccode)){
				subsql = subsql + " and S_FUNSUBJECTCODE='"+expfunccode+"'";
			}
			if(payamt!=null && !"".equals(payamt)){
				subsql = subsql + " and N_MONEY='"+payamt+"'";
			}
			if(!subsql.equals("")){
				sqlwhere = sqlwhere + " and I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM HTV_DIRECTPAYMSGSUB WHERE "+subsql.replaceFirst("and", "")+" ) ";
			}
		}else if(dto instanceof TvGrantpaymsgmainDto){//��Ȩ֧�����
			if(expfunccode!=null && !"".equals(expfunccode)){
				subsql = subsql + " and S_FUNSUBJECTCODE='"+expfunccode+"'";
			}
			if(payamt!=null && !"".equals(payamt)){
				subsql = subsql + " and N_MONEY='"+payamt+"'";
			}
			if(!subsql.equals("")){
				sqlwhere = sqlwhere + " and I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM TV_GRANTPAYMSGSUB WHERE "+subsql.replaceFirst("and", "")+" ) ";
			}
		}else if(dto instanceof HtvGrantpaymsgmainDto){
			if(expfunccode!=null && !"".equals(expfunccode)){
				subsql = subsql + " and S_FUNSUBJECTCODE='"+expfunccode+"'";
			}
			if(payamt!=null && !"".equals(payamt)){
				subsql = subsql + " and N_MONEY='"+payamt+"'";
			}
			if(!subsql.equals("")){
				sqlwhere = sqlwhere + " and I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM HTV_GRANTPAYMSGSUB WHERE "+subsql.replaceFirst("and", "")+" ) ";
			}
		}else if(dto instanceof TvPayoutmsgmainDto){//ʵ���ʽ�
			if(expfunccode!=null && !"".equals(expfunccode)){
				subsql = subsql + " and S_FUNSUBJECTCODE='"+expfunccode+"'";
			}
			if(payamt!=null && !"".equals(payamt)){
				subsql = subsql + " and N_MONEY='"+payamt+"'";
			}
			if(!subsql.equals("")){
				sqlwhere = sqlwhere + " and S_BIZNO IN (SELECT S_BIZNO FROM TV_PAYOUTMSGSUB WHERE "+subsql.replaceFirst("and", "")+" ) ";
			}
		}else if(dto instanceof HtvPayoutmsgmainDto){
			if(expfunccode!=null && !"".equals(expfunccode)){
				subsql = subsql + " and S_FUNSUBJECTCODE='"+expfunccode+"'";
			}
			if(payamt!=null && !"".equals(payamt)){
				subsql = subsql + " and N_MONEY='"+payamt+"'";
			}
			if(!subsql.equals("")){
				sqlwhere = sqlwhere + " and S_BIZNO IN (SELECT S_BIZNO FROM HTV_PAYOUTMSGSUB WHERE "+subsql.replaceFirst("and", "")+" ) ";
			}
		}else if(dto instanceof TvPayreckBankDto){//��������
			if(expfunccode!=null && !"".equals(expfunccode)){
				subsql = subsql + " and S_FUNCBDGSBTCODE='"+expfunccode+"'";
			}
			if(payamt!=null && !"".equals(payamt)){
				subsql = subsql + " and F_AMT='"+payamt+"'";
			}
			if(!subsql.equals("")){
				sqlwhere = sqlwhere + " and I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM TV_PAYRECK_BANK_LIST WHERE "+subsql.replaceFirst("and", "")+" ) ";
			}
		}else if(dto instanceof HtvPayreckBankDto){
			if(expfunccode!=null && !"".equals(expfunccode)){
				subsql = subsql + " and S_FUNSUBJECTCODE='"+expfunccode+"'";
			}
			if(payamt!=null && !"".equals(payamt)){
				subsql = subsql + " and F_AMT='"+payamt+"'";
			}
			if(!subsql.equals("")){
				sqlwhere = sqlwhere + " and I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM HTV_PAYRECK_BANK_LIST WHERE "+subsql.replaceFirst("and", "")+" ) ";
			}
		}else if(dto instanceof HtvPayreckBankDto){//�˿�
			if(expfunccode!=null && !"".equals(expfunccode)){
				subsql = subsql + " and S_FUNSUBJECTCODE='"+expfunccode+"'";
			}
			if(payamt!=null && !"".equals(payamt)){
				subsql = subsql + " and F_AMT='"+payamt+"'";
			}
			if(!subsql.equals("")){
				sqlwhere = sqlwhere + " and I_VOUSRLNO IN (SELECT I_VOUSRLNO FROM TV_PAYRECK_BANK_BACK_LIST WHERE "+subsql.replaceFirst("and", "")+" ) ";
			}
		}
		
		return sqlwhere;
	}
}
