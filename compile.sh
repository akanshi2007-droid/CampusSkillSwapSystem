#!/bin/sh
mkdir -p out
javac -encoding UTF-8 -cp "lib/*" -d out -sourcepath src src/campusskillswap/Main.java && echo "Compiled successfully. Now run ./run.sh"
