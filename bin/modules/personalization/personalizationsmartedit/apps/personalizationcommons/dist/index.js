'use strict';

Object.defineProperty(exports, '__esModule', { value: true });

var common = require('@angular/common');
var core = require('@angular/core');
var forms = require('@angular/forms');
var datetimePicker = require('@fundamental-ngx/core/datetime-picker');
var form = require('@fundamental-ngx/core/form');
var layoutGrid = require('@fundamental-ngx/core/layout-grid');
var smarteditcommons = require('smarteditcommons');
var core$2 = require('@fundamental-ngx/core');
var core$1 = require('@ngx-translate/core');
var moment = require('moment');
var rxjs = require('rxjs');
var lodash = require('lodash');
var operators = require('rxjs/operators');

function _interopDefaultLegacy (e) { return e && typeof e === 'object' && 'default' in e ? e : { 'default': e }; }

function _interopNamespace(e) {
    if (e && e.__esModule) return e;
    var n = Object.create(null);
    if (e) {
        Object.keys(e).forEach(function (k) {
            if (k !== 'default') {
                var d = Object.getOwnPropertyDescriptor(e, k);
                Object.defineProperty(n, k, d.get ? d : {
                    enumerable: true,
                    get: function () { return e[k]; }
                });
            }
        });
    }
    n["default"] = e;
    return Object.freeze(n);
}

var moment__default = /*#__PURE__*/_interopDefaultLegacy(moment);
var lodash__namespace = /*#__PURE__*/_interopNamespace(lodash);

/******************************************************************************
Copyright (c) Microsoft Corporation.

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH
REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY
AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT,
INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM
LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR
OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
PERFORMANCE OF THIS SOFTWARE.
***************************************************************************** */

function __decorate(decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
}

function __param(paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
}

function __metadata(metadataKey, metadataValue) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(metadataKey, metadataValue);
}

function __awaiter(thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
const PERSONALIZATION_DATE_FORMATS = {
    SHORT_DATE_FORMAT: 'M/D/YY',
    MODEL_DATE_FORMAT: 'YYYY-MM-DDTHH:mm:SSZ'
};
const DATE_CONSTANTS = {
    ANGULAR_FORMAT: 'short',
    MOMENT_FORMAT: 'M/D/YY h:mm A',
    MOMENT_ISO: 'YYYY-MM-DDTHH:mm:00ZZ',
    ISO: 'yyyy-MM-ddTHH:mm:00Z'
};
exports.PERSONALIZATION_MODEL_STATUS_CODES = void 0;
(function (PERSONALIZATION_MODEL_STATUS_CODES) {
    PERSONALIZATION_MODEL_STATUS_CODES["ENABLED"] = "ENABLED";
    PERSONALIZATION_MODEL_STATUS_CODES["DISABLED"] = "DISABLED";
    PERSONALIZATION_MODEL_STATUS_CODES["DELETED"] = "DELETED";
})(exports.PERSONALIZATION_MODEL_STATUS_CODES || (exports.PERSONALIZATION_MODEL_STATUS_CODES = {}));
const PERSONALIZATION_VIEW_STATUS_MAPPING_CODES = {
    ALL: 'ALL',
    ENABLED: 'ENABLED',
    DISABLED: 'DISABLED'
};
const PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING = {
    0: {
        borderClass: 'personalizationsmarteditComponentSelected0',
        listClass: 'personalizationsmarteditComponentSelectedList0'
    },
    1: {
        borderClass: 'personalizationsmarteditComponentSelected1',
        listClass: 'personalizationsmarteditComponentSelectedList1'
    },
    2: {
        borderClass: 'personalizationsmarteditComponentSelected2',
        listClass: 'personalizationsmarteditComponentSelectedList2'
    },
    3: {
        borderClass: 'personalizationsmarteditComponentSelected3',
        listClass: 'personalizationsmarteditComponentSelectedList3'
    },
    4: {
        borderClass: 'personalizationsmarteditComponentSelected4',
        listClass: 'personalizationsmarteditComponentSelectedList4'
    },
    5: {
        borderClass: 'personalizationsmarteditComponentSelected5',
        listClass: 'personalizationsmarteditComponentSelectedList5'
    },
    6: {
        borderClass: 'personalizationsmarteditComponentSelected6',
        listClass: 'personalizationsmarteditComponentSelectedList6'
    },
    7: {
        borderClass: 'personalizationsmarteditComponentSelected7',
        listClass: 'personalizationsmarteditComponentSelectedList7'
    },
    8: {
        borderClass: 'personalizationsmarteditComponentSelected8',
        listClass: 'personalizationsmarteditComponentSelectedList8'
    },
    9: {
        borderClass: 'personalizationsmarteditComponentSelected9',
        listClass: 'personalizationsmarteditComponentSelectedList9'
    },
    10: {
        borderClass: 'personalizationsmarteditComponentSelected10',
        listClass: 'personalizationsmarteditComponentSelectedList10'
    },
    11: {
        borderClass: 'personalizationsmarteditComponentSelected11',
        listClass: 'personalizationsmarteditComponentSelectedList11'
    },
    12: {
        borderClass: 'personalizationsmarteditComponentSelected12',
        listClass: 'personalizationsmarteditComponentSelectedList12'
    },
    13: {
        borderClass: 'personalizationsmarteditComponentSelected13',
        listClass: 'personalizationsmarteditComponentSelectedList13'
    },
    14: {
        borderClass: 'personalizationsmarteditComponentSelected14',
        listClass: 'personalizationsmarteditComponentSelectedList14'
    }
};
const COMBINED_VIEW_TOOLBAR_ITEM_KEY = 'personalizationsmartedit.container.combinedview.toolbar';
const CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY = 'personalizationsmartedit.container.pagecustomizations.toolbar';
const PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES = {
    OLD: 'old',
    NEW: 'new',
    DELETE: 'delete',
    UPDATE: 'update'
};
const PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER = {
    ALL: 'all',
    ONLY_THIS_PAGE: 'onlythispage'
};
const PERSONALIZATION_CATALOG_FILTER = {
    ALL: 'all',
    CURRENT: 'current',
    PARENTS: 'parents'
};
const COMPONENT_CONTAINER_TYPE = 'CxCmsComponentContainer';
const CONTAINER_SOURCE_ID_ATTR = 'data-smartedit-container-source-id';
const CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS = {
    BASIC_INFO_TAB_NAME: 'basicinfotab',
    BASIC_INFO_TAB_FORM_NAME: 'form.basicinfotab',
    TARGET_GROUP_TAB_NAME: 'targetgrptab',
    TARGET_GROUP_TAB_FORM_NAME: 'form.targetgrptab'
};
const CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS = {
    CONFIRM_OK: 'confirmOk',
    CONFIRM_CANCEL: 'confirmCancel',
    CONFIRM_NEXT: 'confirmNext'
};
const CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY = {
    CRITERIA_AND: 'AND',
    CRITERIA_OR: 'OR'
};

/* @ngInject */ exports.PersonalizationsmarteditDateUtils = class /* @ngInject */ PersonalizationsmarteditDateUtils {
    constructor(translateService) {
        this.translateService = translateService;
    }
    formatDate(dateStr, format) {
        format = format || DATE_CONSTANTS.MOMENT_FORMAT;
        if (dateStr) {
            if (dateStr.match &&
                dateStr.match(/^(\d{4})\-(\d{2})\-(\d{2})T(\d{2}):(\d{2}):(\d{2})(\+|\-)(\d{4})$/)) {
                dateStr = `${dateStr.slice(0, -2)}:${dateStr.slice(-2)}`;
            }
            return moment__default["default"](new Date(dateStr)).format(format);
        }
        else {
            return '';
        }
    }
    formatDateWithMessage(dateStr, format) {
        format = format || PERSONALIZATION_DATE_FORMATS.SHORT_DATE_FORMAT;
        if (dateStr) {
            return this.formatDate(dateStr, format);
        }
        else {
            return this.translateService.instant('personalization.toolbar.pagecustomizations.nodatespecified');
        }
    }
    isDateInThePast(modelValue) {
        if (smarteditcommons.stringUtils.isBlank(modelValue)) {
            return false;
        }
        else {
            return moment__default["default"](modelValue, DATE_CONSTANTS.MOMENT_FORMAT).isBefore();
        }
    }
    isDateValidOrEmpty(modelValue) {
        return smarteditcommons.stringUtils.isBlank(modelValue) || this.isDateValid(modelValue);
    }
    isDateValid(modelValue) {
        return moment__default["default"](modelValue, DATE_CONSTANTS.MOMENT_FORMAT).isValid();
    }
    isDateRangeValid(startDate, endDate) {
        if (smarteditcommons.stringUtils.isBlank(startDate) || smarteditcommons.stringUtils.isBlank(endDate)) {
            return true;
        }
        else {
            return moment__default["default"](new Date(startDate)).isSameOrBefore(moment__default["default"](new Date(endDate)));
        }
    }
    isDateStrFormatValid(dateStr, format) {
        format = format || DATE_CONSTANTS.MOMENT_FORMAT;
        if (smarteditcommons.stringUtils.isBlank(dateStr)) {
            return false;
        }
        else {
            return moment__default["default"](dateStr, format, true).isValid();
        }
    }
};
/* @ngInject */ exports.PersonalizationsmarteditDateUtils = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [core$1.TranslateService])
], /* @ngInject */ exports.PersonalizationsmarteditDateUtils);

