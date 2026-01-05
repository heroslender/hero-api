package com.github.heroslender.hero_api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("storage")
public class StorageProperties {

	/**
	 * Folder location for storing files
	 */
	@Getter
	@Setter
	private String versionsLocation = "versions";


	/**
	 * Folder location for storing plugin thumbnail files
	 */
	@Getter
	@Setter
	private String thumbnailsLocation = "thumbnails";
}