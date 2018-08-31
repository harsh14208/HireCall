package app.com.thetechnocafe.hirecall.Models;

/**
 * Created by rvkmr on 17-10-2017.
 */

public class CandidateSubFilterModel {
    String subFilterName;
    Boolean subFilterChecked;

    public CandidateSubFilterModel(String subFilterName, Boolean subFilterChecked) {
        this.subFilterName = subFilterName;
        this.subFilterChecked = subFilterChecked;
    }

    public String getSubFilterName() {
        return subFilterName;
    }

    public void setSubFilterName(String subFilterName) {
        this.subFilterName = subFilterName;
    }

    public Boolean getSubFilterChecked() {
        return subFilterChecked;
    }

    public void setSubFilterChecked(Boolean subFilterChecked) {
        this.subFilterChecked = subFilterChecked;
    }
}
