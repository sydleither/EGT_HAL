"""Generate a sbatch .sb file based on the input arguments.

Expected usage:
python3 create_sbatch_job.py email name time memory path (node)

Where:
email: email for notification of failed jobs
name: name of job and sbatch file
time: job time limit with format days-hours:minutes (00-00:00)
memory: job memory limit with unit (eg 1gb)
path: the full path to the code
node: optional, the buy-in node to run jobs on
"""

import os
import sys


def sbatch(email, name, time, memory, path, node):
    """Generate sbatch script str"""
    sbatch_script = [
        "#!/bin/bash --login",
        "#SBATCH --mail-type=FAIL",
        f"#SBATCH --mail-user={email}",
        f"#SBATCH --job-name={name}",
        f"#SBATCH -o out/{name}/%A.out",
        f"#SBATCH --time={time}",
        f"#SBATCH --mem-per-cpu={memory}",
        "module load Java/21.0.2",
        f"cd {path}",
        "java -cp build/:lib/* SpatialEGT.SpatialEGT ${1} ${2} ${3} ${4} ${5}",
    ]
    if node is not None:
        sbatch_script.insert(1, f"#SBATCH -A {node}")
    return "\n".join(sbatch_script)


def main(email, name, time, memory, path, node=None):
    """Generate and save sbatch script based on input arguments"""
    script = sbatch(email, name, time, memory, path, node)
    if not os.path.exists(f"out/{name}"):
        os.makedirs(f"out/{name}")
    with open(f"job_{name}.sb", "w", encoding="UTF-8") as f:
        f.write(script)


if __name__ == "__main__":
    if len(sys.argv) in (6, 7):
        main(*sys.argv[1:])
    else:
        print("Please see the module docstring for usage instructions.")
