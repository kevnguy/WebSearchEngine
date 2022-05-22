package edu.ucr.cs172;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import static edu.ucr.cs172.IndexDocs.logger;

public class Utilities {
    static final String URI = "mongodb://localhost:27017";
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

