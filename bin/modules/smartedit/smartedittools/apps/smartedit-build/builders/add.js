/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { keysConfigLoop } = require('./utils');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:add
 * @description
 * Adds a item to an array according to the nestable keys provided.
 *
 * Example:
 *
 * let config = add('resolve.module', 'Path 1')();
 *
 * Will result in.
 * ```
 * {
 *      resolve: {
 *          module: [
 *              'Path 1'
 *          ],
 *      },
 * }
 * ```
 *
 * @param {string} keys A path of keys separated by a period. The last key being an array to append the value to.
 * @param {*} value A value to add to the last key.
 * @param {boolean} merge Concatenates an arrayed value with the current array.
 * @returns {function(config)} A builder for a configuration object.
 */
module.exports = (keys, value, merge = false) => (config) => {
    return keysConfigLoop(keys, config, (current, key) => {
        current[key] = [...(current[key] || [])];

        if (merge) {
            current[key] = current[key].concat(value);
        } else {
            current[key].push(value);
        }
    });
};
