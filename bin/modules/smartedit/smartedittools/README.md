# SmartEdit Tools

This folder contains the set of tools that are necessary to build, test, and run SmartEdit.

## Requirements

-   Node version: >=14.15.0 <15.0.0 || >=16.0.0 <17.0.0

## Installation

Add the smartedittools extension to the localextensions.xml file:

```
<extension name="smartedittools" />
```

## Rush

SmartEdit uses RushJS to link and keep track of the different projects making up SmartEdit.

Please check https://rushjs.io/pages/intro/welcome/ for more information.

### Rush Configuration

Below are some important configuration options used in Rush:

-   PNPM Options
    -   strictPeerDependencies: true,
    -   When this flag is true, "rush install" will fail if there are unsatisfied peer dependencies.
    -   The recommended value is true. However, this might cause compatibility issues with legacy packages. If this is the case, we might need to set this to false.
-   projectFolderMaxDepth: 20
    -   It is not recommended to have a very deep folder structure. It might be an indication that there are bad coding practices. In fact, RushJS team recommends a max depth of 2. This might be a good idea in the future. For now, the value is 20 to maintain backward compatibility.

Please check https://rushjs.io/pages/configs/rush_json/ for more configuration options.

Note: SmartEdit is using RushJS internally. We do not recommend running any Rush command manually, and instead recommend using SmartEdit tooling instead.

## Commands

```sh
# If you did not do it before, you have to run the rush update command at least once before building:
ant rushupdate -Dpath=path_to_smartedittools

# Dev team should run rush update with full mode to update all dependencies periodically
ant rushupdatefull -Dpath=path_to_smartedittools

# To build all libraries in bulk, you can choose one of these commands below:
run the commmand `ant build` from the smartedittools extension folder
or
run the command: `yenv build smartedit`
or
run the command: `ant rushbuild -Dpath=path_to_smartedittools`

# To build all libraries in bulk with development mode
ant rushbuilddev -Dpath=path_to_smartedittools

# For rebuild:
ant rushrebuild -Dpath=path_to_smartedittools

# For rebuild in dev mode:
ant rushrebuilddev -Dpath=path_to_smartedittools

# For rush check:
ant rushcheck -Dpath=path_to_smartedittools

# To run rush purge:
ant clean

# To run rush purge, delete pnpm store, rush home folder and npm cache, run:
ant clean && rm -rf ~/.pnpm-store && rm -rf ~/.rush/ && npm cache clear --force

```