window.__smartedit__.addDecoratorPayload("Component", "DatetimePickerRangeComponent", {
    selector: 'datetime-picker-range',
    template: `<fd-layout-grid class="pe-datetime-range"><div [fdLayoutGridCol]="6" class="pe-datetime-range__from" [ngClass]="{ 'has-error': !isFromDateValid }"><label for="pe-start-date" fd-form-label class="perso__datetimepicker__label">{{ 'personalization.modal.customizationvariationmanagement.basicinformationtab.details.startdate' | translate }}</label><se-help><span translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.startdate.help"></span></se-help><fd-datetime-picker [allowNull]="true" id="pe-start-date" name="pe-dateFrom" [placeholder]="placeholderText | translate" [disabled]="!isEditable" [(ngModel)]="fdDateFrom" (ngModelChange)="dateFromOnChange(fdDateFrom)"></fd-datetime-picker><span *ngIf="!isFromDateValid" class="pe-datetime__error-msg pe-datetime__msg" translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.wrongdateformatfrom.description"></span></div><div [fdLayoutGridCol]="6" class="pe-datetime-range__to" [ngClass]="{ 'has-error': !isToDateValid, 'has-warning': isEndDateInThePast }"><label for="pe-end-date" fd-form-label class="perso__datetimepicker__label">{{ 'personalization.modal.customizationvariationmanagement.basicinformationtab.details.enddate' | translate }}</label><se-help><span translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.enddate.help"></span></se-help><fd-datetime-picker [allowNull]="true" id="pe-end-date" name="pe-dateTo" [placeholder]="placeholderText | translate" [disabled]="!isEditable" [(ngModel)]="fdDateTo" (ngModelChange)="dateToOnChange(fdDateTo)"></fd-datetime-picker><span *ngIf="!isToDateValid" class="pe-datetime__error-msg pe-datetime__msg" translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.wrongdateformatfrom.description"></span> <span *ngIf="isEndDateInThePast" class="help-block pe-datetime__warning-msg pe-datetime__msg"><span translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.enddateinthepast.description"></span></span></div></fd-layout-grid>`,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
exports.DatetimePickerRangeComponent = class DatetimePickerRangeComponent {
    constructor(dateUtil, cdr) {
        this.dateUtil = dateUtil;
        this.cdr = cdr;
        this.placeholderText = 'personalization.commons.datetimepicker.placeholder';
        this.dateFromChange = new core.EventEmitter();
        this.dateToChange = new core.EventEmitter();
        this.isFromDateValid = true;
        this.isToDateValid = true;
        this.isEndDateInThePast = false;
    }
    ngAfterViewInit() {
        this._initDateTime();
    }
    dateFromOnChange(fdDateFrom) {
        if (!!fdDateFrom) {
            this.isFromDateValid = fdDateFrom.isDateValid();
            this.dateFrom = this.dateUtil.formatDate(fdDateFrom.toString(), undefined);
        }
        else {
            this.isFromDateValid = true;
            this.dateFrom = '';
        }
        this.dateFromChange.emit(this.dateFrom);
    }
    dateToOnChange(fdDateTo) {
        if (!!fdDateTo) {
            this.isToDateValid = fdDateTo.isDateValid();
            this.dateTo = this.dateUtil.formatDate(fdDateTo.toString(), undefined);
        }
        else {
            this.isToDateValid = true;
            this.dateTo = '';
        }
        this.isEndDateInThePast = this.dateUtil.isDateInThePast(this.dateTo);
        this.dateToChange.emit(this.dateTo);
    }
    _initDateTime() {
        if (!!this.dateFrom) {
            this.fdDateFrom = core$2.FdDate.getFdDateByDate(new Date(this.dateFrom));
        }
        else {
            this.fdDateFrom = core$2.FdDate.getToday();
            this.dateFromOnChange(this.fdDateFrom);
        }
        if (!!this.dateTo) {
            this.fdDateTo = core$2.FdDate.getFdDateByDate(new Date(this.dateTo));
        }
        else {
            this.fdDateTo = core$2.FdDate.getToday();
            this.dateToOnChange(this.fdDateTo);
        }
        this.cdr.detectChanges();
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.DatetimePickerRangeComponent.prototype, "dateFrom", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.DatetimePickerRangeComponent.prototype, "dateTo", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], exports.DatetimePickerRangeComponent.prototype, "isEditable", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.DatetimePickerRangeComponent.prototype, "dateFromChange", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], exports.DatetimePickerRangeComponent.prototype, "dateToChange", void 0);
exports.DatetimePickerRangeComponent = __decorate([
    core.Component({
        selector: 'datetime-picker-range',
        template: `<fd-layout-grid class="pe-datetime-range"><div [fdLayoutGridCol]="6" class="pe-datetime-range__from" [ngClass]="{ 'has-error': !isFromDateValid }"><label for="pe-start-date" fd-form-label class="perso__datetimepicker__label">{{ 'personalization.modal.customizationvariationmanagement.basicinformationtab.details.startdate' | translate }}</label><se-help><span translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.startdate.help"></span></se-help><fd-datetime-picker [allowNull]="true" id="pe-start-date" name="pe-dateFrom" [placeholder]="placeholderText | translate" [disabled]="!isEditable" [(ngModel)]="fdDateFrom" (ngModelChange)="dateFromOnChange(fdDateFrom)"></fd-datetime-picker><span *ngIf="!isFromDateValid" class="pe-datetime__error-msg pe-datetime__msg" translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.wrongdateformatfrom.description"></span></div><div [fdLayoutGridCol]="6" class="pe-datetime-range__to" [ngClass]="{ 'has-error': !isToDateValid, 'has-warning': isEndDateInThePast }"><label for="pe-end-date" fd-form-label class="perso__datetimepicker__label">{{ 'personalization.modal.customizationvariationmanagement.basicinformationtab.details.enddate' | translate }}</label><se-help><span translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.enddate.help"></span></se-help><fd-datetime-picker [allowNull]="true" id="pe-end-date" name="pe-dateTo" [placeholder]="placeholderText | translate" [disabled]="!isEditable" [(ngModel)]="fdDateTo" (ngModelChange)="dateToOnChange(fdDateTo)"></fd-datetime-picker><span *ngIf="!isToDateValid" class="pe-datetime__error-msg pe-datetime__msg" translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.wrongdateformatfrom.description"></span> <span *ngIf="isEndDateInThePast" class="help-block pe-datetime__warning-msg pe-datetime__msg"><span translate="personalization.modal.customizationvariationmanagement.basicinformationtab.details.enddateinthepast.description"></span></span></div></fd-layout-grid>`,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __metadata("design:paramtypes", [exports.PersonalizationsmarteditDateUtils,
        core.ChangeDetectorRef])
], exports.DatetimePickerRangeComponent);

exports.DatetimePickerRangeModule = class DatetimePickerRangeModule {
};
exports.DatetimePickerRangeModule = __decorate([
    core.NgModule({
        imports: [
            common.CommonModule,
            forms.FormsModule,
            form.FormModule,
            smarteditcommons.SeTranslationModule.forChild(),
            smarteditcommons.HelpModule,
            datetimePicker.DatetimePickerModule,
            layoutGrid.LayoutGridModule
        ],
        declarations: [exports.DatetimePickerRangeComponent],
        entryComponents: [exports.DatetimePickerRangeComponent],
        exports: [exports.DatetimePickerRangeComponent]
    })
], exports.DatetimePickerRangeModule);

exports.PersonalizationInfiniteScrollDirective = class PersonalizationInfiniteScrollDirective {
    constructor(element) {
        this.element = element;
        this.scrollPercent = 75;
        this.onScrollAction = new core.EventEmitter();
    }
    ngOnInit() {
        this.scrollEvent = rxjs.fromEvent(this.element.nativeElement, 'scroll');
        this.subscription = this.scrollEvent.subscribe((e) => {
            if ((e.target.scrollTop + e.target.offsetHeight) / e.target.scrollHeight >
                this.scrollPercent / 100) {
                this.onScrollAction.emit(null);
            }
        });
    }
    ngOnDestroy() {
        if (this.subscription) {
            this.subscription.unsubscribe();
        }
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.PersonalizationInfiniteScrollDirective.prototype, "scrollPercent", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", Object)
], exports.PersonalizationInfiniteScrollDirective.prototype, "onScrollAction", void 0);
exports.PersonalizationInfiniteScrollDirective = __decorate([
    core.Directive({
        selector: '[persoInfiniteScroll]'
    }),
    __param(0, core.Inject(core.ElementRef)),
    __metadata("design:paramtypes", [core.ElementRef])
], exports.PersonalizationInfiniteScrollDirective);

window.__smartedit__.addDecoratorPayload("Component", "PersonalizationsmarteditInfiniteScrollingComponent", {
    selector: 'personalization-infinite-scrolling',
    template: `<div [ngClass]="dropDownContainerClass" persoInfiniteScroll (onScrollAction)="nextPage()" [scrollPercent]="distance" *ngIf="initiated"><div [ngClass]="dropDownClass"><ng-content></ng-content></div></div>`
});
exports.PersonalizationsmarteditInfiniteScrollingComponent = class PersonalizationsmarteditInfiniteScrollingComponent {
    constructor() {
        this.distance = 80;
        this.initiated = false;
    }
    ngOnInit() {
        this.init();
    }
    ngOnChanges() {
        this.init();
    }
    nextPage() {
        this.fetchPage();
    }
    init() {
        const wasInitiated = this.initiated;
        this.initiated = true;
        if (wasInitiated) {
            this.nextPage();
        }
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.PersonalizationsmarteditInfiniteScrollingComponent.prototype, "dropDownContainerClass", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", String)
], exports.PersonalizationsmarteditInfiniteScrollingComponent.prototype, "dropDownClass", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.PersonalizationsmarteditInfiniteScrollingComponent.prototype, "distance", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Function)
], exports.PersonalizationsmarteditInfiniteScrollingComponent.prototype, "fetchPage", void 0);
exports.PersonalizationsmarteditInfiniteScrollingComponent = __decorate([
    core.Component({
        selector: 'personalization-infinite-scrolling',
        template: `<div [ngClass]="dropDownContainerClass" persoInfiniteScroll (onScrollAction)="nextPage()" [scrollPercent]="distance" *ngIf="initiated"><div [ngClass]="dropDownClass"><ng-content></ng-content></div></div>`
    })
], exports.PersonalizationsmarteditInfiniteScrollingComponent);

window.__smartedit__.addDecoratorPayload("Component", "PersonalizationPreventParentScrollComponent", {
    selector: 'personalization-prevent-parent-scroll',
    template: `<div persoPreventParentScroll><ng-content></ng-content></div>`
});
exports.PersonalizationPreventParentScrollComponent = class PersonalizationPreventParentScrollComponent {
};
exports.PersonalizationPreventParentScrollComponent = __decorate([
    core.Component({
        selector: 'personalization-prevent-parent-scroll',
        template: `<div persoPreventParentScroll><ng-content></ng-content></div>`
    })
], exports.PersonalizationPreventParentScrollComponent);

exports.PersonalizationPreventParentScrollDirective = class PersonalizationPreventParentScrollDirective {
    constructor(element) {
        this.element = element;
        this.mouseWheelEventHandler = (event) => this.onMouseWheel(event);
    }
    ngOnInit() {
        const element = this.element.nativeElement;
        element.addEventListener('mousewheel', this.mouseWheelEventHandler);
        element.addEventListener('DOMMouseScroll', this.mouseWheelEventHandler);
    }
    ngOnDestroy() {
        const element = this.element.nativeElement;
        element.removeEventListener('mousewheel', this.mouseWheelEventHandler);
        element.removeEventListener('DOMMouseScroll', this.mouseWheelEventHandler);
    }
    onMouseWheel(event) {
        const element = this.element.nativeElement;
        const originalEventCondition = event.originalEvent &&
            (event.originalEvent.wheelDeltaY || event.originalEvent.wheelDelta);
        const IEEventCondition = -(event.deltaY || event.delta) || 0;
        element.parentElement.parentElement.parentElement.scrollTop -=
            event.wheelDeltaY || originalEventCondition || event.wheelDelta || IEEventCondition;
        event.stopPropagation();
        event.preventDefault();
        event.returnValue = false;
    }
};
exports.PersonalizationPreventParentScrollDirective = __decorate([
    core.Directive({
        selector: '[persoPreventParentScroll]'
    }),
    __param(0, core.Inject(core.ElementRef)),
    __metadata("design:paramtypes", [core.ElementRef])
], exports.PersonalizationPreventParentScrollDirective);

window.__smartedit__.addDecoratorPayload("Component", "ScrollZoneComponent", {
    selector: 'personalizationsmartedit-scroll-zone',
    template: `<ng-template #scrollZoneBody><personalizationsmartedit-scroll-zone-body-container><personalizationsmartedit-scroll-zone-body [hidden]="!scrollZoneTop" [isTransparent]="isTransparent" [additionalClasses]="['perso__scrollzone--top']" (scrollZoneBodyMouseenter)="onScrollTopMouseenter()" (scrollZoneBodyMouseleave)="stopScroll()"></personalizationsmartedit-scroll-zone-body><personalizationsmartedit-scroll-zone-body [hidden]="!scrollZoneBottom" [isTransparent]="isTransparent" [additionalClasses]="['perso__scrollzone--bottom']" (scrollZoneBodyMouseenter)="onScrollBottomMouseenter()" (scrollZoneBodyMouseleave)="stopScroll()"></personalizationsmartedit-scroll-zone-body></personalizationsmartedit-scroll-zone-body-container></ng-template>`
});
exports.ScrollZoneComponent = class ScrollZoneComponent {
    constructor(viewContainerRef, document) {
        this.viewContainerRef = viewContainerRef;
        this.document = document;
        this.scrollZoneTop = false;
        this.scrollZoneBottom = false;
        this.scrollZoneVisible = false;
        this.isTransparent = false;
    }
    ngAfterViewInit() {
        if (this.scrollZoneVisible) {
            this.createScrollZoneBody();
        }
    }
    ngOnChanges(changes) {
        const scrollZoneVisibleChange = changes.scrollZoneVisible;
        if (scrollZoneVisibleChange && !scrollZoneVisibleChange.firstChange) {
            if (scrollZoneVisibleChange.currentValue) {
                this.createScrollZoneBody();
            }
            else {
                this.destroyScrollZoneBody();
            }
        }
    }
    ngOnDestroy() {
        this.destroyScrollZoneBody();
    }
    onScrollTopMouseenter() {
        this.isScrolling = true;
        this.scrollTop();
    }
    onScrollBottomMouseenter() {
        this.isScrolling = true;
        this.scrollBottom();
    }
    stopScroll() {
        this.isScrolling = false;
        clearTimeout(this.scrollZoneTopTimeout);
        clearTimeout(this.scrollZoneBottomTimeout);
    }
    scrollTop() {
        if (!this.isScrolling) {
            clearTimeout(this.scrollZoneTopTimeout);
            return;
        }
        this.scrollZoneTop = this.elementToScroll.scrollTop() <= 2 ? false : true;
        this.scrollZoneBottom = true;
        this.elementToScroll.scrollTop(this.elementToScroll.scrollTop() - 15);
        this.scrollZoneTopTimeout = setTimeout(() => {
            this.scrollTop();
        }, 100);
    }
    scrollBottom() {
        if (!this.isScrolling) {
            clearTimeout(this.scrollZoneBottomTimeout);
            return;
        }
        this.scrollZoneTop = true;
        const heightVisibleFromTop = this.elementToScroll.get(0).scrollHeight - this.elementToScroll.scrollTop();
        this.scrollZoneBottom =
            Math.abs(heightVisibleFromTop - this.elementToScroll.outerHeight()) < 2 ? false : true;
        this.elementToScroll.scrollTop(this.elementToScroll.scrollTop() + 15);
        this.scrollZoneBottomTimeout = setTimeout(() => {
            this.scrollBottom();
        }, 100);
    }
    createScrollZoneBody() {
        if (this.viewRef) {
            return;
        }
        this.viewRef = this.viewContainerRef.createEmbeddedView(this.scrollZoneBody);
        const scrollZoneBodyDom = this.viewRef.rootNodes[0];
        this.document.body.appendChild(scrollZoneBodyDom);
        this.scrollZoneTop = true;
        this.scrollZoneBottom = true;
    }
    destroyScrollZoneBody() {
        if (!this.viewRef) {
            return;
        }
        this.viewRef.destroy();
        this.viewRef = null;
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], exports.ScrollZoneComponent.prototype, "scrollZoneVisible", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], exports.ScrollZoneComponent.prototype, "isTransparent", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Object)
], exports.ScrollZoneComponent.prototype, "elementToScroll", void 0);
__decorate([
    core.ViewChild('scrollZoneBody', { static: true }),
    __metadata("design:type", core.TemplateRef)
], exports.ScrollZoneComponent.prototype, "scrollZoneBody", void 0);
exports.ScrollZoneComponent = __decorate([
    core.Component({
        selector: 'personalizationsmartedit-scroll-zone',
        template: `<ng-template #scrollZoneBody><personalizationsmartedit-scroll-zone-body-container><personalizationsmartedit-scroll-zone-body [hidden]="!scrollZoneTop" [isTransparent]="isTransparent" [additionalClasses]="['perso__scrollzone--top']" (scrollZoneBodyMouseenter)="onScrollTopMouseenter()" (scrollZoneBodyMouseleave)="stopScroll()"></personalizationsmartedit-scroll-zone-body><personalizationsmartedit-scroll-zone-body [hidden]="!scrollZoneBottom" [isTransparent]="isTransparent" [additionalClasses]="['perso__scrollzone--bottom']" (scrollZoneBodyMouseenter)="onScrollBottomMouseenter()" (scrollZoneBodyMouseleave)="stopScroll()"></personalizationsmartedit-scroll-zone-body></personalizationsmartedit-scroll-zone-body-container></ng-template>`
    }),
    __param(1, core.Inject(common.DOCUMENT)),
    __metadata("design:paramtypes", [core.ViewContainerRef,
        Document])
], exports.ScrollZoneComponent);

