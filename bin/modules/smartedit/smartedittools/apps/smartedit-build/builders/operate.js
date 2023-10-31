/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const compose = require('./compose');
const { keysConfigLoop } = require('./utils');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:operate
 * @description
 * Applies builders to a nested object.
 *
 * @param {string} keys A path of keys separated by a period.
 * @param {function(config)} builders List of builders to perform inside the nested object.
 * @returns {function(config)} A builder for a configuration object.
 */
module.exports = (keys, ...builders) => (config) => {
    return keysConfigLoop(keys, config, (current, key) => {
        current[key] = compose(...builders)(current[key]);
    });
};
