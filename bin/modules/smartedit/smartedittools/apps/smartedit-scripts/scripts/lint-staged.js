/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
'use strict';

/**
 * Script to run linter on staged files.
 * Used in git pre-commit hook. It can also be run separately with "npm run lint-staged".
 *
 * 1. Takes staged files.
 * 2. For each file, based on its path find the root app:
 * 2.1 Check for "lint:base" script in package.json.
 * 2.2 Check for tsconfig.json.
 * 2.3 If "include" is not defined, fallback to tsconfig.build.json.
 * 3. Exclude files that do not match tsconfig "include" wildcard.
 * 4. Create a configuration for each app that consists of tsconfig.json and the files intended for linting.
 * For example:
 * ```
 * {
 *     'smartedit/apps/smartedit-container/': {
 *         tsconfig: 'smartedit/apps/smartedit-container/tsconfig.json',
 *         files: ['src/CustomHandlingStrategy.ts', 'src/smarteditcontainer.ts']
 *     }
 * }
 * ```
 * Files are validated against the given tsconfig.json whether they match "include" wildcard.
 * Based on the above configuration, for smartedit-container app, "src/CustomHandlingStrategy.ts" and "src/smarteditcontainer.ts" will be linted (handled by step 5.).
 *
 * 5. Generate and run commands that will run npm script "lint:base" on each app. The files to be linted are passed as an argument.
 *
 * Usage:
 * npm run lint-staged
 */
const os = require('os');
const path = require('path');
const { promisify } = require('util');
const exec = promisify(require('child_process').exec);
const glob = require('glob');
const fs = require('fs');
const chalk = require('chalk');

const SMARTEDITMODULE = 'smartedit-module';
const SMARTEDITMODULE_PATH = getSmarteditModulePath();

(async () => {
    console.log(chalk.white.bold('Starting "lint-staged"\n'));

    const stagedFiles = await getStagedFiles();
    const configFilesMap = createConfigFilesMap(stagedFiles);
    if (Object.keys(configFilesMap).length === 0) {
        console.log(chalk.yellow.bold('\nNo files found for linting'));
        process.exit(0);
    }

    printStagedForLint(configFilesMap);

    try {
        await generateAndRunCommands(configFilesMap);
    } catch (error) {
        console.error(chalk.red.bold('Lint failed'));
        process.exit(1);
    }
    console.log(chalk.green.bold('"lint-staged" completed successfully'));
})();

function getSmarteditModulePath() {
    const cwd = process.cwd();
    const idx = cwd.indexOf(SMARTEDITMODULE);
    const end = idx + SMARTEDITMODULE.length;

    return cwd.substring(0, end);
}

