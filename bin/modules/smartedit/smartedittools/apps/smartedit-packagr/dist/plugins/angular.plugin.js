"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.angular = void 0;
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const utils_1 = require("../utils");
const path_1 = require("path");
const rollup_plugin_angular_1 = require("./rollup.plugin.angular");
/**
 * The Angular plugin is a Rollup plugin that reads Angular decorators and processes
 * any HTML or styles referenced in those decorators in the following way:
 * - Template: The HTML will be minified and left inline.
 * - TemplateUrl: The file will be read and the HTML will be minified, and inlined.
 * - Styles: The Styles will be compiled, minified, and left inline.
 * - StyleUrls: The files will be read, compiled, minified and inlined.
 *
 * @param stylesProcessor An instance of a StylesProcessor class used to compile styles.
 */
const angular = (stylesProcessor) => {
    return rollup_plugin_angular_1.angularPlugin({
        preprocessors: {
            template: (html) => {
                return utils_1.minifyHtml(html);
            },
            style: (scss, id) => {
                const filePath = path_1.dirname(id);
                return Promise.resolve(stylesProcessor.compileInnerSass(scss, filePath));
            }
        }
    });
};
exports.angular = angular;
