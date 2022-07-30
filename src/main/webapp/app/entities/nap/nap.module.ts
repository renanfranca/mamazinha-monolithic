import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { NapComponent } from './list/nap.component';
import { NapDetailComponent } from './detail/nap-detail.component';
import { NapUpdateComponent } from './update/nap-update.component';
import { NapDeleteDialogComponent } from './delete/nap-delete-dialog.component';
import { NapRoutingModule } from './route/nap-routing.module';

@NgModule({
  imports: [SharedModule, NapRoutingModule],
  declarations: [NapComponent, NapDetailComponent, NapUpdateComponent, NapDeleteDialogComponent],
  entryComponents: [NapDeleteDialogComponent],
})
export class NapModule {}
