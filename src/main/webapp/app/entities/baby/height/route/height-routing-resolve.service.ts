import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHeight, Height } from '../height.model';
import { HeightService } from '../service/height.service';

@Injectable({ providedIn: 'root' })
export class HeightRoutingResolveService implements Resolve<IHeight> {
  constructor(protected service: HeightService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IHeight> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((height: HttpResponse<Height>) => {
          if (height.body) {
            return of(height.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Height());
  }
}
