package tractography;

import junit.framework.*;
import junit.extensions.*;


/**
 * Automated tests for <code>MaxCP</code>. 
 *
 * @version $Id: TestMaxCP.java,v 1.1 2005/11/09 13:28:57 ucacpco Exp $
 * @author  Philip Cook
 * @see <A HREF="http://www.junit.org">Junit Homepage</A>
 *
 *
 */
public class TestMaxCP extends TestCase {
  

    /** 
     * Initialise global resources for tests.
     */
    protected void setUp() {

    }

   
    protected void tearDown() {

    }


    public TestMaxCP(String name) {
	super(name);
	
    }
    
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }

   
    public static Test suite() {
	return new TestSuite(TestMaxCP.class);
    }


    public void testResult() {

	double[][][] image1 = new double[2][2][2];

	image1[0][0][0] = 1.0;
	image1[0][0][1] = 2.0;
	image1[0][1][0] = 3.0;
	image1[0][1][1] = 4.0;

	image1[1][0][0] = 5.0;
	image1[1][0][1] = 6.0;
	image1[1][1][0] = 7.0;
	image1[1][1][1] = 0.0;

	double[][][] image2 = new double[2][2][2];

	image2[0][0][0] = 8.0;
	image2[0][0][1] = 7.0;
	image2[0][1][0] = 6.0;
	image2[0][1][1] = 5.0;

	image2[1][0][0] = 4.0;
	image2[1][0][1] = 3.0;
	image2[1][1][0] = 2.0;
	image2[1][1][1] = 0.0;
	
	MaxCP max = new MaxCP(2,2,2);
	
	max.addImage(image1);
	max.addImage(image2);
	
	double[][][] result = max.result();

	assertEquals(8.0, result[0][0][0], 1E-6);
	assertEquals(7.0, result[0][0][1], 1E-6);
	assertEquals(6.0, result[0][1][0], 1E-6);
	assertEquals(5.0, result[0][1][1], 1E-6);

	assertEquals(5.0, result[1][0][0], 1E-6);
	assertEquals(6.0, result[1][0][1], 1E-6);
	assertEquals(7.0, result[1][1][0], 1E-6);
	assertEquals(0.0, result[1][1][1], 1E-6);
    }

       

}
