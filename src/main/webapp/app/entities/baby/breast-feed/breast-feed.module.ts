import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BreastFeedComponent } from './list/breast-feed.component';
import { BreastFeedDetailComponent } from './detail/breast-feed-detail.component';
import { BreastFeedUpdateComponent } from './update/breast-feed-update.component';
import { BreastFeedDeleteDialogComponent } from './delete/breast-feed-delete-dialog.component';
import { BreastFeedRoutingModule } from './route/breast-feed-routing.module';

@NgModule({
  imports: [SharedModule, BreastFeedRoutingModule],
  declarations: [BreastFeedComponent, BreastFeedDetailComponent, BreastFeedUpdateComponent, BreastFeedDeleteDialogComponent],
  entryComponents: [BreastFeedDeleteDialogComponent],
})
export class BabyBreastFeedModule {}
