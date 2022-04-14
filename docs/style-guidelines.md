# Style guidelines

Code style guidelines exist so everyone writes code that looks about the same. It also makes sure all methods have a
javadoc. We have a Checkstyle configuration that enforces most of the guidelines, but not everything. If a pipeline
build fails with a Checkstyle violation, please try to fix it.

## Overview

| Type | Value |
|---|---|
| Classes | `PascalCase` |
| Variables | `camelCase` |
| Endpoints | `kebab-case` |
| CSS classes and ids | `kebab-case` |
| Indentation | 4 spaces |

## IntelliJ Checkstyle plugin

There is a handy IntelliJ plugin you can use to check if anything violates the Checkstyle. You can find it under
`File > Settings... > Plugins > IntelliJ-IDEA`. You then have to add our Checkstyle configuration by going to
`File > Settings... > Tools > Checkstyle`. Click on the `+` to add a configuration. Give it a description and browse
to the `./config/checkstyle/checkstyle.xml` file. Click on `Next`, `Next` and `Finish`. Now go to
the CheckStyle tab at the bottom of IntelliJ. Under `Rules`, select the configuration you just added. Now you can
press the `Check Project` button at the left to check the whole project.
