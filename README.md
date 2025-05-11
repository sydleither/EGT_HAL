# EGT_HAL
Evolutionary game theory (EGT) model implemented with the Hybrid Automata Library (HAL)

## Installation

### Repository
By itself:
- `git clone --recursive git@github.com:sydleither/EGT_HAL.git`

As a submodule:
- `git submodule add https://github.com/sydleither/EGT_HAL`
- `git submodule update --init --recursive`

### Java
Java version: 21.0.2

`mkdir lib`

Install the jar files for [jackson-core-2.16.1](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core/2.16.1), [jackson-databind-2.16.1](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind/2.16.1), and [jackson-annotations-2.16.1](https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-annotations/2.16.1)

Place the three jar files in `lib/`.

`bash build.sh`

When modifying the Java code, run `javac -d "build" -cp "lib/*" @sources.txt` to recompile.

### Test Installation
`mkdir -p output/test/test/0`

`java -cp build/:lib/* SpatialEGT.SpatialEGT output test test 2D 0`
- Arguments are: overall data directory, experiment directory, experiment name, dimension, replicate/seed

### Usage Pipeline
The ABM takes in a json configuration to run experiments. The test installation didn't need one because it is hard-coded. In config_utils.py, see the function write_config() for the parameters that the json config requires. If each parameter is not included in the json, the ABM will not run. See any Python script in the data_generation/ directory of [https://github.com/sydleither/agent-based-games](https://github.com/sydleither/agent-based-games) for examples of how to interface with config_utils.py to write json configurations for experiments.