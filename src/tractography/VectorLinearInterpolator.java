package tractography;

import numerics.*;

/**
 * Provides interpolated measurements of the principal direction
 * at any point within the dataset.
 *
 *
 * @version $Id: VectorLinearInterpolator.java 1160 2012-12-12 17:43:16Z ucacpco $
 * @author Philip Cook
 * 
 */
public final class VectorLinearInterpolator extends EightNeighbourLinearInterpolator implements ImageInterpolator {


    private TractographyImage image;

    
    /** Construct an interpolator.
     * @param data the dataset to use for interpolation.
     */
    public VectorLinearInterpolator(TractographyImage data) {
	
	super( data.xDataDim(), data.yDataDim(), data.zDataDim(), 
	       data.xVoxelDim(), data.yVoxelDim(), data.zVoxelDim()
	       );

	image = data;

    }
    
    /**
     * Chooses which vector to use for interpolation in this voxel, given the previous
     * direction. Will negate the chosen vector if necessary, so the dot product of the
     * previous direction and the returned vector is always non-negative.
     *
     */
    private Vector3D chooseVector(int i, int j, int k, Vector3D previousDir) {
	
	Vector3D[] vecs = image.getPDs(i,j,k, previousDir);

	if (vecs.length == 0) {
	    return null;
	}
        if (vecs.length == 1) {
	    if (vecs[0].dot(previousDir) > 0.0) {
		return vecs[0];
	    }
	    else {
		return vecs[0].negated();
	    }
	}
	
	int choice = 0;
	double maxDot = 0.0;
	
	for (int d = 0; d < vecs.length; d++) {
	    double dot = Math.abs(vecs[d].dot(previousDir));
	    if (dot > maxDot) {
		maxDot = dot;
		choice = d;
	    }
	}

	if (vecs[choice].dot(previousDir) > 0.0) {
	    return vecs[choice];
	}
	else {
	    return vecs[choice].negated();
	}
    }


    /**
     * Returns null if there are no vectors in this voxel
     */
    protected double[] getVoxelDataAsArray(int i, int j, int k, Vector3D previousDir) {
        Vector3D v = chooseVector(i,j,k, previousDir);

        return v != null ? new double[] {v.x, v.y, v.z} : null;
    } 

  
    public Vector3D getTrackingDirection(Point3D point, Vector3D previousDir) {

        double[] interpolated = interpolateVectorElements(point, previousDir);
	
	return new Vector3D(interpolated).normalized();

    }


    /** 
     * Get the initial tracking direction, given a pdIndex and a seed point.
     * 
     * @param direction if true, the direction will be the PD, if false, it will be the negated PD.
     * @return the tracking direction for this point. 
     * 
     */
    public Vector3D getTrackingDirection(Point3D point, int pdIndex, boolean direction) {

	int i = (int)( (point.x / xVoxelDim) );
	int j = (int)( (point.y / yVoxelDim) );
	int k = (int)( (point.z / zVoxelDim) );

	Vector3D[] pds = image.getPDs(i,j,k);

	if (direction) {
	    return getTrackingDirection(point, pds[pdIndex]);
	}
	else {
	    return getTrackingDirection(point, pds[pdIndex].negated());
	}

    }

    
    public TractographyImage getImage() {
        return image;
    }


}
