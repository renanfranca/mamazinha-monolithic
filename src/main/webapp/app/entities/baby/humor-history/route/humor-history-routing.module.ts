import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { HumorHistoryComponent } from '../list/humor-history.component';
import { HumorHistoryDetailComponent } from '../detail/humor-history-detail.component';
import { HumorHistoryUpdateComponent } from '../update/humor-history-update.component';
import { HumorHistoryRoutingResolveService } from './humor-history-routing-resolve.service';

const humorHistoryRoute: Routes = [
  {
    path: '',
    component: HumorHistoryComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: HumorHistoryDetailComponent,
    resolve: {
      humorHistory: HumorHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: HumorHistoryUpdateComponent,
    resolve: {
      humorHistory: HumorHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: HumorHistoryUpdateComponent,
    resolve: {
      humorHistory: HumorHistoryRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(humorHistoryRoute)],
  exports: [RouterModule],
})
export class HumorHistoryRoutingModule {}
