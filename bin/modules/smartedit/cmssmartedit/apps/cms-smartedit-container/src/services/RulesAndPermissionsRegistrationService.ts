/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
import { Injectable } from '@angular/core';
import { AttributePermissionsRestService, TypePermissionsRestService } from 'cmscommons';
import {
    ICatalogService,
    ICatalogVersionPermissionService,
    IExperience,
    IExperienceService,
    IPermissionService,
    ISharedDataService,
    PermissionContext,
    TypedMap,
    windowUtils,
    EXPERIENCE_STORAGE_KEY,
    CMSModesService,
    IPageService,
    WorkflowService,
    Workflow
} from 'smarteditcommons';
import {
    RULE_PERMISSION_CATALOG_NON_ACTIVE,
    RULE_PERMISSION_NOT_VERSIONING_PERSPECTIVE,
    RULE_PERMISSION_VERSION_PAGE_SELECTED,
    RULE_PERMISSION_CURRENT_PAGE_HAS_ACTIVE_WORKFLOW,
    RULE_PERMISSION_HAS_CHANGE_TYPE_PERMISSION_ON_CURRENT_PAGE,
    RULE_PERMISSION_HAS_READ_TYPE_PERMISSION_ON_CURRENT_PAGE,
    RULE_PERMISSION_HAS_READ_PERMISSION_ON_VERSION_TYPE,
    RULE_PERMISSION_HAS_CREATE_PERMISSION_ON_VERSION_TYPE,
    RULE_PERMISSION_HAS_CHANGE_PERMISSION_ON_WORKFLOW_TYPE,
    RULE_PERMISSION_WRITE_PAGE,
    RULE_PERMISSION_WRITE_SLOT,
    RULE_PERMISSION_WRITE_COMPONENT,
    RULE_PERMISSION_WRITE_TO_CURRENT_CATALOG_VERSION,
    RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW,
    RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_WORKFLOW_CURRENT_ACTION,
    RULE_PERMISSION_SYNC_CATALOG,
    RULE_PERMISSION_READ_PAGE,
    RULE_PERMISSION_READ_SLOT,
    RULE_PERMISSION_READ_COMPONENT,
    RULE_PERMISSION_READ_CURRENT_CATALOG_VERSION,
    RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE
} from '../constants';
import { CatalogVersionRestService } from '../dao';

@Injectable()
export class RulesAndPermissionsRegistrationService {
    constructor(
        private readonly attributePermissionsRestService: AttributePermissionsRestService,
        private readonly catalogService: ICatalogService,
        private readonly catalogVersionPermissionService: ICatalogVersionPermissionService,
        private readonly catalogVersionRestService: CatalogVersionRestService,
        private readonly cMSModesService: CMSModesService,
        private readonly experienceService: IExperienceService,
        private readonly pageService: IPageService,
        private readonly permissionService: IPermissionService,
        private readonly sharedDataService: ISharedDataService,
        private readonly typePermissionsRestService: TypePermissionsRestService,
        private readonly workflowService: WorkflowService
    ) {}

    public register(): void {
        this.registerRules();
        this.registerRulesForTypeCodeFromContext();
        this.registerRulesForCurrentPage();
        this.registerRulesForTypeCode();
        this.registerRulesForTypeAndQualifier();
        this.registerPermissions();
    }

    private onSuccess(results: boolean[]): boolean {
        return results.every((isValid) => isValid);
    }

    private onError(): boolean {
        return false;
    }

    private async getCurrentPageActiveWorkflow(): Promise<Workflow | null> {
        if (!windowUtils.getGatewayTargetFrame()) {
            return null;
        }
        const { uuid } = await this.pageService.getCurrentPageInfo();
        return this.workflowService.getActiveWorkflowForPageUuid(uuid);
    }

