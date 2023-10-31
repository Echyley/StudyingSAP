/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
'use strict';

/**
 * Unlink SmartEdit projects:
 *
 * This project
 *
 * It will remove any extension from the master application. This makes sure that the cleanup of other
 * parts of the project and future builds are not affected by previous configurations.
 *
 */
const path = require('path');
const fs = require('fs');
const afs = fs.promises;
const util = require('util');

const writeFile = util.promisify(fs.writeFile);
const readFile = util.promisify(fs.readFile);

const baseExtensionPath = process.argv[2];
const DEBUG = process.argv[3];
const commonConfigPath = path.join(baseExtensionPath, 'common', 'config');
const seExtensionsFileName = path.join(commonConfigPath, 'smartedit-extensions.json');

DEBUG && console.log(`Un-Linking smartedit projects...`);

/**
 * This method reads the list of SmartEdit extensions' applications found in the smartedit-extensions.json
 * file.
 */
const getRegisteredExtensionsList = async () => {
    try {
        const rawFileContent = await readFile(seExtensionsFileName);
        return JSON.parse(rawFileContent).extensions;
    } catch {
        return [];
    }
};

/**
 * Gets the path to the directory of the application working as the SmartEdit master. There has to
 * be one master application.
 *
 * @param {*} registeredExtensions
 */
const getMasterExtensionPath = (registeredExtensions) => {
    const masterExtension = registeredExtensions.find((extension) => {
        return extension.type === 'master';
    });

    if (!masterExtension) {
        throw new Error('Cannot unlink extensions. Cannot find master application.');
    }

    return masterExtension.appDir;
};

/**
 * This method reads the package.json of the master application and removes all registered extensions
 * from the dependencies list.
 *
 * @param {*} masterApplicationPath
 * @param {*} registeredExtensions
 */
const removeExtensionsFromMaster = async (masterApplicationPath, registeredExtensions) => {
    DEBUG && console.log('Removing extensions from master app package.json');
    const registeredExtensionsNames = registeredExtensions.map((extension) => {
        return extension.name;
    });

    const packageJsonFilePath = path.join(masterApplicationPath, 'package.json');
    if (!fs.existsSync(packageJsonFilePath)) {
        return;
    }
    const packageJsonFile = await readFile(packageJsonFilePath);
    const packageJson = JSON.parse(packageJsonFile);

    const dependenciesKeys = Object.keys(packageJson.dependencies);
    dependenciesKeys.forEach((dependency) => {
        if (registeredExtensionsNames.includes(dependency)) {
            delete packageJson.dependencies[dependency];
        }
    });

    await writeFile(packageJsonFilePath, JSON.stringify(packageJson, null, 2));
    DEBUG && console.log('master app package.json cleaned');
};

const unlinkSmartEditExtensions = async () => {
    const registeredExtensions = await getRegisteredExtensionsList();
    if (!registeredExtensions.length) {
        return;
    }
    const masterApplicationPath = getMasterExtensionPath(registeredExtensions);

    await removeExtensionsFromMaster(masterApplicationPath, registeredExtensions);
};

(async () => {
    try {
        await unlinkSmartEditExtensions();
    } catch (e) {
        console.error(e);
        process.exit(1);
    }
})();
