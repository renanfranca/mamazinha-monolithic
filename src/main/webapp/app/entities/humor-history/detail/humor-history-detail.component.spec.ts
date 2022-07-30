import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { HumorHistoryDetailComponent } from './humor-history-detail.component';

describe('HumorHistory Management Detail Component', () => {
  let comp: HumorHistoryDetailComponent;
  let fixture: ComponentFixture<HumorHistoryDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [HumorHistoryDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ humorHistory: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(HumorHistoryDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(HumorHistoryDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load humorHistory on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.humorHistory).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