    private registerRules(): void {
        this.permissionService.registerRule({
            names: [
                RULE_PERMISSION_WRITE_PAGE,
                RULE_PERMISSION_WRITE_SLOT,
                RULE_PERMISSION_WRITE_COMPONENT,
                RULE_PERMISSION_WRITE_TO_CURRENT_CATALOG_VERSION
            ],
            verify: (permissionNameObjs: PermissionContext[]) => {
                const promises = permissionNameObjs.map((permissionNameObject) => {
                    if (permissionNameObject.context) {
                        return this.catalogVersionPermissionService.hasWritePermission(
                            permissionNameObject.context.catalogId,
                            permissionNameObject.context.catalogVersion
                        );
                    } else {
                        return this.catalogVersionPermissionService.hasWritePermissionOnCurrent();
                    }
                });
                return Promise.all(promises).then(this.onSuccess, this.onError);
            }
        });

        /**
         * This rule returns true if the page is in a workflow and current user is participant of this workflow
         * or there is no workflow.
         * Otherwise, it returns false;
         */
        this.permissionService.registerRule({
            names: [RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW],
            verify: (permissionNameObjs) => {
                const isAvailableForCurrentPrincipal = (workflow: Workflow): boolean =>
                    workflow === null ? true : workflow.isAvailableForCurrentPrincipal;

                const promises = permissionNameObjs.map((permissionNameObject) => {
                    if (permissionNameObject.context) {
                        return this.workflowService
                            .getActiveWorkflowForPageUuid(
                                permissionNameObject.context.pageInfo.uuid
                            )
                            .then((workflow) => isAvailableForCurrentPrincipal(workflow));
                    } else {
                        return this.getCurrentPageActiveWorkflow().then(
                            (workflow) => isAvailableForCurrentPrincipal(workflow),
                            () => true
                        );
                    }
                });
                return Promise.all(promises).then(this.onSuccess, this.onError);
            }
        });

        /**
         * This rule returns true if the current user is a participant of currently active step of a workflow
         * or there is no workflow.
         * Otherwise, it returns false;
         */
        this.permissionService.registerRule({
            names: [RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_WORKFLOW_CURRENT_ACTION],
            verify: (permissionNameObjs: PermissionContext[]) => {
                const isUserParticipant = async (workflow: Workflow): Promise<boolean> =>
                    workflow === null
                        ? true
                        : this.workflowService.isUserParticipanInActiveAction(
                              workflow.workflowCode
                          );

                const promises = permissionNameObjs.map((permissionNameObject) => {
                    if (permissionNameObject.context) {
                        return this.workflowService
                            .getActiveWorkflowForPageUuid(
                                permissionNameObject.context.pageInfo.uuid
                            )
                            .then((workflow) => isUserParticipant(workflow));
                    } else {
                        return this.getCurrentPageActiveWorkflow().then(
                            (workflow) => isUserParticipant(workflow),
                            () => true
                        );
                    }
                });
                return Promise.all(promises).then(this.onSuccess, this.onError);
            }
        });

        this.permissionService.registerRule({
            names: [RULE_PERMISSION_SYNC_CATALOG],
            verify: (permissionNameObjs: PermissionContext[]) => {
                const promises = permissionNameObjs.map((permissionNameObject) => {
                    if (permissionNameObject.context) {
                        return this.catalogVersionPermissionService.hasSyncPermission(
                            permissionNameObject.context.catalogId,
                            permissionNameObject.context.catalogVersion,
                            permissionNameObject.context.targetCatalogVersion
                        );
                    } else {
                        return this.catalogVersionPermissionService.hasSyncPermissionFromCurrentToActiveCatalogVersion();
                    }
                });
                return Promise.all(promises).then(this.onSuccess, this.onError);
            }
        });

        this.permissionService.registerRule({
            names: ['se.approval.status.page'],
            verify: async () => {
                const { approvalStatus } = await this.pageService.getCurrentPageInfo();
                return approvalStatus === 'APPROVED';
            }
        });

        this.permissionService.registerRule({
            names: [
                RULE_PERMISSION_READ_PAGE,
                RULE_PERMISSION_READ_SLOT,
                RULE_PERMISSION_READ_COMPONENT,
                RULE_PERMISSION_READ_CURRENT_CATALOG_VERSION
            ],
            verify: () => this.catalogVersionPermissionService.hasReadPermissionOnCurrent()
        });

        this.permissionService.registerRule({
            names: [RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE],
            verify: async () => {
                const experience = (await this.sharedDataService.get(
                    EXPERIENCE_STORAGE_KEY
                )) as IExperience;
                return (
                    experience.pageContext &&
                    experience.pageContext.catalogVersionUuid ===
                        experience.catalogDescriptor.catalogVersionUuid
                );
            }
        });

        /**
         * Show the clone icon:
         * - If a page belonging to an active catalog version, whose copyToCatalogsDisabled flag is set to false and has at-least one clonable target.
         * - If a page belonging to a non active catalog version has at-least one clonable target.
         *
         * !NOTE: Logic here is very similar to logic used in ManagePageService#isPageCloneable, so if any changes are done here it should be considered to add those changes in mentioned service as well
         */
        this.permissionService.registerRule({
            names: ['se.cloneable.page'],
            verify: async () => {
                const experience = (await this.sharedDataService.get(
                    EXPERIENCE_STORAGE_KEY
                )) as IExperience;
                if (!experience.pageContext) {
                    return false;
                }
                const pageUriContext = {
                    CURRENT_CONTEXT_SITE_ID: experience.pageContext.siteId,
                    CURRENT_CONTEXT_CATALOG: experience.pageContext.catalogId,
                    CURRENT_CONTEXT_CATALOG_VERSION: experience.pageContext.catalogVersion
                };

                const pageInfo = await this.pageService.getCurrentPageInfo();
                const targets = await this.catalogVersionRestService.getCloneableTargets(
                    pageUriContext
                );
                if (experience.pageContext.active) {
                    return targets.versions.length > 0 && !pageInfo.copyToCatalogsDisabled;
                }

                return targets.versions.length > 0;
            }
        });

        this.permissionService.registerRule({
            names: [RULE_PERMISSION_CATALOG_NON_ACTIVE],
            verify: () => this.catalogService.isContentCatalogVersionNonActive()
        });

        this.permissionService.registerRule({
            names: [RULE_PERMISSION_NOT_VERSIONING_PERSPECTIVE],
            verify: async () => {
                const isActive = await this.cMSModesService.isVersioningPerspectiveActive();
                return !isActive;
            }
        });

        this.permissionService.registerRule({
            names: [RULE_PERMISSION_VERSION_PAGE_SELECTED],
            verify: async () => {
                const experience = await this.experienceService.getCurrentExperience();
                return !!experience.versionId;
            }
        });

        this.permissionService.registerRule({
            names: ['se.version.page.not.selected'],
            verify: async () => {
                const experience = await this.experienceService.getCurrentExperience();
                return !experience.versionId;
            }
        });

        this.permissionService.registerRule({
            names: ['se.catalogversion.has.workflows.enabled'],
            verify: () => this.workflowService.areWorkflowsEnabledOnCurrentCatalogVersion()
        });

        this.permissionService.registerRule({
            names: [RULE_PERMISSION_CURRENT_PAGE_HAS_ACTIVE_WORKFLOW],
            verify: async () => {
                const workflow = await this.getCurrentPageActiveWorkflow();
                return workflow !== null;
            }
        });

        this.permissionService.registerRule({
            names: ['se.current.page.has.no.active.workflow'],
            verify: async () => {
                const workflow = await this.getCurrentPageActiveWorkflow();
                return workflow === null;
            }
        });

        // Attribute Permissions
        this.permissionService.registerRule({
            names: ['se.has.change.permission.on.page.approval.status'],
            verify: async () => {
                const attributeName = 'approvalStatus';
                const pageInfo = await this.pageService.getCurrentPageInfo();

                const result =
                    await this.attributePermissionsRestService.hasChangePermissionOnAttributesInType(
                        pageInfo.typeCode,
                        [attributeName]
                    );
                return result[attributeName];
            }
        });
    }

