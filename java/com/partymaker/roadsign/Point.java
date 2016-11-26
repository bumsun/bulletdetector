package com.partymaker.roadsign;

import java.util.List;

/**
 * Created by vladimir on 22.10.16.
 */

public class Point {
    private Integer x;
    private Integer y;
    private List<Point> segment;

    public Point(Integer x, Integer y, List<Point> segment){
        this.x = x;
        this.y = y;
        this.segment = segment;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Point> getSegment() {
        return segment;
    }

    public void setSegment(List<Point> segment) {
        this.segment = segment;
    }
}
