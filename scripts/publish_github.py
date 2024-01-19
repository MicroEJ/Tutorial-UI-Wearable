#  Copyright 2024 MicroEJ Corp. All rights reserved.
#  Use of this source code is governed by a BSD-style license that can be found with this software.
import subprocess
import argparse
import sys


def get_commits_count():
    # Execute git rev-list command to count commits on the specified branch
    command = ['git', 'rev-list', '--count', 'HEAD']
    return int(subprocess.check_output(command, stderr=subprocess.STDOUT).decode().strip())


def publish(git_repo_addr):
    # List of step and solution branches from 1 to 10, and "step/final"
    branches = [f"step/{x}" for x in range(1, 11)] + [f"solution/{x}" for x in range(1, 11)] + ["step/final"]

    for branch in branches:

        # Checkout the branch
        checkout_command = ["git", "checkout", branch]
        try:
            subprocess.run(checkout_command, check=True)
        except subprocess.CalledProcessError as e:
            print(f"Error checking out {branch}: {e}")
            sys.exit(1)

        subprocess.run(["git", "pull"], check=False)

        commits_count = get_commits_count()
        if (commits_count > 1):
            print(f"WARNING: the branch {branch} may not be squashed")

        # Push the changes to the branch
        push_command = ["git", "push", "--set-upstream", "--force", git_repo_addr, branch]
        try:
            subprocess.run(push_command, check=True)
        except subprocess.CalledProcessError as e:
            print(f"Error pushing changes to {branch}: {e}")
            sys.exit(1)

        print(f"Branch pushed to remote: {branch}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Pushes the step and solution branches to the specified git repository.")
    parser.add_argument("git_repo_addr", help="The git repository address.")

    args = parser.parse_args()
    git_repo_addr = args.git_repo_addr

    # Prompt for user confirmation
    user_input = input(f"\nProceed with pushing the training to {git_repo_addr}? (y/n): ").lower()

    if user_input != 'y':
        print("Update aborted.")
        sys.exit(0)

    # Create the backup zip file
    publish(git_repo_addr)
