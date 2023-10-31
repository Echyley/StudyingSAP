/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
 package de.hybris.platform.webhookbackoffice.widgets.editors;

 import de.hybris.platform.apiregistryservices.dao.impl.DefaultDestinationDao;
 import de.hybris.platform.apiregistryservices.enums.DestinationChannel;
 import de.hybris.platform.apiregistryservices.model.ConsumedDestinationModel;

 import javax.annotation.Resource;

 import java.util.List;
 import java.util.stream.Collectors;

 import com.hybris.cockpitng.editor.defaultreferenceeditor.DefaultReferenceEditor;
 import com.hybris.cockpitng.search.data.pageable.PageableList;


 /**
  * Custom WebhookConfiguration ConsumedDestination Editor.
  */
 public class WebhookConsumedDestinationEditor extends DefaultReferenceEditor<ConsumedDestinationModel>
 {
	 @Resource
	 private DefaultDestinationDao<ConsumedDestinationModel> destinationDao;

	 /**
	  * Update the references list to show only consumed destinations that are assigned to destination targets
	  * with WEBHOOKSERVICES destination channels.
	  */
	 @Override
	 public void updateReferencesListBoxModel()
	 {
		 final List<ConsumedDestinationModel> consumedDestinations = destinationDao.findAllConsumedDestinations().stream()
				 .filter(this::isWebhookDestination).collect(Collectors.toList());
		 this.pageable = new PageableList<>(consumedDestinations, this.pageSize);
	 }

	 private boolean isWebhookDestination(final ConsumedDestinationModel consumedDestination)
	 {
		 return consumedDestination.getDestinationTarget() != null && DestinationChannel.WEBHOOKSERVICES == consumedDestination
				 .getDestinationTarget().getDestinationChannel();
	 }
 }
