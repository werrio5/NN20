/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kohonen;

/**
 *
 * @author werrio5
 */
public class Main {
    
    private static double[] dataExample;
    
    private static SOMMap initKohonenMap(){
        //ширина (шт. элементов)
        int width = 100;
        
        //высота (шт. элементов)
        int height = 100;
        
        //длина входного вектора
        int vectorLength = dataExample.length;
        
        //начальная скорость обучения
        double startLearningRate = 0.1;
        
        //количество итераций
        int iterationLimit = 1000;
                
        //Kohonen map
        SOMMap map = new SOMMap(width, height, vectorLength, startLearningRate, iterationLimit);
        
        return map;
    }
    
    public static void main(String[] args) {
        //создать карту
        SOMMap map = initKohonenMap();
    }
}
