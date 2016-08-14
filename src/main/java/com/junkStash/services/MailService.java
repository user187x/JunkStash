package com.junkStash.services;

import java.util.ArrayList;
import java.util.Date;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.junkStash.config.DatabaseConfig;
import com.mongodb.client.MongoCursor;

@Service
public class MailService {

	@Autowired
	private DatabaseConfig databaseService;
	
	@Autowired
	private UserService userService;
	
	public boolean sendMail(String actionUserId, String recipient, String message) {
		
		if(userService.userExists(actionUserId) == false || userService.userExists(recipient) == false)
			return false;
		
		Document document = new Document();
		document.append("to", recipient);
		document.append("from", actionUserId);
		document.append("message", message);
		document.append("timeStamp", new Date());
		document.append("acknowledged", false);
		
		databaseService.getMailCollection().insertOne(document);
		
		return true;
	}
	
	public long getUnreadCount(String userId){
		
		Document match = new Document();
		match.append("to", userId);
		match.append("acknowledged", false);
		
		return databaseService.getMailCollection().count(match);
	}
	
	public ArrayList<Document> getAllMail(String userId){
		
		Document match = new Document();
		match.append("to", userId);
		
		MongoCursor<Document> cursor = databaseService.getMailCollection().find(match).iterator();

		ArrayList<Document> mail = new ArrayList<>();
		if(cursor==null || cursor.hasNext()==false)
			return mail;
		
		while(cursor.hasNext())
			mail.add(cursor.next());
		
		return mail;
	}
	
	public JsonArray getUnreadMail(String userId){
		
		Document match = new Document();
		match.append("to", userId);
		match.append("acknowledged", false);
		
		MongoCursor<Document> cursor = databaseService.getMailCollection().find(match).iterator();

		JsonArray mail = new JsonArray();
		
		if(cursor==null || cursor.hasNext()==false)
			return mail;
		
		while(cursor.hasNext())
			mail.add(cursor.next().toJson());
		
		return mail;
	}
	
	public void acknowledgeMail(String mailId){
		
		Document match = new Document();
		match.append("_id", new ObjectId(mailId));
		
		Document update = new Document();
		update.append("$set", new Document("acknowledged", true));
		
		databaseService.getMailCollection().updateOne(match, update);
	}
	
	public boolean hasUnAcknowledgedMail(String userId){
		
		Document match = new Document();
		match.append("to", userId);
		match.append("acknowledged", false);
		
		long x = getUnreadCount(userId);
		
		if(x==0)
			return false;
		else
			return true;
	}
}