window.__smartedit__.addDecoratorPayload("Component", "ScrollZoneBodyComponent", {
    selector: 'personalizationsmartedit-scroll-zone-body',
    template: `<div class="perso__scrollzone" [ngClass]="cssClasses" (mouseenter)="onMouseenter()" (mouseleave)="onMouseleave()"></div>`,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let ScrollZoneBodyComponent = class ScrollZoneBodyComponent {
    constructor() {
        this.additionalClasses = [];
        this.isTransparent = false;
        this.scrollZoneBodyMouseenter = new core.EventEmitter();
        this.scrollZoneBodyMouseleave = new core.EventEmitter();
        this.cssClasses = [];
    }
    ngOnChanges() {
        this.cssClasses = this.getCssClasses(this.isTransparent, this.additionalClasses || []);
    }
    onMouseenter() {
        this.scrollZoneBodyMouseenter.emit();
    }
    onMouseleave() {
        this.scrollZoneBodyMouseleave.emit();
    }
    getCssClasses(isTransparent, additionalClasses) {
        const transparencyClass = isTransparent
            ? 'perso__scrollzone--transparent'
            : 'perso__scrollzone--normal';
        return [transparencyClass, ...additionalClasses];
    }
};
__decorate([
    core.Input(),
    __metadata("design:type", Array)
], ScrollZoneBodyComponent.prototype, "additionalClasses", void 0);
__decorate([
    core.Input(),
    __metadata("design:type", Boolean)
], ScrollZoneBodyComponent.prototype, "isTransparent", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], ScrollZoneBodyComponent.prototype, "scrollZoneBodyMouseenter", void 0);
__decorate([
    core.Output(),
    __metadata("design:type", core.EventEmitter)
], ScrollZoneBodyComponent.prototype, "scrollZoneBodyMouseleave", void 0);
ScrollZoneBodyComponent = __decorate([
    core.Component({
        selector: 'personalizationsmartedit-scroll-zone-body',
        template: `<div class="perso__scrollzone" [ngClass]="cssClasses" (mouseenter)="onMouseenter()" (mouseleave)="onMouseleave()"></div>`,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    }),
    __metadata("design:paramtypes", [])
], ScrollZoneBodyComponent);

window.__smartedit__.addDecoratorPayload("Component", "ScrollZoneBodyContainerComponent", {
    selector: 'personalizationsmartedit-scroll-zone-body-container',
    template: `<ng-content></ng-content>`,
    changeDetection: core.ChangeDetectionStrategy.OnPush
});
let ScrollZoneBodyContainerComponent = class ScrollZoneBodyContainerComponent {
};
ScrollZoneBodyContainerComponent = __decorate([
    core.Component({
        selector: 'personalizationsmartedit-scroll-zone-body-container',
        template: `<ng-content></ng-content>`,
        changeDetection: core.ChangeDetectionStrategy.OnPush
    })
], ScrollZoneBodyContainerComponent);

exports.ScrollZoneModule = class ScrollZoneModule {
};
exports.ScrollZoneModule = __decorate([
    core.NgModule({
        imports: [common.CommonModule, smarteditcommons.TranslationModule.forChild()],
        providers: [],
        declarations: [exports.ScrollZoneComponent, ScrollZoneBodyContainerComponent, ScrollZoneBodyComponent],
        entryComponents: [exports.ScrollZoneComponent],
        exports: [ScrollZoneBodyComponent, exports.ScrollZoneComponent]
    })
], exports.ScrollZoneModule);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class IPersonalizationsmarteditContextMenuServiceProxy {
    openDeleteAction(config) {
        'proxyFunction';
        return undefined;
    }
    openAddAction(config) {
        'proxyFunction';
        return undefined;
    }
    openEditAction(config) {
        'proxyFunction';
        return undefined;
    }
    openEditComponentAction(config) {
        'proxyFunction';
        return undefined;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class IPersonalizationsmarteditContextServiceProxy {
    setPersonalization(personalization) {
        'proxyFunction';
        return undefined;
    }
    setCustomize(customize) {
        'proxyFunction';
        return undefined;
    }
    setCombinedView(combinedView) {
        'proxyFunction';
        return undefined;
    }
    setSeData(seData) {
        'proxyFunction';
        return undefined;
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class PaginationHelper {
    constructor(initialData = {}) {
        this.count = initialData.count || 0;
        this.page = initialData.page || 0;
        this.totalCount = initialData.totalCount || 0;
        this.totalPages = initialData.totalPages || 0;
    }
    reset() {
        this.count = 50;
        this.page = -1;
        this.totalPages = 1;
        this.totalCount = 0;
    }
    getCount() {
        return this.count;
    }
    getPage() {
        return this.page;
    }
    getTotalCount() {
        return this.totalCount;
    }
    getTotalPages() {
        return this.totalPages;
    }
    isLastPage() {
        return this.getPage() === this.getTotalPages();
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class PersonalizationsmarteditCommerceCustomizationService {
    constructor() {
        this.nonCommerceActionTypes = ['cxCmsActionData'];
        this.types = [];
    }
    isNonCommerceAction(action) {
        return this.nonCommerceActionTypes.some((val) => val === action.type);
    }
    isCommerceAction(action) {
        return !this.isNonCommerceAction(action);
    }
    isTypeEnabled(type, seConfigurationData) {
        return (seConfigurationData !== undefined &&
            seConfigurationData !== null &&
            seConfigurationData[type.confProperty] === true);
    }
    registerType(item) {
        const type = item.type;
        let exists = false;
        this.types.forEach((val) => {
            if (val.type === type) {
                exists = true;
            }
        });
        if (!exists) {
            this.types.push(item);
        }
    }
    getAvailableTypes(seConfigurationData) {
        return this.types.filter((item) => this.isTypeEnabled(item, seConfigurationData));
    }
    isCommerceCustomizationEnabled(seConfigurationData) {
        const at = this.getAvailableTypes(seConfigurationData);
        return at.length > 0;
    }
    getNonCommerceActionsCount(variation) {
        return (variation.actions || []).filter(this.isNonCommerceAction, this).length;
    }
    getCommerceActionsCountMap(variation) {
        const result = {};
        (variation.actions || []).filter(this.isCommerceAction, this).forEach((action) => {
            const typeKey = action.type.toLowerCase();
            let count = result[typeKey];
            if (count === undefined) {
                count = 1;
            }
            else {
                count += 1;
            }
            result[typeKey] = count;
        });
        return result;
    }
    getCommerceActionsCount(variation) {
        return (variation.actions || []).filter(this.isCommerceAction, this).length;
    }
}

/* @ngInject */ exports.PersonalizationsmarteditMessageHandler = class /* @ngInject */ PersonalizationsmarteditMessageHandler {
    constructor(alertService) {
        this.alertService = alertService;
    }
    sendInformation(informationMessage) {
        this.alertService.showInfo(informationMessage);
    }
    sendError(errorMessage) {
        this.alertService.showDanger(errorMessage);
    }
    sendWarning(warningMessage) {
        this.alertService.showWarning(warningMessage);
    }
    sendSuccess(successMessage) {
        this.alertService.showSuccess(successMessage);
    }
};
/* @ngInject */ exports.PersonalizationsmarteditMessageHandler = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.IAlertService])
], /* @ngInject */ exports.PersonalizationsmarteditMessageHandler);

/* @ngInject */ exports.PersonalizationsmarteditUtils = class /* @ngInject */ PersonalizationsmarteditUtils {
    constructor(translateService, l10nPipe, catalogService) {
        this.translateService = translateService;
        this.l10nPipe = l10nPipe;
        this.catalogService = catalogService;
    }
    pushToArrayIfValueExists(array, sKey, sValue) {
        if (sValue) {
            array.push({
                key: sKey,
                value: sValue
            });
        }
    }
    getVariationCodes(variations) {
        if (typeof variations === undefined || variations === null) {
            return [];
        }
        const allVariationsCodes = variations
            .map((elem) => elem.code)
            .filter((elem) => typeof elem !== 'undefined');
        return allVariationsCodes;
    }
    getVariationKey(customizationId, variations) {
        if (customizationId === undefined || variations === undefined) {
            return [];
        }
        const allVariationsKeys = variations.map((variation) => ({
            variationCode: variation.code,
            customizationCode: customizationId,
            catalog: variation.catalog,
            catalogVersion: variation.catalogVersion
        }));
        return allVariationsKeys;
    }
    getSegmentTriggerForVariation(variation) {
        const triggers = variation.triggers || [];
        const segmentTriggerArr = triggers.filter((trigger) => trigger.type === 'segmentTriggerData');
        if (segmentTriggerArr.length === 0) {
            return {};
        }
        return segmentTriggerArr[0];
    }
    isPersonalizationItemEnabled(item) {
        return item.status === exports.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED;
    }
    getEnablementTextForCustomization(customization, keyPrefix) {
        keyPrefix = keyPrefix || 'personalization';
        if (this.isPersonalizationItemEnabled(customization)) {
            return this.translateService.instant(keyPrefix + '.customization.enabled');
        }
        else {
            return this.translateService.instant(keyPrefix + '.customization.disabled');
        }
    }
    getEnablementTextForVariation(variation, keyPrefix) {
        keyPrefix = keyPrefix || 'personalization';
        if (this.isPersonalizationItemEnabled(variation)) {
            return this.translateService.instant(keyPrefix + '.variation.enabled');
        }
        else {
            return this.translateService.instant(keyPrefix + '.variation.disabled');
        }
    }
    getEnablementActionTextForVariation(variation, keyPrefix) {
        keyPrefix = keyPrefix || 'personalization';
        if (this.isPersonalizationItemEnabled(variation)) {
            return this.translateService.instant(keyPrefix + '.variation.options.disable');
        }
        else {
            return this.translateService.instant(keyPrefix + '.variation.options.enable');
        }
    }
    getActivityStateForCustomization(customization) {
        if (customization.status === exports.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED) {
            const startDate = new Date(customization.enabledStartDate);
            const endDate = new Date(customization.enabledEndDate);
            const startDateIsValid = moment__default["default"](startDate).isValid();
            const endDateIsValid = moment__default["default"](endDate).isValid();
            if ((!startDateIsValid && !endDateIsValid) ||
                (startDateIsValid && !endDateIsValid && moment__default["default"]().isAfter(startDate, 'minute')) ||
                (!startDateIsValid && endDateIsValid && moment__default["default"]().isBefore(endDate, 'minute')) ||
                moment__default["default"]().isBetween(startDate, endDate, 'minute', '[]')) {
                return 'perso__status--enabled';
            }
            else {
                return 'perso__status--ignore';
            }
        }
        else {
            return 'perso__status--disabled';
        }
    }
    getActivityStateForVariation(customization, variation) {
        if (variation.enabled) {
            return this.getActivityStateForCustomization(customization);
        }
        else {
            return 'perso__status--disabled';
        }
    }
    isItemVisible(item) {
        return item.status !== exports.PERSONALIZATION_MODEL_STATUS_CODES.DELETED;
    }
    getVisibleItems(items) {
        return items.filter((item) => this.isItemVisible(item));
    }
    getValidRank(items, item, increaseValue) {
        const from = items.indexOf(item);
        const delta = increaseValue < 0 ? -1 : 1;
        let increase = from + increaseValue;
        while (increase >= 0 && increase < items.length && !this.isItemVisible(items[increase])) {
            increase += delta;
        }
        increase = increase >= items.length ? items.length - 1 : increase;
        increase = increase < 0 ? 0 : increase;
        return items[increase].rank;
    }
    getStatusesMapping() {
        const statusesMapping = [];
        statusesMapping.push({
            code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ALL,
            text: 'personalization.context.status.all',
            modelStatuses: [
                exports.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED,
                exports.PERSONALIZATION_MODEL_STATUS_CODES.DISABLED
            ]
        });
        statusesMapping.push({
            code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.ENABLED,
            text: 'personalization.context.status.enabled',
            modelStatuses: [exports.PERSONALIZATION_MODEL_STATUS_CODES.ENABLED]
        });
        statusesMapping.push({
            code: PERSONALIZATION_VIEW_STATUS_MAPPING_CODES.DISABLED,
            text: 'personalization.context.status.disabled',
            modelStatuses: [exports.PERSONALIZATION_MODEL_STATUS_CODES.DISABLED]
        });
        return statusesMapping;
    }
    getClassForElement(index) {
        const wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
        return PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING[wrappedIndex].listClass;
    }
    getLetterForElement(index) {
        const wrappedIndex = index % Object.keys(PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING).length;
        return String.fromCharCode('a'.charCodeAt(0) + wrappedIndex).toUpperCase();
    }
    getCommerceCustomizationTooltip(variation, prefix, suffix) {
        prefix = prefix || '';
        suffix = suffix || '\n';
        let result = '';
        lodash__namespace.forEach(variation.commerceCustomizations, (propertyValue, propertyKey) => {
            result +=
                prefix +
                    this.translateService.instant('personalization.modal.manager.commercecustomization.' + propertyKey) +
                    ': ' +
                    propertyValue +
                    suffix;
        });
        return result;
    }
    getCommerceCustomizationTooltipHTML(variation) {
        return this.getCommerceCustomizationTooltip(variation, '<div>', '</div>');
    }
    isItemFromCurrentCatalog(item, seData) {
        const cd = seData.seExperienceData.catalogDescriptor;
        return item.catalog === cd.catalogId && item.catalogVersion === cd.catalogVersion;
    }
    hasCommerceActions(variation) {
        return variation.numberOfCommerceActions > 0;
    }
    getCatalogVersionNameByUuid(catalogVersionUuid) {
        return __awaiter(this, void 0, void 0, function* () {
            const catalogVersion = yield this.catalogService.getCatalogVersionByUuid(catalogVersionUuid);
            let catalogVersionName = yield this.l10nPipe
                .transform(catalogVersion.catalogName)
                .pipe(operators.take(1))
                .toPromise();
            catalogVersionName = catalogVersionName + ' (' + catalogVersion.version + ')';
            return catalogVersionName;
        });
    }
    getAndSetCatalogVersionNameL10N(customization) {
        return __awaiter(this, void 0, void 0, function* () {
            customization.catalogVersionNameL10N = yield this.getCatalogVersionNameByUuid(customization.catalog + '/' + customization.catalogVersion);
        });
    }
    uniqueArray(array1, array2, fieldName) {
        fieldName = fieldName || 'code';
        array2.forEach((instance) => {
            if (!array1.some((el) => el[fieldName] === instance[fieldName])) {
                array1.push(instance);
            }
        });
        // eslint-disable-next-line @typescript-eslint/no-unsafe-return
        return array1;
    }
};
/* @ngInject */ exports.PersonalizationsmarteditUtils = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [core$1.TranslateService,
        smarteditcommons.L10nPipe,
        smarteditcommons.ICatalogService])
], /* @ngInject */ exports.PersonalizationsmarteditUtils);

