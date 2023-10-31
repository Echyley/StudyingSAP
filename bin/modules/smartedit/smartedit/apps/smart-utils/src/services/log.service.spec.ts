/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/**
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 * @module smartutils
 */
import 'jasmine';
import { LogLevel, LogService } from './log.service';

describe('Test LogService', () => {
    let mockedConsole: jasmine.SpyObj<Console>;
    const logService = new LogService();
    const message = 'theMessage';

    function resetCalls(): void {
        mockedConsole.log.calls.reset();
        mockedConsole.debug.calls.reset();
        mockedConsole.info.calls.reset();
        mockedConsole.warn.calls.reset();
        mockedConsole.error.calls.reset();
    }

    beforeEach(() => {
        mockedConsole = jasmine.createSpyObj<Console>('console', [
            'log',
            'warn',
            'info',
            'debug',
            'error'
        ]);
        spyOn(logService as any, '_console').and.returnValue(mockedConsole);
    });

    it('when loglevel is error only error method prints', () => {
        logService.setLogLevel(LogLevel.error);
        resetCalls();
        logService.error(message);
        expect(mockedConsole.error).toHaveBeenCalledWith(message);
        resetCalls();
        logService.warn(message);
        expect(mockedConsole.warn).not.toHaveBeenCalled();
        resetCalls();
        logService.info(message);
        expect(mockedConsole.info).not.toHaveBeenCalled();
        resetCalls();
        logService.debug(message);
        expect(mockedConsole.debug).not.toHaveBeenCalled();
        resetCalls();
        logService.log(message);
        expect(mockedConsole.log).not.toHaveBeenCalled();
        resetCalls();
    });

    it('when loglevel is warn only error and warn methods print', () => {
        logService.setLogLevel(LogLevel.warn);
        resetCalls();
        logService.error(message);
        expect(mockedConsole.error).toHaveBeenCalledWith(message);
        resetCalls();
        logService.warn(message);
        expect(mockedConsole.warn).toHaveBeenCalledWith(message);
        resetCalls();
        logService.info(message);
        expect(mockedConsole.info).not.toHaveBeenCalled();
        resetCalls();
        logService.debug(message);
        expect(mockedConsole.debug).not.toHaveBeenCalled();
        resetCalls();
        logService.log(message);
        expect(mockedConsole.log).not.toHaveBeenCalled();
        resetCalls();
    });

    it('when loglevel is info, only error, warn and info print', () => {
        logService.setLogLevel(LogLevel.info);
        resetCalls();
        logService.error(message);
        expect(mockedConsole.error).toHaveBeenCalledWith(message);
        resetCalls();
        logService.warn(message);
        expect(mockedConsole.warn).toHaveBeenCalledWith(message);
        resetCalls();
        logService.info(message);
        expect(mockedConsole.info).toHaveBeenCalledWith(message);
        resetCalls();
        logService.debug(message);
        expect(mockedConsole.debug).not.toHaveBeenCalled();
        resetCalls();
        logService.log(message);
        expect(mockedConsole.log).not.toHaveBeenCalled();
        resetCalls();
    });

    it("when loglevel is debug, only log method doesn't print", () => {
        logService.setLogLevel(LogLevel.debug);
        resetCalls();
        logService.error(message);
        expect(mockedConsole.error).toHaveBeenCalledWith(message);
        resetCalls();
        logService.warn(message);
        expect(mockedConsole.warn).toHaveBeenCalledWith(message);
        resetCalls();
        logService.info(message);
        expect(mockedConsole.info).toHaveBeenCalledWith(message);
        resetCalls();
        logService.debug(message);
        expect(mockedConsole.debug).toHaveBeenCalledWith(message);
        resetCalls();
        logService.log(message);
        expect(mockedConsole.log).not.toHaveBeenCalled();
        resetCalls();
    });

    it('when loglevel is log all methods print', () => {
        logService.setLogLevel(LogLevel.log);
        resetCalls();
        logService.error(message);
        expect(mockedConsole.error).toHaveBeenCalledWith(message);
        resetCalls();
        logService.warn(message);
        expect(mockedConsole.warn).toHaveBeenCalledWith(message);
        resetCalls();
        logService.info(message);
        expect(mockedConsole.info).toHaveBeenCalledWith(message);
        resetCalls();
        logService.debug(message);
        expect(mockedConsole.debug).toHaveBeenCalledWith(message);
        resetCalls();
        logService.log(message);
        expect(mockedConsole.log).toHaveBeenCalledWith(message);
        resetCalls();
    });
});
