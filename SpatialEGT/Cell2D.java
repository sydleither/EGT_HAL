package SpatialEGT;

import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Util;

public class Cell2D extends AgentSQ2Dunstackable<Model2D> {
    int type;
    double deathRate;

    public void Init(int type) {
        this.type = type;
        this.deathRate = G.deathRates[type];
    }

    public double GetDivRate() {
        double total_payoff = 0;
        int neighbors = MapOccupiedHood(G.interactHood);
        for (int i = 0; i < neighbors; i++) {
            Cell2D neighborCell = G.GetAgent(G.interactHood[i]);
            total_payoff += G.payoffMatrix[this.type][neighborCell.type];
        }
        if (G.payoffMatrix[this.type][G.numTypes] != 0.0) {
            int empty = MapEmptyHood(G.interactHood);
            total_payoff += empty * G.payoffMatrix[this.type][G.numTypes];
            neighbors += empty;
        }
        return total_payoff / neighbors;
    }

    public void CellStep() {
        // divison
        double divRate = this.GetDivRate();
        if (G.rng.Double() < divRate) {
            int options = MapEmptyHood(G.reproHood);
            if (options > 0) {
                G.NewAgentSQ(G.reproHood[G.rng.Int(options)]).Init(this.type);
            }
        }

        // death
        if (G.rng.Double() < this.deathRate) {
            Dispose();
            return;
        }
    }
}