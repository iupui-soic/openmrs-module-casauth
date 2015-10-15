# openmrs-module-casauth

## Overview
The module allows authentication over CAS - Central authentication service

## Installation
Build the module from source. Install the omod file through OpenMRS module management
Configure the settings for CAS endpoints - login, application code, validate and logout
Configure the username and password for the user who will be used to authenticate other users.

###Build
To ensure that your commit builds fine run
```
mvn clean package
```
before opening a new pull request.

###Coding conventions

This module adheres to the OpenMRS coding conventions, please read
https://wiki.openmrs.org/display/docs/Coding+Conventions

####Code style

Help us to keep the code consistent!
This will produce readable diffs and make merging easier and quicker!

This module uses the Eclipse formatter plugin to automatically format *.java
files. This plugin is automatically executed when you build the module.

To manually run the formatter plugin, do
```
mvn java-formatter:format
```



