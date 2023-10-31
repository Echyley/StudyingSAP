/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
'use strict';

/**
 * Script to perform the migration of a legacy smartedit-dependent extension to the 21.05 librarification version.
 *
 * Supported versions that can be migrated are:
 * 20.05
 *
 * Usage:
 * node migrate-smartedit.js -extName=smartedit -extPath=path/to/my/extension -gitBranch=my-git-branch
 */

const path = require('path');
const fs = require('fs');
const os = require('os');
const { promisify } = require('util');
const exec = promisify(require('child_process').exec);
const afs = fs.promises;
const smarteditUtils = require(path.join(__dirname, 'utils.js'));

// Variables
const args = process.argv.slice(2);
const getArgByName = (argName) => {
    const arg = args.find((arg) => arg.indexOf(`-${argName}=`) === 0);
    return arg && arg.substr(arg.indexOf('=') + 1);
};
const EXTENSION_NAME = getArgByName('extName') || null;
const EXTENSION_PATH = getArgByName('extPath') || null;
if (!EXTENSION_NAME || !EXTENSION_PATH) {
    console.error('Error: -extName and -extPath must be defined !');
    console.error(
        'Example: migrate-smartedit.js -extPath=path/to/my/extension -extName=myExtension'
    );
    process.exit(1);
}
// git branch is optional. If it's defined it will be used to perform the migration.
const GIT_BRANCH = getArgByName('gitBranch') || null;

console.log(`EXTENSION_NAME=${EXTENSION_NAME}`);
console.log(`EXTENSION_PATH=${EXTENSION_PATH}`);
console.log(`GIT_BRANCH=${GIT_BRANCH}`);

// Legacy application paths
const LEGACY_PATHS = {
    WEB_FEATURES: path.join('web', 'features'),
    UNIT_TESTS: path.join('jsTests', 'tests')
};
const LEGACY_FILES = [LEGACY_PATHS.WEB_FEATURES, 'buildcallbacks.xml', LEGACY_PATHS.UNIT_TESTS];
const PACKAGE_JSON = 'package.json';
const LEGACY_SYMLINKS = [PACKAGE_JSON, 'node_modules' /*, 'smartedit-build'*/];
const BACKUP_FOLDER_PATH = path.join('backup', new Date().toISOString().slice(0, 19));

// Migrated application paths
const MIGRATED_APP_PATHS = 'apps';
const BASE_TEMPLATES_PATH = path.join(__dirname, 'templates');
const WEB_APP_TEMPLATES = {
    PACKAGE: path.join(BASE_TEMPLATES_PATH, PACKAGE_JSON),
    SMARTEDIT: path.join(BASE_TEMPLATES_PATH, 'smartedit.json'),
    TSCONFIG: path.join(BASE_TEMPLATES_PATH, 'tsconfig.json'),
    TSCONFIG_BUILD: path.join(BASE_TEMPLATES_PATH, 'tsconfig.build.json'),
    SPEC: {
        KARMA: path.join(BASE_TEMPLATES_PATH, 'karma.conf.js'),
        TSCONFIG: path.join(BASE_TEMPLATES_PATH, 'tsconfig.spec.json'),
        WEBPACK: path.join(BASE_TEMPLATES_PATH, 'webpack.config.spec')
    }
};

let LEGACY_WEB_FEATURES = {
    INNER: null,
    OUTER: null,
    COMMONS: null
};

const SMARTEDIT_DEFAULT_VERSION = '0.0.1';
const GIT_IGNORE_ENTRIES = [
    'temp',
    'apps/**/dist',
    '/package.json',
    'coverage',
    'junit',
    '/backup'
];

const useGitBranch = async (branchName) => {
    console.log(`Using git branch: ${branchName}`);
    try {
        await exec(`git show-ref refs/heads/${GIT_BRANCH}`);
        await exec(`git checkout ${GIT_BRANCH}`);
    } catch {
        console.log(`Creating git branch ${GIT_BRANCH}...`);
        await exec(`git checkout -b ${GIT_BRANCH}`);
    }
};

