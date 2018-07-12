package ro.ase.angel.licenta1.Utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by angel on 26.03.2018.
 */

public class Records implements Parcelable {
    private String globalId;
    private List<Integer> pulse;
    private List<Float> speed;
    private List<Double> latitudes;
    private List<Double> longitudes;
    private Long time;
    private String userGlobalId;


    public Records() {
    }



    public Records(List<Integer> pulse, List<Float> speed) {
        this.pulse = pulse;
        this.speed = speed;
    }

    public Records(List<Integer> pulse, List<Float> speed, Long time) {
        this.pulse = pulse;
        this.speed = speed;
        this.time = time;
    }

    public Records(List<Integer> pulse, List<Float> speed, Long time, List<Double> latitudes,
                   List<Double> longitudes, String userGlobalId) {
        this.pulse = pulse;
        this.speed = speed;
        this.time = time;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
        this.userGlobalId = userGlobalId;
    }

    //    public Records(String globalId, Integer pulse, Float speed, Long time) {
//        this.globalId = globalId;
//        this.pulse = pulse;
//        this.speed = speed;
//        this.time = time;
//    }

    public Records(Parcel parcel) {
        this.pulse = parcel.readArrayList(Integer.class.getClassLoader());
        this.speed = parcel.readArrayList(Float.class.getClassLoader());
        this.time = parcel.readLong();
        this.latitudes = parcel.readArrayList(Double.class.getClassLoader());
        this.longitudes = parcel.readArrayList(Double.class.getClassLoader());
    }

    public String getUserGlobalId() {
        return userGlobalId;
    }

    public void setUserGlobalId(String userGlobalId) {
        this.userGlobalId = userGlobalId;
    }

    public List<Integer> getPulse() {
        return pulse;
    }

    public void setPulse(List<Integer> pulse) {
        this.pulse = pulse;
    }

    public List<Float> getSpeed() {
        return speed;
    }

    public void setSpeed(List<Float> speed) {
        this.speed = speed;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getGlobalId() {
        return globalId;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
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

    public String timeFormat(Long millis) {
        return String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
    }

    public static final Parcelable.Creator<Records> CREATOR = new Creator<Records>() {
        @Override
        public Records createFromParcel(Parcel parcel) {
            return new Records(parcel);
        }

        @Override
        public Records[] newArray(int i) {
            return new Records[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(this.pulse);
        parcel.writeList(this.speed);
        parcel.writeLong(this.time);
        parcel.writeList(this.latitudes);
        parcel.writeList(this.longitudes);
    }
}
