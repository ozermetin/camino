package simulation.geometry.substrates;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Logger;

import org.omg.CORBA.portable.OutputStream;

import simulation.DiffusionSimulation;
import simulation.SimulationParams;
import simulation.dynamics.Walker;
import simulation.geometry.elements.SquashyCylinder;
import simulation.geometry.elements.SquashyCylinder.Chord;
import tools.CL_Initializer;

import misc.LoggedException;
import numerics.GammaRandom;
import numerics.MTRandom;

public class SquashyInflammationSubstrate extends CylinderSubstrate {

	/** for debugging */
	public boolean chuck=false;
	
	/** logging object */
	private final Logger logger=Logger.getLogger(this.getClass().getName());
	
	/** random number generator */
	private final MTRandom rng= new MTRandom(CL_Initializer.seed);
	
	/** dimensionality of space */
	private final int D=DiffusionSimulation.D;
	
	/** size of substrate */
	private final double[] L;
	
	/** array of cylinders */
	public SquashyCylinder[] cylinder;
	
	/** min cylinder radius */
	private double[] radius;
		
	/** radius increment at each step */
	private double[] rinc;
	
	/** number of steps in incrementing  cylinder size */
	private final int numIncrements;
	
	/** number of steps between re-sizing cylinders */
	private final int inflammationIncrementModulus;
	
	/** counter for the number of times the substrate initialiser function is called */
	//private int calls=0;
	
	/** index of current increment */
	private int n=0;
	
	/** array of cylinder positions */
	public double[][] P;
	
	/** counter for miscellainious purposes */
	private int counter=0;
	
	/** records which cylinder an intersection was with */
	private int[] cylCrossed;
	
	/** list of shortest distances to barrier crossings */
	private double[] shortestDist;
	
	/** list of membrane permeabilities */
	private double[] cyl_p;
	
	/** list of d[0] values for barrier intersections */
	private double[] intOriginDist;
	
	/** list of intersection normals for barrier intersections */
	private double[][] intNormal;
	
	/** list of initially in or out flags for intersections */
	private boolean[] intInside;
	
	/** cloning border */
	//private static double border=DiffusionSimulation.border;
	
	/** which cylinder was the most recent detected intersection with? */
	private int lastCrossed=-1;	
	
	/** membrane permiability */
	private final double p;
	
	/** expansion coeff */
	private final double K=1E4;
	
	/** gamma random number generator */
	private final GammaRandom grng;
	
	/** place to store clones */
	private final ArrayList<double[]> clones=new ArrayList<double[]>();

	/** 
	 * number of placed cylinders before cloning
	 */
	private final int Nbefore;
	
