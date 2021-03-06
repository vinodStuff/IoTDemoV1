package com.verizon.iot.mongo;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;

public class MongoDBClient {

	private static final String VZIOT_DATA_DUMP_DB = "vziotdatadump";
	private static final String VZIOT_BILLING_DB = "vziotbillingdb";
	private static final String VZIOT_PLAN_MASTER_DB = "vziotplandb";
	private static final String VZIOT_USER_PROFILE_DB = "vziotprofiledb";

	private static final Map<String, Double> RATE_MAP = new HashMap<String, Double>();

	private static MongoClient mongoClient;
	private static MongoDatabase mongoDatabase;

	static {
		try {
			System.out.println("Init pizza");
			initDBConnection();
			dropCollection(VZIOT_DATA_DUMP_DB);
			dropCollection(VZIOT_BILLING_DB);
			dropCollection(VZIOT_PLAN_MASTER_DB);
			dropCollection(VZIOT_USER_PROFILE_DB);
			System.out.println("This Works");
			buildRateMap();
			buildPlans();
			provisionUser(1234,"SMALL");
			insertIntoDataDumpTable(1234, "ny", "HealthDevices", 12.00);
			fetchUserPlanId(1234);			
			fetchPlanDetails("SMALL");
			updateBillData(1234, "HealthDevices", 3.00, 3.24);
			fetchCurrentBillData(1234);
			fetchCurrentUsageDetails(1234, "HealthDevices");
			fetchPlanDetails();
		
		} catch (Exception e) {
			throw new ExceptionInInitializerError("DB connection not created");
		}
	}

	public static Map<String, Double> getRateMap() {
		return RATE_MAP;
	}

	public static void main(String[] args) {
		// buildPlans();
		   //buildRateMap();
		// provisionUser(1234,"SMALL");
		 //insertIntoDataDumpTable(1234, "ny", "HealthDevices", 12.00);
		 //fetchUserPlanId(1234);
		 //fetchPlanDetails("SMALL");
		 //updateBillData(1234, "HealthDevices", 3.00, 3.24);
		 //fetchCurrentBillData(1234);
		 //fetchCurrentUsageDetails(1234, "HealthDevices");
		 //fetchPlanDetails();

	}

	private static void buildRateMap() {
		RATE_MAP.put("HealthDevices", 0.32);
		RATE_MAP.put("Gadgets", 0.22);
		RATE_MAP.put("Appliances", 0.12);
		RATE_MAP.put("Others", 0.18);
	}

