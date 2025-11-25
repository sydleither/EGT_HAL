package SpatialLV;

import java.nio.file.Paths;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SpatialLV {
    public static void main(String[] args) {
        // read in path to config
        String configFile = null;

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
        String growthModel = (String) params.get("growthModel");
        int numTypes = (int) params.get("numTypes");
        int interactionRadius = (int) params.get("interactionRadius");
        int reproductionRadius = (int) params.get("reproductionRadius");
        int gridLength = (int) params.get("gridLength");
        int gridHeight = (int) params.get("gridHeight");
        int writeFrequency = (int) params.get("writeFrequency");
        int numTicks = (int) params.get("numTicks");

        double[][] interactionMatrix = new double[numTypes][numTypes];
        double[] intrinsicGrowths = new double[numTypes];
        double[] initialCounts = new double[numTypes];
        double[] deathRates = new double[numTypes];
        for (int i = 0; i < numTypes; i++) {
            for (int j = 0; j < numTypes; j++) {
                interactionMatrix[i][j] = (float) params.get("A_"+i+j);
            }
            intrinsicGrowths[i] = (float) params.get("r_"+i);
            deathRates[i] = (float) params.get("d_"+i);
            initialCounts[i] = (float) params.get("x_"+i);
        }

        // initialize model
        if (dimension == 2) {
            Model2D model = new Model2D(seed, growthModel, numTypes, interactionRadius, reproductionRadius, gridLength, gridHeight, interactionMatrix, intrinsicGrowths, initialCounts, deathRates);
        }
        else {
            throw new java.lang.RuntimeException(dimension+"D not supported.");
        }

        // initialize output
        modelOut = new FileIO(saveLoc+"coords.csv", "w");
        modelOut.Write("time,type,x,y\n");

        // run model
        model.InitTumorRandom(numCells, proportionResistant);
        for (int tick = 0; tick <= numTicks; tick++) {
            if ((tick % writeFrequency == 0)) {
                List<List<Integer>> coordLists = model.GetCoords(model);
                List<Integer> cellTypes = coordLists.get(0);
                List<Integer> xCoords = coordLists.get(1);
                List<Integer> yCoords = coordLists.get(2);
                for (int i = 0; i < cellTypes.size(); i++) {
                    modelOut.Write(tick+","+cellTypes.get(i)+","+xCoords.get(i)+","+yCoords.get(i)+"\n");
                }
            }
            model.ModelStep();
        }

        // close output file
        modelOut.Close();
    }
}
