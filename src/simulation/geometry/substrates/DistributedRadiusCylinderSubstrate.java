package simulation.geometry.substrates;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import misc.LoggedException;
import numerics.GammaRandom;
import numerics.MTRandom;

import simulation.DiffusionSimulation;
import simulation.SimulationParams;
import simulation.geometry.elements.BasicCylinder;
import simulation.geometry.elements.Cylinder;
import simulation.geometry.elements.CylinderFactory;
import tools.CL_Initializer;

/** 
 *  implements a class of substrate whereby the radii of cylinders
 * 	are drawn from a specified distribution. 
 * 
 *  Currently cylinder radii are drawn from a uniform distribution
 *  and are placed in a square packing with fixed separation R.
 *  
 *  TODO: In the future this class will also implement non-fixed 
 *  cylinder separations and radii drawn from arbitrary distributions
 *  
 * 
 * 
 * 
 * @author matt m.hall@cs.ucl.ac.uk
 *
 */
public class DistributedRadiusCylinderSubstrate extends CylinderSubstrate {

	/** logging object */
	private final Logger logger= Logger.getLogger(this.getClass().getName());
	
	/** dimensionality of space */
	private static final int D=DiffusionSimulation.D;
	
	/** size of substrate */
	private final double[] l;
	
	/** set of cylinder centres */
	private double[][] P;
	
	/** set of radii of cylinders */
	private double[] radius;
	
	/** border for cloning cylinders */
	private static final double border= DiffusionSimulation.border;
	
	/** place to store clones */
	private final ArrayList<double[]> clones=new ArrayList<double[]>();

