=========
Changelog
=========

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

------------------
1.2.0 - 2024-01-19
------------------

Added
=====

- Add the training manual in the ``doc`` directory.
- Add modules and plugins repositories configuration in the build and settings scripts of the project.
- Add section in the README on how to update this repository and how to package this repository.
- Add scripts to help update and package this repository.

Changed
=======

- Bump MICROEJ SDK plugin to 0.14.0.
- Bump edc to 1.3.5.
- Bump microui to 3.2.0.
- Bump mwt to 3.5.2.
- Bump widget to 5.0.0.
- Fix compilation errors when bumping to widget 5.0.0.
- Use generic VEE Port for wearable 1.5.0.
- Bump Gradle version to 8.2.
- Update README and move to RST.
- Migrate CHANGELOG to RST.
- Use correct license in LICENSE.txt
- Update Copyright notices.
- Change the images heap to 615000 bytes to match with the display of the generic wearable VEE Port (392 x 392 px).
- Fix wording and grammar in manual, code and readme.

Removed
=======

- Remove JenkinsFile (not used).
- Remove SDK 5 files (.classpath, .project, module.ivy).

------------------
1.1.0 - 2023-05-06
------------------

Added
=====

- Add Gradle build files for SDK6 support.

Changed
=======

- Bump edc to 1.3.5.
- Bump microui to 3.1.0.
- Bump mwt to 3.4.0.
- Update README.md with Gradle setup instructions.
- Fix CHANGELOG date format.

------------------
1.0.1 - 2022-09-07
------------------

Added
=====

- Add solutions in ``solution/...`` branches.


Changed
=======

- Fix the Run Configurations.
- Fix the instructions in the README.


------------------
1.0.0 - 2022-08-26
------------------

Added
=====

- Add the 10 steps of the UI training.

  
.. ReStructuredText
.. Copyright 2022-2024 MicroEJ Corp. All rights reserved.
.. Use of this source code is governed by a BSD-style license that can be found with this software.
