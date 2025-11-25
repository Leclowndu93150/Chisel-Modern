#!/bin/bash

# Script to organize glasspanedyed textures into color subdirectories

cd "/mnt/c/Users/xdra1/IdeaProjects/ChiselModern/src/main/resources/assets/chisel/textures/block/glasspanedyed"

# List of colors
colors="white orange magenta light_blue yellow lime pink gray light_gray cyan purple blue brown green red black"

# Create directories and move files
for color in $colors; do
    mkdir -p "$color"
    # Move files that start with the color name
    for file in ${color}_*; do
        if [ -f "$file" ]; then
            # Remove the color prefix from filename
            newname="${file#${color}_}"
            mv "$file" "$color/$newname"
        fi
    done
done

echo "Done organizing glasspanedyed textures!"
ls -la
