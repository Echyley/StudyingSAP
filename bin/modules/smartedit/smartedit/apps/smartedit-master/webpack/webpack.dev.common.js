/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

module.exports = {
    mode: 'development',
    devtool: 'source-map',
    ignoreWarnings: [/Failed to parse source map/],
    module: {
        rules: [
            {
                test: /\.(js|ts)$/,
                enforce: 'pre',
                use: ['source-map-loader']
            }
        ]
    }
};
