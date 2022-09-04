package inverters;

import data.*;
import imaging.*;
import misc.*;
import numerics.*;
import tools.CL_Initializer;

import java.io.*;

/**
 *
 * Uses a weighted-linear least-squares algorithm to fit the diffusion tensor. Initializes the 
 * solution to the ordinary least squares estimate, then iteratively solves for the weighted solution.
 * 
 * @author Philip Cook
 * @version $Id$
 * @see inverters.LinearDT_Inversion
 */
public class WeightedLinearDT_Inversion extends DT_Inversion {

    /**
     * This is the threshold on the singular values. If a singular value has
     * value less that the largest singular value divided by this value, it is
     * inverted to zero.
     */
    protected static final double SVTHRESH = 1.0E12;

    private final LinearDT_Inversion olsInv;

    // number of measurements per voxel
    private final int numMeas;

    // b-matrix
    private final RealMatrix X;
    private final RealMatrix XT;

    // optionally write noise variance to this output stream
    private DataOutputStream noiseMap = null;
    private DataOutputStream residualVectorMap = null;

    private static final int MAX_ITERATIONS = 10;

 
    /**
     * The constructor requires the details of the sequence used to generate the
     * data that will be processed.
     * 
     * @param imParams
     *            The imaging sequence parameters.
     */
    public WeightedLinearDT_Inversion(DW_Scheme imParams) {
        ip = imParams;

	numMeas = ip.numMeasurements();

        // Set up the matrix X, which is the b-matrix

        X = ip.getB_Matrix();

	XT = X.transpose();

	olsInv = new LinearDT_Inversion(imParams);

        // Initialize the outlier map output stream.
        if (CL_Initializer.noiseVarianceMapFile != null) {
	    try {
		FileOutputStream fout = new FileOutputStream(CL_Initializer.noiseVarianceMapFile);

		noiseMap = 
		    new DataOutputStream(new BufferedOutputStream(fout, OutputManager.FILEBUFFERSIZE));
	    }
	    catch (java.io.IOException e) {
		throw new LoggedException(e);
	    }
	}

        if (CL_Initializer.residualVectorMapFile != null) {
	    try {
		FileOutputStream fout = new FileOutputStream(CL_Initializer.residualVectorMapFile);

		residualVectorMap = 
		    new DataOutputStream(new BufferedOutputStream(fout, OutputManager.FILEBUFFERSIZE));
	    }
	    catch (java.io.IOException e) {
		throw new LoggedException(e);
	    }
	}

    }



    /**
     *
     * Fits the diffusion tensor.
     * <p> 
     * If any of the data is bad (log of the data cannot be computed), the inversion attempts to compensate by 
     * giving bad measurements zero weight. If this fails, the unweighted linear fit is returned.  
     *
     * @param data
     *            The MRI data.
     * 
     * @return {exitcode, ln A^\star(0), Dxx, Dxy, Dxz, Dyy, Dyz, Dzz}
     *
     * 
     */
    public double[] invert(double[] data) {

	double[] olsSolution = olsInv.invert(data);

	if (Double.isNaN(olsSolution[0]) || Double.isInfinite(olsSolution[0])) {
	    // could not get linear fit
	    double[] badData = new double[8];
            badData[0] = -100.0;
            return badData;
	}

	RealMatrix B = null;

	RealMatrix[] weightedFit = null;

	// Always have to output these, even if weighted fit fails
	// If fitting fails, we output zeros for these values
	double sigmaSq = 0.0;

	double[] residuals = new double[numMeas];
	
	try {
	    weightedFit = computeWeightedFit(data, olsSolution);
	    B = weightedFit[3];

	    if (noiseMap != null) {
		sigmaSq = computeNoiseVariance(weightedFit);
	    }
	    if (residualVectorMap != null) {
		residuals = computeResidualVector(weightedFit);
	    }

	}
	catch(SVD_Exception e) {

	    // can't do the SVD for some reason, return OLS fit
	    
	    B = new RealMatrix(7,1);

	    for (int i = 0; i < 7; i++) {
		B.entries[i][0] = olsSolution[i+1];
	    }

	}
	
	
	if (noiseMap != null) {
	    try { 
		
		noiseMap.writeDouble(sigmaSq);
	    }
	    catch (IOException e) {
		throw new LoggedException(e);
	    }
	}

	if (residualVectorMap != null) {
	    try { 
		for (int i = 0; i < numMeas; i++) {
		    residualVectorMap.writeDouble(residuals[i]);
		}
	    }
	    catch (IOException e) {
		throw new LoggedException(e);
	    }
	}
	
	
	double[] result = new double[8];
	
	result[0] = olsSolution[0];
	
	for (int i = 0; i < 7; i++) {
	    result[i+1] = B.entries[i][0];
	}
	
	return result;

    } 


