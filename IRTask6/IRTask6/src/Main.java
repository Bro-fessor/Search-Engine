import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		final Path dirPath = Paths.get(args[0]);
		if(!Files.exists(dirPath) || !Files.isDirectory(dirPath)){
				// entered path is not a directory
				System.out.println("please enter a valid directory path!");
				System.exit(-1);
		}

		IndexWriter writer;
		try {
			Analyzer myTxtAnalyzer = CustomAnalyzer.builder()
					.withTokenizer("standard")
					.addTokenFilter("lowercase")
					.addTokenFilter("stop")
					.addTokenFilter("porterstem")
					.build();
			// directory with indexes
			Directory indexDirectory = FSDirectory.open(dirPath);
			
			// indexer
			writer = new IndexWriter(indexDirectory, new IndexWriterConfig(myTxtAnalyzer).setOpenMode(OpenMode.CREATE));
//			System.out.println(writer.getConfig().getOpenMode().toString());
			Indexer.indexDocs(writer, dirPath);
			writer.close();			
			
			MySearcher searcher = new MySearcher(dirPath, myTxtAnalyzer);
			TopDocs hits = searcher.search("Task 3.txt");
			
			System.out.println(hits.totalHits + " documents found. ");
			for(ScoreDoc scoreDoc : hits.scoreDocs) {
				Document doc = searcher.getDocument(scoreDoc);
				System.out.println("Score: " + scoreDoc.score + " File: " + doc.get(LuceneConstants.FILE_PATH) + "last mod time: " +  doc.get(LuceneConstants.LAST_MODIFICATION_TIME));
			}
			
		}catch (Exception e) {
			System.out.println("Error occured: " + e);
			System.exit(-1);
		};		
	}

}
