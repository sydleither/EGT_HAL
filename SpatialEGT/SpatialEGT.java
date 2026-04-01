package SpatialEGT;

import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import HAL.Rand;
import HAL.Tools.FileIO;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SpatialEGT {
    public static void main(String[] args) {
        // read in path to config
        String path = args[0];
        String configFile = path+"/config.json";

        // read in json parameters
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> params;
        try{
            params = mapper.readValue(Paths.get(configFile).toFile(), Map.class);
        }
        catch (Exception e) {
            e.printStackTrace(System.out);
            return;
        }

        // turn parameters json into variables
        int seed = (int) params.get("seed");
        int dimension = (int) params.get("dimension");
        int numTypes = (int) params.get("numTypes");
        int interactionRadius = (int) params.get("interactionRadius");
        int reproductionRadius = (int) params.get("reproductionRadius");
        int gridLength = (int) params.get("gridLength");
        int gridHeight = (int) params.get("gridHeight");
        int writeFrequency = (int) params.get("writeFrequency");
        int numTicks = (int) params.get("numTicks");

        double[][] interactionMatrix = new double[numTypes][numTypes];
        int[] initialCounts = new int[numTypes];
        double[] intrinsicGrowths = new double[numTypes];
        for (int i = 0; i < numTypes; i++) {
            for (int j = 0; j < numTypes; j++) {
                interactionMatrix[i][j] = (double) params.get("A_"+i+j);
            }
            intrinsicGrowths[i] = (double) params.get("r_"+i);
            initialCounts[i] = (int) params.get("x_"+i);
        }

        // initialize output file
        FileIO modelOut = new FileIO(path+"/coords.csv", "w");

        // initialize model
        Model2D model;
        if (dimension == 2) {
            modelOut.Write("time,type,x,y,growth_factor\n");
            model = new Model2D(new Rand(seed), numTypes, interactionRadius, reproductionRadius, gridLength, gridHeight, interactionMatrix, intrinsicGrowths);
        }
        else {
            throw new java.lang.RuntimeException(dimension+"D not supported.");
        }

        // run model
        model.InitTumorRandom(initialCounts);
        for (int tick = 0; tick <= numTicks; tick++) {
            if ((tick % writeFrequency == 0)) {
                List<List<Integer>> coordLists = model.GetCoords();
                for (int i = 0; i < coordLists.get(0).size(); i++) {
                    modelOut.Write(tick+",");
                    for (int j = 0; j < coordLists.size(); j++) {
                        modelOut.Write(coordLists.get(j).get(i)+",");
                    }
                    modelOut.Write("\n");
                }
            }
            model.ModelStep();
        }

        // close output file
        modelOut.Close();
    }
}
