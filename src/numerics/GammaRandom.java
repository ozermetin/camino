package numerics;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Logger;


/**
 * generates gamma-distributed random numbers using a
 * mersienne twister and a rejection-sampling technique
 *
 *
 * @author matt (m.hall@cs.ucl.ac.uk)
 *
 */
public class GammaRandom {

	/** logging object */
	private final Logger logger= Logger.getLogger(this.getClass().getName());
	
	/** twister, for uniform distributed random numbers */
	private final MTRandom rng;
	
	/** shape parameter of distribution */
	private final double k;
	
	/** inverse scale parameter */
	private final double beta;
	
	/** integer part of shape parameter */
	private final int kFloor;
	
	/** non-integer part of k */
	private final double delta;
	
	/**
	 * constructor. instantiates rng and sets distribution parameters.
	 * 
	 * @param seed rng seed
	 * @param k distribution shape parameter
	 * @param beta distribution inverse-scale parameter (beta=1/theta) 
	 */
	public GammaRandom(long seed, double k, double beta){
		this.rng= new MTRandom(seed);
		this.k=k;
		this.kFloor=(int)Math.floor(k);
		this.delta=k-kFloor;
		this.beta=beta;
	}
	
	
	/**
	 * returns a sample drawn from the gamma distribution given by
	 * k and beta. This uses the summation rule:
	 * 
	 * sum_i=1^n X_n ~ Gamma(n, 1)
	 * where X_n~Gamma(1,1)
	 * 
	 * along with the scale property
	 * tX~Gamma(1,t)
	 * 
	 * and a rejection-sampling method for the non-integer part of 
	 * k.
	 * 
	 * Gamma(1,1) numbers are generated from the property that if U
	 * is uniformly distributed on [0,1), then -lnU ~ Gamma(1,1)
	 * 
	 * @return gamma distributed random number
	 */
	public double nextGamma(){
		
		// construct X~Gamma(kFloor, 1)
		double X=0.0;
		double exa=0.0;;
		
		for(int i=0; i<kFloor; i++){
			double U=getUniformNonZeroValue();
		
			X+=-Math.log(U);
		}

		if(delta>0.0){
			// now construct non-integer part of k
			double v0= Math.E/(Math.E+delta);
			boolean done=false;

			while(!done){
				double v1= getUniformNonZeroValue();
				double v2= getUniformNonZeroValue();
				double v3= getUniformNonZeroValue();
		
				double zeta;
		
				if(v1<v0){
					exa=Math.pow(v2, 1.0/delta);
					zeta=v3*Math.pow(exa, delta-1.0);
				}
				else{
					exa=1.0-Math.log(v2);
					zeta=v3*Math.exp(-exa);
				}
			
				if(zeta>Math.pow(exa, delta-1.0)*Math.exp(-exa)){
					done=true;
				}
			
			}
		}
		// add integer and non-integer parts for unscaled distrn
		double Gk1= Math.max(0.0, exa+X);
		
		// scale Gk1 to get full distribution value
		return beta*Gk1;
	}
	
	/**
	 * generates a uniformly distrubuted random number from
	 * the interval [0,1). This is basically the same as the 
	 * rng.nextDouble() method, but rejects any zeros that 
	 * crop up.
	 * 
	 * @return value uniformly distributed on [0,1)
	 */
	private double getUniformNonZeroValue(){
		double U=0.0;
		
		while(U==0.0){
			U=rng.nextDouble();
		}
		
		return U;
	}
	
	
	/**
	 * returns the weight for the specified bin in the specified distribution,
	 * weighted by bin width.
	 * 
	 * @param k shape parameter
	 * @param beta scale parameter
	 * @param xmin lower end of bin
	 * @param xmax upper end of bin
	 * 
	 * @return integral over range 
	 */
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		int samples=100000;
		
		int bins=1000;
		double[] distrn= new double[bins];
		
		double min=Double.MAX_VALUE;
		double max=0.0;
		
		//double binsize=(max-min)/bins;
				
		
		GammaRandom grng= new GammaRandom(34756037485l, 2.331206, 6.909243E-7);
		
		System.err.print("generating and binning "+samples+" numbers... ");
		
		for(int i=0; i<samples; i++){
			double num= grng.nextGamma();
			
			if(num<min){
				min=num;
			}
			if(num>max){
				max=num;
			}
		}

		double binsize=(max-min)/bins;
		for(int i=0; i<samples; i++){
						
			double num= grng.nextGamma();
			
			int bin=Math.min((int)(num/binsize), bins-1);
			
			if(bin==-1){
				System.err.println("i="+i+" bin is -1. that shouldn't happen...");
			}
			
			distrn[bin]++;
		}

		
		
		
		System.err.println("done");
		
		System.err.print("outputting distributiion... ");
		
		FileWriter writer;
		
		try{
			writer= new FileWriter("gammaDist.csv");
			
			for(int i=0; i<bins; i++){
				writer.write((i*binsize)+","+distrn[i]+"\n");
			}
			
			writer.flush();
			writer.close();
		}
		catch(IOException ioe){
			throw new RuntimeException(ioe);
		}
		
		System.err.println("done");

	}

}
