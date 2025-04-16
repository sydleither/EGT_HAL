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

### Test Installation
`mkdir -p output/test/test/0`

`java -cp build/:lib/* SpatialEGT.SpatialEGT output test test 2D 0`

More details coming soon.