package tractography;

import numerics.*;

import java.util.ArrayList;



/**
 * Computes the mean connection probability to all voxels in the image.
 *
 * @author Philip Cook
 * @version $Id: MeanCP.java 58 2006-10-26 11:38:18Z ucacmgh $
 */
public class MeanCP extends CP_StatMap {

    public MeanCP(int xDataDim, int yDataDim, int zDataDim) {
	super(xDataDim, yDataDim, zDataDim);
    }



    public double[][][] result() {
	
	double nImages = (double)numberOfImages;

	double[][][] meanImage = new double[xDataDim][yDataDim][zDataDim];

	for (int i = 0; i < xDataDim; i++) {
	    for (int j = 0; j < yDataDim; j++) {
		for (int k = 0; k < zDataDim; k++) {
		    Voxel v = new Voxel(i,j,k);
		    
		    ArrayList<Double> a = voxels.get(v);
		    		    
		    if (a != null) {
			
			for (int n = 0; n < a.size(); n++) {
			    double d = a.get(n);
			    meanImage[i][j][k] += d / nImages;
			}
						
		    }
		    
		    
		}
	    }
	}


	return meanImage;
	
    }





}
