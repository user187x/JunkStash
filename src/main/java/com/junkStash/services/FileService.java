package com.junkStash.services;

import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.junkStash.config.DatabaseConfig;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.result.UpdateResult;

@Service
public class FileService {
	
	public static final long USER_SPACE_SIZE = 52428800;
	public static final long MAX_SERER_SIZE = 1073741824;

	@Autowired
	private DatabaseConfig databaseService;
	
	@Autowired
	private UserService userService;
	
	private static String cachedIndex;
	
	public FileService(){
		cacheResource();
	}
		
	public MongoDatabase getMongoDatabase(){
		return databaseService.getMongoDatabase();
	}
	
	public MongoClient getMongoClient(){
		return databaseService.getMongoClient();
	}
	
	public GridFSBucket getGridFSBucket(){
		return databaseService.getGridFSBucket();
	}
	
	public boolean exists(String fileId){
		
		Document query = new Document();
		query.append("_id", new ObjectId(fileId));
		
		FindIterable<Document> results = databaseService.getFileCollection().find(query);
		
		if(results.iterator().hasNext())
			return true;
		
		else
			return false;
	}
	
	public boolean isUserOwner(String userId, String fileId){
		
		Document match = new Document();
		match.append("owner", userId);
		match.append("_id", new ObjectId(fileId));
		
		MongoCursor<Document> cursor = databaseService.getFileCollection().find(match).iterator();
		
		if(cursor.hasNext())
			return true;
		else
			return false;
	}
	
	public boolean isDiskCapacityFull(long size){
		
		//sum all users sizes
		//subtract total server size
		//check if new account is less than equal to remaining
		
		return false;
	}
	
	public boolean remove(String fileId, String userId){
		
		if(isUserOwner(userId, fileId) || userService.isUserAdmin(userId)){
		
			try{
				getGridFSBucket().delete(new ObjectId(fileId));
			}
			catch(Exception e){
				return false;
			}
			return true;
		}
		return false;
	}
	
	public boolean removeShared(String userId, String fileId){
		
		if(isUserOwner(userId, fileId) || userService.isUserAdmin(userId) || isFileShared(userId, fileId)){
			if(userService.removeShare(userId, fileId))
				return true;
			else
				return false;
		}
		return false;
	}
	
	public boolean isFileShared(String userId, String fileId){
		
		if(isUserOwner(userId, fileId))
			return false;
		else if(userService.userHasFileAccess(userId, fileId))
			return true;
		else
			return false;
	}
	
	public JsonObject getFile(String fileId, Document shareInfo){
		
		Document match = new Document();
		match.append("_id", new ObjectId(fileId));
		
		FindIterable<Document> cursor = databaseService.getFileCollection().find(match);
			
		Document result = cursor.first();
		
		String messsage = result.getString("message");
		String type = result.getString("type");
		String id = result.get("_id").toString();
		String name = result.getString("filename");
		String time = result.getDate("uploadDate").toString();
		String owner = result.getString("owner");
		long size = result.getLong("length");
		
		JsonObject json = new JsonObject();
		
		if(StringUtils.isNotEmpty(messsage))
			json.add("message", new JsonPrimitive(messsage));
		
		if(StringUtils.isNotEmpty(time))
			json.add("time", new JsonPrimitive(time));
		
		if(StringUtils.isNotEmpty(type))
			json.add("type", new JsonPrimitive(type));
		
		if(StringUtils.isNotEmpty(id))
			json.add("id", new JsonPrimitive(id));
		
		if(StringUtils.isNotEmpty(name))
			json.add("name", new JsonPrimitive(name));
		
		if(StringUtils.isNotEmpty(owner))
			json.add("owner", new JsonPrimitive(owner));
		
		JsonObject shareDetails = new JsonObject();
		
		if(shareInfo!=null){
			
			String sharerId = shareInfo.getString("sharerId");
			Date shareDate = shareInfo.getDate("shareDate");
			
			shareDetails.add("shared", new JsonPrimitive(true));
			shareDetails.add("sharer", new JsonPrimitive(sharerId));
			shareDetails.add("shareDate", new JsonPrimitive(shareDate.toString()));
		}
		else{
			shareDetails.add("shared", new JsonPrimitive(false));
		}
		
		json.add("shared", shareDetails);
		json.add("size", new JsonPrimitive(FileUtils.byteCountToDisplaySize(size)));
		
		return json;	
	}
	
