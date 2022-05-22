package edu.ucr.cs172;

import com.mongodb.client.*;
import static com.mongodb.client.model.Filters.eq;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Fields;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.PointsConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.json.JSONArray;

import java.lang.reflect.Array;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class SearchDocs {
    public static ArrayList<Map<String, Object>> searchDocuments(String queryIn) throws Exception {
        MongoClient mongoClient = Utilities.getMongoClient(Utilities.URI);
        assert mongoClient != null;
        MongoDatabase database = mongoClient.getDatabase("Wikipedia");
        MongoCollection<org.bson.Document> collection = database.getCollection("Pages");

        Directory directory = FSDirectory.open(Path.of("index/"));
        DirectoryReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        Map<String, Analyzer> analyzerPerField = new HashMap<>();
        analyzerPerField.put("mainText", Utilities.customAnalyzer());
        analyzerPerField.put("title", new SimpleAnalyzer());

        Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), analyzerPerField);

        Map<String, Float> boosts = new HashMap<>();
        boosts.put("title", 0.2f);
        boosts.put("boldText", 0.1f);
        boosts.put("italicText", 0.05f);

        CharSequence[] fields = {"boldText", "italicText", "title"};
//        QueryParser parser = new QueryParser("mainText", analyzer);
        StandardQueryParser parser = new StandardQueryParser(analyzer);
        parser.setMultiFields(fields);
//        QueryParser parser = new MultiFieldQueryParser(fields, analyzer, boosts);
//
        PointsConfig pointsConfig = new PointsConfig(new DecimalFormat(), Integer.class);
        Map<String, PointsConfig> pointsConfigMap = new HashMap<>();
        pointsConfigMap.put("lastMod", pointsConfig);
        parser.setPointsConfigMap(pointsConfigMap);


//        Scanner myObj = new Scanner(System.in);
//        System.out.println("Enter query: ");
//        String queryIn = myObj.nextLine();
        Query query = parser.parse(queryIn,"mainText");

        int topHitCount = 10;
        ScoreDoc[] hits = indexSearcher.search(query, topHitCount).scoreDocs;
        ArrayList<Map<String,Object>> documents = new ArrayList<>();

        for(int rank = 0; rank < hits.length; rank++){
            int id = hits[rank].doc;
            Document hitDoc = indexSearcher.doc(hits[rank].doc);
            String url = hitDoc.get("url");
            org.bson.Document doc = collection.find(eq("url", url)).first();
            String mainText = doc.getString("mainText");

            System.out.println((rank+1) + " (score: " + hits[rank].score + ") --> " + "url: " + url);
//            System.out.println(indexSearcher.explain(query, hits[rank].doc));
            SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter();
            Highlighter highlighter = new Highlighter(htmlFormatter, new QueryScorer(query));
            Fields tvector = indexReader.getTermVectors(id);
            TokenStream tokenStream = TokenSources.getTermVectorTokenStreamOrNull("mainText", tvector,  -1) ;
            String snippets = highlighter.getBestFragments(tokenStream, mainText,3, "...");
//            System.out.println(snippets);
            documents.add(Utilities.docToMap(doc,url,snippets));
        }
        indexReader.close();
        directory.close();
        return documents;
    }

    public static void main(String[] args) throws Exception {
        ArrayList a = searchDocuments("Dog");
    }

}