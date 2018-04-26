package ro.ase.angel.licenta1.Utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.concurrent.TimeUnit;

/**
 * Created by angel on 26.03.2018.
 */

public class Records implements Parcelable {
    private String globalId;
    private Integer pulse;
    private Float speed;
    private Long time;
    private String userGlobalId;


    public Records() {
    }



    public Records(Integer pulse, Float speed) {
        this.pulse = pulse;
        this.speed = speed;
    }

    public Records(Integer pulse, Float speed, Long time) {
        this.pulse = pulse;
        this.speed = speed;
        this.time = time;
    }

    public Records(Integer pulse, Float speed, Long time, String userGlobalId) {
        this.pulse = pulse;
        this.speed = speed;
        this.time = time;
        this.userGlobalId = userGlobalId;
    }

    //    public Records(String globalId, Integer pulse, Float speed, Long time) {
//        this.globalId = globalId;
//        this.pulse = pulse;
//        this.speed = speed;
//        this.time = time;
//    }

    public Records(Parcel parcel) {
        this.pulse = parcel.readInt();
        this.speed = parcel.readFloat();
        this.time = parcel.readLong();
    }

    public String getUserGlobalId() {
        return userGlobalId;
    }

    public void setUserGlobalId(String userGlobalId) {
        this.userGlobalId = userGlobalId;
    }

    public Integer getPulse() {
        return pulse;
    }

    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
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
        parcel.writeInt(this.pulse);
        parcel.writeFloat(this.speed);
        parcel.writeLong(this.time);
    }
}
