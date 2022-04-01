import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;

public class Indexer {

	static void indexDocs(final IndexWriter writer, Path path) throws IOException {
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException {
				try {
					// call to indexTextDoc
					if (TextFileFilter.accept(file)) {
						indexTextDoc(writer, file);
					}
					if (HTMLFileFilter.accept(file)) {
						indexHtmlDoc(writer, file);
					}
				} catch (IOException ignore) {
					// dont index files you cant read
				}
				return FileVisitResult.CONTINUE;
			}
		});

	}

	static void indexTextDoc(final IndexWriter writer, Path file) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {

			// create empty document
			Document doc = new Document();

			// add the last modification time field
			Field lastModField = new StoredField(LuceneConstants.LAST_MODIFICATION_TIME, Files.getAttribute(file, "lastModifiedTime", LinkOption.NOFOLLOW_LINKS).toString());
			doc.add(lastModField);
			
			// add the path Field
			Field pathField = new StringField(LuceneConstants.FILE_PATH, file.toString(), Field.Store.YES);
			doc.add(pathField);

			// add the name Field
			doc.add(new StringField(LuceneConstants.FILE_NAME, file.getFileName().toString(), Field.Store.YES));

			// add the content
			doc.add(new TextField(LuceneConstants.CONTENTS, new BufferedReader(new InputStreamReader(stream))));

			System.out.println("adding " + file);
			writer.addDocument(doc);

		} catch (Exception e) {
			System.out.println("Could not add: " + file);
		}
	}

	static void indexHtmlDoc(final IndexWriter writer, Path file) throws IOException {
		try (InputStream stream = Files.newInputStream(file)) {

			// create empty document
			Document doc = new Document();

			// add the last modification time field
			doc.add(new LongPoint(LuceneConstants.LAST_MODIFICATION_TIME, file.toFile().lastModified()));
			
			// add the path Field
			Field pathField = new StringField(LuceneConstants.FILE_PATH, file.toString(), Field.Store.YES);
			doc.add(pathField);

			// add the name Field
			doc.add(new StringField(LuceneConstants.FILE_NAME, file.getFileName().toString(), Field.Store.YES));

			// add the title Field
//			doc.add(new StringField(LuceneConstants.TITLE, , Field.Store.YES));

			// add the date Field
//			doc.add(new StringField(LuceneConstants.DATE, , Field.Store.YES));

			// add the content
			doc.add(new TextField(LuceneConstants.CONTENTS, new BufferedReader(new InputStreamReader(stream))));

			System.out.println("adding " + file);
			writer.addDocument(doc);

		} catch (Exception e) {
			System.out.println("Could not add: " + file);
		}
	}

}
