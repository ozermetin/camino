package tractography;


/**
 * Interface for a collection of distinct regions of interest. 
 *
 * @author Philip Cook
 * @version $Id: ROI_Collection.java 1020 2012-01-17 21:19:19Z ucacpco $
 */
public interface ROI_Collection {


    /**
     * @return the number of regions in this collection.
     * 
     */
    public int numberOfRegions();


    /**
     * Get a specific ROI.
     *
     * @param index the index of the required region, 
     * where <code>-1 < index < numberOfRegions() </code>.
     */
    public RegionOfInterest getRegion(int index);


    /**
     * Get all regions in an array.
     *
     */
    public RegionOfInterest[] getAllRegions();
    
}