	public JsonArray getFiles(String userId){
	
		MongoCursor<Document> cursor = null;
		
		if(userService.isUserAdmin(userId))
			cursor = databaseService.getFileCollection().find().iterator();
		else
			cursor = databaseService.getFileCollection().find(new Document("owner", userId)).iterator();
		
		JsonArray jsonArray = new JsonArray();
		
		while(cursor.hasNext()){
			
			Document result = cursor.next();
			
			String messsage = result.getString("message");
			String type = result.getString("type");
			String id = result.get("_id").toString();
			String name = result.getString("filename");
			String time = result.getDate("uploadDate").toString();
			String owner = result.getString("owner");
			long size = result.getLong("length");
			
			JsonObject json = new JsonObject();
			
			if(StringUtils.isNotEmpty(messsage))
				json.add("message", new JsonPrimitive(messsage));
			
			if(StringUtils.isNotEmpty(time))
				json.add("time", new JsonPrimitive(time));
			
			if(StringUtils.isNotEmpty(type))
				json.add("type", new JsonPrimitive(type));
			
			if(StringUtils.isNotEmpty(id))
				json.add("id", new JsonPrimitive(id));
			
			if(StringUtils.isNotEmpty(name))
				json.add("name", new JsonPrimitive(name));
			
			if(StringUtils.isNotEmpty(owner))
				json.add("owner", new JsonPrimitive(owner));
			
			JsonObject shareInfo = new JsonObject();
			shareInfo.add("shared", new JsonPrimitive(false));
			
			json.add("shared", shareInfo);
			json.add("size", new JsonPrimitive(FileUtils.byteCountToDisplaySize(size)));
			
			jsonArray.add(json);
		}
		
		pruneOrphanedSharedFiles(userId);
		
		return addSharedFiles(userId, jsonArray);
	}
	
	public void pruneOrphanedSharedFiles(String userId){
		
		Document query = new Document();
		query.append("user", userId);
		
		FindIterable<Document> results = databaseService.getUserCollection().find(query);
		
		Document user = results.first();
		
		if(user.get("shared")==null)
			return;
		
		ArrayList<Document> sharedFiles = (ArrayList<Document>) user.get("shared");
		
		for(Document share : sharedFiles){
			
			String fileId = share.getString("_id");
			
			if(!exists(fileId)){
				removeShared(userId, fileId);
			}
		}
	}
	
	public JsonArray addSharedFiles(String userId, JsonArray files){
		
		Document query = new Document();
		query.append("user", userId);
		
		FindIterable<Document> results = databaseService.getUserCollection().find(query);
		
		results.forEach(new Block<Document>() {
		    
			@Override
		    public void apply(final Document document) {
		    	
				@SuppressWarnings("unchecked")
				ArrayList<Document> shared = (ArrayList<Document>) document.get("shared");
				
				if(shared==null || shared.isEmpty())
					return;
				
				for(Document id : shared){
					
					String fileId = id.getString("_id");
					JsonObject file = getFile(fileId, id);
					
					files.add(file);
				}
		    }
		});
		
		return files;
	}
	
