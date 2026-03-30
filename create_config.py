import argparse

import numpy as np

from config_utils import write_config


def main():
    """Generate and save sbatch script based on input arguments"""
    parser = argparse.ArgumentParser()
    parser.add_argument("-loc", "--save_loc", type=str, default="data/test")
    parser.add_argument("-seed", "--seed", type=int, default=42)
    parser.add_argument("-dim", "--dimension", type=int, default=2)
    parser.add_argument("-types", "--num_types", type=int, default=2)
    parser.add_argument(
        "-P",
        "--payoff_matrix",
        nargs="+",
        type=float,
        default=[0.1, 0.3, 0.2, 0.6, 0.4, 0.1, 0, 0, 0],
    )
    parser.add_argument("-x", "--initial_counts", nargs="+", type=float, default=[100, 100])
    parser.add_argument("-d", "--death_rates", nargs="+", type=float, default=[0.001, 0.001])
    parser.add_argument("-gl", "--grid_length", type=int, default=100)
    parser.add_argument("-gh", "--grid_height", type=int, default=100)
    parser.add_argument("-m", "--interaction_radius", type=int, default=2)
    parser.add_argument("-n", "--reproduction_radius", type=int, default=1)
    parser.add_argument("-end", "--ticks", type=int, default=100)
    parser.add_argument("-freq", "--write_freq", type=int, default=10)
    args = parser.parse_args()

    payoff_matrix = (
        np.array(args.payoff_matrix).reshape([args.num_types + 1, args.num_types + 1]).tolist()
    )

    write_config(
        save_loc=args.save_loc,
        seed=args.seed,
        dimension=args.dimension,
        num_types=args.num_types,
        payoff_matrix=payoff_matrix,
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
