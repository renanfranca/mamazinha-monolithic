import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHumor, Humor } from '../humor.model';
import { HumorService } from '../service/humor.service';

@Injectable({ providedIn: 'root' })
export class HumorRoutingResolveService implements Resolve<IHumor> {
  constructor(protected service: HumorService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IHumor> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((humor: HttpResponse<Humor>) => {
          if (humor.body) {
            return of(humor.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Humor());
  }
}