	/** 
	 * constructor. needs min and max radius, the number of 
	 * cylinders, and the number of increments 
	 * 
	 */
	public SquashyInflammationSubstrate(SimulationParams simParams) {

		super(new double[]{SimulationParams.sim_L, 
		                   SimulationParams.sim_L, 
		                   SimulationParams.sim_L}, simParams, true);
		
		double k=CL_Initializer.gamma_k;
		double beta=CL_Initializer.gamma_beta;
		int numCylinders= SimulationParams.sim_cyl_dist_size;
		int numVoxels=CL_Initializer.numVoxels;
		int numIncrements= SimulationParams.sim_inflamm_increments;
		double substrateSize= SimulationParams.sim_L;
		
		
		grng= new GammaRandom(CL_Initializer.seed+8273l, k, beta);
		
		this.numIncrements= numIncrements;
		this.L=new double[]{substrateSize, substrateSize, substrateSize};
		
		if(numVoxels<numIncrements){
			logger.warning("number of voxels less than number if inflammation increments.\n"+
					"inflammation increment modulus =1 will be used.");
			this.inflammationIncrementModulus=1;
		}
		else{
			this.inflammationIncrementModulus=numVoxels/numIncrements;
		}
		
		
		P= new double[numCylinders][D];
		
		cylCrossed= new int[numCylinders];
		cyl_p= new double[numCylinders];
		shortestDist= new double[numCylinders];
		intOriginDist= new double[numCylinders];
		intNormal= new double[numCylinders][D];
		intInside= new boolean[numCylinders];
		
		
		// initialise radius array
		radius= new double[numCylinders];

		// initialise radii
		for(int i=0; i<numCylinders; i++){
			radius[i]= grng.nextGamma();
		}
		
		
		
		// check if cross sectional areas are compatible
		double cylArea=0.0;
		for(int i=0; i<radius.length; i++){
			cylArea+=Math.PI*radius[i]*radius[i];
		}
		
		double sqArea=L[0]*L[1];
		if(cylArea>=sqArea){
			logger.warning("total area of specified gamma-distributed cylinders ("+cylArea+") is greater than cross section of substrate ("+sqArea+")");
		}
		
		// sort radii into ascending order
		Arrays.sort(radius);
		
		// reverse it into descending order
		double[] tempRadius= new double[radius.length];
		for(int i=0; i<radius.length; i++){
			tempRadius[radius.length-i-1]=radius[i];
		}
		radius=tempRadius;
		// set the increments
		rinc= new double[radius.length];
		
		for(int i=0; i<radius.length; i++){
			double r= radius[i];
			
			double a= Math.PI*r*r;
			
			double inc= K*a;
			
			rinc[i]=inc;
		}
		// array of cylinder positions
		P= new double[numCylinders][D];
		
		int maxInd=numCylinders;
		// place cylinders on substrate
		for(int i=0; i<radius.length; i++){
			boolean overlapping= true;
			int count=0;
			
			ArrayList<double[]> newClones=null;
			
			while(overlapping && (count<10000)){
				P[i][0]= L[0]*rng.nextDouble();
				P[i][1]= L[1]*rng.nextDouble();
				
				overlapping=false;
				
				// check against cylinders already placed
				for(int j=0; j<i; j++){
					double sqDist= (P[i][0]-P[j][0])*(P[i][0]-P[j][0]) + (P[i][1]-P[j][1])*(P[i][1]-P[j][1]);
					double rmax= radius[i]+radius[j];
					
					if(sqDist<(rmax*rmax)){
						overlapping=true;
						break;
					}
				}
				
				// check against clones
				for(int j=0; j<clones.size(); j++){
					double[] Pc=clones.get(j);
					double sqDist= (P[i][0]-Pc[0])*(P[i][0]-Pc[0]) + (P[i][1]-Pc[1])*(P[i][1]-Pc[1]);
					double clinc= Math.PI*Pc[D]*Pc[D]*K;
					double rmax=radius[i]+Pc[D];
					
					if(sqDist<rmax*rmax){
						overlapping=true;
						break;
					}
					
				}

				// check if the new cylinder needs cloning, 
				// and check the clones for overlaps
				newClones=checkForCloning(P[i], radius[i], i);

				for(int j=0; j<newClones.size(); j++){
					double[] Pc= newClones.get(j);
					double r= Pc[D];
					double clinc=Math.PI*r*r*K;
					double rmax= r+numIncrements*clinc;
					
					
					// check against placed cylinders
					for(int kk=0; kk<i; kk++){
						double sqDist= (Pc[0]-P[kk][0])*(Pc[0]-P[kk][0]) + (Pc[1]-P[kk][1])*(Pc[1]-P[kk][1]);
						if(sqDist<(r+radius[kk])*(r+radius[kk])){
							overlapping=true;
							break;
						}
					}
					
					// check against other clones
					for(int kk=0; kk<clones.size(); kk++){
						double[] clone= clones.get(kk);
						double rClone= clone[D];
						
						double sqDist= (Pc[0]-clone[0])*(Pc[0]-clone[0]) + (Pc[1]-clone[1])*(Pc[1]-clone[1]);
						if(sqDist<(r+rClone)*(r+rClone)){
							overlapping=true;
							break;
						}
					}
				}
	
				count++;
			}
						
			if(count==10000){
				logger.warning("could only place "+(i+1)+" of "+ numCylinders+" cylinders on substrate");
				maxInd=i;
				
				double[][] Pnew = new double[maxInd][];
				
				for(int j=0; j<maxInd; j++){
				    Pnew[j]=P[j];
				}
				
				P=Pnew;
				
				break;
			}

			// if we're down here and not overlapping then it should be ok to add the clones
			if(!overlapping){
				clones.addAll(newClones);
			}
		}
		
		// calculate intracellular vol frac
		cylArea=0.0;
		for(int i=0; i<maxInd; i++){
			cylArea+=Math.PI*radius[i]*radius[i];
		}
		
		double V_I= cylArea/sqArea;
		
		logger.info("intracellular volume fraction "+V_I);
		
		
		// remember how many we've managed to place
		Nbefore=maxInd;
		
		// clone the cylinders around the edge (instantiates the cylinder array)
		initClones();

		int numCyls=Nbefore+clones.size();
		
		cylinder= new SquashyCylinder[numCyls];
		
		// instantiate the placed cylinders		
		for(int i=0; i<Nbefore; i++){
			cylinder[i]= new SquashyCylinder(P[i], radius[i], SimulationParams.sim_p);
		}
		
		
		//instantiate the clones
		for(int i=Nbefore; i<cylinder.length; i++){
			double[] pos= new double[D];
			double[] Pc= clones.get(i-Nbefore);
			
			for(int j=0; j<D; j++){
				pos[j]=Pc[j];
			}
			
			cylinder[i]=new SquashyCylinder(pos, Pc[D], simParams.sim_p);

			// who are you a clone of?
			int cloneOf= (int)Pc[D+1];
			
			// let the original cylinder know that it has a clone
			cylinder[cloneOf].addToMyClones(cylinder[i]);
		}
		
		// finally, must propagate knowledge of clonal relationships to all clones
		for(int i=0; i<Nbefore; i++){
			cylinder[i].propagateCloneRelationships();
		}
		
		
		// set the cylinder array in the superclass
		setCylinders(cylinder);
		
		// reset the increments
		rinc= new double[cylinder.length];
		for(int i=0; i<cylinder.length; i++){
			double r= cylinder[i].getRadius();
			
			double a= Math.PI*r*r;
			
			double inc= K*a;
			
			rinc[i]=inc;
		}
				
		this.p=simParams.getP();
		
		//finally, redo the radius array to take account of the clones
		tempRadius=new double[cylinder.length];
		for(int i=0; i<Nbefore; i++){
			tempRadius[i]=radius[i];
		}
		
		for(int i=Nbefore; i<cylinder.length; i++){
			double[] pos= new double[D];
			double[] Pc= clones.get(i-Nbefore);
			
			for(int j=0; j<D; j++){
				pos[j]=Pc[j];
			}
			
			tempRadius[i]=Pc[D];
		}
		
		radius=tempRadius;
		
		/*logger.info("writing cylinder cross sections to csv file...");
		try{
		    BufferedWriter cylWriter= new BufferedWriter(new FileWriter("testCyls_"+cylinder.length+".csv"));
		    for(int i=0; i<cylinder.length; i++){
		        if((i==5)||(i==15)||(i==63)||(i==74)){
		            cylinder[i].drawCrossSection(cylWriter);
		        }
		    }
		    
		    cylWriter.flush();
		    cylWriter.close();
		    
		    cylWriter= new BufferedWriter(new FileWriter("cyl134.csv"));
		    
		    cylinder[134].drawCrossSection(cylWriter);
		    
		    cylWriter.flush();
            cylWriter.close();
		    
		}
		catch(IOException ioe){
		    throw new LoggedException(ioe);
		}
		logger.info("done");
		*/
		
		logger.info("initialising spatial optimisation");
        int[] n= new int[]{10, 10, 1};
        
        initialiseSpatialOptimisation(n);
        logger.info("done.");
		
	}

