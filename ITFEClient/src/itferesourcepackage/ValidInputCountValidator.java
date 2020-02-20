package itferesourcepackage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cfcc.jaf.ui.validator.IValidator;
import com.cfcc.jaf.ui.validator.ValidationError;

/**
 * @author db2admin
 * @time 13-03-08 03:16:37
 */
public class ValidInputCountValidator implements IValidator {

	public ValidationError isPartiallyValid(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ValidationError isValid(Object arg0) {

		String regex = "\\d{8}";
		Matcher matcher;
		Pattern pattern;
		boolean falg = false;
		String tempStr = arg0.toString();
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(tempStr);
		if (!matcher.matches()) {
			return ValidationError.error("输入不合法，应该输入8位数字！");

		}
		return null;
	}

}