class Customize {
    constructor() {
        this.enabled = false;
        this.selectedCustomization = null;
        this.selectedVariations = null;
        this.selectedComponents = null;
    }
}

class CombinedView {
    constructor() {
        this.enabled = false;
        this.selectedItems = null;
        this.customize = new Customize();
    }
}

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
class Personalization {
    constructor() {
        this.enabled = false;
    }
}

class SeData {
    constructor() {
        this.pageId = null;
        this.seExperienceData = null;
        this.seConfigurationData = null;
    }
}

/* @ngInject */ exports.PersonalizationsmarteditContextUtils = class /* @ngInject */ PersonalizationsmarteditContextUtils {
    getContextObject() {
        return {
            personalization: new Personalization(),
            customize: new Customize(),
            combinedView: new CombinedView(),
            seData: new SeData()
        };
    }
    clearCustomizeContext(contextService) {
        const customize = contextService.getCustomize();
        customize.enabled = false;
        customize.selectedCustomization = null;
        customize.selectedVariations = null;
        customize.selectedComponents = null;
        contextService.setCustomize(customize);
    }
    clearCustomizeContextAndReloadPreview(previewService, contextService) {
        const selectedVariations = lodash.cloneDeep(contextService.getCustomize().selectedVariations);
        this.clearCustomizeContext(contextService);
        if (lodash.isObject(selectedVariations) && !lodash.isArray(selectedVariations)) {
            previewService.removePersonalizationDataFromPreview();
        }
    }
    clearCombinedViewCustomizeContext(contextService) {
        const combinedView = contextService.getCombinedView();
        combinedView.customize.enabled = false;
        combinedView.customize.selectedCustomization = null;
        combinedView.customize.selectedVariations = null;
        combinedView.customize.selectedComponents = null;
        if (lodash.isArray(combinedView.selectedItems)) {
            combinedView.selectedItems.forEach((item) => {
                delete item.highlighted;
            });
        }
        contextService.setCombinedView(combinedView);
    }
    clearCombinedViewContext(contextService) {
        const combinedView = contextService.getCombinedView();
        combinedView.enabled = false;
        combinedView.selectedItems = null;
        contextService.setCombinedView(combinedView);
    }
    clearCombinedViewContextAndReloadPreview(previewService, contextService) {
        const cvEnabled = lodash.cloneDeep(contextService.getCombinedView().enabled);
        const cvSelectedItems = lodash.cloneDeep(contextService.getCombinedView().selectedItems);
        this.clearCombinedViewContext(contextService);
        if (cvEnabled && cvSelectedItems) {
            previewService.removePersonalizationDataFromPreview();
        }
    }
};
/* @ngInject */ exports.PersonalizationsmarteditContextUtils = __decorate([
    core.Injectable()
], /* @ngInject */ exports.PersonalizationsmarteditContextUtils);

