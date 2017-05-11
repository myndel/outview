package com.abek.outview.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

public class HTMLUtils {
	public static String extractText(String html) throws IOException {
	    final ArrayList<String> list = new ArrayList<String>();

	    ParserDelegator parserDelegator = new ParserDelegator();
	    ParserCallback parserCallback = new ParserCallback() {
	        public void handleText(final char[] data, final int pos) { 
	            list.add(new String(data));
	        }	
	        public void handleComment(final char[] data, final int pos) { }
	        public void handleError(final java.lang.String errMsg, final int pos) { }
	    };
	    parserDelegator.parse(new StringReader(html), parserCallback, true);

	    String text = "";

	    for(String s : list) {
	        text += " " + s;
	    }

	    return text;
	}
}
