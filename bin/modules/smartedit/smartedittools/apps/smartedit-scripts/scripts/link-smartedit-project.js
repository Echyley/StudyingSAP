/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
'use strict';

/**
 * Link SmartEdit projects:
 * 
 * This script receives a csv of smartedit extensions paths.
 * It will can each extension/apps folders to search for all "smartedit.json" files within them.
 * If smartedit.json is found, it will generate the smartedit-extensions.json in common/config folder with configuration for each application: name, projectFolder, type (inner|outer).
 * It will also create the rush.json file (based on the rush.tpl.json template file) will the reference of all the projects that will be configured with rush.
 * As per contract, each application must:
 * - Have an "apps" folder at the root of the extension folder.
 * - Each application folder in the "apps" folder must have a "smartedit.json" file of the following format (see example below).
 * Only "name" is mandatory. The "extension" is optional.
    "name": "cmssmarteditcontainer",
    "extension": {
        "type": "container",
        "angularApp": "cmssmarteditContainer"
    }
 */

const path = require('path');
const { promises: fs } = require('fs');

// Variables
const EXTENSIONS_PATHS = (process.argv[2] || []).split(',').filter((n) => n);
const BASE_EXTENSION_PATH = process.argv[3];
const DEBUG = process.argv[4];
const COMMON_CONFIG_PATH = path.join(BASE_EXTENSION_PATH, 'common', 'config');

const RUSH_TEMPLATE_FILE_PATH = path.join(COMMON_CONFIG_PATH, 'rush.tpl.json');
const RUSH_FILE_PATH = path.join(BASE_EXTENSION_PATH, 'rush.json');
const EXTENSIONS_FILE_PATH = path.join(COMMON_CONFIG_PATH, 'smartedit-extensions.json');

const readJSON = async (file) => {
    try {
        return JSON.parse(await fs.readFile(file));
    } catch (err) {
        console.error(`Error reading JSON file ${file}.`);
        throw err;
    }
};

const writeJSON = (file, data) => {
    return fs.writeFile(file, JSON.stringify(data, null, 2));
};

const readApps = async (extensionsPaths) => {
    const apps = await getListOfExtensionsApps(extensionsPaths);

    DEBUG && console.log('****** smartedit-extensions.json ******');
    DEBUG && console.log(apps.extensions);

    await writeJSON(EXTENSIONS_FILE_PATH, {
        extensions: apps.extensions
    });

    return apps;
};

/**
 * Scan each extension path "apps" folder, the `smarteditExtensionsJSON` returned has information of all the application that needs to be linked.
 * @param {*} extensionsPaths
 * @param {*} smarteditExtensionsJSON
 */
const getListOfExtensionsApps = async (extensionsPaths) => {
    const extensionApps = [].concat(
        ...(await Promise.all(
            extensionsPaths.map(async (extensionPath) => {
                const appsFolderPath = path.join(path.resolve(extensionPath), 'apps');
                DEBUG && console.log(`scanning appsFolderPath: ${appsFolderPath}...`);

                const appDirs = await getFirstLevelDirs(appsFolderPath);
                DEBUG && console.log(`applications list: ${appDirs}`);

                return getExtensionApps(appDirs);
            })
        ))
    );

    const masterApps = extensionApps.filter((ext) => ext.type && ext.type === 'master');
    if (masterApps.length !== 1) {
        throw new Error(
            masterApps.length > 1
                ? `There can be only one smartedit master application, found ${masterApps.length}`
                : 'No smartedit master application found.'
        );
    }
    const masterApp = masterApps[0];
    const masterAppConfig = getMasterAppConfig(extensionApps, masterApp);

    return {
        extensions: reorderAppsByDepTree(extensionApps),
        masterAppConfig
    };
};

/**
 * Data structure for calculating the dependency nodes.
 */
class ExtensionDepTreeNode {
    constructor(extension) {
        this.extension = extension;
        this.parent = null;
        this.children = [];
    }
}

/**
 * Orders the apps in the correct dependency order.
 * @param {*} apps
 */
const reorderAppsByDepTree = (apps) => {
    const allAppNames = new Set(apps.map(({ name }) => name));
    const appNodeMap = new Map();

    apps.forEach((app) => {
        /**
         * Removes all unrelated apps.
         */
        app.dependencies = app.dependencies.filter((dep) => {
            return allAppNames.has(dep);
        });

        const node = new ExtensionDepTreeNode(app);
        appNodeMap.set(app.name, node);
    });

    apps.forEach(({ name, dependencies }) => {
        const node = appNodeMap.get(name);

        dependencies.forEach((dep) => {
            const childDep = appNodeMap.get(dep);
            childDep.parent = node;

            node.children.push(childDep);
        });
    });

    let root = null;
    for (const app of appNodeMap.values()) {
        if (app.parent === null) {
            root = app;
            break;
        }
    }

    return Array.from(traverseAppDepTree(root), ({ extension }) => extension);
};

