/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint esversion: 6 */
module.exports = (function() {
    const bundlePaths = require('../smartedit-build/bundlePaths');
    const paths = {
        bundlePaths
    };

    paths.tsconfig = {
        compodocSmarteditCommons: `${bundlePaths.genPath}/tsconfig.compodoc.smarteditcommons.json`,
        compodocSmartedit: `${bundlePaths.genPath}/tsconfig.compodoc.smartedit.json`,
        compodocSmarteditContainer: `${
            bundlePaths.genPath
        }/tsconfig.compodoc.smarteditcontainer.json`
    };

    paths.compodoc = {
        temp: './compodocs',
        source: './web/features',
        output: {
            general: './jsTarget/compodoc/',
            smarteditCommons: './jsTarget/compodoc/personalizationpromotionssmarteditcommons',
            smartedit: './jsTarget/compodoc/personalizationpromotionssmartedit',
            smarteditContainer: './jsTarget/compodoc/personalizationpromotionssmarteditcontainer'
        },
        customLogo: './web/webroot/static-resources/images/SAP_scrn_R.png'
    };

    return paths;
})();
