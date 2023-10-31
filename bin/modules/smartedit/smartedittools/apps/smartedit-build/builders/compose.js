/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const execute = require('./execute');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:compose
 * @description
 * Receives an array of builders to compose a set builders to build a configuration object.
 *
 * Example:
 *
 * ```
 * const build = compose(
 *    set('hello', false),
 *    add('array', 'Item1'),
 *    plugin(new DLLPlugin)
 * );
 *
 * const config = build();
 * ```
 *
 * // Will result in.
 * ```
 * {
 *      hello: false,
 *      array: ['Item1'],
 *      plugins: [
 *          DLLPlugin {}
 *      ]
 * }
 * ```
 *
 * @param {function(config)} arguments An array of function builders.
 * @returns {function(config)} A builder for a configuration object.
 */
module.exports = (...builders) => {
    return (base) => builders.reduce((config, builder) => execute(builder)(config), base);
};
