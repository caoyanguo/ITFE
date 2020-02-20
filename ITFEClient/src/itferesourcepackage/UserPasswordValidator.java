package itferesourcepackage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cfcc.devplatform.utils.validator.PattenValidator;
import com.cfcc.jaf.ui.validator.IValidator;
import com.cfcc.jaf.ui.validator.ValidationError;

/**
 * @author db2admin
 * @time 09-11-23 13:31:44
 */
public class UserPasswordValidator implements IValidator {

	public ValidationError isPartiallyValid(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ValidationError isValid(Object arg0) {

		String pwd = arg0.toString();

		String numformat = "\\d";// ����
		Pattern p = Pattern.compile(numformat);
		Matcher m = p.matcher(pwd);
		
		String ceilformat = "[a-zA-Z]";// ��ĸ
		Pattern p1 = Pattern.compile(ceilformat);
		Matcher m1 = p1.matcher(pwd);
		
		String zifuformat = "[^a-zA-Z_0-9]"; // �ǵ����ַ�\W
		Pattern p2 = Pattern.compile(zifuformat);
		Matcher m2 = p2.matcher(pwd);
		
		if (null == arg0 || arg0.toString().trim().length() < 8) {
			return ValidationError.error("�û�����Ȳ�С��8λ��");
		} else if(!m.find()){
			return ValidationError.error("�û������в������֣�");
		}else if(!m1.find()){
			return ValidationError.error("�û������в�����ĸ��");
		}else if(!m2.find()){
			return ValidationError.error("�û������в����ַ���");
		}else {
			return null;
		}
	}
}
