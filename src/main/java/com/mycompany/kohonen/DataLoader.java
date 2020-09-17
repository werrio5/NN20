/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kohonen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author werrio5
 */
public class DataLoader {

    public static Map<Integer[],String> loadData() {
        //путь к данным
        String curDir = System.getProperty("user.dir");
        String datapath = curDir + File.separator + "data";
        File folder = new File(datapath);

        //список папок
        File[] directories = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        
        Map<Integer[],String> data = new HashMap<>();

        //data
        //перебор папок
        for (File directory : directories) {
            //файлы
            File[] files = directory.listFiles();
            if(files == null) continue;

            //перебор файлов
            for (File file : files) {
                //файл
                try {
                    BufferedReader br = new BufferedReader(new FileReader(file));
                    //чтение строк 
                    try {
                        String line = br.readLine();
                        while (line != null) {
                            //разделить строку
                            String[] digitsStrings = line.split(" ");
                            Integer[] digits = new Integer[digitsStrings.length];

                            //str to int
                            for (int i = 0; i < digits.length; i++) {
                                digits[i] = Integer.valueOf(digitsStrings[i]);
                            }
                            
                            //запись
                            data.put(digits,directory.getName());

                            //следующая строка
                            line = br.readLine();
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return data;
    }
}
