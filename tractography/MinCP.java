package tractography;

import numerics.*;


import java.util.ArrayList;

/**
 * Computes the minimum connection probability to all voxels in the image.
 *
 * @author Philip Cook
 * @version $Id: MinCP.java 58 2006-10-26 11:38:18Z ucacmgh $
 */
public class MinCP extends CP_StatMap {

    public MinCP(int xDataDim, int yDataDim, int zDataDim) {
	super(xDataDim, yDataDim, zDataDim);
    }



    public double[][][] result() {
	
	double[][][] minImage = new double[xDataDim][yDataDim][zDataDim];

	for (int i = 0; i < xDataDim; i++) {
	    for (int j = 0; j < yDataDim; j++) {
		for (int k = 0; k < zDataDim; k++) {
		    Voxel v = new Voxel(i,j,k);
		    
		    ArrayList<Double> a = voxels.get(v);
		    
		    double min = Double.MAX_VALUE;

		    if (a == null) {
			min = 0.0;
		    }

		    if (a != null) {
			
			if (a.size() < numberOfImages) {
			    min = 0.0;
			}
			else {
			    for (int n = 0; n < a.size(); n++) {
				double d = a.get(n);
				if (d < min) {
				    min = d;
				}
			    }
			}
			
			minImage[i][j][k] = min;
			
		    }
		    
		    
		}
	    }
	}


	return minImage;
	
    }





}
