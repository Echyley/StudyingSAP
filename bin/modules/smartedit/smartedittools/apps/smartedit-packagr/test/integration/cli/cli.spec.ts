/*
 * Copyright (c) 2021 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';

import { assertResultIsEqualToExpectedOutput, getTestsBasePath, readTestFile } from '../utils';

const cp = require('child_process');
const util = require('util');
const path = require('path');
const fs = require('fs');

const exec = util.promisify(cp.exec);
const readFile = util.promisify(fs.readFile);

describe('CLI', () => {
    const TEST_FOLDER = 'cli';

    const executeCli = async (testPath: string, filePath?: string) => {
        const command = `../../../../bin/index.js`;
        const { error, stderr } = await exec(command, {
            cwd: testPath
        });

        if (error || stderr) {
            throw new Error('Test failed ' + stderr);
        }
    };

    const assertBundleWasWrittenProperly = async (testPath: string) => {
        const jsFilePath = path.join(testPath, 'dist', 'index.js');
        const styleFilePath = path.join(testPath, 'dist', 'styles.css');

        const expectedJsFilePath = path.join(testPath, 'expected', 'index.js');
        const expectedStyleFilePath = path.join(testPath, 'expected', 'styles.css');

        const [jsFile, styleFile, expectedJsFile, expectedStyleFile] = await Promise.all([
            readFile(jsFilePath, 'utf8'),
            readFile(styleFilePath, 'utf8'),
            readFile(expectedJsFilePath, 'utf8'),
            readFile(expectedStyleFilePath, 'utf8')
        ]);

        assertResultIsEqualToExpectedOutput(jsFile, expectedJsFile);
        assertResultIsEqualToExpectedOutput(styleFile, expectedStyleFile);
    };

    it('GIVEN smartedit app with default configuration file WHEN executed THEN it compiles and bundles', async () => {
        // GIVEN
        const testPath = path.join(getTestsBasePath(), TEST_FOLDER, 'default-config-file');

        // WHEN
        await executeCli(testPath);

        // THEN
        await assertBundleWasWrittenProperly(testPath);
    });

    it('GIVEN smartedit add with valid custom configuration WHEN executed THEN it compiles and bundles', async () => {
        // GIVEN
        const testPath = path.join(getTestsBasePath(), TEST_FOLDER, 'custom-config');
    
        // WHEN
        await executeCli(testPath);
    
        // THEN
        await assertBundleWasWrittenProperly(testPath);
    });
});
