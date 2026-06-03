#!/usr/bin/env python3
"""
Convert a square image to Android adaptive icon foreground.
Usage: python create_icon.py <input_image_path>

This will generate ic_launcher_foreground.png in all mipmap directories.
"""
import sys
import os
from pathlib import Path

try:
    from PIL import Image, ImageOps
except ImportError:
    print("Installing Pillow...")
    os.system(f"{sys.executable} -m pip install Pillow")
    from PIL import Image, ImageOps

def create_icon(input_path):
    # Output directories
    base_dir = Path(__file__).parent.parent / "app" / "src" / "main" / "res"

    # Android adaptive icon foreground size: 432x432 pixels (108dp at xxxhdpi)
    # The safe zone is 66% of the total (72dp in center of 108dp)
    target_size = 432
    safe_zone = int(target_size * 0.66)  # 285 pixels

    # Load and process image
    img = Image.open(input_path)

    # Convert to PNG if needed
    img = img.convert("RGBA")

    # Make it square by cropping to center
    width, height = img.size
    if width != height:
        # Center crop to square
        size = min(width, height)
        left = (width - size) // 2
        top = (height - size) // 2
        img = img.crop((left, top, left + size, top + size))

    # Resize to target size
    img = img.resize((target_size, target_size), Image.Resampling.LANCZOS)

    # The foreground should have the content in the safe zone (center 66%)
    # Create a new transparent canvas
    foreground = Image.new("RGBA", (target_size, target_size), (0, 0, 0, 0))

    # Paste the resized image centered
    offset = (target_size - safe_zone) // 2
    resized_content = img.resize((safe_zone, safe_zone), Image.Resampling.LANCZOS)
    foreground.paste(resized_content, (offset, offset), resized_content if resized_content.mode == 'RGBA' else None)

    # Save to all density directories
    densities = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }

    for density, size in densities.items():
        out_dir = base_dir / density
        out_dir.mkdir(parents=True, exist_ok=True)

        # Resize for this density
        icon = foreground.resize((size, size), Image.Resampling.LANCZOS)
        icon.save(out_dir / "ic_launcher_foreground.png", "PNG")
        print(f"Created {density}/ic_launcher_foreground.png ({size}x{size})")

    # Also save a large version for the drawable directory
    drawable_dir = base_dir / "drawable"
    drawable_dir.mkdir(parents=True, exist_ok=True)
    foreground.save(drawable_dir / "ic_launcher_foreground.png", "PNG")
    print(f"Created drawable/ic_launcher_foreground.png ({target_size}x{target_size})")

    print("\nDone! App icon has been updated.")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python create_icon.py <input_image_path>")
        print("Example: python create_icon.py C:\\Users\\koibito\\Desktop\\cat.png")
        sys.exit(1)

    input_path = sys.argv[1]
    if not os.path.exists(input_path):
        print(f"Error: File not found: {input_path}")
        sys.exit(1)

    create_icon(input_path)
