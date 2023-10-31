/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const compose = require('../compose');
const execute = require('../execute');
const set = require('../set');

const isHeadless = process.argv.indexOf('--browser') === -1;

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:karma.headless
 * @description
 * Configures a Karma configuration based on the value set. If no value is detected, it runs the configuration in
 * non-headless mode if it detects '--browser' in the process arguments, otherwise it runs in headless mode.
 *
 * @param {boolean} headless A boolean indicating to run headless.
 * @returns {function(config)} A builder for a karma configuration object.
 */
module.exports = (headless = isHeadless) =>
    execute(
        !headless &&
            compose(
                set('browsers', ['Chrome']),
                set('singleRun', false)
            )
    );
