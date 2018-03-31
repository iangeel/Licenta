package ro.ase.angel.licenta1.Utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by angel on 16.03.2018.
 */

public class Users implements Parcelable {
    private String globalId;
    private String userName;
    private String password;
    private List<Records> records;

    public Users() {
    }

    public Users(String globalId, String userName, String password) {
        this.globalId = globalId;
        this.userName = userName;
        this.password = password;
    }

    public Users(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public Users(Parcel parcel) {
        this.userName = parcel.readString();
        this.password = parcel.readString();
    }

    public Users(String userName, String password, List<Records> records) {
        this.userName = userName;
        this.password = password;
        this.records = records;
    }

    public Users(String globalId, String userName, String password, List<Records> records) {
        this.globalId = globalId;
        this.userName = userName;
        this.password = password;
        this.records = records;
    }

    public String getGlobalId() {
        return globalId;
    }

    public List<Records> getRecords() {
        return records;
    }

    public void setRecords(List<Records> records) {
        this.records = records;
    }

    public void setGlobalId(String globalId) {
        this.globalId = globalId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public static final Parcelable.Creator CREATOR = new Creator<Users>() {
        @Override
        public Users createFromParcel(Parcel parcel) {
            return new Users(parcel);
        }

        @Override
        public Users[] newArray(int i) {
            return new Users[i];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.getUserName());
        parcel.writeString(this.getPassword());
    }
}
