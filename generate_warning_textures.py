from PIL import Image
import os

# Paths
base_path = "src/main/resources/assets/chisel/textures/block/warning"
base_texture_path = os.path.join(base_path, "base.png")

# Load base texture
base = Image.open(base_texture_path).convert("RGBA")

# All warning variants (overlays)
variants = [
    "radiation",
    "biohazard",
    "fire",
    "explosion",
    "death",
    "falling",
    "fall",
    "voltage",
    "generic",
    "acid",
    "underconstruction",
    "sound",
    "noentry",
    "cryogenic",
    "oxygen"
]

for variant in variants:
    overlay_path = os.path.join(base_path, f"{variant}.png")
    output_path = os.path.join(base_path, f"{variant}.png")

    if os.path.exists(overlay_path):
        # Load overlay
        overlay = Image.open(overlay_path).convert("RGBA")

        # Resize if needed to match base
        if overlay.size != base.size:
            overlay = overlay.resize(base.size, Image.NEAREST)

        # Composite: base + overlay
        combined = Image.alpha_composite(base.copy(), overlay)

        # Save
        combined.save(output_path)
        print(f"Generated: {variant}.png")
    else:
        print(f"Skipped (not found): {variant}.png")

print("Done!")
