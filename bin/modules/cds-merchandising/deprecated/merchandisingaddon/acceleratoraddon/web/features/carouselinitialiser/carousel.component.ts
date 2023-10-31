import { DataService } from './carousel.service';
import { DataLayer, CAROUSEL_CLICKED_EVENT, CAROUSEL_VIEWED_EVENT, CarouselClickEventPayload, CarouselEventPayload } from './datalayer.service';
import { log, sanitizeUrl, getCookie } from './utils';
import * as $ from 'jquery';

/**
 * UI component, wrapper around the owlCarousel
 * @see jQuery.fn.owlCarousel
 */
export class CarouselComponent {
    private $el;
    private data;
    private metadata = {};
    private defaultViewportPercentage = 80;

    constructor({ el, data = {} }) {
        this.$el = $(el);
        this.data = data;
    }

    render() {
        log('render', this.data);
        this.$el
            .empty().append(this.getHtml())
            .find('.js-merch-carousel')
            .owlCarousel({
                scrollPerPage: this.data.scroll === 'ALLVISIBLE',
                navigation: true,
                navigationText: ["<span class='glyphicon glyphicon-chevron-left'></span>", "<span class='glyphicon glyphicon-chevron-right'></span>"],
                pagination: false,
                itemsCustom: [[0, 2], [640, 4], [1024, 5], [1400, 7]]
            });
        this.registerIntersectionObserver();
    }

    registerIntersectionObserver() {
        const carouselIntersectionhandler = (entries, observer) => {
            entries.filter(entry => entry.intersectionRatio > 0).forEach(entry => {
                const eventPayload = new CarouselEventPayload(window.__merchcarousels[entry.target.id].data);
                CarouselComponent.raiseEvent(CAROUSEL_VIEWED_EVENT, eventPayload);
                observer.unobserve(document.getElementById(entry.target.id));
            });
        };

        const viewportPercentage = this.data.viewportpercentage ? this.data.viewportpercentage : this.defaultViewportPercentage;
        const intersectionOptions = {
            root: null,
            rootMargin: '0px',
            threshold: (viewportPercentage / 100)
        };

        const observer = new IntersectionObserver(carouselIntersectionhandler, intersectionOptions);
        const carousel = document.getElementById(this.data.carouselId);
        observer.observe(carousel);
    }

    static raiseEvent(eventType : string, event : CarouselEventPayload) {
        const viewedEvent = new CustomEvent(eventType, { "detail": event });
        window.dispatchEvent(viewedEvent);
    }

    static onClick(carouselId:any, productId: String, index: Number) {
        const carousel = window.__merchcarousels[carouselId];
        const eventPayload = new CarouselClickEventPayload(carousel.data, index, productId);
        CarouselComponent.raiseEvent(CAROUSEL_CLICKED_EVENT, eventPayload);
    }

