package edu.indiana.d2i.htrc.bookworm.facetbuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.martiansoftware.jsap.JSAPResult;

public enum Config implements HTRCBookwormDBSettings, FacetSpecsFileSettings, OptionsTemplateSettings {
	INSTANCE;
	
	private String htrcBookwormDBServer;
	private String htrcBookwormDBName;
	private String htrcBookwormDBUser;
	private String htrcBookwormDBPsswd;
	private String defaultFacetSpecsFilename;
	private String userFacetSpecsFilename = null;
	private String defaultOptionsTemplateFilename;
	private String userOptionsTemplateFilename = null;
	private String uiComponentsKey;
	private String facetsPlaceHolderStr;
	private String databaseNameKey;
	private String outputFilename;
	
	Config() {
		Properties props = new Properties();
		String propFile = Constants.PROPERTIES_FILENAME;
		
		try (InputStream is = getClass().getClassLoader().getResourceAsStream(propFile)) {
			if (is != null) {
				props.load(is);
				htrcBookwormDBServer = props.getProperty(Constants.HTRC_BOOKWORM_DB_SERVER, Constants.DEFAULT_HTRC_BOOKWORM_DB_SERVER);
				htrcBookwormDBName = props.getProperty(Constants.HTRC_BOOKWORM_DB_NAME, Constants.DEFAULT_HTRC_BOOKWORM_DB_NAME);
				htrcBookwormDBUser = props.getProperty(Constants.HTRC_BOOKWORM_DB_USER, Constants.DEFAULT_HTRC_BOOKWORM_DB_USER);
				htrcBookwormDBPsswd = props.getProperty(Constants.HTRC_BOOKWORM_DB_PSSWD);
				if (htrcBookwormDBPsswd == null) {
					System.err.println("No password specified to access Bookworm database");
					System.exit(1);
				}
				defaultFacetSpecsFilename = props.getProperty(Constants.FACET_SPECS_FILE, Constants.DEFAULT_FACET_SPECS_FILE);
				defaultOptionsTemplateFilename = props.getProperty(Constants.OPTIONS_TEMPLATE_FILE, Constants.DEFAULT_OPTIONS_TEMPLATE_FILE);
				uiComponentsKey = props.getProperty(Constants.UI_COMPONENTS_KEY, Constants.DEFAULT_UI_COMPONENTS_KEY);
				facetsPlaceHolderStr = props.getProperty(Constants.FACETS_PLACE_HOLDER, Constants.DEFAULT_FACETS_PLACE_HOLDER);
				databaseNameKey = props.getProperty(Constants.DATABASE_NAME_KEY, Constants.DEFAULT_DATABASE_NAME_KEY);
				outputFilename = props.getProperty(Constants.OUTPUT_FILE, Constants.DEFAULT_OUTPUT_FILE);
			} else {
				System.err.println("Unable to find properties file " + propFile);
				System.exit(1);
			}	
		} catch (IOException e) {
			System.err.println("Error while reading properties from file " + propFile);
			System.exit(1);
		}
	}
	
	public void parseCmdLineArgs(String[] args) {
		// parse command line arguments
		JSAPResult cmdLine = ArgumentParser.parseArguments(args);
		if (cmdLine == null) {
			printUsage();
			System.exit(1);
		}
		else {
			String dbName = cmdLine.getString("database");
			if (dbName != null) {
				htrcBookwormDBName = dbName; 
			}
			
			String username = cmdLine.getString("username");
			if (username != null) {
				htrcBookwormDBUser = username;
			}
			
			String psswd = cmdLine.getString("password");
			if (psswd != null) {
				htrcBookwormDBPsswd = psswd;
			}
			
			File newFacetSpecsFile = cmdLine.getFile("facetSpecsFile");
			if (newFacetSpecsFile != null) {
				userFacetSpecsFilename = newFacetSpecsFile.toString();
			}
			
			File newOptionsTemplateFile = cmdLine.getFile("optionsTemplateFile");
			if (newOptionsTemplateFile != null) {
				userOptionsTemplateFilename = newOptionsTemplateFile.toString();
			}

			File newOutputFile = cmdLine.getFile("outputFile");
			if (newOutputFile != null) {
				outputFilename = newOutputFile.toString();
			}
		}
	}
	
	private static void printUsage() {
		System.out.println("Usage: java -jar HTRC-BookwormFacetBuilder.jar [-d database]");
		System.out.println("         [-u username]");
		System.out.println("         [-p password]");
		System.out.println("         [-f facetSpecsFilepath]");
		System.out.println("         [-t optionsTemplateFilepath]");
		System.out.println("         [-o outputFilepath]");
	}

	@Override
	public String getHTRCBookwormDBServer() {
		return htrcBookwormDBServer;
	}
	
	@Override
	public String getHTRCBookwormDBName() {
		return htrcBookwormDBName;
	}
	
	@Override
	public String getHTRCBookwormDBUser() {
		return htrcBookwormDBUser;
	}
	
	@Override
	public String getHTRCBookwormDBPsswd() {
		return htrcBookwormDBPsswd;
	}
	
	@Override
	public String getFacetSpecsFilename() {
		if (userFacetSpecsFilename == null) {
			return defaultFacetSpecsFilename;
		} else {
			return userFacetSpecsFilename;
		}
	}
	
	@Override
	public boolean openFacetSpecsFileAsResource() {
		return (userFacetSpecsFilename == null);
	}

	@Override
	public String getOptionsTemplateFilename() {
		if (userOptionsTemplateFilename == null) {
			return defaultOptionsTemplateFilename;
		} else {
			return userOptionsTemplateFilename;
		}
	}

	@Override
	public boolean openOptionsTemplateFileAsResource() {
		return (userOptionsTemplateFilename == null);
	}

	@Override
	public String getUiComponentsKey() {
		return uiComponentsKey;
	}
	
	@Override
	public String getFacetsPlaceHolderStr() {
		return facetsPlaceHolderStr;
	}
	
	public String getDatabaseNameKey() {
		return databaseNameKey;
	}

	public String getOutputFilename() {
		return outputFilename;
	}
}
