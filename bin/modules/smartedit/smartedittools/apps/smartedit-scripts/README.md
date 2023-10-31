<h1 align="center">SmartEdit CLI</h1>
The smartedit-scripts library contains scripts and other tools that are necessary to setup and manage SmartEdit installation.

## Prerequisites

To execute the smartedit-scripts it must be added to rush.json (this should be the case out-of-the-box) and it must be installed with Rush. To do this, please follow the instructions in [smartedittools](../../#commands). Once this is done, the smartedit-scripts will have a copy of node_modules in it.

> 🚧 Important Note
>
> Never execute `npm install` directly on the smartedit-scripts folder. The smartedit-scripts and all other npm packages in the SmartEdit repository are managed only through the Rush tooling.

## Scripts

The `scripts/` folder contains a list of utility scripts that are used when managing SmartEdit.

### Link SmartEdit

This script is used to find the libraries defined in each SmartEdit extension and link them with Rush.
The script receives a csv of smartedit extensions paths and does the following:

-   It will scan each extension/apps folders to search for all `smartedit.json` files within them.
-   If `smartedit.json` is found, it will generate the smartedit-extensions.json in common/config folder with configuration for each application: name, projectFolder, type (inner|outer).
-   It will also create the `rush.json` file (based on the rush.tpl.json template file) will the reference of all the projects that will be configured with Rush.

Note: For this script to work, each application must abide by the following contract:

-   Have an "apps" folder at the root of the extension folder.
-   Each application folder in the "apps" folder must have a "smartedit.json" file of the following format (only the "name" is mandatory):
    ```json
    {
        "name": "cmssmarteditcontainer",
        "extension": {
            "type": "container",
            "angularApp": "cmssmarteditContainer"
        }
    }
    ```

### Unlink SmartEdit

This script removes any extension from the smartedit-master application. This ensures proper cleanup of other parts of the project and that future builds are not affected by previous configurations.

### Migrate SmartEdit

This script is used to migrate a legacy SmartEdit-dependent extension to the 21.05 librarified version.

Currently, these versions are supported by the migration script:

-   20.05

The following snippet shows how to perform the migration:

```bash
node migrate-smartedit.js -extName=smartedit -extPath=path/to/my/extension -gitBranch=my-git-branch
```

The migration can also be executed through ant as shown below:

```bash
ant migrateSmarteditExtension -DextName=<extension-name-to-migrate> -DextPath=/path/to/extension/to/migrate
```

For more information please refer to this [link](https://cxwiki.sap.com/display/CxM/Librarification+-+Upgrading+personalizationsmartedit+and+merchandisingsmartedit+Extensions).
