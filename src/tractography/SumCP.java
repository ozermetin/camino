package tractography;

import numerics.*;


import java.util.ArrayList;

/**
 * Computes the minimum connection probability to all voxels in the image.
 *
 * @author Philip Cook
 * @version $Id: SumCP.java 58 2006-10-26 11:38:18Z ucacmgh $
 */
public class SumCP extends CP_StatMap {

    public SumCP(int xDataDim, int yDataDim, int zDataDim) {
	super(xDataDim, yDataDim, zDataDim);
    }



    public double[][][] result() {
	
	double[][][] sumImage = new double[xDataDim][yDataDim][zDataDim];

	for (int i = 0; i < xDataDim; i++) {
	    for (int j = 0; j < yDataDim; j++) {
		for (int k = 0; k < zDataDim; k++) {
		    Voxel v = new Voxel(i,j,k);
		    
		    ArrayList<Double> a = voxels.get(v);

		    double sum = 0.0;
		    
		    if (a == null) {
			sum = 0.0;
		    }
		    else {
			
			for (int n = 0; n < a.size(); n++) {
			    sum += a.get(n);
			}
			
			sumImage[i][j][k] = sum;
			
		    }
		    
		    
		}
	    }
	}
	

	return sumImage;
	
    }





}
