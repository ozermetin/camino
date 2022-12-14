package sphfunc;

/**
 * <dl>
 *
 * <dt>Purpose:
 *
 * <dd> General class for functions of the sphere used as linear basis
 * functions.
 * 
 * <dt>Description:
 *
 * <dd> All spherical functions inheriting from this class have
 * natural use as basis functions on the sphere, eg, spherical
 * harmonics and radial basis functions.
 *
 * This abstract class contains no functionality of its own.
 * 
 * </dl>
 * 
 * @author Danny Alexander
 *
 * @version $Id: LinearBasisFunction.java 58 2006-10-26 11:38:18Z ucacmgh $
 *  
 */
public abstract class LinearBasisFunction extends SphericalFunction {

    /**
     * Default constructor
     */
    public LinearBasisFunction() {
        super();
    }

}