/**
 * @ngdoc overview
 * @name PersonalizationsmarteditCommonsComponentsModule
 */
exports.PersonalizationsmarteditCommonsComponentsModule = class PersonalizationsmarteditCommonsComponentsModule {
};
exports.PersonalizationsmarteditCommonsComponentsModule = __decorate([
    core.NgModule({
        imports: [
            smarteditcommons.TranslationModule.forChild(),
            common.CommonModule,
            smarteditcommons.L10nPipeModule,
            exports.DatetimePickerRangeModule,
            exports.ScrollZoneModule
        ],
        providers: [
            {
                provide: 'PaginationHelper',
                useFactory: () => (initialData) => new PaginationHelper(initialData)
            },
            smarteditcommons.L10nPipe,
            exports.PersonalizationsmarteditUtils,
            exports.PersonalizationsmarteditDateUtils,
            exports.PersonalizationsmarteditContextUtils,
            exports.PersonalizationsmarteditMessageHandler,
            PersonalizationsmarteditCommerceCustomizationService
        ],
        declarations: [
            exports.PersonalizationInfiniteScrollDirective,
            exports.PersonalizationPreventParentScrollDirective,
            exports.PersonalizationsmarteditInfiniteScrollingComponent,
            exports.PersonalizationPreventParentScrollComponent
        ],
        entryComponents: [
            exports.PersonalizationsmarteditInfiniteScrollingComponent,
            exports.PersonalizationPreventParentScrollComponent
        ],
        exports: [
            exports.PersonalizationInfiniteScrollDirective,
            exports.PersonalizationPreventParentScrollDirective,
            exports.PersonalizationsmarteditInfiniteScrollingComponent,
            exports.PersonalizationPreventParentScrollComponent
        ]
    })
], exports.PersonalizationsmarteditCommonsComponentsModule);

