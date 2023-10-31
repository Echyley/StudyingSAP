/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';

import {
    getDefaultConfigurationForTest,
    assertResultIsEqualToExpectedOutput,
    readTestFile,
    runBundler
} from '../utils';
import { SeAppConfiguration } from '../../../src/configuration';

describe('Angular', () => {
    let configuration: SeAppConfiguration;

    const FIXTURES_PATH = 'angular/fixtures/angular';

    beforeEach(() => {
        configuration = getDefaultConfigurationForTest(FIXTURES_PATH);
    });

    it('GIVEN component with no template or style WHEN bundler is ran THEN it is bundled', async () => {
        // GIVEN
        configuration.typescript.entry = './src/test.component.ts';
        configuration.dependencies = ['@angular/core'];

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(FIXTURES_PATH, 'expected/test.component.js');
        assertResultIsEqualToExpectedOutput(bundle.output[0].code, expectedOutput);
    });

    it('GIVEN component has inline template THEN it is bundled without minification', async () => {
        // GIVEN
        configuration.typescript.entry = './src/test-imports.component.ts';
        configuration.dependencies = ['@angular/core'];

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const unminifiedCode =
            '\n        <div>\n            This is an inline example\n        </div>\n    ';
        let expectedOutput = await readTestFile(
            FIXTURES_PATH,
            'expected/test-imports.component.js'
        );

        let actualOutput = bundle.output[0].code;
        expect(actualOutput.includes(unminifiedCode)).toBe(true);
        assertResultIsEqualToExpectedOutput(actualOutput, expectedOutput);
    });

    it('GIVEN component has URL template WHEN bundle is created THEN template is bundled and minified', async () => {
        // GIVEN
        configuration.typescript.entry = './src/test-url-imports.component.ts';
        configuration.dependencies = ['@angular/core'];

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const minifiedCode = '<div>This is a test</div>';
        const expectedOutput = await readTestFile(
            FIXTURES_PATH,
            'expected/test-url-imports.component.js'
        );

        let actualOutput = bundle.output[0].code;
        expect(actualOutput.includes(minifiedCode)).toBe(true);
        assertResultIsEqualToExpectedOutput(actualOutput, expectedOutput);
    });
});
