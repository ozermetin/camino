package tractography;

import junit.framework.*;
import junit.extensions.*;

import data.*;
import misc.DT;
import numerics.*;


/**
 * Tests for RK4 tracker
 *
 * @version $Id$
 * @author  Philip Cook
 * @see <A HREF="http://www.junit.org">Junit Homepage</A>
 *
 *
 */
public class TestRK4FibreTracker extends TestFibreTracker {

    
 
    /** 
     * Initialise global resources for tests.
     */
    protected void setUp() {

    }

   
    protected void tearDown() {

    }


    public TestRK4FibreTracker(String name) {
	super(name);
	
    }
    
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }

   
    public static Test suite() {
	return new TestSuite(TestRK4FibreTracker.class);
    }



 	
    protected FibreTracker getTracker(TractographyImage image) {
	return new RK4FibreTracker(new VectorLinearInterpolator(image), 0.5);
    }



    /**
     * Ensure we get the correct first / next step at various points.
     *
     */
    public void testStep() {

        DT_TractographyImage cube = Images.getCube();

        RK4FibreTracker tracker = new RK4FibreTracker(new DT_LinearInterpolator(cube), 0.5);

        // in the middle
        Point3D point = new Point3D(Images.xVoxelDim, Images.yVoxelDim, Images.zVoxelDim);

        Vector3D step = tracker.getNextStep(point, new Vector3D(1.0, 0.0, 0.0));

        assertEquals(0.5, step.mod(), 1E-6);

    }


    
}
