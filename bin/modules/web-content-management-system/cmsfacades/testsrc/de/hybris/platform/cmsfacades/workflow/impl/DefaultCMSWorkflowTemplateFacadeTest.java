/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.workflow.impl;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasValue;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.catalog.CatalogVersionService;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.cms2.workflow.service.CMSWorkflowTemplateService;
import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;
import de.hybris.platform.cmsfacades.data.WorkflowTemplateData;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.workflow.model.WorkflowTemplateModel;

import java.util.Arrays;
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
public class DefaultCMSWorkflowTemplateFacadeTest
{

	private static final String CATALOG_ID = "catalogID";
	private static final String VERSION_NAME = "versionName";

	private final String WORKFLOW_TEMPLATE_1_CODE = "workflowTemplate1Code";
	private final String WORKFLOW_TEMPLATE_1_NAME_EN = "workflowTemplate1Name_EN";
	private final String WORKFLOW_TEMPLATE_1_NAME_FR = "workflowTemplate1Name_FR";

	private final String WORKFLOW_TEMPLATE_2_CODE = "workflowTemplate2Code";
	private final String WORKFLOW_TEMPLATE_2_NAME_EN = "workflowTemplate2Name_EN";
	private final String WORKFLOW_TEMPLATE_2_NAME_FR = "workflowTemplate2Name_FR";

	// LocalizedPopulator
	private static final String EN = Locale.ENGLISH.getLanguage();
	private static final String FR = Locale.FRENCH.getLanguage();

	@InjectMocks
	private DefaultCMSWorkflowTemplateFacade workflowTemplateFacade;

	@Mock
	private CatalogVersionService catalogVersionService;

	@Mock
	private CMSWorkflowTemplateService cmsWorkflowTemplateService;

	// LocalizedPopulator
	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CMSUserService cmsUserService;
	@InjectMocks
	private DefaultLocalizedPopulator localizedPopulator;

	@Mock
	private CatalogVersionModel catalogVersion;

	@Mock
	private WorkflowTemplateModel workflowTemplate1;

	@Mock
	private WorkflowTemplateModel workflowTemplate2;

	@Before
	public void setup()
	{
		when(catalogVersionService.getCatalogVersion(CATALOG_ID, VERSION_NAME)).thenReturn(catalogVersion);
		when(workflowTemplate1.getCode()).thenReturn(WORKFLOW_TEMPLATE_1_CODE);
		when(workflowTemplate1.getName(Locale.ENGLISH)).thenReturn(WORKFLOW_TEMPLATE_1_NAME_EN);
		when(workflowTemplate1.getName(Locale.FRENCH)).thenReturn(WORKFLOW_TEMPLATE_1_NAME_FR);

		when(workflowTemplate2.getCode()).thenReturn(WORKFLOW_TEMPLATE_2_CODE);
		when(workflowTemplate2.getName(Locale.ENGLISH)).thenReturn(WORKFLOW_TEMPLATE_2_NAME_EN);
		when(workflowTemplate2.getName(Locale.FRENCH)).thenReturn(WORKFLOW_TEMPLATE_2_NAME_FR);

		// LocalizedPopulator
		when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(new HashSet<>(Arrays.asList(EN, FR)));
		final LanguageData languageEN = new LanguageData();
		languageEN.setIsocode(EN);
		final LanguageData languageFR = new LanguageData();
		languageFR.setIsocode(FR);
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));
		when(commonI18NService.getLocaleForIsoCode(EN)).thenReturn(ENGLISH);
		when(commonI18NService.getLocaleForIsoCode(FR)).thenReturn(FRENCH);
		workflowTemplateFacade.setLocalizedPopulator(localizedPopulator);
	}

	@Test
	public void getWorkflowTemplatesReturnsEmptyWhenNoTemplatesForCatalogVersion()
	{

		// WHEN
		final List<WorkflowTemplateData> result = workflowTemplateFacade.getWorkflowTemplates(CATALOG_ID, VERSION_NAME);

		// THEN
		assertThat(result, empty());
	}

	@Test
	public void getWorkflowTemplatesReturnsTemplatesForCatalogVersion()
	{

		// GIVEN
		when(cmsWorkflowTemplateService.getVisibleWorkflowTemplatesForCatalogVersion(catalogVersion))
				.thenReturn(Arrays.asList(workflowTemplate1, workflowTemplate2));

		// WHEN
		final List<WorkflowTemplateData> result = workflowTemplateFacade.getWorkflowTemplates(CATALOG_ID, VERSION_NAME);

		// THEN
		assertThat(result,
				hasItems(
						allOf(hasProperty(WorkflowTemplateModel.CODE, equalTo(WORKFLOW_TEMPLATE_1_CODE)),
								hasProperty(WorkflowTemplateModel.NAME, allOf(
										hasEntry(EN, WORKFLOW_TEMPLATE_1_NAME_EN),
										hasEntry(FR, WORKFLOW_TEMPLATE_1_NAME_FR)))),
						allOf(hasProperty(WorkflowTemplateModel.CODE, equalTo(WORKFLOW_TEMPLATE_2_CODE)),
								hasProperty(WorkflowTemplateModel.NAME, allOf(
										hasEntry(EN, WORKFLOW_TEMPLATE_2_NAME_EN),
										hasEntry(FR, WORKFLOW_TEMPLATE_2_NAME_FR)))) //
				));
	}

}
