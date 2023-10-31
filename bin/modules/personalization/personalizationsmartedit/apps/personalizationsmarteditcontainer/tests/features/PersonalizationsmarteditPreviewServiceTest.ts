import { PersonalizationsmarteditPreviewService } from 'personalizationsmarteditcontainer/service/PersonalizationsmarteditPreviewService';
import { promiseUtils } from 'smarteditcommons';

describe('PersonalizationsmarteditPreviewService', () => {
    // Mock
    let experienceService: jasmine.SpyObj<any>;

    // Service being tested
    let personalizationsmarteditPreviewService: PersonalizationsmarteditPreviewService;

    beforeEach(() => {
        experienceService = jasmine.createSpyObj('experienceService', [
            'setCurrentExperience',
            'getCurrentExperience'
        ]);
        experienceService.getCurrentExperience.and.returnValue(promiseUtils.defer().promise);
        experienceService.setCurrentExperience.and.returnValue(promiseUtils.defer().promise);
        personalizationsmarteditPreviewService = new PersonalizationsmarteditPreviewService(
            experienceService
        );
        spyOn(
            personalizationsmarteditPreviewService,
            'updatePreviewTicketWithVariations'
        ).and.callThrough();
    });

    describe('updatePreviewTicketWithVariations', () => {
        it('should be defined', () => {
            expect(
                personalizationsmarteditPreviewService.updatePreviewTicketWithVariations
            ).toBeDefined();
        });

        it('should pass proper object to experienceService', () => {
            // when
            personalizationsmarteditPreviewService.updatePreviewTicketWithVariations([
                'variation1',
                'variation2'
            ]);
            // then
            expect(experienceService.getCurrentExperience).toHaveBeenCalled();
        });
    });

    describe('removePersonalizationDataFromPreview', () => {
        it('should be defined', () => {
            expect(
                personalizationsmarteditPreviewService.removePersonalizationDataFromPreview
            ).toBeDefined();
        });

        it('should call proper function with proper arguments', () => {
            // when
            personalizationsmarteditPreviewService.removePersonalizationDataFromPreview();
            // then
            expect(
                personalizationsmarteditPreviewService.updatePreviewTicketWithVariations
            ).toHaveBeenCalledWith([]);
            expect(experienceService.getCurrentExperience).toHaveBeenCalled();
        });
    });
});
