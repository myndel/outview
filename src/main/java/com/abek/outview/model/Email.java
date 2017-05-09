package com.abek.outview.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Objet mail
 * @author abekh
 *
 */
public class Email {

	private String from;
	private String subject;
	private String body;
	private int index;
	private List<PJ> attachments;
	
	public Email() {
		super();
	}

	public Email(String from, String subject, List<PJ> attachments) {
		super();
		this.from = from;
		this.subject = subject;
		this.attachments = attachments;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public List<PJ> getAttachments() {
		if(attachments == null){
			attachments = new ArrayList<PJ>();
		}
		return attachments;
	}

	public void setAttachments(List<PJ> attachments) {
		this.attachments = attachments;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attachments == null) ? 0 : attachments.hashCode());
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Email other = (Email) obj;
		if (attachments == null) {
			if (other.attachments != null)
				return false;
		} else if (!attachments.equals(other.attachments))
			return false;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Mail [from=" + from + ", subject=" + subject + ", attachments=" + attachments + "]";
	}
	
}
