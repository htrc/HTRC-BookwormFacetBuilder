# HTRC-BookwormFacetBuilder

A tool to obtain data from the HTRC Bookworm MySQL database for different
facets such as language and genre, and create a JSON file used by BookwormGUI
to display the facets. 

## Build

Create an executable jar containing all dependencies, i.e., a fat jar, using
the following command:
```
mvn clean package
```

## Run
```
java -jar HTRC-BookwormFacetBuilder.jar [-d database]
     [-u username]
     [-p password]
     [-f facetSpecsFilepath]
     [-t optionsTemplateFilepath]
     [-o outputFilepath]
```

### Options

```
-d	The name of the Bookworm MySQL database from which facet information is obtained.

-u 	The user name to access the Bookworm MySQL database.

-p	The password to access the Bookworm MySQL database.

-f 	File containing details regarding which facets to include in the output, the names of tables and 
        columns containing facet information, the sort order, and the number of facet values. See 
        [src/main/resources/facetSpecs.txt](src/main/resources/facetSpecs.txt).

-t	Template file using which the output file is created. This file contains a place holder indicating 
        where facet information needs to be placed. See [src/main/resources/optionsTemplate.json](./src/main/resources/optionsTemplate.json). 

-o 	Output file. The default output file is options.json.
```

## Output

A JSON file, options.json by default, containing facet information which can be used by BookwormGUI.
