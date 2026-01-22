"""Generate a sbatch .sb file based on the input arguments"""

import argparse

import numpy as np

from config_utils import write_spatiallv_config


def main():
    """Generate and save sbatch script based on input arguments"""
    parser = argparse.ArgumentParser()
    parser.add_argument("-dir", "--data_dir", type=str, default="data")
    parser.add_argument("-exp", "--exp_name", type=str, default="experiment")
    parser.add_argument("-run", "--run_name", type=str, default="run")
    parser.add_argument("-seed", "--seed", type=int, default=42)
    parser.add_argument("-dim", "--dimension", type=int, default=2)
    parser.add_argument("-model", "--growth_model", type=str, default="linear")
    parser.add_argument("-types", "--num_types", type=int, default=2)
    parser.add_argument(
        "-A", "--interaction_matrix", nargs="+", type=float, default=[0.11, 0.16, 0.19, 0.08]
    )
    parser.add_argument("-r", "--intrinsic_growths", nargs="+", type=float, default=[0.0, 0.0])
    parser.add_argument("-k", "--carrying_capacities", nargs="+", type=float, default=[5, 5])
    parser.add_argument("-x", "--initial_counts", nargs="+", type=float, default=[100, 100])
    parser.add_argument("-d", "--death_rates", nargs="+", type=float, default=[0.001, 0.001])
    parser.add_argument("-l", "--grid_length", type=int, default=100)
    parser.add_argument("-h", "--grid_height", type=int, default=100)
    parser.add_argument("-m", "--interaction_radius", type=int, default=2)
    parser.add_argument("-n", "--reproduction_radius", type=int, default=1)
    parser.add_argument("-end", "--ticks", type=int, default=100)
    parser.add_argument("-freq", "--write_freq", type=int, default=10)
    args = parser.parse_args()

    interaction_matrix = (
        np.array(args.interaction_matrix).reshape([args.num_types, args.num_types]).tolist()
    )

    write_spatiallv_config(
        data_dir=args.data_dir,
        exp_dir=args.exp_name,
        config_name=args.run_name,
        seed=args.seed,
        dimension=args.dimension,
        growth_model=args.growth_model,
        num_types=args.num_types,
        interaction_matrix=interaction_matrix,
        intrinsic_growths=args.intrinsic_growths,
        carrying_capacities=args.carrying_capacity,
        initial_counts=args.initial_counts,
        death_rates=args.death_rates,
        interaction_radius=args.interaction_radius,
        reproduction_radius=args.reproduction_radius,
        grid_length=args.grid_length,
        grid_height=args.grid_height,
        ticks=args.ticks,
        write_freq=args.write_freq,
    )


if __name__ == "__main__":
    main()
