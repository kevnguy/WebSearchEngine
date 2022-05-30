package edu.ucr.cs172;


import java.io.IOException;
import java.util.HashMap;
import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;

import static edu.ucr.cs172.IndexDocs.logger;

public class Utilities {
    // MongoDB connection URI
    static final String URI = "mongodb://localhost:27017";

    // Helper function to configure custom analyzer
    public static Analyzer customAnalyzer() throws IOException {
        return CustomAnalyzer.builder()
                .withTokenizer("standard")
                .addTokenFilter("lowercase")
                .addTokenFilter("stop")
                .addTokenFilter("englishPossessive")
                .addTokenFilter("porterStem")
                .build();
    }

    // helper function to get MongoDB connection
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

    // helper function to convert document into dictionary
    public static HashMap<String,Object> docToMap(org.bson.Document document, String url, String snippet){
        HashMap<String,Object> result = new HashMap<>();
        result.put("title",document.get("title"));
        result.put("url",url);
        result.put("TOC",document.get("TOC"));
        result.put("lastMod",document.get("lastMod"));
        result.put("img",document.get("img"));
        result.put("snippet",snippet.replaceAll("\\n"," ")+"...");
        return result;
    }
}

