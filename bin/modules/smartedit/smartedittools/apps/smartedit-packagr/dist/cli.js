"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const bundler_1 = require("./bundler");
const configuration_1 = require("./configuration");
const chalk = require('chalk');
const path = require('path');
const parseArgs = require('minimist');
const OPTIONS_FILE = 'options';
const PACKAGE_JSON_FILE = 'package';
const args = parseArgs(process.argv.slice(2));
const optionsFilePath = args[OPTIONS_FILE];
const packageJsonFilePath = args[PACKAGE_JSON_FILE];
const handleError = (err, appName) => {
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
        const appConfiguration = await configuration_1.loadConfiguration(optionsFilePath, packageJsonFilePath);
        appName = appConfiguration.name;
        console.log(`${chalk.white.bold('Starting build for ' + appName)}`);
        console.log(`${chalk.bgYellow.black('Configuration read')} ${chalk.white.bold(appName)}`);
        console.log(`${chalk.bgYellow.black('Loading plugins')} ${chalk.white.bold(appName)}`);
        const plugins = await bundler_1.getConfiguredPlugins(appConfiguration);
        console.log(`${chalk.bgYellow.black('Generating bundle')} ${chalk.white.bold(appName)}`);
        const bundle = await bundler_1.generateBundle(appConfiguration, plugins);
        console.log(`${chalk.bgGreen.black('Bundle created')} ${chalk.white.bold(appName)}`);
        console.log(`- Generated ${bundle.output.length} files`);
        console.log(`- Output folder ${path.parse(appConfiguration.typescript.dist).dir}`);
    }
    catch (err) {
        handleError(err, appName);
        throw err;
    }
};
executeBuild();
