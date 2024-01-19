#  Copyright 2024 MicroEJ Corp. All rights reserved.
#  Use of this source code is governed by a BSD-style license that can be found with this software.
import subprocess
import sys
import argparse
import squash_all_commits
import manage_git_config


def get_current_branch():
    command = ['git', 'rev-parse', '--abbrev-ref', 'HEAD']
    current_branch = subprocess.check_output(command, stderr=subprocess.STDOUT).decode().strip()
    return current_branch


def main():
    parser = argparse.ArgumentParser(
        description="Updates the specified branch (squash all commits and push to remote). If no branch is specified, use the current branch.")
    parser.add_argument("--branch", default=None, help="The branch to update.")

    args = parser.parse_args()
    branch_name = args.branch

    if branch_name is None:
        # Get the current branch
        branch_name = get_current_branch()

    # Prompt for user confirmation
    user_input = input(
        f"\nSelected branch for update: {branch_name}\nProceed with update the remote (prior backup recommended)? (y/n): ").lower()

    if user_input != 'y':
        print("Update aborted.")
        sys.exit(0)

    # Set committer to MicroEJ name and email (for publication)
    manage_git_config.set_microej_delivery_git_config()

    squash_all_commits.squash_and_push(branch_name)

    # Restore git config to the user's global
    manage_git_config.restore_git_config()


if __name__ == "__main__":
    main()
