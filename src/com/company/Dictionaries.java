package com.company;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public enum Dictionaries {
    KA_RECIPIENT("ka_recipient"),
    RECIPIENT("recipient"),
    ORIGINATOR("originator"),
    KA_AREA("ka_area"),
    KA_SUBAREA("ka_subarea"),
    AR_CONFIDENTIALITY("ar_confidentiality"),
    AR_LANGUAGE("ar_language"),
    AR_STATUS("ar_status"),
    DISCIPLINE("discipline"),
    DOC_TYPE("doc_type"),
    KA_DISCIPLINE("ka_discipline"),
    KA_PHASE("ka_phase"),
    AR_DEPARTMENT("ar_department"),
    AR_SERVICE("ar_service"),
    KA_SAP_CODE("ka_sap_code"),
    KA_FOLDER("ka_folder"),
    KA_UNIT("ka_unit"),
    KA_BUILDING("ka_building"),
    UNUT("unit"),
    CONTRACT("contract"),
    KA_PROJECT_EXTENSION("ka_project_extension");

    private final String name;
    private final HashMap<String, String>values_map;

    private static HashMap<String, String> getValuesFromFile(String fileName){
        HashMap<String, String> values_map = new HashMap<>();

        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            FileReader fr = new FileReader("src/com/company/dictionaries/"+fileName);
            CSVReader reader = new CSVReaderBuilder(fr).withCSVParser(parser).build();
            String[] nextLine;
            //System.out.println("Loading dictionary ["+fileName+"]");
            while ((nextLine = reader.readNext()) != null) {
                values_map.put(nextLine[0], nextLine[1]);
                //System.out.println("<value>"+nextLine[1]+"</value>");
            }
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
        return values_map;
    }

    static Dictionaries getDictionary(String name){
        Dictionaries dictionaries = null;
        for (Dictionaries  dict : Dictionaries.values()) {
            if (dict.getName().equals(name)) {
                dictionaries = dict;
                break;
            }
        }
        if (dictionaries == null){ System.out.println("Error, dictianary ["+name+"] not foud");};
        return dictionaries;
    }

    static String getValue(String dictName, String key){
        String value = null;
        Dictionaries dictionaries = getDictionary(dictName);
        if (dictionaries != null){
            value = (String) dictionaries.getValues_map().get(key);
        }
        if (value != null){
            return value;
        }
        else {
            System.out.println("Key ["+key+"] not found in the dictionary ["+dictName+"]");
            return key;
        }
    }

    static List<Object> getValues(String dictName, List<Object> keys){
        List<Object> return_values = new ArrayList<Object>(1);
        for (Object item : keys) {
            return_values.add( getValue(dictName,(String) item));
        }
        return return_values;
    }

    static List<String> getList(){
        List<String> list = new ArrayList<String>(1);
        for (Dictionaries d : Dictionaries.values()) {
            list.add(d.getName());
        }
        return list;
    }

    Dictionaries(String name){
        this.name = name;
        this.values_map = getValuesFromFile(name+".csv");
    }
    public String getName() { return name; }
    public Map getValues_map() { return values_map; }
}