    private registerRulesForTypeCodeFromContext(): void {
        const registerTypePermissionRuleForTypeCodeFromContext = (
            ruleName: string,
            verifyRule: (types: string[]) => Promise<TypedMap<boolean>>
        ): void => {
            this.permissionService.registerRule({
                names: [ruleName],
                verify: (permissionNameObjs) => {
                    const promises = permissionNameObjs.map((permissionNameObject) =>
                        verifyRule([permissionNameObject.context.typeCode]).then(
                            (updatePermission) =>
                                updatePermission[permissionNameObject.context.typeCode]
                        )
                    );
                    return Promise.all(promises).then(this.onSuccess, this.onError);
                }
            });
        };

        // check if the current user has change permission on the type provided part of the permission object
        registerTypePermissionRuleForTypeCodeFromContext(
            'se.has.change.permissions.on.type',
            (types: string[]) => this.typePermissionsRestService.hasUpdatePermissionForTypes(types)
        );

        // check if the current user has create permission on the type provided part of the permission object
        registerTypePermissionRuleForTypeCodeFromContext(
            'se.has.create.permissions.on.type',
            (types: string[]) => this.typePermissionsRestService.hasCreatePermissionForTypes(types)
        );

        // check if the current user has remove permission on the type provided part of the permission object
        registerTypePermissionRuleForTypeCodeFromContext(
            'se.has.remove.permissions.on.type',
            (types: string[]) => this.typePermissionsRestService.hasDeletePermissionForTypes(types)
        );
    }

