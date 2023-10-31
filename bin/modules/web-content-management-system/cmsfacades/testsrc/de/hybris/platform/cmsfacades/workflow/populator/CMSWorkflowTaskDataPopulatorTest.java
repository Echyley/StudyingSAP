/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.workflow.populator;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.model.contents.CMSItemModel;
import de.hybris.platform.cms2.model.pages.AbstractPageModel;
import de.hybris.platform.cms2.workflow.service.CMSWorkflowParticipantService;
import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;
import de.hybris.platform.cmsfacades.data.CMSWorkflowActionData;
import de.hybris.platform.cmsfacades.data.CMSWorkflowTaskData;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.type.ComposedTypeModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.type.TypeService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowItemAttachmentModel;
import de.hybris.platform.workflow.model.WorkflowModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.google.common.collect.Lists;


@UnitTest
@RunWith(MockitoJUnitRunner.Silent.class)
public class CMSWorkflowTaskDataPopulatorTest
{
	private final String PAGE_UID = "page-uid";
	private final String PAGE_NAME = "page-name";
	private final String CATALOG_ID = "catalog-id";
	private final String CATALOG_NAME_EN = "catalog-name_EN";
	private final String CATALOG_NAME_FR = "catalog-name_FR";
	private final String CATALOG_VERSION = "catalog-version";

	// LocalizedPopulator
	private static final String EN = Locale.ENGLISH.getLanguage();
	private static final String FR = Locale.FRENCH.getLanguage();

	@Mock
	private TypeService typeService;
	@Mock
	private UserService userService;
	@Mock
	private CMSWorkflowParticipantService cmsWorkflowParticipantService;
	@Mock
	private Converter<WorkflowActionModel, CMSWorkflowActionData> cmsWorkflowActionDataConverter;
	@Mock
	private WorkflowModel workflowModel;
	@Mock
	private List<CMSWorkflowTaskData> target;
	@Mock
	private WorkflowItemAttachmentModel attachmentModel;
	@Mock
	private ComposedTypeModel composedTypeModel;
	@Mock
	private CMSItemModel itemModel;
	@Mock
	private WorkflowActionModel workflowActionModel;
	@Mock
	private UserModel userModel;
	@Mock
	private CMSWorkflowActionData workflowActionData;
	@Mock
	private CatalogVersionModel catalogVersionModel;
	@Mock
	private CatalogModel catalogModel;

	// LocalizedPopulator
	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CMSUserService cmsUserService;
	@InjectMocks
	private DefaultLocalizedPopulator localizedPopulator;

	@InjectMocks
	private CMSWorkflowTaskDataPopulator cmsWorkflowTaskDataPopulator;

	@Before
	public void setUp()
	{

		target = new ArrayList<>();

		when(attachmentModel.getTypeOfItem()).thenReturn(composedTypeModel);
		when(attachmentModel.getItem()).thenReturn(itemModel);
		when(itemModel.getUid()).thenReturn(PAGE_UID);
		when(itemModel.getName()).thenReturn(PAGE_NAME);
		when(itemModel.getCatalogVersion()).thenReturn(catalogVersionModel);

		when(catalogVersionModel.getVersion()).thenReturn(CATALOG_VERSION);
		when(catalogVersionModel.getCatalog()).thenReturn(catalogModel);
		when(catalogModel.getId()).thenReturn(CATALOG_ID);
		when(catalogModel.getName(ENGLISH)).thenReturn(CATALOG_NAME_EN);
		when(catalogModel.getName(FRENCH)).thenReturn(CATALOG_NAME_FR);

		when(workflowActionModel.getWorkflow()).thenReturn(workflowModel);
		when(workflowActionModel.getPrincipalAssigned()).thenReturn(userModel);
		when(workflowActionModel.getStatus()).thenReturn(WorkflowActionStatus.IN_PROGRESS);

		when(workflowModel.getAttachments()).thenReturn(Collections.singletonList(attachmentModel));

		when(typeService.getComposedTypeForClass(AbstractPageModel.class)).thenReturn(composedTypeModel);
		when(Boolean.valueOf(typeService.isAssignableFrom(composedTypeModel, composedTypeModel))).thenReturn(Boolean.TRUE);

		when(userService.getCurrentUser()).thenReturn(userModel);

		when(cmsWorkflowParticipantService.getRelatedPrincipals(userModel)).thenReturn(Collections.singletonList(userModel));

		when(cmsWorkflowActionDataConverter.convert(workflowActionModel)).thenReturn(workflowActionData);

		when(workflowActionData.getStartedAgoInMillis()).thenReturn(Long.valueOf(20L));

		// LocalizedPopulator
		when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(new HashSet<>(Arrays.asList(EN, FR)));
		final LanguageData languageEN = new LanguageData();
		languageEN.setIsocode(EN);
		final LanguageData languageFR = new LanguageData();
		languageFR.setIsocode(FR);
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));
		when(commonI18NService.getLocaleForIsoCode(EN)).thenReturn(ENGLISH);
		when(commonI18NService.getLocaleForIsoCode(FR)).thenReturn(FRENCH);
		cmsWorkflowTaskDataPopulator.setLocalizedPopulator(localizedPopulator);
	}

	@Test
	public void shouldRetrieveWorkflowTasks()
	{
		// WHEN
		cmsWorkflowTaskDataPopulator.populate(workflowActionModel, target);

		// THEN
		assertThat(target.get(0).getAction(), is(workflowActionData));

		assertThat(target.get(0).getAttachments(), hasItems(
				allOf(hasProperty("pageUid", equalTo(PAGE_UID)),
						hasProperty("pageName", equalTo(PAGE_NAME)),
						hasProperty("pageName", equalTo(PAGE_NAME)),
						hasProperty("catalogName", allOf(
								hasEntry(EN, CATALOG_NAME_EN),
								hasEntry(FR, CATALOG_NAME_FR))),
						hasProperty("catalogVersion", equalTo(CATALOG_VERSION)))));
	}
}
