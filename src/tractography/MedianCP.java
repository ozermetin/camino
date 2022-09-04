package tractography;

import numerics.*;
import tools.*;


import java.util.ArrayList;

/**
 * Computes the median connection probability to all voxels in the image.
 *
 * @author Philip Cook
 * @version $Id: MedianCP.java 58 2006-10-26 11:38:18Z ucacmgh $
 */
public class MedianCP extends CP_StatMap {

    public MedianCP(int xDataDim, int yDataDim, int zDataDim) {
	super(xDataDim, yDataDim, zDataDim);
    }



    public double[][][] result() {

	double[][][] medianImage = new double[xDataDim][yDataDim][zDataDim];

	for (int i = 0; i < xDataDim; i++) {
	    for (int j = 0; j < yDataDim; j++) {
		for (int k = 0; k < zDataDim; k++) {
		    Voxel v = new Voxel(i,j,k);
		    
		    ArrayList<Double> a = voxels.get(v);
		    
		    if (a != null) {
			
			double[] values = new double[numberOfImages];
			
			for (int n = 0; n < a.size(); n++) {
			    double d = a.get(n);
			    values[n] = d;
			}
			
			medianImage[i][j][k] = ArrayOps.median(values);
		    }
		    
		    
		}
	    }
	}


	return medianImage;
	
    }





}
