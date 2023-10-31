/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { resolve } = require('path');

const group = require('../../compose');
const alias = require('../alias');

const bundlePaths = require('../../../bundlePaths');
const plugin = require('../plugin');

/**
 * @ngdoc service
 * @name ConfigurationBuilder.service:webpack.dllPlugins
 * @description
 * A preset group of builders for configuring a webpack config to consume smarteditcommons library and vendor chunk.
 *
 * @returns {function(config)} A builder for a webpack configuration object.
 */
module.exports = () =>
    group(
        alias('smarteditcommons', resolve(bundlePaths.external.generated.commons.lib)),
        plugin({
            apply: (compiler) => {
                const options = {
                    context: require('path').resolve('/'),
                    manifest:
                        'smartedit-build/lib/smarteditcommons/dist/smarteditcommons.manifest.json' // hard-coded for backward compatibility (generateWebpackConfig)
                };
                return new (require('webpack')).DllReferencePlugin(options).apply(compiler);
            }
        }),
        plugin({
            apply: (compiler) => {
                return new (require('webpack')).DllReferencePlugin({
                    context: require('path').resolve('/'),
                    manifest: 'smartedit-build/lib/vendor/dist/vendor.dll.manifest.json' // hard-coded for backward compatibility (generateWebpackConfig)
                }).apply(compiler);
            }
        })
    );
