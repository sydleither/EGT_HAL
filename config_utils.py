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
            if run_output[0][0:4] == "java":
                abs_path = os.path.dirname(os.path.realpath(__file__)).replace(" ", "\ ")
                f.write(f"cd {abs_path}\n")
            for output_line in batch:
                f.write(output_line)
    print("Make sure you recompile SpatialEGT before running experiments:")
    print('javac -d "build" -cp "lib/*" @sources.txt')


def write_config(
    save_loc,
    seed,
    num_types,
    payoff_matrix,
    initial_counts,
    death_rates,
    grid_length=100,
    grid_height=100,
    interaction_radius=2,
    reproduction_radius=1,
    ticks=200,
    write_freq=20,
    dimension=2
):
    if len(payoff_matrix) != num_types + 1:
        raise ValueError("Payoff matrix does not have a dimension for empty space.")

    config = {
        "dimension": dimension,
        "seed": seed,
        "numTicks": ticks,
        "writeFrequency": write_freq,
        "gridLength": grid_length,
        "gridHeight": grid_height,
        "numTypes": num_types,
        "interactionRadius": interaction_radius,
        "reproductionRadius": reproduction_radius,
    }

    for i in range(num_types + 1):
        for j in range(num_types + 1):
            config[f"P_{i}{j}"] = float(payoff_matrix[i][j])

    for i in range(num_types):
        config[f"d_{i}"] = float(death_rates[i])
        config[f"x_{i}"] = int(initial_counts[i])

    if not os.path.exists(save_loc):
        os.makedirs(save_loc)
    with open(f"{save_loc}/config.json", "w", encoding="UTF-8") as f:
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
