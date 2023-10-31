/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { StylesProcessor, minifyHtml } from '../utils';
import { dirname } from 'path';
import { angularPlugin } from './rollup.plugin.angular';

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
export const angular = (stylesProcessor: StylesProcessor) => {
    return angularPlugin({
        preprocessors: {
            template: (html: string): Promise<string> => {
                return minifyHtml(html);
            },
            style: (scss: string, id: string): Promise<string> => {
                const filePath = dirname(id);
                return Promise.resolve(stylesProcessor.compileInnerSass(scss, filePath));
            }
        }
    });
};
