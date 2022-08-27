import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BabyProfileComponent } from '../list/baby-profile.component';
import { BabyProfileDetailComponent } from '../detail/baby-profile-detail.component';
import { BabyProfileUpdateComponent } from '../update/baby-profile-update.component';
import { BabyProfileRoutingResolveService } from './baby-profile-routing-resolve.service';

const babyProfileRoute: Routes = [
  {
    path: '',
    component: BabyProfileComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BabyProfileDetailComponent,
    resolve: {
      babyProfile: BabyProfileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BabyProfileUpdateComponent,
    resolve: {
      babyProfile: BabyProfileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BabyProfileUpdateComponent,
    resolve: {
      babyProfile: BabyProfileRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(babyProfileRoute)],
  exports: [RouterModule],
})
export class BabyProfileRoutingModule {}
