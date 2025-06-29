package SpatialEGT;

import java.util.Arrays;
import java.lang.Math;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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

    public static List<Double> GetDrugGradient(Model2D model) {
        List<Double> drug = new ArrayList<Double>();
        for (Cell2D cell: model) {
            drug.add(cell.GetDrugGrowthReduction());
        }
        return drug;
    }

    public SpatialEGT2D(String saveLoc, Map<String, Object> params, long seed, int visualizationFrequency) {
        // turn parameters json into variables
        int runNull = (int) params.get("null");
        int runContinuous = (int) params.get("continuous");
        int runAdaptive = (int) params.get("adaptive");
        int runGradient = (int) params.get("gradient");
        int writeModelFrequency = (int) params.get("writeModelFrequency");
        int numTicks = (int) params.get("numTicks");
        int x = (int) params.get("x");
        int y = (int) params.get("y");
        int interactionRadius = (int) params.get("interactionRadius");
        int reproductionRadius = (int) params.get("reproductionRadius");
        double deathRate = (double) params.get("deathRate");
        double drugGrowthReduction = (double) params.get("drugGrowthReduction");
        int numCells = (int) params.get("numCells");
        double proportionResistant = (double) params.get("proportionResistant");
        double adaptiveTreatmentThreshold = (double) params.get("adaptiveTreatmentThreshold");
        int initialTumor = (int) params.get("initialTumor");
        int toyGap = (int) params.get("toyGap");
        double[][] payoff = new double[2][2];
        payoff[0][0] = (double) params.get("A");
        payoff[0][1] = (double) params.get("B");
        payoff[1][0] = (double) params.get("C");
        payoff[1][1] = (double) params.get("D");

        // initialize with specified models
        HashMap<String,Model2D> models = new HashMap<String,Model2D>();
        if (runNull == 1) {
            Model2D nullModel = new Model2D(x, y, new Rand(seed), interactionRadius, reproductionRadius, deathRate, 0.0, false, 0.0, 0, payoff);
            models.put("nodrug", nullModel);
        }
        if (runGradient == 1) {
            Model2D gradientModel = new Model2D(x, y, new Rand(seed), interactionRadius, reproductionRadius, deathRate, drugGrowthReduction, false, 0.0, 5, payoff);
            models.put("gradient", gradientModel);
        }
        if (runContinuous == 1) {
            Model2D continuousModel = new Model2D(x, y, new Rand(seed), interactionRadius, reproductionRadius, deathRate, drugGrowthReduction, false, 0.0, 0, payoff);
            models.put("continuous", continuousModel);
        }
        if (runAdaptive == 1) {
            Model2D adaptiveModel = new Model2D(x, y, new Rand(seed), interactionRadius, reproductionRadius, deathRate, drugGrowthReduction, true, adaptiveTreatmentThreshold, 0, payoff);
            models.put("adaptive", adaptiveModel);
        }

        // check what to run and initialize output
        boolean writeModel = writeModelFrequency != 0;
        boolean visualize = visualizationFrequency != 0;

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
            if (runGradient == 1) {
                modelOut.Write("model,time,type,x,y,drug\n");
            }
            else {
                modelOut.Write("model,time,type,x,y\n");
            }
        }

        // run models
        for (Map.Entry<String,Model2D> modelEntry : models.entrySet()) {
            String modelName = modelEntry.getKey();
            Model2D model = modelEntry.getValue();
            if (initialTumor == 1)
                model.InitTumorLinear(proportionResistant, toyGap);
            else if (initialTumor == 2)
                model.InitTumorConvex(numCells, proportionResistant);
            else if (initialTumor == 3)
                model.InitTumorConcave(numCells, proportionResistant);
            else if (initialTumor == 4)
                model.InitTumorCircle(proportionResistant, toyGap);
            else
                model.InitTumorRandom(numCells, proportionResistant);

            for (int tick = 0; tick <= numTicks; tick++) {
                if (writeModel) {
                    if ((tick % writeModelFrequency == 0)) {
                        List<List<Integer>> coordLists = GetModelCoords(model);
                        List<Integer> cellTypes = coordLists.get(0);
                        List<Integer> xCoords = coordLists.get(1);
                        List<Integer> yCoords = coordLists.get(2);
                        if (modelName == "gradient") {
                            List<Double> drugs = GetDrugGradient(model);
                            for (int i = 0; i < cellTypes.size(); i++) {
                                modelOut.Write(modelName+","+tick+","+cellTypes.get(i)+","+xCoords.get(i)+","+yCoords.get(i)+","+drugs.get(i)+"\n");
                            }
                        }
                        else {
                            for (int i = 0; i < cellTypes.size(); i++) {
                                modelOut.Write(modelName+","+tick+","+cellTypes.get(i)+","+xCoords.get(i)+","+yCoords.get(i)+"\n");
                            }
                        }
                    }
                }
                if (visualize) {
                    model.DrawModel(win, 0);
                    if (tick % visualizationFrequency == 0) {
                        win.ToPNG(saveLoc+modelName+tick+".png");
                    }
                    gifWin.AddFrame(win);
                }
                model.ModelStep();
            }
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
