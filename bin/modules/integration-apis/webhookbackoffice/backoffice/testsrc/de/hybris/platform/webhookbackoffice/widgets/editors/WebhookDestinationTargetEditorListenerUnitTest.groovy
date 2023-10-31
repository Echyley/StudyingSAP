/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.webhookbackoffice.widgets.editors

import com.hybris.backoffice.widgets.notificationarea.event.NotificationEvent.Level
import com.hybris.cockpitng.editors.EditorListener
import com.hybris.cockpitng.util.notifications.NotificationService
import de.hybris.bootstrap.annotations.UnitTest
import de.hybris.platform.apiregistryservices.model.DestinationTargetModel
import de.hybris.platform.testframework.JUnitPlatformSpecification
import org.junit.Test
import org.zkoss.lang.Strings

import static de.hybris.platform.webhookbackoffice.constants.WebhookbackofficeConstants.NOTIFICATION_TYPE

@UnitTest
class WebhookDestinationTargetEditorListenerUnitTest extends JUnitPlatformSpecification {
	def originalListener = Mock EditorListener<DestinationTargetModel>
	def notificationService = Mock NotificationService
	def listener = new WebhookDestinationTargetEditorListener(originalListener, notificationService)

	@Test
	def "Original listeners are called."() {
		given:
		def destinationTarget = new DestinationTargetModel()
		def string = "someString"
		def object = new Object()
		when:
		listener.onValueChanged(destinationTarget)
		listener.onEditorEvent(string)
		listener.sendSocketOutput(string, object)
		then:
		1 * originalListener.onValueChanged(destinationTarget)
		1 * originalListener.onEditorEvent(string)
		1 * originalListener.sendSocketOutput(string, object)
	}

	@Test
	def "Notification #isOrIsNot displayed when destination target is #destinationTarget"() {
		when:
		listener.onValueChanged(destinationTarget)

		then:
		timesNotified * notificationService.notifyUser(Strings.EMPTY, NOTIFICATION_TYPE, Level.WARNING, _);

		where:
		isOrIsNot | timesNotified | destinationTarget
		"is not"  | 0             | null
		"is not"  | 0             | destinationTarget("webhookServices")
		"is"      | 1             | destinationTarget("anotherDestinationTarget")
	}

	DestinationTargetModel destinationTarget(id) {
		def destinationTarget = new DestinationTargetModel()
		destinationTarget.setId(id)
		return destinationTarget
	}
}
