/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { merge } = require('webpack-merge');
const prodCommonConfig = require('../webpack.prod.common');
const commonConfigPromise = require('./webpack.common');

const prodConfigPromise = commonConfigPromise.then((commonConfig) =>
    merge(commonConfig, prodCommonConfig)
);

module.exports = prodConfigPromise;
