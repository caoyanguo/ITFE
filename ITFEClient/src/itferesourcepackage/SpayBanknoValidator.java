package itferesourcepackage;

import com.cfcc.jaf.ui.validator.IValidator;
import com.cfcc.jaf.ui.validator.ValidationError;

/**
 * @author db2admin
 * @time   09-11-23 10:32:37
 */
public class SpayBanknoValidator implements IValidator {

	public ValidationError isPartiallyValid(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ValidationError isValid(Object arg0) {
		if(null==arg0||arg0.toString().trim().length()<12){
			return ValidationError.error("清算行行号为12位");
			
		}else{
			return null;
		}
	}

}
