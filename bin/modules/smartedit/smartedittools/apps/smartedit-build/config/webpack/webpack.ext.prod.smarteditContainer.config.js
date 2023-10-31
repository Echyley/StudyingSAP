/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { resolve } = require('path');

const base = require('./shared/webpack.base.config');

const {
    compose,
    webpack: { tsLoader, sassLoader, prod }
} = require('../../builders');

const bundlePaths = require('../../bundlePaths');

module.exports = compose(
    prod(),
    sassLoader(),
    tsLoader(resolve(bundlePaths.external.generated.tsconfig.prodSmarteditContainer))
)(base);
