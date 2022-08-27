jest.mock('@angular/router');

import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { of, Subject } from 'rxjs';
import { BabyProfile, IBabyProfile } from '../baby-profile.model';
import { BabyProfileService } from '../service/baby-profile.service';
import { BabyProfileUpdateComponent } from './baby-profile-update.component';

describe('Component Tests', () => {
  describe('BabyProfile Management Update Component', () => {
    let comp: BabyProfileUpdateComponent;
    let fixture: ComponentFixture<BabyProfileUpdateComponent>;
    let activatedRoute: ActivatedRoute;
    let babyProfileService: BabyProfileService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BabyProfileUpdateComponent],
        providers: [FormBuilder, ActivatedRoute],
      })
        .overrideTemplate(BabyProfileUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BabyProfileUpdateComponent);
      activatedRoute = TestBed.inject(ActivatedRoute);
      babyProfileService = TestBed.inject(BabyProfileService);

      comp = fixture.componentInstance;
    });

    describe('ngOnInit', () => {
      it('Should update editForm', () => {
        const babyProfile: IBabyProfile = { id: 456 };

        activatedRoute.data = of({ babyProfile });
        comp.ngOnInit();

        expect(comp.editForm.value).toEqual(expect.objectContaining(babyProfile));
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BabyProfile>>();
        const babyProfile = { id: 123 };
        jest.spyOn(babyProfileService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ babyProfile });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: babyProfile }));
        saveSubject.complete();

        // THEN
        expect(comp.previousState).toHaveBeenCalled();
        expect(babyProfileService.update).toHaveBeenCalledWith(babyProfile);
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BabyProfile>>();
        const babyProfile = new BabyProfile();
        jest.spyOn(babyProfileService, 'create').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        jest.spyOn(global.Date, 'now').mockImplementationOnce(() => new Date('2021-11-13T08:07:10.000Z').getMilliseconds());
        activatedRoute.data = of({ babyProfile });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.next(new HttpResponse({ body: babyProfile }));
        saveSubject.complete();

        // THEN
        expect(babyProfileService.create).toHaveBeenCalledWith(babyProfile);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).toHaveBeenCalled();
      });

      it('Should set isSaving to false on error', () => {
        // GIVEN
        const saveSubject = new Subject<HttpResponse<BabyProfile>>();
        const babyProfile = { id: 123 };
        jest.spyOn(babyProfileService, 'update').mockReturnValue(saveSubject);
        jest.spyOn(comp, 'previousState');
        activatedRoute.data = of({ babyProfile });
        comp.ngOnInit();

        // WHEN
        comp.save();
        expect(comp.isSaving).toEqual(true);
        saveSubject.error('This is an error!');

        // THEN
        expect(babyProfileService.update).toHaveBeenCalledWith(babyProfile);
        expect(comp.isSaving).toEqual(false);
        expect(comp.previousState).not.toHaveBeenCalled();
      });
    });
  });
});
