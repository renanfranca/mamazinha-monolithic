import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { HeightComponent } from '../list/height.component';
import { HeightDetailComponent } from '../detail/height-detail.component';
import { HeightUpdateComponent } from '../update/height-update.component';
import { HeightRoutingResolveService } from './height-routing-resolve.service';

const heightRoute: Routes = [
  {
    path: '',
    component: HeightComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HeightDetailComponent,
    resolve: {
      height: HeightRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HeightUpdateComponent,
    resolve: {
      height: HeightRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HeightUpdateComponent,
    resolve: {
      height: HeightRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(heightRoute)],
  exports: [RouterModule],
})
export class HeightRoutingModule {}
