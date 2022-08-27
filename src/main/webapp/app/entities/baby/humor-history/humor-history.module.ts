import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { HumorHistoryComponent } from './list/humor-history.component';
import { HumorHistoryDetailComponent } from './detail/humor-history-detail.component';
import { HumorHistoryUpdateComponent } from './update/humor-history-update.component';
import { HumorHistoryDeleteDialogComponent } from './delete/humor-history-delete-dialog.component';
import { HumorHistoryRoutingModule } from './route/humor-history-routing.module';

@NgModule({
  imports: [SharedModule, HumorHistoryRoutingModule],
  declarations: [HumorHistoryComponent, HumorHistoryDetailComponent, HumorHistoryUpdateComponent, HumorHistoryDeleteDialogComponent],
  entryComponents: [HumorHistoryDeleteDialogComponent],
})
export class BabyHumorHistoryModule {}
