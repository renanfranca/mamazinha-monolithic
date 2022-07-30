import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { NapService } from '../service/nap.service';
import { INap, Nap } from '../nap.model';
import { IBabyProfile } from 'app/entities/baby-profile/baby-profile.model';
import { BabyProfileService } from 'app/entities/baby-profile/service/baby-profile.service';
import { IHumor } from 'app/entities/humor/humor.model';
import { HumorService } from 'app/entities/humor/service/humor.service';

import { NapUpdateComponent } from './nap-update.component';

describe('Nap Management Update Component', () => {
  let comp: NapUpdateComponent;
  let fixture: ComponentFixture<NapUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let napService: NapService;
  let babyProfileService: BabyProfileService;
  let humorService: HumorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [NapUpdateComponent],
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
      .overrideTemplate(NapUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(NapUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    napService = TestBed.inject(NapService);
    babyProfileService = TestBed.inject(BabyProfileService);
    humorService = TestBed.inject(HumorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call BabyProfile query and add missing value', () => {
      const nap: INap = { id: 456 };
      const babyProfile: IBabyProfile = { id: 56029 };
      nap.babyProfile = babyProfile;

      const babyProfileCollection: IBabyProfile[] = [{ id: 23167 }];
      jest.spyOn(babyProfileService, 'query').mockReturnValue(of(new HttpResponse({ body: babyProfileCollection })));
      const additionalBabyProfiles = [babyProfile];
      const expectedCollection: IBabyProfile[] = [...additionalBabyProfiles, ...babyProfileCollection];
      jest.spyOn(babyProfileService, 'addBabyProfileToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ nap });
      comp.ngOnInit();

      expect(babyProfileService.query).toHaveBeenCalled();
      expect(babyProfileService.addBabyProfileToCollectionIfMissing).toHaveBeenCalledWith(babyProfileCollection, ...additionalBabyProfiles);
      expect(comp.babyProfilesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Humor query and add missing value', () => {
      const nap: INap = { id: 456 };
      const humor: IHumor = { id: 1943 };
      nap.humor = humor;

      const humorCollection: IHumor[] = [{ id: 11750 }];
      jest.spyOn(humorService, 'query').mockReturnValue(of(new HttpResponse({ body: humorCollection })));
      const additionalHumors = [humor];
      const expectedCollection: IHumor[] = [...additionalHumors, ...humorCollection];
      jest.spyOn(humorService, 'addHumorToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ nap });
      comp.ngOnInit();

      expect(humorService.query).toHaveBeenCalled();
      expect(humorService.addHumorToCollectionIfMissing).toHaveBeenCalledWith(humorCollection, ...additionalHumors);
      expect(comp.humorsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const nap: INap = { id: 456 };
      const babyProfile: IBabyProfile = { id: 7412 };
      nap.babyProfile = babyProfile;
      const humor: IHumor = { id: 2263 };
      nap.humor = humor;

      activatedRoute.data = of({ nap });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(nap));
      expect(comp.babyProfilesSharedCollection).toContain(babyProfile);
      expect(comp.humorsSharedCollection).toContain(humor);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Nap>>();
      const nap = { id: 123 };
      jest.spyOn(napService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nap });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: nap }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(napService.update).toHaveBeenCalledWith(nap);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Nap>>();
      const nap = new Nap();
      jest.spyOn(napService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nap });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: nap }));
      saveSubject.complete();

      // THEN
      expect(napService.create).toHaveBeenCalledWith(nap);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Nap>>();
      const nap = { id: 123 };
      jest.spyOn(napService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ nap });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(napService.update).toHaveBeenCalledWith(nap);
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