	/** 
	 * handle the clones around the cylinder edges
	 */
	protected void initClones(){
		
		// add the clones to the array of centres
		double[][] Pfull=new double[P.length+clones.size()][];
		
		for(int i=0; i<P.length; i++){
			Pfull[i]=new double[D];
			for(int j=0; j<D; j++){
				Pfull[i][j]=P[i][j];
			}
		}
		
		for(int i=0; i<clones.size(); i++){
			double[] clone=clones.get(i);
			
			Pfull[i+P.length]=clone;
		}
		
		// copy the new array (including clones) to the
		// cylinder centres array
		P=Pfull;
	}
	
	/**
	 * calculates the ditance between two given points
	 * projected into the plane perp to cylinder axis
	 * 
	 * @param p1 posn of first point
	 * @param p2 posn of second point
	 * 
	 * @return euclidean distance
	 */
	protected final double twoDdist(double[] p1, double[] p2){
		
		double sqDist=0.0;
		for(int i=0; i<2; i++){
			sqDist+=(p1[i]-p2[i])*(p1[i]-p2[i]);
		}
		
		return Math.sqrt(sqDist);
		
	}
		
	/** checks if a walker's step will take it across a membrane or not.
	 * this just involves checking every cylinder in turn. The cylinder
	 * crosses() method should fill in the blanks where necessary.
	 * 
	 * @param walker the walker to check
	 * @param stepVector the step being made
	 * @param normal space to store the normal if a barrier is crossed
	 * @param d space to store barrier distance from origin dotted with normal
	 * @param skipCurrent flag indicating we're sitting on a barrier and should 
	 *        ignore the closest one.
	 * @param originLength the original length of the step vector;
	 * 
	 * @return true or false
	 */
	/*public boolean crossesMembrane(Walker walker, double[] offset, double[] stepVector,
			double[] normal, double[] d, boolean skipCurrent, double origLength, boolean[] in, double[] p) {
		
		double len= 0.0;
		double[] walkerPos= new double[D];
		double[] intDist= new double[1];
		
		boolean[] intIn= new boolean[1];
		double[] intP= new double[1];
		
		getSubstrateCoords(walker.r, offset, walkerPos);
		
		for(int i=0; i<stepVector.length; i++){
			len += stepVector[i]*stepVector[i];
		}
		len=Math.sqrt(len);
		
		if(len/origLength<=1E-14){
			return false;
		}
			
		int numIntersections=0;
		boolean skip=skipCurrent;
		
		for(int i=0; i<cylinder.length; i++){
			
			if(skipCurrent){
				if(i==lastCrossed){
					intIn[0]=in[0];
					skip=true;
				}
				else{
					intIn[0]=cylinder[i].inside(walkerPos);
					skip= false;
				}				
			}
			
			if(cylinder[i].crosses(walkerPos, stepVector, normal, d, skip, origLength, intDist, intIn, intP)){
				
				shortestDist[numIntersections]=intDist[0];
				intOriginDist[numIntersections]=d[0];
				for(int j=0; j<D; j++){
					intNormal[numIntersections][j]=normal[j];
				}
				intInside[numIntersections]=intIn[0];
				cylCrossed[numIntersections]=i;
				cyl_p[numIntersections]=intP[0];
				
				numIntersections++;
				
			}
			
		}
		

		if(numIntersections==1){
			// if there's one interesection everything is  
			// as it should be so just copy the distance
			// and normal over and return true
			d[0]=intOriginDist[0];
			for(int i=0; i<D; i++){
				normal[i]=intNormal[0][i];
			}
			in[0]=intInside[0];
			
			p[0]=cyl_p[0];
			
			lastCrossed=cylCrossed[0];
			
			return true;
		}
		
		if(numIntersections>1){
			
			// if there are more than intersections
			// we must pick the one that would happen
			// first, and set up the distance and 
			// normal accordingly
			double closestDist=Double.MAX_VALUE;
			int closest=-1;
			
			// find closest interesection to the walker
			for(int i=0; i<numIntersections; i++){
				if(shortestDist[i]<closestDist){
					closestDist=shortestDist[i];
					closest=i;
					lastCrossed=cylCrossed[i];
				}
			}
			
			if(closest==-1){
				return false;
			}
			
			// set the distance and normal
			d[0]=intOriginDist[closest];
			for(int i=0; i<D; i++){
				normal[i]=intNormal[closest][i];
			}
			in[0]=intInside[closest];
			p[0]=cyl_p[closest];
			
			return true;
		}
		
		// in this case there are no intersections
		return false;
	}*/

