package org.archive.crawler.extractor;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.archive.crawler.datamodel.CrawlURI;
import org.archive.io.ReplayCharSequence;
import org.archive.util.HttpRecorder;

public class ExtractorSohu extends Extractor {

	private static final long serialVersionUID = -7914995152462902519L;

	public ExtractorSohu(String name, String description) {
		super(name, description);
	}
	
	public ExtractorSohu(String name) {
		super(name, "sohu news extractor");
	}
	
	//<a class="" href="xxxx" target="_blank">
	//http://news.sohu.com/20131020/n388534488.shtml
//	private static final String A_HERF = "<a(.*)href\\s*=\\s*(\"([^\"]*)\"|[^\\s>])(.*)>";
	private static final String A_HREF = "<a(.*)href\\s*=\\s*(\'([^\']*)\'|\"([^\"]*)\"|[^\\s>]*)(.*)>";
	private static final String NEWS_SOHU = "http://news.sohu.com/(.*)/n(.*).shtml";

	@Override
	protected void extract(CrawlURI curi) {
		String url = "";
		try {
			HttpRecorder hr = curi.getHttpRecorder();
			if(hr == null){
				throw new IOException("HttpRecorder is null");
			}
			ReplayCharSequence cs = hr.getReplayCharSequence();
			if(cs == null){
				return;
			}
			String context = cs.toString();
			
			Pattern pattern = Pattern.compile(A_HREF, Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(context);
			while(matcher.find()){
				url = matcher.group(2);
				System.out.println("url:"+url);
				url = url.replace("\"", "").replace("\'", "");
//				url = url.replace("\"", "");
				if(url.matches(NEWS_SOHU)){
//					System.out.println("true");
					curi.createAndAddLinkRelativeToBase(url, context, Link.NAVLINK_HOP);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
