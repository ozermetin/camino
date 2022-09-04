package apps;

import imaging.*;
import misc.LoggedException;
import tools.*;

import java.io.*;

import data.*;

/**
 * Splits a 4D Nifti image into 3D component volumes.
 *
 * @author Philip Cook
 */
public class Split4D_NiiImage extends Executable {

    public Split4D_NiiImage(String[] args) {
        super(args);
    }
    
    private String outputRoot;
     
    public void initDefaultVals() {
        outputRoot = "";
    }
    
    
    public void initOptions(String[] args) {
        
        CL_Initializer.CL_init(args);
	
		for (int i = 0; i < args.length; i++) {
                    if (args[i].equals("-outputroot")) {
                        outputRoot = args[i+1];
                        CL_Initializer.markAsParsed(i, 2);
                    }
                }
                
                CL_Initializer.checkParsing(args);
    }
    

    public void initVariables() {

    }


    public void execute(OutputManager om) { 	
        
	Nifti1Dataset input = new Nifti1Dataset(CL_Initializer.inputFile);
        
	if (input.components() == 1) {
	    throw new LoggedException("This image is already three-dimensional");
	}
        
	try {
	    Nifti1Dataset.convertTo3D(CL_Initializer.inputFile, outputRoot);
	}
	catch (IOException e) {
	    throw new LoggedException(e);
	}

    }



}