var /* @ngInject */ BaseSiteHeaderInterceptor_1;
/* @ngInject */ exports.BaseSiteHeaderInterceptor = /* @ngInject */ BaseSiteHeaderInterceptor_1 = class /* @ngInject */ BaseSiteHeaderInterceptor {
    constructor(sharedDataService) {
        this.sharedDataService = sharedDataService;
    }
    intercept(request, next) {
        if (/* @ngInject */ BaseSiteHeaderInterceptor_1.PERSONALIZATION_ENDPOINT.test(request.url)) {
            return rxjs.from(this.sharedDataService.get('experience')).pipe(operators.switchMap((experience) => {
                if (experience.catalogDescriptor.siteId) {
                    const newReq = request.clone({
                        headers: request.headers.set(/* @ngInject */ BaseSiteHeaderInterceptor_1.HEADER_NAME, experience.catalogDescriptor.siteId)
                    });
                    return next.handle(newReq);
                }
                return next.handle(request);
            }));
        }
        else {
            return next.handle(request);
        }
    }
};
/* @ngInject */ exports.BaseSiteHeaderInterceptor.HEADER_NAME = 'Basesite';
/* @ngInject */ exports.BaseSiteHeaderInterceptor.PERSONALIZATION_ENDPOINT = /\/personalizationwebservices/;
/* @ngInject */ exports.BaseSiteHeaderInterceptor = /* @ngInject */ BaseSiteHeaderInterceptor_1 = __decorate([
    core.Injectable(),
    __metadata("design:paramtypes", [smarteditcommons.ISharedDataService])
], /* @ngInject */ exports.BaseSiteHeaderInterceptor);

