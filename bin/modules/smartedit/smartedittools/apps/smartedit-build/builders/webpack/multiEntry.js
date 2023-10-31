/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { basename, relative, resolve } = require('path');

const entry = require('./entry');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.entry
 * @description
 * Sets multiple entries to a webpack config through a provided glob pattern
 *
 * @param {string} root root path
 * @param {string|Array} pattern path or sequence of path segments to be resolved into an absolute path
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = (root, pattern) => {
    const entryMap = {
        generated_dummyE2EModule: resolve('./smartedit-build/test/e2e/dummyE2EModule.ts')
    };
    const paths =
        Array.isArray(pattern) && pattern.length > 0 ? [root, ...pattern] : [root, pattern];

    require('glob')
        .sync(resolve(...paths))
        .forEach((file) => {
            let path = relative(root, file).replace(/\\/g, '\\\\');
            let filename = basename(path);
            path = path.replace(filename, 'generated_' + filename).replace(/\.ts$/, '');
            entryMap[path] = file;
        });
    return entry(entryMap);
};
