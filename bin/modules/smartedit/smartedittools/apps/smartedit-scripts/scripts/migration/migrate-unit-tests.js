/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
'use strict';

const path = require('path');
const fs = require('fs');
const afs = fs.promises;
const smarteditUtils = require(path.join('..', 'utils.js'));

// Variables
const args = process.argv.slice(2);
const getArgByName = (argName) => {
    const arg = args.find((arg) => arg.indexOf(`-${argName}=`) === 0);
    return arg && arg.substr(arg.indexOf('=') + 1);
};
const EXTENSION_NAME = getArgByName('extName') || null;
const EXTENSION_PATH = getArgByName('extPath') || null;
const EXTENSION_APPS = getArgByName('extApps') ? JSON.parse(getArgByName('extApps')) : [];
const UNIT_TESTS_FOLDER = getArgByName('unitTestsFolder') || null;
const BASE_TEMPLATES_PATH = getArgByName('baseTemplatesPath')
    ? JSON.parse(getArgByName('baseTemplatesPath'))
    : null;
if (!EXTENSION_NAME || !EXTENSION_PATH) {
    console.error('Error: -extName and -extPath must be defined !');
    console.error(
        'Example: migrate-unit-tests.js -extPath=path/to/my/extension -extName=myExtension -extApps=app1,app2 -unitTestsFolderpath/to/tests -baseTemplatesPath=path/to/base/templates'
    );
    process.exit(1);
}

console.log(`EXTENSION_NAME=${EXTENSION_NAME}`);
console.log(`EXTENSION_PATH=${EXTENSION_PATH}`);
console.log(`UNIT_TESTS_FOLDER=${UNIT_TESTS_FOLDER}`);
console.log(`BASE_TEMPLATES_PATH=${BASE_TEMPLATES_PATH}`);
console.log(`EXTENSION_APPS=`);
console.log(EXTENSION_APPS);

const MIGRATED_APP_PATHS = 'apps';

const KARMA_CONFIG_BASE_PATH = 'smartedit-build/config/karma';
const KARMA_CONFIG_PATHS = {
    INNER: `${KARMA_CONFIG_BASE_PATH}/karma.ext.smartedit.conf`,
    OUTER: `${KARMA_CONFIG_BASE_PATH}/karma.ext.smarteditContainer.conf`,
    COMMONS: `${KARMA_CONFIG_BASE_PATH}/shared/karma.base.conf`
};

const WEBPACK_CONFIG_BASE_PATH = 'smartedit-build/config/webpack';
const WEBPACK_CONFIG_PATHS = {
    INNER: `${WEBPACK_CONFIG_BASE_PATH}/webpack.ext.karma.smartedit.config.js`,
    OUTER: `${WEBPACK_CONFIG_BASE_PATH}/webpack.ext.karma.smartedit.config.js`,
    COMMONS: `${WEBPACK_CONFIG_BASE_PATH}/shared/webpack.bare.config`
};

const getLegacyUnitTestType = (folderName) => {
    folderName = folderName.toLowerCase();
    if (folderName.includes('container')) {
        return 'OUTER';
    } else if (folderName.includes('commons')) {
        return 'COMMONS';
    } else if (folderName.includes(EXTENSION_NAME.toLowerCase())) {
        return 'INNER';
    }
    return null;
};

const migrateUnitTests = async () => {
    const legacyUnitTestsEntries = await afs.readdir(UNIT_TESTS_FOLDER, {
        withFileTypes: true
    });
    for (const entry of legacyUnitTestsEntries) {
        if (entry.isDirectory()) {
            const type = getLegacyUnitTestType(entry.name);
            if (type !== null) {
                console.log(`Found unit test entry - name:${entry.name}, type:${type}`);
                const unitTestPath = path.join(UNIT_TESTS_FOLDER, entry.name, 'unit');
                if (fs.existsSync(unitTestPath)) {
                    const appBasePath = path.join(MIGRATED_APP_PATHS, EXTENSION_APPS[type]);
                    const unitTestDestinationPath = path.join(appBasePath, 'tests');
                    console.log(
                        `Copying legacy unit tests from ${unitTestPath} to ${unitTestDestinationPath}`
                    );
                    await smarteditUtils.copyDir(unitTestPath, unitTestDestinationPath);
                    await createMetadataFiles(type, appBasePath);
                } else {
                    console.error('Error: path does not exist:', unitTestPath);
                    process.exit(1);
                }
            }
        }
    }
};

const getTsConfigJsonPropertyByType = (type, property) => {
    return {
        INNER: {
            paths: {
                [`${EXTENSION_NAME}/*`]: ['src/*'],
                [EXTENSION_NAME]: ['src']
            }
        },
        OUTER: {
            paths: {
                [`${EXTENSION_NAME}container/*`]: ['src/*'],
                [`${EXTENSION_NAME}container`]: ['src']
            }
        },
        COMMONS: {
            paths: {
                [EXTENSION_APPS.COMMONS]: ['src'],
                [`${EXTENSION_APPS.COMMONS}/*`]: ['src/*']
            }
        }
    }[type][property];
};

const getWebpackAliasByType = (type) => {
    return {
        INNER: EXTENSION_NAME,
        OUTER: `${EXTENSION_NAME}container`,
        COMMONS: EXTENSION_APPS.COMMONS
    }[type];
};

const createKarma = async (type, appBasePath) => {
    let karmaConfig = await afs.readFile(BASE_TEMPLATES_PATH.KARMA);
    karmaConfig = String(karmaConfig).replace(/%KARMA_PATH%/g, KARMA_CONFIG_PATHS[type]);
    await afs.writeFile(path.join(appBasePath, 'karma.conf.js'), karmaConfig);
};

const createTsConfigJson = async (type, appBasePath) => {
    const jsonTemplate = JSON.parse(await afs.readFile(BASE_TEMPLATES_PATH.TSCONFIG));
    const tsConfigJson = Object.assign({}, jsonTemplate);
    tsConfigJson.compilerOptions.paths = {
        ...jsonTemplate.compilerOptions.paths,
        ...getTsConfigJsonPropertyByType(type, 'paths')
    };
    await afs.writeFile(
        path.join(appBasePath, 'tsconfig.spec.json'),
        JSON.stringify(tsConfigJson, null, 2)
    );
};

const createWebpackConfigSpec = async (type, appBasePath) => {
    let webpackConfig = await afs.readFile(BASE_TEMPLATES_PATH.WEBPACK);
    webpackConfig = String(webpackConfig)
        .replace(/%WEBPACK_PATH%/g, WEBPACK_CONFIG_PATHS[type])
        .replace(/%WEBPACK_ALIAS%/g, getWebpackAliasByType(type));
    await afs.writeFile(path.join(appBasePath, 'webpack.config.spec'), webpackConfig);
};

const createMetadataFiles = async (type, appBasePath) => {
    await createKarma(type, appBasePath);
    await createTsConfigJson(type, appBasePath);
    await createWebpackConfigSpec(type, appBasePath);
};

(async () => {
    console.log('Migrating Unit tests...');
    await migrateUnitTests();
})();