/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
// Target Group Triggers / Expressions
exports.TriggerActionId = void 0;
(function (TriggerActionId) {
    TriggerActionId["AND"] = "AND";
    TriggerActionId["OR"] = "OR";
    TriggerActionId["NOT"] = "NOT";
})(exports.TriggerActionId || (exports.TriggerActionId = {}));
exports.TriggerType = void 0;
(function (TriggerType) {
    TriggerType["DEFAULT_TRIGGER"] = "defaultTriggerData";
    TriggerType["SEGMENT_TRIGGER"] = "segmentTriggerData";
    TriggerType["EXPRESSION_TRIGGER"] = "expressionTriggerData";
    TriggerType["GROUP_EXPRESSION"] = "groupExpressionData";
    TriggerType["SEGMENT_EXPRESSION"] = "segmentExpressionData";
    TriggerType["NEGATION_EXPRESSION"] = "negationExpressionData";
    TriggerType["CONTAINER_TYPE"] = "container";
    TriggerType["ITEM_TYPE"] = "item";
    TriggerType["DROPZONE_TYPE"] = "dropzone";
})(exports.TriggerType || (exports.TriggerType = {}));
const PERSONLIZATION_SEARCH_EXTENSION_TOKEN = new core.InjectionToken('PERSONLIZATION_SEARCH_EXTENSION_TOKEN');
const PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN = new core.InjectionToken('PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN');
const PERSONLIZATION_PROMOTION_EXTENSION_TOKEN = new core.InjectionToken('PERSONLIZATION_PROMOTION_EXTENSION_TOKEN');
const PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN = new core.InjectionToken('PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN');

