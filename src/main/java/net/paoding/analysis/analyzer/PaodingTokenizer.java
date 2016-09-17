/**
 * Copyright 2007 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.paoding.analysis.analyzer;

import net.paoding.analysis.Constants;
import net.paoding.analysis.analyzer.impl.MaxWordLengthTokenCollector;
import net.paoding.analysis.analyzer.impl.MostWordsTokenCollector;
import net.paoding.analysis.knife.*;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

/**
 * PaodingTokenizer是基于“庖丁解牛”框架的TokenStream实现，为PaodingAnalyzer使用。
 * 
 * @author Lanxiaowei
 * 
 * @see Beef
 * @see Knife
 * @see Paoding
 * @see Tokenizer
 * @see PaodingAnalyzer
 * 
 * @see Collector
 * @see TokenCollector
 * @see net.paoding.analysis.analyzer.impl.MaxWordLengthTokenCollector
 * @see MostWordsTokenCollector
 * 
 * @since 1.0
 */
public final class PaodingTokenizer extends Tokenizer implements Collector {
	/**
	 * 从input读入的总字符数
	 */
	private int inputLength;

	private static final int bufferLength = 128;

	/**
	 * 接收来自{@link #input}的文本字符
	 * 
	 * @see #incrementToken()
	 */
	protected final char[] buffer = new char[bufferLength];

	/**
	 * {@link #buffer}[0]在{@link #input}中的偏移
	 * 
	 * @see #collect(String, int, int)
	 * @see #incrementToken()
	 */
	private int offset;

	private final Beef beef = new Beef(buffer, 0, 0);

	private int dissected;

	/**
	 * 用于分解beef中的文本字符，由PaodingAnalyzer提供
	 * 
	 * @see #incrementToken()
	 */
	private Knife knife;

	/**
	 * 切分句子后在这里保存所有的词
	 */
	private TokenCollector tokenCollector;

	/**分词模式*/
	private String mode;

	/**庖丁解牛分词器需要的properties属性文件加载路径*/
	private String propertiesPath;

	/**
	 * tokens迭代器，用于next()方法顺序读取tokens中的Token对象
	 * @see #tokenCollector
	 * @see #incrementToken()
	 */
	private Iterator<Token> tokenIteractor;
	private CharTermAttribute termAtt;
	private OffsetAttribute offsetAtt;
    private PositionIncrementAttribute positionIncrementAttribute;
	/**词性*/
	private TypeAttribute typeAtt;

	public PaodingTokenizer(String propertiesPath,String mode,Knife knife) {
		this.propertiesPath = propertiesPath;
		init();
		setKnife(knife);
		this.mode = mode;
		this.tokenCollector = createTokenCollector(this.mode);
	}

	public PaodingTokenizer(String mode,Knife knife) {
		init();
		setKnife(knife);
		this.mode = mode;
		this.tokenCollector = createTokenCollector(this.mode);
	}

	public PaodingTokenizer(String propertiesPath,String mode) {
		this.propertiesPath = propertiesPath;
		init();
		this.mode = mode;
		this.tokenCollector = createTokenCollector(this.mode);
	}

	public PaodingTokenizer(Knife knife) {
		init();
		setKnife(knife);
		this.tokenCollector = createTokenCollector(this.mode);
	}

	public PaodingTokenizer(String mode) {
		init();
		this.mode = mode;
		this.tokenCollector = createTokenCollector(this.mode);
	}

	public PaodingTokenizer() {
		init();
		this.tokenCollector = createTokenCollector(this.mode);
	}

	private void init() {
		this.termAtt = addAttribute(CharTermAttribute.class);
		this.offsetAtt = addAttribute(OffsetAttribute.class);
		this.positionIncrementAttribute = addAttribute(PositionIncrementAttribute.class);
		this.typeAtt = addAttribute(TypeAttribute.class);
		buildKnife();
	}

