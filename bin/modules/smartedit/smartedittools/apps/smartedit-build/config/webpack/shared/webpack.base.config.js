/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const base = require('./webpack.bare.config');

const {
    compose,
    webpack: { dllPlugins }
} = require('../../../builders');

module.exports = compose(
    dllPlugins()
)(base);
