/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { resolve } = require('path');

const base = require('smartedit-build/config/webpack/shared/webpack.bare.config');

const {
    compose,
    webpack: { alias, karma }
} = require('smartedit-build/builders');

module.exports = compose(
    karma(),
    alias('smarteditcommons', resolve('src')),
)(base);

