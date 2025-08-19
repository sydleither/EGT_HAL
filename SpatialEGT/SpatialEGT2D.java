package SpatialEGT;

import java.util.Arrays;
import java.lang.Math;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.*;

import HAL.Gui.GifMaker;
import HAL.Gui.GridWindow;
import HAL.Tools.FileIO;
import HAL.Rand;
import HAL.Util;

public class SpatialEGT2D {
    public static List<List<Integer>> GetModelCoords(Model2D model) {
        List<Integer> cellTypes = new ArrayList<Integer>();
        List<Integer> xCoords = new ArrayList<Integer>();
        List<Integer> yCoords = new ArrayList<Integer>();
        for (Cell2D cell: model) {
            cellTypes.add(cell.type);
            xCoords.add(cell.Xsq());
            yCoords.add(cell.Ysq());
        }
        List<List<Integer>> returnList = new ArrayList<List<Integer>>();
        returnList.add(cellTypes);
        returnList.add(xCoords);
        returnList.add(yCoords);
        return returnList;
    }

    public static double ProportionSensitive(Model2D model) {
        int sensitive = 0;
        int resistant = 0;
        for (Cell2D cell: model) {
            if (cell.type == 0) {
                sensitive += 1;
            }
            else {
                resistant += 1;
            }
        }
        return sensitive / ((double) (sensitive + resistant));
    }

    public static List<double[][]> extractPayoffMatrices(Map<String, Object> params) {
        Pattern matrixKeyPattern = Pattern.compile("([a-dA-D])(?:([0-9]+))?");
        Map<Integer, Map<String, Double>> groupedMatrix = new TreeMap<>();

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            Matcher matcher = matrixKeyPattern.matcher(entry.getKey());
            if (matcher.matches() && entry.getValue() instanceof Number) {
                String label = matcher.group(1).toLowerCase();
                String suffix = matcher.group(2);
                int index = (suffix != null) ? Integer.parseInt(suffix) : 0;
                groupedMatrix
                    .computeIfAbsent(index, k -> new HashMap<>())
                    .put(label, ((Number) entry.getValue()).doubleValue());
            }
        }

        List<double[][]> payoffMatrices = new ArrayList<>();
        for (Map.Entry<Integer, Map<String, Double>> entry : groupedMatrix.entrySet()) {
            Map<String, Double> values = entry.getValue();
            for (String key : List.of("a", "b", "c", "d")) {
                if (!values.containsKey(key)) {
                    throw new IllegalArgumentException("Missing matrix value for key '" + key + "' in group " + entry.getKey());
                }
            }
            double[][] matrix = new double[2][2];
            matrix[0][0] = values.get("a");
            matrix[0][1] = values.get("b");
            matrix[1][0] = values.get("c");
            matrix[1][1] = values.get("d");
            payoffMatrices.add(matrix);
        }

        return payoffMatrices;
    }

    public SpatialEGT2D(String saveLoc, Map<String, Object> params, long seed, int visualizationFrequency) {
        // turn parameters json into variables
        int writeModelFrequency = (int) params.get("writeModelFrequency");
        int numTicks = (int) params.get("numTicks");
        int x = (int) params.get("x");
        int y = (int) params.get("y");
        int interactionRadius = (int) params.get("interactionRadius");
        int reproductionRadius = (int) params.get("reproductionRadius");
        double deathRate = (double) params.get("deathRate");
        int numCells = (int) params.get("numCells");
        double proportionResistant = (double) params.get("proportionResistant");
        double stopAt = (double) params.get("stopAt");
        List<double[][]> payoffMatrices = extractPayoffMatrices(params);

        // initialize model
        Model2D model = new Model2D(x, y, new Rand(seed), interactionRadius, reproductionRadius, deathRate, payoffMatrices);

        // check what to run and initialize output
        boolean writeModel = writeModelFrequency != 0;
        boolean visualize = visualizationFrequency != 0;

        boolean stopAtProportion = false;
        if (stopAt > 0) {
            boolean stopAtProportion = true;
        }

        GridWindow win = null;
        GifMaker gifWin = null;
        if (visualize) {
            win = new GridWindow("SpatialEGT", x, y, 4);
            gifWin = new GifMaker(saveLoc+"growth.gif", 0, false);
            writeModel = false;
        }
        FileIO modelOut = null;
        if (writeModel) {
            modelOut = new FileIO(saveLoc+"coords.csv", "w");
            modelOut.Write("time,type,x,y\n");
        }

        // run model
        model.InitTumorRandom(numCells, proportionResistant);
        for (int tick = 0; tick <= numTicks; tick++) {
            if (stopAtProportion) {
                double proportionSensitive = ProportionSensitive(model);
                if (proportionSensitive - 0.025 < stopAt && proportionSensitive + 0.025 > stopAt) {
                    List<List<Integer>> coordLists = GetModelCoords(model);
                    List<Integer> cellTypes = coordLists.get(0);
                    List<Integer> xCoords = coordLists.get(1);
                    List<Integer> yCoords = coordLists.get(2);
                    for (int i = 0; i < cellTypes.size(); i++) {
                        modelOut.Write(tick+","+cellTypes.get(i)+","+xCoords.get(i)+","+yCoords.get(i)+"\n");
                    }
                    break;
                }
            }
            if (writeModel) {
                if ((tick % writeModelFrequency == 0)) {
                    List<List<Integer>> coordLists = GetModelCoords(model);
                    List<Integer> cellTypes = coordLists.get(0);
                    List<Integer> xCoords = coordLists.get(1);
                    List<Integer> yCoords = coordLists.get(2);
                    for (int i = 0; i < cellTypes.size(); i++) {
                        modelOut.Write(tick+","+cellTypes.get(i)+","+xCoords.get(i)+","+yCoords.get(i)+"\n");
                    }
                }
            }
            if (visualize) {
                model.DrawModel(win, 0);
                if (tick % visualizationFrequency == 0) {
                    win.ToPNG(saveLoc+tick+".png");
                }
                gifWin.AddFrame(win);
            }
            model.ModelStep();
        }

        // close output files
        if (visualize) {
            win.Close();
            gifWin.Close();
        }
        if (writeModel) {
            modelOut.Close();
        }
    }
}
