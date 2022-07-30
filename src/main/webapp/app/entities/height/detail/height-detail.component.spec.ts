import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { HeightDetailComponent } from './height-detail.component';

describe('Height Management Detail Component', () => {
  let comp: HeightDetailComponent;
  let fixture: ComponentFixture<HeightDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HeightDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ height: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(HeightDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(HeightDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load height on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.height).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
