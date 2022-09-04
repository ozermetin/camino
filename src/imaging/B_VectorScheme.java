package imaging;

import misc.*;

import java.text.*;
import java.util.logging.Logger;
import java.util.Scanner;
import java.util.Vector;





/**
 * This class stores a list of b-values. It provides no additional information besides the B-matrix. 
 * It can read scheme files corresponding to the old Scheme V2 / V0.
 *
 * @author Philip Cook
 * @version $Id$
 *  
 */
public class B_VectorScheme extends DW_Scheme {

    /**
     * String that identifies this scheme version in the scheme file.
     */
    public static final String VERSION = "BVECTOR";


    /**
     * Logging object
     */
    private static final Logger logger = Logger.getLogger("camino.imaging.B_VectorScheme");


    /** B values. */
    private final double[] bValue;


    /**
     * @param gradDir gradient directions, one per measurement. Should be zero
     * for b=0 measurements.
     *
     * @param bVal b-values for each measurement.
     *
     */
    public B_VectorScheme(double[][] gradDir, double[] bVal) {
	super(gradDir);
	
	bValue = new double[numMeas];

	System.arraycopy(bVal, 0, bValue, 0, numMeas);
    }
    


    /**
     * @return the b-value for measurement i.
     */
    public double getB_Value(int i) {
	return bValue[i];
    }


    /**
     * Flips a component of the gradient vectors.
     */
    private DW_Scheme flip(int comp) {
	double[][] flippedG_Dir = new double[numMeas][3];

	for (int i = 0; i < numMeas; i++) {
	    if (!zero(i)) {
		flippedG_Dir[i] = getG_Dir(i);
		
		flippedG_Dir[i][comp] = -flippedG_Dir[i][comp];
	    }

	    // if zero, nothing to copy
	    
	}

	return new B_VectorScheme(flippedG_Dir, bValue);

    }


    /**
     * Negates the X component of the gradient directions.
     */
    public DW_Scheme flipX() {
	return flip(0);
    }


    /**
     * Negates the Y component of the gradient directions.
     */
    public DW_Scheme flipY() {
	return flip(1);
    }

    /**
     * Negates the Z component of the gradient directions.
     */
    public DW_Scheme flipZ() {
	return flip(2);
    }


    /**
     * Gets a scheme composed of a subset of the measurements.
     */
    public DW_Scheme getSubsetScheme(int[] indices) {
	
	int subsetSize = indices.length;

	double[][] subsetG_Dir = new double[indices.length][3];

	double[] subsetB_Vals = new double[subsetSize];
	
	for (int i = 0; i < subsetSize; i++) {
	    System.arraycopy(getG_Dir(indices[i]), 0, subsetG_Dir[i], 0, 3);
	    subsetB_Vals[i] = bValue[indices[i]];
	}

	return new B_VectorScheme(subsetG_Dir, subsetB_Vals);
    }


    /**
     * Re-orders the components of the gradient vectors.
     */
    public DW_Scheme gradOrder(int[] order) {
	//expects a 3 element array consisting of 0 (x dir), 1 (y dir) and 2 (z dir)

	double[][] swappedG_Dir = new double[numMeas][3];

	for(int i = 0; i < numMeas; i++) {
	    double[] dir = getG_Dir(i);

	    swappedG_Dir[i][0] = dir[order[0]];
	    swappedG_Dir[i][1] = dir[order[1]];
	    swappedG_Dir[i][2] = dir[order[2]];

	}

	return new B_VectorScheme(swappedG_Dir, bValue);
    } 


    /**
     * For backwards compatibility, allow construction at run time (no scheme file) given the 
     * number of zero and non-zero measurements and a fixed b-value.
     * 
     * @param M The number of zero measurements. These will be first in the scheme.
     * 
     * @param N The number of non-zero measurements. The gradient directoins are taken 
     *          from the electrostatically optimized point sets stored in PointSets/.
     *
     * @param b the b-value.
     * 
     *
     */
    public static B_VectorScheme elecPointSetScheme(int M, int N, double b) {

	double[][] nonZeroG = SphericalPoints.getElecPointSet(N);

	double[][] g = new double[M+N][3];
	
	double[] bVals = new double[N+M];

	for (int i = 0; i < N; i++) {
	    bVals[M+i] = b;
	    g[M+i] = nonZeroG[i];
	}
	

	return new B_VectorScheme(g, bVals);

    }




