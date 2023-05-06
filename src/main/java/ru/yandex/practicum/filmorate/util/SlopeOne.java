package ru.yandex.practicum.filmorate.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@Component
public class SlopeOne {

    private Map<Integer, Map<Integer, Double>> diff = new HashMap<>();
    private Map<Integer, Map<Integer, Integer>> freq = new HashMap<>();
    private final List<Integer> filmIds = new ArrayList<>();

    public void buildDifferencesMatrix(Map<Integer, Map<Integer, Double>> data) {
        for (Map<Integer, Double> user : data.values()) {
            for (Entry<Integer, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<Integer, Double>());
                    freq.put(e.getKey(), new HashMap<Integer, Integer>());
                }
                for (Entry<Integer, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey());
                    }
                    double oldDiff = 0.0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey());
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Integer j : diff.keySet()) {
            for (Integer i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i);
                int count = freq.get(j).get(i);
                diff.get(j).put(i, oldValue / count);
            }
        }
    }

    public Map<Integer, Map<Integer, Integer>> predict(Map<Integer, Map<Integer, Double>> data) {
        Map<Integer, Map<Integer, Integer>> outputData = new HashMap<>();
        HashMap<Integer, Double> uPred = new HashMap<Integer, Double>();
        HashMap<Integer, Integer> uFreq = new HashMap<Integer, Integer>();
        for (Integer j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }
        for (Entry<Integer, Map<Integer, Double>> e : data.entrySet()) {
            for (Integer j : e.getValue().keySet()) {
                for (Integer k : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(k).get(j) + e.getValue().get(j);
                        double finalValue = predictedValue * freq.get(k).get(j);
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + freq.get(k).get(j));
                    } catch (NullPointerException ignored) {
                    }
                }
            }
            HashMap<Integer, Integer> clean = new HashMap<Integer, Integer>();
            for (Integer j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, (int) Math.round(uPred.get(j)) / uFreq.get(j));
                }
            }
            for (Integer j : filmIds) {
                if (e.getValue().containsKey(j)) {
                    clean.put(j, (int) Math.round(e.getValue().get(j)));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1);
                }
            }
            outputData.put(e.getKey(), clean);
        }
        return outputData;
    }
}