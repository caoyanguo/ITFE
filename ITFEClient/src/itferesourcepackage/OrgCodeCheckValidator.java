package itferesourcepackage;

import com.cfcc.jaf.ui.validator.IValidator;
import com.cfcc.jaf.ui.validator.ValidationError;

/**
 * @author db2admin
 * @time   09-11-23 10:15:25
 */
public class OrgCodeCheckValidator implements IValidator {

	public ValidationError isPartiallyValid(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ValidationError isValid(Object arg0) {
		if (null == arg0||arg0.toString().trim().length()<12) {
			return ValidationError.error("核算主体代码长度为12位");
		}
		return null;
	}

}
