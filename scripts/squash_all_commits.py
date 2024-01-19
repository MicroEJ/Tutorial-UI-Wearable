#  Copyright 2024 MicroEJ Corp. All rights reserved.
#  Use of this source code is governed by a BSD-style license that can be found with this software.
import subprocess
import sys


def get_commits_count():
    # Execute git rev-list command to count commits on the specified branch
    command = ['git', 'rev-list', '--count', 'HEAD']
    return int(subprocess.check_output(command, stderr=subprocess.STDOUT).decode().strip())


def squash_and_push(branch_name):
    # Checkout the branch
    checkout_command = ["git", "checkout", branch_name]
    try:
        subprocess.run(checkout_command, check=True)
    except subprocess.CalledProcessError as e:
        print(f"Error checking out {branch_name}: {e}")
        sys.exit(1)

    commits_count = get_commits_count()
    if (commits_count > 1):
        # Create a new commit with git commit-tree
        new_commit_command = ["git", "commit-tree", "HEAD^{tree}", "-m", f"Add {branch_name}"]
        new_commit_hash = subprocess.check_output(new_commit_command, universal_newlines=True).strip()

        # Reset the branch to the new commit
        reset_command = ["git", "reset", "--hard", new_commit_hash]
        try:
            subprocess.run(reset_command, check=True)
        except subprocess.CalledProcessError as e:
            print(f"Error resetting branch {branch_name}: {e}")
            sys.exit(1)

        # Push the changes to the branch
        push_command = ["git", "push", "origin", branch_name, "--force"]
        try:
            subprocess.run(push_command, check=True)
        except subprocess.CalledProcessError as e:
            print(f"Error pushing changes to {branch_name}: {e}")
            sys.exit(1)

        print(f"Branch updated: {branch_name}")

    else:
        print(f"Nothing to do on branch: {branch_name}")
