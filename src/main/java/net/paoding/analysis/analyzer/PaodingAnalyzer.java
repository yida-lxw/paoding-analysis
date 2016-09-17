package net.paoding.analysis.analyzer;

import java.io.Reader;

import net.paoding.analysis.analyzer.estimate.TryPaodingAnalyzer;
import net.paoding.analysis.knife.Knife;
import org.apache.lucene.analysis.Analyzer;

/**
 * PaodingAnalyzer是基于“庖丁解牛”框架的Lucene词语分析器，是“庖丁解牛”框架对Lucene的适配器。
 * <p>
 *
 * PaodingAnalyzer是线程安全的：并发情况下使用同一个PaodingAnalyzer实例是可行的。<br>
 * PaodingAnalyzer是可复用的：推荐多次同一个PaodingAnalyzer实例。
 * <p>
 *
 * PaodingAnalyzer自动读取类路径下的paoding-analysis.properties属性文件，装配PaodingAnalyzer
 * <p>
 *
 * @author Lanxiaowei
 *
 * @see PaodingAnalyzer
 *
 * @since 1.0
 *
 */
public class PaodingAnalyzer extends Analyzer {
	/**庖丁解牛分词器需要的properties属性文件加载路径*/
	private String propertiesPath;
	/**分词模式*/
	private String mode;
	/**用于向PaodingTokenizer提供，分解文本字符*/
	private Knife knife;

	/**
	 * 根据类路径下的paoding-analysis.properties构建一个PaodingAnalyzer对象
	 * <p>
	 * 在一个JVM中，可多次创建，而并不会多次读取属性文件，不会重复读取字典。
	 */
	public PaodingAnalyzer(String propertiesPath,String mode,Knife knife) {
		this.propertiesPath = propertiesPath;
		this.mode = mode;
		this.knife = knife;
	}

	public PaodingAnalyzer(String mode,Knife knife) {
		this.mode = mode;
		this.knife = knife;
	}

	public PaodingAnalyzer(String propertiesPath,String mode) {
		this.propertiesPath = propertiesPath;
		this.mode = mode;
	}

	public PaodingAnalyzer(Knife knife) {
		this.knife = knife;
	}

	public PaodingAnalyzer(String mode) {
		this.mode = mode;
	}

	public PaodingAnalyzer() {}

	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
		return new TokenStreamComponents(new PaodingTokenizer(
				propertiesPath,mode,knife));
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getPropertiesPath() {
		return propertiesPath;
	}

	public void setPropertiesPath(String propertiesPath) {
		this.propertiesPath = propertiesPath;
	}

	public Knife getKnife() {
		return knife;
	}

	public void setKnife(Knife knife) {
		this.knife = knife;
	}

	/**
	 * 本方法为PaodingAnalyzer附带的测试评估方法。 <br>
	 * 执行之可以查看分词效果。以下任选一种方式进行:
	 * <p>
	 *
	 * java net...PaodingAnalyzer<br>
	 * java net...PaodingAnalyzer --help<br>
	 * java net...PaodingAnalyzer 中华人民共和国<br>
	 * java net...PaodingAnalyzer -m max 中华人民共和国<br>
	 * java net...PaodingAnalyzer -f c:/text.txt<br>
	 * java net...PaodingAnalyzer -f c:/text.txt -c utf-8<br>
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		if (System.getProperty("paoding.try.app") == null) {
			System.setProperty("paoding.try.app", "PaodingAnalyzer");
			System.setProperty("paoding.try.cmd", "java PaodingAnalyzer");
		}
		TryPaodingAnalyzer.main(args);
	}
}
