package com.abek.outview.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.abek.outview.AppConstants;
import com.abek.outview.exception.FilterException;
import com.abek.outview.exception.ManagerException;
import com.abek.outview.manager.interfaces.IManager;
import com.abek.outview.model.Email;

public class FilterManager implements IManager{
	private static Logger LOGGER = Logger.getLogger(FilterManager.class);

	private List<String> from;
	private List<String> keywords;
	private String pj;
	
	private static FilterManager _instance;
	private ConfigManager config;
	
	private FilterManager(){
		super();
		from = new ArrayList<>();
		keywords = new ArrayList<>();
		pj = AppConstants.FILTER_ALL;
	}

	private FilterManager(ConfigManager config){
		this();
		this.config = config;
	}
	
	public static FilterManager getInstance(ConfigManager config){
		if(_instance == null){
			_instance = new FilterManager(config);
		}
		
		return _instance;
	}

	@Override
	public void init() throws ManagerException {
		LOGGER.debug("[FILTER] Initializing filter configuration");
		if(config == null){
			LOGGER.error("[FILTER] config manager must not be null");
			throw new FilterException("config manager must not be null");
		}
		from.clear();
		keywords.clear();
		
		LOGGER.debug("[FILTER] Reading properties");
		Properties properties = config.getProperties();
		//reading raw properties
		String rawFrom = properties.getProperty(AppConstants.PROPERTY_FILTER_FROM, "");
		String rawTags = properties.getProperty(AppConstants.PROPERTY_FILTER_WORDS_SUBJECT, "");
		String rawHasPj = properties.getProperty(AppConstants.PROPERTY_FILTER_HAS_PJ, AppConstants.FILTER_ALL);
		
		if(rawFrom != null && !rawFrom.trim().isEmpty()){
			String[] fromSplitted = rawFrom.split(";");
			from.addAll(Arrays.asList(fromSplitted));
			LOGGER.debug("[FILTER] FROM Filter: "+from.toString());
		}
		
		if(rawTags != null && !rawTags.trim().isEmpty()){
			String[] tagsSplitted = rawTags.split(";");
			keywords.addAll(Arrays.asList(tagsSplitted));
			LOGGER.debug("[FILTER] TAGS Filter: "+keywords.toString());
		}
		
		if(rawHasPj != null && !rawHasPj.trim().isEmpty()){
			pj = rawHasPj.toUpperCase().trim();
			//Contrôle des valeurs
			switch (rawHasPj.toUpperCase()) {
				case AppConstants.FILTER_ALL:
				case AppConstants.FILTER_WITH_PJ:
				case AppConstants.FILTER_WITHOUT_PJ:
					break;
				default:
					LOGGER.error(String.format("[FILTER] Unknown value \"%s\" PJ filter: required values <empty>, all, without or with only", rawHasPj));
					throw new FilterException(String.format("Unknown \"%s\" PJ filter: required values <empty>, all, without or with only", rawHasPj));
			}
		}
		
		LOGGER.debug("[FILTER] Finished initializing filter configuration");
	}
	
	public boolean select(Email email){
		boolean select = true;
		
		//Filtre PJ
		if(!AppConstants.FILTER_ALL.equals(pj)){
			if(AppConstants.FILTER_WITH_PJ.equals(pj) ){
				select &= !email.getAttachments().isEmpty();
			}
			else if(AppConstants.FILTER_WITHOUT_PJ.equals(pj)){
				select &= email.getAttachments().isEmpty();
			}
		}
		
		//Filtre expéditeur
		if(select && !this.from.isEmpty() && email.getFrom() != null){
			//On sélectionne si l'expéditeur correspond à au moins un élement du filtre
			select &= this.from.stream().anyMatch(from -> email.getFrom().contains(from));
			LOGGER.debug(String.format("[FILTER] Filtering from \"%s\", result %s", email.getFrom(), ""+select));
		}
		
		//Filtre mot clé
		if(select && !this.keywords.isEmpty() && email.getSubject() != null){
			//On sélectionne si l'expéditeur correspond à au moins un élement du filtre
			select &= this.keywords.stream().anyMatch(keyword -> email.getSubject().toUpperCase().contains(keyword.toUpperCase()));
		}
		
		return select;
	}
}
