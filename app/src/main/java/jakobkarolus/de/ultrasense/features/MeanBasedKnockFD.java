package jakobkarolus.de.ultrasense.features;

import java.util.ArrayList;
import java.util.List;

import jakobkarolus.de.ultrasense.algorithm.AlgoHelper;

/**
 * FeatureDetector using a mean-based scheme per timestep to detect Features.<br>
 * Implements a STFT to get the power of frequency value per timestep
 *
 * <br><br>
 * Created by Jakob on 02.07.2015.
 */
public class MeanBasedKnockFD extends FeatureDetector {

    private int fftLength;
    private int hopSize;
    private double[] win;
    private double windowAmp;
    private double[] carryOver;
    private boolean carryAvailable;


    /**
     * creates a new MeanBasedFD with the given feature detection parameters
     *
     * @param featureProcessor the FeatureProcessor associated with this FeatureDetector
     * @param sampleRate       sampleRate of the signal
     * @param fftLength        fft length to use
     * @param hopSize          hop size during fft procession
     * @param win              fft window to use
     */
    public MeanBasedKnockFD(FeatureProcessor featureProcessor, double sampleRate, int fftLength, int hopSize, double[] win) {
        super((double) hopSize / sampleRate, featureProcessor);
        this.fftLength = fftLength;
        this.hopSize = hopSize;
        this.carryOver = new double[hopSize];
        this.win = win;
        this.windowAmp = AlgoHelper.sumWindowNorm(win);
        this.carryAvailable = false;
    }

    @Override
    public String printParameters() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("FD parameters:\n");
        buffer.append("not needed after transformation for knock detection!");

        return buffer.toString();
    }

    @Override
    public void checkForFeatures(double[] audioBuffer, boolean applyHighPass) {

        if (applyHighPass)
            AlgoHelper.applyHighPassFilter(audioBuffer);

        double[] tempBuffer;
        //buffer is assumed to be a multiple of 4096, plus added hopSize from the previous buffer
        if (carryAvailable) {
            tempBuffer = new double[audioBuffer.length + hopSize];
            System.arraycopy(carryOver, 0, tempBuffer, 0, hopSize);
            System.arraycopy(audioBuffer, 0, tempBuffer, hopSize, audioBuffer.length);
            //save the carry-over for the next buffer
            System.arraycopy(audioBuffer, audioBuffer.length - hopSize, carryOver, 0, hopSize);
        } else {
            tempBuffer = new double[audioBuffer.length];
            System.arraycopy(audioBuffer, 0, tempBuffer, 0, audioBuffer.length);
            //save the carry-over for the next buffer
            System.arraycopy(audioBuffer, audioBuffer.length - hopSize, carryOver, 0, hopSize);
            carryAvailable = true;

        }
//
        double[] buffer = new double[fftLength];

        for (int i = 0; i <= tempBuffer.length - fftLength; i += hopSize) {
            System.arraycopy(tempBuffer, i, buffer, 0, fftLength);

            increaseTime();
            double[] values = AlgoHelper.fftMagnitude(buffer, win, windowAmp);//speichern ergibt
            double valuesSum = 0;
            for (int j = 0; j < values.length; j++) {
                valuesSum += values[j];
            }
            //          System.out.println(valuesSum);
            processFeatureValue(getCurrentPeakFeature(), valuesSum);
        }

    }

    List<Double> arrayCash = new ArrayList();


   /* private void processFeatureValue(UnrefinedFeature uF, double valueSum) {
        //feature berechnung wie octave

        if (valueSum >= -200000) {
            if (!uF.hasStarted()) {
                //start a new feature
                uF.setHasStarted(true);
                uF.setStartTime(getTime());

                uF.addTimeStep(valueSum);
            } else {
                //reset slack
                uF.addTimeStep(valueSum);
            }
        } else {
            if (uF.hasStarted()) {

                if (arrayCash.size() < 5) {

                    arrayCash.add(valueSum);

                    if (valueSum <= -250000) {


                        uF.setHasStarted(false);
                        uF.setEndTime(getTime() - getTimeIncreasePerStep());

                        for (int i = 0; i < arrayCash.size(); i++) {
                            uF.addTimeStep(arrayCash.get(i));
                        }

                        notifyFeatureDetectedPeak();
                        arrayCash.clear();
                        // System.out.println(uF.getEndTime() - uF.getStartTime());
                    }
                } else {

                    uF.setHasStarted(false);
                    uF.setEndTime(getTime() - getTimeIncreasePerStep());
                    notifyFeatureDetectedPeak();
                    arrayCash.clear();
                }
            }
            // }
        }
    }
}*/







   private void processFeatureValue(UnrefinedFeature uF, double valueSum) {
        //feature berechnung wie octave
        if(valueSum >= -210000){
            if(!uF.hasStarted()){
                //start a new feature
                uF.setHasStarted(true);
                uF.setStartTime(getTime());

                uF.addTimeStep(valueSum);
            }
            else{
                //reset slack
                uF.addTimeStep(valueSum);
            }
        }
        else{
            if(uF.hasStarted()){


            //is it also below the low threshold
                uF.setHasStarted(false);
                uF.setEndTime(getTime() - getTimeIncreasePerStep());
                System.out.println(uF.getEndTime() - uF.getStartTime());
                notifyFeatureDetectedPeak();
                }
                else{
                    //below threshold is fine for already started features
                    uF.addTimeStep(valueSum);
                }
            }
    }
}
