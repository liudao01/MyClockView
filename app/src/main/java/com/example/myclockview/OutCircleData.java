package com.example.myclockview;

/**
 * Created by liuml on 2021/10/28 10:40
 */
public class OutCircleData {

    private float startAngle;
    private float endAngle;
    private float sweepAngle;//扫过的角度
    private int color;
    private String name;


    public float getSweepAngle() {
        return sweepAngle;
    }

    public void setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }


    public float getEndAngle() {
        return endAngle;
    }

    public void setEndAngle(float endAngle) {
        this.endAngle = endAngle;
    }

    public float getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(float startAngle) {
        this.startAngle = startAngle;
    }

    public void setEndAngle(int endAngle) {
        this.endAngle = endAngle;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "OutCircleData{" +
                "startAngle=" + startAngle +
                ", endAngle=" + endAngle +
                ", color=" + color +
                ", name='" + name + '\'' +
                '}';
    }
}