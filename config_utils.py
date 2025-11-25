"""Convenient functions for running experiments with EGT_HAL"""

import json
import os


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
    config_path,
    seed,
    dimension,
    growth_model,
    num_types,
    interaction_matrix,
    intrinsic_growths,
    initial_counts,
    death_rates,
    interaction_radius,
    reproduction_radius,
    grid_length,
    grid_height,
    ticks,
    write_freq,
):
    config = {
        "seed": seed,
        "dimension": dimension,
        "growthModel": growth_model,
        "numTypes": num_types,
        "interactionRadius": interaction_radius,
        "reproductionRadius": reproduction_radius,
        "gridLength": grid_length,
        "gridHeight": grid_height,
        "numTicks": ticks,
        "writeFrequency": write_freq,
    }

    for i in range(num_types):
        for j in range(num_types):
            config[f"A_{i}{j}"] = interaction_matrix[i][j]
        config[f"r_{i}"] = intrinsic_growths[i]
        config[f"d_{i}"] = death_rates[i]
        config[f"x_{i}"] = initial_counts[i]

    if not os.path.exists(config_path):
        os.makedirs(config_path)
    with open(f"{config_path}/config.json", "w", encoding="UTF-8") as f:
        json.dump(config, f, indent=4)


def latin_hybercube_sample(num_samples, param_names, lower_bounds, upper_bounds, ints, rnd, seed):
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
    from scipy.stats import qmc

    sampler = qmc.LatinHypercube(d=len(lower_bounds), seed=seed)
    unscaled_sample = sampler.random(n=num_samples)
    sample = qmc.scale(unscaled_sample, lower_bounds, upper_bounds).tolist()
    sampled_params = [
        {param_names[i]: round(s[i]) if ints[i] else round(s[i], rnd) for i in range(len(s))}
        for s in sample
    ]
    return sampled_params
