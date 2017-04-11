package edu.indiana.d2i.htrc.bookworm.facetbuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

interface OptionsTemplateSettings {
	public String getOptionsTemplateFilename();
	public boolean openOptionsTemplateFileAsResource();
	public String getUiComponentsKey();
	public String getFacetsPlaceHolderStr();
}

public class OptionsTemplate {
	private String optionsTemplateFilename;
	private boolean openAsResource;
	private String uiComponentsKey;
	private String facetsPlaceHolderStr;
	private JsonObject template;
	
	public OptionsTemplate(OptionsTemplateSettings settings) throws OptionsTemplateFormatException, IOException, JsonParseException {
		this.optionsTemplateFilename = settings.getOptionsTemplateFilename();
		this.openAsResource = settings.openOptionsTemplateFileAsResource();
		this.uiComponentsKey = settings.getUiComponentsKey();
		this.facetsPlaceHolderStr = settings.getFacetsPlaceHolderStr();
		this.template = readOptionsTemplateFile();
	}
	
	public JsonObject getJsonObject() {
		return this.template;
	}
	
	public JsonObject readOptionsTemplateFile() throws OptionsTemplateFormatException, IOException, JsonParseException {
		if (this.openAsResource) {
			return readDefaultOptionsTemplateFile();
		} else {
			return readUserOptionsTemplateFile();
		}
	}

	public JsonObject readDefaultOptionsTemplateFile() throws OptionsTemplateFormatException, JsonParseException, IOException {
		JsonObject result = null;
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(optionsTemplateFilename)))) {
			result = readOptionsTemplate(reader);
		}
		return result;
	}
	
	public JsonObject readUserOptionsTemplateFile() throws IOException, JsonParseException, OptionsTemplateFormatException {
		try (BufferedReader reader = new BufferedReader(new FileReader(optionsTemplateFilename))) {
			return readOptionsTemplate(reader);
		}
	}

	// reads the JSON object from reader and ensures that it has the expected form
	public JsonObject readOptionsTemplate(Reader reader) throws OptionsTemplateFormatException, JsonParseException {
		JsonParser parser = new JsonParser();
		JsonElement elem = parser.parse(reader);
		if (elem.isJsonObject()) {
			JsonObject obj = elem.getAsJsonObject();
			// the JsonObject should itself contain a value of the key uiComponentsKey, or a descendant JSON object should
			JsonObject objWithUiComponents = find(obj, this.uiComponentsKey);
			if (objWithUiComponents != null) {
				JsonElement uiComponents = objWithUiComponents.get(this.uiComponentsKey);
			    if (uiComponents.isJsonArray()) {
			    	return obj;
			    } else {
			    	throw new OptionsTemplateFormatException(templateFormatErrorMsg());
			    }
			} else {
				throw new OptionsTemplateFormatException(templateFormatErrorMsg());
			}
		} else {
			throw new OptionsTemplateFormatException(templateFormatErrorMsg());
		}
	}

	// inserts the given array of JSON objects into the array associated with uiComponentsKey in this.template;
	// this method modifies this.template in-place
	public void insertInUiComponents(JsonArray uiComponentsForFacets) {
		JsonObject objWithUiComponents = find(template, this.uiComponentsKey);
		if (objWithUiComponents != null) {
			JsonElement elem = objWithUiComponents.get(uiComponentsKey);
			if ((elem != null) && elem.isJsonArray()) {
				JsonArray uiComponents = elem.getAsJsonArray();
				JsonPrimitive facetsPlaceHolder = new JsonPrimitive(facetsPlaceHolderStr);
				
				// if uiComponents contains facetsPlaceHolder, then construct a new array which is the same as uiComponents, except
				// with facetPlaceHolder replace with the elements of uiComponentsForFacets
				if (uiComponents.contains(facetsPlaceHolder)) {
					JsonArray newUiComponents = new JsonArray();

					int i = 0; 
					int size = uiComponents.size();
					JsonElement item = uiComponents.get(i);
					while ((i < size) && !item.equals(facetsPlaceHolder)) {
						newUiComponents.add(item);
						i++;
						item = uiComponents.get(i);
					}
					newUiComponents.addAll(uiComponentsForFacets);
					i++;
					while (i < size) {
						item = uiComponents.get(i);
						newUiComponents.add(item);
						i++;
					}
				
					objWithUiComponents.remove(uiComponentsKey);
					objWithUiComponents.add(uiComponentsKey, newUiComponents);
				}
			}
		}
	}
	
	// find the JSON object in the tree rooted at obj, that contains the given key
	private JsonObject find(JsonObject obj, String key) {
		if (obj.has(key)) {
			return obj;
		} else {
			Set<Map.Entry<String,JsonElement>> objMembers = obj.entrySet();
			for (Map.Entry<String, JsonElement> member: objMembers) {
				JsonElement memberVal = member.getValue();
				if (memberVal.isJsonObject()) {
					JsonObject subObj = find(memberVal.getAsJsonObject(), key);
					if (subObj != null) {
						return subObj;
					}
				}
			}
			return null;
		}
	}
	
	// find the given key in the tree rooted at this.template, and replace its value with the given value; this method modifies this.template in-place
	public void setValueOfKey(String key, String value) {
		setValueOfKey(template, key, new JsonPrimitive(value));
	}
	
	private static void setValueOfKey(JsonObject obj, String key, JsonElement value) {
		if (obj.has(key)) {
			obj.remove(key);
			obj.add(key, value);
		} else {
			Set<Map.Entry<String,JsonElement>> objMembers = obj.entrySet();
			for (Map.Entry<String, JsonElement> member: objMembers) {
				JsonElement memberVal = member.getValue();
				if (memberVal.isJsonObject()) {
					setValueOfKey(memberVal.getAsJsonObject(), key, value);
				}
			}
		}
	}

	private String templateFormatErrorMsg() {
		return ("Unexpected options template in " + this.optionsTemplateFilename);
	}
	
	public static String optionsTemplateFormatInfoMsg(String uiComponentsKey, String facetsPlaceHolderStr) {
		String str1 = "The options template file should contain a JSON object obj. obj or a contained JSON object should associate an array with key \"" + uiComponentsKey + "\".\n";
		String str2 = "The array may contain a string \"" + facetsPlaceHolderStr + "\".\n";
		String str3 = "This string indicates where the JSON objects for the specified facets are to be added.\n";
		String str4 = "An example of an acceptable JSON object: \n";
		String str5 = "{..., \"" + uiComponentsKey + "\": [..., \"" + facetsPlaceHolderStr + "\", ...], ...}";
		return (str1 + str2 + str3 + str4 + str5);
	}
}