/** Returns an array of staged files. */
async function getStagedFiles() {
    const gitDiffCmd =
        'git diff --name-only --cached --diff-filter=d | grep "\\.js$\\|\\.ts$" || true';
    console.log(`Running command: "${gitDiffCmd}"`);

    try {
        const { error, stdout: diff, stderr } = await exec(gitDiffCmd);
        if (error || stderr) {
            throw stderr;
        }
        // git command prints string with files names that are separated with line feed character.
        // That adds one additional line at the end.
        // To go get an array with expected file names we need to remove the last empty element.
        const files = diff.split(os.EOL);
        files.pop();

        // Print staged files
        return files.filter((file) => {
            console.log(file);
            if (file.match(/^ysmarteditmodule\//)) {
                printExcludingFromLinting('Files in ysmarteditmodule cannot be linted.');
                return false;
            }
            return true;
        });
    } catch (error) {
        console.error(error);
        process.exit(1);
    }
    return true;
}

/**
 * See file description for an example.
 */
function createConfigFilesMap(stagedFiles) {
    const configFilesMap = {};
    for (const file of stagedFiles) {
        console.log('\nChecking file:', file);
        const isInsideAppsDir = file.includes(path.normalize('/apps/'));
        if (!isInsideAppsDir) {
            printExcludingFromLinting('File is not inside "apps" directory');
        }

        const configurationPaths = extractConfigurationPaths(file);
        const configurationError = checkConfigurationError(configurationPaths);
        if (configurationError) {
            printExcludingFromLinting(configurationError);
        }
        if (!isInsideAppsDir || configurationError) {
            continue;
        }
        const { appRootPath, fileToLintPath } = configurationPaths;
        if (!configFilesMap[appRootPath]) {
            configFilesMap[appRootPath] = [];
        }
        configFilesMap[appRootPath].push(fileToLintPath);
    }
    return configFilesMap;
}

/**
 *
 * Extracts configurations paths for app root, tsconfig from a given staged file.
 *
 * Example:
 * file => smartedit/apps/smartedit/src/components/contextualMenuItem/ContextualMenuItemComponent.ts
 *
 * appRootPath => smartedittools/apps/smartedit/
 * fileToLintPath => src/components/contextualMenuItem/ContextualMenuItemComponent.ts
 * tsConfigPath => smartedittools/apps/smartedit/tsconfig.json
 * tsconfigBuildPath => smartedittools/apps/smartedit/tsconfig.build.json
 * packageJsonPath => smartedittools/apps/smartedit/package.json
 */
function extractConfigurationPaths(file) {
    console.log('Extracting configuration paths', file);
    const filePathRegex = /(.+(?:\/|\\)apps(?:\/|\\).+?(?:\/|\\))(.+$)/; // OS compatible

    const filePathMatch = file.match(filePathRegex);
    const appRootPath = filePathMatch[1];
    const fileToLintPath = filePathMatch[2];
    const tsconfigPath = `${appRootPath}tsconfig.json`;
    const tsconfigBuildPath = `${appRootPath}tsconfig.build.json`;
    const packageJsonPath = `${appRootPath}package.json`;

    return {
        appRootPath,
        fileToLintPath,
        tsconfigPath,
        tsconfigBuildPath,
        packageJsonPath
    };
}

/**
 * Check a configuration whether the file can be linted.
 * If it cannot, a reason text is returned.
 * Otherwise returns null.
 */
function checkConfigurationError(configurationPaths) {
    const {
        fileToLintPath,
        appRootPath,
        tsconfigPath,
        tsconfigBuildPath,
        packageJsonPath
    } = configurationPaths;
    // lint:base script must be present
    const packageJson = readFile(getRelativePath(packageJsonPath));
    if (!packageJson.scripts['lint:base']) {
        return `lint:base script not found in: ${packageJsonPath}`;
    }

    let resolvedTsConfigInclude = resolveTsConfigInclude(tsconfigPath, tsconfigBuildPath);
    if (resolvedTsConfigInclude === undefined) {
        return `Neither tsconfig.json nor tsconfig.build.json found: ${tsconfigPath}`;
    } else if (resolvedTsConfigInclude === null) {
        console.log(`"include" not found. Fallback to default ['**/*'].`);
        resolvedTsConfigInclude = ['**/*'];
    }
    const includeWildCards = resolvedTsConfigInclude;
    console.log(`Filtering based on tsconfig.json "include": "${includeWildCards}"`);

    // Validate file against glob pattern.
    // If the file was matched by the glob pattern, do lint.
    // Note: It does not match the tsconfig.json "exclude" pattern because we assume that these are specified in .gitignore and therefore should not be staged.
    const includePattern = getGlobPattern(includeWildCards);
    const globMatches = glob.sync(includePattern, { cwd: getRelativePath(appRootPath) });
    if (!globMatches.includes(fileToLintPath)) {
        return `Reason: File does not match the tsconfig.json "includes" pattern: "${includeWildCards}"`;
    }
    return null;
}

function resolveTsConfigInclude(tsconfigPath, tsconfigBuildPath) {
    let include;
    // tsconfig.json must be present to check if the file matches "include" wildcard
    const tsconfig = readFile(getRelativePath(tsconfigPath));
    if (!tsconfig) {
        return null;
    }
    include = tsconfig.include;

    // tsconfig.json can extend tsconfig.build.json. If there is no "include" in tsconfig.json, get it from tsconfig.build.json.
    if (!include) {
        const tsconfigBuild = readFile(getRelativePath(tsconfigBuildPath));
        if (!tsconfigBuild) {
            return null;
        }
        include = tsconfigBuild.include;
    }
    return include || null;
}

/**
 * Returns a content of tsconfig.json as an object.
 */
function readFile(path) {
    try {
        const exists = fileExists();
        if (!exists) {
            process.exit(1);
        }

        const content = fs.readFileSync(path, { encoding: 'utf8' });
        return JSON.parse(content);
    } catch (error) {
        console.error(error);
        process.exit(1);
    }

    function fileExists() {
        try {
            fs.accessSync(path);
            return true;
        } catch (error) {
            return false;
        }
    }
    return true;
}

/**
 * Returns relative path to "smartedit-module" directory.
 */
function getRelativePath(targetFilePath) {
    return path.join(SMARTEDITMODULE_PATH, targetFilePath);
}

function getGlobPattern(tsconfigInclude) {
    let includePattern = tsconfigInclude.join(',');
    if (tsconfigInclude.length > 1) {
        includePattern = `{${includePattern}}`;
    }
    return includePattern;
}

function generateAndRunCommands(configFilesMap) {
    return runCommands(generateCommands(configFilesMap));
}

/**
 * Returns an array of linting commands
 */
function generateCommands(configFilesMap) {
    return Object.keys(configFilesMap).map((appRootPath) => {
        const files = configFilesMap[appRootPath];
        const lintFiles = files.join(' ');
        const appRootPathAbs = path.join(SMARTEDITMODULE_PATH, appRootPath);

        return `npm run --prefix ${appRootPathAbs} lint:base -- ${lintFiles} --quiet`;
    });
}

/** Runs lint commands. */
async function runCommands(commands) {
    console.log('\nRunning lint commands...');
    for (const cmd of commands) {
        console.log(`Running: ${cmd}`);
        try {
            const { error, stdout, stderr } = await exec(cmd);
            if (error || stderr) {
                throw stderr;
            }

            console.log(`Result: ${stdout}`);
        } catch (error) {
            console.log(chalk.red('Command failed'));
            console.error(error.stdout || error);
            process.exit(1);
        }
    }
}

function printStagedForLint(configFilesMap) {
    const lintFiles = [];
    for (const appRootPath in configFilesMap) {
        if (!configFilesMap.hasOwnProperty(appRootPath)) {
            continue;
        }
        const files = configFilesMap[appRootPath];
        const filesFullPath = files.map((file) => `${appRootPath}${file}`);

        lintFiles.push.apply(lintFiles, filesFullPath);
    }
    console.log('\nStaged files that will be linted:');
    lintFiles.forEach((file) => console.log(file));
}

function printExcludingFromLinting(reason) {
    console.log('Excluding from linting...');
    console.log(`Reason: ${reason}`);
}
