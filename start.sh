#!/bin/bash
JARPATH=$(ls target | grep ".jar$")
JARPATH="target/"$JARPATH
java -jar $(JARPATH) & echo $! > ./pid.file &
