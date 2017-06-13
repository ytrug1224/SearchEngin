package org.archive.crawler.postprocessor;

import org.archive.crawler.datamodel.CandidateURI;

public class MyFrontierScheduler extends FrontierScheduler {

	private static final long serialVersionUID = 4936731991816379250L;

	public MyFrontierScheduler(String name) {
		super(name);
	}
	
	 protected void schedule(CandidateURI caUri) {
		 if(caUri.toString().contains("news")){
			 getController().getFrontier().schedule(caUri);
		 }
	 }

}
