package apps;
import data.*;
import misc.*;
import tools.*;

import java.io.*;
import java.util.logging.*;

/**
 * Computes indices of tensor shape. These include standard indices: l1, l2, l3, tr, md, rd, fa, ra, 2dfa.
 * Also linearity, planarity, and isotropy as defined by Westin et al Proc MICCAI 441-452 (1999).
 *
 * @author Philip Cook
 * @version $Id$
 */
public class DT_ShapeStatistics extends Executable{
    
    public DT_ShapeStatistics(String[] args){
        super(args);
    }
    
    /**
     * logging object
     */
    private static Logger logger= Logger.getLogger("camino.apps.DT_ShapeStatistics");
    
    
    /**
     * Output manager
     */
    //private static OutputManager om;
    
    
    //public static void main(String[] args) {
    
    int maxComponents;
    VoxelOrderDataSource input;
    String statistic; // was final
    
    //String stat = "";
    String stat;
    
    public void initDefaultVals(){
        stat = " ";
    }
    
    
    public void initOptions(String[] args){
        CL_Initializer.maxTensorComponents = 1;
        CL_Initializer.inputDataType = "double";
        
        CL_Initializer.CL_init(args);
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-stat")) {
                stat = args[i+1];
                CL_Initializer.markAsParsed(i,2);
            }
        }
        
        CL_Initializer.checkParsing(args);
    }
    
    //   om = new OutputManager();
    
    public void initVariables(){
        /*int maxComponents = CL_Initializer.maxTensorComponents;
        VoxelOrderDataSource input = new VoxelOrderDataSource(CL_Initializer.inputFile, 12,
        CL_Initializer.inputDataType);
        // final variable + immutable string, should allow if to be optimized
        final String statistic = stat;*/
        maxComponents = CL_Initializer.maxTensorComponents;
        input = new VoxelOrderDataSource(CL_Initializer.inputFile, 12, CL_Initializer.inputDataType);
        // final variable + immutable string, should allow if to be optimized
		statistic = stat;
    }
    
    
    public void execute(OutputManager om){
        while (input.more()) {
            
            double[] eig = input.nextVoxel();
            
            double value = computeStat(eig, statistic);
            
            om.output(new double[] {value});
        }
        
        
        om.close();
    }
    
    
    public static final double computeStat(double[] eig, String statistic) {
        
        double answer = 0.0;
        
        if (statistic.equals("cl")) {
            answer = (eig[0] - eig[4]) / eig[0];
        }
        else if (statistic.equals("cp")) {
            answer = (eig[4] - eig[8]) / eig[0];
        }
        else if (statistic.equals("cs")) {
            answer = eig[8] / eig[0];
        }
        else if (statistic.equals("l1")) {
            answer = eig[0];
        }
        else if (statistic.equals("l2")) {
            answer = eig[4];
        }
        else if (statistic.equals("l3")) {
            answer = eig[8];
        }
        else if (statistic.equals("tr")) {
            answer = eig[0] + eig[4] + eig[8];
        }
        else if (statistic.equals("md")) {
            answer = (eig[0] + eig[4] + eig[8]) / 3.0;
        }
        else if (statistic.equals("rd")) {
            answer = (eig[4] + eig[8]) / 2.0;
        }
        else if (statistic.equals("fa")) {
            
            double lmean = (eig[0] + eig[4] + eig[8]) / 3.0;
            
            if (lmean > 0.0) {
                
                answer =
                Math.sqrt(1.5 * ((eig[0] - lmean) * (eig[0] - lmean) +
                (eig[4] - lmean) * (eig[4] - lmean) +
                (eig[8] - lmean) * (eig[8] - lmean)) /
                (eig[0] * eig[0] + eig[4] * eig[4] + eig[8] * eig[8]));
            }
            else {
                answer = 0.0;
            }
            
        }
        else if (statistic.equals("ra")) {
            
            double lmean = (eig[0] + eig[4] + eig[8]) / 3.0;
            
            if (lmean > 0.0) {
                
                answer =
                Math.sqrt( 1.0 / 3.0 * ((eig[0] - lmean) * (eig[0] - lmean) +
                (eig[4] - lmean) * (eig[4] - lmean) +
                (eig[8] - lmean) * (eig[8] - lmean)) ) / lmean;
                
            }
            else {
                answer = 0.0;
            }
            
        }
        else if (statistic.equals("2dfa")) {
            double lmean = (eig[4] + eig[8]) / 2.0;
            
            if (lmean > 0.0) {
                
                answer =
                Math.sqrt(2.0 * ((eig[4] - lmean) * (eig[4] - lmean) +
                (eig[8] - lmean) * (eig[8] - lmean)) /
                (eig[4] * eig[4] + eig[8] * eig[8]));
            }
            else {
                answer = 0.0;
            }
        }
        else {
            throw new LoggedException("unrecognized statistic " + statistic);
        }
        
        return answer;
        
    }
    
    
    
}
