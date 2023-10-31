/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */

package de.hybris.platform.swaggerintegration.config;

import de.hybris.platform.swaggerintegration.constants.SwaggerintegrationConstants;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import de.hybris.platform.webservicescommons.swagger.services.ApiVendorExtensionService;

import javax.annotation.Resource;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springdoc.core.AbstractRequestService;
import org.springdoc.core.GenericResponseService;
import org.springdoc.core.OpenAPIService;
import org.springdoc.core.OperationService;
import org.springdoc.core.SpringDocConfigProperties;
import org.springdoc.core.SpringDocProviders;
import org.springdoc.core.SpringDocUtils;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.ParameterCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springdoc.core.filters.OpenApiMethodFilter;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;


/**
 * Configuration class for swagger integration. The beans are provided to configure springdoc-openapi which scans and provides the open api spec yaml file.
 *
 * @since 2211
 */
@Configuration
@Import({ org.springdoc.core.SpringDocConfiguration.class, org.springdoc.webmvc.core.SpringDocWebMvcConfiguration.class,
		org.springdoc.webmvc.ui.SwaggerConfig.class, org.springdoc.core.SwaggerUiConfigProperties.class,
		org.springdoc.core.SwaggerUiOAuthProperties.class, SpringDocConfigProperties.class,
		org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration.class })
public class SwaggerIntegrationConfig
{
	public static final String FIELDS_DESCRIPTION = "Response configuration. This is the list of fields that should be returned in the response body. Examples: ";

	@Resource(name = "apiVendorExtensionService")
	private ApiVendorExtensionService apiVendorExtensionService;

	@Resource(name = "apiDocInfoProvider")
	private ApiDocInfoProvider apiDocInfoProvider;

	@Bean
	OpenApiWebMvcResource openApiResource(final ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory,
			final AbstractRequestService requestBuilder, final GenericResponseService responseBuilder,
			final OperationService operationParser, final SpringDocConfigProperties springDocConfigProperties,
			final List<OperationCustomizer> operationCustomizers, final List<OpenApiCustomiser> openApiCustomizers,
			final List<OpenApiMethodFilter> methodFilters, final SpringDocProviders springDocProviders)
	{
		return new CommerceOpenApiWebMvcResource(openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser,
				operationCustomizers, openApiCustomizers, methodFilters, springDocConfigProperties, springDocProviders);
	}

	@Bean
	public OpenAPI openApi()
	{
		SpringDocUtils.getConfig().removeRequestWrapperToIgnore(Locale.class);
		return new OpenAPI().info(new Info().title(apiDocInfoProvider.getTitle()).description(apiDocInfoProvider.getDescription())
						.version(apiDocInfoProvider.getVersion())
						.license(new License().name(apiDocInfoProvider.getLicense()).url(apiDocInfoProvider.getLicenseUrl())))
				.components(createComponents()).security(createSecurityRequirements())
				.externalDocs(new ExternalDocumentation().description("SAP Commerce Cloud").url("https://www.sap.com"));
	}

	@Bean("openApiVendorExtensionCustomizer")
	public OpenApiCustomiser openApiVendorExtensionCustomizer()
	{
		return openAPI -> apiVendorExtensionService.getAllVendorExtensions(apiDocInfoProvider.getConfigPrefix())
				.forEach(vendorExtension -> openAPI.addExtension(vendorExtension.getName(), vendorExtension.getValue()));
	}

	@Bean("openApiFieldsParamCustomizer")
	public ParameterCustomizer openApiFieldsParamCustomizer()
	{
		return (param, methodParameter) -> {
			Optional.ofNullable(methodParameter.getParameterAnnotation(ApiFieldsParam.class)).ifPresent(apiFieldsParam -> {
				Optional.ofNullable(param.getSchema()).ifPresent(schema -> schema.setDefault(apiFieldsParam.defaultValue()));
				param.description(getParamDescription(apiFieldsParam));
			});
			return param;
		};
	}