    private registerRulesForCurrentPage(): void {
        const registerTypePermissionRuleOnCurrentPage = (
            ruleName: string,
            verifyRule: (types: string[]) => Promise<TypedMap<boolean>>
        ): void => {
            this.permissionService.registerRule({
                names: [ruleName],
                verify: async () => {
                    const pageInfo = await this.pageService.getCurrentPageInfo();
                    const permissionObject = await verifyRule([pageInfo.typeCode]);
                    return permissionObject[pageInfo.typeCode];
                }
            });
        };

        // check if the current user has change permission on the page currently loaded
        registerTypePermissionRuleOnCurrentPage(
            RULE_PERMISSION_HAS_CHANGE_TYPE_PERMISSION_ON_CURRENT_PAGE,
            (types: string[]) => this.typePermissionsRestService.hasUpdatePermissionForTypes(types)
        );

        // check if the current user has create permission on the page currently loaded
        registerTypePermissionRuleOnCurrentPage(
            'se.has.create.type.permissions.on.current.page',
            (types: string[]) => this.typePermissionsRestService.hasCreatePermissionForTypes(types)
        );

        // check if the current user has read permission on the page currently loaded
        registerTypePermissionRuleOnCurrentPage(
            RULE_PERMISSION_HAS_READ_TYPE_PERMISSION_ON_CURRENT_PAGE,
            (types: string[]) => this.typePermissionsRestService.hasReadPermissionForTypes(types)
        );
    }

