package com.partymaker.roadsign;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladimir on 22.10.16.
 */

public class Segment extends ArrayList {
    List<Segment> linkedSegments = new ArrayList<>();

    public List<Segment> getLinkedSegments() {
        return linkedSegments;
    }

    public void addSegment(Segment segment){
        linkedSegments.add(segment);
    }
}
