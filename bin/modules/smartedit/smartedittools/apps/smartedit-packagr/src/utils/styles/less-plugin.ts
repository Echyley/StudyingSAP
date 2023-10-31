/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { copyToAssetsFolder } from '../assets';
import { resolvePath } from '../path';

const less = require('less');
const path = require('path');
const tilde = '~';

/**
 * This file contains a plugin used in Less used to rewrite URLs found while processing .less files.
 * URLs are rewritten in the following way:
 * - URLs with a tilde (~): The tilde is replaced with node_modules/
 * - For URLs pointing to assets in other libraries (node_modules), the assets are copied
 *   to the /dist/assets folder in the current library.
 * - URLs are rewritten to ensure they are relative from the root of the current library.
 *
 * Note:
 * - This is NOT a Rollup plugin. This is a plugin used only within Less.
 */

/**
 * The visitor used within the plugin to find and rewrite URLs.
 */
class RewriteUrlVisitor {
    public nativeVisitor: any;
    public isPreEvalVisitor: boolean;
    public isReplacing: boolean;

    constructor(private rootFilePath: string) {
        this.nativeVisitor = new less.visitors.Visitor(this);
        this.isPreEvalVisitor = false; // This is necessary to ensure that URL values are already processed.
        this.isReplacing = true;
    }

    public run(root: any) {
        return this.nativeVisitor.visit(root);
    }

    public visitUrl(urlNode: any, visitArgs: any) {
        const value = urlNode.value;
        if (value && value instanceof less.tree.Quoted && value.value) {
            let urlValue = value.value;
            if (urlValue.startsWith(tilde)) {
                urlValue = urlValue.replace(tilde, 'node_modules/');
            }

            const currentDirectory = urlNode.fileInfo().currentDirectory || this.rootFilePath;
            if (this.shouldCopyAsset(currentDirectory, urlValue)) {
                const assetPath = urlValue.startsWith('node_modules')
                    ? resolvePath(urlValue)
                    : this.resolveAssetPath(currentDirectory, urlValue);

                const newPath = copyToAssetsFolder(assetPath);

                return this.rewriteUrl(urlNode, newPath);
            } else if (this.isValidUrl(urlValue)) {
                // Get the dist folder path from the base path
                const distPath = resolvePath('./dist');

                // Get the asset folder from the less file directory.
                const assetPath = path.resolve(currentDirectory, urlValue);

                // Rewrite Url's so that they are relative from the dist folder.
                const newUrl = path.relative(distPath, assetPath);
                return this.rewriteUrl(urlNode, newUrl);
            }
        }

        return urlNode;
    }

    private rewriteUrl(urlNode: any, newUrl: string) {
        newUrl = newUrl.replace(/\\/g, '/');
        const currentFileInfo = urlNode.currentFileInfo;
        const currentIndex = urlNode.index || 0;
        const newValue = new less.tree.Quoted(
            '"' + newUrl + '"',
            newUrl,
            false,
            currentIndex,
            currentFileInfo
        );
        return new less.tree.URL(newValue, currentIndex, currentFileInfo);
    }

    private isValidUrl(url: string): boolean {
        return !url.startsWith('data:');
    }

    private shouldCopyAsset(currentDirectory: string, urlValue: string): boolean {
        return (
            (currentDirectory.includes('node_modules') || urlValue.includes('node_modules')) &&
            this.isValidUrl(urlValue)
        );
    }

    private resolveAssetPath(currentDirectory: string, assetPath: string): string {
        return path.resolve(currentDirectory, assetPath);
    }
}

export const getRewriteUrlPlugin = (rootFilePath: string) => {
    return {
        install: (less: any, pluginManager: any) => {
            pluginManager.addVisitor(new RewriteUrlVisitor(rootFilePath));
        }
    };
};
