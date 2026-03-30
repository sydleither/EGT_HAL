package SpatialEGT;

import java.util.ArrayList;
import java.util.List;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.Gui.GridWindow;
import HAL.Rand;
import HAL.Util;

public class Model2D extends AgentGrid2D<Cell2D> {
    Rand rng;
    int numTypes;
    int[] reproHood;
    int[] interactHood;
    double[][] payoffMatrix;
    double[] deathRates;

    public Model2D(Rand rng, int numTypes, int interactionRadius, int reproductionRadius, int gridLength, int gridHeight, double[][] payoffMatrix, double[] deathRates) {
        super(gridLength, gridHeight, Cell2D.class);
        this.rng = rng;
        this.numTypes = numTypes;
        this.interactHood = Util.CircleHood(false, interactionRadius);
        this.reproHood = Util.CircleHood(false, reproductionRadius);
        this.payoffMatrix = payoffMatrix;
        this.deathRates = deathRates;
    }

    public void InitTumorRandom(int[] initialCounts) {
        // list of random positions on grid
        int gridSize = xDim * yDim;
        int[] startingPositions = new int[gridSize];
        for (int i = 0; i < gridSize; i++) {
            startingPositions[i] = i;
        }
        rng.Shuffle(startingPositions);

        // create and place cells on random positions in grid
        int total = 0;
        for (int i = 0; i < this.numTypes; i++) {
            for (int j = 0; j < initialCounts[i]; j++) {
                NewAgentSQ(startingPositions[total]).Init(i);
                total += 1;
            }
        }
    }

    public void ModelStep() {
        ShuffleAgents(rng);
        for (Cell2D cell : this) {
            cell.CellStep();
        }
    }

    public List<List<Integer>> GetCoords() {
        List<Integer> cellTypes = new ArrayList<Integer>();
        List<Integer> xCoords = new ArrayList<Integer>();
        List<Integer> yCoords = new ArrayList<Integer>();
        for (Cell2D cell: this) {
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
}
