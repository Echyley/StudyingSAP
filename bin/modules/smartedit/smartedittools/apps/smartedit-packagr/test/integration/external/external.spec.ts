/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { SeAppConfiguration } from '../../../src/configuration';
import {
    assertResultIsEqualToExpectedOutput,
    getDefaultConfigurationForTest,
    readTestFile,
    runBundler
} from '../utils';

const path = require('path');

describe('External', () => {
    const TEST_BASE_PATH = 'external/fixtures/';
    const SRC_FOLDER = './src';
    const DEFAULT_ENTRY_POINT = 'index.ts';
    const DEFAULT_EXPECTED_FILE = 'expected/index.js';

    const getConfiguration = (fixturesPath: string, entryPoint: string) => {
        const configuration: SeAppConfiguration = getDefaultConfigurationForTest(fixturesPath);
        configuration.typescript.entry = path.join(SRC_FOLDER, entryPoint);

        configuration.dependencies = ['@angular/core'];

        return configuration;
    };

    it('GIVEN relative module is marked as external WHEN compiled THEN it is not added to the bundle', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/third-party-dependency');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, DEFAULT_EXPECTED_FILE);
        assertResultIsEqualToExpectedOutput(bundle.output[0].code, expectedOutput);
    });
});
