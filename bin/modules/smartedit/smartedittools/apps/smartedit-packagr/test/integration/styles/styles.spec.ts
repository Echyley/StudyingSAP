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

import { resolvePath } from '../../../src/utils';

const path = require('path');
const fs = require('fs');

describe('Styles', () => {
    const TEST_BASE_PATH = 'styles/fixtures/';
    const SRC_FOLDER = './src';
    const DEFAULT_ENTRY_POINT = 'index.ts';
    const DEFAULT_GLOBAL_STYLE_FILE = 'global.scss';

    const getConfiguration = (
        fixturesPath: string,
        entryPoint: string,
        globalStyleFile?: string
    ) => {
        const configuration: SeAppConfiguration = getDefaultConfigurationForTest(fixturesPath);
        configuration.typescript.entry = path.join(SRC_FOLDER, entryPoint);

        if (globalStyleFile) {
            configuration.style = {
                global: path.join(SRC_FOLDER, globalStyleFile)
            };
        }

        return configuration;
    };

    const getGeneratedStyles = (bundle: any) => {
        const asset = bundle.output[1];
        if (typeof asset.source === 'string') {
            return asset.source;
        } else {
            return String.fromCharCode.apply(null, asset.source);
        }
    };

    const assertFilesExist = (filesToCheck: string[]) => {
        filesToCheck.forEach((file: string) => {
            const filePath = resolvePath(path.join('./dist/assets/', file));
            expect(fs.existsSync(filePath)).toBe(true);
        });
    };

    const assertFilesMissing = (filesToCheck: string[]) => {
        filesToCheck.forEach((file: string) => {
            const filePath = resolvePath(path.join('./dist/assets/', file));
            expect(fs.existsSync(filePath)).toBe(false);
        });
    };

    it('GIVEN global sass style AND no other sass is imported WHEN bundle is created THEN it is not compiled', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/global-sass');
        const configuration = getConfiguration(
            fixturesPath,
            DEFAULT_ENTRY_POINT,
            DEFAULT_GLOBAL_STYLE_FILE
        );

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
    });

    it('GIVEN global sass has relative imports AND there is another style file WHEN bundle is created THEN they are imported and bundled into css file', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/global-sass-import');
        const configuration = getConfiguration(
            fixturesPath,
            DEFAULT_ENTRY_POINT,
            DEFAULT_GLOBAL_STYLE_FILE
        );

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
    });

    it('GIVEN global sass has tilde imports AND there is another sass file WHEN bundle is created THEN they are imported and bundled into css file', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/global-sass-tilde');
        const configuration = getConfiguration(
            fixturesPath,
            DEFAULT_ENTRY_POINT,
            DEFAULT_GLOBAL_STYLE_FILE
        );

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
    });

    it('GIVEN sass has tilde imports WHEN bundle is created THEN they are imported and bundled into css file', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/sass-tilde');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
    });

    it('GIVEN styles are imported in files WHEN bundle is created THEN they are bundled into css file', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/styles-import');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
    });

    it('GIVEN global style AND styles imported in files WHEN bundle is created THEN they are bundled into css file', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/global-and-styles-import');
        const configuration = getConfiguration(
            fixturesPath,
            DEFAULT_ENTRY_POINT,
            DEFAULT_GLOBAL_STYLE_FILE
        );

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
    });

    it('GIVEN global style AND inner style WHEN bundle is created THEN is inlined', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/global-sass-inline-ref');
        const configuration = getConfiguration(
            fixturesPath,
            DEFAULT_ENTRY_POINT,
            DEFAULT_GLOBAL_STYLE_FILE
        );

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const generatedCode = bundle.output[0].code;
        const expectedInnerStyle = 'styles: [`.main-content{color:#fff}.component{color:#fff}`]';
        const expectedOutput = await readTestFile(fixturesPath, 'expected/index.js');

        expect(generatedCode.includes(expectedInnerStyle)).toBe(true);
        assertResultIsEqualToExpectedOutput(generatedCode, expectedOutput);
    });

    it('GIVEN invalid sass WHEN bundle is created THEN it should return an error', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/invalid-sass');
        const configuration = getConfiguration(
            fixturesPath,
            DEFAULT_ENTRY_POINT,
            DEFAULT_GLOBAL_STYLE_FILE
        );

        // WHEN / THEN
        // follow 'invalid-less', just expect a Rejected promise
        await expectAsync(runBundler(configuration)).toBeRejected();
    });

    it('GIVEN less is imported directly in code WHEN bundle is created THEN it should be bundled', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/less');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
    });

    it('GIVEN less file imports another file from node modules WHEN bundle is created THEN it should be bundled', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/less-node-modules-import');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
    });

    it('GIVEN file imports sass and less WHEN bundle is created THEN they are compiled independently and bundled together', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/sass-and-less');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
    });

    it('GIVEN invalid less WHEN bundle is created THEN it should return an error', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/invalid-less');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);

        // WHEN / THEN
        await expectAsync(runBundler(configuration)).toBeRejected();
    });

    it('GIVEN less has local assets WHEN bundle is created THEN assets are not copied and urls are rewritten to be relative from the dist folder', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/local-assets-less');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
        assertFilesMissing(['img1.png', 'img2.png', 'img3.png']);
    });

    it('GIVEN less references assets from node_modules WHEN bundle is created THEN assets are copied to assets folder and url is automatically rewritten', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/reference-assets-less');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
        assertFilesExist(['img1.png', 'img2.png']);
    });

    it('GIVEN sass references assets and rewrite rules are given WHEN bundle is created THEN matched assets are copied and url is rewritten according to the rules', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/assets-sass');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);
        configuration.style = {
            urlsToRewrite: [
                {
                    urlMatcher: '../../img/*.png',
                    assetsLocation: 'node_modules/example/img/'
                },
                {
                    urlMatcher: '../images/img1.png',
                    assetsLocation: 'images/'
                }
            ]
        };

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
        assertFilesExist(['img1.png', 'img2.png']);
    });

    it('GIVEN css references assets and rewrite rules are given WHEN bundle is created THEN matched assets are copied and url is rewritten according to the rules', async () => {
        // GIVEN
        const fixturesPath = path.join(TEST_BASE_PATH, '/assets-css');
        const configuration = getConfiguration(fixturesPath, DEFAULT_ENTRY_POINT);
        configuration.style = {
            urlsToRewrite: [
                {
                    urlMatcher: '../../img/*.png',
                    assetsLocation: 'node_modules/example/img/'
                },
                {
                    urlMatcher: '../images/img1.png',
                    assetsLocation: 'images/'
                }
            ]
        };

        // WHEN
        const bundle: any = await runBundler(configuration);

        // THEN
        const expectedOutput = await readTestFile(fixturesPath, 'expected/styles.css');
        assertResultIsEqualToExpectedOutput(getGeneratedStyles(bundle), expectedOutput);
        assertFilesExist(['img1.png', 'img2.png']);
    });
});
