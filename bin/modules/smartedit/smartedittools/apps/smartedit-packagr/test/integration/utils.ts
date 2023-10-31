/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { SeAppConfiguration } from '../../src/configuration';
import { generateBundle, getConfiguredPlugins } from '../../src/bundler';
import { setBasePath } from '../../src/utils';

const path = require('path');
const fs = require('fs');
const util = require('util');

const readFile = util.promisify(fs.readFile);

const DEFAULT_BASE_PATH = 'test/integration/';

export const getTestsBasePath = () => {
    return DEFAULT_BASE_PATH;
};

export const getDefaultConfigurationForTest = (testPath: string) => {
    const testBasePath = path.join(DEFAULT_BASE_PATH, testPath);
    setBasePath(testBasePath);

    const defaultConfiguration: SeAppConfiguration = {
        name: 'some name',
        typescript: {
            config: '../../../tsconfig.base.json',
            entry: '',
            dist: './dist/index.js'
        },
        dependencies: []
    };

    return defaultConfiguration;
};

export const runBundler = async (config: SeAppConfiguration) => {
    const plugins = await getConfiguredPlugins(config);
    return await generateBundle(config, plugins, true);
};

export const readTestFile = async (basePath: string, fileName: string) => {
    const filePath = path.join(DEFAULT_BASE_PATH, basePath, fileName);
    return await readFile(filePath, 'utf8');
};

export const assertResultIsEqualToExpectedOutput = (result: string, expectedOutput: string) => {
    expectedOutput = expectedOutput.replace(/(\r)/g, '');
    result = result.replace(/(\r)/g, '');

    expect(result).toEqual(expectedOutput);
};
