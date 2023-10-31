/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
module.exports = {
    // Webpack utils
    devtool: require('./devtool'),
    plugin: require('./plugin'),
    rule: require('./rule'),
    output: require('./output'),
    addModule: require('./addModule'),
    mode: require('./mode'),
    external: require('./external'),
    entry: require('./entry'),
    multiEntry: require('./multiEntry'),
    alias: require('./alias'),

    // Loaders
    tsLoader: require('./custom/ts'),
    sassLoader: require('./custom/sass'),
    coverageLoader: require('./custom/coveragePlugin'),

    // Plugins
    karmaErrorsPlugin: require('./custom/karmaErrorsPlugin'),
    contextReplacementPlugin: require('./custom/contextReplacementPlugin'),

    // Presets
    minify: require('./custom/minify'),
    karma: require('./custom/karma'),
    dllPlugins: require('./custom/dllPlugins'),

    // Environments
    prod: require('./custom/prod'),
    dev: require('./custom/dev')
};
