import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BreastFeedDetailComponent } from './breast-feed-detail.component';

describe('Component Tests', () => {
  describe('BreastFeed Management Detail Component', () => {
    let comp: BreastFeedDetailComponent;
    let fixture: ComponentFixture<BreastFeedDetailComponent>;

    beforeEach(() => {
      TestBed.configureTestingModule({
        declarations: [BreastFeedDetailComponent],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: { data: of({ breastFeed: { id: 123 } }) },
          },
        ],
      })
        .overrideTemplate(BreastFeedDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(BreastFeedDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should load breastFeed on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.breastFeed).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
  });
});
