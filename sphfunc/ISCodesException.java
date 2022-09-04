package sphfunc;

/**
 * <dl>
 * <dt>Purpose: Exception class used in ISCodes database.
 * <dd>
 * 
 * <dt>Description:
 * <dd>Exception class used in ISCodes database.
 * 
 * </dl>
 * 
 * $Id: ISCodesException.java 430 2007-11-14 01:45:08Z ucacpco $
 * 
 * @author Danny Alexander
 *  
 */
public class ISCodesException extends Exception {
    
    ISCodesException() {
        super();
    }

    ISCodesException(String s) {
        super(s);
    }
}

