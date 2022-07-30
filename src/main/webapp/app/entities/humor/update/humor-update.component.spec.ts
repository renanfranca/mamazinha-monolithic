import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { HumorService } from '../service/humor.service';
import { IHumor, Humor } from '../humor.model';

import { HumorUpdateComponent } from './humor-update.component';

describe('Humor Management Update Component', () => {
  let comp: HumorUpdateComponent;
  let fixture: ComponentFixture<HumorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let humorService: HumorService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [HumorUpdateComponent],
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
      .overrideTemplate(HumorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(HumorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    humorService = TestBed.inject(HumorService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const humor: IHumor = { id: 456 };

      activatedRoute.data = of({ humor });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(humor));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Humor>>();
      const humor = { id: 123 };
      jest.spyOn(humorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ humor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: humor }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(humorService.update).toHaveBeenCalledWith(humor);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Humor>>();
      const humor = new Humor();
      jest.spyOn(humorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ humor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: humor }));
      saveSubject.complete();

      // THEN
      expect(humorService.create).toHaveBeenCalledWith(humor);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Humor>>();
      const humor = { id: 123 };
      jest.spyOn(humorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ humor });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(humorService.update).toHaveBeenCalledWith(humor);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
