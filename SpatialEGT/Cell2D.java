package SpatialEGT;

import java.lang.Math;

import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Util;

public class Cell2D extends AgentSQ2Dunstackable<Model2D> {
    int type;
    double intrinsicGrowth;
    double growthFactor;

    public void Init(int type) {
        this.type = type;
        this.intrinsicGrowth = G.intrinsicGrowths[type];
        this.growthFactor = -1;
    }

    public double GetGrowthFactor() {
        // initialize neighbor counts
        int[] neighborCounts = new int[G.numTypes];
        for (int i = 0; i < G.numTypes; i++) {
            neighborCounts[i] = 0;
        }

        // fill in neighbor counts
        int neighbors = MapOccupiedHood(G.interactHood);
        for (int i = 0; i < neighbors; i++) {
            Cell2D neighborCell = G.GetAgent(G.interactHood[i]);
            neighborCounts[neighborCell.type] += 1;
        }

        // calculate growth factor
        double f = this.intrinsicGrowth;
        int maxNeighbors = G.interactHood.length;
        for (int i = 0; i < G.numTypes; i++) {
            f += G.interactionMatrix[this.type][i] * (neighborCounts[i] / (double) maxNeighbors);
        }

        return f;
    }

    public void CellStep() {
        double growthFactor = GetGrowthFactor();
        this.growthFactor = growthFactor;

        // death
        if (growthFactor < 0) {
            if (G.rng.Double() < Math.abs(growthFactor)) {
                Dispose();
                return;
            }
        }

        // reproduction
        else {
            if (G.rng.Double() < growthFactor) {
                int options = MapEmptyHood(G.reproHood);
                if (options > 0) {
                    G.NewAgentSQ(G.reproHood[G.rng.Int(options)]).Init(this.type);
                }
            }
        }
    }
}