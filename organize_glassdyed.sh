#!/bin/bash

# Script to reorganize glassdyed textures into color-based folders

GLASSDYED_DIR="/mnt/c/Users/xdra1/IdeaProjects/ChiselModern/src/main/resources/assets/chisel/textures/block/glassdyed"

# List of all Minecraft dye colors
COLORS=("white" "orange" "magenta" "light_blue" "yellow" "lime" "pink" "gray" "light_gray" "cyan" "purple" "blue" "brown" "green" "red" "black")

cd "$GLASSDYED_DIR"

# Create color directories
for color in "${COLORS[@]}"; do
    mkdir -p "$color"
done

# Move files into their respective color folders
for file in *.png *.mcmeta; do
    # Skip if it's a directory
    if [ -d "$file" ]; then
        continue
    fi

    # Extract color from filename
    for color in "${COLORS[@]}"; do
        if [[ "$file" == ${color}_* ]]; then
            # Remove color prefix from filename
            newname="${file#${color}_}"
            mv "$file" "$color/$newname"
            echo "Moved $file to $color/$newname"
            break
        fi
    done
done

echo "Reorganization complete!"
