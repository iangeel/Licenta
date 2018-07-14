package ro.ase.angel.licenta1.Utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by ANGEL on 7/14/2018.
 */

public class ValuesOfInterest implements Parcelable{
    private int[] pulseValuesOfInterest;
    private float[] speedValuesOfInterest;
    private List<Double> latitudes;
    private List<Double> longitudes;

    public ValuesOfInterest() {
    }

    public ValuesOfInterest(int[] pulseValuesOfInterest, float[] speedValuesOfInterest, List<Double> latitudes, List<Double> longitudes) {
        this.pulseValuesOfInterest = pulseValuesOfInterest;
        this.speedValuesOfInterest = speedValuesOfInterest;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
    }

    public ValuesOfInterest(Parcel parcel) {
        this.pulseValuesOfInterest = parcel.createIntArray();
        this.speedValuesOfInterest = parcel.createFloatArray();
        this.latitudes = parcel.readArrayList(Double.class.getClassLoader());
        this.longitudes = parcel.readArrayList(Double.class.getClassLoader());
    }

    public int[] getPulseValuesOfInterest() {
        return pulseValuesOfInterest;
    }

    public void setPulseValuesOfInterest(int[] pulseValuesOfInterest) {
        this.pulseValuesOfInterest = pulseValuesOfInterest;
    }

    public float[] getSpeedValuesOfInterest() {
        return speedValuesOfInterest;
    }

    public void setSpeedValuesOfInterest(float[] speedValuesOfInterest) {
        this.speedValuesOfInterest = speedValuesOfInterest;
    }

    public List<Double> getLatitudes() {
        return latitudes;
    }

    public void setLatitudes(List<Double> latitudes) {
        this.latitudes = latitudes;
    }

    public List<Double> getLongitudes() {
        return longitudes;
    }

    public void setLongitudes(List<Double> longitudes) {
        this.longitudes = longitudes;
    }

    public static final Parcelable.Creator<ValuesOfInterest> CREATOR = new Creator<ValuesOfInterest>() {
        @Override
        public ValuesOfInterest createFromParcel(Parcel parcel) {
            return new ValuesOfInterest(parcel);
        }

        @Override
        public ValuesOfInterest[] newArray(int i) {
            return new ValuesOfInterest[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.pulseValuesOfInterest);
        dest.writeFloatArray(this.speedValuesOfInterest);
        dest.writeList(this.latitudes);
        dest.writeList(this.longitudes);
    }
}
