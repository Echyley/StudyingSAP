"use strict";
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.loadConfiguration = void 0;
const _ = require('lodash');
const fs = require('fs');
const util = require('util');
const readFile = util.promisify(fs.readFile);
const DEFAULT_OPTIONS_FILE = './smartedit.json';
const DEFAULT_PACKAGE_JSON_FILE = './package.json';
const DEFAULT_CONFIGURATION = {
    name: '',
    typescript: {
        config: './tsconfig.build.json',
        entry: './src/index.ts',
        dist: './dist/index.js'
    },
    dependencies: [],
    legacy: {
        js: ['src/**/*[!Test].js'],
        ngTemplates: {
            moduleName: 'coretemplates',
            includePathInName: false,
            files: ['src/**/*.html']
        }
    }
};
const parseConfiguration = (options, packageJson) => {
    const config = {};
    // Properties coming from package.json
    if (!packageJson.name) {
        throw new Error('Invalid package.json. It must specify a name.');
    }
    config.name = packageJson.name;
    const dependencies = packageJson.dependencies || {};
    const peerDependencies = packageJson.peerDependencies || {};
    config.dependencies = [...Object.keys(dependencies), ...Object.keys(peerDependencies)];
    // Legacy options are only enabled if they are specified in the optionsFile.
    const defaultConfiguration = _.cloneDeep(DEFAULT_CONFIGURATION);
    if (!options.legacy) {
        delete defaultConfiguration.legacy;
    }
    // Consider default values if none has already been set.
    _.defaultsDeep(config, options, defaultConfiguration);
    return config;
};
const readConfigurationFile = async (filePath, defaultPath, isRequired) => {
    try {
        if (!filePath) {
            filePath = defaultPath;
        }
        const fileContent = await readFile(filePath);
        return JSON.parse(fileContent);
    }
    catch (_a) {
        if (isRequired) {
            throw new Error(`Cannot read configuration file ${filePath}.`);
        }
        else {
            return {};
        }
    }
};
const loadConfiguration = async (optionsFilePath, packageJsonFilePath) => {
    const [optionsFile, packageJson] = await Promise.all([
        readConfigurationFile(optionsFilePath, DEFAULT_OPTIONS_FILE, false),
        readConfigurationFile(packageJsonFilePath, DEFAULT_PACKAGE_JSON_FILE, true)
    ]);
    return parseConfiguration(optionsFile, packageJson);
};
exports.loadConfiguration = loadConfiguration;