	private TokenCollector createTokenCollector(String mode) {
		if(null == mode || "".equals(mode)) {
			return new MostWordsTokenCollector();
		}
		if ("most-words".equalsIgnoreCase(mode)
				|| "default".equalsIgnoreCase(mode)) {
			return new MostWordsTokenCollector();
		}
		if ("max-word-length".equalsIgnoreCase(mode)) {
			return new MaxWordLengthTokenCollector();
		}
		return new MostWordsTokenCollector();
	}

	/**
	 * 制作“刀”
	 * @return
     */
	private void buildKnife() {
		// 根据PaodingMaker说明，
		// 1、多次调用getProperties()，返回的都是同一个properties实例(只要属性文件没发生过修改)
		// 2、相同的properties实例，PaodingMaker也将返回同一个Paoding实例
		// 根据以上1、2点说明，在此能够保证多次创建PaodingAnalyzer并不会多次装载属性文件和词典
		if(null == this.propertiesPath ||
				"".equals(this.propertiesPath)) {
			this.propertiesPath = PaodingMaker.DEFAULT_PROPERTIES_PATH;
		}
		Properties properties = PaodingMaker.getProperties(propertiesPath);
		Paoding knife = PaodingMaker.make(properties);
		if (null == knife) {
			throw new NullPointerException("knife should be set before token");
		}
		//备好“刀”
		setKnife(knife);
		String mode = Constants
				.getProperty(properties, Constants.ANALYZER_MODE);
		this.mode = mode;
	}

	public TokenCollector getTokenCollector() {
		return tokenCollector;
	}

	public void setTokenCollector(TokenCollector tokenCollector) {
		this.tokenCollector = tokenCollector;
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
		if((null == this.knife) || (null != this.knife && null != knife)) {
			this.knife = knife;
		}
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void collect(String word, int offset, int end) {
		tokenCollector.collect(word, this.offset + offset, this.offset + end);
	}

	public int getInputLength() {
		return inputLength;
	}

	@Override
	public boolean incrementToken() throws IOException {
		// 已经穷尽tokensIteractor的Token对象，则继续请求reader流入数据
		while (tokenIteractor == null || !tokenIteractor.hasNext()) {
			int read = 0;
			// 重新从reader读入字符前，buffer中还剩下的字符数，
			// 负数表示当前暂不需要从reader中读入字符
			int remainning = -1;
			if (dissected >= beef.length()) {
				remainning = 0;
			} else if (dissected < 0) {
				remainning = bufferLength + dissected;
			}
			if (remainning >= 0) {
				if (remainning > 0) {
					System.arraycopy(buffer, -dissected, buffer, 0, remainning);
				}
				read = input
						.read(buffer, remainning, bufferLength - remainning);
				inputLength += read;
				int charCount = remainning + read;
				if (charCount < 0) {
					// reader已尽，按接口next()要求返回null.
					return false;
				}
				if (charCount < bufferLength) {
					buffer[charCount++] = 0;
				}
				// 构造“牛”，并使用knife“解”之
				beef.set(0, charCount);
				offset += Math.abs(dissected);
				// offset -= remainning;
				dissected = 0;
			}
			dissected = this.knife.dissect(this, beef, dissected);
			// offset += read;// !!!
			tokenIteractor = tokenCollector.iterator();
		}

        if(tokenIteractor.hasNext()) {
            // 返回tokensIteractor下一个Token对象
            Token token = tokenIteractor.next();
            termAtt.setEmpty();
            termAtt.append(token);
            offsetAtt.setOffset(correctOffset(token.startOffset()),
                    correctOffset(token.endOffset()));
            positionIncrementAttribute.setPositionIncrement(token.endOffset());
            return true;
        }
		return tokenIteractor.hasNext();
	}

	@Override
	public void reset() throws IOException {
		super.reset();
		this.offset = 0;
		this.inputLength = 0;
        this.tokenIteractor = null;
		this.dissected = 0;
		this.beef.set(0, 0);
	}
}
