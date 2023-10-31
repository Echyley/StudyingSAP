/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import 'jasmine';
import { HttpClient } from '@angular/common/http';
import lo from 'lodash';
import { of, throwError } from 'rxjs';
import { windowUtils, ISharedDataService, LogService } from 'smarteditcommons';
import { IframeManagerService } from 'smarteditcontainer/services';
import { jQueryHelper } from 'testhelpers';

describe('iframeManagerService', () => {
    const httpClient: jasmine.SpyObj<HttpClient> = jasmine.createSpyObj('httpClient', ['get']);
    let yjQuery: JQueryStatic;
    const sharedDataService: jasmine.SpyObj<ISharedDataService> = jasmine.createSpyObj(
        'sharedDataService',
        ['get']
    );
    const logService: jasmine.SpyObj<LogService> = jasmine.createSpyObj('logService', [
        'error',
        'debug'
    ]);

    const previewTicket = 'previewTicket1';
    const storefrontPreviewRoute = 'testPreviewRoute';
    const previewPath = '/testPreviewRoute?cmsTicketId=previewTicket1';
    const iframeManagerServiceParam = {
        width: 600,
        height: '100%',
        icon: 'icon.png',
        selectedIcon: 'icon-selected.png',
        blueIcon: 'icon-selected.png',
        type: 'newType',
        name: 'newName'
    };

    let iframeMock: jasmine.SpyObj<JQuery>;

    let iframeManagerService: IframeManagerService;

    beforeEach(() => {
        yjQuery = jQueryHelper.jQuery();

        iframeManagerService = new IframeManagerService(
            logService,
            httpClient,
            yjQuery,
            windowUtils,
            sharedDataService
        );

        iframeMock = jasmine.createSpyObj('iframeMock', ['removeClass', 'addClass', 'css', 'attr']);
        iframeMock.removeClass.and.returnValue(iframeMock);
        iframeMock.addClass.and.returnValue(iframeMock);

        spyOn(iframeManagerService, 'getIframe').and.returnValue(iframeMock);

        httpClient.get.calls.reset();

        sharedDataService.get.and.callFake(function () {
            return Promise.resolve({
                storefrontPreviewRoute
            });
        });
    });

    describe('_mustLoadAsSuch', () => {
        it('will return true if not currentLocation is set', () => {
            iframeManagerService.setCurrentLocation(undefined);
            expect((iframeManagerService as any)._mustLoadAsSuch('/myurl')).toBe(true);
        });

        it('will return true if the currentLocation is the homePageOrPageFromPageList', () => {
            iframeManagerService.setCurrentLocation('/profilepage');
            expect((iframeManagerService as any)._mustLoadAsSuch('/profilepage')).toBe(true);
        });

        it('will return true if the currentLocation has a cmsTicketId', () => {
            iframeManagerService.setCurrentLocation('/profilepage?cmsTicketId=myticketID');
            expect((iframeManagerService as any)._mustLoadAsSuch('/otherpage')).toBe(true);
        });

        it("will return false if we have a currentLocation that is not the home page or a page from the page list, and doesn't have a cmsTicketID", () => {
            iframeManagerService.setCurrentLocation('/randomURL');
            expect(
                (iframeManagerService as any)._mustLoadAsSuch('/homePageOrPageFromPageList')
            ).toBe(false);
        });
    });

    it('GIVEN that _mustLoadHasSuch has returned false WHEN I request to load a preview THEN the page will be first loaded in preview mode, then we will load the currentLocation', async () => {
        // Arrange
        (iframeManagerService as any)._mustLoadAsSuch = jasmine.createSpy().and.returnValue(false);
        spyOn(iframeManagerService, 'load');
        httpClient.get.and.callFake((uri: string) => of() as any);

        // Act
        iframeManagerService.setCurrentLocation('aLocation');
        await iframeManagerService.loadPreview('myurl', previewTicket);

        // Assert
        expect(httpClient.get).toHaveBeenCalledWith('myurl' + previewPath, {
            observe: 'body',
            responseType: 'text'
        } as any);
        expect(iframeManagerService.load).toHaveBeenCalledWith(
            'aLocation',
            true,
            'myurl' + previewPath
        );
    });

    it('GIVEN that loads is called with checkIfFailingHTML set to true WHEN the HTML is not failing THEN iframe will display the requested URL', async () => {
        // Arrange/Act
        httpClient.get.and.callFake((uri: string) => of() as any);
        await iframeManagerService.load('/notfailinghtml', true, '/myhomepage');

        // Assert
        expect(iframeMock.attr as any).toHaveBeenCalledWith('src', '/notfailinghtml');
    });

    it('GIVEN that loads is called with checkIfFailingHTML set to true WHEN the HTML is failing THEN iframe will display the homepage', async () => {
        const homepage = '/myhomepage';
        // Arrange/Act
        httpClient.get.and.callFake((uri: string) =>
            throwError({
                status: 404
            })
        );
        await iframeManagerService.load('/failinghtml', true, homepage);

        // Assert
        expect(iframeMock.attr as any).toHaveBeenCalledWith('src', homepage);
    });

    it('iframeManagerService load the expected url into the iframe', () => {
        // Arrange/Act
        iframeManagerService.load('myurl');

        // Assert
        expect(iframeMock.attr as any).toHaveBeenCalledWith('src', 'myurl');
        expect(httpClient.get).not.toHaveBeenCalled();
    });

    it('iframeManagerService loadPreview appends storefront preview route suffix to the url and the preview ticket to the query string case 1', async () => {
        // Arrange
        spyOn(iframeManagerService, 'load');

        // Act
        await iframeManagerService.loadPreview('myurl', previewTicket);

        // Assert
        expect(iframeManagerService.load).toHaveBeenCalledWith('myurl' + previewPath);
    });
    it('iframeManagerService loadPreview appends storefront preview route suffix to the url and the preview ticket to the query string case 2', async () => {
        // Arrange
        spyOn(iframeManagerService, 'load');

        // Act
        await iframeManagerService.loadPreview('myurl/', previewTicket);

        // Assert
        expect(iframeManagerService.load).toHaveBeenCalledWith('myurl' + previewPath);
    });
    it('iframeManagerService loadPreview appends storefront preview route suffix to the url and the preview ticket to the query string case 3', async () => {
        // Arrange
        spyOn(iframeManagerService, 'load');

        // Act
        await iframeManagerService.loadPreview('myurl?param1=value1', previewTicket);

        // Assert
        expect(iframeManagerService.load).toHaveBeenCalledWith(
            'myurl/testPreviewRoute?param1=value1&cmsTicketId=previewTicket1'
        );
    });
    it('iframeManagerService loadPreview appends storefront preview route suffix to the url and the preview ticket to the query string case 4', async () => {
        // Arrange
        spyOn(iframeManagerService, 'load');

        // Act
        await iframeManagerService.loadPreview('myurl/?param1=value1', previewTicket);

        // Assert
        expect(iframeManagerService.load).toHaveBeenCalledWith(
            'myurl/testPreviewRoute?param1=value1&cmsTicketId=previewTicket1'
        );
    });
    it('iframeManagerService loadPreview, without any configuration, appends default storefront preview route suffix to the url and the preview ticket to the query string case 5', async () => {
        // Arrange
        spyOn(iframeManagerService, 'load');
        sharedDataService.get.and.callFake(function () {
            return Promise.resolve({
                configuration: {}
            });
        });

        // Act
        await iframeManagerService.loadPreview('myurl', previewTicket);

        // Assert
        expect(iframeManagerService.load).toHaveBeenCalledWith(
            'myurl/cx-preview?cmsTicketId=previewTicket1'
        );
    });
    it('iframeManagerService loadPreview, without storefrontPreviewRoute configuration, appends default storefront preview route suffix to the url and the preview ticket to the query string case 6', async () => {
        // Arrange
        spyOn(iframeManagerService, 'load');
        sharedDataService.get.and.callFake(function () {
            return Promise.resolve({
                configuration: {
                    storefrontPreviewRoute: ''
                }
            });
        });

        // Act
        await iframeManagerService.loadPreview('myurl', previewTicket);

        // Assert
        expect(iframeManagerService.load).toHaveBeenCalledWith(
            'myurl/cx-preview?cmsTicketId=previewTicket1'
        );
    });

    it('apply on no arguments gives a full frame', () => {
        iframeManagerService.apply();
        expect(iframeMock.removeClass).toHaveBeenCalled();
        expect(iframeMock.addClass).not.toHaveBeenCalled();
        expect(iframeMock.css).toHaveBeenCalledWith({
            width: '100%',
            height: '100%',
            display: 'block',
            margin: 'auto'
        } as any);
    });

    it('apply device support with no orientation sets it to vertical', () => {
        iframeManagerService.apply(lo.cloneDeep(iframeManagerServiceParam));
        expect(iframeMock.removeClass).toHaveBeenCalled();
        expect(iframeMock.addClass).toHaveBeenCalledWith('device-vertical device-default');
        expect(iframeMock.css).toHaveBeenCalledWith({
            width: 600,
            height: '100%',
            display: 'block',
            margin: 'auto'
        } as any);
    });

    it('apply device support with orientation applies this orientation', () => {
        iframeManagerService.apply(
            {
                ...lo.cloneDeep(iframeManagerServiceParam),
                height: 600,
                width: '100%'
            },
            {
                orientation: 'horizontal',
                key: 'se.deviceorientation.horizontal.label'
            }
        );
        expect(iframeMock.removeClass).toHaveBeenCalled();
        expect(iframeMock.addClass).toHaveBeenCalledWith('device-horizontal device-default');
        expect(iframeMock.css).toHaveBeenCalledWith({
            width: 600,
            height: '100%',
            display: 'block',
            margin: 'auto'
        } as any);
    });
});