    /**
     * Closes the noise map output stream, if open.
     */
    public void close() {

	if (noiseMap != null) {
	    try { 
		noiseMap.close();
	    }
	    catch (IOException e) {
		throw new LoggedException(e);
	    }
	}
	if (residualVectorMap != null) {
	    try { 
		residualVectorMap.close();
	    }
	    catch (IOException e) {
		throw new LoggedException(e);
	    }
	}

    }


    /**
     * In background voxels this inverter needs to output zero if there is a noise map.
     */
    public void background() {
        if (noiseMap != null) {

	    try { 
		noiseMap.writeDouble(0.0);  
	    }
	    catch (IOException e) {
		throw new LoggedException(e);
	    }

        }
	if (residualVectorMap != null) {
	    
	    try { 
		for (int i = 0; i < numMeas; i++) {
		    residualVectorMap.writeDouble(0.0);
		}
	    }
	    catch (IOException e) {
		throw new LoggedException(e);
	    }
	    
        }
    }


    /**
     * 
     * @param weightedSolution array [Y, W, XB]. The array [Y, W, XB, B], returned from the
     * computeWeightedFit method, may also be used as the parameter (but B is not required). 
     * The first three elements are used to compute the weighted residuals, where
     * Y = log of the DWI measurements, W = diag[exp(XB(i))], ie diagonal matrix containing 
     * predicted DWI measurements, and XB = product of design matrix with parameter matrix.
     *
     * @return WY - WXB
     *
     */
    public double[] computeResidualVector(RealMatrix[] weightedSolution) {
	RealMatrix Y = weightedSolution[0];
	RealMatrix W = weightedSolution[1];
	RealMatrix XB = weightedSolution[2];
	
	RealMatrix E = W.product(Y).sub(W.product(XB));
	
	double[] result = new double[numMeas];

	for (int i = 0; i < numMeas; i++) {
	    result[i] = E.entries[i][0];
	}

	return result;
    }
    

    /**
     * Computes the noise variance \sigma^2 = (Y - XB)^T W^2 (Y - XB) / (DOF), where 
     * DOF is the number of degrees of freedom in the model. This is  
     * (number of measurements used to fit model) - 7. Bad data is excluded during the fitting 
     * process, so the DOF is reduced accordingly.
     *
     * @param weightedSolution array [Y, W, XB]. The array [Y, W, XB, B], returned from the
     * computeWeightedFit method, may also be used as the parameter (but B is not required). 
     * The first three elements are used to compute the noise variance, where
     * Y = log of the DWI measurements, W = diag[exp(XB(i))], ie diagonal matrix containing 
     * predicted DWI measurements, and XB = product of design matrix with parameter matrix.
     * 
     *
     * @return \sigma^2. Assumes that the residuals have zero mean and equal variance. 
     *
     */
    public double computeNoiseVariance(RealMatrix[] weightedSolution) {

	RealMatrix Y = weightedSolution[0];
	RealMatrix W = weightedSolution[1];
	RealMatrix XB = weightedSolution[2];

	RealMatrix WSQ = new RealMatrix(numMeas, numMeas);

	int numExcludedMeas = 0;

	for (int i = 0; i < numMeas; i++) {
	    
	    WSQ.entries[i][i] = W.entries[i][i] * W.entries[i][i];

	    if (WSQ.entries[i][i] == 0.0) {
		numExcludedMeas++;
	    }
	    
	}

	RealMatrix sumResiduals = (Y.sub(XB).transpose()).product(WSQ).product(Y.sub(XB));


	// degrees of freedom in data is numMeas - 7 - numExcludedMeas
	// ie number of measurements actually used in the calculation of B, minus the number of
	// parameters in B

	double sigmaSq = sumResiduals.entries[0][0] / (numMeas - 7 - numExcludedMeas);
	
	return sigmaSq;

    }


    /**
     * Solves W*Y = W*X*B for B. Y = log(data), W = weights, 
     * B = [ln A^\star(0), Dxx, Dxy, Dxz, Dyy, Dyz, Dzz]^T.
     * 
     * The fit is initialized with the results of a linear inversion.
     * 
     *
     * @return {Y, W, XB, B}.
     * 
     */
    public RealMatrix[] computeWeightedFit(double[] data) throws numerics.SVD_Exception {
	return computeWeightedFit(data, olsInv.invert(data));
    }


