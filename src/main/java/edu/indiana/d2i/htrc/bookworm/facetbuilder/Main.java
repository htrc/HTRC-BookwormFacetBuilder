package edu.indiana.d2i.htrc.bookworm.facetbuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class Main {
	public static void main(String[] args) {
		Config config = Config.INSTANCE;
		config.parseCmdLineArgs(args);
		
		// read the facet specifications from the given file, and build output JSON objects for each facet
		List<BookwormFacet> facets = readFacetSpecs();
		HTRCBookwormDBClient htrcBookwormDBClient = new HTRCBookwormDBClient(config);
		JsonArray uiComponentsForFacets = createJsonForFacets(facets, htrcBookwormDBClient);
		
		// read the options template from the given file, and modify it to obtain the output JSON; specifically,
		// add the JSON objects for each facet, and set the name of the database
		OptionsTemplate optionsTemplate = readOptionsTemplate();
		optionsTemplate.insertInUiComponents(uiComponentsForFacets);
		optionsTemplate.setValueOfKey(config.getDatabaseNameKey(), config.getHTRCBookwormDBName());
		JsonObject obj = optionsTemplate.getJsonObject();

		// write the output JSON to file
    	try (PrintWriter writer = new PrintWriter(config.getOutputFilename(), "UTF-8")) {
        	Gson gson = new GsonBuilder().setPrettyPrinting().create();
        	String jsonOutput = gson.toJson(obj);
        	writer.println(jsonOutput);
        	System.out.println("Output in " + config.getOutputFilename() + ".");
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
			System.err.println("Error: unable to write to output file " + config.getOutputFilename() + ", " + e);
			System.exit(1);
		} 
	}
	
	private static List<BookwormFacet> readFacetSpecs() {
		Config config = Config.INSTANCE;

		FacetSpecsReader facetSpecsReader = new FacetSpecsReader(config);
		List<BookwormFacet> facets = null;
		try {
			facets = facetSpecsReader.getFacets();
		} catch (IOException e) {
			System.err.println("Error: unable to read facet specifications file " + config.getFacetSpecsFilename());
			System.err.println(e);
			System.exit(1);
		} catch (FacetSpecsFormatException e) {
			System.err.println(e);
			System.err.println(FacetSpecsReader.facetSpecsFormatInfoMsg());
			System.exit(1);
		}
		return facets;
	}
	
	private static OptionsTemplate readOptionsTemplate() {
		Config config = Config.INSTANCE;

		String uiComponentsKey = config.getUiComponentsKey();
		String facetsPlaceHolderStr = config.getFacetsPlaceHolderStr();
		OptionsTemplate optionsTemplate = null;
		try {
			optionsTemplate = new OptionsTemplate(config);
		} catch (OptionsTemplateFormatException e) {
			System.err.println(e);
			System.err.println(OptionsTemplate.optionsTemplateFormatInfoMsg(uiComponentsKey, facetsPlaceHolderStr));
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Error: unable to read options template file " + config.getOptionsTemplateFilename());
			System.err.println(e);
			System.exit(1);
		} catch (JsonParseException e) {
			System.err.println("Invalid JSON in " + config.getOptionsTemplateFilename());
			System.err.println(e);
			System.exit(1);
		}
		return optionsTemplate;
	}
	
	private static JsonArray createJsonForFacets(List<BookwormFacet> facets, HTRCBookwormDBClient htrcBookwormDBClient) {
		JsonArray result = new JsonArray();
		for (BookwormFacet f: facets) {
			try {
				JsonObject facetMetadata = f.facetMetadata(htrcBookwormDBClient);
				if (facetMetadata != null) {
					result.add(facetMetadata);
				}
			} catch (SQLException e) {
				System.err.println("Exception while processing table " + f.getTableName() + ": " + e);
				System.err.println(f.getFacetName() + " not included in result.");
			}
		}
		return result;
	}
}
