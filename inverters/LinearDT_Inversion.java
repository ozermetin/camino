package inverters;

import misc.*;
import numerics.*;
import imaging.*;

/**
 * <dl>
 * 
 * <dt>Purpose:
 * 
 * <dd>Fits the diffusion tensor.
 * 
 * <dt>Description:
 * 
 * <dd>Uses the standard least squares linear approach using singular value
 * decomposition.
 * 
 * </dl>
 * 
 * @author Danny Alexander
 * @version $Id: LinearDT_Inversion.java 728 2009-07-15 21:16:01Z ucacpco $
 *  
 */
public class LinearDT_Inversion extends DT_Inversion {

    /**
     * This matrix is the linear inversion matrix. It is B^-1, where the rows of
     * B are {1, -b g_1^2, -2 b g_1 g_2, -2 b g_1 g_3, -b g_2^2, -2 b g_2 g_3,
     * -b g_3^2}.
     */
    protected RealMatrix linearInv;

    /**
     * This is the threshold on the singular values. If a singular value has
     * value less that the largest singular value divided by this value, it is
     * inverted to zero.
     */
    protected static final double SVTHRESH = 1.0E12;

    /**
     * The constructor requires the details of the sequence used to generate the
     * data that will be processed.
     * 
     * @param imParams
     *            The imaging sequence parameters.
     */
    public LinearDT_Inversion(DW_Scheme imParams) {
        ip = imParams;

        // Set up the matrix X.
        RealMatrix B = ip.getB_Matrix();

	// Now get the singular value decomposition.
        RealMatrix[] svd = null;
        try {
            svd = B.svd();
        }
        catch (Exception e) {
            throw new LoggedException(e);
        }

        // Find the maximum singular value.
        double maxSV = svd[1].entry(0, 0);
        for (int i = 0; (i < svd[1].rows() && i < svd[1].columns()); i++) {
            if (svd[1].entry(i, i) > maxSV) {
                maxSV = svd[1].entry(i, i);
            }
        }

        // Invert the singular values.
        for (int i = 0; (i < svd[1].rows() && i < svd[1].columns()); i++) {
            double curSV = svd[1].entry(i, i);
            svd[1].setEntry(i, i, (curSV > maxSV / SVTHRESH) ? (1.0 / curSV) : 0.0);
        }

        // Get the pseudo inverse.
        linearInv = svd[2].product(svd[1].transpose()).product(svd[0].transpose());
    }

    /**
     * Fits the diffusion tensor using the inverse matrix.
     * 
     * @param data
     *            The MRI data.
     * 
     * @return {exitcode, ln A^\star(0), Dxx, Dxy, Dxz, Dyy, Dyz, Dzz}
     */
    public double[] invert(double[] data) {

        // The failure free exit code is 0.
        double exitCode = 0.0;

        // Make the data matrix.
        RealMatrix A = new RealMatrix(data.length, 1);
        for (int i = 0; i < data.length; i++) {
            if (data[i] > 0) {
                A.setEntry(i, 0, Math.log(data[i]));
            }
            else {
                A.setEntry(i, 0, 0.0);

                // The exit code is 6 if the data is bad and has to
                // be changed to perform the inversion.
                exitCode = 6.0;
            }
        }

        // Do the inversion.
        RealMatrix D = linearInv.product(A);

        // Create the return array.
        double[] res = new double[8];
        res[0] = exitCode;
        for (int i = 0; i < 7; i++) {
            res[i + 1] = D.entry(i, 0);
        }

        return res;
    }

    /**
     * Returns the linear inversion matrix.
     * 
     * @return the linear inversion matrix for fitting the DT.
     */
    public RealMatrix getMatrix() {
        return (RealMatrix)linearInv.clone();
    }

}
