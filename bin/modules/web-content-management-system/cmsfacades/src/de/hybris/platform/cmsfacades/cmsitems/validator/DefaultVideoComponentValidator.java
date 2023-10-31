/*
 * Copyright (c) 2022 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.cmsfacades.cmsitems.validator;

import static de.hybris.platform.cmsfacades.common.validator.ValidationErrorBuilder.newValidationErrorBuilder;
import static de.hybris.platform.cmsfacades.constants.CmsfacadesConstants.FIELD_REQUIRED_L10N;
import static java.util.Objects.isNull;

import de.hybris.platform.cms2.enums.ThumbnailSelectorOptions;
import de.hybris.platform.cms2.model.contents.components.VideoComponentModel;
import de.hybris.platform.cmsfacades.common.function.Validator;
import de.hybris.platform.cmsfacades.common.validator.ValidationErrorsProvider;
import de.hybris.platform.cmsfacades.languages.LanguageFacade;
import de.hybris.platform.commercefacades.storesession.data.LanguageData;
import de.hybris.platform.core.model.media.MediaContainerModel;
import de.hybris.platform.core.model.media.MediaModel;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

/**
 * Default implementation of the validator for {@link VideoComponentModel}
 */
public class DefaultVideoComponentValidator implements Validator<VideoComponentModel>
{
    private ValidationErrorsProvider validationErrorsProvider;
    private LanguageFacade languageFacade;
    private CommonI18NService commonI18NService;

    @Override
    public void validate(final VideoComponentModel validate)
    {
        validateEmptyField(validate);
        validateThumbnailField(validate);
    }

    protected void validateEmptyField(final VideoComponentModel validate) {
        getLanguageFacade().getLanguages().stream() //
                .filter(LanguageData::isRequired) //
                .forEach(languageData -> {
                    final MediaModel videoModel = validate.getVideo(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode()));
                    if (isNull(videoModel) || (videoModel != null && isNull(videoModel.getCode())))
                    {
                        getValidationErrorsProvider().getCurrentValidationErrors().add(
                                newValidationErrorBuilder() //
                                        .field(VideoComponentModel.VIDEO) //
                                        .language(languageData.getIsocode())
                                        .errorCode(FIELD_REQUIRED_L10N) //
                                        .errorArgs(new Object[] {languageData.getIsocode()}) //
                                        .build()
                        );
                    }
                });
    }

    protected void validateThumbnailField(final VideoComponentModel validate) {
        getLanguageFacade().getLanguages().stream() //
                .filter(LanguageData::isRequired) //
                .forEach(languageData -> {
                    final ThumbnailSelectorOptions option = validate.getThumbnailSelector();
                    if (option == ThumbnailSelectorOptions.UPLOAD_THUMBNAIL) {
                        final MediaContainerModel thumbnailModel = validate.getThumbnail(getCommonI18NService().getLocaleForIsoCode(languageData.getIsocode()));
                        if (isNull(thumbnailModel) || thumbnailModel.getMedias().isEmpty())
                        {
                            getValidationErrorsProvider().getCurrentValidationErrors().add(
                                    newValidationErrorBuilder() //
                                            .field(VideoComponentModel.THUMBNAIL) //
                                            .language(languageData.getIsocode())
                                            .errorCode(FIELD_REQUIRED_L10N) //
                                            .errorArgs(new Object[] {languageData.getIsocode()}) //
                                            .build()
                            );
                        }
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
