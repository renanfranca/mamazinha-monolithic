import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { BreastFeedService } from '../service/breast-feed.service';
import { BreastFeedComponent } from './breast-feed.component';

describe('Component Tests', () => {
  describe('BreastFeed Management Component', () => {
    let comp: BreastFeedComponent;
    let fixture: ComponentFixture<BreastFeedComponent>;
    let service: BreastFeedService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        declarations: [BreastFeedComponent],
      })
        .overrideTemplate(BreastFeedComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(BreastFeedComponent);
      comp = fixture.componentInstance;
      service = TestBed.inject(BreastFeedService);

      const headers = new HttpHeaders().append('link', 'link;link');
      jest.spyOn(service, 'query').mockReturnValue(
        of(
          new HttpResponse({
            body: [{ id: 123 }],
            headers,
          })
        )
      );
    });

    it('Should call load all on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.breastFeeds[0]).toEqual(expect.objectContaining({ id: 123 }));
    });

    it('should load a page', () => {
      // WHEN
      comp.loadPage(1);

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.breastFeeds[0]).toEqual(expect.objectContaining({ id: 123 }));
    });

    it('should calculate the sort attribute for a start', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalledWith(expect.objectContaining({ sort: ['start,desc'] }));
    });

    it('should calculate the sort attribute for a non-id attribute', () => {
      // INIT
      comp.ngOnInit();

      // GIVEN
      comp.predicate = 'name';

      // WHEN
      comp.loadPage(1);

      // THEN
      expect(service.query).toHaveBeenLastCalledWith(expect.objectContaining({ sort: ['name,desc', 'start'] }));
    });

    it('should re-initialize the page', () => {
      // WHEN
      comp.loadPage(1);
      comp.reset();

      // THEN
      expect(comp.page).toEqual(0);
      expect(service.query).toHaveBeenCalledTimes(2);
      expect(comp.breastFeeds[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
