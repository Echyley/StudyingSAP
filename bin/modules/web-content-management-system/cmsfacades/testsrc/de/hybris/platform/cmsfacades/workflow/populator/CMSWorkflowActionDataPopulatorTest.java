/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.workflow.populator;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.FRENCH;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import de.hybris.bootstrap.annotations.UnitTest;
import de.hybris.platform.cmsfacades.common.populator.impl.DefaultLocalizedPopulator;
import de.hybris.platform.cmsfacades.data.CMSWorkflowActionData;
import de.hybris.platform.cms2.workflow.service.CMSWorkflowParticipantService;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.cmsfacades.users.services.CMSUserService;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.workflow.enums.WorkflowActionStatus;
import de.hybris.platform.workflow.enums.WorkflowActionType;
import de.hybris.platform.workflow.model.WorkflowActionModel;
import de.hybris.platform.workflow.model.WorkflowDecisionModel;

import java.util.Arrays;
import java.util.HashSet;
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
public class CMSWorkflowActionDataPopulatorTest
{

	private final String IS_CURRENT_USER_PARTICIPANT = "isCurrentUserParticipant";

	private final String WORKFLOW_ACTION1_CODE = "action1Code";
	private final String WORKFLOW_ACTION1_NAME_EN = "action1Name_EN";
	private final String WORKFLOW_ACTION1_NAME_FR = "action1Name_FR";
	private final String WORKFLOW_ACTION1_STATUS = "action1Status";
	private final String WORKFLOW_ACTION1_DESCRIPTION_EN = "action1Description_EN";
	private final String WORKFLOW_ACTION1_DESCRIPTION_FR = "action1Description_FR";

	private final String WORKFLOW_DECISION1_CODE = "decision1Code";
	private final String WORKFLOW_DECISION2_CODE = "decision2Code";
	private final String WORKFLOW_DECISION1_NAME_EN = "decision1Name_EN";
	private final String WORKFLOW_DECISION1_NAME_FR = "decision1Name_FR";
	private final String WORKFLOW_DECISION2_NAME_EN = "decision2Name_EN";
	private final String WORKFLOW_DECISION1_DESCRIPTION_EN = "decision1Desc_EN";
	private final String WORKFLOW_DECISION1_DESCRIPTION_FR = "decision1Desc_FR";
	private final String WORKFLOW_DECISION2_DESCRIPTION_EN = "decision2Desc_EN";

	// LocalizedPopulator
	private static final String EN = Locale.ENGLISH.getLanguage();
	private static final String FR = Locale.FRENCH.getLanguage();


	@Mock
	private WorkflowActionModel workflowAction1Model;

	@Mock
	private WorkflowDecisionModel workflowDecision1Model;

	@Mock
	private WorkflowDecisionModel workflowDecision2Model;

	@Mock
	private CMSWorkflowParticipantService cmsWorkflowParticipantService;

	private final CMSWorkflowActionData cmsWorkflowActionData = new CMSWorkflowActionData();

	@InjectMocks
	private CMSWorkflowActionDataPopulator cmsWorkflowActionDataPopulator;

	// LocalizedPopulator Mock
	@Mock
	private LanguageFacade languageFacade;
	@Mock
	private CommonI18NService commonI18NService;
	@Mock
	private CMSUserService cmsUserService;
	@InjectMocks
	private DefaultLocalizedPopulator localizedPopulator;

