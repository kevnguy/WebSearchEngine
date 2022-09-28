package edu.ucr.cs172;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;

import java.io.IOException;

import static edu.ucr.cs172.IndexDocs.logger;

public class Utilities {
    public static Analyzer customAnalyzer() throws IOException {
        return CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("stop")
//                .addTokenFilter("porterstem")
//                .addTokenFilter("length","min","2","max","255")
                .build();
    }

    public static MongoClient getMongoClient(String uri) {
        MongoClient mongoClient = null;
        try {
            mongoClient = MongoClients.create(uri);
            logger.info("Connected to MongoDB!");
        } catch (MongoException e) {
            logger.error("MongoDB connection error: ", e);
        }
        return mongoClient;
    }
}
