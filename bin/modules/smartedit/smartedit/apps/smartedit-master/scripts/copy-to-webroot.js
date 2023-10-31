/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const fs = require('fs-extra');

const args = process.argv.slice(2);

const hasArgument = (expectedArg) => args.some((arg) => arg === expectedArg);

const copyCKEditor = () => {
    console.log('Copying CKEditor sources to webroot');
    fs.copySync(
        './node_modules/ckeditor4',
        '../../web/webroot/static-resources/thirdparties/ckeditor',
        {
            overwrite: true,
            dereference: true
        }
    );
};

if (hasArgument('--inner')) {
    console.log('Copying generated inner frame sources to webroot');
    fs.copySync('./dist/smartedit', '../../web/webroot/static-resources/dist/smartedit-new', {
        overwrite: true
    });
}

if (hasArgument('--container')) {
    console.log('Copying generated container sources to webroot');
    fs.copySync(
        './dist/smartedit-container',
        '../../web/webroot/static-resources/dist/smartedit-container-new',
        {
            overwrite: true
        }
    );

    fs.copySync('./dist/index.jsp', '../../web/webroot/WEB-INF/views/index.jsp', {
        overwrite: true
    });

    fs.copySync(
        './node_modules/smarteditcommons/node_modules/@sap-theming/theming-base-content/content/Base/baseLib/sap_fiori_3/css_variables.css',
        '../../web/webroot/static-resources/dist/smartedit-container-new/sap_fiori_3/css_variables.css',
        {
            overwrite: true
        }
    );
    fs.copySync(
        './node_modules/smarteditcommons/node_modules/fundamental-styles/dist/theming/sap_fiori_3.css',
        '../../web/webroot/static-resources/dist/smartedit-container-new/sap_fiori_3/sap_fiori_3.css',
        {
            overwrite: true
        }
    );
    fs.copySync(
        './node_modules/smarteditcommons/node_modules/@sap-theming/theming-base-content/content/Base/baseLib/sap_horizon/css_variables.css',
        '../../web/webroot/static-resources/dist/smartedit-container-new/sap_horizon/css_variables.css',
        {
            overwrite: true
        }
    );
    fs.copySync(
        './node_modules/smarteditcommons/node_modules/fundamental-styles/dist/theming/sap_horizon.css',
        '../../web/webroot/static-resources/dist/smartedit-container-new/sap_horizon/sap_horizon.css',
        {
            overwrite: true
        }
    );
    fs.copySync(
        './node_modules/smarteditcommons/node_modules/@sap-theming/theming-base-content/content/Base/baseLib/sap_fiori_3_dark/css_variables.css',
        '../../web/webroot/static-resources/dist/smartedit-container-new/sap_fiori_3_dark/css_variables.css',
        {
            overwrite: true
        }
    );
    fs.copySync(
        './node_modules/smarteditcommons/node_modules/fundamental-styles/dist/theming/sap_fiori_3_dark.css',
        '../../web/webroot/static-resources/dist/smartedit-container-new/sap_fiori_3_dark/sap_fiori_3_dark.css',
        {
            overwrite: true
        }
    );
}

copyCKEditor();
