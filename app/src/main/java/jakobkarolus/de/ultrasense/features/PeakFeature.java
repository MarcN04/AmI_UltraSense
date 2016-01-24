package jakobkarolus.de.ultrasense.features;

/**
 * Feature represented by a Gaussian curve
 *<br><br>
 * Created by Jakob on 02.07.2015.
 */
public class PeakFeature extends Feature{

    private double length;
    private double max;
    private double integral;


    public PeakFeature(int extractorId, double _length, double _max, double _integral) {
        super(extractorId);
        this.length = _length;
        this.max = _max;
        this.integral = _integral;
    }
    //********** not needed any more only implemented because of root class Feature
    @Override
    public double getMax() {
        return 0;
    }
    //********** not needed any more only implemented because of root class Feature
    @Override
    public double getIntegral() {
        return 0;
    }

    @Override
    public double getLength() {
        return length;
    }

}
