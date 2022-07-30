import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { HeightService } from '../service/height.service';
import { IHeight, Height } from '../height.model';
import { IBabyProfile } from 'app/entities/baby-profile/baby-profile.model';
import { BabyProfileService } from 'app/entities/baby-profile/service/baby-profile.service';

import { HeightUpdateComponent } from './height-update.component';

describe('Height Management Update Component', () => {
  let comp: HeightUpdateComponent;
  let fixture: ComponentFixture<HeightUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let heightService: HeightService;
  let babyProfileService: BabyProfileService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [HeightUpdateComponent],
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
      .overrideTemplate(HeightUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HeightUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    heightService = TestBed.inject(HeightService);
    babyProfileService = TestBed.inject(BabyProfileService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call BabyProfile query and add missing value', () => {
      const height: IHeight = { id: 456 };
      const babyProfile: IBabyProfile = { id: 85130 };
      height.babyProfile = babyProfile;

      const babyProfileCollection: IBabyProfile[] = [{ id: 54071 }];
      jest.spyOn(babyProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: babyProfileCollection })));
      const additionalBabyProfiles = [babyProfile];
      const expectedCollection: IBabyProfile[] = [...additionalBabyProfiles, ...babyProfileCollection];
      jest.spyOn(babyProfileService, 'addBabyProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ height });
      comp.ngOnInit();

      expect(babyProfileService.query).toHaveBeenCalled();
      expect(babyProfileService.addBabyProfileToCollectionIfMissing).toHaveBeenCalledWith(babyProfileCollection, ...additionalBabyProfiles);
      expect(comp.babyProfilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const height: IHeight = { id: 456 };
      const babyProfile: IBabyProfile = { id: 8302 };
      height.babyProfile = babyProfile;

      activatedRoute.data = of({ height });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(height));
      expect(comp.babyProfilesSharedCollection).toContain(babyProfile);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Height>>();
      const height = { id: 123 };
      jest.spyOn(heightService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ height });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: height }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(heightService.update).toHaveBeenCalledWith(height);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Height>>();
      const height = new Height();
      jest.spyOn(heightService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ height });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: height }));
      saveSubject.complete();

      // THEN
      expect(heightService.create).toHaveBeenCalledWith(height);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Height>>();
      const height = { id: 123 };
      jest.spyOn(heightService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ height });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(heightService.update).toHaveBeenCalledWith(height);
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
