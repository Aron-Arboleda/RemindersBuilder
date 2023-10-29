package main.remind;

import java.util.Date;

public class Element{
	String subject, content;
    Date deadline;
    int format;
	
	Element(String subject, String content, Date deadline, int format){
		this.subject = subject;
		this.content = content;
		this.deadline = deadline;
        this.format = format;
	}
}

