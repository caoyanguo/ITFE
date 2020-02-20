package itferesourcepackage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cfcc.jaf.ui.validator.IValidator;
import com.cfcc.jaf.ui.validator.ValidationError;

/** 
 * 功能：日期格式校验
 * @author hejianrong
 * @time   14-06-04 14:56:53
 */
public class DateValidator implements IValidator {

	public ValidationError isPartiallyValid(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public ValidationError isValid(Object arg0) {
		String date=arg0+"";
		if(date==null||date.trim().length()==0)
			return null;
		String regex = "\\d{8}";
		Matcher matcher;
		Pattern pattern;
		boolean falg = false;
		pattern = Pattern.compile(regex);
		matcher = pattern.matcher(date);
		if (!matcher.matches()) {
			return ValidationError.error("输入不合法，应该输入8位数字！");
		}return null;
	}

}