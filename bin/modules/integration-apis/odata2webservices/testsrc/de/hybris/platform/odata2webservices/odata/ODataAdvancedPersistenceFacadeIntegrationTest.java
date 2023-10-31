/*
 * [y] hybris Platform
 *
 * Copyright (c) 2018 SAP SE or an SAP affiliate company.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package de.hybris.platform.odata2webservices.odata;

import static de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder.integrationObjectItemAttribute;
import static de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder.integrationObjectItem;
import static de.hybris.platform.integrationservices.IntegrationObjectModelBuilder.integrationObject;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelDoesNotExist;
import static de.hybris.platform.integrationservices.util.IntegrationTestUtil.assertModelExists;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.ERROR_CODE;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.catalogVersionContent;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.createContext;
import static de.hybris.platform.odata2webservices.odata.ODataFacadeTestUtils.oDataPostRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import de.hybris.bootstrap.annotations.IntegrationTest;
import de.hybris.platform.catalog.model.CatalogModel;
import de.hybris.platform.catalog.model.CatalogVersionModel;
import de.hybris.platform.core.model.enumeration.EnumerationValueModel;
import de.hybris.platform.core.model.media.MediaFolderModel;
import de.hybris.platform.core.model.user.PhoneContactInfoModel;
import de.hybris.platform.cronjob.model.CronJobModel;
import de.hybris.platform.cronjob.model.TriggerModel;
import de.hybris.platform.impex.jalo.ImpExException;
import de.hybris.platform.impex.model.ImpExMediaModel;
import de.hybris.platform.impex.model.cronjob.ImpExImportCronJobModel;
import de.hybris.platform.integrationservices.IntegrationObjectItemAttributeModelBuilder;
import de.hybris.platform.integrationservices.IntegrationObjectItemModelBuilder;
import de.hybris.platform.integrationservices.IntegrationObjectModelBuilder;
import de.hybris.platform.integrationservices.util.IntegrationTestUtil;
import de.hybris.platform.integrationservices.util.JsonBuilder;
import de.hybris.platform.odata2services.odata.asserts.ODataAssertions;
import de.hybris.platform.servicelayer.ServicelayerTest;

import java.util.Locale;

import javax.annotation.Resource;

import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.apache.olingo.odata2.api.processor.ODataContext;
import org.apache.olingo.odata2.api.processor.ODataRequest;
import org.apache.olingo.odata2.api.processor.ODataResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

@IntegrationTest
public class ODataAdvancedPersistenceFacadeIntegrationTest extends ServicelayerTest
{
	private static final String TEST_NAME = "ODataAdvancedPersistence";
	private static final String JOB_IO_CODE = TEST_NAME + "_IO";
	private static final String PHONE_IO_CODE = TEST_NAME + "PhoneIO";
	private static final String JOBS_ENDPOINT = "ImpExImportCronJobs";
	private static final String MEDIAS_ENDPOINT = "ImpExMedias";
	private static final String PHONES_ENDPOINT = "Phones";
	private static final String CATALOG_ID = TEST_NAME + "_Catalog";
	private static final String CATALOG_VERSION = "Staged";
	private static final String CRON_EXPRESSION = "2 2 2 1/1 * ? *";
	private static final String ATTR_WORK_MEDIA = "workMedia";
	private static final String PATH_CODE = "d.code";
	private static final String ERROR_CODE_PATH = "error.code";
	private static final String ERROR_MSG_PATH = "error.message.value";
	private static final String ERROR_KEY_PATH = "error.innererror";
	private static final String MISSING_KEY_CODE = null;
	private static final String JOB_CODE = TEST_NAME + "_Job";
	private static final String PHONE_CODE = TEST_NAME + "_Phone";

	@Rule
	public IntegrationObjectModelBuilder PHONE_IO = integrationObject()
			.withCode(PHONE_IO_CODE)
			.withItem(item("Phone").root()
			                       .withType("PhoneContactInfo")
			                       .withAttribute(attribute("code").unique())
			                       .withAttribute(attribute("number").withQualifier("phoneNumber"))
			                       .withAttribute(attribute("type").withReturnItem("PhoneType").autoCreate())
			                       .withAttribute(attribute("user").withReturnItem("User")))
			.withItem(item("PhoneType").withType("PhoneContactInfoType")
			                           .withAttribute(attribute("code").unique()))
			.withItem(item("User").withType("Customer")
			                      .withAttribute(attribute("id").withQualifier("uid")));
	@Rule
	public IntegrationObjectModelBuilder JOBS_IO = integrationObject()
			.withCode(JOB_IO_CODE)
			.withItem(item("ImpExImportCronJob")
					.withAttribute(attribute("code"))
					.withAttribute(attribute("workMedia").withReturnItem("ImpExMedia"))
					.withAttribute(attribute("currentStep").withReturnItem("Step"))
					.withAttribute(attribute("triggers").withReturnItem("Trigger")))
			.withItem(item("ImpExMedia")
					.withAttribute(attribute("code"))
					.withAttribute(attribute("folder").withReturnItem("MediaFolder"))
					.withAttribute(attribute("catalogVersion").withReturnItem("CatalogVersion")))
			.withItem(item("MediaFolder")
					.withAttribute(attribute("qualifier")))
			.withItem(item("Catalog")
					.withAttribute(attribute("id")))
			.withItem(item("CatalogVersion")
					.withAttribute(attribute("catalog").withReturnItem("Catalog"))
					.withAttribute(attribute("version")))
			.withItem(item("Step")
					.withAttribute(attribute("code")))
			.withItem(item("Trigger")
					.withAttribute(attribute("cronExpression").unique())
					.withAttribute(attribute("cronJob").unique().withReturnItem("ImpExImportCronJob"))
					.withAttribute(attribute("day")));

	@Resource(name = "defaultODataFacade")
	private ODataFacade facade;

	@Before
	public void setUp() throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE Catalog; id[unique = true]",
				"                     ; " + CATALOG_ID,
				"INSERT_UPDATE CatalogVersion; catalog(id)[unique = true]; version[unique = true]",
				"                            ; " + CATALOG_ID + "        ; " + CATALOG_VERSION);
	}

	@After
	public void cleanup()
	{
		IntegrationTestUtil.remove(CronJobModel.class, m -> JOB_CODE.equals(m.getCode()));
		IntegrationTestUtil.remove(ImpExMediaModel.class, this::isCatalogEqualToTestCatalog);
		IntegrationTestUtil.remove(CatalogModel.class, m -> CATALOG_ID.equals(m.getId()));
		IntegrationTestUtil.remove(PhoneContactInfoModel.class, m -> PHONE_CODE.equals(m.getCode()));
		IntegrationTestUtil.remove(EnumerationValueModel.class, m -> TEST_NAME.equals(m.getCode()));
	}

	private boolean isCatalogEqualToTestCatalog(final ImpExMediaModel m)
	{
		final CatalogVersionModel catalogVersion = m.getCatalogVersion();
		return catalogVersion != null && CATALOG_ID.equals(catalogVersion.getCatalog().getId());
	}

	@Test
	public void testPersistEntity_ImpExImportCronJobModel_NewEntryEntityShouldCreate()
	{
		// given
		final String testWorkMediaCode = "testWorkMediaCode";
		final ImpExImportCronJobModel cronJobModel = cronJob(JOB_CODE);
		assertModelDoesNotExist(cronJobModel);
		final var content = JsonBuilder.json()
		                               .withCode(JOB_CODE)
		                               .withField(ATTR_WORK_MEDIA,
				                               impExMediaContent(testWorkMediaCode));

		// when
		final ODataResponse oDataResponse = facade.handleRequest(postRequest(JOBS_ENDPOINT, content));

		// then
		ODataAssertions.assertThat(oDataResponse)
		               .hasStatus(HttpStatusCodes.CREATED)
		               .jsonBody()
		               .hasPathWithValue(PATH_CODE, JOB_CODE);

		final ImpExImportCronJobModel persistedModel = assertModelExists(cronJobModel);
		assertThat(persistedModel)
				.hasFieldOrPropertyWithValue("code", JOB_CODE)
				.hasFieldOrPropertyWithValue("workMedia.code", testWorkMediaCode)
				.hasFieldOrPropertyWithValue("workMedia.catalogVersion.version", CATALOG_VERSION)
				.hasFieldOrPropertyWithValue("workMedia.catalogVersion.catalog.id", CATALOG_ID)
				.hasFieldOrPropertyWithValue("workMedia.owner", persistedModel);
	}

	@Test
	public void testPersistEntity_ImpExImportCronJobModel_collectionEntryShouldUpdated() throws ImpExException
	{
		// given
		cronJobExists(JOB_CODE, CRON_EXPRESSION);
		final int dayValue = 2;
		final var content = JsonBuilder.json()
		                               .withCode(JOB_CODE)
		                               .withFieldValues("triggers", JsonBuilder.json()
		                                                                       .withField("cronExpression", CRON_EXPRESSION)
		                                                                       .withField("day", dayValue));

		// when
		final ODataResponse oDataResponse = facade.handleRequest(postRequest(JOBS_ENDPOINT, content));

		// then
		ODataAssertions.assertThat(oDataResponse)
		               .hasStatus(HttpStatusCodes.CREATED);
		final TriggerModel trigger = IntegrationTestUtil.findAny(TriggerModel.class,
				                                                m -> JOB_CODE.equals(m.getCronJob().getCode()))
		                                                .orElse(null);
		assertThat(trigger)
				.isNotNull()
				.hasFieldOrPropertyWithValue("cronExpression", CRON_EXPRESSION)
				.hasFieldOrPropertyWithValue("day", dayValue);
	}

	@Test
	public void testPersistEntity_ImpExMediaModel_WhenIsPartOfFalse_NoNewEntryEntityShouldCreate()
	{
		// given
		final ImpExMediaModel impExMediaModel = impExMediaModel("test-workMedia");
		assertModelDoesNotExist(impExMediaModel);

		final var newCatalogVersion = TEST_NAME + "_New_Version";
		final var content = JsonBuilder.json()
		                               .withCode(impExMediaModel.getCode())
		                               .withField("catalogVersion", catalogVersionContent(CATALOG_ID, newCatalogVersion));

		// when
		final ODataResponse oDataResponse = facade.handleRequest(postRequest(MEDIAS_ENDPOINT, content));

		// then
		ODataAssertions.assertThat(oDataResponse)
		               .hasStatus(HttpStatusCodes.BAD_REQUEST)
		               .jsonBody()
		               .hasPathWithValue(ERROR_CODE, "missing_nav_property");
		assertModelDoesNotExist(impExMediaModel);
		assertModelDoesNotExist(catalogVersion(newCatalogVersion));
	}

	@Test
	public void testPersistEntity_ImpExMediaModel_WhenAutoCreateIsSetToTrue_NewEntryShouldCreate() throws ImpExException
	{
		// given
		// override previous attribute definition with autoCreate set to 'true'
		IntegrationTestUtil.importImpEx(
				"$item=integrationObjectItem(integrationObject(code), code)",
				"UPDATE IntegrationObjectItemAttribute; $item[unique = true]          ; attributeName[unique = true]; autoCreate",
				"                                     ; " + JOB_IO_CODE + ":ImpExMedia; catalogVersion              ; true");
		final String mediaCode = "mediaWithCatalogVersionPartOfTrue";
		final String newCatalogVersion = TEST_NAME + "_New";
		final var content = JsonBuilder.json()
		                               .withCode(mediaCode)
		                               .withField("catalogVersion", catalogVersionContent(CATALOG_ID, newCatalogVersion));

		// when
		final ODataResponse oDataResponse = facade.handleRequest(postRequest(MEDIAS_ENDPOINT, content));

		// then
		ODataAssertions.assertThat(oDataResponse)
		               .hasStatus(HttpStatusCodes.CREATED)
		               .jsonBody()
		               .hasPathWithValue(PATH_CODE, mediaCode)
		               .hasPath("d.catalogVersion.__deferred");
		assertModelExists(impExMediaModel(mediaCode));
		assertModelExists(catalogVersion(newCatalogVersion));
	}

	@Test
	public void testPersistEntity_WhenAutoCreateIsSetToTrue_NewEntryShouldCreate_EvenDeeplyNested() throws ImpExException
	{
		// given
		// set previous attribute definition with autoCreate set to 'true'
		IntegrationTestUtil.importImpEx(
				"$item=integrationObjectItem(integrationObject(code), code)",
				"UPDATE IntegrationObjectItemAttribute; $item[unique = true]                   ; attributeName[unique = true]; autoCreate",
				"                                     ; " + JOB_IO_CODE + ":ImpExImportCronJob ; workMedia                   ; true",
				"                                     ; " + JOB_IO_CODE + ":ImpExMedia         ; folder                      ; true");
		final String mediaCode = "mediaWithFolderPartOfTrue";
		final String folder = "MyPartOfFolder";

		final var content = JsonBuilder.json()
		                               .withCode(JOB_CODE)
		                               .withField(ATTR_WORK_MEDIA, impExMediaContent(mediaCode)
				                               .withField("folder", JsonBuilder.json().withField("qualifier", folder)));

		// when
		final ODataResponse oDataResponse = facade.handleRequest(postRequest(JOBS_ENDPOINT, content));

		// then
		ODataAssertions.assertThat(oDataResponse)
		               .hasStatus(HttpStatusCodes.CREATED)
		               .jsonBody()
		               .hasPathWithValue(PATH_CODE, JOB_CODE)
		               .hasPath("d.workMedia.__deferred");

		assertModelExists(cronJob(JOB_CODE));
		assertModelExists(impExMediaModel(mediaCode));
		assertModelExists(mediaFolder(folder));
	}

	private MediaFolderModel mediaFolder(final String folder)
	{
		final MediaFolderModel model = new MediaFolderModel();
		model.setQualifier(folder);
		return model;
	}

	private ImpExMediaModel impExMediaModel(final String s)
	{
		final ImpExMediaModel impExMediaModel = new ImpExMediaModel();
		impExMediaModel.setCode(s);
		return impExMediaModel;
	}

	private CatalogVersionModel catalogVersion(final String version)
	{
		final CatalogVersionModel model = new CatalogVersionModel();
		model.setVersion(version);
		return model;
	}

	@Test
	public void testPersistEntity_MissingKeyField_transactional_WhenErrorOccursShouldRollback()
	{
		// given
		final String testWorkMediaCode = "shouldNotCreatedWorkMedia";
		final ImpExMediaModel testWorkMediaModel = impExMediaModel(testWorkMediaCode);
		assertModelDoesNotExist(testWorkMediaModel);

		final var content = JsonBuilder.json()
		                               .withField(ATTR_WORK_MEDIA,
				                               impExMediaContent(MISSING_KEY_CODE));

		// when
		final ODataResponse oDataResponse = facade.handleRequest(postRequest(JOBS_ENDPOINT, content));

		// then
		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.BAD_REQUEST);
		assertModelDoesNotExist(testWorkMediaModel);
		assertModelDoesNotExist(cronJob(JOB_CODE));
	}

	@Test
	public void testPersistEntity_nonexisting_referenced_item_in_collection_transactional_WhenErrorOccursShouldRollback()
	{
		// given
		final String testTriggerCronExpression = "some cron expression";

		final TriggerModel testTriggerModel = new TriggerModel();
		testTriggerModel.setCronExpression(testTriggerCronExpression);
		assertModelDoesNotExist(testTriggerModel);

		final var jobContent = JsonBuilder.json().withCode("non-existing-cronjob");
		final var content = JsonBuilder.json()
		                               .withFieldValues("triggers", JsonBuilder.json()
		                                                                       .withField("cronExpression", CRON_EXPRESSION)
		                                                                       .withField("cronJob", jobContent));

		// when
		final ODataResponse oDataResponse = facade.handleRequest(postRequest(JOBS_ENDPOINT, content));

		// then
		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.BAD_REQUEST);
		assertModelDoesNotExist(testTriggerModel);
	}

	@Test
	public void testPersistEntity_CreatesNewEnumValueWhenItIsAutocreateInTheIntegrationObject()
	{
		// given request contains a non-existent enum value for the phone type
		final var content = JsonBuilder.json()
		                               .withCode(PHONE_CODE)
		                               .withField("number", "123-456-7890")
		                               .withField("type", JsonBuilder.json().withCode(TEST_NAME))
		                               .withField("user", JsonBuilder.json().withId("anonymous"));

		// when
		final ODataResponse oDataResponse = facade.handleRequest(postRequest(PHONE_IO_CODE, PHONES_ENDPOINT, content));

		// then
		ODataAssertions.assertThat(oDataResponse).hasStatus(HttpStatusCodes.CREATED);
		// and
		assertThat(IntegrationTestUtil.findAny(PhoneContactInfoModel.class, m -> PHONE_CODE.equals(m.getCode()))).isPresent();
		assertThat(IntegrationTestUtil.findAny(EnumerationValueModel.class, m -> TEST_NAME.equals(m.getCode()))).isPresent();
	}

	@Test
	public void testPersistEntity_DoesNotCreatesNewEnumValueWhenItIsNotAutocreateInTheIntegrationObject() throws ImpExException
	{
		// given the "type" attribute is not autocreate in the integration object
		IntegrationTestUtil.importImpEx(
				"$item=integrationObjectItem(integrationObject(code), code)",
				"UPDATE IntegrationObjectItemAttribute; $item[unique = true]        ; attributeName[unique = true]; autoCreate",
				"                                     ; " + PHONE_IO_CODE + ":Phone ; type                        ; false");
		// and request contains a non-existent enum value for the phone type
		final var content = JsonBuilder.json()
		                               .withCode(PHONE_CODE)
		                               .withField("number", "123-456-7890")
		                               .withField("type", JsonBuilder.json().withCode(TEST_NAME))
		                               .withField("user", JsonBuilder.json().withId("anonymous"));

		// when
		final ODataResponse oDataResponse = facade.handleRequest(postRequest(PHONE_IO_CODE, PHONES_ENDPOINT, content));

		// then
		ODataAssertions.assertThat(oDataResponse)
		               .hasStatus(HttpStatusCodes.BAD_REQUEST)
		               .jsonBody()
		               .hasPathWithValue(ERROR_CODE_PATH, "missing_nav_property")
		               .hasPathWithValue(ERROR_KEY_PATH, PHONE_CODE)
		               .hasPathWithValueContaining(ERROR_MSG_PATH, "[Phone]")
		               .hasPathWithValueContaining(ERROR_MSG_PATH, "[type]");
		// and
		assertThat(IntegrationTestUtil.findAny(PhoneContactInfoModel.class, m -> PHONE_CODE.equals(m.getCode()))).isEmpty();
		assertThat(IntegrationTestUtil.findAny(EnumerationValueModel.class, m -> TEST_NAME.equals(m.getCode()))).isEmpty();
	}

	@Test
	public void testPersistEntity_rollsBackCreatedEnumValueWhenContainerItemFailsToPersist()
	{
		// given request contains a non-existent enum value for the phone type and does not contain required attribute "user"
		final var content = JsonBuilder.json()
		                               .withCode(PHONE_CODE)
		                               .withField("number", "123-456-7890")
		                               .withField("type", JsonBuilder.json().withCode(TEST_NAME));

		// when
		final ODataResponse oDataResponse = facade.handleRequest(postRequest(PHONE_IO_CODE, PHONES_ENDPOINT, content));

		// then
		ODataAssertions.assertThat(oDataResponse)
		               .hasStatus(HttpStatusCodes.BAD_REQUEST)
		               .jsonBody()
		               .hasPathWithValue(ERROR_CODE_PATH, "missing_nav_property")
		               .hasPathWithValue(ERROR_KEY_PATH, PHONE_CODE)
		               .hasPathWithValueContaining(ERROR_MSG_PATH, "[Phone]")
		               .hasPathWithValueContaining(ERROR_MSG_PATH, "[user]");
		// and
		assertThat(IntegrationTestUtil.findAny(PhoneContactInfoModel.class, m -> PHONE_CODE.equals(m.getCode()))).isEmpty();
		assertThat(IntegrationTestUtil.findAny(EnumerationValueModel.class, m -> TEST_NAME.equals(m.getCode()))).isEmpty();
	}

	private static JsonBuilder impExMediaContent(final String code)
	{
		return JsonBuilder.json()
		                  .withCode(code)
		                  .withField("catalogVersion", catalogVersionContent(CATALOG_ID, CATALOG_VERSION));
	}

	private static ImpExImportCronJobModel cronJob(final String jobCode)
	{
		final ImpExImportCronJobModel cronJobModel = new ImpExImportCronJobModel();
		cronJobModel.setCode(jobCode);
		return cronJobModel;
	}

	private static IntegrationObjectItemModelBuilder item(final String typeCode)
	{
		return integrationObjectItem().withCode(typeCode);
	}

	private static IntegrationObjectItemAttributeModelBuilder attribute(final String name)
	{
		return integrationObjectItemAttribute().withName(name);
	}

	private static ODataContext postRequest(final String entitySet, final JsonBuilder content)
	{
		return postRequest(JOB_IO_CODE, entitySet, content);
	}

	private static ODataContext postRequest(final String io, final String entitySet, final JsonBuilder content)
	{
		final ODataRequest req = oDataPostRequest(io, entitySet, content.build(), Locale.ENGLISH, APPLICATION_JSON_VALUE);
		return createContext(req);
	}

	private static void cronJobExists(final String jobCode, final String cronExpression) throws ImpExException
	{
		IntegrationTestUtil.importImpEx(
				"INSERT_UPDATE ImpExImportCronJob; code[unique = true]",
				"                                ; " + jobCode,
				"INSERT_UPDATE Trigger; cronExpression[unique = true]; cronJob(code)[unique = true]",
				"                     ; " + cronExpression + "       ; " + jobCode);
	}
}
