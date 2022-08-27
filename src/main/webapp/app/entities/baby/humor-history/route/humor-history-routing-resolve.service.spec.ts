jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IHumorHistory, HumorHistory } from '../humor-history.model';
import { HumorHistoryService } from '../service/humor-history.service';

import { HumorHistoryRoutingResolveService } from './humor-history-routing-resolve.service';

describe('Service Tests', () => {
  describe('HumorHistory routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: HumorHistoryRoutingResolveService;
    let service: HumorHistoryService;
    let resultHumorHistory: IHumorHistory | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(HumorHistoryRoutingResolveService);
      service = TestBed.inject(HumorHistoryService);
      resultHumorHistory = undefined;
    });

    describe('resolve', () => {
      it('should return IHumorHistory returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultHumorHistory = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultHumorHistory).toEqual({ id: 123 });
      });

      it('should return new IHumorHistory if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultHumorHistory = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultHumorHistory).toEqual(new HumorHistory());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as HumorHistory })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultHumorHistory = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultHumorHistory).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
