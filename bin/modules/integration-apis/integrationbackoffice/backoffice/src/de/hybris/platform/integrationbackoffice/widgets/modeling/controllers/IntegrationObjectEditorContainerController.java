/*
 * Copyright (c) 2023 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.integrationbackoffice.widgets.modeling.controllers;

import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.CLEAR_LIST_BOX;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.CLEAR_TREE;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.CREATE_DYNAMIC_TREE_NODE_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.CREATE_TREE_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.LOAD_IO_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.OPEN_CLONE_MODAL_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.OPEN_ITEM_TYPE_MODAL_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorConstants.SELECTED_ITEM_OUTPUT_SOCKET;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorUtils.isClassificationAttributePresent;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorValidator.validateDefinitions;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorValidator.validateHasKey;
import static de.hybris.platform.integrationbackoffice.widgets.modeling.utility.EditorValidator.validateHasNoDuplicateAttributeNames;

import de.hybris.platform.catalog.enums.ClassificationAttributeTypeEnum;
import de.hybris.platform.catalog.model.classification.ClassAttributeAssignmentModel;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.integrationbackoffice.constants.IntegrationbackofficeConstants;
import de.hybris.platform.integrationbackoffice.exceptions.ModelingAbstractAttributeAutoCreateOrPartOfException;
import de.hybris.platform.integrationbackoffice.widgets.common.controllers.AbstractIntegrationEditorContainerController;
import de.hybris.platform.integrationbackoffice.widgets.common.data.IntegrationFilterState;
import de.hybris.platform.integrationbackoffice.widgets.common.utility.EditorAccessRights;
import de.hybris.platform.integrationbackoffice.widgets.modals.builders.AuditReportBuilder;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.CreateIntegrationObjectModalData;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.CreateVirtualAttributeModalData;
import de.hybris.platform.integrationbackoffice.widgets.modals.data.SelectedClassificationAttributesData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.CreateTreeData;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinition;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectDefinitionDuplicationMap;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.IntegrationObjectPresentation;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.AbstractListItemDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemClassificationAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemDTOMissingDescriptorModelException;
import de.hybris.platform.integrationbackoffice.widgets.modeling.data.dto.ListItemVirtualAttributeDTO;
import de.hybris.platform.integrationbackoffice.widgets.modeling.providers.ClassificationAttributeQualifierProvider;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectDefinitionConverter;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectDefinitionTrimmer;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.IntegrationObjectPresentationPopulator;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.ReadService;
import de.hybris.platform.integrationbackoffice.widgets.modeling.services.WriteService;
import de.hybris.platform.integrationservices.exception.AbstractAttributeAutoCreateOrPartOfException;
import de.hybris.platform.integrationservices.model.IntegrationObjectItemModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectModel;
import de.hybris.platform.integrationservices.model.IntegrationObjectVirtualAttributeDescriptorModel;
import de.hybris.platform.integrationservices.util.Log;
import de.hybris.platform.servicelayer.exceptions.ModelRemovalException;
import de.hybris.platform.servicelayer.exceptions.ModelSavingException;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.assertj.core.util.Arrays;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.zkoss.lang.Strings;
import org.zkoss.zhtml.Messagebox;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.annotation.WireVariable;

import com.hybris.cockpitng.annotations.SocketEvent;
import com.hybris.cockpitng.core.events.CockpitEventQueue;
import com.hybris.cockpitng.core.events.impl.DefaultCockpitEvent;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectCRUDHandler;
import com.hybris.cockpitng.dataaccess.facades.object.ObjectFacade;
import com.hybris.cockpitng.util.notifications.NotificationService;
import com.hybris.cockpitng.util.notifications.event.NotificationEvent;

/**
 * Controls the functionality of the integration object modeling editor
 */
