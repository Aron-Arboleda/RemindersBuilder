package main.remind;

import java.util.*;

public class Reminder{
    String title;
    ArrayList<Segment> segments;
    String dateMade;

    Reminder(String title, ArrayList<Segment> segments, String dateMade){
		this.title = title;
        this.segments = segments;
        this.dateMade = dateMade;
	}
}

