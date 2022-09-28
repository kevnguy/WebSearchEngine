package edu.ucr.cs172;

import com.mongodb.client.*;
import static com.mongodb.client.model.Filters.eq;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;


import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SearchDocs {
    public static void main(String[] args) throws Exception {
        final String uri = "mongodb://localhost:27017";
        MongoClient mongoClient = Utilities.getMongoClient(uri);
        assert mongoClient != null;
        MongoDatabase database = mongoClient.getDatabase("Wikipedia");
        MongoCollection<org.bson.Document> collection = database.getCollection("Pages");

        Directory directory = FSDirectory.open(Path.of("index/"));
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put("mainText", Utilities.customAnalyzer());

        Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);

        Map<String, Float> boosts = new HashMap<>();
        boosts.put("title", 0.2f);
        boosts.put("boldText", 0.1f);
        boosts.put("italicText", 0.05f);

        String[] fields = {"mainText", "boldText", "italicText", "title"};
//        QueryParser parser = new QueryParser("mainText", analyzer);
        QueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);

        Scanner myObj = new Scanner(System.in);
        System.out.println("Enter query: ");
        String queryIn = myObj.nextLine();
        Query query = parser.parse(queryIn);

        int topHitCount = 1;
        ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;

        for(int rank = 0; rank < hits.length; rank++){
            Document hitDoc = indexSearcher.doc(hits[rank].doc);
            String url = hitDoc.get("url");
            org.bson.Document doc = collection.find(eq("url", url)).first();
            String mainText = doc.getString("mainText");
            System.out.println((rank+1) + " (score: " + hits[rank].score + ") --> " +
                "url: " + url + " " + mainText.substring(0,Math.min(mainText.length(), 100)));
//            System.out.println(indexSearcher.explain(query, hits[rank].doc));
        }
        indexReader.close();
        directory.close();
    }
}