	/**
	 * @return linear size of substrate
	 */
	public double[] getSubstrateSize() {
		// TODO Auto-generated method stub
		return L;
	}

	/** returns the centra of a square substrate
	 * 
	 * @return substrate size L/2.0
	 */
	public double getPeakCoord() {
		return L[0]/2;
	}

	/**
	 * initialise squashy cylinders of given radius at
	 * each centre specified by the P array
	 * 
	 */
	public void init() {
		
	    if(numIncrements!=1){
    		if((DiffusionSimulation.calls%inflammationIncrementModulus == 0)){
    		    
    			for(int i=0; i<cylinder.length; i++){
    				
    				// reinitialise cylinder either with new or same radius
    				if((cylinder[i]==null)||(cylinder[i].isExpanding())){
    					cylinder[i]=new SquashyCylinder(P[i], radius[i]+n*rinc[i], p);
    				}
    				else{
    					cylinder[i]= new SquashyCylinder(P[i], cylinder[i].getRadius(), p);
    					
    					// cylinders are expanding by default. if static in the previous
    					// iteration, must be static in this.
    					cylinder[i].stopExpanding();
    				}
    			}
    		}

			// reassemble clonal relationships
			for(int i=Nbefore; i<P.length; i++){
				int cloneOf= (int)P[i][D+1];
				
				cylinder[cloneOf].addToMyClones(cylinder[i]);
			}
			
			// propagate clone relationships
			for(int i=0; i<Nbefore; i++){
				cylinder[i].propagateCloneRelationships();
				
				// this might seem a little odd! we have to
				// propagate out the non-expanding flag to
				// the clones. when we set it before they
				// weren't affected by the call (but it needed 
				// to be made otherwise we'd have no record of
				// whether the cylinder is expanding or not)
				if(!cylinder[i].isExpanding()){
					cylinder[i].stopExpanding();
				}
			}
			
			
			
			for(int i=0; i<cylinder.length; i++){				
				// add intersections
				for(int j=0; j<i; j++){
					if(i==j){
						continue;
					}
									
					if(cylinder[i].abutts(cylinder[j])){
						if(!cylinder[i].hasIntersectionWith(cylinder[j])){
							cylinder[i].addIntersectionWith(cylinder[j]);
						}
						if(!cylinder[j].hasIntersectionWith(cylinder[i])){
							cylinder[j].addIntersectionWith(cylinder[i]);
						}
					}
				}

				for(int j=0; j<i; j++){
					Iterator<Chord> chIt=((SquashyCylinder)cylinder[j]).chords.iterator();
					
					while(chIt.hasNext()){
						Chord chord= (Chord)chIt.next();

						if(chord.tmin>=chord.tmax){
							System.err.println("added cylinder "+i+", cyl "+j+" has zero-length chord");
							System.err.println("tmin= "+chord.tmin+", tmax= "+chord.tmax);
						}
						
						
						if(Double.isNaN(chord.tmin)){
							System.err.println("added cylinder "+i+", cylinder "+j+" tmin is NaN");
						}
						if(Double.isNaN(chord.tmax)){
							System.err.println("added cylinder "+i+", cylinder "+j+" tmax is NaN");
						}
					}
				}
			}
						
			n++;
			logger.info("iteration "+DiffusionSimulation.calls+"  vol frac= "+intraCellularVolFrac());				

		}
		
		if(SimulationParams.sim_drawCrossSection){
		    try{
		        drawCrossSection();
		    }
		    catch(IOException ioe){
		        throw new LoggedException(ioe);
		    }
		}
		
		// read out the cylinder radii and positions
		if(substrateInfo){
    		logger.info("cylinder positions and radii:");
    		
    		String data= new String();
    		
    		for(int i=0; i<cylinder.length; i++){
    		    double[] pos= cylinder[i].getPosition();
    		    double radius= cylinder[i].getRadius();
    		    
    		    data+="cylinder "+i+" pos = ("+pos[0]+", "+pos[1]+", "+pos[2]+")\tradius = "+radius+"\n";
    		}
    		
    		logger.info("cylinder positions and radii:\n"+data);
    		
    		substrateInfo= false;
		}		
		
		lastCrossed=-1;
		
		DiffusionSimulation.calls++;
	}

