import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { IBreastFeed, BreastFeed } from '../breast-feed.model';
import { BreastFeedService } from '../service/breast-feed.service';

import { BreastFeedRoutingResolveService } from './breast-feed-routing-resolve.service';

describe('BreastFeed routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: BreastFeedRoutingResolveService;
  let service: BreastFeedService;
  let resultBreastFeed: IBreastFeed | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(BreastFeedRoutingResolveService);
    service = TestBed.inject(BreastFeedService);
    resultBreastFeed = undefined;
  });

  describe('resolve', () => {
    it('should return IBreastFeed returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBreastFeed = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultBreastFeed).toEqual({ id: 123 });
    });

    it('should return new IBreastFeed if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBreastFeed = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultBreastFeed).toEqual(new BreastFeed());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as BreastFeed })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultBreastFeed = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultBreastFeed).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
