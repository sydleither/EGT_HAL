"""Convenient functions for running experiments with EGT_HAL"""

import json
import os

from scipy.stats import qmc


def write_run_scripts(data_dir, experiment_name, run_output):
    """Writes batched bash files for running multiple instances of EGT_HAL

    :param data_dir: the directory the configs are stored in
    :type data_dir: str
    :param experiment_name: the name of the experiment
    :type experiment_name: str
    :param run_output: a list containing all the lines specifying the EGT_HAL runs
    :type run_output: list[str]
    """
    run_output_batches = [run_output[i : i + 999] for i in range(0, len(run_output), 999)]
    for i, batch in enumerate(run_output_batches):
        with open(f"{data_dir}/{experiment_name}/run{i}.sh", "w", encoding="UTF-8") as f:
            for output_line in batch:
                f.write(output_line)
    print("Make sure you recompile SpatialEGT before running experiments:")
    print('javac -d "build" -cp "lib/*" @sources.txt')


def write_config(
    data_dir,
    exp_dir,
    config_name,
    seed,
    payoff,
    num_cells,
    proportion_r,
    null=1,
    adaptive=0,
    continuous=0,
    write_freq=250,
    x=125,
    y=125,
    ticks=250,
    radius=2,
    turnover=0.009,
    drug_reduction=0.5,
    at_threshold=0.5,
    init_tumor=0,
    toy_gap=5,
):
    """Write a config which parameterizes an EGT_HAL run

    :param data_dir: the overall name of the directory storing the data
    :type data_dir: str
    :param exp_dir: the specific experiment name/directory
    :type exp_dir: str
    :param config_name: the name of the config file
    :type config_name: str
    :param seed: the seed for the EGT_HAL run
    :type seed: int or str
    :param payoff: the payoff matrix in format [a,b,c,d]
    :type payoff: list[float]
    :param num_cells: starting number of cells
    :type num_cells: int
    :param proportion_r: how many of the starting cells are resistant
    :type proportion_r: float
    :param null: whether to run the null model, defaults to 1
    :type null: int, optional
    :param adaptive: whether to run the adaptive model, defaults to 0
    :type adaptive: int, optional
    :param continuous: whether to run the continuous drug model, defaults to 0
    :type continuous: int, optional
    :param write_freq: how often to save the model state to csv, defaults to 250
    :type write_freq: int, optional
    :param ticks: how many time steps to run the model for, defaults to 250
    :type ticks: int, optional
    :param radius: the neighborhood size, defaults to 2
    :type radius: int, optional
    :param turnover: the probability each cell dies each time step, defaults to 0.009
    :type turnover: float, optional
    :param drug_reduction: how much reproduction rate is reduced by drug, defaults to 0.5
    :type drug_reduction: float, optional
    :param at_threshold: adaptive therapy drug on threshold, defaults to 0.5
    :type at_threshold: float, optional
    :param init_tumor: initial tumor type (see SpatialEGT/SpatialEGT2D.java), defaults to 0
    :type init_tumor: int, optional
    :param toy_gap: how much space between tumor borders, defaults to 5
    :type toy_gap: int, optional
    """
    config = {
        "null": null,
        "adaptive": adaptive,
        "continuous": continuous,
        "writeModelFrequency": write_freq,
        "x": x,
        "y": y,
        "neighborhoodRadius": radius,
        "numTicks": ticks,
        "deathRate": turnover,
        "drugGrowthReduction": drug_reduction,
        "numCells": num_cells,
        "proportionResistant": proportion_r,
        "adaptiveTreatmentThreshold": at_threshold,
        "initialTumor": init_tumor,
        "toyGap": toy_gap,
        "A": payoff[0],
        "B": payoff[1],
        "C": payoff[2],
        "D": payoff[3],
    }

    path = f"{data_dir}/{exp_dir}/{config_name}"
    if not os.path.exists(f"{path}/{seed}"):
        os.makedirs(f"{path}/{seed}")
    with open(f"{path}/{config_name}.json", "w", encoding="UTF-8") as f:
        json.dump(config, f, indent=4)


def latin_hybercube_sample(num_samples, param_names, lower_bounds, upper_bounds, ints, seed):
    """Latin Hypercube Sample

    :param num_samples: how many samples to take from parameter space
    :type num_samples: int
    :param param_names: names of parameters
    :type param_names: list[str]
    :param lower_bounds: lower bound of each paremeter
    :type lower_bounds: list[float]
    :param upper_bounds: upper bound of each parameter
    :type upper_bounds: list[float]
    :param ints: whether each parameter should be returned as an int
    :type ints: list[bool]
    :param seed: random seed
    :type seed: int
    :return: the sampled parameters, named
    :rtype: list[dict]
    """
    sampler = qmc.LatinHypercube(d=len(lower_bounds), seed=seed)
    unscaled_sample = sampler.random(n=num_samples)
    sample = qmc.scale(unscaled_sample, lower_bounds, upper_bounds).tolist()
    sampled_params = [
        {param_names[i]: round(s[i]) if ints[i] else round(s[i], 2) for i in range(len(s))}
        for s in sample
    ]
    return sampled_params
