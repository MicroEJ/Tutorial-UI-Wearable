.. image:: https://shields.microej.com/endpoint?url=https://repository.microej.com/packages/badges/sdk_6.0.json
   :alt: sdk_6.0 badge
   :align: left

.. image:: https://shields.microej.com/endpoint?url=https://repository.microej.com/packages/badges/arch_8.0.json
   :alt: arch_7.18 badge
   :align: left

.. image:: https://shields.microej.com/endpoint?url=https://repository.microej.com/packages/badges/gui_3.json
   :alt: gui_3 badge
   :align: left

Overview
========

This project contains coding challenges to learn the basics of UI development with MicroEJ.
The training is divided into 10 steps, each helping the user create the views of a smartwatch.

Note: This version of the training is compatible with `MICROEJ SDK 6 <https://docs.microej.com/en/latest/SDK6UserGuide/index.html>`_ only.
If you are using `MICROEJ SDK 5 <https://docs.microej.com/en/latest/SDKUserGuide/index.html>`_, please contact `our support <https://docs.microej.com/en/latest/support.html>`_.

How to follow the training?
===========================

Get the Manual
--------------

The training manual is provided in the document ``UI-training-manual.pdf``, in the ``doc`` directory.
Each step comes with a brief introduction that lays out the theoretical foundations and provides instructions.

Moving into steps
-----------------

The git repository that contains this project has multiple branches. Each branch ``step/...`` is the start of a step.
The branch ``step/1`` is the branch to start with. When the step is complete, move to branch ``step/2``, and so on.

The branches ``solution/...`` contain the solution for a given step.
For example, the branch ``solution/1`` contains the solution for step 1.

It is recommended to commit your changes or drop untracked changes before moving to the next step.
You can use ``git checkout -f BRANCH-NAME`` for example.

The final branch with all steps completed is on branch ``solution/10``.

Getting the step instructions
-----------------------------

To find the code placeholder and instructions for the current step, search for the text ``STEP X`` in the project files (match upper case).
For example, for the first step, look for ``STEP 1``.


Need help?
----------

You can find more documentation about UI application development on our website:

- `Application Developer Guide <https://docs.microej.com/en/latest/ApplicationDeveloperGuide/UI/ui.html>`_
- `Tutorial UI <https://docs.microej.com/en/latest/Tutorials/getStartedWithGUI/index.html>`_

You can also look into our `forum <https://forum.microej.com/>`_ or `get support <https://docs.microej.com/en/latest/support.html>`_.


Project structure
=================

Packages
--------

The project contains the following packages:

- ``com.microej.exercise.ui.activity``: contains the classes for the Activity application. The Activity application monitors the step count and progress toward the daily goal.
- ``com.microej.exercise.ui.applicationmenu``: contains the classes for the application menu. This menu shows a list of all the applications of the device. Of course, apart from the Activity application, all the applications are stub ones in the context of the exercise.
- ``com.microej.exercise.ui.stubapplication``: contains the classes for the stub applications. The stub application shows an icon and an image of the application.
- ``com.microej.exercise.ui.style``: contains the classes for the style management. These are convenient classes that specify the fonts and the images to use in the widgets.
- ``com.microej.exercise.ui.utils``: contains the utility classes.
- ``com.microej.exercise.ui.watchface``: contains the classes for the watch faces. There are two watch faces, one digital and one analog.

There are four pages in the application: the watch face, the application menu, the activity application, and the stub application.
In their respective package, the page comes with the necessary widgets.


Main classes
------------

Main
~~~~

It is the entry point of the project.

At boot, it does the following:

1. Starts the MicroUI framework.
2. Creates a desktop, a placeholder for the widget hierarchy.
3. Creates the watchface and sets it as the root widget of the desktop.
4. Shows the desktop on the display.

This class also provides convenient methods for navigating from one view to another.


Page
~~~~

Every page of the application extends the type ``Page``.
Subclasses have to implement the 3 following methods:

- ``getWidget()``: creates and returns a widget that represents the content of the page.
- ``populateStylesheet()``: sets the style attributes for the widgets of the page.
- ``update()``: notifies the page that the model data changed. Do whatever action to update the widget content.

Examples of pages:

- WatchfacePage
- ActivityPage
- ApplicationMenuPage
- StubApplicationPage

Model
~~~~~

This class simulates a business data model from which the pages can retrieve sensing or device data, like the step count or the battery level.
The sole instance of ``Model`` can be accessed with a call to ``Model.getInstance()``.
For example, one can retrieve the step count with a call to ``Model.getInstance().getStepCount()``.
To be notified of data changes in the model, register as an observer of the model: call ``Model.getInstance().setObserver(myPage)``.



Usage
=====

This application uses the generic Wearable VEE Port. This VEE Port simulates a typical wearable device with a round display (392 x 392 px).

Application flow
----------------

