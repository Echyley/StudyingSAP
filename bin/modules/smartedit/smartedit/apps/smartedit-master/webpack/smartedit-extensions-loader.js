/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const fs = require('fs');
const EOL = require('os').EOL;
const path = require('path');
const util = require('util');

const readFile = util.promisify(fs.readFile);
const writeFile = util.promisify(fs.writeFile);

/**
 * This method creates the index file that will be used by WebPack. This file is created by reading
 * the original index.ts file and appending all the imports needed by the SmartEdit extensions.
 *
 * @param extensionsToLoad The list of SmartEdit extensions to load
 * @param target The target for which to create the master index file. It can be inner or container.
 */
const createMasterIndexFile = async (extensionsToLoad, target) => {
    try {
        const folderPath = path.resolve(`./src/${target}`);
        const originalFilePath = path.join(folderPath, '/index.ts');
        const destFilePath = path.join(folderPath, '/master_index.ts');
        let fileContent = await readFile(originalFilePath, 'utf8');

        fileContent += EOL;
        extensionsToLoad.forEach((extension) => {
            fileContent += `require('${extension}/dist/styles.css');${EOL}`;
            fileContent += `import '${extension}';${EOL}`;
        });

        await writeFile(destFilePath, fileContent);
    } catch (ex) {
        throw new Error(`Cannot create master index file.`, ex);
    }
};

/**
 * This method retrieves all the information about the SmartEdit extensions configured in the given target and creates a
 * master file will all the imports needed by those extensions.
 *
 * @param {*} target the target for which to load extensions. It can be container or inner.
 *
 * @returns the information about the extensions loaded.
 */
const loadExtensions = async (target) => {
    if (!target) {
        throw new Error(
            'Must specify the target (container or inner) for which to load extensions info.'
        );
    }

    try {
        const smarteditscripts = require('@smartedit/scripts');
        const fileContent = await readFile(smarteditscripts.smarteditExtensionsPath);
        const extensionsConfiguration = JSON.parse(fileContent).extensions;

        const extensionsInfo = {
            extensionsToLoad: [],
            containerAngularAppsToLoad: [],
            innerAngularAppsToLoad: []
        };

        extensionsConfiguration.forEach((extension) => {
            if (extension.type === target) {
                extensionsInfo.extensionsToLoad.push(extension.name);
            }

            if (extension.type === 'container' && extension.angularApp) {
                extensionsInfo.containerAngularAppsToLoad.push(extension.angularApp);
            }

            if (extension.type === 'inner' && extension.angularApp) {
                extensionsInfo.innerAngularAppsToLoad.push(extension.angularApp);
            }
        });

        await createMasterIndexFile(extensionsInfo.extensionsToLoad, target);

        return extensionsInfo;
    } catch (ex) {
        throw new Error(`Cannot load extensions information.`, ex);
    }
};

module.exports = {
    loadExtensions
};
