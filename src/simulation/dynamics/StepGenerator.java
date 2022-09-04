/* Step.java created on 25-Nov-2005
 * (simulation)
 * 
 * author: ucacmgh
 * 
 */
package simulation.dynamics;

import simulation.dynamics.StepGeneratorFactory.StepType;

/**
 *  Camino fibre reconstruction and tracking toolkit
 * 
 * Step (simulation)
 * 
 *  Contains a method protottype to get a step vector for 
 *  a Walker ro use during walker update
 * 
 *
 * @author Matt Hall (m.hall@cs.ucl.ac.uk)
 *
 */
public interface StepGenerator {
    
    /** returns a step vector as array of doubles 
     * @return step vector 
     */
    public double[] getStep(Walker walker);

    /**
     * get the border width for cloning 
     */
    public double getBorder();
    
    /**
     * get the step generator type
     */
    public StepType getType();
}
