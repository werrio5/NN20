/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.kohonen;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author werrio5
 */
public class SOMMap {

    private final List<CSOMNode> nodes;
    private final Set<Double[]> trainingSet;
    private final Map<Double[], String> labels;

    //начальные параметры    
    private final double startLearningRate;
    private final double initRadius;
    private final double timeConstant;
    private final int iterationLimit;
    private final int width;
    private final int height;

    //итерационные параметры
    private int iteration;
    private double learningRate;
    private double curRadius;

    //прочее
    //drawFrame
    Frame drawFrame;
    //drawScale
    int drawScale;

    public SOMMap(int width, int height, int vectorLength, double startLearningRate, int iterationLimit) {
        //начальные параметры       
        //ширина и высота
        this.width = width;
        this.height = height;

        //начальная скорость обучения
        this.startLearningRate = startLearningRate;

        //текущий № итерации
        iteration = 0;

        //всего итераций
        this.iterationLimit = iterationLimit;

        //половина наибольшего значения (ширины либо высоты)
        initRadius = width > height ? width / 2 : height / 2;

        //постоянная времени
        timeConstant = this.iterationLimit / Math.log(initRadius);

        //nodes init
        nodes = new ArrayList<>();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Point position = new Point(x, y);
                CSOMNode node = new CSOMNode(vectorLength, position);
                nodes.add(node);
            }
        }

        //training set
        trainingSet = new HashSet<>();

        //метки (подписи на карте)
        labels = new HashMap<>();
    }

    /**
     * поиск Node с наибольшим значением
     *
     * @return
     */
    private CSOMNode getBMU(Double[] inputVector) {
        CSOMNode BMU = nodes.get(0);
        for (CSOMNode node : nodes) {
            double distance = node.getDistance(inputVector);
            if (distance < BMU.getDistance(inputVector)) {
                BMU = node;
            }
        }
        return BMU;
    }

    /**
     * вычислить область изменения весов
     */
    private void calcBMURadius() {
        curRadius = initRadius * Math.exp(-(double) iteration / timeConstant);
    }

    /**
     * вычислить текущий коэффициент скорости обучения
     */
    private void calcLearningRate() {
        learningRate = startLearningRate * Math.exp(-(double) iteration / iterationLimit);
    }

    /**
     * поиск попадающих в область элементов и изменение их весовых коэффициентов
     *
     * @param BMUPos
     */
    private void adjustWeights(Point BMUPos, Double[] inputVector) {
        for (CSOMNode node : nodes) {
            //положение текущего
            Point curPos = node.getPosition();

            //расстояние до BMU
            double distToBMU = Math.sqrt(Math.pow(curPos.x - BMUPos.x, 2) + Math.pow(curPos.y - BMUPos.y, 2));

            //расстояние не больше радиуса -> меняем весовые коэффициенты
            if (distToBMU <= curRadius) {
                //изменение весовых коэффициентов 
                node.adjustWeight(distToBMU, curRadius, learningRate, inputVector);
            }
        }
    }

    private void calcIterationParameters() {
        calcLearningRate();
        calcBMURadius();
    }

    private void iterate(Double[] inputVector) {
        //параметры для итерации
        calcIterationParameters();

        //best matching unit
        CSOMNode BMU = getBMU(inputVector);

        //расположение BMU в пространстве
        Point BMUPosition = BMU.getPosition();

        //изменить веса
        adjustWeights(BMUPosition, inputVector);

        //номер следующей итерации
        iteration++;
    }

    public void addTrainVector(Integer[] vector, String title) {
        Double[] dVector = new Double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            dVector[i] = vector[i].doubleValue() / 255d;
        }
        trainingSet.add(dVector);
        labels.put(dVector, title);
    }

    private Double[] getRandomTrainingVector(Map<Integer[], String> data) {
        //random index
        int index = (int) Math.round(Math.random() * (data.size() - 1));

        //random vector
        Integer[] intRandomVector = (Integer[]) data.keySet().toArray()[index];
        Double[] randomVector = {intRandomVector[0] / 63d, intRandomVector[1] / 63d, intRandomVector[2] / 63d};
        return randomVector;
    }

    private Double[] getRandomTrainingVector() {
        //random index
        int index = (int) Math.round(Math.random() * (trainingSet.size() - 1));

        //random vector
        Double[] randomVector = (Double[]) trainingSet.toArray()[index];
        return randomVector;
    }

    public void train(Map<Integer[], String> data) {
        for (int i = 0; i < iterationLimit; i++) {
            //случайный вектор из обучающего набора
            Double[] inputVector = getRandomTrainingVector(data);

            //отрисовка некоторых изображений            
            if (iteration < 100 & iteration % 10 == 0
                    | //до 100 шаг 10
                    iteration < 1000 & iteration % 100 == 0
                    | //до 1000 шаг 100 , далее шаг 500 
                    iteration % 500 == 0) {

                System.out.println("iteration = " + iteration + " / " + iterationLimit);
                drawMap();
                drawLabels(data);
            }
            //следующая итерация
            iterate(inputVector);
        }

        //финальное изображение
        drawMap();
        drawLabels(data);
    }

    /**
     * обучение
     */
    public void train() {

        for (int i = 0; i < iterationLimit; i++) {
            //случайный вектор из обучающего набора
            Double[] inputVector = getRandomTrainingVector();

            //отрисовка некоторых изображений            
            if (iteration < 100 & iteration % 10 == 0
                    | //до 100 шаг 10
                    iteration < 1000 & iteration % 100 == 0
                    | //до 1000 шаг 100, далее шаг 500 
                    iteration % 500 == 0) {

                drawMap();
                //метки
                drawLabels();
            }
            //следующая итерация
            iterate(inputVector);
        }

        //финальное изображение
        drawMap();
        //метки
        drawLabels();
    }

    public List<CSOMNode> getNodes() {
        return nodes;
    }

    public Image weightsToImage(int scale) {
        //image
        BufferedImage im = new BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_RGB);
        for (CSOMNode node : nodes) {
            //pos
            Point pos = node.getPosition();

            //weights
            Double[] weights = node.getWeights();

            //weights to color       
            Color color = new Color(weights[0].floatValue(), weights[1].floatValue(), weights[2].floatValue());

            //draw pixels
            for (int x = 0; x < scale; x++) {
                for (int y = 0; y < scale; y++) {
                    im.setRGB(pos.x * scale + x, pos.y * scale + y, color.getRGB());
                }
            }
        }
        return im;
    }

    public void setDrawFrame(Frame drawFrame) {
        this.drawFrame = drawFrame;
    }

    public void setDrawScale(int drawScale) {
        this.drawScale = drawScale;
    }

    /**
     * отрисовка узлов
     */
    public void drawMap() {
        Image im = weightsToImage(drawScale);
        Graphics g = drawFrame.getGraphics();
        g.clearRect(0, 0, drawFrame.getWidth() - 250, drawFrame.getHeight());
        g.drawImage(im, 20, 50, drawFrame);
    }

    private void drawLabels(Map<Integer[], String> data) {
        Set<String> labels = new HashSet<String>(data.values());
        for (String value : labels) {
            //первый вектор с этим значением
            Double[] vector = new Double[3];
            for (Integer[] key : data.keySet()) {
                if (data.get(key) == value) {
                    vector[0] = key[0] / 63d;
                    vector[1] = key[1] / 63d;
                    vector[2] = key[2] / 63d;
                    break;
                }
            }
            //участок кластера
            CSOMNode BMU = getBMU(vector);
            Point pos = BMU.getPosition();

            //метка
            drawTitle(value, pos);
        }

    }

    private void drawLabels() {
        if (trainingSet != null) {
            for (Double[] trainVector : trainingSet) {
                //центр кластера
                CSOMNode BMU = getBMU(trainVector);
                Point pos = BMU.getPosition();

                //метка
                String title = labels.get(trainVector);
                drawTitle(title, pos);
            }
        }
    }

    private void drawTitle(String title, Point pos) {
        Graphics g = drawFrame.getGraphics();
        g.setColor(Color.BLACK);
        g.drawString(title, pos.x * drawScale + 20, pos.y * drawScale + 50);
    }

    private void drawPoint(Point pos) {
        Graphics g = drawFrame.getGraphics();
        g.setColor(Color.BLACK);
        g.drawRect(pos.x * drawScale + 20, pos.y * drawScale + 50, drawScale, drawScale);
        System.out.println(pos);
    }

    /**
     *
     * @param inputVector
     */
    public void findVector(Double[] inputVector) {
        //местонахождение
        CSOMNode BMU = getBMU(inputVector);
        Point pos = BMU.getPosition();
        //Color c = new Color( 255 - (int)(BMU.getWeights()[0]*255),255 -  (int)(BMU.getWeights()[1] * 255), 255 - (int)(BMU.getWeights()[2] * 255));

        //отрисовка карты
        drawMap();

        //отрисовка входного вектора
        drawPoint(pos);
    }
}
