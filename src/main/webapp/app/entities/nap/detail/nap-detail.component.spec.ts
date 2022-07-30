import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { NapDetailComponent } from './nap-detail.component';

describe('Nap Management Detail Component', () => {
  let comp: NapDetailComponent;
  let fixture: ComponentFixture<NapDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [NapDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ nap: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(NapDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(NapDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load nap on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.nap).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
