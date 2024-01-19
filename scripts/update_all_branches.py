#  Copyright 2024 MicroEJ Corp. All rights reserved.
#  Use of this source code is governed by a BSD-style license that can be found with this software.
import subprocess
import sys
import argparse
import squash_all_commits
import manage_git_config
import time


def get_current_commit_hash():
    return subprocess.check_output(["git", "rev-parse", "HEAD"], universal_newlines=True).strip()


def cherry_pick_and_push(branch_name, current_commit_hash):
    # Checkout the branch
    checkout_command = ["git", "checkout", branch_name]
    try:
        subprocess.run(checkout_command, check=True)
    except subprocess.CalledProcessError as e:
        print(f"Error checking out {branch_name}: {e}")
        sys.exit(1)

    # Cherry-pick the current commit with --strategy=recursive -X theirs option
    cherry_pick_command = ["git", "cherry-pick", "--strategy=recursive", "-X", "theirs", current_commit_hash]
    try:
        subprocess.run(cherry_pick_command, check=True)
    except subprocess.CalledProcessError as e:
        print(f"Error cherry-picking on {branch_name}: {e}")
        sys.exit(1)

    squash_all_commits.squash_and_push(branch_name)


def main():
    parser = argparse.ArgumentParser(
        description="Updates all step/solution branches with the specified commit. If no commit is specified, the latest commit of the current branch is used.")
    parser.add_argument("--commit", default=None, help="The commit to apply on all step/solution branches.")

    args = parser.parse_args()
    commit_hash = args.commit

    if commit_hash is None:
        # Get the current commit hash
        commit_hash = get_current_commit_hash()

    # Prompt for user confirmation
    user_input = input(
        f"\nSelected commit for updating all branches: {commit_hash}\nProceed with updating the remote (prior backup recommended)? (y/n): ").lower()

    if user_input != 'y':
        print("Update aborted.")
        sys.exit(0)

    # List of step and solution branches from 1 to 10, and "step/final"
    branches = [f"step/{x}" for x in range(1, 11)] + [f"solution/{x}" for x in range(1, 11)] + ["step/final"]

    # Set committer to MicroEJ name and email (for publication)
    manage_git_config.set_microej_delivery_git_config()
    user_name = manage_git_config.get_user_name()
    user_mail = manage_git_config.get_user_mail()
    print(f"Author: {user_name}, {user_mail}", flush=True)

    # Cherry-pick, reset, and push to all branches
    for branch in branches:
        cherry_pick_and_push(branch, commit_hash)

    # Restore git config to the user's global
    manage_git_config.restore_git_config()

if __name__ == "__main__":
    main()
