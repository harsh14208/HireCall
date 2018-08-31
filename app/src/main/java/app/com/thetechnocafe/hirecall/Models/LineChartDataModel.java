package app.com.thetechnocafe.hirecall.Models;

/**
 * Created by gurleen on 23/5/17.
 */

public class LineChartDataModel {
    private String tag;
    private int count;
    private int position;

    public LineChartDataModel(String tag, int count, int position) {
        this.tag = tag;
        this.count = count;
        this.position = position;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
