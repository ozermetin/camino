package inverters;

import optimizers.*;

/**
 * <dl>
 * 
 * <dt>Purpose:
 * 
 * <dd>Non-linear least squares fitter of a two tensor model to DW-MR data with
 * fixed mixing parameter.
 * 
 * <dt>Description:
 * 
 * <dd>This class implements the abstract methods of MarquardtChiSqFitter to
 * provide a Levenburg-Marquardt algorithm for fitting a two diffusion tensor
 * model to DW-MR measurements with fixed mixing parameter.
 * 
 * </dl>
 * 
 * $Id: TwoTensorCholFixMP_Fitter.java 728 2009-07-15 21:16:01Z ucacpco $
 * 
 * @author Danny Alexander
 *  
 */
public class TwoTensorCholFixMP_Fitter extends TwoTensorCholFitter {

    /**
     * Default constructor does nothing.
     */
    public TwoTensorCholFixMP_Fitter() {
    }

    /**
     * The constructor requires a list of independent values (indepVals) and
     * associated dependent values (depVals) - these are the data. Together with
     * the number of unweighted acquisitions that are made (nob0s), which is
     * required to estimate the noise levels of each data item. The diffusion
     * time used in the imaging sequence is also required.
     * 
     * @param indepVals
     *            The matrix of gradient directions g without the zero entries.
     * 
     * @param depVals
     *            The normalized measurements.
     * 
     * @param bValues
     *            The array of non-zero b-values.
     * 
     * @param nob0s
     *            The number of b=0 measurements.
     */
    public TwoTensorCholFixMP_Fitter(double[][] indepVals, double[] depVals,
            double[] bValues, int nob0s) throws MarquardtMinimiserException {
        noParams = 12;
        dt1StartIndex = 1;
        dt2StartIndex = 7;
        mixParIndex = 13;
        initialize(indepVals, depVals, bValues, nob0s);
    }

    /**
     * Overridden to do nothing as mixing parameter is fixed.
     */
    protected void insertMixPar(double[] startPoint, double mix) {
    }

    /**
     * Overridden to do nothing as mixing parameter is fixed.
     */
    protected void insertOtherDerivs(double[] derivs, double[] atry, double d1Contrib,
            double d2Contrib, double p) {

    }

    /**
     * Computes the mixing parameter from the array of parameters.
     */
    protected double getP(double[] a) {

        //The mixing parameter is fixed at 0.5.
        return 0.5;
    }

}
