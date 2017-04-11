package edu.indiana.d2i.htrc.bookworm.facetbuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

interface FacetSpecsFileSettings {
	public String getFacetSpecsFilename();
	public boolean openFacetSpecsFileAsResource();
}

public class FacetSpecsReader {
	private static final String[] allowedFacetValueOrders = {"default", "alphabetical"};
	private String facetSpecsFilename;
	private boolean openAsResource;
	
	public FacetSpecsReader(FacetSpecsFileSettings settings) {
		this.facetSpecsFilename = settings.getFacetSpecsFilename();
		this.openAsResource = settings.openFacetSpecsFileAsResource();
	}
	
	private static boolean contains(String[] a, String str) {
		for (String strInArray: a) {
			if (strInArray.equals(str))
				return true;
		}
		return false;
	}
	
	private static boolean isValidFacetValueOrder(String facetValueOrder) {
		return contains(allowedFacetValueOrders, facetValueOrder);
	}
	
	public List<BookwormFacet> getFacets() throws FileNotFoundException, IOException, FacetSpecsFormatException {
		if (this.openAsResource) {
			return getDefaultFacets();
		} else {
			return getUserFacets();
		}
	}
	
	public List<BookwormFacet> getDefaultFacets()  throws FileNotFoundException, IOException, FacetSpecsFormatException {
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(facetSpecsFilename)))) {
			return getFacets(reader);
		}
	}
	
	public List<BookwormFacet> getUserFacets() throws FileNotFoundException, IOException, FacetSpecsFormatException {
		try (BufferedReader reader = new BufferedReader(new FileReader(facetSpecsFilename))) {
			return getFacets(reader);
		}
	}
	
	private List<BookwormFacet> getFacets(BufferedReader reader) throws IOException, FacetSpecsFormatException {
		String line;
		List<BookwormFacet> result = new LinkedList<BookwormFacet>();
		while ((line = reader.readLine()) != null) {
			String trimmedLine = line.trim();
			// ignore blank lines and comment lines that start with '#'
			if (!(trimmedLine.equals("") || trimmedLine.startsWith("#"))) {
				String[] facetSpecs = trimmedLine.split(",");
				if (facetSpecs.length != 6) {
					throw new FacetSpecsFormatException("Unexpected line \"" + line + "\" in " + facetSpecsFilename);
				}
				String facetValueOrder = facetSpecs[4].trim();
				if (!isValidFacetValueOrder(facetValueOrder)) {
					throw new FacetSpecsFormatException("Unexpected facet value order " + facetValueOrder + " at line \"" + line + "\" in " + facetSpecsFilename);
				}
				int maxValues;
				try {
					maxValues = Integer.parseInt(facetSpecs[5].trim());
				} catch (NumberFormatException e) {
					throw new FacetSpecsFormatException("Limit on the no. of facet values should be an integer. Line \"" + line + "\" in " + facetSpecsFilename);
				}
				result.add(new BookwormFacet(facetSpecs[0].trim(), facetSpecs[1].trim(), facetSpecs[2].trim(), facetSpecs[3].trim(), facetValueOrder, maxValues));
			}
		}
		return result;
	}
	
	public static String facetSpecsFormatInfoMsg() {
		String str1 = "The facet specifications file should be a CSV file where each line is of the following form\n";
		String str2 = "facetName, tableName, dbcodeFieldName, valueFieldName, facetValueOrder, maxValues\n";
		String str3 = "facetValueOrder must be either \"default\" or \"alphabetical\".\n";
		String str4 = "maxValues must be an integer. To get all facet values use a negative integer.\n";
		String str5 = "For example,\n";
		String str6 = "Genre, genres__id, genres__id, genres, default, 500";
		return (str1 + str2 + str3 + str4 + str5 + str6);
	}
}