public class IntegrationObjectEditorContainerController extends AbstractIntegrationEditorContainerController
{
	private static final String RECEIVE_OBJECT_COMBOBOX_IN_SOCKET = "receiveObjectComboBox";
	private static final String CREATE_INTEGRATION_OBJECT_EVENT_IN_SOCKET = "createIntegrationObjectEvent";
	private static final String SAVE_EVENT_IN_SOCKET = "saveEvent";
	private static final String RECEIVE_DELETE_IN_SOCKET = "receiveDelete";
	private static final String METADATA_MODAL_EVENT_IN_SOCKET = "metadataModalEvent";
	private static final String OPEN_ITEM_TYPE_IOI_MODAL_EVENT_IN_SOCKET = "openItemTypeIOIModalEvent";
	private static final String OPEN_VIRTUAL_ATTRIBUTE_MODAL_IN_SOCKET = "openVirtualAttributeModal";
	private static final String AUDIT_REPORT_EVENT_IN_SOCKET = "auditReportEvent";
	private static final String RECEIVE_CLONE_IN_SOCKET = "receiveClone";
	private static final String SAVE_BUTTON_ITEM_TYPE_MATCH_IN_SOCKET = "saveButtonItemTypeMatch";
	private static final String CLONE_INTEGRATION_OBJECT_EVENT_IN_SOCKET = "cloneIntegrationObjectEvent";
	private static final String ADD_CLASSIFICATION_ATTRIBUTES_EVENT_IN_SOCKET = "addClassificationAttributesEvent";
	private static final String ADD_VIRTUAL_ATTRIBUTE_EVENT_IN_SOCKET = "addVirtualAttributeEvent";
	private static final String OPEN_VIRTUAL_ATTRIBUTE_MODAL_OUT_SOCKET = "openVirtualAttributeModal";

	private static final Logger LOG = Log.getLogger(IntegrationObjectEditorContainerController.class);

	@WireVariable
	private transient CockpitEventQueue cockpitEventQueue;
	@WireVariable
	private transient EditorAccessRights editorAccessRights;
	@WireVariable
	private transient AuditReportBuilder auditReportBuilder;
	@WireVariable
	private transient ClassificationAttributeQualifierProvider classificationAttributeQualifierProvider;
	@WireVariable
	protected transient IntegrationObjectPresentationPopulator integrationObjectPresentationPopulator;
	private transient IntegrationObjectPresentation iOPresentation;
	@WireVariable
	private transient IntegrationObjectDefinitionTrimmer integrationObjectDefinitionTrimmer;
	@WireVariable
	private transient IntegrationObjectDefinitionConverter integrationObjectDefinitionConverter;
	@WireVariable
	private transient ReadService readService;
	@WireVariable
	private transient WriteService writeService;
	@WireVariable
	private transient NotificationService notificationService;

	@Override
	public void initialize(final Component component)
	{
		super.initialize(component);
		iOPresentation = integrationObjectPresentationPopulator.getIOEditorPresentation();
		iOPresentation.setEditModeFlag(editorAccessRights.isUserAdmin());
		iOPresentation.resetState();
	}

	@Required
	public void setEditorPresentation(final IntegrationObjectPresentation iOPresentation)
	{
		this.iOPresentation = iOPresentation;
	}

	@SocketEvent(socketId = RECEIVE_OBJECT_COMBOBOX_IN_SOCKET)
	public void loadObject(final IntegrationObjectModel obj)
	{
		iOPresentation.setSelectedIntegrationObject(obj);
		sendOutput(LOAD_IO_OUTPUT_SOCKET, "");
		iOPresentation.setModified(false);
	}

