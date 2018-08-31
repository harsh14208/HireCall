package app.com.thetechnocafe.hirecall.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by rvkmr on 26-10-2017.
 */

public class CompanyHeadModel implements Parcelable {
    private String domain;
    private List<String> heads = null;

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public List<String> getHeads() {
        return heads;
    }

    public void setHeads(List<String> heads) {
        this.heads = heads;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.domain);
        dest.writeStringList(this.heads);
    }

    public CompanyHeadModel() {
    }

    protected CompanyHeadModel(Parcel in) {
        this.domain = in.readString();
        this.heads = in.createStringArrayList();
    }

    public static final Creator<CompanyHeadModel> CREATOR = new Creator<CompanyHeadModel>() {
        @Override
        public CompanyHeadModel createFromParcel(Parcel source) {
            return new CompanyHeadModel(source);
        }

        @Override
        public CompanyHeadModel[] newArray(int size) {
            return new CompanyHeadModel[size];
        }
    };
}
