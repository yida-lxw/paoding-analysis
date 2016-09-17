/**
 *
 */
package net.paoding.analysis.test;

import java.io.IOException;
import java.io.StringReader;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;


public class AnalysisSimpleTest {

    public static void main(String[] args) throws IOException {
        Analyzer paodingAnalyzer = new PaodingAnalyzer();
        String text = "奶茶妹妹抱女出镜 小宝贝头戴粉色小发卡";
        AnalyzerUtils.displayTokens(paodingAnalyzer,text);
    }

}
