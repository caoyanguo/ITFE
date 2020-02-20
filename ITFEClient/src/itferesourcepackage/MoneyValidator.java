package itferesourcepackage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cfcc.jaf.ui.validator.IValidator;
import com.cfcc.jaf.ui.validator.ValidationError;

/**
 * @author Administrator
 * @time   13-11-08 15:36:59
 */
public class MoneyValidator implements IValidator {

	public ValidationError isPartiallyValid(Object arg0) {
		return null;
	}

	public ValidationError isValid(Object arg0) {
		if (null == arg0 || arg0.toString().trim().length() > 26) {
			return ValidationError.error("金额整数部分不能超过18位数字！");
		} else {
			return null;
		}
	}

}
