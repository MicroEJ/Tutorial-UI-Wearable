#  Copyright 2024 MicroEJ Corp. All rights reserved.
#  Use of this source code is governed by a BSD-style license that can be found with this software.
import sys
import squash_all_commits
import manage_git_config


def main():
    # Prompt for user confirmation
    user_input = input(
        f"\nProceed with squashing all step/solution branches on the remote (prior backup recommended)? (y/n): ").lower()

    if user_input != 'y':
        print("Update aborted.")
        sys.exit(0)

    # List of step and solution branches from 1 to 10, and "step/final"
    branches = [f"step/{x}" for x in range(1, 11)] + [f"solution/{x}" for x in range(1, 11)] + ["step/final"]

    # Set committer to MicroEJ name and email (for publication)
    manage_git_config.set_microej_delivery_git_config()

    # Cherry-pick, reset, and push to all branches
    for branch in branches:
        squash_all_commits.squash_and_push(branch)

    # Restore git config to the user's global
    manage_git_config.restore_git_config()


if __name__ == "__main__":
    main()
