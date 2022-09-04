package inverters;

import imaging.*;
import java.util.logging.Logger;
import misc.LoggedException;

/**
 * <dl>
 * 
 * <dt>Purpose:
 * 
 * <dd>General abstract class for inversion techniques for diffusion MRI data.
 * 
 * <dt>Description:
 * 
 * <dd>Defines the essential methods and instance data.
 * 
 * </dl>
 * 
 * @author Danny Alexander
 * @version $Id: DiffusionInversion.java 728 2009-07-15 21:16:01Z ucacpco $
 *  
 */
public abstract class DiffusionInversion {

    /**
     * Logging object
     */
    protected static Logger logger = Logger.getLogger("camino.inverters.DiffusionInversion");

    /**
     * Parameters of the imaging sequence that gave rise to the data.
     */
    protected DW_Scheme ip;

    /**
     * Does the inversion.
     * 
     * @param data The MRI data.
     * 
     * @return Fitted parameters of the inversion.
     */
    public abstract double[] invert(double[] data);

    /**
     * Specifies the number of elements of the output array from the inversion.
     * 
     * @return The number of elements of the output array.
     */
    public abstract int itemsPerVoxel();

    /**
     * Operations to perform in background voxels. The default does nothing.
     */
    public void background() {
    }

    /**
     * Perform final closing operations on the inverter. The default method
     * outputs nothing.
     */
    public void close() {
    }

}
