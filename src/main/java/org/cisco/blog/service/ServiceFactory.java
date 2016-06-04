package org.cisco.blog.service;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class ServiceFactory {
	
	
	private static ThreadLocal<Datastore> mongoTL = new ThreadLocal<Datastore>();
	
	/**
	 * Method to retrieve a mongo database client from the thread local storage
	 * @return
	 */
	public static Datastore getMongoDB(){
		if(mongoTL.get()==null){
			MongoClientURI connectionString = new MongoClientURI("mongodb://162.222.181.52:27017");
//			MongoClientURI connectionString = new MongoClientURI("mongodb://vm-ajchande-001.cisco.com:27017");
			MongoClient mongoClient = new MongoClient(connectionString);	
			Morphia morphia = new Morphia();
			//morphia.mapPackage("com.mysocial.model");
			morphia.mapPackage("org.cisco.blog.model");
			Datastore datastore = morphia.createDatastore(mongoClient, "blog");
			datastore.ensureIndexes();
			mongoTL.set(datastore);
			return datastore;
		}
		return mongoTL.get();
	}
	
}
