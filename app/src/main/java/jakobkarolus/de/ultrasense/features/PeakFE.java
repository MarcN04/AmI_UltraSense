package jakobkarolus.de.ultrasense.features;

import java.util.List;

/**
 * A FeatureExtractor implementing linear least-square approx using a Gaussian curve
 *
 * <br><br>
 * Created by Jakob on 02.07.2015.
 */
public class PeakFE extends FeatureExtractor{
    /**
     * creates a new GaussianFE with the given id.<br>
     * The id used to discern different FEs when passing their feature to the FeatureProcessor.
     *
     * @param id identifier for this specific FeatureExtractor
     */
    public PeakFE(int id) {
        super(id);
    }

    @Override
    public Feature onPeakFeatureDetected(UnrefinedFeature uF){
        return fitPeak(uF);
    }


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
        //double featureLength = uF.getEndTime() -uF.getStartTime();


        //System.out.println(featureLength);

        if(uF.getUnrefinedFeature().size() <= 1)
            return null;

        List<Double> arrayOfSum = uF.getUnrefinedFeature();

        double featureLength = (double) arrayOfSum.size() * 1024 * 1/44100;
        System.out.println("LENGTH" + featureLength);
        double featureMax = arrayOfSum.get(0);
        double featureIntegral = 0;

        for(int i=0; i < arrayOfSum.size(); i++) {

            System.out.println("ARRAYVALUE" + arrayOfSum.get(i));
            if(arrayOfSum.get(i)>featureMax)
                featureMax = arrayOfSum.get(i);


            featureIntegral += arrayOfSum.get(i);
        }

        //******* TO BE IMPLEMENTED
        System.out.println("length:" + featureLength + "max:" + featureMax + "integral:" + featureIntegral);

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
