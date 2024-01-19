#  Copyright 2024 MicroEJ Corp. All rights reserved.
#  Use of this source code is governed by a BSD-style license that can be found with this software.
import subprocess
import shutil
import os
import zipfile
from datetime import datetime
import re
import argparse


def fetch_all_branches():
    # Fetch all remote branches and prune deleted branches
    subprocess.run(["git", "fetch", "--all", "--prune"], check=False)

    # Pull changes for all branches
    subprocess.run(["git", "pull", "--all"], check=False)

    # Get a list of all branches
    branches = subprocess.check_output(["git", "branch", "-a"], universal_newlines=True).split()

    # Iterate through branches and checkout
    for branch in branches:
        if "remotes/origin/" in branch and not branch.endswith("/HEAD"):
            branch_name = branch.split("remotes/origin/")[1]
            if branch_name.startswith("step/") or branch_name.startswith("solution/"):
                subprocess.run(["git", "checkout", branch_name], check=False)


def checkout_step_one():
    # Checkout the step/1 branch
    subprocess.run(["git", "checkout", "step/1"], check=False)


def remove_unwanted_branches():
    branch_patterns_to_keep = ["step/", "solution/"]
    branches_to_delete = []

    # List all branches
    all_branches = subprocess.check_output(['git', 'branch'], stderr=subprocess.STDOUT).decode().splitlines()

    for branch in all_branches:
        branch_name = branch.strip('* ').strip()
        if not any(branch_name.startswith(pattern) for pattern in branch_patterns_to_keep):
            branches_to_delete.append(branch_name)

    # Delete branches
    for branch_name in branches_to_delete:
        subprocess.check_output(['git', 'branch', '-D', branch_name], stderr=subprocess.STDOUT)


def extract_version_from_gradle():
    # Read the version from build.gradle.kts
    gradle_path = "../build.gradle.kts"
    with open(gradle_path, "r") as gradle_file:
        for line in gradle_file:
            if "version" in line:
                # Use regular expression to extract version
                match = re.search(r"version\s*=\s*['\"](.*?)['\"]", line)
                if match:
                    return match.group(1).strip()


def chmod_files(path):
    # Change permissions for the top-level folder
    os.chmod(path, 0o755)

    for root, dirs, files in os.walk(path):
        for file in dirs:
            os.chmod(os.path.join(root, file), 0o755)

        for file in files:
            os.chmod(os.path.join(root, file), 0o755)


def create_package_zip(git_repo_addr):
    # Clone the repository to a temporary directory
    temp_directory = "tmp_dir"
    shutil.rmtree(temp_directory, ignore_errors=True)
    os.makedirs(temp_directory)

    subprocess.run(["git", "clone", git_repo_addr, temp_directory])

    # Switch to the cloned repository
    os.chdir(temp_directory)

    # Fetch all branches and checkout step/1
    fetch_all_branches()
    checkout_step_one()

    # Remove remote
    subprocess.run(["git", "remote", "remove", "origin"])

    # Remove unwanted branches
    remove_unwanted_branches()

    # Remove MicroEJ local git hooks
    shutil.rmtree(".git/hooks")

    # Extract version from build.gradle.kts
    version = extract_version_from_gradle()

    # Get the current date in the format yyyymmdd
    current_date = datetime.now().strftime("%Y%m%d")

    # Create a zip file with the specified name in the root folder
    zip_file_name = f"{current_date}_ui-training-wearable_{version}.zip"
    zip_file_path = os.path.join("..", zip_file_name)

    with zipfile.ZipFile(zip_file_path, 'w', zipfile.ZIP_DEFLATED) as zipf:
        for root, _, files in os.walk("."):
            for file in files:
                file_path = os.path.join(root, file)
                zipf.write(file_path, os.path.relpath(file_path, "."))

    # Clean up the temporary directory
    os.chdir("..")
    chmod_files(temp_directory)
    shutil.rmtree(temp_directory)

    print(f"Zip file created: {zip_file_path}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Script that packages the training materials in a zip file.")
    parser.add_argument("git_repo_addr", help="The address of the git repository to clone.")

    args = parser.parse_args()
    git_repo_addr = args.git_repo_addr

    # Create the zip file
    create_package_zip(git_repo_addr)
