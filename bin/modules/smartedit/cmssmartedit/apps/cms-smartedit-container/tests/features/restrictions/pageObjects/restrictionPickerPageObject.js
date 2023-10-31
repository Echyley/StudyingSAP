/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
/* jshint unused:false, undef:false */
function RestrictionPickerPageObject(mockedRestrictionTypes, mockedRestrictions, element) {
    var RESTRICTION_RESULT_ID_PREFIX = '#restriction-search-result-id-';
    var uiSelectPO = new UiSelectPageObject(element);
    var self = this;
    this.elements = {
        getSearchField() {
            return element.find('#search-field-id input');
        },
        getRestrictionSearchResult(restrictionId) {
            return element.find(RESTRICTION_RESULT_ID_PREFIX + restrictionId);
        }
    };
    this.actions = {
        openUiSelect() {
            uiSelectPO.clickSelectToggle();
        },
        selectFirstRestrictionType() {
            uiSelectPO.clickSelectElement(0);
        },
        enterSearchValue(val) {
            self.elements.getSearchField().val(val).change();
        },
        selectFirstSearchResult() {
            self.elements
                .getRestrictionSearchResult(self.getFirstRestrictionOfFirstRestrictionType().uid)
                .click();
        }
    };
    this.assertions = {
        assertFirstUiSelectTextEquals() {
            expect(uiSelectPO.getSelectElement(0).text().trim()).toBe(
                mockedRestrictionTypes.restrictionTypes[0].name.en
            );
        },
        assertSearchFieldDisplayed() {
            expect(self.isElementVisible(self.elements.getSearchField())).toBe(true);
        },
        assertSearchResultsContains(restrictionId) {
            expect(
                self.isElementVisible(self.elements.getRestrictionSearchResult(restrictionId))
            ).toBe(true);
        }
    };

    this.isElementVisible = function (selector) {
        if (selector) {
            return selector.is(':visible');
        }
        return false;
    };

    this.getSearchStringForFirstRestrictionType = function () {
        return this.getFirstRestrictionOfFirstRestrictionType().name.charAt(0);
    };

    this.getFirstRestrictionTypeId = function () {
        return mockedRestrictionTypes.restrictionTypes[0].code;
    };

    this.getFirstRestrictionOfFirstRestrictionType = function () {
        return this.getMockRestrictionsForType(this.getFirstRestrictionTypeId())[0];
    };

    this.getMockRestrictionsForType = function (type) {
        return mockedRestrictions.restrictions.filter(function (restriction) {
            return restriction.typeCode === type;
        });
    };
}