	public JsonObject getTotalDiskSpace(String userKey){
		
		String userId = userService.getUserId(userKey);
		
		MongoCursor<Document> cursor = null;
		
		if(userService.isUserAdmin(userId))
			cursor = databaseService.getFileCollection().find().iterator();
		else
			cursor = databaseService.getFileCollection().find(new Document("owner", userId)).iterator();
		
		JsonObject json = new JsonObject();
		long totalSize = 0;
		
		while(cursor.hasNext()){
			
			Document result = cursor.next();

			long size = result.getLong("length");
			totalSize += size;
		}
		
		json.add("size", new JsonPrimitive(totalSize));
		json.add("normalized", new JsonPrimitive(FileUtils.byteCountToDisplaySize(totalSize)));
		
		if(userService.isUserAdmin(userId)){
			json.add("maxSpaceNormalized", new JsonPrimitive(FileUtils.byteCountToDisplaySize(MAX_SERER_SIZE)));
			json.add("maxSpace", new JsonPrimitive(MAX_SERER_SIZE));
		}
		else{
			json.add("maxSpaceNormalized", new JsonPrimitive(FileUtils.byteCountToDisplaySize(USER_SPACE_SIZE)));
			json.add("maxSpace", new JsonPrimitive(USER_SPACE_SIZE));
		}
		
		return json;
	}
	
	public JsonObject getUserDiskSpace(String userId){
		
		Document match = new Document();
		match.append("owner", userId);
		
		MongoCursor<Document> cursor = databaseService.getFileCollection().find(match).iterator();
		
		JsonObject json = new JsonObject();
		long totalSize = 0;
		
		while(cursor.hasNext()){
			
			Document result = cursor.next();

			long size = result.getLong("length");
			totalSize += size;
		}
		
		json.add("size", new JsonPrimitive(totalSize));
		json.add("normalized", new JsonPrimitive(FileUtils.byteCountToDisplaySize(totalSize)));
		
		return json;
	}
	
	
	public boolean setFileType(String fileId, String fileType){
		
		Document query = new Document();
		query.append("_id", new ObjectId(fileId));
		
		Document update = new Document();
		update.append("$set", new Document("type", fileType));
		
		UpdateResult results = databaseService.getFileCollection().updateOne(query, update);
		
		if(results.getModifiedCount()>0)
			return true;
		else
			return false;
	}
	
	public boolean setFileOwner(String fileId, String userId){
		
		Document query = new Document();
		query.append("_id", new ObjectId(fileId));
		
		Document update = new Document();
		update.append("$set", new Document("owner", userId));
		
		UpdateResult results = databaseService.getFileCollection().updateOne(query, update);
		
		if(results.getModifiedCount()>0)
			return true;
		else
			return false;
	}
	
	public Document find(String fileId){
		
		ObjectId objectId = new ObjectId(fileId);
		
		Document query = new Document();
		query.append("_id", objectId);
		
		FindIterable<Document> results = databaseService.getFileCollection().find(query);
		
		if(results.iterator().hasNext())
			return results.iterator().next();
		
		else
			return new Document();
	}
	
	public String getFileName(String fileId){
		
		ObjectId objectId = new ObjectId(fileId);
		
		Document query = new Document();
		query.append("_id", objectId);
		
		FindIterable<Document> results = databaseService.getFileCollection().find(query);
		
		if(results.iterator().hasNext())
			return results.iterator().next().getString("filename");
		
		else
			return null;
	}
	
	public String getFileOwner(String fileId){
		
		Document query = new Document();
		query.append("_id", new ObjectId(fileId));
		
		FindIterable<Document> results = databaseService.getFileCollection().find(query);
		
		if(results.iterator().hasNext())
			return results.iterator().next().getString("owner");
		
		else
			return null;
	}
	
	public static void cacheResource(){
		
		InputStream inputStream = FileService.class.getResourceAsStream("/index.html");
		
		StringWriter writer = new StringWriter();
		
		try{
			IOUtils.copy(inputStream, writer);
			cachedIndex = writer.toString();
		}
		catch(Exception e){
			System.out.println("Failure Caching Resource File : index.html");
			System.exit(1);
		}
		
		System.out.println("Resource Cached : "+cachedIndex.getBytes().length+" Bytes");
	}
	
	public String getIndex(){
		return cachedIndex;
	}
}
