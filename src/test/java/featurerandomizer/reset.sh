#!/bin/bash
DIR="/Applications/eclipse/java-2021-06/ECSE429/src/test/resources/features"

for file in "$DIR"/*.feature
do
  # Remove the random letter appended to the front of the file name
  filename=$(basename "$file")
  cleanfilename=${filename:1}
  mv "$file" "${file%/*}/$cleanfilename"
done

echo "Feature files cleaned up"
