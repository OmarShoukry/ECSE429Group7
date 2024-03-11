#!/bin/bash
DIR="/Applications/eclipse/java-2021-06/ECSE429/src/test/resources/features"
for file in "$DIR"/*
do
  # Cucumber runs the test using the name of the feature files in alphabetical order
  # In order to randomize the order the tests are run in we randomly append a letter at the beginning of each feature's name
  # See code below
  letters="ABCDEFGHIJKLMNOPQRSTUVWXYZ"
  randomletter=${letters:$(( RANDOM % ${#letters} )):1}
  filename=$(basename "$file")
  mv "$file" "$DIR/$randomletter$filename"
done
echo "Features testing order randomized"