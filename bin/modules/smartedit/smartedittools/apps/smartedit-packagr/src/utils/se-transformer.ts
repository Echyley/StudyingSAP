/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import * as ts from 'typescript';
import { InstrumentationConfiguration } from '../configuration';

/**
 * If a class has one of these decorators then it should be annotated with @ngInject
 */
const decoratorsRequiringNgInject = new Set<string>([
    'SeInjectable',
    'SeDirective',
    'SeModule',

    /**
     * For cross-framework dependency injection some Angular services must be annotated
     * with @ngInject
     */
    'Injectable'
]);

/**
 * If a class has one of these decorators then the class must be "augmented" with more information
 * so that the SmartEdit dependency injection can do some downgrading for custom registration.
 */
const decoratorsRequiringAugmentation = new Set<string>(['Injectable', 'Component']);

/**
 * Object that holds information about a decorator.
 */
class DecoratorData {
    constructor(public decoratorName: string, public metadata: ts.Expression) {}
}

const getDecoratorName = (node: ts.Node): string | null => {
    if (ts.isIdentifier(node)) {
        return node.getText();
    }

    if (node.getChildCount() === 0) {
        return null;
    }

    return node.forEachChild((child) => {
        return getDecoratorName(child);
    }) as string;
};

const getDecoratorObject = (node: ts.Node): ts.Expression | null => {
    if (ts.isObjectLiteralExpression(node)) {
        return node;
    }

    if (node.getChildCount() === 0) {
        return null;
    }

    return node.forEachChild((child) => {
        return getDecoratorObject(child);
    }) as ts.Expression;
};

const augmentDecorator = (
    node: ts.ClassDeclaration,
    decoratorToAugment: DecoratorData
): ts.ExpressionStatement => {
    const className = ts.forEachChild(node, (child): string | undefined => {
        if (ts.isIdentifier(child)) {
            return child.getText();
        }
    });

    /**
     * Creates the equivalent statement:
     * @example
     * (window as any).__smartedit__.addDecoratorPayload(
     *  'Component',
     *  'TacosComponent',
     *  {
     *    selector: 'se-tacos',
     *    template: '<span>me gusta</span>
     *  }
     * )
     */
    const statement = ts.createExpressionStatement(
        ts.createCall(
            ts.createPropertyAccess(
                ts.createPropertyAccess(
                    ts.createParen(
                        ts.createAsExpression(
                            ts.createIdentifier('window'),
                            ts.createKeywordTypeNode(ts.SyntaxKind.AnyKeyword)
                        )
                    ),
                    ts.createIdentifier('__smartedit__')
                ),
                ts.createIdentifier('addDecoratorPayload')
            ),
            undefined,
            [
                ts.createStringLiteral(decoratorToAugment!.decoratorName),
                ts.createStringLiteral(className!),
                decoratorToAugment!.metadata
            ]
        )
    );

    return statement;
};

const addNgInject = (node: ts.ClassDeclaration, context: ts.TransformationContext) => {
    const visitor = (child: ts.Node) => {
        if (ts.isIdentifier(child)) {
            return ts.createIdentifier(`/* @ngInject */ ${child.getText()}`);
        }

        return child;
    };

    return ts.visitEachChild(node, visitor, context);
};

const buildResultNode = (statement: ts.ExpressionStatement | null, node: ts.Node) => {
    return statement ? [statement, node] : node;
};

const transformClass = (node: ts.ClassDeclaration, context: ts.TransformationContext) => {
    let requiresNgInject: boolean = false;
    let decoratorToAugment: DecoratorData | null = null;

    ts.forEachChild(node, (child) => {
        if (!ts.isDecorator(child)) {
            return;
        }
        const decoratorName = getDecoratorName(child);
        if (!decoratorName) {
            return;
        }

        if (decoratorsRequiringNgInject.has(decoratorName)) {
            requiresNgInject = true;
        }

        if (!decoratorsRequiringAugmentation.has(decoratorName)) {
            return;
        }

        const metadata = getDecoratorObject(child) as ts.Node;

        if (metadata) {
            const mutableMetadata = ts.getMutableClone(metadata) as ts.Expression;
            decoratorToAugment = new DecoratorData(decoratorName, mutableMetadata);
        }
    });

    let statement = null;
    if (decoratorToAugment) {
        statement = augmentDecorator(node, decoratorToAugment);
    }

    if (requiresNgInject) {
        return buildResultNode(statement, addNgInject(node, context));
    }

    return buildResultNode(statement, node);
};

/**
 *
 * @param statements
 */
export const smartEditTransformer = (): ts.TransformerFactory<ts.SourceFile> => {
    return (context: ts.TransformationContext): ts.Transformer<ts.SourceFile> => {
        return (sourceFile: ts.SourceFile) => {
            const visitor: ts.Visitor = (node: ts.Node) => {
                if (ts.isClassDeclaration(node) && node.decorators && node.decorators.length) {
                    return transformClass(node, context);
                }

                return ts.visitEachChild(node, (child) => visitor(child), context);
            };

            return ts.visitNode(sourceFile, visitor);
        };
    };
};

export const smartEditTransformerFactory = (config?: InstrumentationConfiguration) => {
    return () => {
        if (config && config.skipInstrumentation) {
            return {};
        }

        return {
            before: smartEditTransformer()
        };
    };
};
