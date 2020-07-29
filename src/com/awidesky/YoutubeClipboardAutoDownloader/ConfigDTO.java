package com.awidesky.YoutubeClipboardAutoDownloader;

import java.io.Serializable;

public class ConfigDTO implements Serializable{

	private static final long serialVersionUID = 1184293966501697035L;
	
	private String saveto;
	private String format;
	private String quality;

	public ConfigDTO(String saveto, String extension, String quality) {

		this.saveto = saveto;
		this.format = extension;
		this.quality = quality;
	
	}

	
	public String getSaveto() {
		
		return saveto;
		
	}
	
	public void setSaveto(String saveto) {
		
		this.saveto = saveto;
		
	}
	
	public String getFormat() {
		
		return format;
		
	}
	
	public void setFormat(String extension) {
		
		this.format = extension;
		
	}
	
	public String getQuality() {
		
		return quality;
		
	}
	
	public void setQuality(String quality) {
		
		this.quality = quality;
		
	}
	
}