The user can go from the watch face to the application list and vice versa by pressing the physical button.
Clicking on an item in the list opens the page of the corresponding application. Pressing the button goes back to the list.

Run on the Simulator
--------------------

First, make sure to install MICROEJ SDK 6 as described in the `online documentation <https://docs.microej.com/en/latest/SDK6UserGuide/index.html>`_.

1. Open the Gradle pane
2. Under ``wearable`` > ``Tasks`` > ``microej``, double-click on ``runOnSimulator``

Note:
You can also do the same in the CLI, use the command ``.\gradlew.bat runOnSimulator`` (use ``gradlew`` on Linux/macOS)



Requirements
============

This project requires the following Foundation Libraries:

    EDC-1.3, BON-1.4, MICROUI-3.2, DRAWING-1.0

Dependencies
============

_All dependencies are retrieved transitively by Gradle.

Source
======

N/A.

Restrictions
============

None.

Appendix for the maintainers of this repository
===============================================

How to maintain this repository?
--------------------------------

The structure of this repository, where the multiple steps of the training sit on different git branches, can make it difficult to maintain and evolve.
The project comes with scripts that help with these tasks.

Modifications that affect all branches
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Some changes will affect all branches, for example when updating copyright notices, bumping library versions, updating README, updating SDK plugin versions, and so on.

In this case, you want to do the changes once on one branch and propagate them on all ``step/`` and ``solution/`` branches.
The Python script ``update_all_branches.py`` in the ``scripts`` directory is meant to automate the process of updating the multiple branches with the changes of a given commit.

**IMPORTANT**: the script will push your changes to the remote. 
Before proceeding, it is strongly recommended to do a backup of the repository.
See the section "How to back up and restore the repository" below for more information.

The following sequence has proven to give good results:

1. Checkout a new branch ``feature/`` from ``develop``.
2. Make changes.
3. Commit the changes in only one commit (or squash your commits into only one).
4. Run the script: ``python scripts/update_all_branches.py``.
5. Integrate the feature branch into ``develop`` according to the regular MicroEJ git-flow.

Note: you can also use the script with a specific commit hash like this: ``python scripts/update_all_branches.py --commit [commit_hash]`` to select a specific commit.

Modifications that affect only specific branches
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Some changes will affect only specific branches, for example when adding/modifying a step/solution.

All you have to do is update a given step/solution branch and squash all the commits into one (to clean commit history).
The Python script ``update_branch.py`` in the ``scripts`` directory is meant to automate the process of squashing all commits into one.

**IMPORTANT**: the script will push your changes to the remote.
Before proceeding, it is strongly recommended to do a backup of the repository.
See the section "How to back up and restore the repository" below for more information.

Do the following:

1. Checkout the branch ``step/`` or ``solution/`` to update.
2. Commit changes.
3. Run the script: ``python scripts/update_branch.py`` to squash all commits of the current branch.

Note: you can also use the script to squash all the commits of a specific branch like this: ``python scripts/update_branch.py --branch [branch_name]``.

How to publish this training on MicroEJ's GitHub?
-------------------------------------------------

To publish the ``step/`` and ``solution\`` branches to the github repository, do the following:

1. Make sure that all ``step/`` and ``solution\`` branches are squashed. This should already be the case if you followed the instructions above when updating the repository. If it's not, run the script ``squash_all_branches.py``.
2. Run the script ``publish_github.py`` (specify the correct GitHub repository address as an argument).


How to package this training in a zip archive?
----------------------------------------------

It can be convenient to package the content of this repository to share it with others as a standalone training resource.

The Python script ``package_repository.py``, in the ``scripts`` directory, creates a zip file containing the training, ready-to-use.

The script does the following:

1. Clone the specified repository to a temporary directory.
2. Fetch all branches and check out the branch ``step/1``.
3. Remove unwanted branches (develop and master).
4. Extract the version from the ``build.gradle.kts`` file.
5. Create a zip file containing the repository contents.
6. Clean up the temporary directory.

The training is exported as a zip file named ``yyyymmdd_ui-training-wearable_x.y.z.zip`` where ``yyyymmdd`` is the current date and ``x.y.z`` is the current version of the training.

How to back up and restore the repository
-----------------------------------------

You can use the Python script ``backup_repository.py`` in the ``scripts`` directory to back up a repository.
The backup will be exported as a zip file ``yyyyMMdd-HHmmss_repository_backup.zip``.
The repository can then be restored from that state if needed.

Let's assume something went wrong, and you want to restore the repository to the backup state:

1. Unzip the backup file on your filesystem (**not in this git repository!**).
2. cd to the unzipped directory. Note: this is a bare repository (obtained with ``git clone --mirror``).
3. Open a terminal from this directory and execute the command: ``git push --mirror [repository_address]``

.. ReStructuredText
.. Copyright 2022-2024 MicroEJ Corp. All rights reserved.
.. Use of this source code is governed by a BSD-style license that can be found with this software.