    getHtml() {
        const { backgroundcolour, textcolour } = this.data;
        let textStyle = {};
        let backgroundStyle = {};
        let itemClassName = 'carousel__item--name',
            priceClassName = 'carousel__item--price';

        if (textcolour) {
            textStyle['color'] = textcolour;
        }
        if (backgroundcolour) {
            backgroundStyle['background-color'] = backgroundcolour;
            itemClassName += ' merchcarousel_custom-color';
            priceClassName += ' merchcarousel_custom-color';
        }

        /*
         * We have provided an initial value to reduce, so the index starts from 0. As we want slots to start from
         * 1 (a slot of 0 doesn't make much sense), we need to offset the index when we generate the product metadata
         */
        const itemsHTML = this.data.items.reduce((acc, item, index) => {
            return acc.add(
                $("<div>").addClass("carousel__item")
                    .click(() => window.__merchcarousels.CarouselComponent.onClick(this.data.carouselId, item.id, index + 1))
                .append(
                    $("<a>").attr("href", sanitizeUrl(item.pageUrl))
                        .append(
                            $("<div>").addClass("carousel__item--thumb")
                                .append(
                                    $("<img>").attr("src", sanitizeUrl(item.mainImage)).attr("alt", item.name).attr("title", item.name)
                                )
                        )
                        .append(
                            $("<div>").addClass(itemClassName).css(textStyle).text(item.name)
                        )
                        .append(
                            $("<div>").addClass(priceClassName).css(textStyle).text(this.data.currency + item.price)
                        )
                )
                .append(
                    $("<div>").attr("id", "data-merchandising-product-"+item.id).addClass("data-merchandising-product")
                        .attr(getProductMetadata(item, index + 1))
                ))
        }, $());

        this.metadata["data-merchandising-carousel-title"] = this.data.title || "";
        this.metadata["data-merchandising-carousel-name"] = this.data.name || "undefined";
        this.metadata["data-merchandising-carousel-strategyid"] = this.data.strategy || "undefined";
        this.metadata["data-merchandising-carousel-id"] = this.data.carouselId || "undefined";
        this.metadata["data-merchandising-carousel-slots"] = this.data.items.length;

        if (this.data && this.data.metadata) {
            for(let key in this.data.metadata) {
                const value = this.data.metadata[key];
                this.metadata['data-merchandising-carousel-'+ key] = value;
            }
        }

        return $("<div>").addClass("carousel__component merchandising-carousel").css(backgroundStyle)
                    .append(
                        $("<div>").addClass("carousel__component--headline").css(textStyle).text(this.data.title)
                    )
                    .append(
                        $('<div>').addClass("carousel__component--carousel js-merch-carousel").append(itemsHTML)
                    )
                    .append(
                        $("<div>").attr("id", "data-merchandising-carousel-"+this.data.carouselId).addClass("data-merchandising-carousel")
                            .attr(this.metadata)
                    );
    }

    static init() {
        log('init');
        const dataLayer = new DataLayer();
        dataLayer.initialize();
        Object.keys(window.__merchcarousels || {}).forEach((carouselId) => {
            const carouselSettings = window.__merchcarousels[carouselId];
            const { el, inited, data} = carouselSettings;

            if (!el || inited) {
                return;
            }

            const { numbertodisplay, title, name, currency, strategy, scroll, url, backgroundcolour, textcolour, viewportpercentage } = el.dataset;
            const numberToDisplay = +numbertodisplay;

            carouselSettings.inited = true;
            const service = new DataService(strategy, url, numberToDisplay);

            const consentReferenceCookie = getCookie(service._getSiteId()+"-consentReference");
            if (consentReferenceCookie) {
                service.setConsentReference(consentReferenceCookie);
            }

            window.addEventListener('profiletag_consentReferenceLoaded', (event: CustomEvent) => {
                log('profiletag_consentReferenceLoaded', event);
                service.setConsentReference(event.detail.consentReference);
            });

            log('inited');
            service.getProducts().then((data) => {
                log('data received', data);
                let items = null;
                if (data && data.products) {
                    items = numberToDisplay !== 0 ? data.products.slice(0, numberToDisplay) : data.products;
                }
                let metadata;
                if (data && data.metadata) {
                    metadata = data.metadata;
                }
                log('numbertodisplay', numberToDisplay);
                log('items', items);

                const carouselData = {
                    scroll,
                    items,
                    title,
                    name,
                    currency,
                    backgroundcolour,
                    textcolour,
                    strategy,
                    carouselId,
                    metadata,
                    viewportpercentage
                };
                if (items && items.length && $.fn['owlCarousel'] != null) {
                    new CarouselComponent({
                        el,
                        data: carouselData
                    } as any).render();
                    window.__merchcarousels[carouselId].data = carouselData;
                    log('rendered');
                }
            });
        });

        window.__merchcarousels = window.__merchcarousels || <any>{};
        window.__merchcarousels.CarouselComponent = CarouselComponent;
    }
}

function getProductMetadata(product: any, productIndex: number) {

    let productMetadata = {
        "data-merchandising-product-id": product.id,
        "data-merchandising-product-slot": productIndex
    };

    if (product.metadata) {
        for (let key in product.metadata) {
            const value = product.metadata[key];
            productMetadata['data-merchandising-product-'+ key] = value;
        }
    }
    return productMetadata;
}