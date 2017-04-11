package edu.indiana.d2i.htrc.bookworm.facetbuilder;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

public class ArgumentParser {
    private static Parameter[] getApplicationParameters() {
        Parameter db = new FlaggedOption("database")
        .setStringParser(JSAP.STRING_PARSER)
        .setShortFlag('d')
        .setRequired(false)
        .setHelp("Specifies the database from which facet metadata is to be retrieved.");

        Parameter username = new FlaggedOption("username")
        .setStringParser(JSAP.STRING_PARSER)
        .setShortFlag('u')
        .setRequired(false)
        .setHelp("Specifies the user account with which the database is accessed.");

        Parameter psswd = new FlaggedOption("password")
        .setStringParser(JSAP.STRING_PARSER)
        .setShortFlag('p')
        .setRequired(false)
        .setHelp("Specifies the password of the user account with which the database is accessed.");
        
        Parameter facetSpecsFile = new FlaggedOption("facetSpecsFile")
        .setStringParser(FileStringParser.getParser()
                        .setMustBeFile(true))
        .setShortFlag('f')
        .setRequired(false)
        .setHelp("Specifies the full path of the facet specifications file.");
        
        Parameter optionsTemplateFile = new FlaggedOption("optionsTemplateFile")
        .setStringParser(FileStringParser.getParser()
                        .setMustBeFile(true))
        .setShortFlag('t')
        .setRequired(false)
        .setHelp("Specifies the full path of the options template file.");

        Parameter outputFile = new FlaggedOption("outputFile")
        .setStringParser(FileStringParser.getParser()
                        .setMustBeFile(true))
        .setShortFlag('o')
        .setRequired(false)
        .setHelp("Specifies the full path of the output file.");

        return new Parameter[] {db, username, psswd, facetSpecsFile, optionsTemplateFile, outputFile};
    }

    private static String getApplicationHelp() {
        return "Application to obtain facet metadata for BookwormGUI from the Bookworm MySQL database.";
    }

    public static JSAPResult parseArguments(String[] args) {
    	try {
    		SimpleJSAP jsap = new SimpleJSAP("Main", getApplicationHelp(), getApplicationParameters());
        	JSAPResult result = jsap.parse(args);
        	if (jsap.messagePrinted()) {
        		return null;
        	}
        	else return result;
        } catch (JSAPException e) {
        	System.out.println("Exception in parsing cmd line args: " + e);
        	return null;
        }
    }

}