	@Before
	public void setUp()
	{
		when(Boolean.valueOf(cmsWorkflowParticipantService.isWorkflowActionParticipant(workflowAction1Model))).thenReturn(
				Boolean.TRUE);

		when(workflowAction1Model.getActionType()).thenReturn(WorkflowActionType.START);
		when(workflowAction1Model.getCode()).thenReturn(WORKFLOW_ACTION1_CODE);
		when(workflowAction1Model.getName(ENGLISH)).thenReturn(WORKFLOW_ACTION1_NAME_EN);
		when(workflowAction1Model.getName(FRENCH)).thenReturn(WORKFLOW_ACTION1_NAME_FR);
		when(workflowAction1Model.getDescription(ENGLISH)).thenReturn(WORKFLOW_ACTION1_DESCRIPTION_EN);
		when(workflowAction1Model.getDescription(FRENCH)).thenReturn(WORKFLOW_ACTION1_DESCRIPTION_FR);
		when(workflowAction1Model.getStatus()).thenReturn(WorkflowActionStatus.PENDING);
		when(workflowAction1Model.getDecisions()).thenReturn(Arrays.asList(workflowDecision1Model, workflowDecision2Model));

		when(workflowDecision1Model.getCode()).thenReturn(WORKFLOW_DECISION1_CODE);
		when(workflowDecision1Model.getName(ENGLISH)).thenReturn(WORKFLOW_DECISION1_NAME_EN);
		when(workflowDecision1Model.getName(FRENCH)).thenReturn(WORKFLOW_DECISION1_NAME_FR);
		when(workflowDecision1Model.getDescription(ENGLISH)).thenReturn(WORKFLOW_DECISION1_DESCRIPTION_EN);
		when(workflowDecision1Model.getDescription(FRENCH)).thenReturn(WORKFLOW_DECISION1_DESCRIPTION_FR);

		when(workflowDecision2Model.getCode()).thenReturn(WORKFLOW_DECISION2_CODE);
		when(workflowDecision2Model.getName(ENGLISH)).thenReturn(WORKFLOW_DECISION2_NAME_EN);
		when(workflowDecision2Model.getDescription(ENGLISH)).thenReturn(WORKFLOW_DECISION2_DESCRIPTION_EN);

		// LocalizedPopulator
		when(cmsUserService.getReadableLanguagesForCurrentUser()).thenReturn(new HashSet<>(Arrays.asList(EN, FR)));
		final LanguageData languageEN = new LanguageData();
		languageEN.setIsocode(EN);
		final LanguageData languageFR = new LanguageData();
		languageFR.setIsocode(FR);
		when(languageFacade.getLanguages()).thenReturn(Lists.newArrayList(languageEN, languageFR));
		when(commonI18NService.getLocaleForIsoCode(EN)).thenReturn(ENGLISH);
		when(commonI18NService.getLocaleForIsoCode(FR)).thenReturn(FRENCH);
		cmsWorkflowActionDataPopulator.setLocalizedPopulator(localizedPopulator);
	}

	@Test
	public void whenPopulateIsCalled_ThenItAddsAllTheRequiredInformation()
	{
		// WHEN
		cmsWorkflowActionDataPopulator.populate(workflowAction1Model, cmsWorkflowActionData);

		// THEN
		assertThat(cmsWorkflowActionData.getActionType(), is(WorkflowActionType.START.name()));
		assertThat(cmsWorkflowActionData.getCode(), is(WORKFLOW_ACTION1_CODE));
		assertThat(cmsWorkflowActionData.getName(), allOf(
				hasEntry(EN, WORKFLOW_ACTION1_NAME_EN),
				hasEntry(FR, WORKFLOW_ACTION1_NAME_FR)));
		assertThat(cmsWorkflowActionData.getDescription(), allOf(
				hasEntry(EN, WORKFLOW_ACTION1_DESCRIPTION_EN),
				hasEntry(FR, WORKFLOW_ACTION1_DESCRIPTION_FR)));
		assertThat(cmsWorkflowActionData.getStatus(), is(WorkflowActionStatus.PENDING.name()));
		assertThat(Boolean.valueOf(cmsWorkflowActionData.isIsCurrentUserParticipant()), is(Boolean.TRUE));

		assertThat(cmsWorkflowActionData.getDecisions(), hasItems(
				allOf(hasProperty(WorkflowDecisionModel.CODE, equalTo(WORKFLOW_DECISION1_CODE)),
						hasProperty(WorkflowDecisionModel.NAME, allOf(
								hasEntry(EN, WORKFLOW_DECISION1_NAME_EN),
								hasEntry(FR, WORKFLOW_DECISION1_NAME_FR))),
						hasProperty(WorkflowDecisionModel.DESCRIPTION, allOf(
								hasEntry(EN, WORKFLOW_DECISION1_DESCRIPTION_EN),
								hasEntry(FR, WORKFLOW_DECISION1_DESCRIPTION_FR)))),
				allOf(hasProperty(WorkflowDecisionModel.CODE, equalTo(WORKFLOW_DECISION2_CODE)),
						hasProperty(WorkflowDecisionModel.NAME, hasEntry(EN, WORKFLOW_DECISION2_NAME_EN)),
						hasProperty(WorkflowDecisionModel.DESCRIPTION, hasEntry(EN, WORKFLOW_DECISION2_DESCRIPTION_EN)))));
	}

}
