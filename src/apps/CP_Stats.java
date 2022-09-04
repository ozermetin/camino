package apps;

import data.*;
import imaging.AnalyzeHeader;
import misc.LoggedException;
import tools.*;
import tractography.*;

import java.io.*;
import java.util.logging.*;


/**
 * Reads PICo probability maps and computes a voxel-based statistic of them.
 *
 * 
 * @author Philip Cook
 * @version $Id: CP_Stats.java 1020 2012-01-17 21:19:19Z ucacpco $
 *
 */
public class CP_Stats {

    /**
     * Logging object
     */
    private static Logger logger = Logger.getLogger("camino.apps.CP_Stats");


    private static int volume = 0;

    // one CP map per PD per seed point in the new code
    private static boolean volumePerPD = true;

    // appended to file roots (for new files only, not on old root_${seed}.hdr files)
    private static String suffix = "";


    public static void main(String[] args) {

	CL_Initializer.CL_init(args);

	String inputRoot = "";

	String outputFile = OutputManager.outputFile;

	String operation = "";

	// pd, one out of N possibilities numbered 1...N
	int pd = 1;

	int targetIndex = -1;

	for (int i = 0; i < args.length; i++) {
	    if (args[i].equals("-inputroot")) {
		inputRoot = args[i+1];
		CL_Initializer.markAsParsed(i,2);
	    }
	    else if (args[i].equals("-operation")) {
		operation = args[i+1];
		CL_Initializer.markAsParsed(i,2);
	    }
	    else if (args[i].equals("-pd")) {
		pd = Integer.parseInt(args[i+1]);
		CL_Initializer.markAsParsed(i,2);
	    }
	    else if (args[i].equals("-outputroot")) {
		outputFile = args[i+1];
		CL_Initializer.markAsParsed(i,2);
	    }
	    else if (args[i].equals("-targetindex")) {
		targetIndex = Integer.parseInt(args[i+1]);
		
		suffix = "_" + targetIndex;

		CL_Initializer.markAsParsed(i,2);
	    }
	}

	if (inputRoot.equals("") && CL_Initializer.inputFile != null) {
	    logger.warning("input root not specified, using argument of -inputfile option as input root");
	    inputRoot = CL_Initializer.inputFile;
	}
	

	try {

          
            // for legacy compatibility (older versions of track numbered seeds from zero)
            if (new File(inputRoot + "0.hdr").exists()) {
                volumePerPD = false;
            }
            else if (new File(inputRoot + "1.hdr").exists()) {
                volume = 1;
		volumePerPD = false;
            }
	    else if (new File(inputRoot + "1_1.hdr").exists()) {
		volume = 1;
		volumePerPD = true;
	    }
	    else {

		volume = 1;
		volumePerPD = true;

		String s = getVolumeFileRoot(inputRoot, volume, 1);
		if (new File(s + ".hdr").exists()) {
		    // we are OK to continue
		}
		else {
		    throw new LoggedException("Cannot find connection probability files matching input root");
		}
	    }
	    
	    String firstPD_FileRoot = getVolumeFileRoot(inputRoot, volume, 1);

	    String fileRoot = getVolumeFileRoot(inputRoot, volume, pd);

	    AnalyzeHeader ah = AnalyzeHeader.readHeader(firstPD_FileRoot + ".hdr");

            ah.setGzip(OutputManager.gzipOut);
	    
	    int xDataDim = ah.xDataDim();
	    
	    int yDataDim = ah.yDataDim();
	    
	    int zDataDim = ah.zDataDim();
            
	    CP_StatMap map = null;
	    

	    if (operation.equals("min")) {
		map = new MinCP(xDataDim, yDataDim, zDataDim);
	    }
	    else if (operation.equals("max")) {
		map = new MaxCP(xDataDim, yDataDim, zDataDim);
	    }
	    else if (operation.equals("mean")) {
		map = new MeanCP(xDataDim, yDataDim, zDataDim);
	    }
	    else if (operation.equals("median")) {
		map = new MedianCP(xDataDim, yDataDim, zDataDim);
	    }
	    else if (operation.equals("sum")) {
		map = new SumCP(xDataDim, yDataDim, zDataDim);
	    }
	    
	    
	    // CP maps for PDs above 1 may not exist for every seed
	    // so iterate over first PD, which should exist for every seed
	    while (new File(firstPD_FileRoot + ".hdr").exists()) {   
	    
		if (new File(fileRoot + ".hdr").exists()) {
		    AnalyzeHeader hdr = AnalyzeHeader.readHeader(fileRoot + ".hdr");
		    double[][][] vol = hdr.readSingleVolumeData();
		    map.addImage(vol);
		}
		
		volume++;

		fileRoot = getVolumeFileRoot(inputRoot, volume, pd);
		firstPD_FileRoot = getVolumeFileRoot(inputRoot, volume, 1);
		
	    }
	    

	    if (!volumePerPD) {

		if (outputFile == null) {
		    outputFile = operation + "cp";
		}
		
		ah.writeScalarImage(map.result(), OutputManager.outputFile);

	    }
	    else {
                
		if (outputFile == null) {
		    outputFile = operation + "cp_1";
		}
		
                ah.writeScalarImage(map.result(), OutputManager.outputFile);
	    }
	    
	}
	catch (IOException e) {
	    throw new LoggedException(e);
	}
    }


    private static String getVolumeFileRoot(String inputRoot, int volume, int pd) {
	
	if (volumePerPD) {
	    
	    String root = inputRoot + volume + "_" + pd + suffix;
	    return root;
	}
	else {
	    String root = inputRoot + volume;
	    return root;
	}

    }




}
