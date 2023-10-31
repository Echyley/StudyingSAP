/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { ISettingsService, LogService, WindowUtils } from 'smarteditcommons';
import { UserTrackingService } from 'smarteditcontainer/services';

describe('UserTrackingService', () => {
    const customProject = 'CustomProject';
    const customCustomer = 'CustomCustomer';
    const customEnvironment = 'CustomEnvironment';
    const customUrl = 'https://license.test.com/collect';
    const testFunctionality = 'functionality';
    const testKey = 'key';
    const trackingPushArray = ['trackEvent', 'SmartEdit', testFunctionality, testKey];
    let userTrackingService: UserTrackingService;
    let logService: jasmine.SpyObj<LogService>;
    let settingService: jasmine.SpyObj<ISettingsService>;
    let windowUtils: jasmine.SpyObj<WindowUtils>;
    let mockPaq: jasmine.SpyObj<any>;

    function initializePaq(): void {
        const window = jasmine.createSpyObj<Window>('window', {}, { _paq: mockPaq });
        windowUtils.getWindow.and.returnValue(window);
    }

    function customizeCustomerEnvironmentAttributes(): void {
        settingService.get
            .withArgs(userTrackingService.PROJECT_KEY)
            .and.returnValue(Promise.resolve(customProject));
        settingService.get
            .withArgs(userTrackingService.CUSTOMER_KEY)
            .and.returnValue(Promise.resolve(customCustomer));
        settingService.get
            .withArgs(userTrackingService.ENVIRONMENT_KEY)
            .and.returnValue(Promise.resolve(customEnvironment));
        settingService.get
            .withArgs(userTrackingService.TRACKING_URL_KEY)
            .and.returnValue(Promise.resolve(customUrl));
    }

    beforeEach(() => {
        mockPaq = jasmine.createSpyObj<any>('_paq', ['push']);
        windowUtils = jasmine.createSpyObj<WindowUtils>('mockWindowsUtils', ['getWindow']);
        settingService = jasmine.createSpyObj<ISettingsService>('mockIsettingService', [
            'get',
            'getBoolean'
        ]);
        logService = jasmine.createSpyObj<LogService>('mockLogService', ['warn']);

        userTrackingService = new UserTrackingService(windowUtils, settingService, logService);
    });

    it('User tracking service is not enabled, then not to check _paq  AND is not initialized AND can not track action', async () => {
        settingService.getBoolean.and.returnValue(Promise.resolve(false));

        await userTrackingService.initConfiguration();
        userTrackingService.trackingUserAction(testFunctionality, testKey);

        expect(windowUtils.getWindow).not.toHaveBeenCalled();
        expect(userTrackingService.isInitialized).toEqual(false);
        expect(mockPaq.push).not.toHaveBeenCalledWith(trackingPushArray);
    });

    it('User tracking service is enabled and tracking env is not initialized, then log warn AND not initialized', async () => {
        settingService.getBoolean.and.returnValue(Promise.resolve(true));

        const window = jasmine.createSpyObj<Window>('window', ['_paq']);
        windowUtils.getWindow.and.returnValue(window);

        await userTrackingService.initConfiguration();
        userTrackingService.trackingUserAction(testFunctionality, testKey);

        expect(windowUtils.getWindow).toHaveBeenCalled();
        expect(logService.warn).toHaveBeenCalled();
        expect(userTrackingService.isInitialized).toEqual(false);
        expect(mockPaq.push).not.toHaveBeenCalledWith(trackingPushArray);
    });

    it('User tracking service is enabled and tracking env is initialized', async () => {
        settingService.getBoolean.and.returnValue(Promise.resolve(true));
        initializePaq();

        await userTrackingService.initConfiguration();
        userTrackingService.trackingUserAction(testFunctionality, testKey);

        expect(userTrackingService.isInitialized).toEqual(true);
        expect(mockPaq.push).toHaveBeenCalledWith([
            'setTrackerUrl',
            userTrackingService.DEFAULT_TRACKING_URL
        ]);
        expect(mockPaq.push).toHaveBeenCalledWith([
            'setSiteId',
            `${userTrackingService.DEFAULT_CUSTOMER}-${userTrackingService.DEFAULT_PROJECT}-${userTrackingService.DEFAULT_ENVIRONMENT}`
        ]);
        expect(mockPaq.push).toHaveBeenCalledWith(trackingPushArray);
    });

    it('tracking env is initialized and customer attributes have value', async () => {
        settingService.getBoolean.and.returnValue(Promise.resolve(true));
        initializePaq();
        customizeCustomerEnvironmentAttributes();

        await userTrackingService.initConfiguration();

        expect(mockPaq.push).toHaveBeenCalledWith(['setTrackerUrl', customUrl]);
        expect(mockPaq.push).toHaveBeenCalledWith([
            'setSiteId',
            `${customCustomer}-${customProject}-${customEnvironment}`
        ]);
    });
});