	/** 
	 * constructor.
	 * 
	 * @param subsSize size of substrate
	 * @param N number of cylinders on the substrate
	 * @param k shape param for gamma distributed cylinder radii
	 * @param beta scale param for gamma distributed cylinder radii
	 * @param simParams simulation parameters object
	 * 
	 */
	public DistributedRadiusCylinderSubstrate(SimulationParams simParams){

		super(new double[]{SimulationParams.sim_L, SimulationParams.sim_L, SimulationParams.sim_L}, simParams, false);
				
		double k=CL_Initializer.gamma_k;
		double beta=CL_Initializer.gamma_beta;
		double subsSize= SimulationParams.sim_L;

		// set substrate size
		this.l=new double[]{subsSize, subsSize, subsSize};
		
		int N=SimulationParams.sim_cyl_dist_size;
		
		Cylinder[] cylinder= arrangeCylinders(k, beta, subsSize, N, simParams);
		
		// set the cylinder array in the superclass
		setCylinders(cylinder);
		
		logger.info("initialising spatial optimisation");
		int[] n= new int[]{10, 10, 1};
		
		initialiseSpatialOptimisation(n);
		logger.info("done.");
		
		//drawCrossSection();
	}
	
	
	protected final Cylinder[] arrangeCylinders(double k, double beta, double subsSize, int N, SimulationParams simParams){
		
		MTRandom rng= new MTRandom(CL_Initializer.seed);
		GammaRandom grng= new GammaRandom(CL_Initializer.seed+8273l, k, beta);
				
		
		if(N==0){
			logger.warning("number of cylinders is zero!");
		}
		
		// initialise radius array
		radius= new double[N];

		// initialise radii
		for(int i=0; i<N; i++){
			radius[i]= grng.nextGamma();
		}
		
		// check if cross sectional areas are compatible
		double cylArea=0.0;
		for(int i=0; i<radius.length; i++){
			cylArea+=Math.PI*radius[i]*radius[i];
		}
		
		double sqArea=l[0]*l[1];
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
		
		// array of cylinder positions
		P= new double[N][D];
		
		int maxInd=N;
		// place cylinders on substrate
		for(int i=0; i<radius.length; i++){
			boolean overlapping= true;
			int count=0;
			
			ArrayList<double[]> newClones=null;
			
			while(overlapping && (count<10000)){
				P[i][0]= l[0]*rng.nextDouble();
				P[i][1]= l[1]*rng.nextDouble();
				
				overlapping=false;
				
				// check against cylinders already placed
				for(int j=0; j<i; j++){
					double sqDist= (P[i][0]-P[j][0])*(P[i][0]-P[j][0]) + (P[i][1]-P[j][1])*(P[i][1]-P[j][1]);
					if(sqDist<(radius[i]+radius[j])*(radius[i]+radius[j])){
						overlapping=true;
						break;
					}
				}
				
				// check against clones
				for(int j=0; j<clones.size(); j++){
					double[] Pc=clones.get(j);
					double sqDist= (P[i][0]-Pc[0])*(P[i][0]-Pc[0]) + (P[i][1]-Pc[1])*(P[i][1]-Pc[1]);
					
					if(sqDist<(radius[i]+Pc[D])*(radius[i]+Pc[D])){
						overlapping=true;
						break;
					}
					
				}

				// check if the new cylinder needs cloning, 
				// and check the clones for overlaps
				newClones=checkForCloning(P[i], radius[i]);

				for(int j=0; j<newClones.size(); j++){
					double[] Pc= newClones.get(j);
					double r= Pc[D];
					
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
				logger.warning("could only place "+(i+1)+" of "+ N+" cylinders on substrate");
				maxInd=i;
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
		int Nbefore=maxInd;
		
		// clone the cylinders around the edge (instantiates the cylinder array)
		initClones();

		int numCyls=Nbefore+clones.size();
		
		cylinder= new Cylinder[numCyls];
		
		// instantiate the placed cylinders		
		for(int i=0; i<Nbefore; i++){
			//cylinder[i]= new BasicCylinder(P[i], radius[i], SimulationParams.sim_p);
			cylinder[i]= CylinderFactory.getCylinder(P[i], radius[i], simParams);
		}
		
		
		//instantiate the clones
		for(int i=Nbefore; i<cylinder.length; i++){
			double[] pos= new double[D];
			double[] Pc= clones.get(i-Nbefore);
			
			for(int j=0; j<D; j++){
				pos[j]=Pc[j];
			}
			
			//cylinder[i]=new BasicCylinder(pos, Pc[D], simParams.sim_p);
			cylinder[i]= CylinderFactory.getCylinder(pos, Pc[D], simParams);
		}
		
		
		return cylinder;
		
	}
	
	
	/** returns the size of the substrate (N*R) 
	 */
	public double[] getSubstrateSize() {
		return l;
	}


	/** returns the coord for the delta peak (centre of substrate)
	 */
	public double getPeakCoord() {
		return l[0]/2;
	}

	
	/**
	 * check if a cylinder needs to be cloned
	 */
	private ArrayList<double[]> checkForCloning(double[] P, double radius){
		
		ArrayList<double[]> added= new ArrayList<double[]>();
		
		for(int j=0; j<2; j++){
			// if cylinder lower side is close enough...
			if(P[j]-radius<=border){
				double[] clone= new double[D+1];
				
				for(int k=0; k<D; k++){
					if(k==j){
						clone[k]=P[k]+(double)(L[k]);
					}
					else{
						clone[k]=P[k];
					}
				}
				clone[D]=radius;
				
				//clones.add(clone);
				added.add(clone);
			}

			// if cylinder upper side is close enough...
			if(L[0]-(P[j]+radius)<=border){
				double[] clone= new double[D+1];
				
				for(int k=0; k<D; k++){
					if(k==j){
						clone[k]=P[k]-(double)(L[k]);
					}
					else{
						clone[k]=P[k];
					}

				}
				clone[D]=radius;
				
				//clones.add(clone);
				added.add(clone);
			}
		}
		
		// check for corners
		// top-right
		if((L[0]-(P[0]+radius)<=border)&&(L[1]-(P[1]+radius)<=border)){
			double[] clone= new double[]{P[0]-(double)(L[0]), 
										 P[1]-(double)(L[1]), 
										 P[2], 
					 					 radius};
			//clones.add(clone);
			added.add(clone);
		}
		// bottom-left
		if((P[0]-radius<=border)&&(P[1]-radius<=border)){
			double[] clone= new double[]{P[0]+(double)(L[0]), 
										 P[1]+(double)(L[1]), 
										 P[2], 
					 					 radius};
			//clones.add(clone);
			added.add(clone);
		}
		// top-left
		if((L[0]-(P[0]+radius)<=border)&&(P[1]-radius<=border)){
			double[] clone= new double[]{P[0]-(double)(L[0]), 
					 					 P[1]+(double)(L[1]), 
					 					 P[2], 
					 					 radius};
			//clones.add(clone);
			added.add(clone);
		}
		// bottom-right
		if((P[0]-(radius)<=border)&&(L[1]-(P[1]+radius)<=border)){
			double[] clone= new double[]{P[0]+(double)L[0], 
					 					 P[1]-(double)L[1], 
					 					 P[2], 
					 					 radius};
			//clones.add(clone);
			added.add(clone);
		}
		
		return added;
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
		
		// initialise the cylinders array
		cylinder= new Cylinder[P.length];
	}

	/**
	 * writes the cross sections of each cylinder to a file
	 *
	 */
	public final void drawCrossSection(){
		
		int Nincs=20;
		
		BufferedWriter out;
		
		try{
			out= new BufferedWriter(new FileWriter("gamma_cyls.csv"));
		
			
			for(int i=0; i<cylinder.length; i++){
				
				double r= cylinder[i].getRadius();
				for(int t=0; t<Nincs; t++){
					double theta1= (((double)t)/((double)Nincs))*2.0*Math.PI;
					double theta2= (((double)(t+1))/((double)Nincs))*2.0*Math.PI;
					
					double x1= P[i][0]+r*Math.cos(theta1);
					double y1= P[i][1]+r*Math.sin(theta1);
					
					double x2= P[i][0]+r*Math.cos(theta2);
					double y2= P[i][1]+r*Math.sin(theta2);
					
					out.write(x1+","+y1+"\n");
					out.write(x2+","+y2+"\n");
				}
			}
			
			out.flush();
			out.close();
			
		}
		catch(IOException ioe){
			throw new RuntimeException(ioe);
		}
	}
		
}
