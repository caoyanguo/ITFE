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

		String numformat = "\\d";// 数字
		Pattern p = Pattern.compile(numformat);
		Matcher m = p.matcher(pwd);
		
		String ceilformat = "[a-zA-Z]";// 字母
		Pattern p1 = Pattern.compile(ceilformat);
		Matcher m1 = p1.matcher(pwd);
		
		String zifuformat = "[^a-zA-Z_0-9]"; // 非单词字符\W
		Pattern p2 = Pattern.compile(zifuformat);
		Matcher m2 = p2.matcher(pwd);
		
		if (null == arg0 || arg0.toString().trim().length() < 8) {
			return ValidationError.error("用户口令长度不小于8位！");
		} else if(!m.find()){
			return ValidationError.error("用户口令中不含数字！");
		}else if(!m1.find()){
			return ValidationError.error("用户口令中不含字母！");
		}else if(!m2.find()){
			return ValidationError.error("用户口令中不含字符！");
		}else {
			return null;
		}
	}
}