	/** 
	 * tests if a position is intracellular or not.
	 * 
	 * @param walker walker whose position to test
	 * 
	 * @return true if inside a cylinder, otherwise false
	 */
	public boolean intracellular(Walker walker){
		
		double[] substrateCoords= new double[D];
		getSubstrateCoords(walker.r, new double[D], substrateCoords);
		
		for(int i=0; i<cylinder.length; i++){
			
			if(cylinder[i].inside(substrateCoords)){
				return true;
			}
		}
		
		return false;
	}

	
	
	/** numerically calculates the intracellular volume fraction of 
	 *  the substrate by checking a large number of points across the
	 *  cross section of the cylinders (xy-plane)
	 *  
	 *  @return intracellular volume fraction
	 */
	private double intraCellularVolFrac(){
		
		int pts= 100;
		
		double inc=(double)L[0]/(double)pts;
		
		int intraCellularPts=0;
		
		double[] point= new double[D];
		point[2]=0.0;
		
		for(int i=0; i<pts; i++){
			point[0]=i*inc;
			for(int j=0; j<pts; j++){
				point[1]=j*inc;
				
				for(int k=0; k<cylinder.length; k++){
					
					
					if(cylinder[k].inside(point)){
						intraCellularPts++;
						break;
					}
				}
			}
		}
		
		return ((double)intraCellularPts)/((double)(pts*pts));
	}
	
	
	/**
	 * check if a cylinder needs to be cloned
	 */
	private ArrayList<double[]> checkForCloning(double[] P, double radius, int i){
		
		ArrayList<double[]> added= new ArrayList<double[]>();
		
		for(int j=0; j<2; j++){
			// if cylinder lower side is close enough...
			if(P[j]-radius<=border[j]){
				double[] clone= new double[D+2];
				
				for(int k=0; k<D; k++){
					if(k==j){
						clone[k]=P[k]+(double)(L[k]);
					}
					else{
						clone[k]=P[k];
					}
				}
				// clone radius
				clone[D]=radius;
				
				// remember who you are a clone of
				clone[D+1]=i;
				
				//clones.add(clone);
				added.add(clone);
			}

			// if cylinder upper side is close enough...
			if(L[0]-(P[j]+radius)<=border[j]){
				double[] clone= new double[D+2];
				
				for(int k=0; k<D; k++){
					if(k==j){
						clone[k]=P[k]-L[k];
					}
					else{
						clone[k]=P[k];
					}

				}
				clone[D]=radius;
				clone[D+1]=i;
				
				added.add(clone);
			}
		}
		
		// check for corners
		// top-right
		if((L[0]-(P[0]+radius)<=border[0])&&(L[1]-(P[1]+radius)<=border[1])){
			double[] clone= new double[]{P[0]-(double)(L[0]), 
										 P[1]-(double)(L[1]), 
										 P[2], 
					 					 radius, 
					 					 i};
			
			added.add(clone);
		}
		// bottom-left
		if((P[0]-radius<=border[0])&&(P[1]-radius<=border[1])){
			double[] clone= new double[]{P[0]+(double)(L[0]), 
										 P[1]+(double)(L[1]), 
										 P[2], 
					 					 radius, 
					 					 i};
			
			added.add(clone);
		}
		// top-left
		if((L[0]-(P[0]+radius)<=border[0])&&(P[1]-radius<=border[1])){
			double[] clone= new double[]{P[0]-(double)(L[0]), 
					 					 P[1]+(double)(L[1]), 
					 					 P[2], 
					 					 radius,
					 					 i};

			added.add(clone);
		}
		// bottom-right
		if((P[0]-(radius)<=border[0])&&(L[1]-(P[1]+radius)<=border[1])){
			double[] clone= new double[]{P[0]+(double)L[0], 
					 					 P[1]-(double)L[1], 
					 					 P[2], 
					 					 radius,
					 					 i};

			added.add(clone);
		}
		
		return added;
	}
	
	
	