	private static void initDBConnection() throws Exception {
		try {
			MongoClientURI uu = new MongoClientURI("mongodb://CloudFoundry_rt2ng8pk_tin9mr48_85ogqdj9:QJppO4eURNfKMVvd7tudJC_-ot4ufGGE@ds035633.mongolab.com:35633/CloudFoundry_rt2ng8pk_tin9mr48");
			//MongoClientURI uu = new MongoClientURI("ds035713.mongolab.com:35713/CloudFoundry_rt2ng8pk_5bfinaug");			
			mongoClient = new MongoClient(uu);
			//mongoClient = new MongoClient("ds035633.mongolab.com", 35633);
			mongoDatabase = mongoClient.getDatabase("CloudFoundry_rt2ng8pk_tin9mr48");
			System.out.println("Connect to database successfully");
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static void buildPlans(){
		try{
			MongoCollection<Document> collection = mongoDatabase.getCollection(VZIOT_PLAN_MASTER_DB);
			
			Document doc = new Document();
			doc.put("planId","SMALL");
			
			Document docg = new Document();
			docg.put("Gadgets", 5.00);
			docg.put("HealthDevices", 10.00);
			docg.put("Appliances", 20.00);
			docg.put("Others", 30.00);
			docg.put("PlanCharges", 19.99);
			
			doc.put("details", docg);
			collection.insertOne(doc);
			
			Document doc1 = new Document();
			doc1.put("planId","MEDIUM");
			
			Document docg1 = new Document();
			docg1.put("Gadgets", 10.00);
			docg1.put("HealthDevices", 20.00);
			docg1.put("Appliances", 30.00);
			docg1.put("Others", 40.00);
			docg1.put("PlanCharges", 29.99);
			
			doc1.put("details", docg1);
			collection.insertOne(doc1);

			Document doc2 = new Document();
			doc2.put("planId","LARGE");
			
			Document docg2 = new Document();
			docg2.put("Gadgets", 20.00);
			docg2.put("HealthDevices", 30.00);
			docg2.put("Appliances", 40.00);
			docg2.put("Others", 50.00);
			docg2.put("PlanCharges", 49.99);
			
			doc2.put("details", docg2);
			collection.insertOne(doc2);
			
			System.out.println("Inserted JSON string to database successfully");
		} catch (Exception e){
			e.printStackTrace();
		}
			
	}



	public static Document fetchCurrentUsageDetails(long userId, String deviceCategory){
		MongoCursor<Document> cursor = null;
		Document userUsageDoc = null;
		try{
			MongoCollection<Document> collection = mongoDatabase.getCollection(VZIOT_BILLING_DB);
			BasicDBObject dbo = new BasicDBObject("userId", userId).append("deviceCategory", deviceCategory);
			FindIterable<Document> iter = collection.find(dbo);
			cursor = iter.iterator();			
			System.out.println("Fetching document :"+userId);
			
			while(cursor.hasNext()){
				userUsageDoc = cursor.next();
				System.out.println("Row Data = "+ userUsageDoc);
				break;
			}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return userUsageDoc;
	}



	public static Document fetchPlanDetails(String planId){
		MongoCursor<Document> cursor = null;
		Document planDoc = null;
		try{
			MongoCollection<Document> collection = mongoDatabase.getCollection(VZIOT_PLAN_MASTER_DB);
			BasicDBObject dbo = new BasicDBObject("planId", planId);
			FindIterable<Document> iter = collection.find(dbo);
			cursor = iter.iterator();			
			System.out.println("Fetching document for plan id :"+planId);
			
			while(cursor.hasNext()){
				planDoc = cursor.next();
				System.out.println("Row Data = "+ planDoc);
				break;
			}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return planDoc;
	}
	
	public static JsonArray fetchPlanDetails(){		
		MongoCursor<Document> cursor = null;
		JsonArray jarray = null;
		
		try{
			Document planDoc = null;
			MongoCollection<Document> collection = mongoDatabase.getCollection(VZIOT_PLAN_MASTER_DB);
							
			FindIterable<Document> iter = collection.find();
			cursor = iter.iterator();			
			JsonArrayBuilder jab = Json.createArrayBuilder();
			while(cursor.hasNext()){
				JsonObjectBuilder job = Json.createObjectBuilder();
				planDoc = cursor.next();	
				job.add("planId", planDoc.get("planId").toString());
				//job.add("details", planDoc.get("details").toString());
				
				if(((Document)planDoc.get("details")).get("Gadgets") != null)
				job.add("Gadgets", String.valueOf(((Document)planDoc.get("details")).get("Gadgets")));
				else
					job.add("Gadgets", "0.0");
				if(((Document)planDoc.get("details")).get("Appliances") != null)
					job.add("Appliances", String.valueOf(((Document)planDoc.get("details")).get("Appliances")));
					else
						job.add("Appliances", "0.0");
				if(((Document)planDoc.get("details")).get("Others") != null)
					job.add("Others", String.valueOf(((Document)planDoc.get("details")).get("Others")));
					else
						job.add("Others", "0.0");
				if(((Document)planDoc.get("details")).get("HealthDevices") != null)
					job.add("HealthDevices", String.valueOf(((Document)planDoc.get("details")).get("HealthDevices")));
					else
						job.add("HealthDevices", "0.0");

				if(((Document)planDoc.get("details")).get("PlanCharges") != null)
					job.add("PlanCharges", String.valueOf(((Document)planDoc.get("details")).get("PlanCharges")));
					else
						job.add("PlanCharges", "0.0");
				job.add("Gadgets_Rate", RATE_MAP.get("Gadgets"));
				job.add("Appliances_Rate", RATE_MAP.get("Appliances"));
				job.add("HealthDevices_Rate", RATE_MAP.get("HealthDevices"));
				job.add("Others_Rate", RATE_MAP.get("Others"));
				
				JsonObject jo = job.build();
				jab.add(jo);
			}
			
			jarray = jab.build();			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		
		//System.out.println("ja= " + ja);
		
		
		return jarray;
	}

	
	public static String fetchUserPlanId(long userId){
		MongoCursor<Document> cursor = null;
		Document planDoc = null;
		String planID=null;
		try{
			MongoCollection<Document> collection = mongoDatabase.getCollection(VZIOT_USER_PROFILE_DB);
			BasicDBObject dbo = new BasicDBObject("userId", userId);
			FindIterable<Document> iter = collection.find(dbo);
			cursor = iter.iterator();			
			System.out.println("Fetching document for userId :"+userId);
			
			while(cursor.hasNext()){
				planDoc = cursor.next();
				System.out.println("Plan Row Data = "+ planDoc);
				planID=planDoc.getString("planId");
				break;
			}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			cursor.close();
		}
		return planID;
	}
	
	public static void provisionUser(long userId, String planId){
		try{
			MongoCollection<Document> collection = mongoDatabase.getCollection(VZIOT_USER_PROFILE_DB);
			System.out.println("User provisioned in DB successfully");
			UpdateOptions uo = new UpdateOptions();
			uo.upsert(true);
			
			collection.updateOne(
					new Document("userId", userId),
					new Document("$set", new Document("planId", planId)),
					uo
					);			
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void insertIntoDataDumpTable(long userId, String location, String deviceCategory, double dataVolume){
		try{
			MongoCollection<Document> collection = mongoDatabase.getCollection(VZIOT_DATA_DUMP_DB);
			
			Document doc = new Document();
			doc.put("userId",userId);
			doc.put("location",location);
			doc.put("deviceCategory",deviceCategory);
			doc.put("dataVolume",dataVolume);
			collection.insertOne(doc);
			System.out.println("Inserted JSON string into DB successfully");
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * Returns Json String; contains current bill amount of the given customer.
	 * 
	 * @param paramKey
	 * @param paramValue
	 * @return
	 */
	public static String fetchCurrentBillData(long userId) {
		System.out.println("Fetching Document :" + userId);
		MongoCursor<Document> cursor = null;
		JsonArray jarry = Json.createArrayBuilder().build();

		JsonArrayBuilder jab = Json.createArrayBuilder();

		try {
			String planId = MongoDBClient.fetchUserPlanId(userId);
			Document planDoc = MongoDBClient.fetchPlanDetails(planId);
			
			double pc = ((Document)planDoc.get("details")).getDouble("PlanCharges");
			
			
			MongoCollection<Document> collection = mongoDatabase.getCollection(VZIOT_BILLING_DB);
			BasicDBObject dbo = new BasicDBObject("userId", userId);
			FindIterable<Document> iter = collection.find(dbo);
			cursor = iter.iterator();

			String deviceCategory = null;
			double dataVolume = 0.00;
			double currentBillAmt = 0.00;
			
			double currentBillSum = 0.00;
			JsonObjectBuilder job = null;
			while (cursor.hasNext()) {
				Document docx = cursor.next();
				deviceCategory = (String) docx.get("deviceCategory");
				dataVolume = (Double) docx.get("dataVolume");
				currentBillAmt = (Double) docx.get("currentBillAmt");
				
				currentBillSum += (double)docx.getDouble("currentBillAmt");
				job = Json.createObjectBuilder();
				job.add("userId", userId);
				job.add("deviceCategory", deviceCategory);
				job.add("dataVolume", dataVolume);
				job.add("currentBillAmt", currentBillAmt);

				jab.add(job);

			}
			job = Json.createObjectBuilder();
			job.add("userId", userId);
			job.add("deviceCategory", "PlanCharges");
			job.add("dataVolume", "");
			job.add("currentBillAmt", pc);

			jab.add(job);
			
			currentBillSum+=pc;
			job = Json.createObjectBuilder();
			job.add("userId", userId);
			job.add("deviceCategory", "TotalBill");
			job.add("dataVolume", "");
			job.add("currentBillAmt", currentBillSum);

			jab.add(job);

			jarry = jab.build();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cursor.close();
		}

		System.out.println("Returning Document :" + jarry.toString());
		return jarry.toString();
	}
	
	
	public static void updateBillData(long userId, String deviceCategory, double dataVolume, double currentBillAmount){
		MongoCollection<Document> collection = mongoDatabase.getCollection(VZIOT_BILLING_DB);
		BasicDBObject dbo = new BasicDBObject("userId", userId).append("deviceCategory",deviceCategory);
		FindIterable<Document> iter = collection.find(dbo);
		MongoCursor<Document> cursor = iter.iterator();
		
		System.out.println("Fetching document userId = " + userId + " deviceCategory = " + deviceCategory);
		
		Document doc = null;
		while(cursor.hasNext()){
			doc = cursor.next();
			System.out.println("row data doc =" + doc);
			break;
		}
		
		double totAmount = 0;
		double totData = 0;
		
		if (doc != null){
			double a = doc.getDouble("currentBillAmt");
			double b = doc.getDouble("dataVolume");
			
			System.out.println("a = " + a);
			System.out.println("b = " + b);
			
			totAmount = currentBillAmount + a ;
			totData = dataVolume + b;
		} else {
			totAmount = currentBillAmount;
			totData = dataVolume;
		}
		
		System.out.println(totAmount);
		System.out.println(totData);
		
		UpdateOptions uo = new UpdateOptions();
		uo.upsert(true);
		
		DecimalFormat df=new DecimalFormat(".##");
		totAmount = Double.parseDouble(df.format(totAmount));
		collection.updateOne(
				new Document("userId", userId).append("deviceCategory",deviceCategory),
				new Document("$set", new Document("currentBillAmt", totAmount).append("dataVolume",totData)),
				uo
				);	
		System.out.println("Updated to database successfully");
			
	}
	
	private static void dropCollection(String collectionName){
		MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
		collection.drop();
		
	}
	
}