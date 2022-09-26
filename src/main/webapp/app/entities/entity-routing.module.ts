import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'humor',
        data: { pageTitle: 'gatewayApp.babyHumor.home.title' },
        loadChildren: () => import('./baby/humor/humor.module').then(m => m.BabyHumorModule),
      },
      {
        path: 'baby-profile',
        data: { pageTitle: 'gatewayApp.babyBabyProfile.home.title' },
        loadChildren: () => import('./baby/baby-profile/baby-profile.module').then(m => m.BabyBabyProfileModule),
      },
      {
        path: 'breast-feed',
        data: { pageTitle: 'gatewayApp.babyBreastFeed.home.title' },
        loadChildren: () => import('./baby/breast-feed/breast-feed.module').then(m => m.BabyBreastFeedModule),
      },
      {
        path: 'height',
        data: { pageTitle: 'gatewayApp.babyHeight.home.title' },
        loadChildren: () => import('./baby/height/height.module').then(m => m.BabyHeightModule),
      },
      {
        path: 'humor-history',
        data: { pageTitle: 'gatewayApp.babyHumorHistory.home.title' },
        loadChildren: () => import('./baby/humor-history/humor-history.module').then(m => m.BabyHumorHistoryModule),
      },
      {
        path: 'nap',
        data: { pageTitle: 'gatewayApp.babyNap.home.title' },
        loadChildren: () => import('./baby/nap/nap.module').then(m => m.BabyNapModule),
      },
      {
        path: 'weight',
        data: { pageTitle: 'gatewayApp.babyWeight.home.title' },
        loadChildren: () => import('./baby/weight/weight.module').then(m => m.BabyWeightModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
