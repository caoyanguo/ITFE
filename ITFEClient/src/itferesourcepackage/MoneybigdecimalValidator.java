package itferesourcepackage;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cfcc.jaf.ui.validator.IValidator;
import com.cfcc.jaf.ui.validator.ValidationError;
/**
 * @author win7
 * @time   15-01-27 13:54:16
 */
public class MoneybigdecimalValidator implements IValidator {

	public ValidationError isPartiallyValid(Object arg0) {
		return null;
	}

	public ValidationError isValid(Object arg0) {
		String pwd = arg0.toString();
		if (null==pwd|| pwd.trim().length()<=0) {
			return null;
		}
		
		Pattern p = Pattern.compile("^[1-9]\\d*\\.?\\d*|0\\.\\d*[1-9]\\d*$");
		Matcher m = p.matcher(pwd.trim());
		boolean flag=m.matches();
		if(!flag){
			return ValidationError.error("输入不合法！");
		}else {
			return null;
		}
		
		

	}

}
