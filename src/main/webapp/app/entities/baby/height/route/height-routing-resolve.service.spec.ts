jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IHeight, Height } from '../height.model';
import { HeightService } from '../service/height.service';

import { HeightRoutingResolveService } from './height-routing-resolve.service';

describe('Service Tests', () => {
  describe('Height routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: HeightRoutingResolveService;
    let service: HeightService;
    let resultHeight: IHeight | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(HeightRoutingResolveService);
      service = TestBed.inject(HeightService);
      resultHeight = undefined;
    });

    describe('resolve', () => {
      it('should return IHeight returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultHeight = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultHeight).toEqual({ id: 123 });
      });

      it('should return new IHeight if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultHeight = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultHeight).toEqual(new Height());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Height })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultHeight = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultHeight).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