/**
 * Sanity check: verify that the extension contains files and folders structure as expected for a successful migration to take place.
 */
const executeLegacySanityCheck = async () => {
    const allFilesExist =
        LEGACY_FILES.filter((file) => fs.existsSync(file)).length === LEGACY_FILES.length;
    if (!allFilesExist) {
        const requiredFiles = LEGACY_FILES.filter((file) => !fs.existsSync(file));
        throw new Error(
            'Error: The following files/folders must exist before doing the migration: ' +
                requiredFiles
        );
    }
};

const checkLegacyWebFeatureFoldersStructure = async () => {
    const unknownTypes = Object.keys(LEGACY_WEB_FEATURES).filter(
        (legacyWebFeature) => LEGACY_WEB_FEATURES[legacyWebFeature] === null
    );
    if (unknownTypes.length) {
        console.warn(
            '**** WARNING: Some folder(s) were not properly identifiable when scanning the extension web/features folder ****'
        );
        console.warn(
            'You need to rename the folder(s) in web/features/* to comply with the following naming convention:'
        );

        // TODO: improvement: use interactive mode for the user to have a chance to enter the folder name.
        unknownTypes.forEach((unknownType) => {
            const warningMessage = `* The expected naming convention for the ${unknownType} layer is: %pattern%`;
            console.warn(
                {
                    INNER: warningMessage.replace(/%pattern%/, `web/features/${EXTENSION_NAME}`),
                    OUTER: warningMessage.replace(
                        /%pattern%/,
                        `web/features/${EXTENSION_NAME}container`
                    ),
                    COMMONS: warningMessage.replace(
                        /%pattern%/,
                        `web/features/${EXTENSION_NAME}commons`
                    )
                }[unknownType]
            );
        });

        throw new Error('Error while scanning legacy web/features folder');
    }
};

const getLegacyWebFeatures = async () => {
    const legacyWebFeaturesEntries = await afs.readdir(LEGACY_PATHS.WEB_FEATURES, {
        withFileTypes: true
    });
    for (const legacyWebFeatureEntry of legacyWebFeaturesEntries) {
        if (legacyWebFeatureEntry.isDirectory()) {
            const type = getLegacyWebFeatureType(legacyWebFeatureEntry.name);
            if (type !== null) {
                if (LEGACY_WEB_FEATURES[type]) {
                    throw new Error(
                        `Duplicate entry found for ${type} - name: ${legacyWebFeatureEntry.name}. Make sure there is only one matching folder for ${type}.`
                    );
                }
                LEGACY_WEB_FEATURES[type] = legacyWebFeatureEntry.name;
            } else {
                console.warn(
                    `WARNING: Unknown folder found in web/features: "${legacyWebFeatureEntry.name}"`
                );
                console.warn(
                    `WARNING: Copying unknown "${legacyWebFeatureEntry.name}" folder to ${MIGRATED_APP_PATHS}... You may want to verify this after the migration is complete.`
                );
                const migratedLegacyUnknownFolder = path.join(
                    MIGRATED_APP_PATHS,
                    legacyWebFeatureEntry.name
                );
                await smarteditUtils.createFolder(migratedLegacyUnknownFolder);
                await smarteditUtils.copyDir(
                    path.join(LEGACY_PATHS.WEB_FEATURES, legacyWebFeatureEntry.name),
                    migratedLegacyUnknownFolder
                );
            }
        } else {
            console.warn(
                'WARNING: Unknown file found in web/features:',
                legacyWebFeatureEntry.name
            );
        }
    }

    console.log('web/features/* scan result:');
    console.log(LEGACY_WEB_FEATURES);

    return LEGACY_WEB_FEATURES;
};

