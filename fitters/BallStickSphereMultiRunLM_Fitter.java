package fitters;

import models.*;
import models.compartments.CompartmentModel;
import optimizers.*;
import inverters.*;
import numerics.*;
import misc.*;
import data.*;
import tools.*;
import imaging.*;

/**
 * <dl>
 * 
 * <dt>Purpose:
 * 
 * <dd>Fits the ballsticksphere model using multiple runs of a Levenburg
 * Marquardt.
 * 
 * <dt>Description:
 * 
 * <dd> Fitting is as in BallStickSphereLM_GaussianFitter.  The
 * perturbations are 10% of the initial starting value.
 * 
 * </dl>
 *
 * @author  laura(panagio@cs.ucl.ac.uk)
 * @version $Id$
 *
 */
public class BallStickSphereMultiRunLM_Fitter extends
		BallStickSphereLM_Fitter {

	/**
	 * Constructor implements the mapping between model and optimized
	 * parameters in the Codec object.
	 *
	 * @param scheme The imaging protocol.
	 */
	public BallStickSphereMultiRunLM_Fitter(DW_Scheme scheme,
			int repeats, int seed) {

		this.scheme = scheme;
		makeCodec();
		String[] compNames = new String[3];
		compNames[0] = new String("stick");
		compNames[1] = new String("ball");
		compNames[2] = new String("spheregpd");
		double[] initialParams = new double[10];
		initialParams[0] = 1.0; //the S0
		initialParams[1] = 0.6; //volume fraction of the first compartment
		initialParams[2] = 0.2; //volume fraction of the second compartment
		initialParams[3] = 0.1; //volume fraction of the third compartment
		initialParams[4] = 1.7E-9;//stick diffusivity
		initialParams[5] = 1.570796326794897;//theta
		initialParams[6] = 0.0;//phi
		initialParams[7] = 1.7E-9;//ball diffusivity
		initialParams[8] = 1.7E-9;//astro diffusivity
		initialParams[9] = 1E-9;//R

		 cm = new CompartmentModel(compNames, initialParams);
		 try {
	            initMultiRunLM_Minimizer(NoiseModel.getNoiseModel(CL_Initializer.noiseModel), new FixedSTD_GaussianPerturbation(), repeats, seed);
		    ((MultiRunLM_Minimizer) minimizer).setCONVERGETHRESH(1e-8);
		    ((MultiRunLM_Minimizer) minimizer).setMAXITER(5000);
	        } catch(Exception e) {
	            throw new LoggedException(e);
	        }

		
		bsfitter = new BallStickMultiRunLM_GaussianFitter(scheme, 3, 0);
		bsafitter = new BallStickAstrosticksMultiRunLM_Fitter(scheme, 3, 0);

	}



	public static void main(String[] args) {

		CL_Initializer.CL_init(args);
		CL_Initializer.checkParsing(args);
		CL_Initializer.initImagingScheme();
		CL_Initializer.initDataSynthesizer();

		OutputManager om = new OutputManager();

		BallStickSphereMultiRunLM_Fitter inv = new BallStickSphereMultiRunLM_Fitter(
				CL_Initializer.imPars, 100, 0);

		// Loop over the voxels.
		while (CL_Initializer.data.more()) {
			try {

				double[] nextVoxel = CL_Initializer.data.nextVoxel();
				double[][] fit = inv.fit(nextVoxel);

				for (int i = 0; i < fit.length; i++)
					om.output(fit[i]);
			} catch (Exception e) {
				System.err.println(e);
			}
		}

		om.close();
	}

}
