/**
 * Copyright (c) 2020 SAP SE or an SAP affiliate company. All rights reserved.
 */

describe('merchandisingsmartedit - some test suite with TypeScript', () => {
    let mockAngular: any;
    mockAngular = jasmine.createSpyObj('mockAngular', ['module']);

	beforeEach(() => {
        mockAngular.module("merchandisingsmartedit");
	});

	it('will assert that true equals true', () => {
		expect(true).toBe(true);
	});
});
