package inverters;

import optimizers.*;
import misc.*;

/**
 * <dl>
 * 
 * <dt>Purpose:
 * 
 * <dd>Non-linear least squares fitter of a two prolate axisymmetric tensor
 * model to DW-MR data.
 * 
 * <dt>Description:
 * 
 * <dd>This class implements the abstract methods of MarquardtChiSqFitter to
 * provide a Levenburg-Marquardt algorithm for fitting a two prolate
 * axisymmetric diffusion tensor model to DW-MR measurements.
 * 
 * </dl>
 * 
 * $Id: TwoTensorAxiSymFitter.java 728 2009-07-15 21:16:01Z ucacpco $
 * 
 * @author Danny Alexander
 *  
 */
public class TwoTensorAxiSymFitter extends TwoTensorOneAxiSymFitter {

    // The two tensor model is
    // f(x) = (1-p)e^(x^T D1 x) + p e^(x^T D2 x)
    // D1 and D2 are axisymmetric and each defined by 4 parameters,
    // Di = Fi Fi^T + Si^2 I.
    // We use p = sin^2(\theta) to constrain the mixing parameter to
    // the interval [0, 1].
    // The optimized parameters are enumerated as follows:
    // a1 = F1x, a2 = F1y, a3 = F1z, a4 = S1
    // a5 = F2x, a6 = F2y, a7 = F2z, a8 = S2
    // a9 = \theta.

    //Number of parameters in the full axisymmetric two tensor model.
    private int AXISYMNOPARAMS = 9;

    /**
     * Default constructor does nothing.
     */
    public TwoTensorAxiSymFitter() {
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
    public TwoTensorAxiSymFitter(double[][] indepVals, double[] depVals,
            double[] bValues, int nob0s) throws MarquardtMinimiserException {
        noParams = 9;
        dt1StartIndex = 1;
        dt2StartIndex = 5;
        mixParIndex = 9;
        initialize(indepVals, depVals, bValues, nob0s);
    }

    /**
     * Overridden as there is no need to compute the axisymmetric diffusion
     * tensor.
     */
    protected double[] initParams2() {
        double traceD2 = 2.4E-9;
        double[] params = new double[4];
        for (int i = 0; i < 3; i++) {
            params[i] = 0.0;
        }
        params[3] = Math.sqrt(traceD2 / 3.0);

        return params;
    }

    /**
     * Overridden as there is no need to compute the axisymmetric diffusion
     * tensor.
     */
    protected double[] fAndBetaToParams2(double[] fBeta) {
        double[] params = new double[4];

        params[0] = fBeta[0];
        params[1] = fBeta[1];
        params[2] = fBeta[2];
        params[3] = Math.sqrt(fBeta[3]);

        return params;
    }

    /**
     * Overridden to replace the first DT by the axisymmetric representation.
     */
    protected void insertTensorParamDerivs(double[] derivs, double[] atry, double[] g,
            double d1Contrib, double d2Contrib, double p, double b) {


        double s1 = atry[dt1StartIndex + 3];
        double s2 = atry[dt2StartIndex + 3];

        double gDotF1 = g[0] * atry[dt1StartIndex] + g[1] * atry[dt1StartIndex + 1]
                + g[2] * atry[dt1StartIndex + 2];
        double gDotF2 = g[0] * atry[dt2StartIndex] + g[1] * atry[dt2StartIndex + 1]
                + g[2] * atry[dt2StartIndex + 2];

        insertAxiSymDerivs(derivs, b, dt1StartIndex, g, s1, gDotF1, p,
                d1Contrib);
        insertAxiSymDerivs(derivs, b, dt2StartIndex, g, s2, gDotF2, 1 - p,
                d2Contrib);

    }

    /**
     * Overridden to use the axisymmetric representation.
     */
    protected DT getDT2(double[] a) {
        return getDT_AxiSym(a, dt2StartIndex);
    }

    /**
     * Overridden to use the axisymmetric representation.
     */
    protected double[] dt2ToParams(DT dt2) {
        return getAxiSymParams(dt2);
    }

}
