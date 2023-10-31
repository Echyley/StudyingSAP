/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { merge } = require('webpack-merge');
const devCommonConfig = require('../webpack.dev.common');
const commonConfigPromise = require('./webpack.common');

const devConfigPromise = commonConfigPromise.then((commonConfig) =>
    merge(commonConfig, devCommonConfig)
);

module.exports = devConfigPromise;
