package com.company;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SafeRelations {

    public HashMap<String, ArrayList<String[]>> data;

    public SafeRelations(){
        this.data = new HashMap<String, ArrayList<String[]>>();
        try {
            CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
            FileReader fr = new FileReader("src/com/company/dictionaries/safe_relation.csv");
            CSVReader reader = new CSVReaderBuilder(fr).withCSVParser(parser).build();
            String[] nextLine;
            String curParentId = "";
            ArrayList<String[]> curList = new ArrayList<>();

            nextLine = reader.readNext();
            curParentId = nextLine[2];
            curList.add(nextLine);

            while ((nextLine = reader.readNext()) != null) {
                if (!curParentId.equals(nextLine[1])){
                    this.data.put(curParentId, curList);
                    curParentId = nextLine[1];
                    curList = new ArrayList<>();
                }
                curList.add(nextLine);
            }
            this.data.put(curParentId, curList);

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }

    }

}
