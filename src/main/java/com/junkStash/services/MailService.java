package com.junkStash.services;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
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
		
		SocketService.checkMailAndNotify(recipient);
		
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
		
		while(cursor.hasNext()){
				
			Document result = cursor.next();
			
			String id = result.get("_id").toString();
			String messsage = result.getString("message");
			String from = result.getString("from");
			String timeStamp = result.getDate("timeStamp").toString();
			
			JsonObject json = new JsonObject();
			
			json.add("id", new JsonPrimitive(id));
			
			if(StringUtils.isNotEmpty(messsage))
				json.add("message", new JsonPrimitive(messsage));
			
			if(StringUtils.isNotEmpty(timeStamp))
				json.add("timeStamp", new JsonPrimitive(timeStamp));
			
			if(StringUtils.isNotEmpty(from))
				json.add("from", new JsonPrimitive(from));
			
			
			mail.add(json);
		}
		
		return mail;
	}
	
	public boolean acknowledgeMail(String mailId){
		
		Document match = new Document();
		match.append("_id", new ObjectId(mailId));
		
		Document update = new Document();
		update.append("$set", new Document("acknowledged", true));
		
		long modCount = databaseService.getMailCollection().updateOne(match, update).getModifiedCount();
		
		if(modCount==1)
			return true;
		else
			return false;
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