	/**
	 * writes the cross sections of each cylinder to a file.
	 * this is done by checking the intersections along the 
	 * edges of a fine grid defined over the substrate.
	 * 
	 *
	 */
	public final void drawCrossSection() throws IOException{
		
		logger.info("generating cylinder cross section image.");
		
		int pts=SimulationParams.sim_crossSectionImageSize;

		// distance between grid points
		double dx = (this.getSubstrateSize())[0]/pts;
		
		// step vectors along each grid line
		double[][] s= new double[][]{{ dx, 0.0, 0.0},
				                     {-dx, 0.0, 0.0},
				                     {0.0,  dx, 0.0},
				                     {0.0, -dx, 0.0}};
		
		// file output
		FileOutputStream fstream = null;
    	try{
            //String fname= new String("crossSec_"+DiffusionSimulation.calls+".gray");
    		String fname= SimulationParams.crossSectionFig;
    		fstream= new FileOutputStream(fname);
    	}
    	catch(Exception e){
    		throw new LoggedException(e);
    	}
    	
    	DataOutputStream out= new DataOutputStream(new BufferedOutputStream(fstream, 1024));
		
		double[] pos= new double[D];
		logger.info("instantiating non-simulation walker for cross section. ignore the following warning.");
		Walker walker= new Walker(pos);
		logger.info("non-simulation walker instantiated.");
		
		walker.testReplacePositionVector(pos);
		
		double[] offset = new double[D];
		double[] normal = new double[D];
		double[] d = new double[1];
		boolean[] in = new boolean[1];
		double[] pFake= new double[]{1.0};
		
		for(int i=0; i<pts; i++){
			
			// set x xoord
			pos[0]=dx*(i+0.5);
			
			System.err.print("\ri= "+i+"     ");
			for(int j=0; j<pts; j++){
				
				// set y coord
				pos[1]=dx*(j+0.5);
				
				//check intersection
				boolean crosses= false;
				
				for(int k=0; k<s.length; k++){
					
					crosses=crossesMembrane(walker, offset, s[k], normal, d, false, dx, in, pFake);
					
					if(crosses){
						break;
					}
				}
				
				if(crosses){
					out.writeByte(0);
				}
				else{
					if(intracellular(walker)){
						out.writeByte(-1);
					}
					else{
						out.write(-128);
					}
				}
			}
		}
		
		out.flush();
		out.close();
		
		System.err.println();
		
		logger.info("cylinder cross section image generated.");
	}
	
}
