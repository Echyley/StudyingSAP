/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const { smartEditTransformer } = require('@smartedit/packagr');
const ts = require('typescript');

module.exports = function (code) {
    // This array will gather the decorator metadata found while transforming the source file
    const sourceFile = ts.createSourceFile(
        'source.ts',
        code,
        ts.ScriptTarget.ES2016,
        true,
        ts.ScriptKind.TS
    );

    // Transform the source file (adds ngInject) and collects decorator metadata
    const transformationResult = ts.transform(sourceFile, [smartEditTransformer()]);

    // Appends the decorator metadata statements
    const oldSource = transformationResult.transformed[0];
    const newSource = ts.updateSourceFileNode(oldSource, oldSource.statements);

    // Get the transformed text
    const printer = ts.createPrinter({ newLine: ts.NewLineKind.LineFeed });
    const output = printer.printBundle(ts.createBundle([newSource]));
    transformationResult.dispose();

    return output;
};
