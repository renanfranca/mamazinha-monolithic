jest.mock('@angular/router');

import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';
import { IBabyProfile } from '../../../baby/baby-profile/baby-profile.model';
import { BabyProfileService } from '../../../baby/baby-profile/service/baby-profile.service';
import { BreastFeed, IBreastFeed } from '../breast-feed.model';
import { BreastFeedService } from '../service/breast-feed.service';
import { BreastFeedUpdateComponent } from './breast-feed-update.component';

describe('Component Tests', () => {
  describe('BreastFeed Management Update Component', () => {
    let comp: BreastFeedUpdateComponent;
    let fixture: ComponentFixture<BreastFeedUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let breastFeedService: BreastFeedService;
    let babyProfileService: BabyProfileService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BreastFeedUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BreastFeedUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BreastFeedUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      breastFeedService = TestBed.inject(BreastFeedService);
      babyProfileService = TestBed.inject(BabyProfileService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call BabyProfile query and add missing value', () => {
        const breastFeed: IBreastFeed = { id: 456 };
        const babyProfile: IBabyProfile = { id: 26751 };
        breastFeed.babyProfile = babyProfile;

        const babyProfileCollection: IBabyProfile[] = [{ id: 20797 }];
        jest.spyOn(babyProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: babyProfileCollection })));
        const additionalBabyProfiles = [babyProfile];
        const expectedCollection: IBabyProfile[] = [...additionalBabyProfiles, ...babyProfileCollection];
        jest.spyOn(babyProfileService, 'addBabyProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ breastFeed });
        comp.ngOnInit();

        expect(babyProfileService.query).toHaveBeenCalled();
        expect(babyProfileService.addBabyProfileToCollectionIfMissing).toHaveBeenCalledWith(
          babyProfileCollection,
          ...additionalBabyProfiles
        );
        expect(comp.babyProfilesSharedCollection).toEqual(expectedCollection);
      });

      // it('Should update editForm', () => {
      //   const breastFeed: IBreastFeed = { id: 456 };
      //   const babyProfile: IBabyProfile = { id: 30632 };
      //   breastFeed.babyProfile = babyProfile;

      //   activatedRoute.data = of({ breastFeed });
      //   comp.ngOnInit();

      //   expect(comp.editForm.value).toEqual(expect.objectContaining(breastFeed));
      //   expect(comp.babyProfilesSharedCollection).toContain(babyProfile);
      // });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BreastFeed>>();
        const breastFeed = { id: 123 };
        jest.spyOn(breastFeedService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        jest.spyOn(global.Date, 'now').mockImplementationOnce(() => Date.parse('2021-11-07T08:07:00.000Z'));
        activatedRoute.data = of({ breastFeed });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: breastFeed }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(breastFeedService.update).toHaveBeenCalledWith(breastFeed);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BreastFeed>>();
        const breastFeed = new BreastFeed();
        jest.spyOn(breastFeedService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        jest.spyOn(global.Date, 'now').mockImplementationOnce(() => Date.parse('2021-11-07T08:07:00.000Z'));
        activatedRoute.data = of({ breastFeed });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: breastFeed }));
        saveSubject.complete();

        // THEN
        expect(breastFeedService.create).toHaveBeenCalledWith(breastFeed);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BreastFeed>>();
        const breastFeed = { id: 123 };
        jest.spyOn(breastFeedService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        jest.spyOn(global.Date, 'now').mockImplementationOnce(() => Date.parse('2021-11-07T08:07:00.000Z'));
        activatedRoute.data = of({ breastFeed });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(breastFeedService.update).toHaveBeenCalledWith(breastFeed);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });

    describe('Tracking relationships identifiers', () => {
      describe('trackBabyProfileById', () => {
        it('Should return tracked BabyProfile primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackBabyProfileById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
