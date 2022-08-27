jest.mock('@angular/router');

import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';
import { IBabyProfile } from '../../../baby/baby-profile/baby-profile.model';
import { BabyProfileService } from '../../../baby/baby-profile/service/baby-profile.service';
import { IHumor } from '../../../baby/humor/humor.model';
import { HumorService } from '../../../baby/humor/service/humor.service';
import { HumorHistory, IHumorHistory } from '../humor-history.model';
import { HumorHistoryService } from '../service/humor-history.service';
import { HumorHistoryUpdateComponent } from './humor-history-update.component';

describe('Component Tests', () => {
  describe('HumorHistory Management Update Component', () => {
    let comp: HumorHistoryUpdateComponent;
    let fixture: ComponentFixture<HumorHistoryUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let humorHistoryService: HumorHistoryService;
    let babyProfileService: BabyProfileService;
    let humorService: HumorService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [HumorHistoryUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(HumorHistoryUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(HumorHistoryUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      humorHistoryService = TestBed.inject(HumorHistoryService);
      babyProfileService = TestBed.inject(BabyProfileService);
      humorService = TestBed.inject(HumorService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should call BabyProfile query and add missing value', () => {
        const humorHistory: IHumorHistory = { id: 456 };
        const babyProfile: IBabyProfile = { id: 54737 };
        humorHistory.babyProfile = babyProfile;

        const babyProfileCollection: IBabyProfile[] = [{ id: 91602 }];
        jest.spyOn(babyProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: babyProfileCollection })));
        const additionalBabyProfiles = [babyProfile];
        const expectedCollection: IBabyProfile[] = [...additionalBabyProfiles, ...babyProfileCollection];
        jest.spyOn(babyProfileService, 'addBabyProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ humorHistory });
        comp.ngOnInit();

        expect(babyProfileService.query).toHaveBeenCalled();
        expect(babyProfileService.addBabyProfileToCollectionIfMissing).toHaveBeenCalledWith(
          babyProfileCollection,
          ...additionalBabyProfiles
        );
        expect(comp.babyProfilesSharedCollection).toEqual(expectedCollection);
      });

      it('Should call Humor query and add missing value', () => {
        const humorHistory: IHumorHistory = { id: 456 };
        const humor: IHumor = { id: 80993 };
        humorHistory.humor = humor;

        const humorCollection: IHumor[] = [{ id: 53807 }];
        jest.spyOn(humorService, 'query').mockReturnValue(of(new HttpResponse({ body: humorCollection })));
        const additionalHumors = [humor];
        const expectedCollection: IHumor[] = [...additionalHumors, ...humorCollection];
        jest.spyOn(humorService, 'addHumorToCollectionIfMissing').mockReturnValue(expectedCollection);

        activatedRoute.data = of({ humorHistory });
        comp.ngOnInit();

        expect(humorService.query).toHaveBeenCalled();
        expect(humorService.addHumorToCollectionIfMissing).toHaveBeenCalledWith(humorCollection, ...additionalHumors);
        expect(comp.humorsSharedCollection).toEqual(expectedCollection);
      });

      it('Should update editForm', () => {
        const humorHistory: IHumorHistory = { id: 456 };
        const babyProfile: IBabyProfile = { id: 95888 };
        humorHistory.babyProfile = babyProfile;
        const humor: IHumor = { id: 56492 };
        humorHistory.humor = humor;

        activatedRoute.data = of({ humorHistory });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(humorHistory));
        expect(comp.babyProfilesSharedCollection).toContain(babyProfile);
        expect(comp.humorsSharedCollection).toContain(humor);
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<HumorHistory>>();
        const humorHistory = { id: 123 };
        jest.spyOn(humorHistoryService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ humorHistory });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: humorHistory }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(humorHistoryService.update).toHaveBeenCalledWith(humorHistory);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<HumorHistory>>();
        const humorHistory = new HumorHistory();
        jest.spyOn(humorHistoryService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        jest.spyOn(global.Date, 'now').mockImplementationOnce(() => new Date('2021-11-13T08:07:10.000Z').getMilliseconds());
        activatedRoute.data = of({ humorHistory });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: humorHistory }));
        saveSubject.complete();

        // THEN
        expect(humorHistoryService.create).toHaveBeenCalledWith(humorHistory);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<HumorHistory>>();
        const humorHistory = { id: 123 };
        jest.spyOn(humorHistoryService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ humorHistory });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(humorHistoryService.update).toHaveBeenCalledWith(humorHistory);
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

      describe('trackHumorById', () => {
        it('Should return tracked Humor primary key', () => {
          const entity = { id: 123 };
          const trackResult = comp.trackHumorById(0, entity);
          expect(trackResult).toEqual(entity.id);
        });
      });
    });
  });
});
