package edu.iastate.coms.cs472.newspet.trainer;

public class Message
{
	public enum MessageType
	{
		INCREMENTAL, BATCH
	}
	
	private MessageType messageType;
	
	private int userId;
	
	private int categoryId;
	
	//can be either a feedItemID or a batch training source ID.
	private int sourceId;
	
	public Message(MessageType messageType, int userId, int categoryId, int sourceId)
	{
		this.messageType = messageType;
		this.userId = userId;
		this.categoryId = categoryId;
		this.sourceId = sourceId;
	}
	
	public Message(String rawMessage)
	{
		String[] elements = rawMessage.split(",");
		if(elements.length != 4) throw new IllegalArgumentException("String must have 4 comma-delimited values");
		
		if(elements[0].equals("INCREMENTAL")) this.messageType = MessageType.INCREMENTAL;
		else if(elements[0].equals("BATCH")) this.messageType = MessageType.BATCH;
		else throw new IllegalArgumentException("First element must be INCREMENTAL or BATCH");
		
		this.userId = Integer.parseInt(elements[1]);
		this.categoryId = Integer.parseInt(elements[2]);
		this.sourceId = Integer.parseInt(elements[3]);
	}
	
	public MessageType getMessageType()
	{
		return messageType;
	}
	
	public int getUserId()
	{
		return userId;
	}
	
	public int getCategoryId()
	{
		return categoryId;
	}
	
	public int getSourceId()
	{
		return sourceId;
	}
}
