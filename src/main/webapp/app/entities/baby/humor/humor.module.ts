import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { HumorComponent } from './list/humor.component';
import { HumorDetailComponent } from './detail/humor-detail.component';
import { HumorUpdateComponent } from './update/humor-update.component';
import { HumorDeleteDialogComponent } from './delete/humor-delete-dialog.component';
import { HumorRoutingModule } from './route/humor-routing.module';

@NgModule({
  imports: [SharedModule, HumorRoutingModule],
  declarations: [HumorComponent, HumorDetailComponent, HumorUpdateComponent, HumorDeleteDialogComponent],
  entryComponents: [HumorDeleteDialogComponent],
})
export class BabyHumorModule {}
