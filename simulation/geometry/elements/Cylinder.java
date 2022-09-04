/**
 * 
 */
package simulation.geometry.elements;

import java.io.IOException;
import java.io.Writer;

/**
 * subinterface for cylinders. contains everything in
 * SubstrateObject plus some cylinder-specific getters
 * and a toFile() method for debugging
 * 
 * @author matt (m.hall@cs.ucl.ac.uk)
 *
 */
public interface Cylinder extends SubstrateObject {

	/**
	 * @return outer radius of cylinder
	 */
	public double getRadius();
	
	/**
	 * @return position in space
	 */
	public double[] getPosition();
	
	/** 
	 * @return distance of central axis to a point
	 */
	public double getDistanceFrom(double[] pos);
		
	/**
	 * spits out cylinder cross section to a file
	 */
	public void toFile(Writer writer) throws IOException;
}
