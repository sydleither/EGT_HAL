package SpatialLV;

import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Util;

public class Cell2D extends AgentSQ2Dunstackable<Model2D> {
    int type;
    double intrinsicGrowth;
    double deathRate;

    public void Init(int type) {
        this.type = type;
        this.intrinsicGrowth = G.intrinsicGrowths[type];
        this.deathRate = G.deathRates[type];
    }

    public double GetReproRate() {
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
        for (int i = 0; i < numTypes; i++) {
            f += G.interactionMatrix[this.type][i]*neighborCounts[i];
        }

        // convert growth factor into probability of reproduction
        return 1.0 - Math.exp(-Math.max(f, 0));
    }

    public void CellStep() {
        // reproduction
        double divRate = GetReproRate();
        if (G.rng.Double() < divRate) {
            int options = MapEmptyHood(G.divHood);
            if (options > 0) {
                G.NewAgentSQ(G.divHood[G.rng.Int(options)]).Init(this.type);
            }
        }

        // death
        if (G.rng.Double() < this.deathRate) {
            Dispose();
            return;
        }
    }
}