/**
 *
 * @param {*} node The app node
 * @param {*} order The order in which the extensions should be loaded.
 */
const traverseAppDepTree = (node, order = new Set()) => {
    node.children.forEach((child) => {
        traverseAppDepTree(child, order);
    });

    order.add(node);
    return order;
};

/**
 * Get the list of 1st-level folders within the given `dir`
 * @param {*} dir
 * @param {*} filelist
 */
const getFirstLevelDirs = async (dir) => {
    const files = await fs.readdir(dir);

    const directories = await Promise.all(
        files.map((file) => {
            const filePath = path.join(dir, file);

            return fs.stat(filePath).then((stat) => {
                return stat.isDirectory() ? filePath : null;
            });
        })
    );

    return directories.filter((directory) => directory);
};

const getExtensionApps = async (appDirs) => {
    const apps = await Promise.all(appDirs.map((appDir) => getAppConfigurations(appDir)));

    return apps.filter((app) => app);
};

const getAppConfigurations = async (appDir) => {
    const smarteditJsonFilePath = path.join(appDir, 'smartedit.json');

    try {
        await fs.access(smarteditJsonFilePath);
    } catch (e) {
        return null;
    }

    DEBUG && console.log(`Found smartedit.json in ${appDir}...`);
    const smartEditJson = await readJSON(smarteditJsonFilePath);

    const { extension: { type } = {} } = smartEditJson;
    const packageJsonFilePath = path.join(appDir, `package${type === 'master' ? '.tpl' : ''}.json`);
    const packageJson = await readJSON(packageJsonFilePath);

    const extensionInfo = {
        name: packageJson.name,
        version: packageJson.version,
        projectFolder: path.relative(BASE_EXTENSION_PATH, appDir),
        appDir,
        dependencies: Object.keys(packageJson.dependencies || {})
    };

    if (smartEditJson.extension) {
        if (smartEditJson.extension.type) {
            extensionInfo.type = smartEditJson.extension.type;
        }
        if (smartEditJson.extension.angularApp) {
            extensionInfo.angularApp = smartEditJson.extension.angularApp;
        }
    }

    return extensionInfo;
};

const updateRushJson = async (extensions) => {
    const rushJson = await readJSON(RUSH_TEMPLATE_FILE_PATH);

    rushJson.projects = rushJson.projects.concat(
        extensions.map(({ name, projectFolder }) => {
            return {
                packageName: name,
                projectFolder: projectFolder
            };
        })
    );

    DEBUG && console.log('****** rush projects ******');
    DEBUG && console.log(rushJson.projects);

    await writeJSON(RUSH_FILE_PATH, rushJson);
};

const getMasterAppConfig = (extensions, masterApp) => {
    DEBUG && console.log(`Found smartedit master application: ${masterApp.name}`);
    const deps = extensions
        .filter((ext) => ext.name !== masterApp.name)
        .map((ext) => {
            return {
                name: ext.name,
                version: ext.version
            };
        });

    deps.forEach((dep) => {
        masterApp.dependencies.push(dep.name);
    });

    return {
        ...masterApp,
        deps
    };
};

/**
 * Editing the smartedit 'master' application package.json to set it's list of "dependencies".
 * The smartedit 'master' application is identified by having the property "extension": { "type": "master" } in it's smartedit.json file.
 * There can (and must) be only one smartedit 'master' application that exist.
 * @param {*} masterAppConfig
 */
const editMasterAppPackageJson = async ({ appDir: applicationPath, deps: dependencies }) => {
    const packageJsonTplPath = path.join(applicationPath, 'package.tpl.json');
    const packageJsonFilePath = path.join(applicationPath, 'package.json');

    DEBUG && console.log(process.argv);
    DEBUG && console.log('Adding npm dependencies to application, path:', applicationPath);
    DEBUG && console.log(`Dependencies:`);
    DEBUG && console.log(dependencies);

    const packageJson = await readJSON(packageJsonTplPath);

    dependencies.forEach((dep) => {
        packageJson.dependencies[dep.name] = dep.version;
    });

    await writeJSON(packageJsonFilePath, packageJson);
};

(async () => {
    DEBUG && console.log(`🔗 Linking smartedit projects...`);
    DEBUG && console.log(`extensionsPaths: ${EXTENSIONS_PATHS}`);
    const { extensions, masterAppConfig } = await readApps(EXTENSIONS_PATHS);

    await Promise.all([editMasterAppPackageJson(masterAppConfig), updateRushJson(extensions)]);
})().catch((e) => {
    console.error(e);
    process.exit(1);
});
