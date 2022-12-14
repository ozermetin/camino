
package simulation.dynamics;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;

import numerics.MTRandom;

import simulation.DiffusionSimulation;
import simulation.SimulationParams;
import simulation.dynamics.StepGeneratorFactory.StepType;
import tools.CL_Initializer;

/**
 *  Generates steps of random orientation and fixed length
 * 
 *
 * @author Matt Hall (m.hall@cs.ucl.ac.uk)
 *
 */
public class FixedLengthStepGenerator implements StepGenerator {
    
    /** the length of a step */
    private final double length;
    
    /** random number generator */
    //private MTRandom stepTwister=new MTRandom((1227293371<<32)|(126345811));
    MTRandom stepTwister=new MTRandom(CL_Initializer.seed+17);
    
    /** logging object */
    private final Logger logger=Logger.getLogger(this.getClass().getName());
    
    /** dimensionality of step */
    private int D=DiffusionSimulation.D;

    /** step vector */
    private double[] step= new double[D];

    
    /** arrays for getting at step statistics  */
    private final int[][] stepDist = new int[D][1001];
    
    /** flag to generate step stats or not */
    private final boolean stepStats;
    
    
    /** constructor. requires simulation parameters */
    public FixedLengthStepGenerator(SimulationParams simParams){
        
        this.length=simParams.getStepParams()[0];
        
        stepStats=false;
    }
    
    private FixedLengthStepGenerator(double length){
        
        this.length=length;
        
        stepStats=true;
    }
    
    /** 
     * generates a step of fixed length and uniformly distributed
     * orientation in D<4 dimensions
     * 
     * @return vector containing new step
     */
    public double[] getStep(Walker walker) {
        if(D==1){
            if(stepTwister.nextDouble()<0.5){
                step[0]=-length;
            }
            else{
                step[0]= length;
            }
        }
        else if(D==2){
            double theta= 2.0*Math.PI*stepTwister.nextDouble();
            
            step[0]=length*Math.cos(theta);
            step[1]=length*Math.sin(theta);
        }
        else if(D==3){
            double theta= 2.0*Math.PI*stepTwister.nextDouble();
            double cosPhi = 2.0*stepTwister.nextDouble()-1.0;
            
            double cosTh= Math.cos(theta);
            double sinTh= Math.sin(theta);
            
            double sinPhi= Math.sqrt(1.0-cosPhi*cosPhi);
                        
            step[0]= length*cosTh*sinPhi;
            step[1]= length*sinTh*sinPhi;
            step[2]= length*cosPhi;
            
            
            for(int i=0; i<D; i++){
                int bin = (int)(((step[i]/length)+1.0)*500.0);
                stepDist[i][bin]++;
            }
            
            
        }
        else{
            String errMess= new String("steps of dimension "+D+" are not yet implemented. sorry.");
            logger.severe(errMess);
            throw new RuntimeException(errMess);
        }
        
        return step;
    }

    /** 
     * tells the width of the cloning border.
     * in this case this is the just the 
     * length of a step. 
     * 
     * @return border width (step length) in meters
     */
    public final double getBorder(){
    	return length;
    }

    
    /** 
     * @return type of step generator
     */
    public final StepType getType(){
    	return StepType.FIXEDLENGTH;
    }
    
    public static void main(String[] args) {

        int num_steps = 100000;
        
        BufferedWriter dispsfile=null;
        
        
        FixedLengthStepGenerator gen = new FixedLengthStepGenerator(1.0);
        
        
        for(int i=0; i<num_steps; i++){
            double[] step = gen.getStep(null);
        }
    
        int[][] stepDist= gen.stepDist;
        
        
        try{
            dispsfile=new BufferedWriter(new FileWriter("stepCompDistrns.dat"));
        }
        catch(IOException ioe){
            throw new RuntimeException(ioe);
        }
            
        try{
            for(int i=0; i<stepDist[0].length; i++){
                double x= (((double)i)-500.0)/1000;
            
                dispsfile.write(x+" ");
                for(int j=0; j<gen.D; j++){
                    dispsfile.write(stepDist[j][i]+" ");
                }
                dispsfile.write("\n");
            }
            
            dispsfile.flush();
            dispsfile.close();
            
        }
        catch(IOException ioe){
            throw new RuntimeException(ioe);
        }
    }
}
