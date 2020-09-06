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
       
    private static SOMMap initKohonenMap(){
        //ширина (шт. элементов)
        int width = 40;
        
        //высота (шт. элементов)
        int height = 40;
        
        //длина входного вектора
        int vectorLength = 3;
        
        //начальная скорость обучения
        double startLearningRate = 0.1;
        
        //количество итераций
        int iterationLimit = 10000;
                
        //Kohonen map
        SOMMap map = new SOMMap(width, height, vectorLength, startLearningRate, iterationLimit);
        
        //train set
        map.addTrainVector(new Integer[] {255,0,0});//r
        map.addTrainVector(new Integer[] {0,128,0});//g
        map.addTrainVector(new Integer[] {0,0,255});//b
        map.addTrainVector(new Integer[] {0,100,0});//dg
        map.addTrainVector(new Integer[] {0,0,139});//db
        map.addTrainVector(new Integer[] {255,255,0});//y
        map.addTrainVector(new Integer[] {255,165,0});//o
        map.addTrainVector(new Integer[] {128,0,128});//p
        
        map.addTrainVector(new Integer[] {255,255,255});//p
        map.addTrainVector(new Integer[] {0,0,0});//p

        return map;
    }
    
    public static void main(String[] args) {
        //создать карту
        SOMMap map = initKohonenMap();
        
        //train
        map.train();
        
        //масштаб выходного изображения
        int scale = 8;
        
        //frame
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DrawFrame(map.weightsToImage(scale)).setVisible(true);
            }
        });         
    }
}
