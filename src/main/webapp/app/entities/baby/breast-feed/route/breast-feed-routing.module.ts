import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BreastFeedComponent } from '../list/breast-feed.component';
import { BreastFeedDetailComponent } from '../detail/breast-feed-detail.component';
import { BreastFeedUpdateComponent } from '../update/breast-feed-update.component';
import { BreastFeedRoutingResolveService } from './breast-feed-routing-resolve.service';

const breastFeedRoute: Routes = [
  {
    path: '',
    component: BreastFeedComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BreastFeedDetailComponent,
    resolve: {
      breastFeed: BreastFeedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BreastFeedUpdateComponent,
    resolve: {
      breastFeed: BreastFeedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BreastFeedUpdateComponent,
    resolve: {
      breastFeed: BreastFeedRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(breastFeedRoute)],
  exports: [RouterModule],
})
export class BreastFeedRoutingModule {}
