#  Copyright 2024 MicroEJ Corp. All rights reserved.
#  Use of this source code is governed by a BSD-style license that can be found with this software.
import subprocess
import shutil
import os
import zipfile
from datetime import datetime
import argparse


def chmod_files(path):
    # Change permissions for the top-level folder
    os.chmod(path, 0o755)

    for root, dirs, files in os.walk(path):
        for file in dirs:
            os.chmod(os.path.join(root, file), 0o755)

        for file in files:
            os.chmod(os.path.join(root, file), 0o755)


def create_backup_zip(git_repo_addr):
    # Clone the repository to a temporary directory
    temp_directory = "tmp_dir"
    shutil.rmtree(temp_directory, ignore_errors=True)
    os.makedirs(temp_directory)

    subprocess.run(["git", "clone", "--mirror", git_repo_addr, temp_directory])

    # Switch to the cloned repository
    os.chdir(temp_directory)

    # Get the current date in the format yyyyMMdd-HHmmss
    current_date = datetime.now().strftime("%Y%m%d-%H%M%S")

    # Create a zip file with the specified name in the root folder
    zip_file_name = f"{current_date}_repository_backup.zip"
    zip_file_path = os.path.join("..", zip_file_name)

    with zipfile.ZipFile(zip_file_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, _, files in os.walk("."):
            for file in files:
                file_path = os.path.join(root, file)
                zipf.write(file_path, os.path.relpath(file_path, "."))

            # Add empty directories to the ZIP archive
            for dir in os.listdir(root):
                dir_path = os.path.join(root, dir)
                if os.path.isdir(dir_path) and not os.listdir(dir_path):
                    zipf.write(dir_path, os.path.relpath(dir_path, "."))

    # Clean up the temporary directory
    os.chdir("..")
    chmod_files(temp_directory)
    shutil.rmtree(temp_directory)

    print(f"Backup file created: {zip_file_path}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Script that does a backup (clone --mirror) of the specified git repository.")
    parser.add_argument("git_repo_addr", help="The git repository address.")

    args = parser.parse_args()
    git_repo_addr = args.git_repo_addr

    # Create the backup zip file
    create_backup_zip(git_repo_addr)
