package apps;

import data.OutputManager;
import tools.*;


import java.text.*;
import java.util.*;
import java.util.logging.Logger;

 /**
  * Converts FSL b-vectors and b-values to Camino scheme file format.
  * 
  * @author Philip Cook
  * @version $Id: FSL_ToScheme.java 889 2010-11-21 01:04:29Z ucacpco $
  */
public class FSL_ToScheme extends Executable {


    /**
     * logging object
     */
    private static final Logger logger = Logger.getLogger("apps.FSL_ToScheme");

    private String bvecfile;
    private String bvalfile;
    
    private String outputFile;
    
    private double bScale;
    
    private boolean flipX;
    private boolean flipY;
    private boolean flipZ;
    
    private int numScans;

    private boolean interleave;
    
    /** 
     * Some scanners define b-value as |g|^2 * \beta where \beta is what 
     * they CLAIM the b-value is. If you use normalized gradient directions, 
     * you need to increase b accordingly to make the same ADC calculation.
     */
    private boolean useGradMod;
    

    public FSL_ToScheme(String[] args) {
        super(args);
        Locale.setDefault(Locale.UK);
    }


    public void initDefaultVals() {

        bvecfile = null;
        bvalfile = null;
        
        outputFile = null;
        
        bScale = 1.0E6;
        
        flipX = false;
        flipY = false;
        flipZ = false;
        
        numScans = 1;
        
        interleave = false;

    }

    
    public void initOptions(String[] args) {

	CL_Initializer.CL_init(args);

	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-bvecfile") || args[i].equals("-bvecs")) {
		bvecfile = args[i+1];
		CL_Initializer.markAsParsed(i,2);
	    }
	    else if (args[i].equals("-bvalfile") || args[i].equals("-bvals")) {
		bvalfile = args[i+1];
		CL_Initializer.markAsParsed(i,2);
	    }
	    else if (args[i].equals("-outputfile")) {
		outputFile = args[i+1];
		CL_Initializer.markAsParsed(i,2);
	    }
	    else if (args[i].equals("-bscale")) {
		bScale = Double.parseDouble(args[i+1]);
		CL_Initializer.markAsParsed(i,2);
	    }
            else if (args[i].equals("-numscans")) {
                numScans = Integer.parseInt(args[i+1]);
                CL_Initializer.markAsParsed(i,2);
            }
            else if (args[i].equals("-interleave")) {
                interleave = true;
                CL_Initializer.markAsParsed(i);
            }
	    else if (args[i].equals("-usegradmod")) {
		useGradMod = true;
		logger.info("Gradient direction magnitude will be incorporated into b-value");	
		CL_Initializer.markAsParsed(i);
	    }
	    else if (args[i].equals("-flipx")) {
		flipX = true;
		CL_Initializer.markAsParsed(i);
	    }
	    else if (args[i].equals("-flipy")) {
		flipY = true;
		CL_Initializer.markAsParsed(i);
	    }
	    else if (args[i].equals("-flipz")) {
		flipZ = true;
		CL_Initializer.markAsParsed(i);
	    }
	}

        CL_Initializer.checkParsing(args);
        
    }

    public void initVariables() {
        // Still not seeing the point of this method
    }
    

    public void execute(OutputManager om) {

	String scheme = "";

	FileInput vecIn = new FileInput(bvecfile);

	String[] x = vecIn.readString().trim().split("\\s+");
	
	String[] y = vecIn.readString().trim().split("\\s+");

	String[] z = vecIn.readString().trim().split("\\s+");
	
	vecIn.close();

	int numMeasSingleScan = x.length;
	int numMeas = numScans * numMeasSingleScan;

	FileInput valIn = new FileInput(bvalfile);

	String[] b = valIn.readString().trim().split("\\s+");
       
	if (b.length != numMeasSingleScan) {
	    throw new misc.LoggedException("Number of b-vectors and b-values do not match");
	}

	DecimalFormat df = new DecimalFormat("   0.000000;  -0.000000");

	DecimalFormat v0DF = new DecimalFormat("0.000000");

	DecimalFormat bValDF = new DecimalFormat("   0.000E00");

	scheme += "# Scheme file created by fsl2scheme\nVERSION: BVECTOR\n";

	boolean warnAboutGradDirMod = false;

        // parse to double
	double[] vx = new double[numMeasSingleScan];
	double[] vy = new double[numMeasSingleScan];
	double[] vz = new double[numMeasSingleScan];
	double[] bv = new double[numMeasSingleScan];

	for (int i = 0; i < numMeasSingleScan; i++) {
	    vx[i] = Double.parseDouble(x[i]);
	    vy[i] = Double.parseDouble(y[i]);
	    vz[i] = Double.parseDouble(z[i]);
	    bv[i] = Double.parseDouble(b[i]);
	}


	// add repeated scans if necessary
	if (numScans > 1) {
            
	    double[] vxWithRepeats = new double[numMeas];
	    double[] vyWithRepeats = new double[numMeas];
	    double[] vzWithRepeats = new double[numMeas];
	    double[] bvWithRepeats = new double[numMeas];

	    if (interleave) {
		for (int i = 0; i < numMeasSingleScan; i++) {
		    for (int s = 0; s < numScans; s++) {
			vxWithRepeats[i*numScans + s] = vx[i];
			vyWithRepeats[i*numScans + s] = vy[i];
			vzWithRepeats[i*numScans + s] = vz[i];
			bvWithRepeats[i*numScans + s] = bv[i];
		    }
		}
	    }
	    
	    else {
		for (int s = 0; s < numScans; s++) {
		    System.arraycopy(vx, 0, vxWithRepeats, numMeasSingleScan * s, numMeasSingleScan);
		    System.arraycopy(vy, 0, vyWithRepeats, numMeasSingleScan * s, numMeasSingleScan);
		    System.arraycopy(vz, 0, vzWithRepeats, numMeasSingleScan * s, numMeasSingleScan);
		    System.arraycopy(bv, 0, bvWithRepeats, numMeasSingleScan * s, numMeasSingleScan);
		}
	    }

	    vx = vxWithRepeats;
	    vy = vyWithRepeats;
	    vz = vzWithRepeats;
	    bv = bvWithRepeats;
	}

        for (int i = 0; i < numMeas; i++) {
	
            double modV = Math.sqrt(vx[i]*vx[i] + vy[i]*vy[i] + vz[i]*vz[i]);

            if (modV == 0.0 || Math.abs(1.0 - modV) < 1E-4)
                modV = 1.0;

            if (!useGradMod && modV != 1.0) {
                warnAboutGradDirMod = true;
            }
		
            vx[i] = (vx[i] / modV);
		
            if (flipX && vx[i] != 0.0) {
                vx[i] = -vx[i];
            }
		
            vy[i] = (vy[i] / modV);
		
            if (flipY && vy[i] != 0.0) {
                vy[i] = -vy[i];
            }
		
            vz[i] = (vz[i] / modV);
		
            if (flipZ && vz[i] != 0.0) {
                vz[i] = -vz[i];
            }

            // get rid of -0.0
            if (vx[i] == 0.0) { 
                vx[i] = Math.abs(vx[i]);
            }
            if (vy[i] == 0.0) { 
                vy[i] = Math.abs(vy[i]);
            }
            if (vz[i] == 0.0) { 
                vz[i] = Math.abs(vz[i]);
            }

				
            scheme += df.format(vx[i]);
            scheme += df.format(vy[i]);
            scheme += df.format(vz[i]);
		
            if (useGradMod) {
                scheme += bValDF.format(bv[i] * bScale * modV * modV) + "\n";
            }
            else { 
                scheme += bValDF.format(bv[i] * bScale) + "\n";
            }

        }

    
	
        if (warnAboutGradDirMod) {
            logger.warning("Some measurements have non-unit gradient directions. Directions have been " + 
                           "normalized to unit length");
        }
	
        
        if (outputFile != null) {
            FileOutput out = new FileOutput(outputFile);
	    
            out.writeString(scheme);
            
            out.close();
        }
        else {
            System.out.println(scheme);
        }


    } 

    
}
