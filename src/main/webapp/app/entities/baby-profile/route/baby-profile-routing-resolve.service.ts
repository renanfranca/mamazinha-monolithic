import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBabyProfile, BabyProfile } from '../baby-profile.model';
import { BabyProfileService } from '../service/baby-profile.service';

@Injectable({ providedIn: 'root' })
export class BabyProfileRoutingResolveService implements Resolve<IBabyProfile> {
  constructor(protected service: BabyProfileService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBabyProfile> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((babyProfile: HttpResponse<BabyProfile>) => {
          if (babyProfile.body) {
            return of(babyProfile.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BabyProfile());
  }
}
