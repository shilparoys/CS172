#!/bin/bash
seed_file=$1
num_pages=$2
hops_away=$3
output_dir=$4
javac -cp .:jsoup-1.8.1.jar Test.java
exec java -cp .:jsoup-1.8.1.jar Test $seed_file $num_pages $hops_away $output_dir

