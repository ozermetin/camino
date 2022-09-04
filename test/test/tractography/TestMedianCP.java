package tractography;

import junit.framework.*;
import junit.extensions.*;


/**
 * Automated tests for <code>MedianCP</code>. 
 *
 * @version $Id: TestMedianCP.java,v 1.1 2005/11/09 13:28:57 ucacpco Exp $
 * @author  Philip Cook
 * @see <A HREF="http://www.junit.org">Junit Homepage</A>
 *
 *
 */
public class TestMedianCP extends TestCase {
  

    /** 
     * Initialise global resources for tests.
     */
    protected void setUp() {

    }

   
    protected void tearDown() {

    }


    public TestMedianCP(String name) {
	super(name);
	
    }
    
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }

   
    public static Test suite() {
	return new TestSuite(TestMedianCP.class);
    }


    public void testOddResult() {

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
	

	double[][][] image3 = new double[2][2][2];

	image3[0][0][0] = 0.0;
	image3[0][0][1] = 1.0;
	image3[0][1][0] = 2.0;
	image3[0][1][1] = 3.0;

	image3[1][0][0] = 4.0;
	image3[1][0][1] = 5.0;
	image3[1][1][0] = 6.0;
	image3[1][1][1] = 7.0;


	MedianCP median = new MedianCP(2,2,2);
	
	median.addImage(image1);
	median.addImage(image2);
	median.addImage(image3);	

	double[][][] result = median.result();

	assertEquals(1.0, result[0][0][0], 1E-6);
	assertEquals(2.0, result[0][0][1], 1E-6);
	assertEquals(3.0, result[0][1][0], 1E-6);
	assertEquals(4.0, result[0][1][1], 1E-6);

	assertEquals(4.0, result[1][0][0], 1E-6);
	assertEquals(5.0, result[1][0][1], 1E-6);
	assertEquals(6.0, result[1][1][0], 1E-6);
	assertEquals(0.0, result[1][1][1], 1E-6);	

    }


   public void testEvenResult() {

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
	

	MedianCP median = new MedianCP(2,2,2);
	
	median.addImage(image1);
	median.addImage(image2);

	double[][][] result = median.result();

	for (int i = 0; i < 2; i++) {
	    for (int j = 0; j < 2; j++) {
		for (int k = 0; k < 2; k++) {
		    assertEquals( (image1[i][j][k] + image2[i][j][k]) / 2.0, 
				  result[i][j][k], 1E-6);
		}
	    }
	}

    }

       

}