exports.COMBINED_VIEW_TOOLBAR_ITEM_KEY = COMBINED_VIEW_TOOLBAR_ITEM_KEY;
exports.COMPONENT_CONTAINER_TYPE = COMPONENT_CONTAINER_TYPE;
exports.CONTAINER_SOURCE_ID_ATTR = CONTAINER_SOURCE_ID_ATTR;
exports.CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS = CUSTOMIZATION_VARIATION_MANAGEMENT_BUTTONS;
exports.CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY = CUSTOMIZATION_VARIATION_MANAGEMENT_SEGMENTTRIGGER_GROUPBY;
exports.CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS = CUSTOMIZATION_VARIATION_MANAGEMENT_TABS_CONSTANTS;
exports.CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY = CUSTOMIZE_VIEW_TOOLBAR_ITEM_KEY;
exports.CombinedView = CombinedView;
exports.Customize = Customize;
exports.DATE_CONSTANTS = DATE_CONSTANTS;
exports.IPersonalizationsmarteditContextMenuServiceProxy = IPersonalizationsmarteditContextMenuServiceProxy;
exports.IPersonalizationsmarteditContextServiceProxy = IPersonalizationsmarteditContextServiceProxy;
exports.PERSONALIZATION_CATALOG_FILTER = PERSONALIZATION_CATALOG_FILTER;
exports.PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING = PERSONALIZATION_COMBINED_VIEW_CSS_MAPPING;
exports.PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES = PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES;
exports.PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER = PERSONALIZATION_CUSTOMIZATION_PAGE_FILTER;
exports.PERSONALIZATION_DATE_FORMATS = PERSONALIZATION_DATE_FORMATS;
exports.PERSONALIZATION_VIEW_STATUS_MAPPING_CODES = PERSONALIZATION_VIEW_STATUS_MAPPING_CODES;
exports.PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN = PERSONLIZATION_PROMOTION_EXTENSION_INJECTOR_TOKEN;
exports.PERSONLIZATION_PROMOTION_EXTENSION_TOKEN = PERSONLIZATION_PROMOTION_EXTENSION_TOKEN;
exports.PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN = PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN;
exports.PERSONLIZATION_SEARCH_EXTENSION_TOKEN = PERSONLIZATION_SEARCH_EXTENSION_TOKEN;
exports.PaginationHelper = PaginationHelper;
exports.Personalization = Personalization;
exports.PersonalizationsmarteditCommerceCustomizationService = PersonalizationsmarteditCommerceCustomizationService;
exports.SeData = SeData;
