package tractography;

import numerics.*;


import java.util.ArrayList;


/**
 * Computes the maximum connection probability to each voxel in the image from any seed point in an ROI.
 *
 * @author Philip Cook
 * @version $Id: MaxCP.java 58 2006-10-26 11:38:18Z ucacmgh $
 */
public class MaxCP extends CP_StatMap {

    public MaxCP(int xDataDim, int yDataDim, int zDataDim) {
	super(xDataDim, yDataDim, zDataDim);
    }



    public double[][][] result() {
	
	double[][][] maxImage = new double[xDataDim][yDataDim][zDataDim];

	for (int i = 0; i < xDataDim; i++) {
	    for (int j = 0; j < yDataDim; j++) {
		for (int k = 0; k < zDataDim; k++) {
		    Voxel v = new Voxel(i,j,k);
		    ArrayList<Double> a = voxels.get(v);
		    
		    if (a != null) {
			
			double max = 0.0;
			
			for (int n = 0; n < a.size(); n++) {
			    double d = a.get(n);
			    if (d > max) {
				max = d;
			    }
			}
			
			maxImage[i][j][k] = max;
			
		    }
		    
		    
		}
	    }
	}


	return maxImage;
	
    }





}
