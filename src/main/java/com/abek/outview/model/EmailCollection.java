package com.abek.outview.model;

import java.util.ArrayList;
import java.util.List;

public class EmailCollection {
	private List<Sender> senders;
	private List<Email> emails;

	public EmailCollection() {
		super();
	}

	public List<Sender> getSenders() {
		if(senders == null){
			senders = new ArrayList<>();
		}
		return senders;
	}

	public void setSenders(List<Sender> senders) {
		this.senders = senders;
	}

	public List<Email> getEmails() {
		if(emails == null){
			emails = new ArrayList<>();
		}
		return emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}

	@Override
	public String toString() {
		return "Service [senders=" + senders + "]";
	}
}
