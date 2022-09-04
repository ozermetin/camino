import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import utils.BoundingSphereHolder;
import utils.Shell;
import utils.SphereShell;


public class TestShell {

	private SphereShell sShell;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		BoundingSphereHolder seedHolder = new BoundingSphereHolder(new double[]{0.,0.,0.},10);
		BoundingSphereHolder targetHolder = new BoundingSphereHolder(new double[]{6.,6.,6.},5);
		sShell = new SphereShell(5);
		sShell.setSpheres(seedHolder, targetHolder);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetShellNumber() {
		
		
		int [] coor = new int[]{6,6,6};
		Shell shell=sShell.getShellofCoordinates(coor);
		assertEquals(shell.getShellNumber(), 5);
		
		coor = new int[]{0,0,0};
		shell=sShell.getShellofCoordinates(coor);
		assertEquals(shell.getShellNumber(), 1);
		
		coor = new int[]{10,10,10};
		shell=sShell.getShellofCoordinates(coor);
		assertEquals(shell.getShellNumber(), 5);
		
		coor = new int[]{0,1,0};
		shell=sShell.getShellofCoordinates(coor);
		assertEquals(shell.getShellNumber(), 1);
		
		coor = new int[]{1,1,2};
		shell=sShell.getShellofCoordinates(coor);
		assertEquals(shell.getShellNumber(), 2);
		
		coor = new int[]{3,3,3};
		shell=sShell.getShellofCoordinates(coor);
		assertEquals(shell.getShellNumber(), 4);
		
		coor = new int[]{4,4,4};
		shell=sShell.getShellofCoordinates(coor);
		assertEquals(shell.getShellNumber(), 5);
		coor = new int[]{6,0,6};
		shell=sShell.getShellofCoordinates(coor);
		assertEquals(shell.getShellNumber(), 5);
	}

}
