/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

const _ = require('lodash');
const fs = require('fs');
const util = require('util');

const readFile = util.promisify(fs.readFile);

export interface SeTypeScriptConfiguration {
    config: string;
    entry: string;
    dist: string;
}

/**
 * This interface represents the rules used to rewrite URLs in SASS or plain CSS stylesheets.
 */
export interface UrlToRewrite {
    /**
     * The pattern used to match the URLs in the stylesheets that need to be rewritten.
     */
    urlMatcher: string;

    /**
     * The path (relative from the library being compiled) to the location where the assets referenced in the URLs
     * are located.
     */
    assetsLocation: string;
}

/**
 * This interface represents the options used to configure the styles plugin.
 */
export interface StyleConfiguration {
    /**
     * Optional parameter. It represents the path to a SASS file that contains styles
     * to share with all other SASS files.
     * Note: This path is relative from the root of the library being packaged.
     */
    global?: string;

    /**
     * Optional parameter. An array containing rules to rewrite URLs in SASS or plain CSS stylesheets.
     */
    urlsToRewrite?: UrlToRewrite[];
}

export interface NgTemplateConfiguration {
    moduleName: string;
    includePathInName: boolean;
    files: string | string[];
}

export interface LegacyConfiguration {
    js: string | string[];
    ngTemplates: NgTemplateConfiguration;
}

export interface InstrumentationConfiguration {
    skipInstrumentation: boolean;
}

export interface SeAppConfiguration {
    name: string;
    typescript: SeTypeScriptConfiguration;
    style?: StyleConfiguration;
    legacy?: LegacyConfiguration;
    instrumentation?: InstrumentationConfiguration;
    dependencies: string[];
}

const DEFAULT_OPTIONS_FILE = './smartedit.json';
const DEFAULT_PACKAGE_JSON_FILE = './package.json';

const DEFAULT_CONFIGURATION: SeAppConfiguration = {
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

const parseConfiguration = (options: any, packageJson: any): SeAppConfiguration => {
    const config: any = {};

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

    return <SeAppConfiguration>config;
};

const readConfigurationFile = async (
    filePath: string,
    defaultPath: string,
    isRequired: boolean
) => {
    try {
        if (!filePath) {
            filePath = defaultPath;
        }

        const fileContent = await readFile(filePath);
        return JSON.parse(fileContent);
    } catch {
        if (isRequired) {
            throw new Error(`Cannot read configuration file ${filePath}.`);
        } else {
            return {};
        }
    }
};

export const loadConfiguration = async (
    optionsFilePath: string,
    packageJsonFilePath: string
): Promise<SeAppConfiguration> => {
    const [optionsFile, packageJson] = await Promise.all([
        readConfigurationFile(optionsFilePath, DEFAULT_OPTIONS_FILE, false),
        readConfigurationFile(packageJsonFilePath, DEFAULT_PACKAGE_JSON_FILE, true)
    ]);

    return parseConfiguration(optionsFile, packageJson);
};
