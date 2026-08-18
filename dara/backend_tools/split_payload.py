import os
import json
import hashlib
import sys

def obfuscate_and_split(gz_file_path, output_dir):
    os.makedirs(output_dir, exist_ok=True)
    filename = os.path.basename(gz_file_path)
    base_name = filename.replace(".tar.gz", "").replace(".gz", "")
    
    with open(gz_file_path, "rb") as f:
        data = f.read()

    sha256_hash = hashlib.sha256(data).hexdigest()
    
    half = len(data) // 2
    part_ba = data[:half]
    part_lm = data[half:]
    
    ba_path = os.path.join(output_dir, f"{base_name}.ba")
    lm_path = os.path.join(output_dir, f"{base_name}.lm")
    
    with open(ba_path, "wb") as f:
        f.write(part_ba)
    with open(lm_path, "wb") as f:
        f.write(part_lm)

    print(f"[OK] Split {filename} -> {base_name}.ba ({len(part_ba)} bytes) and {base_name}.lm ({len(part_lm)} bytes)")

    return {
        "id": base_name,
        "version": "1.0.2",
        "size_mb": f"{round(len(data) / (1024 * 1024), 1)} MB",
        "parts": [f"{base_name}.ba", f"{base_name}.lm"],
        "checksum": sha256_hash
    }

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python split_payload.py <path_to_tar_gz>")
        sys.exit(1)
        
    gz_input = sys.argv[1]
    output_directory = "./dist"
    pkg_info = obfuscate_and_split(gz_input, output_directory)
    
    manifest = {"packages": [pkg_info]}
    with open(os.path.join(output_directory, "manifest.json"), "w") as f:
        json.dump(manifest, f, indent=4)
        
    print(f"[OK] Manifest saved to {output_directory}/manifest.json")
