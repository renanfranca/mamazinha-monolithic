import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { HumorComponent } from '../list/humor.component';
import { HumorDetailComponent } from '../detail/humor-detail.component';
import { HumorUpdateComponent } from '../update/humor-update.component';
import { HumorRoutingResolveService } from './humor-routing-resolve.service';

const humorRoute: Routes = [
  {
    path: '',
    component: HumorComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HumorDetailComponent,
    resolve: {
      humor: HumorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HumorUpdateComponent,
    resolve: {
      humor: HumorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HumorUpdateComponent,
    resolve: {
      humor: HumorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(humorRoute)],
  exports: [RouterModule],
})
export class HumorRoutingModule {}
