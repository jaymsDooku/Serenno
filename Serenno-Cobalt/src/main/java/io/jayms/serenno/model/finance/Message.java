package io.jayms.serenno.model.finance;

public class Message {

	private String content;
	private long timeSent;
	
	public Message(String content) {
		this.content = content;
		this.timeSent = System.currentTimeMillis();
	}
	
	public String getContent() {
		return content;
	}
	
	public long getTimeSent() {
		return timeSent;
	}
	
}
