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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author werrio5
 */
public class DataLoader {

    private static Map<Integer[],String> trainData;
    private static Map<Integer[],String> testData;

    public static Map<Integer[],String> getTrainData(){

        //если не загруженны - загрузить
        if(trainData == null){
            loadData();
        }

        return trainData;
    }

    public static Map<Integer[],String> getTestData(){
        return testData;
    }
    
    public static Set<String> getTestNames(){
        Set<String> names = new HashSet<>(trainData.values());
        return names;
    }

    /**
     * данные масштабируются от 0..63 до 0..255
     */
    private static void loadData() {
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
        
        //data
        trainData = new HashMap<>();
        testData = new HashMap<>();

        
        //перебор папок
        for (File directory : directories) {
            //файлы
            File[] files = directory.listFiles();
            if(files == null) continue;

            //перебор файлов
            //все файлы, кроме последнего в train
            //последний в test
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
                            // 4 -> scale 63 to 255
                            for (int i = 0; i < digits.length; i++) {
                                digits[i] = 4 * Integer.valueOf(digitsStrings[i]);
                            }
                            
                            //запись
                            if(file.equals(files[files.length - 1]))
                                testData.put(digits,directory.getName());
                            else 
                                trainData.put(digits,directory.getName());
                            
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
    }
}
