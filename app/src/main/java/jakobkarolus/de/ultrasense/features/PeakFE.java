package jakobkarolus.de.ultrasense.features;

import java.util.List;

/**
 * A FeatureExtractor implementing linear least-square approx using a Gaussian curve
 *
 * <br><br>
 * Created by Jakob on 02.07.2015.
 */
public class PeakFE extends FeatureExtractor{

    public PeakFE(int id) {
        super(id);
    }

    @Override
    public Feature onPeakFeatureDetected(UnrefinedFeature uF){
        return fitPeak(uF);
    }
    /**
     * creates a new GaussianFE with the given id.<br>
     * The id used to discern different FEs when passing their feature to the FeatureProcessor.
     *
     * @param id identifier for this specific FeatureExtractor
     */

    //********** not needed any more only implemented because of root class FeatureExtractor
    @Override
    public Feature onHighFeatureDetected(UnrefinedFeature uF) {
        return null;
    }

    @Override
    public Feature onLowFeatureDetected(UnrefinedFeature uF) {

        return null;
    }

    private PeakFeature fitPeak(UnrefinedFeature uF){

        if(uF.getEndTime() -uF.getStartTime() <= 0) {
            return null;
        }
        double featureLength = uF.getEndTime() -uF.getStartTime();

        if(uF.getUnrefinedFeature().size() <= 1)
            return null;

        List<Double> arrayOfSum = uF.getUnrefinedFeature();
        double featureMax = 0;
        double featureIntegral = 0;

        //******* TO BE IMPLEMENTED

        /***
         * Octave-Script
         *
         * for i = 1:m
         *  vecA = [vecA sum(S(:,i))];
         *
         *  if (vecA(i) > -35000)
         *      peakDuration = peakDuration + 1;
         *      peakIntegral = peakIntegral + vecA(i);
         *  end
         * end
         * peakMaximum = max(vecA);
         * peakDuration = peakDuration * 1024 * 1/fs;
         **/


        return new PeakFeature(getId(), featureLength, featureMax, featureIntegral);
    }

}