const getPackageJsonByType = (commonsLibraryName, type, dependencies) => {
    return {
        INNER: {
            name: EXTENSION_NAME,
            description: `${EXTENSION_NAME} (Inner frame) library`,
            dependencies: {
                ...dependencies,
                smartedit: SMARTEDIT_DEFAULT_VERSION,
                [commonsLibraryName]: SMARTEDIT_DEFAULT_VERSION
            }
        },
        OUTER: {
            name: `${EXTENSION_NAME}container`,
            description: `${EXTENSION_NAME} (Outer frame) library`,
            dependencies: {
                ...dependencies,
                smarteditcontainer: SMARTEDIT_DEFAULT_VERSION,
                [commonsLibraryName]: SMARTEDIT_DEFAULT_VERSION
            }
        },
        COMMONS: {
            name: commonsLibraryName,
            description: `${EXTENSION_NAME} Commons library`,
            dependencies
        }
    }[type];
};

const getSmarteditJsonPropertyByType = (commonsLibraryName, type, property) => {
    return {
        INNER: {
            name: EXTENSION_NAME,
            extension: {
                type: 'inner',
                angularApp: EXTENSION_NAME
            },
            moduleName: `${EXTENSION_NAME}Templates`
        },
        OUTER: {
            name: `${EXTENSION_NAME}container`,
            extension: {
                type: 'container',
                angularApp: `${EXTENSION_NAME}Container`
            },
            moduleName: `${EXTENSION_NAME}ContainerTemplates`
        },
        COMMONS: {
            name: commonsLibraryName,
            moduleName: `${EXTENSION_NAME}CommonTemplates`
        }
    }[type][property];
};

const getTsConfigJsonPropertyByType = (commonsLibraryName, type, property) => {
    return {
        INNER: {
            paths: {
                [`${EXTENSION_NAME}/*`]: ['src/*']
            }
        },
        OUTER: {
            paths: {
                [`${EXTENSION_NAME}container/*`]: ['src/*']
            }
        },
        COMMONS: {
            paths: {
                [commonsLibraryName]: ['src'],
                [`${commonsLibraryName}/*`]: ['src/*']
            }
        }
    }[type][property];
};

const backupLegacyExtension = async () => {
    await smarteditUtils.createFolder(BACKUP_FOLDER_PATH);
    for (const legacyFile of LEGACY_FILES) {
        await smarteditUtils.copyDir(legacyFile, path.join(BACKUP_FOLDER_PATH, legacyFile));
    }
    console.log(`Backup completed in: ${BACKUP_FOLDER_PATH}`);
};

const deleteLegacyExtensionFiles = async () => {
    await deleteFolderRecursive(LEGACY_PATHS.WEB_FEATURES);
    await deleteFolderRecursive(LEGACY_PATHS.UNIT_TESTS);
};

const deleteLegacySymlinks = async () => {
    for (const symLink of LEGACY_SYMLINKS) {
        if (fs.existsSync(symLink)) {
            await afs.unlink(symLink);
        }
    }
};

const deleteFolderRecursive = async (dir) => {
    if (fs.existsSync(dir)) {
        for (const entry of await afs.readdir(dir)) {
            const currentPath = path.join(dir, entry);
            if ((await afs.lstat(currentPath)).isDirectory()) {
                await deleteFolderRecursive(currentPath);
            } else {
                await afs.unlink(currentPath);
            }
        }
        await afs.rmdir(dir);
    }
};

/**
 * Update project.properties to add new property to enable smartedittools
 */
const updateProjectProperties = async () => {
    const PROJECT_PROPERTIES = 'project.properties';
    const SMARTEDIT_TOOLS_CONFIG_PROP_PLACEHOLDER = `${EXTENSION_NAME}.smartedittools.config`;
    const projectProps = await afs.readFile(PROJECT_PROPERTIES);
    if (projectProps.indexOf(SMARTEDIT_TOOLS_CONFIG_PROP_PLACEHOLDER) === -1) {
        await afs.writeFile(
            PROJECT_PROPERTIES,
            `${projectProps}${os.EOL}${SMARTEDIT_TOOLS_CONFIG_PROP_PLACEHOLDER}=true`
        );
    }
};

const copyLegacyWebFeaturesToWebApps = async () => {
    for (const webFeatureFolder of Object.values(LEGACY_WEB_FEATURES)) {
        const migratedWebAppFolder = path.join(MIGRATED_APP_PATHS, webFeatureFolder, 'src');
        await smarteditUtils.createFolder(migratedWebAppFolder);
        await smarteditUtils.copyDir(
            path.join(LEGACY_PATHS.WEB_FEATURES, webFeatureFolder),
            migratedWebAppFolder
        );
    }
};

