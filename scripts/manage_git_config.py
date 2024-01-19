#  Copyright 2024 MicroEJ Corp. All rights reserved.
#  Use of this source code is governed by a BSD-style license that can be found with this software.
import subprocess


def set_git_config(author_name, author_mail):
    # Set user name and mail in git conf
    name_conf_command = ["git", "config", "--local", "user.name", author_name]
    subprocess.run(name_conf_command, check=True)
    mail_conf_command = ["git", "config", "--local", "user.email", author_mail]
    subprocess.run(mail_conf_command, check=True)


def set_microej_delivery_git_config():
    set_git_config("MicroEJ GitHub Delivery", "delivery@microej.com")


def get_user_name():
    # Get Git global user name
    return subprocess.check_output(["git", "config", "--local", "user.name"], universal_newlines=True).strip()


def get_user_mail():
    # Get Git global user mail
    return subprocess.check_output(["git", "config", "--local", "user.email"], universal_newlines=True).strip()


def restore_git_config():
    # Get Git global user name
    user_name = subprocess.check_output(["git", "config", "--global", "user.name"], universal_newlines=True).strip()

    # Get Git global user email
    user_email = subprocess.check_output(["git", "config", "--global", "user.email"], universal_newlines=True).strip()

    # Restore the user configuration
    set_git_config(user_name, user_email)