	@SocketEvent(socketId = CREATE_INTEGRATION_OBJECT_EVENT_IN_SOCKET)
	public void createNewIntegrationObject(final CreateIntegrationObjectModalData data)
	{
		iOPresentation.setModified(false);
		iOPresentation.setAttributeDuplicationMap(new IntegrationObjectDefinitionDuplicationMap());
		iOPresentation.setFilterState(IntegrationFilterState.SHOW_ALL);
		iOPresentation.setSubtypeDataSet(new HashSet<>());
		sendOutput(FILTER_STATE_CHANGE_OUT_SOCKET, IntegrationFilterState.SHOW_ALL);

		final CreateTreeData createTreeData = new CreateTreeData(data.getComposedTypeModel());
		sendOutput(CREATE_TREE_OUTPUT_SOCKET, createTreeData);
		final IntegrationObjectDefinition trimmedMap = createIntegrationObject(data);

		if (!("").equals(validateHasKey(trimmedMap)))
		{
			notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.serviceCreatedNeedsFurtherConfig"));
		}
		else
		{
			notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.SUCCESS,
					getLabel("integrationbackoffice.editMode.info.msg.serviceCreated"));
		}
	}

	@SocketEvent(socketId = SAVE_EVENT_IN_SOCKET)
	public void updateObject(final String message)
	{
		final IntegrationObjectDefinition trimmedAttributesMap = integrationObjectDefinitionTrimmer.trimMap(
				iOPresentation.getComposedTypeTree(), iOPresentation.getCurrentAttributesMap());
		if (validation(trimmedAttributesMap))
		{
			final IntegrationObjectModel selectedIO = iOPresentation.getSelectedIntegrationObject();
			iOPresentation.setItemTypeMatchMap(iOPresentation.groupItemTypeMatchForIntegrationObject(selectedIO));
			final IntegrationObjectModel ioModel = writeService.createDefinitions(selectedIO, trimmedAttributesMap,
					selectedIO.getRootItem().getCode());
			iOPresentation.assignItemTypeMatchForIntegrationObject(ioModel);
			iOPresentation.getItemTypeMatchMap().clear();

			if (iOPresentation.hasRoot())
			{
				persistenceSetup(ioModel);
			}
			else
			{
				Messagebox.show(getLabel("integrationbackoffice.editMode.warning.msg.saveConfirmation"),
						getLabel("integrationbackoffice.editMode.warning.title.saveConfirmation"), new Messagebox.Button[]
								{ Messagebox.Button.OK, Messagebox.Button.CANCEL }, new String[]
								{ getLabel("integrationbackoffice.editMode.button.saveDefinition") }, null, null, clickEvent -> {
							if (clickEvent.getButton() == Messagebox.Button.OK)
							{
								persistenceSetup(ioModel);
								iOPresentation.setModified(true);
							}
						});
			}
		}
	}

	@SocketEvent(socketId = REFRESH_CONTAINER_IN_SOCKET)
	public void refreshButtonOnClick(final String message)
	{
		if (iOPresentation.getSelectedIntegrationObject() != null)
		{
			loadObject(iOPresentation.getSelectedIntegrationObject());
			iOPresentation.setModified(false);
			iOPresentation.getAttributeDuplicationMap().clear();
		}
	}

	@SocketEvent(socketId = RECEIVE_DELETE_IN_SOCKET)
	public void deleteActionOnPerform()
	{
		if (iOPresentation.getSelectedIntegrationObject() != null)
		{
			Messagebox.show(
					getLabel("integrationbackoffice.editMode.info.msg.deleteConfirmation",
							Arrays.array(iOPresentation.getSelectedIntegrationObject().getCode())),
					getLabel("integrationbackoffice.editMode.info.title.deleteConfirmation"), new Messagebox.Button[]
							{ Messagebox.Button.OK, Messagebox.Button.CANCEL }, null, null, null, clickEvent -> {
						if (clickEvent.getButton() == Messagebox.Button.OK)
						{
							processIntegrationObjectDeletionAction();
						}
					});
		}
		else
		{
			showNoServiceSelectedMessage();
		}
	}

	@SocketEvent(socketId = METADATA_MODAL_EVENT_IN_SOCKET)
	public void metaDataModelRequestHandler(final String message)
	{
		sendCurrentIntegrationObject("openMetadataViewer");
	}

	@SocketEvent(socketId = OPEN_ITEM_TYPE_IOI_MODAL_EVENT_IN_SOCKET)
	public void itemTypeIOIModalRequestHandler(final String message)
	{
		sendCurrentIntegrationObject(OPEN_ITEM_TYPE_MODAL_OUTPUT_SOCKET);
	}

	@SocketEvent(socketId = OPEN_VIRTUAL_ATTRIBUTE_MODAL_IN_SOCKET)
	public void virtualAttributeModalRequestHandler(final String message)
	{
		sendOutput(OPEN_VIRTUAL_ATTRIBUTE_MODAL_OUT_SOCKET, iOPresentation.getSelectedTypeAttributes());
	}

	@SocketEvent(socketId = AUDIT_REPORT_EVENT_IN_SOCKET)
	public void downloadIntegrationObjectReport()
	{
		if (iOPresentation.isValidIOSelected())
		{
			final Map<String, InputStream> auditReportMapRes = auditReportBuilder.generateAuditReport(
					iOPresentation.getSelectedIntegrationObject());
			if (!auditReportMapRes.isEmpty())
			{
				LOG.info("Audit Report has been Created Successfully!");
				auditReportBuilder.downloadAuditReport(auditReportMapRes);
			}
			else
			{
				LOG.info("Audit Report Creation Failed!");
			}
		}
		else
		{
			showNoServiceSelectedMessage();
		}
	}

	@SocketEvent(socketId = RECEIVE_CLONE_IN_SOCKET)
	public void cloneActionOnPerform()
	{
		if (iOPresentation.isValidIOSelected())
		{
			if (!iOPresentation.isModified())
			{
				sendOutput(OPEN_CLONE_MODAL_OUTPUT_SOCKET, iOPresentation.getSelectedIntegrationObject());
			}
			else
			{
				notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.WARNING,
						getLabel("integrationbackoffice.editMode.warning.msg.saveBeforeCloning"));
			}
		}
		else
		{
			showNoServiceSelectedMessage();
		}
	}

	@SocketEvent(socketId = SAVE_BUTTON_ITEM_TYPE_MATCH_IN_SOCKET)
	public void itemTypeMatchSaveHandler(final Collection<IntegrationObjectItemModel> integrationObjectItemModels)
	{
		LOG.debug("The number of items to be saved {}", integrationObjectItemModels.size());
		writeService.persistIntegrationObjectItems(integrationObjectItemModels);
		cockpitEventQueue
				.publishEvent(
						new DefaultCockpitEvent(ObjectFacade.OBJECTS_UPDATED_EVENT,
								iOPresentation.getSelectedIntegrationObject(),
								null));
		notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.SUCCESS, getLabel("integrationbackoffice.itemTypeMatchIOIModal.msg.save"));
	}

	@SocketEvent(socketId = CLONE_INTEGRATION_OBJECT_EVENT_IN_SOCKET)
	public void cloneIntegrationObject(final CreateIntegrationObjectModalData data)
	{
		if (data.getComposedTypeModel() != null)
		{
			filterStateChange(IntegrationFilterState.SHOW_ALL);
			sendOutput(FILTER_STATE_CHANGE_OUT_SOCKET, IntegrationFilterState.SHOW_ALL);
			iOPresentation.setModified(false);

			final IntegrationObjectModel integrationObjectClone = writeService.cloneIntegrationObject(
					iOPresentation.getSelectedIntegrationObject(),
					writeService.createIntegrationObject(data.getName(), data.getType()));
			writeService.persistIntegrationObject(integrationObjectClone);
			cockpitEventQueue.publishEvent(new DefaultCockpitEvent(ObjectCRUDHandler.OBJECT_CREATED_EVENT, integrationObjectClone,
					null));
			iOPresentation.setSelectedIntegrationObject(integrationObjectClone);

			final IntegrationObjectDefinition trimmedMap = integrationObjectDefinitionTrimmer.trimMap(
					iOPresentation.getComposedTypeTree(), iOPresentation.getCurrentAttributesMap());
			if (!("").equals(validateHasKey(trimmedMap)))
			{
				notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.WARNING,
						getLabel("integrationbackoffice.editMode.warning.msg.serviceClonedNeedsFurtherConfig"));
			}
			else
			{
				notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.SUCCESS, getLabel("integrationbackoffice.editMode.info.msg.serviceCloned"));
			}
		}
		else
		{
			notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.integrationContextLost"));
		}
	}

	@SocketEvent(socketId = FILTER_STATE_CHANGE_IN_SOCKET)
	public void filterStateChange(final IntegrationFilterState state)
	{
		iOPresentation.setFilterState(state);
		final IntegrationObjectItemModel root = iOPresentation.getSelectedIntegrationObject().getRootItem();
		if (root != null)
		{
			recreateTree(root.getType());
		}
		else
		{
			clearTreeAndListBox();
			sendOutput(SELECTED_ITEM_OUTPUT_SOCKET, iOPresentation.getSelectedTypeInstance());
			notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.invalidObjectLoaded"));
		}
	}

	@SocketEvent(socketId = ADD_CLASSIFICATION_ATTRIBUTES_EVENT_IN_SOCKET)
	public void handleAddClassificationAttributes(final SelectedClassificationAttributesData selectedClassificationAttributesData)
	{
		final String typeCode = iOPresentation.getSelectedTypeInstance().getType().getCode();
		if (readService.isProductType(typeCode))
		{
			final List<AbstractListItemDTO> newAttributes = addClassificationAttributes(selectedClassificationAttributesData);
			iOPresentation.setSelectedTypeAttributes(newAttributes);
			iOPresentation.setAttributeDuplicationMap(iOPresentation.compileDuplicationMap());
			updateUIToModifiedState();
		}
		else
		{
			notificationService.notifyUser(IntegrationbackofficeConstants.EXTENSIONNAME,
					IntegrationbackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.cannotAddClassificationAttributeToNonProductType"));
		}
	}

	@SocketEvent(socketId = ADD_VIRTUAL_ATTRIBUTE_EVENT_IN_SOCKET)
	public void addVirtualAttribute(final CreateVirtualAttributeModalData data)
	{
		final IntegrationObjectVirtualAttributeDescriptorModel descriptorModel;
		if (data.getIsDescriptorPersisted())
		{
			final List<IntegrationObjectVirtualAttributeDescriptorModel> descriptorModelList =
					readService.getVirtualAttributeDescriptorModelsByCode(data.getVadmCode());
			if (descriptorModelList.isEmpty())
			{
				showNoVirtualAttributeDescriptorMessage();
				return;
			}
			descriptorModel = descriptorModelList.get(0);
		}
		else
		{
			descriptorModel = writeService.persistVirtualAttributeDescriptor(data.getVadmCode(), data.getScriptLocation(),
					data.getType());
			notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.SUCCESS,
					getLabel("integrationbackoffice.virtualAttributeDescriptorCreate.info.msg.VADMCreated"));
		}

		addDtoToList(data, descriptorModel);
		iOPresentation.setAttributeDuplicationMap(iOPresentation.compileDuplicationMap());

		updateUIToModifiedState();
	}

	/**
	 * Updates state of UI after modification
	 */
	private void updateUIToModifiedState()
	{
		iOPresentation.setModified(true);
		sendOutput(SELECTED_ITEM_OUTPUT_SOCKET, iOPresentation.getSelectedTypeInstance());
		sendOutput(ENABLE_SAVE_BUTTON_OUT_SOCKET, true);
	}

	/**
	 * Adds newly created virtual attribute to dto list
	 *
	 * @param data            Contains data pertaining to new virtual attribute from modal
	 * @param descriptorModel Virtual attribute descriptor model
	 */
	private void addDtoToList(final CreateVirtualAttributeModalData data,
	                          final IntegrationObjectVirtualAttributeDescriptorModel descriptorModel)
	{
		final ListItemVirtualAttributeDTO dto = ListItemVirtualAttributeDTO.builder(descriptorModel)
		                                                                   .withSelected(true)
		                                                                   .withAttributeName(data.getAlias())
		                                                                   .build();
		final List<AbstractListItemDTO> attributes = iOPresentation.getSelectedTypeAttributes();
		attributes.add(dto);
		iOPresentation.setSelectedTypeAttributes(attributes);
		notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.SUCCESS,
				getLabel("integrationbackoffice.virtualAttributeDescriptorCreate.info.msg.VAAddedToListbox",
						Arrays.array(data.getAlias())));
	}

	private void sendCurrentIntegrationObject(final String socketID)
	{
		if (iOPresentation.isValidIOSelected())
		{
			final IntegrationObjectDefinition trimmedMap = integrationObjectDefinitionTrimmer.trimMap(
					iOPresentation.getComposedTypeTree(), iOPresentation.getCurrentAttributesMap());
			if (!("").equals(validateHasKey(trimmedMap)))
			{
				showObjectLoadedFurtherConfigurationMessage();
			}
			else
			{
				sendOutput(socketID, iOPresentation.getSelectedIntegrationObject());
			}
		}
		else
		{
			showNoServiceSelectedMessage();
		}
	}

	private void persistenceSetup(final IntegrationObjectModel ioModel)
	{
		try
		{
			persistIntegrationObject(ioModel);
		}
		catch (final ModelSavingException exception)
		{
			if (exception.getCause() instanceof AbstractAttributeAutoCreateOrPartOfException)
			{
				final AbstractAttributeAutoCreateOrPartOfException cause =
						(AbstractAttributeAutoCreateOrPartOfException) exception.getCause();
				notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.WARNING, new ModelingAbstractAttributeAutoCreateOrPartOfException(cause,
								cause.getTypeCode(), cause.getAttributeName()));
			}
			else
			{
				notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
						NotificationEvent.Level.WARNING, exception);
			}
		}
	}

	private void persistIntegrationObject(final IntegrationObjectModel ioModel)
	{
		writeService.persistIntegrationObject(ioModel);
		cockpitEventQueue.publishEvent(new DefaultCockpitEvent(ObjectFacade.OBJECTS_UPDATED_EVENT,
				iOPresentation.getSelectedIntegrationObject(), null));
		notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.SUCCESS, getLabel("integrationbackoffice.editMode.info.msg.save"));
		iOPresentation.setModified(false);
		sendOutput(ENABLE_SAVE_BUTTON_OUT_SOCKET, false);
	}

	private IntegrationObjectDefinition createIntegrationObject(final CreateIntegrationObjectModalData data)
	{
		final IntegrationObjectModel selectedIO = writeService.createIntegrationObject(data.getName(), data.getType());
		iOPresentation.setSelectedIntegrationObject(selectedIO);

		final IntegrationObjectDefinition trimmedMap = integrationObjectDefinitionTrimmer.trimMap(
				iOPresentation.getComposedTypeTree(), iOPresentation.getCurrentAttributesMap());
		final IntegrationObjectModel ioModel = writeService.createDefinitions(iOPresentation.getSelectedIntegrationObject(),
				trimmedMap, data.getComposedTypeModel().getCode());
		iOPresentation.assignItemTypeMatchForIntegrationObject(ioModel);
		writeService.persistIntegrationObject(ioModel);
		cockpitEventQueue.publishEvent(
				new DefaultCockpitEvent(ObjectCRUDHandler.OBJECT_CREATED_EVENT, iOPresentation.getSelectedIntegrationObject(),
						null));
		return trimmedMap;
	}

	private boolean validation(final IntegrationObjectDefinition trimmedAttributesMap)
	{
		final String VALIDATION_MESSAGE_TITLE = getLabel("integrationbackoffice.editMode.error.title.validation");
		final String VALIDATION_DUPLICATION_TITLE = getLabel("integrationbackoffice.editMode.error.title.duplicationValidation");
		String validationError;
		validationError = validateDefinitions(trimmedAttributesMap);
		if (!("").equals(validationError))
		{
			Messagebox.show(
					getLabel("integrationbackoffice.editMode.error.msg.definitionValidation", Arrays.array(validationError)),
					VALIDATION_MESSAGE_TITLE, 1, Messagebox.ERROR);
			return false;
		}
		validationError = validateHasKey(trimmedAttributesMap);
		if (!("").equals(validationError))
		{
			Messagebox.show(getLabel("integrationbackoffice.editMode.error.msg.uniqueValidation", Arrays.array(validationError)),
					VALIDATION_MESSAGE_TITLE, 1, Messagebox.ERROR);
			return false;
		}
		validationError = validateHasNoDuplicateAttributeNames(iOPresentation.getAttributeDuplicationMap());
		if (!("").equals(validationError))
		{
			Messagebox.show(
					getLabel("integrationbackoffice.editMode.error.msg.duplicationValidation", Arrays.array(validationError)),
					VALIDATION_DUPLICATION_TITLE, 1, Messagebox.ERROR);
			return false;
		}

		return true;
	}

	private void showNoServiceSelectedMessage()
	{
		notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.WARNING, getLabel("integrationbackoffice.editMode.warning.msg.noServiceSelected"));
	}

	private void showObjectLoadedFurtherConfigurationMessage()
	{
		notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.WARNING,
				getLabel("integrationbackoffice.editMode.warning.msg.serviceLoadedNeedsFurtherConfig"));
	}

	private void showNoVirtualAttributeDescriptorMessage()
	{
		notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.WARNING,
				getLabel("integrationbackoffice.virtualAttributeDescriptorCreate.error.msg.noDescriptorFound"));
	}

	private void showMissingTypeMessage(final String sourceMessage)
	{
		notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
				NotificationEvent.Level.WARNING,
				getLabel("integrationbackoffice.editMode.error.msg.missingType", Arrays.array(sourceMessage)));
	}

	private void processIntegrationObjectDeletionAction()
	{
		try
		{
			writeService.deleteIntegrationObject(iOPresentation.getSelectedIntegrationObject());
			notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.SUCCESS,
					getLabel("integrationbackoffice.editMode.info.msg.delete"));
			cockpitEventQueue.publishEvent(
					new DefaultCockpitEvent(ObjectFacade.OBJECTS_DELETED_EVENT,
							iOPresentation.getSelectedIntegrationObject(), null));
		}
		catch (final ModelRemovalException exception)
		{
			notificationService.notifyUser(Strings.EMPTY, IntegrationbackofficeConstants.NOTIFICATION_TYPE,
					NotificationEvent.Level.WARNING, exception);
		}
	}

	/**
	 * Instantiates a {@link CreateTreeData} with a definition map that depends on whether modeling is in edit mode or not.
	 * Then sends the {@link CreateTreeData} to the editor tree controller.
	 *
	 * @param rootType the {@link ComposedTypeModel} which will be used to create the root node of the tree.
	 */
	private void recreateTree(final ComposedTypeModel rootType)
	{
		final CreateTreeData createTreeData = iOPresentation.isEditModeFlag()
				? createTreeDataFromCurrentContext(rootType)
				: createTreeDataFromSavedDefinition(rootType);

		if (createTreeData != null)
		{
			sendOutput(CREATE_TREE_OUTPUT_SOCKET, createTreeData);
		}
	}

	/**
	 * Creates a {@link CreateTreeData} with a trimmed {@link IntegrationObjectDefinition} of current changes.
	 *
	 * @param rootType the {@link ComposedTypeModel} which will be used to create the root node of the tree.
	 * @return a {@link CreateTreeData} with all the current definition changes stored.
	 */
	private CreateTreeData createTreeDataFromCurrentContext(final ComposedTypeModel rootType)
	{
		final IntegrationObjectDefinition fullDefinition = iOPresentation.getCurrentAttributesMap();
		final IntegrationObjectDefinition trimmedDefinition = integrationObjectDefinitionTrimmer.trimMap(
				iOPresentation.getComposedTypeTree(), fullDefinition);
		return new CreateTreeData(rootType, trimmedDefinition);
	}

	/**
	 * Creates a {@link CreateTreeData} with a definition map converted from the saved definition of the integration object.
	 *
	 * @param rootType the {@link ComposedTypeModel} which will be used to create the root node of the tree.
	 * @return a {@link CreateTreeData} with the saved definition stored. Current definition changes will be lost.
	 */
	private CreateTreeData createTreeDataFromSavedDefinition(final ComposedTypeModel rootType)
	{
		try
		{
			final IntegrationObjectModel integrationObject = iOPresentation.getSelectedIntegrationObject();
			final IntegrationObjectDefinition integrationObjectDefinition =
					integrationObjectDefinitionConverter.toDefinitionMap(integrationObject);
			return new CreateTreeData(rootType, integrationObjectDefinition);
		}
		catch (final ListItemDTOMissingDescriptorModelException e)
		{
			LOG.error(e.getMessage());
			showMissingTypeMessage(e.getMessage());
			return null;
		}
	}

	/**
	 * Adds classification attributes to already selected attributes of selected type instance.
	 * Ignores and notifies for classification attributes already selected.
	 *
	 * @param classificationAttributesData the {@link SelectedClassificationAttributesData} containing the classification attributes to add.
	 * @return a list of already selected attributes with new classification attributes added.
	 */
	private List<AbstractListItemDTO> addClassificationAttributes(
			final SelectedClassificationAttributesData classificationAttributesData)
	{
		boolean attributeAlreadyPresent = false;
		final List<AbstractListItemDTO> attributes = iOPresentation.getSelectedTypeAttributes();
		for (final ClassAttributeAssignmentModel assignment : classificationAttributesData.getAssignments())
		{
			if (!isClassificationAttributePresent(assignment, attributes))
			{
				final String alias = classificationAttributesData.isUseFullQualifier()
						? classificationAttributeQualifierProvider.provide(assignment)
						: Strings.EMPTY;
				final ListItemClassificationAttributeDTO dto = createListItemClassificationAttributeDTO(alias, assignment);
				attributes.add(dto);
			}
			else if (Boolean.FALSE.equals(assignment.getMandatory()))
			{
				attributeAlreadyPresent = true;
			}
		}
		if (attributeAlreadyPresent)
		{
			notificationService.notifyUser(IntegrationbackofficeConstants.EXTENSIONNAME,
					IntegrationbackofficeConstants.NOTIFICATION_TYPE, NotificationEvent.Level.WARNING,
					getLabel("integrationbackoffice.editMode.warning.msg.attributeAlreadyPresent"));
		}
		return attributes;
	}

	private ListItemClassificationAttributeDTO createListItemClassificationAttributeDTO(final String alias,
	                                                                                    final ClassAttributeAssignmentModel assignment)
	{
		final ListItemClassificationAttributeDTO dto = ListItemClassificationAttributeDTO.builder(assignment)
		                                                                                 .withSelected(true)
		                                                                                 .withAttributeName(alias).build();
		if (ClassificationAttributeTypeEnum.REFERENCE == assignment.getAttributeType())
		{
			sendOutput(CREATE_DYNAMIC_TREE_NODE_OUTPUT_SOCKET, dto);
		}
		return dto;
	}

	private void clearTreeAndListBox()
	{
		sendOutput(CLEAR_TREE, "");
		sendOutput(CLEAR_LIST_BOX, "");
	}
}

