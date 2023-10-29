package main.remind;

import javax.swing.*;
import java.util.*;

class ReminderGroup {
	JTextField titleField;
    ArrayList<SegmentGroup> segmentGroups;
	
	public ReminderGroup(JTextField titleField, ArrayList<SegmentGroup> segmentGroups){
        this.titleField = titleField;
        this.segmentGroups = segmentGroups;
	}
}