/**
 * Return the legacy web/features/* folder layer type (OUTER, INNER or COMMONS) given it's name.
 * Note: there is no rule nor naming convention that was enforcing the name of folders within web/features/* folder if the extension was created with an old version (or without) ysmarteditmodule, hence we have to find out the folder type automatically.
 *
 * @param {*} folderName
 */
const getLegacyWebFeatureType = (folderName) => {
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

const createWebAppsMetadataFiles = async () => {
    await createWebAppPackageJson();
    await createWebAppSmarteditJson();
    await createTsConfigJson();
};

/**
 * Web app package.json
 */
const createWebAppPackageJson = async () => {
    const jsonTemplate = JSON.parse(await afs.readFile(WEB_APP_TEMPLATES.PACKAGE));
    const getJsonFileDestPath = (type) =>
        path.join(MIGRATED_APP_PATHS, LEGACY_WEB_FEATURES[type], PACKAGE_JSON);
    const commonsLibraryName = LEGACY_WEB_FEATURES.COMMONS;
    for (const layerType of Object.keys(LEGACY_WEB_FEATURES)) {
        const packageJson = {
            ...jsonTemplate,
            ...getPackageJsonByType(commonsLibraryName, layerType, jsonTemplate.dependencies)
        };
        await afs.writeFile(getJsonFileDestPath(layerType), JSON.stringify(packageJson, null, 2));
    }
};

/**
 * Web app smartedit.json
 */
const createWebAppSmarteditJson = async () => {
    const jsonTemplate = JSON.parse(await afs.readFile(WEB_APP_TEMPLATES.SMARTEDIT));
    const getJsonFileDestPath = (type) =>
        path.join(MIGRATED_APP_PATHS, LEGACY_WEB_FEATURES[type], 'smartedit.json');
    const commonsLibraryName = LEGACY_WEB_FEATURES.COMMONS;
    for (const layerType of Object.keys(LEGACY_WEB_FEATURES)) {
        const smarteditJson = {
            ...jsonTemplate,
            name: getSmarteditJsonPropertyByType(commonsLibraryName, layerType, 'name')
        };
        const extension = getSmarteditJsonPropertyByType(
            commonsLibraryName,
            layerType,
            'extension'
        );
        if (extension) {
            smarteditJson.extension = extension;
        }
        smarteditJson.legacy.ngTemplates.moduleName = getSmarteditJsonPropertyByType(
            commonsLibraryName,
            layerType,
            'moduleName'
        );
        await afs.writeFile(getJsonFileDestPath(layerType), JSON.stringify(smarteditJson, null, 2));
    }
};

/**
 * Web app tsconfig.json
 */
const createTsConfigJson = async () => {
    const getJsonTemplate = async (tsconfigPath) => JSON.parse(await afs.readFile(tsconfigPath));
    const getJsonFileDestPath = (type, filename) =>
        path.join(MIGRATED_APP_PATHS, LEGACY_WEB_FEATURES[type], filename);
    const commonsLibraryName = LEGACY_WEB_FEATURES.COMMONS;
    for (const layerType of Object.keys(LEGACY_WEB_FEATURES)) {
        // tsconfig.build.json
        const tsConfigBuildJson = await getJsonTemplate(WEB_APP_TEMPLATES.TSCONFIG_BUILD);
        tsConfigBuildJson.compilerOptions.paths = getTsConfigJsonPropertyByType(
            commonsLibraryName,
            layerType,
            'paths'
        );
        await afs.writeFile(
            getJsonFileDestPath(layerType, 'tsconfig.build.json'),
            JSON.stringify(tsConfigBuildJson, null, 2)
        );
        // tsconfig.json
        const tsConfigJson = await getJsonTemplate(WEB_APP_TEMPLATES.TSCONFIG);
        await afs.writeFile(
            getJsonFileDestPath(layerType, 'tsconfig.json'),
            JSON.stringify(tsConfigJson, null, 2)
        );
    }
};

const migrateUnitTests = async () => {
    return new Promise((resolve, reject) => {
        const { fork } = require('child_process');
        const migrateTestsScript = path.join(__dirname, 'migration', 'migrate-unit-tests.js');
        const forkedProcess = fork(migrateTestsScript, [
            `-extName=${EXTENSION_NAME}`,
            `-extPath=${EXTENSION_PATH}`,
            `-extApps=${JSON.stringify(LEGACY_WEB_FEATURES)}`,
            `-unitTestsFolder=${LEGACY_PATHS.UNIT_TESTS}`,
            `-baseTemplatesPath=${JSON.stringify(WEB_APP_TEMPLATES.SPEC)}`
        ]);
        forkedProcess.on('exit', function (code, signal) {
            resolve({ code, signal });
            console.log('Unit tests migration completed successfully.');
        });
        forkedProcess.on('error', (e) => {
            reject(e);
        });
    });
};

const updateBuildCallbacksXml = async () => {
    return new Promise((resolve, reject) => {
        const { fork } = require('child_process');
        const nodeScript = path.join(__dirname, 'migration', 'update-buildcallbacks.js');
        const forkedProcess = fork(nodeScript, [
            `-extName=${EXTENSION_NAME}`,
            `-legacyWebFeatures=${JSON.stringify(LEGACY_WEB_FEATURES)}`
        ]);
        forkedProcess.on('exit', function (code, signal) {
            resolve({ code, signal });
            console.log('buildcallbacks.xml update completed successfully.');
        });
        forkedProcess.on('error', (e) => {
            reject(e);
        });
    });
};

const updateGitIgnore = async () => {
    const GIT_IGNORE_FILENAME = '.gitignore';
    let gitIgnore = fs.existsSync(GIT_IGNORE_FILENAME)
        ? await afs.readFile(GIT_IGNORE_FILENAME)
        : '';
    GIT_IGNORE_ENTRIES.forEach((excludedPattern) => {
        if (!gitIgnore.includes(excludedPattern)) {
            gitIgnore = `${gitIgnore}${os.EOL}${excludedPattern}`;
        }
    });
    await afs.writeFile(GIT_IGNORE_FILENAME, gitIgnore);
};

const migrateLegacyExtension = async () => {
    await updateProjectProperties();
    await updateBuildCallbacksXml();
    await copyLegacyWebFeaturesToWebApps();
    await createWebAppsMetadataFiles();
    await migrateUnitTests();
    await updateGitIgnore();
};

/**
 * Migration in 4 steps:
 * STEP 1: Checking Prerequisites
 * STEP 2: Backup
 * STEP 3: Migrate
 * STEP 4: Delete legacy
 */
(async () => {
    process.chdir(EXTENSION_PATH);

    try {
        console.log('Checking Prerequisites...');
        await executeLegacySanityCheck();
        LEGACY_WEB_FEATURES = await getLegacyWebFeatures();
        await checkLegacyWebFeatureFoldersStructure();

        // Use git (optional)
        GIT_BRANCH && (await useGitBranch(GIT_BRANCH));

        console.log('Backup...');
        await backupLegacyExtension();

        console.log('Migrate...');
        await migrateLegacyExtension();

        console.log('Delete legacy...');
        await deleteLegacySymlinks();
        await deleteLegacyExtensionFiles();

        // Done.
        console.log(`Migration of ${EXTENSION_NAME} was successfull.`);
        console.log(
            'To complete the migration, please refer to this documentation: https://wiki.wdf.sap.corp/wiki/display/CxM/Librarification+-+Upgrading+From+2005+to+2105'
        );
        console.log(
            'You can also find the same guidance in our help portal: https://help.sap.com/viewer/a74589c3a81a4a95bf51d87258c0ab15/2105/en-US/23744a827ef94fefbaf0e407a610ab39.html#loio07c9f828f93a4411b5187c600293235f'
        );
    } catch (e) {
        await deleteFolderRecursive(MIGRATED_APP_PATHS);
        console.error(e);
        process.exit(1);
    }
})();
