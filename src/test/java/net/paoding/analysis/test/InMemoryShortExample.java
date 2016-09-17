package net.paoding.analysis.test;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;

/**
 * 在内存中创建Lucene索引的简单测试
 */
public class InMemoryShortExample {
	private static final Analyzer ANALYZER = new PaodingAnalyzer();

	public static void main(String[] args) {
        try {
			//Directory idx = FSDirectory.open(new File("F:/data/lucene/fix"));
            Directory idx = new RAMDirectory();
			IndexWriterConfig iwc = new IndexWriterConfig(ANALYZER);

			IndexWriter writer = new IndexWriter(idx, iwc);

			writer.addDocument(createDocument("维基百科:关于中文维基百科", "维基百科:关于中文维基百科"));

            writer.commit();

            IndexSearcher searcher = new IndexSearcher(DirectoryReader.open(idx));
            Assert.assertTrue(searcher.search(new QueryParser(
                    "title", ANALYZER).parse("title:'维基'"), 10).totalHits > 0);
		} catch (Exception ioe) {
			ioe.printStackTrace();
		}
	}

	private static Document createDocument(String title, String content) {
		Document doc = new Document();
		doc.add(new TextField("title", title, Store.YES));
		doc.add(new TextField("content", content, Store.YES));
		return doc;
	}
}
