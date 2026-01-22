mkdir build_spatialegt
find HAL/HAL/ -type f -name "*.java" > sources_spatialegt.txt
find SpatialEGT -type f -name "*.java" >> sources_spatialegt.txt
cp HAL/HAL/lib/* lib/
javac -d "build_spatialegt" -cp "lib/*" @sources_spatialegt.txt