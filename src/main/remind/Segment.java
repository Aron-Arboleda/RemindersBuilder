package main.remind;

import java.util.*;

public class Segment{
	String title;
    int format;
    int emoji;
    ArrayList<Element> elements;
	
	Segment(String title, int format, int emoji, ArrayList<Element> elements){
		this.title = title;
        this.format = format;
        this.emoji = emoji;
        this.elements = elements;
	}
}