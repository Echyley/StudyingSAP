/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.validator;

import de.hybris.platform.cms2.model.contents.components.PDFDocumentComponentModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED_L10N;
import static java.util.Objects.isNull;

/**
 * Default implementation of the validator for {@link PDFDocumentComponentModel}
 */
public class DefaultPDFDocumentComponentValidator implements Validator<PDFDocumentComponentModel>
{
    private ValidationErrorsProvider validationErrorsProvider;
    private LanguageFacade languageFacade;
    private CommonI18NService commonI18NService;

    @Override
    public void validate(final PDFDocumentComponentModel validate)
    {
        validatePDFFileField(validate);
    }

    protected void validatePDFFileField(final PDFDocumentComponentModel validate) {
        getLanguageFacade().getLanguages().stream() //
                .filter(LanguageData::isRequired) //
                .forEach(languageData -> {
                    final MediaModel pdfDocumentModel = validate.getPdfFile(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode()));
                    if (isNull(pdfDocumentModel) || (pdfDocumentModel != null && isNull(pdfDocumentModel.getCode())))
                    {
                        getValidationErrorsProvider().getCurrentValidationErrors().add(
                                newValidationErrorBuilder() //
                                        .field(PDFDocumentComponentModel.PDFFILE) //
                                        .language(languageData.getIsocode())
                                        .errorCode(FIELD_REQUIRED_L10N) //
                                        .errorArgs(new Object[] {languageData.getIsocode()}) //
                                        .build()
                        );
                    }
                });
    }

    protected ValidationErrorsProvider getValidationErrorsProvider()
    {
        return validationErrorsProvider;
    }

    public void setValidationErrorsProvider(final ValidationErrorsProvider validationErrorsProvider)
    {
        this.validationErrorsProvider = validationErrorsProvider;
    }

    protected LanguageFacade getLanguageFacade()
    {
        return languageFacade;
    }

    public void setLanguageFacade(final LanguageFacade languageFacade)
    {
        this.languageFacade = languageFacade;
    }

    protected CommonI18NService getCommonI18NService()
    {
        return commonI18NService;
    }

    public void setCommonI18NService(final CommonI18NService commonI18NService)
    {
        this.commonI18NService = commonI18NService;
    }
}
