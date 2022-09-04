package tractography;

import numerics.*;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Superclass for memory-efficient storage of multiple scalar images. Only voxels with nonzero
 * values are stored for statistical computation. 
 *
 * @author Philip Cook
 * @version $Id: CP_StatMap.java 58 2006-10-26 11:38:18Z ucacmgh $
 */
public abstract class CP_StatMap {


    // number of images over which this statistic is computed
    protected int numberOfImages; 


    protected final int xDataDim;
    protected final int yDataDim;
    protected final int zDataDim;


    // contains all voxels with at least one nonzero value
    protected final Hashtable<Object,ArrayList<Double>> voxels;

    public CP_StatMap(int xDataDim, int yDataDim, int zDataDim) {
	this.xDataDim = xDataDim;
	this.yDataDim = yDataDim;
	this.zDataDim = zDataDim;

	numberOfImages = 0;

	voxels = new Hashtable<Object,ArrayList<Double>>();
    }



    /**
     * Adds an image. Non-zero values are stored; zero values are discarded.
     *
     */
    public void addImage(double[][][] image) {

	numberOfImages++;
	
	for (int i = 0; i < xDataDim; i++) {
	    for (int j = 0; j < yDataDim; j++) {
		for (int k = 0; k < zDataDim; k++) {

		    if (image[i][j][k] > 0.0) {

                        Voxel v = new Voxel(i,j,k);

                        ArrayList<Double> a = voxels.get(v);
                        
                        if (a != null) {
                            a.add(new Double(image[i][j][k]));
                        }
                        
                        else {
                            a = new ArrayList<Double>();
                            a.add(new Double(image[i][j][k]));
                            voxels.put(v, a);
                        }

                    }
		}
	    }
	}
	
    }


    /**
     * Do the statistical computation and return the result.
     */
    public abstract double[][][] result();





}
