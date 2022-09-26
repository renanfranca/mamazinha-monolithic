import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { NapComponent } from '../list/nap.component';
import { NapDetailComponent } from '../detail/nap-detail.component';
import { NapUpdateComponent } from '../update/nap-update.component';
import { NapRoutingResolveService } from './nap-routing-resolve.service';

const napRoute: Routes = [
  {
    path: '',
    component: NapComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: NapDetailComponent,
    resolve: {
      nap: NapRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: NapUpdateComponent,
    resolve: {
      nap: NapRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: NapUpdateComponent,
    resolve: {
      nap: NapRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(napRoute)],
  exports: [RouterModule],
})
export class NapRoutingModule {}
