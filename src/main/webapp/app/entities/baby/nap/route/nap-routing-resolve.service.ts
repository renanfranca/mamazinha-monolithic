import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { INap, Nap } from '../nap.model';
import { NapService } from '../service/nap.service';

@Injectable({ providedIn: 'root' })
export class NapRoutingResolveService implements Resolve<INap> {
  constructor(protected service: NapService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<INap> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((nap: HttpResponse<Nap>) => {
          if (nap.body) {
            return of(nap.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Nap());
  }
}
