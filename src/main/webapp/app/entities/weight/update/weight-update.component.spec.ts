import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { WeightService } from '../service/weight.service';
import { IWeight, Weight } from '../weight.model';
import { IBabyProfile } from 'app/entities/baby-profile/baby-profile.model';
import { BabyProfileService } from 'app/entities/baby-profile/service/baby-profile.service';

import { WeightUpdateComponent } from './weight-update.component';

describe('Weight Management Update Component', () => {
  let comp: WeightUpdateComponent;
  let fixture: ComponentFixture<WeightUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let weightService: WeightService;
  let babyProfileService: BabyProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [WeightUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(WeightUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(WeightUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    weightService = TestBed.inject(WeightService);
    babyProfileService = TestBed.inject(BabyProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call BabyProfile query and add missing value', () => {
      const weight: IWeight = { id: 456 };
      const babyProfile: IBabyProfile = { id: 44179 };
      weight.babyProfile = babyProfile;

      const babyProfileCollection: IBabyProfile[] = [{ id: 53891 }];
      jest.spyOn(babyProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: babyProfileCollection })));
      const additionalBabyProfiles = [babyProfile];
      const expectedCollection: IBabyProfile[] = [...additionalBabyProfiles, ...babyProfileCollection];
      jest.spyOn(babyProfileService, 'addBabyProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ weight });
      comp.ngOnInit();

      expect(babyProfileService.query).toHaveBeenCalled();
      expect(babyProfileService.addBabyProfileToCollectionIfMissing).toHaveBeenCalledWith(babyProfileCollection, ...additionalBabyProfiles);
      expect(comp.babyProfilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const weight: IWeight = { id: 456 };
      const babyProfile: IBabyProfile = { id: 14934 };
      weight.babyProfile = babyProfile;

      activatedRoute.data = of({ weight });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(weight));
      expect(comp.babyProfilesSharedCollection).toContain(babyProfile);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Weight>>();
      const weight = { id: 123 };
      jest.spyOn(weightService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ weight });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: weight }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(weightService.update).toHaveBeenCalledWith(weight);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Weight>>();
      const weight = new Weight();
      jest.spyOn(weightService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ weight });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: weight }));
      saveSubject.complete();

      // THEN
      expect(weightService.create).toHaveBeenCalledWith(weight);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Weight>>();
      const weight = { id: 123 };
      jest.spyOn(weightService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ weight });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(weightService.update).toHaveBeenCalledWith(weight);
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
