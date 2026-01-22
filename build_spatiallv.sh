mkdir build_spatiallv
find HAL/HAL/ -type f -name "*.java" > sources_spatiallv.txt
find SpatialLV -type f -name "*.java" >> sources_spatiallv.txt
cp HAL/HAL/lib/* lib/
javac -d "build_spatiallv" -cp "lib/*" @sources_spatiallv.txt