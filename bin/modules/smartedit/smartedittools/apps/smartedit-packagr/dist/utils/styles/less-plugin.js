"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.getRewriteUrlPlugin = void 0;
/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const assets_1 = require("../assets");
const path_1 = require("../path");
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
    constructor(rootFilePath) {
        this.rootFilePath = rootFilePath;
        this.nativeVisitor = new less.visitors.Visitor(this);
        this.isPreEvalVisitor = false; // This is necessary to ensure that URL values are already processed.
        this.isReplacing = true;
    }
    run(root) {
        return this.nativeVisitor.visit(root);
    }
    visitUrl(urlNode, visitArgs) {
        const value = urlNode.value;
        if (value && value instanceof less.tree.Quoted && value.value) {
            let urlValue = value.value;
            if (urlValue.startsWith(tilde)) {
                urlValue = urlValue.replace(tilde, 'node_modules/');
            }
            const currentDirectory = urlNode.fileInfo().currentDirectory || this.rootFilePath;
            if (this.shouldCopyAsset(currentDirectory, urlValue)) {
                const assetPath = urlValue.startsWith('node_modules')
                    ? path_1.resolvePath(urlValue)
                    : this.resolveAssetPath(currentDirectory, urlValue);
                const newPath = assets_1.copyToAssetsFolder(assetPath);
                return this.rewriteUrl(urlNode, newPath);
            }
            else if (this.isValidUrl(urlValue)) {
                // Get the dist folder path from the base path
                const distPath = path_1.resolvePath('./dist');
                // Get the asset folder from the less file directory.
                const assetPath = path.resolve(currentDirectory, urlValue);
                // Rewrite Url's so that they are relative from the dist folder.
                const newUrl = path.relative(distPath, assetPath);
                return this.rewriteUrl(urlNode, newUrl);
            }
        }
        return urlNode;
    }
    rewriteUrl(urlNode, newUrl) {
        newUrl = newUrl.replace(/\\/g, '/');
        const currentFileInfo = urlNode.currentFileInfo;
        const currentIndex = urlNode.index || 0;
        const newValue = new less.tree.Quoted('"' + newUrl + '"', newUrl, false, currentIndex, currentFileInfo);
        return new less.tree.URL(newValue, currentIndex, currentFileInfo);
    }
    isValidUrl(url) {
        return !url.startsWith('data:');
    }
    shouldCopyAsset(currentDirectory, urlValue) {
        return ((currentDirectory.includes('node_modules') || urlValue.includes('node_modules')) &&
            this.isValidUrl(urlValue));
    }
    resolveAssetPath(currentDirectory, assetPath) {
        return path.resolve(currentDirectory, assetPath);
    }
}
const getRewriteUrlPlugin = (rootFilePath) => {
    return {
        install: (less, pluginManager) => {
            pluginManager.addVisitor(new RewriteUrlVisitor(rootFilePath));
        }
    };
};
exports.getRewriteUrlPlugin = getRewriteUrlPlugin;