    private registerRulesForTypeCode(): void {
        const registerTypePermissionRuleForTypeCode = (
            ruleName: string,
            itemType: string,
            verifyRule: (types: string[]) => Promise<TypedMap<boolean>>
        ): void => {
            this.permissionService.registerRule({
                names: [ruleName],
                verify: async () => {
                    const UpdatePermission = await verifyRule([itemType]);
                    return UpdatePermission[itemType];
                }
            });
        };

        // check if the current user has read/create/remove/change permission on the CMSVersion type
        registerTypePermissionRuleForTypeCode(
            RULE_PERMISSION_HAS_READ_PERMISSION_ON_VERSION_TYPE,
            'CMSVersion',
            (types: string[]) => this.typePermissionsRestService.hasReadPermissionForTypes(types)
        );
        registerTypePermissionRuleForTypeCode(
            RULE_PERMISSION_HAS_CREATE_PERMISSION_ON_VERSION_TYPE,
            'CMSVersion',
            (types: string[]) => this.typePermissionsRestService.hasCreatePermissionForTypes(types)
        );
        registerTypePermissionRuleForTypeCode(
            'se.has.remove.permission.on.version.type',
            'CMSVersion',
            (types: string[]) => this.typePermissionsRestService.hasDeletePermissionForTypes(types)
        );
        registerTypePermissionRuleForTypeCode(
            'se.has.change.permission.on.version.type',
            'CMSVersion',
            (types: string[]) => this.typePermissionsRestService.hasUpdatePermissionForTypes(types)
        );
        registerTypePermissionRuleForTypeCode(
            'se.has.create.permission.on.abstractcomponent.type',
            'AbstractCMSComponent',
            (types: string[]) => this.typePermissionsRestService.hasCreatePermissionForTypes(types)
        );
        registerTypePermissionRuleForTypeCode(
            'se.has.change.permission.on.contentslotforpage.type',
            'ContentSlotForPage',
            (types: string[]) => this.typePermissionsRestService.hasUpdatePermissionForTypes(types)
        );

        // check if current user has create/change permission on the Workflow type
        registerTypePermissionRuleForTypeCode(
            'se.has.create.permission.on.workflow.type',
            'Workflow',
            (types: string[]) => this.typePermissionsRestService.hasCreatePermissionForTypes(types)
        );
        registerTypePermissionRuleForTypeCode(
            RULE_PERMISSION_HAS_CHANGE_PERMISSION_ON_WORKFLOW_TYPE,
            'Workflow',
            (types: string[]) => this.typePermissionsRestService.hasUpdatePermissionForTypes(types)
        );
        registerTypePermissionRuleForTypeCode(
            'se.has.read.permission.on.workflow.type',
            'Workflow',
            (types: string[]) => this.typePermissionsRestService.hasReadPermissionForTypes(types)
        );

        registerTypePermissionRuleForTypeCode(
            'se.has.create.permission.on.contentslot.type',
            'ContentSlot',
            (types: string[]) => this.typePermissionsRestService.hasCreatePermissionForTypes(types)
        );
        registerTypePermissionRuleForTypeCode(
            'se.has.delete.permission.on.contentslot.type',
            'ContentSlot',
            (types: string[]) => this.typePermissionsRestService.hasDeletePermissionForTypes(types)
        );
    }

    private registerRulesForTypeAndQualifier(): void {
        const registerAttributePermissionRuleForTypeAndQualifier = (
            ruleName: string,
            itemType: string,
            qualifier: string,
            verifyRule: (type: string, attributeNames: string[]) => Promise<TypedMap<boolean>>
        ): void => {
            this.permissionService.registerRule({
                names: [ruleName],
                verify: async () => {
                    const data = await verifyRule(itemType, [qualifier]);
                    return data[qualifier];
                }
            });
        };

        registerAttributePermissionRuleForTypeAndQualifier(
            'se.has.change.permission.on.workflow.status',
            'Workflow',
            'status',
            (type: string, attributeNames: string[]) =>
                this.attributePermissionsRestService.hasChangePermissionOnAttributesInType(
                    type,
                    attributeNames
                )
        );
        registerAttributePermissionRuleForTypeAndQualifier(
            'se.has.change.permission.on.workflow.description',
            'Workflow',
            'description',
            (type: string, attributeNames: string[]) =>
                this.attributePermissionsRestService.hasChangePermissionOnAttributesInType(
                    type,
                    attributeNames
                )
        );
    }