    /**
     * Solves W*Y = W*X*B for B. Y = log(data), W = weights, 
     * B = [ln A^\star(0), Dxx, Dxy, Dxz, Dyy, Dyz, Dzz]^T.
     * 
     * @param olsSolution the array returned by LinearDT_Inversion.invert(data).  This solution is used to 
     * initialize B, and hence W: W(i,i) = exp(XB(i)) for each measurement i.
     * 
     *
     * @return {Y, W, XB, B}.
     * 
     */
    public RealMatrix[] computeWeightedFit(double[] data, double[] olsSolution) throws numerics.SVD_Exception {

	double[] logData = new double[numMeas];

	RealMatrix Y = new RealMatrix(numMeas, 1);

	// B = [ln[S_0], dxx, dxy, dxz, dyy, dyz, dzz]^T
	// initialize from result of OLS solution
	RealMatrix B = new RealMatrix(7,1);

	// weights
	RealMatrix W = new RealMatrix(numMeas, numMeas);


	for (int i = 0; i < 7; i++) {
	    B.entries[i][0] = olsSolution[i+1];
	}	
	
	RealMatrix XB = X.product(B);

	double sumResidualSq = 0.0;

	boolean[] badData = new boolean[numMeas];

	int numExcludedMeas = 0;

	// if exit code from linear fit is zero, there are no problems with bad data
	if (olsSolution[0] == 0.0) {

	    for (int i = 0; i < numMeas; i++) {

		logData[i] = Math.log(data[i]);
		
		Y.entries[i][0] = logData[i];
		
		W.entries[i][i] = Math.exp(XB.entries[i][0]);
		
		sumResidualSq += (logData[i] - XB.entries[i][0]) * (logData[i] - XB.entries[i][0]); 
	    }

	}
	else {

	    // initialize weights to binary: weight of 1.0 for good data, 0.0 for bad

	    for (int i = 0; i < numMeas; i++) {
		
		badData[i] = !(data[i] > 0.0);

		if (badData[i]) {
		    numExcludedMeas++;
		    logData[i] = 0.0;
		}
		else {
		    logData[i] = Math.log(data[i]);
		    sumResidualSq += (logData[i] - XB.entries[i][0]) * (logData[i] - XB.entries[i][0]); 
		}

		Y.entries[i][0] = logData[i];
		W.entries[i][i] = badData[i] ? 0.0 : Math.exp(XB.entries[i][0]);
		
	    }

	}

	if (numMeas - numExcludedMeas < 8) {

	    // not enough data to estimate DT + S_0 + \sigma
	    throw new SVD_Exception("Insufficient data to invert WX");
	}

	
	int iter = 0;
	
	// difference in residuals from last iteration, | \epsilon_{i+1}^2 - \epsilon_i^2 | / \epsilon_{i}^2
	double relDiffResidual = Double.MAX_VALUE;

	// quit iterating if change in residuals is less than this
	double convergeThresh = 1E-2;


	while (relDiffResidual > convergeThresh && iter < MAX_ITERATIONS) {

	    RealMatrix inv = null;
	    
	    RealMatrix[] svd = null;
	    
	    svd = W.product(X).svd();
	    
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
	    inv = svd[2].product(svd[1].transpose()).product(svd[0].transpose());
	    
	    B = inv.product(W.product(Y));
	    
	    double nextSumResidualSq = 0.0;	    
	    
	    XB = X.product(B);
	    
	    for (int i = 0; i < numMeas; i++) {
		// continue to set bad data to zero weight, but weight good measurements
		// to the model parameters
		if (!badData[i]) {
		    nextSumResidualSq += (logData[i] - XB.entries[i][0]) * (logData[i] - XB.entries[i][0]); 
		}

		W.entries[i][i] = badData[i] ? 0.0 : Math.exp(XB.entries[i][0]);

	    }
	    
	    if (sumResidualSq > 0.0) {
		relDiffResidual = Math.abs(nextSumResidualSq - sumResidualSq) / sumResidualSq;
	    }
	    else {
		relDiffResidual = 0.0;
	    }

	    sumResidualSq = nextSumResidualSq;
	    
	    iter++;
	}


	return new RealMatrix[] {Y, W, XB, B};

    }

}
