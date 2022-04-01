import java.io.IOException;
import java.nio.file.Path;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class MySearcher{
	
	private IndexSearcher indexSearcher;
	private QueryParser queryParser;
	private Query query;
	private Query titleQuery;
	private QueryParser titleQueryParser = new QueryParser(LuceneConstants.FILE_NAME, new KeywordAnalyzer());
	
	
	
	public MySearcher (Path dirPath, Analyzer analyzer) throws IOException {
		Directory indexDirectory = FSDirectory.open(dirPath);
		IndexReader reader = DirectoryReader.open(indexDirectory);
		indexSearcher = new IndexSearcher(reader);
		queryParser = new QueryParser(LuceneConstants.CONTENTS, analyzer);
	}
	
	public TopDocs search(String searchQuery) throws IOException, org.apache.lucene.queryparser.classic.ParseException {
		query = queryParser.parse(searchQuery);
		titleQuery = titleQueryParser.parse(searchQuery);
		TopDocs[] topDocs = {indexSearcher.search(query, LuceneConstants.MAX_SEARCH), indexSearcher.search(titleQuery, LuceneConstants.MAX_SEARCH)};
		TopDocs topDoc = TopDocs.merge(LuceneConstants.MAX_SEARCH, topDocs);
		return topDoc;
	}
	
	public Document getDocument(ScoreDoc scoreDoc) throws IOException {
		return indexSearcher.doc(scoreDoc.doc);
	}
	
}