    private registerPermissions(): void {
        this.permissionService.registerPermission({
            aliases: ['se.add.component'],
            rules: [
                RULE_PERMISSION_WRITE_SLOT,
                RULE_PERMISSION_WRITE_COMPONENT,
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_HAS_CHANGE_TYPE_PERMISSION_ON_CURRENT_PAGE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_WORKFLOW_CURRENT_ACTION,
                'se.has.create.permission.on.abstractcomponent.type'
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.clone.component'],
            rules: [
                RULE_PERMISSION_WRITE_COMPONENT,
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_WORKFLOW_CURRENT_ACTION
            ]
        });

        this.permissionService.registerPermission({
            aliases: [RULE_PERMISSION_SYNC_CATALOG],
            rules: [RULE_PERMISSION_SYNC_CATALOG]
        });

        this.permissionService.registerPermission({
            aliases: ['se.edit.navigation'],
            rules: [RULE_PERMISSION_WRITE_COMPONENT]
        });

        this.registerPagePermissions();

        this.registerSlotContextMenuPermissions();

        this.registerVersionPermissions();

        this.registerWorkflowPermissions();
    }

    private registerPagePermissions(): void {
        this.permissionService.registerPermission({
            aliases: [RULE_PERMISSION_READ_PAGE],
            rules: [RULE_PERMISSION_READ_PAGE]
        });

        this.permissionService.registerPermission({
            aliases: ['se.edit.page'],
            rules: [
                RULE_PERMISSION_WRITE_PAGE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.sync.page'],
            rules: [
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.clone.page'],
            rules: ['se.cloneable.page', 'se.has.create.type.permissions.on.current.page']
        });

        this.permissionService.registerPermission({
            aliases: ['se.edit.page.link', 'se.delete.page.menu'],
            rules: [
                RULE_PERMISSION_WRITE_PAGE,
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_NOT_VERSIONING_PERSPECTIVE,
                RULE_PERMISSION_HAS_CHANGE_TYPE_PERMISSION_ON_CURRENT_PAGE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.edit.page.type', 'se.delete.page.type', 'se.restore.page.type'],
            rules: ['se.has.change.permissions.on.type']
        });

        this.permissionService.registerPermission({
            aliases: ['se.clone.page.type'],
            rules: ['se.has.create.permissions.on.type']
        });

        this.permissionService.registerPermission({
            aliases: ['se.permanently.delete.page.type'],
            rules: ['se.has.remove.permissions.on.type']
        });
    }

    private registerSlotContextMenuPermissions(): void {
        this.permissionService.registerPermission({
            aliases: ['se.sync.slot.context.menu', 'se.sync.slot.indicator'],
            rules: [
                RULE_PERMISSION_SYNC_CATALOG,
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.context.menu.remove.component'],
            rules: [
                RULE_PERMISSION_WRITE_SLOT,
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_WORKFLOW_CURRENT_ACTION
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.slot.context.menu.shared.icon', 'se.slot.context.menu.unshared.icon'],
            rules: [
                'se.read.slot',
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_WORKFLOW_CURRENT_ACTION
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.slot.context.menu.visibility'],
            rules: [RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE]
        });

        this.permissionService.registerPermission({
            aliases: ['se.context.menu.edit.component'],
            rules: [
                RULE_PERMISSION_WRITE_COMPONENT,
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.context.menu.drag.and.drop.component'],
            rules: [
                RULE_PERMISSION_WRITE_SLOT,
                RULE_PERMISSION_WRITE_COMPONENT,
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_WORKFLOW_CURRENT_ACTION,
                'se.has.change.permission.on.contentslotforpage.type'
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.shared.slot.override.options'],
            rules: [
                RULE_PERMISSION_WRITE_PAGE,
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_NOT_VERSIONING_PERSPECTIVE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW,
                'se.has.create.permission.on.contentslot.type'
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.revert.to.global.or.shared.slot.link'],
            rules: [
                RULE_PERMISSION_WRITE_PAGE,
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_NOT_VERSIONING_PERSPECTIVE,
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW,
                'se.has.delete.permission.on.contentslot.type'
            ]
        });
    }

    private registerVersionPermissions(): void {
        this.permissionService.registerPermission({
            aliases: ['se.version.page'],
            rules: [
                RULE_PERMISSION_WRITE_PAGE,
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_CATALOG_NON_ACTIVE,
                RULE_PERMISSION_HAS_READ_PERMISSION_ON_VERSION_TYPE,
                RULE_PERMISSION_HAS_READ_TYPE_PERMISSION_ON_CURRENT_PAGE
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.edit.version.page'],
            rules: [
                'se.write.to.current.catalog.version',
                'se.has.change.permission.on.version.type',
                RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.create.version.page'],
            rules: [
                'se.version.page.not.selected',
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
                RULE_PERMISSION_HAS_CREATE_PERMISSION_ON_VERSION_TYPE,
                RULE_PERMISSION_HAS_READ_TYPE_PERMISSION_ON_CURRENT_PAGE
            ]
        });

        const rulesForVersionRollback = [
            RULE_PERMISSION_VERSION_PAGE_SELECTED,
            RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE,
            RULE_PERMISSION_HAS_READ_PERMISSION_ON_VERSION_TYPE,
            RULE_PERMISSION_HAS_CREATE_PERMISSION_ON_VERSION_TYPE,
            RULE_PERMISSION_HAS_CHANGE_TYPE_PERMISSION_ON_CURRENT_PAGE
        ];
        this.permissionService.registerPermission({
            aliases: ['se.rollback.version.page'],
            rules: rulesForVersionRollback
        });

        this.permissionService.registerPermission({
            // the page versions menu button should be visible even if a version is not selected
            aliases: ['se.rollback.version.page.versions.menu'],
            rules: rulesForVersionRollback.filter(
                (rule) => rule !== RULE_PERMISSION_VERSION_PAGE_SELECTED
            )
        });

        this.permissionService.registerPermission({
            aliases: ['se.delete.version.page'],
            rules: ['se.has.remove.permission.on.version.type']
        });
    }

    private registerWorkflowPermissions(): void {
        this.permissionService.registerPermission({
            aliases: ['se.start.page.workflow'],
            rules: [
                'se.has.create.permission.on.workflow.type',
                'se.catalogversion.has.workflows.enabled',
                'se.current.page.has.no.active.workflow'
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.view.page.workflowMenu'],
            rules: [
                'se.has.read.permission.on.workflow.type',
                RULE_PERMISSION_CURRENT_PAGE_HAS_ACTIVE_WORKFLOW
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.cancel.page.workflowMenu'],
            rules: [
                RULE_PERMISSION_HAS_CHANGE_PERMISSION_ON_WORKFLOW_TYPE,
                RULE_PERMISSION_CURRENT_PAGE_HAS_ACTIVE_WORKFLOW,
                'se.has.change.permission.on.workflow.status'
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.edit.workflow.workflowMenu'],
            rules: [
                RULE_PERMISSION_HAS_CHANGE_PERMISSION_ON_WORKFLOW_TYPE,
                RULE_PERMISSION_CURRENT_PAGE_HAS_ACTIVE_WORKFLOW,
                'se.has.change.permission.on.workflow.description'
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.force.page.approval'],
            rules: [
                RULE_PERMISSION_SYNC_CATALOG,
                'se.has.change.permission.on.page.approval.status',
                RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE
            ]
        });

        this.permissionService.registerPermission({
            aliases: ['se.show.page.status'],
            rules: [RULE_PERMISSION_CATALOG_NON_ACTIVE, RULE_PERMISSION_PAGE_BELONGS_TO_EXPERIENCE]
        });

        this.permissionService.registerPermission({
            aliases: ['se.act.on.page.in.workflow'],
            rules: [RULE_PERMISSION_CURRENT_USER_CAN_ACT_ON_PAGE_IN_WORKFLOW]
        });
    }
}
