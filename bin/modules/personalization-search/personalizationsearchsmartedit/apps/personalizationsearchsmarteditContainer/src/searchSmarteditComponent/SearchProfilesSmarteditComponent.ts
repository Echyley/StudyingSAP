/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
import { CdkDragMove } from "@angular/cdk/drag-drop";
import { DOCUMENT } from "@angular/common";
import {
  Component,
  OnInit,
  DoCheck,
  Inject,
  OnDestroy,
  ViewEncapsulation,
} from "@angular/core";
import { TranslateService } from "@ngx-translate/core";
import { cloneDeep } from "lodash";
import {
  PaginationHelper,
  PersonalizationsmarteditMessageHandler,
  SeData,
  Customization,
  CustomizationVariation,
  PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES,
  PersonlizationSearchExtensionInjector,
  PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN,
} from "personalizationcommons";
import {
  SearchRestService,
  SearchProfilesContextService,
} from "personalizationsearchsmarteditcontainer/service";
import {
  SearchProfileAction,
  SearchProfileFilter,
  SearchProfileItem,
} from "personalizationsearchsmarteditcontainer/types";
import { SEARCH_PROFILE_ACTION_TYPE } from "personalizationsearchsmarteditcontainer/utils/constants";
import { Subject, Subscription } from "rxjs";
import { debounceTime } from "rxjs/operators";
import { promiseUtils } from "smarteditcommons";

interface DropAction {
  targetId: string;
  action?: "before" | "after";
}

