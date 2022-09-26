import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { of } from 'rxjs';

import { MetricsComponent } from './metrics.component';
import { Metrics } from './metrics.model';
import { MetricsService } from './metrics.service';

describe('Component Tests', () => {
  describe('MetricsComponent', () => {
    let comp: MetricsComponent;
    let fixture: ComponentFixture<MetricsComponent>;
    let service: MetricsService;

    beforeEach(
      waitForAsync(() => {
        TestBed.configureTestingModule({
          imports: [HttpClientTestingModule],
          declarations: [MetricsComponent],
        })
          .overrideTemplate(MetricsComponent, '')
          .compileComponents();
      })
    );

    beforeEach(() => {
      fixture = TestBed.createComponent(MetricsComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(MetricsService);
    });

    describe('refresh', () => {
      it('should call refresh on init', () => {
        // GIVEN
        jest.spyOn(service, 'getMetrics').mockReturnValue(of({} as Metrics));

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(service.getMetrics).toHaveBeenCalled();
      });
    });
  });
});
