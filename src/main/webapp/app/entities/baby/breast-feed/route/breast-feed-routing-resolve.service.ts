import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBreastFeed, BreastFeed } from '../breast-feed.model';
import { BreastFeedService } from '../service/breast-feed.service';

@Injectable({ providedIn: 'root' })
export class BreastFeedRoutingResolveService implements Resolve<IBreastFeed> {
  constructor(protected service: BreastFeedService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBreastFeed> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((breastFeed: HttpResponse<BreastFeed>) => {
          if (breastFeed.body) {
            return of(breastFeed.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new BreastFeed());
  }
}
