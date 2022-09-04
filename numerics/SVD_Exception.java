package numerics;

/**
 * <dl>
 * 
 * <dt>Purpose:
 * 
 * <dd>Exception class used in the SVD method of RealMatrix.
 * 
 * <dt>Description:
 * 
 * <dd>Standard extension of the Exception class.
 * 
 * </dl>
 * 
 * @version $Id: SVD_Exception.java 379 2007-09-19 22:33:06Z ucacpco $
 * @author Danny Alexander
 *  
 */
public class SVD_Exception extends Exception {

    SVD_Exception() {
        super();
    }

    public SVD_Exception(String s) {
        super(s);
    }
}

