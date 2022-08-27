jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IBabyProfile, BabyProfile } from '../baby-profile.model';
import { BabyProfileService } from '../service/baby-profile.service';

import { BabyProfileRoutingResolveService } from './baby-profile-routing-resolve.service';

describe('Service Tests', () => {
  describe('BabyProfile routing resolve service', () => {
    let mockRouter: Router;
    let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
    let routingResolveService: BabyProfileRoutingResolveService;
    let service: BabyProfileService;
    let resultBabyProfile: IBabyProfile | undefined;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
        providers: [Router, ActivatedRouteSnapshot],
      });
      mockRouter = TestBed.inject(Router);
      mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
      routingResolveService = TestBed.inject(BabyProfileRoutingResolveService);
      service = TestBed.inject(BabyProfileService);
      resultBabyProfile = undefined;
    });

    describe('resolve', () => {
      it('should return IBabyProfile returned by find', () => {
        // GIVEN
        service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultBabyProfile = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultBabyProfile).toEqual({ id: 123 });
      });

      it('should return new IBabyProfile if id is not provided', () => {
        // GIVEN
        service.find = jest.fn();
        mockActivatedRouteSnapshot.params = {};

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultBabyProfile = result;
        });

        // THEN
        expect(service.find).not.toBeCalled();
        expect(resultBabyProfile).toEqual(new BabyProfile());
      });

      it('should route to 404 page if data not found in server', () => {
        // GIVEN
        jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as BabyProfile })));
        mockActivatedRouteSnapshot.params = { id: 123 };

        // WHEN
        routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
          resultBabyProfile = result;
        });

        // THEN
        expect(service.find).toBeCalledWith(123);
        expect(resultBabyProfile).toEqual(undefined);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
      });
    });
  });
});
