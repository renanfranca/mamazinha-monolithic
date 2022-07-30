import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'baby-profile',
        data: { pageTitle: 'babyApp.babyProfile.home.title' },
        loadChildren: () => import('./baby-profile/baby-profile.module').then(m => m.BabyProfileModule),
      },
      {
        path: 'breast-feed',
        data: { pageTitle: 'babyApp.breastFeed.home.title' },
        loadChildren: () => import('./breast-feed/breast-feed.module').then(m => m.BreastFeedModule),
      },
      {
        path: 'height',
        data: { pageTitle: 'babyApp.height.home.title' },
        loadChildren: () => import('./height/height.module').then(m => m.HeightModule),
      },
      {
        path: 'humor',
        data: { pageTitle: 'babyApp.humor.home.title' },
        loadChildren: () => import('./humor/humor.module').then(m => m.HumorModule),
      },
      {
        path: 'humor-history',
        data: { pageTitle: 'babyApp.humorHistory.home.title' },
        loadChildren: () => import('./humor-history/humor-history.module').then(m => m.HumorHistoryModule),
      },
      {
        path: 'nap',
        data: { pageTitle: 'babyApp.nap.home.title' },
        loadChildren: () => import('./nap/nap.module').then(m => m.NapModule),
      },
      {
        path: 'weight',
        data: { pageTitle: 'babyApp.weight.home.title' },
        loadChildren: () => import('./weight/weight.module').then(m => m.WeightModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
