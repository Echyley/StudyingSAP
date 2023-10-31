/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { getConfiguredPlugins, generateBundle } from './bundler';
import { loadConfiguration } from './configuration';

const chalk = require('chalk');
const path = require('path');
const parseArgs = require('minimist');

const OPTIONS_FILE = 'options';
const PACKAGE_JSON_FILE = 'package';

const args = parseArgs(process.argv.slice(2));
const optionsFilePath: any = args[OPTIONS_FILE];
const packageJsonFilePath: any = args[PACKAGE_JSON_FILE];

const handleError = (err: any, appName: string) => {
    console.log(`\n${chalk.bgRed.black('Compilation Failed ')} ${chalk.white.bold(appName)}\n`);

    if (!err.code) {
        console.error(err);
        return;
    }

    const plugin = err.plugin ? `-${err.plugin}` : '';
    console.error(`-- ${err.code}${plugin} -- `);
    console.error(err.stack);
};

const executeBuild = async () => {
    let appName = '';
    try {
        const appConfiguration = await loadConfiguration(optionsFilePath, packageJsonFilePath);
        appName = appConfiguration.name;
        console.log(`${chalk.white.bold('Starting build for ' + appName)}`);
        console.log(`${chalk.bgYellow.black('Configuration read')} ${chalk.white.bold(appName)}`);

        console.log(`${chalk.bgYellow.black('Loading plugins')} ${chalk.white.bold(appName)}`);
        const plugins = await getConfiguredPlugins(appConfiguration);

        console.log(`${chalk.bgYellow.black('Generating bundle')} ${chalk.white.bold(appName)}`);
        const bundle = await generateBundle(appConfiguration, plugins);

        console.log(`${chalk.bgGreen.black('Bundle created')} ${chalk.white.bold(appName)}`);
        console.log(`- Generated ${bundle.output.length} files`);
        console.log(`- Output folder ${path.parse(appConfiguration.typescript.dist).dir}`);
    } catch (err) {
        handleError(err, appName);
        throw err;
    }
};

executeBuild();
