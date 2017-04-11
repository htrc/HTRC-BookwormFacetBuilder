package edu.indiana.d2i.htrc.bookworm.facetbuilder;

public class Constants {
	public static final String PROPERTIES_FILENAME = "config.properties";
	
	// names of properties 
	public static final String HTRC_BOOKWORM_DB_SERVER = "HTRC-bookworm-db.server";
	public static final String HTRC_BOOKWORM_DB_NAME = "HTRC-bookworm-db.name";
	public static final String HTRC_BOOKWORM_DB_USER = "HTRC-bookworm-db.user";
	public static final String HTRC_BOOKWORM_DB_PSSWD = "HTRC-bookworm-db.psswd";
	public static final String FACET_SPECS_FILE = "facet.specs.file";
	public static final String OPTIONS_TEMPLATE_FILE = "options.template.file";
	public static final String UI_COMPONENTS_KEY = "ui.components.key";
	public static final String FACETS_PLACE_HOLDER = "facets.placeholder";
	public static final String DATABASE_NAME_KEY = "database.name.key";
	public static final String OUTPUT_FILE = "output.file";
	
	// default values of properties
	public static final String DEFAULT_HTRC_BOOKWORM_DB_SERVER = "localhost";
	public static final String DEFAULT_HTRC_BOOKWORM_DB_NAME = "hathipd3rd";
	public static final String DEFAULT_HTRC_BOOKWORM_DB_USER = "bookworm";
	public static final String DEFAULT_FACET_SPECS_FILE = "facetSpecs.txt";
	public static final String DEFAULT_OPTIONS_TEMPLATE_FILE = "optionsTemplate.json";
	public static final String DEFAULT_UI_COMPONENTS_KEY = "ui_components";
	public static final String DEFAULT_FACETS_PLACE_HOLDER = "facetsPlaceHolder";
	public static final String DEFAULT_DATABASE_NAME_KEY = "dbname";
	public static final String DEFAULT_OUTPUT_FILE = "options.json";
	
	private Constants() {
		throw new AssertionError();
	}

}
