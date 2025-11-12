"""Generate a sbatch .sb file based on the input arguments"""

import argparse

from config_utils import write_config


def main():
    """Generate and save sbatch script based on input arguments"""
    parser = argparse.ArgumentParser()
    parser.add_argument("-dir", "--data_dir", type=str, default="data")
    parser.add_argument("-exp", "--exp_name", type=str, default="experiment")
    parser.add_argument("-run", "--run_name", type=str, default="run")
    parser.add_argument("-seed", "--seed", type=int, default=42)
    parser.add_argument("-x", "--grid_x", type=int, default=100)
    parser.add_argument("-y", "--grid_y", type=int, default=100)
    parser.add_argument("-init", "--initial_count", type=int, default=1000)
    parser.add_argument("-fr", "--fraction_resistant", type=float, default=0.5)
    parser.add_argument("-m", "--interaction_radius", type=int, default=2)
    parser.add_argument("-n", "--reproduction_radius", type=int, default=1)
    parser.add_argument("-to", "--turnover", type=float, default=0.009)
    parser.add_argument("-mu", "--mutation_rate", type=float, default=0.0)
    parser.add_argument("-a", "--a", type=float, default=0.1)
    parser.add_argument("-b", "--b", type=float, default=0.12)
    parser.add_argument("-c", "--c", type=float, default=0.09)
    parser.add_argument("-d", "--d", type=float, default=0.15)
    parser.add_argument("-freq", "--write_freq", type=int, default=10)
    parser.add_argument("-end", "--end_time", type=int, default=100)
    args = parser.parse_args()

    payoff = [args.a, args.b, args.c, args.d]

    write_config(
        data_dir=args.data_dir,
        exp_dir=args.exp_name,
        config_name=args.run_name,
        seed=args.seed,
        payoff=payoff,
        num_cells=args.initial_count,
        proportion_r=args.fraction_resistant,
        x=args.grid_x,
        y=args.grid_y,
        interaction_radius=args.interaction_radius,
        reproduction_radius=args.reproduction_radius,
        turnover=args.turnover,
        mutation_rate=args.mutation_rate,
        write_freq=args.write_freq,
        ticks=args.end_time,
    )


if __name__ == "__main__":
    main()
