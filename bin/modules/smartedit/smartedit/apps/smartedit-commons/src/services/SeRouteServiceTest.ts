/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { ModuleWithProviders } from '@angular/core';
import { ExtraOptions, RouterModule, Routes } from '@angular/router';
import { SeRoutes, SeRouteService, SeRouteShortcutConfig } from 'smarteditcommons';

describe('SeRouteService', () => {
    const PREFIX_SITEID = '/:siteId/a';

    let forRoot: (routes: Routes, config?: ExtraOptions) => ModuleWithProviders<RouterModule>;

    beforeEach(() => {
        forRoot = spyOn(RouterModule, 'forRoot').and.callThrough();

        // The service is static => should be cleaned after each test
        (SeRouteService as any).routeShortcuts = [];
        (SeRouteService as any).$routeProvider = undefined;
    });

    it('Should register angular route', () => {
        // GIVEN
        const correctNgRoute: SeRoutes = [
            {
                path: '/abc'
            }
        ];
        const config: ExtraOptions = {};

        // WHEN
        SeRouteService.provideNgRoute(correctNgRoute, config);

        // THEN
        expect(forRoot).toHaveBeenCalledWith(correctNgRoute, config);
    });

    it('Should register route shortcut config', () => {
        // GIVEN
        const correctNgRoute: SeRoutes = [
            {
                path: '/abc',
                titleI18nKey: 'title'
            }
        ];

        // WHEN
        SeRouteService.provideNgRoute(correctNgRoute);

        // THEN
        const expectedResult: SeRouteShortcutConfig[] = [
            {
                fullPath: '/abc',
                titleI18nKey: 'title',
                priority: undefined,
                shortcutComponent: undefined
            }
        ];
        const routeShortcutConfigs: SeRouteShortcutConfig[] = SeRouteService.routeShortcutConfigs;
        expect(routeShortcutConfigs).toEqual(expectedResult);
    });

    it('GIVEN angular routes WHEN registered THEN generate shortcut configs for all routes with full path', () => {
        // GIVEN
        const correctNgRoutes: SeRoutes = [
            {
                path: PREFIX_SITEID,
                children: [
                    {
                        path: 'b'
                    },
                    {
                        path: 'c',
                        titleI18nKey: 'cTitle',
                        children: [
                            {
                                path: 'd',
                                children: [
                                    {
                                        path: 'e',
                                        priority: 100
                                    }
                                ]
                            }
                        ]
                    }
                ],
                shortcutComponent: null
            },
            {
                path: '/:catalogId',
                children: [
                    {
                        // skip path
                        children: [
                            {
                                path: 'f'
                            }
                        ]
                    }
                ]
            }
        ];

        // WHEN
        SeRouteService.provideNgRoute(correctNgRoutes);

        // THEN
        const expectedResult: SeRouteShortcutConfig[] = [
            {
                fullPath: PREFIX_SITEID,
                titleI18nKey: undefined,
                priority: undefined,
                shortcutComponent: null
            },
            {
                fullPath: '/:siteId/a/b',
                titleI18nKey: undefined,
                priority: undefined,
                shortcutComponent: undefined
            },
            {
                fullPath: '/:siteId/a/c',
                titleI18nKey: 'cTitle',
                priority: undefined,
                shortcutComponent: undefined
            },
            {
                fullPath: '/:siteId/a/c/d',
                titleI18nKey: undefined,
                priority: undefined,
                shortcutComponent: undefined
            },
            {
                fullPath: '/:siteId/a/c/d/e',
                titleI18nKey: undefined,
                priority: 100,
                shortcutComponent: undefined
            },
            {
                fullPath: '/:catalogId',
                titleI18nKey: undefined,
                priority: undefined,
                shortcutComponent: undefined
            },
            {
                fullPath: '/:catalogId/f',
                titleI18nKey: undefined,
                priority: undefined,
                shortcutComponent: undefined
            }
        ];
        const routeShortcutConfigs: SeRouteShortcutConfig[] = SeRouteService.routeShortcutConfigs;
        expect(routeShortcutConfigs).toEqual(expectedResult);
    });

    it('GIVEN angular routes WHEN registered THEN filter from shortcutsonfig routes without path and with unknown placeholders', () => {
        // GIVEN
        const correctNgRoutes = [
            {
                path: PREFIX_SITEID
            },
            {
                path: '/:label'
            },
            {
                children: [
                    {
                        path: '/b'
                    }
                ]
            }
        ];

        // WHEN
        SeRouteService.provideNgRoute(correctNgRoutes);

        // THEN
        const expectedResult: SeRouteShortcutConfig[] = [
            {
                fullPath: PREFIX_SITEID,
                titleI18nKey: undefined,
                priority: undefined,
                shortcutComponent: undefined
            },
            {
                fullPath: '/b',
                titleI18nKey: undefined,
                priority: undefined,
                shortcutComponent: undefined
            }
        ];
        const routeShortcutConfigs: SeRouteShortcutConfig[] = SeRouteService.routeShortcutConfigs;
        expect(routeShortcutConfigs).toEqual(expectedResult);
    });
});
