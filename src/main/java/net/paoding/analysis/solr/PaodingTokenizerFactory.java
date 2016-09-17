package net.paoding.analysis.solr;

import net.paoding.analysis.analyzer.PaodingTokenizer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.AttributeFactory;

import java.util.Map;

/**
 * Created by Lanxiaowei
 * PaodingTokenizerFactory: 构建PaodingTokenizer的工厂
 * 兼容Solr5.x
 */
public class PaodingTokenizerFactory extends TokenizerFactory {
    /**分词模式*/
    private String mode;

    /**庖丁解牛分词器需要的properties属性文件加载路径*/
    private String propertiesPath;

    public PaodingTokenizerFactory(Map<String, String> args) {
        super(args);
        propertiesPath = get(args, "propertiesPath","classpath:paoding-analysis.properties");
        mode = get(args, "mode","most-words");
    }

    @Override
    public Tokenizer create(AttributeFactory factory) {
        return new PaodingTokenizer(propertiesPath,mode);
    }
}