	private String getParamDescription(final ApiFieldsParam apiFieldsParam)
	{
		return FIELDS_DESCRIPTION + Arrays.stream(apiFieldsParam.examples()).collect(Collectors.joining(","));
	}

	@Bean("openApiOAuthCustomizer")
	public OpenApiCustomiser openApiOAuthCustomizer()
	{
		return openApi -> openApi.getPaths().values().stream().flatMap(pathItem -> pathItem.readOperations().stream())
				.forEach(operation -> createSecurityRequirements().forEach(operation::addSecurityItem));
	}

	@Bean("openApiTypeNameProvider")
	public OpenApiCustomiser commerceTypeNameProvider()
	{
		return new CommerceTypeNameProvider();
	}

	@Bean("openApiRespCodeProvider")
	public OpenApiCustomiser commerceRespCodeProvider()
	{
		return new CommerceRespCodeProvider();
	}

	@Bean("openApiMediaTypeCustomizer")
	public OperationCustomizer commerceMediaTypeCustomizer()
	{
		return new CommerceMediaTypeCustomizer(SwaggerintegrationConstants.DEFAULT_CONSUMES_MEDIA_TYPES,
				SwaggerintegrationConstants.DEFAULT_PRODUCES_MEDIA_TYPES);
	}

	@Bean("openApiParameterSorter")
	public OperationCustomizer commerceParameterSorter()
	{
		return new CommerceParameterSorter();
	}

	@Bean("tagsGenerationAndSortCustomizer")
	public OpenApiCustomiser tagsGenerationAndSortCustomizer()
	{
		return new TagsGenerationAndSortCustomizer();
	}

	@Bean("pathsSortCustomizer")
	public OpenApiCustomiser pathsSortCustomizer()
	{
		return new PathsSortCustomizer();
	}

	@Bean
	public PropertyCustomizer commerceDateTimeFormatter()
	{
		return new CommerceDateTimeFormatCustomizer();
	}

	private List<SecurityRequirement> createSecurityRequirements()
	{
		return List.of(createPasswordSecurityRequirement(), createClientCredentialSecurityRequirement());
	}

	private SecurityRequirement createPasswordSecurityRequirement()
	{
		return new SecurityRequirement().addList(apiDocInfoProvider.getOAuthPasswordName(),
				apiDocInfoProvider.getOAuthPasswordScope());
	}

	private SecurityRequirement createClientCredentialSecurityRequirement()
	{
		return new SecurityRequirement().addList(apiDocInfoProvider.getOAuthClientCredentialName(),
				apiDocInfoProvider.getOAuthClientCredentialScope());
	}

	private Components createComponents()
	{
		return new Components().addSecuritySchemes(apiDocInfoProvider.getOAuthPasswordName(), createPasswordSecurityScheme())
				.addSecuritySchemes(apiDocInfoProvider.getOAuthClientCredentialName(), createClientCredentialSecurityScheme());
	}

	private SecurityScheme createPasswordSecurityScheme()
	{
		final OAuthFlow passwordFlow = createOAuthFlow(apiDocInfoProvider.getOAuthPasswordScope());
		return new SecurityScheme().type(SecurityScheme.Type.OAUTH2).flows(new OAuthFlows().password(passwordFlow));
	}

	private SecurityScheme createClientCredentialSecurityScheme()
	{
		final OAuthFlow clientCredentialFlow = createOAuthFlow(apiDocInfoProvider.getOAuthClientCredentialScope());
		return new SecurityScheme().type(SecurityScheme.Type.OAUTH2)
				.flows(new OAuthFlows().clientCredentials(clientCredentialFlow));
	}

	private OAuthFlow createOAuthFlow(final String scope)
	{
		if (StringUtils.isNotBlank(scope))
		{
			return new OAuthFlow().tokenUrl(apiDocInfoProvider.getOAuthTokenUrl()).scopes(new Scopes().addString(scope, ""));
		}
		return null;
	}

}
