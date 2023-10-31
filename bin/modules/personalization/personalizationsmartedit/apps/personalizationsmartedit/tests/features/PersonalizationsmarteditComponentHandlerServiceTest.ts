import {
    PersonalizationsmarteditComponentHandlerService,
    IPageContentSlotsResponse
} from 'personalizationsmartedit/service/PersonalizationsmarteditComponentHandlerService';
import {
    IPageInfoService,
    RestServiceFactory,
    CrossFrameEventService,
    IRestService,
    IComponentHandlerService
} from 'smarteditcommons';
import { PAGES_CONTENT_SLOT_RESOURCE_URI } from '../../src/constants';

describe('PersonalizationsmarteditComponentHandlerService', () => {
    type MockSlot = { id?: string; isAllowed?: boolean; parentId?: any } & jasmine.SpyObj<
        JQuery<HTMLElement>
    >;
    type MockComponent = {
        id?: string;
        original?: MockComponent;
        containerId?: any;
        length?: any;
        containerSourceId?: any;
    } & jasmine.SpyObj<JQuery<HTMLElement>>;
    const yjQuery: any = jasmine.createSpy('yjQuery');
    let componentHandlerService: jasmine.SpyObj<IComponentHandlerService>;
    let cfEventService: jasmine.SpyObj<CrossFrameEventService>;
    let restServiceFactory: jasmine.SpyObj<RestServiceFactory>;
    let pageInfoService: jasmine.SpyObj<IPageInfoService>;
    let restServiceResource: jasmine.SpyObj<any>;
    let componentMock: MockComponent;
    let containerId: string;
    let anotherIdSlot: string;

    const CONTAINER_ID_ATTRIBUTE = 'data-smartedit-container-id';
    const ID_ATTRIBUTE = 'data-smartedit-component-id';

    const pagescontentslots: IPageContentSlotsResponse = {
        pageContentSlotList: [
            {
                pageId: 'homepage',
                slotId: 'topHeaderSlot',
                position: '0',
                slotShared: true,
                slotStatus: 'TEMPLATE'
            },
            {
                pageId: 'homepage',
                slotId: 'bottomHeaderSlot',
                position: '1',
                slotShared: false,
                slotStatus: 'OVERRIDE'
            },
            {
                pageId: 'homepage',
                slotId: 'footerSlot',
                position: '2',
                slotShared: false,
                slotStatus: 'PAGE'
            },
            {
                pageId: 'homepage',
                slotId: 'otherSlot',
                position: '3',
                slotShared: true,
                slotStatus: 'TEMPLATE'
            }
        ]
    };

    let personalizationsmarteditComponentHandlerService: PersonalizationsmarteditComponentHandlerService;

    // === SETUP ===
    beforeEach(() => {
        containerId = 'myContainer';
        anotherIdSlot = 'anotherIdSlot';

        restServiceFactory = jasmine.createSpyObj<RestServiceFactory>('restServiceFactory', [
            'get'
        ]);

        restServiceResource = jasmine.createSpyObj<IRestService<IPageContentSlotsResponse>>(
            'restServiceResource',
            ['get']
        );

        cfEventService = jasmine.createSpyObj<CrossFrameEventService>('crossFrameEventService', [
            'subscribe'
        ]);

        componentHandlerService = jasmine.createSpyObj('componentHandlerService', [
            'getParentSlotForComponent',
            'getOriginalComponent',
            'isExternalComponent',
            'getCatalogVersionUuid',
            'getAllSlotsSelector',
            'getParentContainerIdForComponent',
            'getParentSlotIdForComponent',
            'getParentContainerForComponent',
            'getParentContainerForComponent',
            'getContainerSourceIdForContainerId',
            'getParentSlotForComponent'
        ]);

        pageInfoService = jasmine.createSpyObj<IPageInfoService>('pageInfoService', ['getPageUID']);

        componentHandlerService.getAllSlotsSelector.and.callFake(function () {
            return '';
        });

        restServiceFactory.get.and.returnValue(restServiceResource);
        restServiceResource.get.and.returnValue(Promise.resolve(pagescontentslots) as any);
        pageInfoService.getPageUID.and.returnValue(Promise.resolve('homepage'));

        personalizationsmarteditComponentHandlerService = new PersonalizationsmarteditComponentHandlerService(
            restServiceFactory,
            cfEventService,
            yjQuery,
            componentHandlerService,
            pageInfoService
        );

        personalizationsmarteditComponentHandlerService.getFromJQuerySelector = function (
            selector: JQuery
        ) {
            return componentMock.find(selector);
        };

        componentMock = jasmine.createSpyObj<JQuery<HTMLElement>>('component', [
            'closest',
            'find',
            'attr'
        ]);
        componentMock.id = 'initial component ID';
        componentMock.original = componentMock;

        componentMock.closest.and.callFake((componentSelector: string) => {
            if (componentSelector !== '') {
                componentMock.length = 1;
                if (componentSelector === "['div']") {
                    componentMock.length = 0;
                }
                if (componentSelector === '["data-smartedit-container-id"="myContainer"]') {
                    componentMock.containerSourceId = 'myContainerSource';
                }
                if (componentSelector === "['myContainerNotExist']") {
                    componentMock = '' as any;
                }
                return componentMock;
            } else {
                return undefined;
            }
        });

        componentMock.attr.and.callFake(((arg: string) => {
            if (arg === CONTAINER_ID_ATTRIBUTE) {
                return containerId;
            } else if (arg === ID_ATTRIBUTE) {
                return anotherIdSlot;
            }
        }) as any);
    });

    describe('constructor', () => {
        it('should call restServiceFactory and subscribe to PAGE_CHANGE event', () => {
            expect(cfEventService.subscribe).toHaveBeenCalledWith(
                'PAGE_CHANGE',
                jasmine.any(Function)
            );
            expect(restServiceFactory.get).toHaveBeenCalledWith(PAGES_CONTENT_SLOT_RESOURCE_URI);
        });

        it('should load fresh data when PAGE_CHANGE event is fired', async () => {
            const cb = cfEventService.subscribe.calls.argsFor(0)[1];

            await cb(null);

            expect(pageInfoService.getPageUID).toHaveBeenCalled();
            expect(restServiceResource.get).toHaveBeenCalledWith({ pageId: 'homepage' } as any);
        });
    });

    describe('getParentContainerIdForComponent', () => {
        it('should be defined', function () {
            expect(
                personalizationsmarteditComponentHandlerService.getParentContainerIdForComponent
            ).toBeDefined();
        });

        it('should return container id if element exists', () => {
            // given
            // when
            const ret = componentMock.closest(
                '["data-smartedit-container-type"="CxCmsComponentContainer"]'
            );
            const attrString = ret.attr(CONTAINER_ID_ATTRIBUTE);
            // then
            expect(attrString).toBe(containerId as any);
        });

        it('should return undefined if element doesnt exists', () => {
            // given
            const ret = componentMock.closest('');
            // then
            expect(ret).toBe(undefined);
        });
    });

    describe('getParentContainerForComponent', () => {
        it('should be defined', () => {
            expect(
                personalizationsmarteditComponentHandlerService.getParentContainerForComponent
            ).toBeDefined();
        });

        it('should return container id if element exists', () => {
            // given
            const element = componentMock.closest(
                '["data-smartedit-container-type"="CxCmsComponentContainer"]'
            );
            // when
            const ret = personalizationsmarteditComponentHandlerService.getParentContainerForComponent(
                element
            );
            // then
            expect(ret.attr(CONTAINER_ID_ATTRIBUTE)).toBe('myContainer');
        });

        it('should return empty array if element element doesnt exists', () => {
            // given
            const element = componentMock.closest("['div']");
            // then
            expect(element.length).toBe(0);
        });
    });

    describe('getParentSlotIdForComponent', () => {
        it('should be defined', () => {
            expect(
                personalizationsmarteditComponentHandlerService.getParentSlotIdForComponent
            ).toBeDefined();
        });

        it('should return null if element doesnt exist', () => {
            // given
            const element = componentMock.closest('');
            // when
            const ret = personalizationsmarteditComponentHandlerService.getParentSlotIdForComponent(
                element
            );
            // then
            expect(ret).toBe(undefined);
        });
    });

    describe('getParentSlotForComponent', () => {
        it('should be defined', () => {
            expect(
                personalizationsmarteditComponentHandlerService.getParentSlotForComponent
            ).toBeDefined();
        });

        it('should return slot id if element exist', () => {
            // given
            const element = componentMock.closest(
                '["data-smartedit-component-type"="ContentSlot"]'
            );
            // when
            const ret = personalizationsmarteditComponentHandlerService.getParentSlotForComponent(
                element
            );
            // then
            expect(ret.attr(ID_ATTRIBUTE)).toBe('anotherIdSlot');
        });

        it('should return empty array if element doesnt exist', () => {
            // given
            const element = componentMock.closest("['div']");
            // then
            expect(element.length).toBe(0);
        });
    });

    describe('getContainerSourceIdForContainerId', () => {
        it('should be defined', () => {
            expect(
                personalizationsmarteditComponentHandlerService.getContainerSourceIdForContainerId
            ).toBeDefined();
        });

        it('should return container source id if container with specific id exist', () => {
            // when
            const ret = componentMock.closest('["data-smartedit-container-id"="myContainer"]');
            // then
            expect(ret.containerSourceId).toBe('myContainerSource');
        });

        it('should return empty string if container with specific id doesnt exist', () => {
            // when
            const ret = componentMock.closest("['myContainerNotExist']");
            // then
            expect(ret).toBe('' as any);
        });
    });
});
