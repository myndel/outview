package com.abek.outview.model;

import java.util.ArrayList;
import java.util.List;

public class Sender {
	
	private String		id;
	private String		mailAddress;
	private String		repository;
	private List<Email>	emails;
	private List<EmailSummary> emailSummaries;

	public Sender() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMailAddress() {
		return mailAddress;
	}

	public void setMailAddress(String mailAddress) {
		this.mailAddress = mailAddress;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public List<EmailSummary> getEmailSummaries() {
		if(emailSummaries == null){
			emailSummaries = new ArrayList<>();
		}
		return emailSummaries;
	}

	public void setEmailSummaries(List<EmailSummary> emailSummaries) {
		this.emailSummaries = emailSummaries;
	}

	public List<Email> getEmails() {
		if (emails == null) {
			emails = new ArrayList<>();
		}
		return emails;
	}

	public void setEmails(List<Email> emails) {
		this.emails = emails;
	}

	@Override
	public String toString() {
		return "Sender [id=" + id + ", mailAddress=" + mailAddress + ", repository=" + repository + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Sender other = (Sender) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
