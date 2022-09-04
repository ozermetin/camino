package tractography;

import junit.framework.*;
import junit.extensions.*;


/**
 * Automated tests for <code>MeanCP</code>. 
 *
 * @version $Id: TestMeanCP.java,v 1.1 2005/11/09 13:28:57 ucacpco Exp $
 * @author  Philip Cook
 * @see <A HREF="http://www.junit.org">Junit Homepage</A>
 *
 *
 */
public class TestMeanCP extends TestCase {
  

    /** 
     * Initialise global resources for tests.
     */
    protected void setUp() {

    }

   
    protected void tearDown() {

    }


    public TestMeanCP(String name) {
	super(name);
	
    }
    
    public static void main(String[] args) {
	junit.textui.TestRunner.run(suite());
    }

   
    public static Test suite() {
	return new TestSuite(TestMeanCP.class);
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
	

	double[][][] image3 = new double[2][2][2];

	image3[0][0][0] = 0.0;
	image3[0][0][1] = 1.0;
	image3[0][1][0] = 2.0;
	image3[0][1][1] = 3.0;

	image3[1][0][0] = 4.0;
	image3[1][0][1] = 5.0;
	image3[1][1][0] = 6.0;
	image3[1][1][1] = 7.0;


	MeanCP mean = new MeanCP(2,2,2);
	
	mean.addImage(image1);
	mean.addImage(image2);
	mean.addImage(image3);	

	double[][][] result = mean.result();

	for (int i = 0; i < 2; i++) {
	    for (int j = 0; j < 2; j++) {
		for (int k = 0; k < 2; k++) {
		    assertEquals( (image1[i][j][k] + image2[i][j][k] + image3[i][j][k]) / 3.0, 
				  result[i][j][k], 1E-6);
		}
	    }
	}

    }

       

}
