import { CarouselComponent } from "./carousel.component";
import { DataService } from "./carousel.service";

describe('CarouselComponent', () => {
	let component: CarouselComponent;
	let el: HTMLElement;
	let data;
	let response = {
		mixcardID: 'mixcardID',
		products: null
	};

	beforeEach(() => {
		delete window.__merchcarousels;
		spyOn(DataService.prototype, 'getProducts').and.callFake(() => {
			return Promise.resolve(response) as any;
		}) // do not call network

		el = document.createElement('div');
		data = {
			items: [],
			title: ''
		};

		component = new CarouselComponent({
			el,
			data
		});
	});

	it('component constructor should exist', () => {
		expect(CarouselComponent).toBeTruthy();
	});

	it('getHtml() should return the html string if items', () => {
		const actual = component.getHtml();

		expect(typeof actual).toEqual('object');
	});

    it('getHtml() should return proper html', () => {

        let carousel = '<div class="carousel__component merchandising-carousel" >' +
            '<div class="carousel__component--headline" ></div>' +
            '<div class="carousel__component--carousel js-merch-carousel"></div>' +
            '<div id="data-merchandising-carousel-undefined" class="data-merchandising-carousel" ' +
            'data-merchandising-carousel-title="" data-merchandising-carousel-name="undefined" ' +
            'data-merchandising-carousel-strategyid="undefined" data-merchandising-carousel-id="undefined"' +
            'data-merchandising-carousel-slots="0"></div></div>';

        carousel = carousel.replace(/\n/g, "").replace(/\s/g, "");

        const actual = component.getHtml().prop('outerHTML').replace(/\n/g, "").replace(/\s/g, "");

        expect(carousel).toEqual(actual);
    });

    it('getHtml() should return proper html when called with additional parameters', () => {

        let carousel = '<div class="carousel__component merchandising-carousel" style="background-color: red;">' +
            '<div class="carousel__component--headline" style="color: blue;">testTitle</div>' +
            '<div class="carousel__component--carousel js-merch-carousel">' +
            '<div class="carousel__item">' +
            '<a href="testURL"><div class="carousel__item--thumb"><img src="testMainImage" alt="testName2" title="testName2">' +
            '</div><div class="carousel__item--name merchcarousel_custom-color" style="color: blue;">testName2</div>' +
            '<div class="carousel__item--price merchcarousel_custom-color" style="color: blue;">testUSD100</div></a>' +
            '<div id="data-merchandising-product-testID" class="data-merchandising-product" data-merchandising-product-id="testID" ' +
            'data-merchandising-product-slot="1" data-merchandising-product-testmetadatakey2="testMetadataValue2"></div>' +
            '</div></div>' +
            '<div id="data-merchandising-carousel-testCarouselId" class="data-merchandising-carousel" ' +
            'data-merchandising-carousel-testmeta="testMetaName"' +
            'data-merchandising-carousel-title="testTitle" data-merchandising-carousel-name="testName" ' +
            'data-merchandising-carousel-strategyid="testStrategy" ' +
            'data-merchandising-carousel-id="testCarouselId"data-merchandising-carousel-slots="1" ' +
            'data-merchandising-carousel-testmetadatakey="testMetadataValue" ></div></div>';

        carousel = carousel.replace(/\n/g, "").replace(/\s/g, "");

        const actual = component.getHtml.apply({
            data: {
                backgroundcolour: "red",
                textcolour: "blue",
                items: [{
                    id: "testID",
                    pageUrl: "testURL",
                    mainImage: "testMainImage",
                    name: "testName2",
                    price: 100,
                    metadata: {testmetadatakey2: "testMetadataValue2"}
                }],
                carouselId: "testCarouselId",
                currency: "testUSD",
                title: "testTitle",
                name: "testName",
                strategy: "testStrategy",
                metadata: {testmetadatakey: "testMetadataValue"}
            },
            metadata: {"data-merchandising-carousel-testmeta":"testMetaName"}
        },[]).prop('outerHTML').replace(/\n/g, "").replace(/\s/g, "");

        expect(carousel).toEqual(actual);
    });

	it('render() should init owlCarousel', () => {
		const jProto = ($.fn as any);
		jProto.owlCarousel = () => {};
		spyOn(jProto, 'owlCarousel');
		component.registerIntersectionObserver = () => {};

		component.render();

		expect(jProto.owlCarousel).toHaveBeenCalled();
	});

	it('init() should save a global ref to CarouselComponent', () => {
		CarouselComponent.init();

		expect(window.__merchcarousels.CarouselComponent).toEqual(CarouselComponent);
	});

	it('init() should not init an item if it is already inited', (done) => {
		const jProto = ($.fn as any);
		jProto.owlCarousel = () => {};

		window['__merchcarousels'] = {
			'item': {
				el,
				inited: true,
				data
			}
		};
		const constructorSpy = spyOn(CarouselComponent.prototype as any, 'constructor').and.callThrough();
		CarouselComponent.init();

		setTimeout(() => {
			expect(constructorSpy).not.toHaveBeenCalled();
			done();
		}, 10);
	});

	it('init() should init an item if it is not already inited', (done) => {
		const jProto = ($.fn as any);
		jProto.owlCarousel = () => {};

		el.dataset.numbertodisplay = '10';
		window['__merchcarousels'] = {
			'item': {
				el,
				inited: false,
				data
			}
		};

		response = {
			mixcardID: 'mixcardID',
			products: [
				{ name: 'name' }
			]
		};

		const renderSpy = spyOn(CarouselComponent.prototype, 'render');
		CarouselComponent.init();

		setTimeout(() => {
			expect(renderSpy).toHaveBeenCalled();
			done();
		}, 10);
	});

});