    /**
     * Reads a B_VectorScheme from a String representation that specifies gradient directions
     * and b-values (old version 2).
     *
     * @param lines the contents of the scheme file, parsed by line, with comments and version 
     * information removed.
     *
     */
    protected static B_VectorScheme readScheme(Vector<String> lines) {
    
	if (lines.size() < 1) {
            // infinite loop in inverters in this case
            throw new 
                LoggedException("No measurements in scheme file");
        }

	// local variables used here, class variables assigned in init method
	int numMeas = lines.size();

	// these get normalized here
	double[][] gDir = new double[numMeas][3];

	double[] bVal = new double[numMeas];
	
	// first line is version information, data starts on second line
	for (int i = 0; i < numMeas; i++) {
		
	    Scanner lineScanner = new Scanner(lines.elementAt(i));

	    gDir[i][0] = lineScanner.nextDouble();
	    gDir[i][1] = lineScanner.nextDouble();
	    gDir[i][2] = lineScanner.nextDouble();

	    bVal[i] = lineScanner.nextDouble();

	    if (bVal[i] == 0.0) {
		if (gDir[i][0] != 0.0 || gDir[i][1] != 0.0 || gDir[i][2] != 0.0) {
		    logger.info("Zero diffusion weighting in measurement " + i + 
				", setting gradient direction to zero");
		    
		    gDir[i][0] = 0.0;
		    gDir[i][1] = 0.0;
		    gDir[i][2] = 0.0;
			
		}
	    }
	    
	}

	return new B_VectorScheme(gDir, bVal);

    }


    /**
     * Reads a B_VectorScheme from a String representation that specifies q and tau 
     * (old version 0).
     *
     * @param lines the contents of the scheme file, parsed by line, with comments and version 
     * information removed.
     *
     */
    protected static B_VectorScheme readQ_VectorScheme(Vector<String> lines) {

	if (lines.size() < 5) {
            // infinite loop in inverters in this case
            throw new 
                LoggedException("No measurements in scheme file");
        }

	double tau = Double.parseDouble(lines.elementAt(0));

	// local variables used here, class variables assigned in init method
        
        double numMeasDecimal = Double.parseDouble(lines.elementAt(1));

	int numMeas = (int)numMeasDecimal;

        if (Math.abs(numMeas - numMeasDecimal) > 1E-9) {
            throw new LoggedException("Number of measurements in scheme, " + numMeasDecimal + ", is not an integer");
        }


	// these get normalized here
	double[][] gDir = new double[numMeas][3];

	double[] bVal = new double[numMeas];
	
        for (int i = 0; i < numMeas; i++) {

	    double[] q = new double[3];
	    
	    for (int j = 0; j < 3; j++) {
		q[j] = Double.parseDouble(lines.elementAt(2 + 3 * i + j));
	    }
	    
	    double modQ = Math.sqrt(q[0] * q[0] + q[1] * q[1] + q[2] * q[2]);

	    bVal[i] = modQ * modQ * tau;

	    if (modQ > 0.0) {
		for (int j = 0; j < 3; j++) {
		    gDir[i][j] = q[j] / modQ;
		}
	    }
	    
	}

	return new B_VectorScheme(gDir, bVal);
    }
	

    /**
     * Returns the String representation of this object.
     *
     */
    public String toString() {
	
	DecimalFormat df = new DecimalFormat("   0.000000;  -0.000000");
	DecimalFormat bValDF = new DecimalFormat("   0.000E00");

	StringBuffer sb = new StringBuffer();
	
	sb.append("#B-vector scheme. Contains gradient directions and b-values\n");
	sb.append("#g_x\tg_y\tg_z\tb\n");

	sb.append("VERSION: " + VERSION + "\n");

	
	for (int i = 0; i < numMeas; i++) {
	    
	    double[] gDir = getG_Dir(i);
	    
	    sb.append(df.format(gDir[0]));
	    sb.append(df.format(gDir[1]));
	    sb.append(df.format(gDir[2]));
	    sb.append(bValDF.format(bValue[i]));
	    sb.append("\n");
	}
	
	return sb.toString();
	
    }
   
}
