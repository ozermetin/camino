package optimizers;

import models.*;
import numerics.*;
import misc.*;
import imaging.*;
import fitters.*;

/**
 * <dl>
 * 
 * <dt>Purpose:
 * 
 * <dd>Runs the LM_Gaussian minimizer multiple times from different starting
 * points and concatenates the results.
 * 
 * </dl>
 *
 * @author  Danny
 * @version $Id$
 *
 */
public class MultiRunLM_GaussianMinimizer extends MultiRunLM_Minimizer {


    public MultiRunLM_GaussianMinimizer(DW_Scheme scheme, ParametricModel pm, Codec cod, Perturbation p, int repeats, int seed) throws MinimizerException {
        super(scheme, pm, cod, p, repeats, seed);
    }


    protected void makeMinimizer(DW_Scheme scheme, ParametricModel pm, Codec cod) throws MarquardtMinimiserException {

        minimizer = new LM_GaussianMinimizer(scheme, pm, cod);

    }

}