@Component({
  selector: "search-profiles-smartedit",
  templateUrl: "./SearchProfilesSmarteditComponent.html",
  styleUrls: ["./SearchProfilesSmarteditComponent.scss"],
  encapsulation: ViewEncapsulation.None,
})
export class SearchProfilesSmarteditComponent
  implements OnInit, DoCheck, OnDestroy {
  public selectedSearchProfile: SearchProfileItem = null;
  public allSearchProfiles: SearchProfileItem[] = [];
  public availableSearchProfiles: SearchProfileItem[] = [];
  public searchProfileActions: SearchProfileAction[] = [];
  public moreSearchProfilestRequestProcessing: boolean;
  public searchProfileContext: any;
  public getSelectedTypeCode: () => string;
  public fetchStrategy = {
    fetchAll: (): Promise<SearchProfileItem[]> => {
      this.availableSearchProfiles = [];
      this.allSearchProfiles.forEach((searchProfile: SearchProfileItem) => {
        if (this.isItemInSelectDisabled(searchProfile)) {
          this.availableSearchProfiles.push(
            this.formateSearchProfileItem(searchProfile)
          );
        }
      });
      return Promise.resolve(this.availableSearchProfiles);
    },
  };

  private seData: SeData;
  private actions: any[];
  private getCustomization: () => Customization;
  private getVariation: () => CustomizationVariation;
  private addAction: (
    action: SearchProfileAction,
    comparer: (p1, p2) => boolean
  ) => void;
  private isActionInSelectDisabled: (
    action: any,
    comparer: (p1, p2) => boolean
  ) => any;
  private searchProfileFilter: SearchProfileFilter;
  private paginationHelper: PaginationHelper;
  private customizationPreCode: string;
  private variationPreCode: string;
  private actionsPreCount = 0;
  // data for drag & drop
  private draggedItemCss = "node-item";
  private dropTargetIds = [];
  private nodeLookup = {};
  private dropAction: DropAction = null;
  private dragMovedSubscription: Subscription;
  private dragMovedSubject = new Subject<CdkDragMove<any> | undefined>();

  constructor(
    @Inject(PERSONLIZATION_SEARCH_EXTENSION_INJECTOR_TOKEN)
    private personlizationSearchExtensionInjector: PersonlizationSearchExtensionInjector,
    @Inject(DOCUMENT) private document: Document,
    private translateService: TranslateService,
    private searchRestService: SearchRestService,
    private searchProfilesContextService: SearchProfilesContextService,
    private personalizationsmarteditMessageHandler: PersonalizationsmarteditMessageHandler
  ) {
    this.seData = personlizationSearchExtensionInjector.seData;
    this.getCustomization =
      personlizationSearchExtensionInjector.getCustomization;
    this.getVariation = personlizationSearchExtensionInjector.getVariation;
    this.actions = personlizationSearchExtensionInjector.actions;
    this.addAction = personlizationSearchExtensionInjector.addAction;
    this.isActionInSelectDisabled =
      personlizationSearchExtensionInjector.isActionInSelectDisabled;
    this.getSelectedTypeCode =
      personlizationSearchExtensionInjector.getSelectedTypeCode;
  }

  ngOnInit(): void {
    this.searchRestService.initSeData(this.seData);
    this.moreSearchProfilestRequestProcessing = false;
    this.paginationHelper = new PaginationHelper({});
    this.paginationHelper.reset();
    this.searchProfileFilter = {
      code: "",
      pageSize: 0,
      currentPage: 0,
    };
    this.searchProfileContext = this.searchProfilesContextService.searchProfileActionContext;
    // create dragMovedSubscription to improve proformance
    this.dragMovedSubscription = this.dragMovedSubject
      .pipe(debounceTime(50))
      .subscribe((event) => {
        this.performDragMoved(event);
      });
  }

  ngDoCheck(): void {
    // populate array searchProfileActions based on current personlization managerView selection
    if (this.actionsPreCount !== this.actions.length) {
      const actionsArray = this.actions || [];
      this.searchProfileActions = actionsArray
        .filter(function (item) {
          return item.action.type === SEARCH_PROFILE_ACTION_TYPE;
        })
        .map(function (item) {
          return {
            ...item.action,
            baseIndex: actionsArray.indexOf(item),
          };
        });
      this.searchProfileContext.searchProfilesOrder = this.searchProfileActions;
      // prepare tree data for drag&drop
      this.resetSearchProfileActionsDragDrop();
      this.updateSearchProfileActionsDragDrop();
      this.actionsPreCount = this.actions.length;
    }
    // update customization code
    if (this.customizationPreCode !== this.getCustomization().code) {
      if (this.getCustomization() && this.getCustomization().code) {
        this.searchProfileContext.customizationCode = this.getCustomization().code;
      } else {
        this.searchProfileContext.customizationCode = undefined;
      }
      this.customizationPreCode = this.getCustomization().code;
    }
    // update customizationVariation code
    if (this.variationPreCode !== this.getVariation().code) {
      if (this.getVariation() && this.getVariation().code) {
        this.searchProfileContext.variationCode = this.getVariation().code;
      } else {
        this.searchProfileContext.variationCode = undefined;
      }
      this.variationPreCode = this.getVariation().code;
    }
  }

  ngOnDestroy(): void {
    this.dragMovedSubscription?.unsubscribe();
  }

  // use arrow function in purpose as it will be involked by other components
  public getPage = (): Promise<any> => this.addMoreSearchProfilesItems();

  public getSearchProfileCode(): string {
    return SEARCH_PROFILE_ACTION_TYPE;
  }

  public searchProfileSelectedEvent = (
    selectedSearchProfile: SearchProfileItem
  ): void => {
    this.selectedSearchProfile = selectedSearchProfile;
    const searchProfileAction = this.buildAction(selectedSearchProfile);
    this.addAction(
      searchProfileAction,
      this.searchProfilesContextService.searchProfileActionComparer
    );
  };

  public dragMoved(event: CdkDragMove<any>): void {
    this.dragMovedSubject.next(event);
  }

  public dragDropped(event: any): void {
    if (this.checkDragDrop(event.item.data)) {
      const draggedItemId = event.item.data;
      const dropItemId = this.dropAction.targetId;
      // update backing actions array
      const draggedItem = this.nodeLookup[draggedItemId];
      const droppedItem = this.nodeLookup[dropItemId];
      if (this.dropAction.action === "before") {
        this.actions.splice(
          droppedItem.baseIndex,
          0,
          this.actions.splice(draggedItem.baseIndex, 1)[0]
        );
      } else {
        this.actions.splice(
          droppedItem.baseIndex + 1,
          0,
          this.actions.splice(draggedItem.baseIndex, 1)[0]
        );
      }
      // update drag&drop tree
      this.searchProfileActions.splice(
        this.searchProfileActions.findIndex(
          (item) => item.searchProfileCode === draggedItemId
        ),
        1
      );
      const droppedIndex = this.searchProfileActions.findIndex(
        (item) => item.searchProfileCode === this.dropAction.targetId
      );
      if (this.dropAction.action === "before") {
        this.searchProfileActions.splice(droppedIndex, 0, draggedItem);
      } else {
        this.searchProfileActions.splice(droppedIndex + 1, 0, draggedItem);
      }
      this.updateSearchProfileActionsBaseIndex();
      // set UPDATED status for modified actions
      const startIdx =
        event.previousIndex < event.currentIndex
          ? event.previousIndex
          : event.currentIndex;
      const increaseValue =
        Math.abs(event.currentIndex - event.previousIndex) + 1;
      const modifiedActions = this.searchProfileActions.slice(
        startIdx,
        increaseValue
      );
      const modifiedWrappedActions = [];
      modifiedActions.forEach((item) => {
        modifiedWrappedActions.push(this.getWrapperActionForAction(item));
      });
      this.setStatusForUpdatedActions(modifiedWrappedActions);
    }
  }

  private getWrapperActionForAction(action: any): any {
    const matchedActions = [];
    this.actions.forEach((comparedAction) => {
      if (
        this.searchProfilesContextService.searchProfileActionComparer(
          action,
          comparedAction.action
        )
      ) {
        matchedActions.push(comparedAction);
      }
    });
    return matchedActions[0];
  }

  private checkDragDrop(draggedItemId: string): boolean {
    return (
      this.dropAction !== null &&
      this.dropAction.action !== null &&
      this.dropAction.targetId !== null &&
      draggedItemId !== this.dropAction.targetId &&
      this.checkDragDropIndex(draggedItemId)
    );
  }

  private checkDragDropIndex(draggedItemId: string): boolean {
    const draggedIndex = this.searchProfileActions.findIndex(
      (item) => item.searchProfileCode === draggedItemId
    );
    let droppedIndex = this.searchProfileActions.findIndex(
      (item) => item.searchProfileCode === this.dropAction.targetId
    );
    if (this.dropAction.action === "before") {
      droppedIndex -= 1;
    } else {
      droppedIndex += 1;
    }
    return draggedIndex !== droppedIndex;
  }

  private updateSearchProfileActionsBaseIndex(): void {
    this.actions.forEach((actionItem, index) => {
      if (actionItem.action.type === SEARCH_PROFILE_ACTION_TYPE) {
        this.nodeLookup[actionItem.action.searchProfileCode].baseIndex = index;
      }
    });
  }

  private setStatusForUpdatedActions(wrapperActions): void {
    wrapperActions.forEach((action) => {
      if (
        action.status !==
        PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.NEW
      ) {
        action.status =
          PERSONALIZATION_COMMERCE_CUSTOMIZATION_ACTION_STATUSES.UPDATE;
      }
    });
  }

  private updateSearchProfileActionsDragDrop(): void {
    this.searchProfileActions.forEach((node) => {
      this.dropTargetIds.push(node.searchProfileCode);
      this.nodeLookup[node.searchProfileCode] = node;
    });
  }

  private resetSearchProfileActionsDragDrop(): void {
    this.dropTargetIds.length = 0;
    this.nodeLookup = {};
  }

  private performDragMoved(event: CdkDragMove<any>): void {
    this.dropAction = {
      targetId: null,
      action: null,
    };
    const ele = this.document.elementFromPoint(
      event.pointerPosition.x,
      event.pointerPosition.y
    );
    if (!ele) {
      this.clearDragInfo();
      return;
    }
    const container: Element = ele.classList.contains(this.draggedItemCss)
      ? ele
      : ele.closest(".node-item");
    if (!container) {
      this.clearDragInfo();
      return;
    }
    this.dropAction = {
      targetId: container.getAttribute("data-id"),
    };
    const targetRect = container.getBoundingClientRect();
    const oneHalf = targetRect.height / 2;
    // memorize moved location
    if (event.pointerPosition.y - targetRect.top < oneHalf) {
      this.dropAction.action = "before";
    } else if (event.pointerPosition.y - targetRect.top > oneHalf) {
      this.dropAction.action = "after";
    }
    this.showDragInfo();
  }

  private showDragInfo(): void {
    this.clearDragInfo();
    if (this.dropAction) {
      this.document
        .getElementById("node-" + this.dropAction.targetId)
        .classList.add("drop-" + this.dropAction.action);
    }
  }

  private clearDragInfo(dropped = false): void {
    if (dropped) {
      this.dropAction = null;
    }
    this.document
      .querySelectorAll(".drop-before")
      .forEach((element) => element.classList.remove("drop-before"));
    this.document
      .querySelectorAll(".drop-after")
      .forEach((element) => element.classList.remove("drop-after"));
  }

  private async addMoreSearchProfilesItems(): Promise<any> {
    try {
      if (
        this.paginationHelper.getPage() <
          this.paginationHelper.getTotalPages() - 1 &&
        !this.moreSearchProfilestRequestProcessing
      ) {
        this.moreSearchProfilestRequestProcessing = true;
        const indexTypeResponse = await this.getIndexTypes();
        let filter: SearchProfileFilter = this.getSearchProfileFilterObject();
        const param = {
          indexTypes: indexTypeResponse.indexTypeIds || [],
        };
        filter = {
          indexTypes: param.indexTypes,
          ...filter,
        };
        const searchProfilesResponse = await this.searchRestService.getSearchProfiles(
          filter
        );
        this.allSearchProfiles = cloneDeep(
          searchProfilesResponse.searchProfiles
        );
        this.paginationHelper = new PaginationHelper(
          searchProfilesResponse.pagination
        );
        this.moreSearchProfilestRequestProcessing = false;
      }
    } catch (err) {
      this.moreSearchProfilestRequestProcessing = false;
      this.personalizationsmarteditMessageHandler.sendError(
        this.translateService.instant(
          "personalizationsearchsmartedit.commercecustomization.search.error.gettingsearchprofiles"
        )
      );
    }
  }

  private getIndexTypes(): Promise<any> {
    const deferred = promiseUtils.defer();
    const experienceDataPromises = [];
    const experienceDataResponse = this.searchRestService.getSeExperienceData();
    experienceDataResponse.productCatalogVersions.forEach(
      (productCatalogVersion) => {
        experienceDataPromises.push(
          this.searchRestService.getIndexTypesForCatalogVersion(
            productCatalogVersion
          )
        );
      }
    );
    Promise.all(experienceDataPromises).then(
      function successCallback(experienceDataPromisesResponse) {
        const mergedResponse = {
          indexTypeIds: [],
        };
        experienceDataPromisesResponse.forEach((singleResponse) => {
          mergedResponse.indexTypeIds = mergedResponse.indexTypeIds.concat(
            singleResponse.indexTypeIds.filter(function (item) {
              return mergedResponse.indexTypeIds.indexOf(item) < 0;
            })
          );
        });
        deferred.resolve(mergedResponse);
      },
      function errorCallback(errorResponse) {
        deferred.reject(errorResponse);
      }
    );
    return deferred.promise;
  }

  private isItemInSelectDisabled(
    searchProfileItem: SearchProfileItem
  ): boolean {
    const searchProfileAction = this.buildAction(searchProfileItem);
    return this.isActionInSelectDisabled(
      searchProfileAction,
      this.searchProfilesContextService.searchProfileActionComparer
    ) === undefined
      ? true
      : false;
  }

  private getSearchProfileFilterObject(): SearchProfileFilter {
    return {
      code: this.searchProfileFilter.code,
      pageSize: this.paginationHelper.getCount(),
      currentPage: this.paginationHelper.getPage() + 1,
    };
  }

  private buildAction(
    searchProfileItem: SearchProfileItem
  ): SearchProfileAction {
    return {
      type: SEARCH_PROFILE_ACTION_TYPE,
      searchProfileCode: searchProfileItem.code,
      searchProfileCatalog: searchProfileItem.catalogVersion.split(":")[0],
    };
  }

  private formateSearchProfileItem(
    searchProfileItem: SearchProfileItem
  ): SearchProfileItem {
    const availableSearchProfileItem: SearchProfileItem = cloneDeep(
      searchProfileItem
    );
    availableSearchProfileItem.id = availableSearchProfileItem.code;
    availableSearchProfileItem.label = availableSearchProfileItem.name;
    return availableSearchProfileItem;
  }
}
