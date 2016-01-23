package jakobkarolus.de.ultrasense.features;

/**
 * A FeatureExtractor implementing linear least-square approx using a Gaussian curve
 *
 * <br><br>
 * Created by Jakob on 02.07.2015.
 ***/
public class PulseFE extends FeatureExtractor {
    /**
     * creates a new GaussianFE with the given id.<br>
     * The id used to discern different FEs when passing their feature to the FeatureProcessor.
     *
     * @param id identifier for this specific FeatureExtractor
     */
    public PulseFE(int id) {
        super(id);
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

    private PulseFeature fitPulse(UnrefinedFeature uF){

        if(uF.getEndTime() -uF.getStartTime() <= 0) {
            return null;
        }
        double featureLength = uF.getEndTime() -uF.getStartTime();

        if(uF.getUnrefinedFeature().size() <= 1)
            return null;

        double[] arrayOfSum = toArray(uF.getUnrefinedFeature());

        //******* implement Octave-logic with Weka results

        return new PulseFeature(getId(), featureLength, featureMax, feautreIntegral);
    }

}

}
