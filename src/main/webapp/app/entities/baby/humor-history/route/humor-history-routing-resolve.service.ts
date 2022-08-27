import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IHumorHistory, HumorHistory } from '../humor-history.model';
import { HumorHistoryService } from '../service/humor-history.service';

@Injectable({ providedIn: 'root' })
export class HumorHistoryRoutingResolveService implements Resolve<IHumorHistory> {
  constructor(protected service: HumorHistoryService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IHumorHistory> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((humorHistory: HttpResponse<HumorHistory>) => {
          if (humorHistory.body) {
            return of(humorHistory.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new HumorHistory());
  }
}
