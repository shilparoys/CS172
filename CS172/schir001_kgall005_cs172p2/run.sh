#!/bin/bash
output_dir=$1
javac -cp jsoup-1.8.1.jar:lucene-core-3.6.0.jar:. Index.java Main.java Parser.java SearchQuery.java
exec java -cp jsoup-1.8.1.jar:lucene-core-3.6.0.jar:. Main $output_dir
