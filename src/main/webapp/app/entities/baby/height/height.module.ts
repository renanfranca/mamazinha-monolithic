import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { HeightComponent } from './list/height.component';
import { HeightDetailComponent } from './detail/height-detail.component';
import { HeightUpdateComponent } from './update/height-update.component';
import { HeightDeleteDialogComponent } from './delete/height-delete-dialog.component';
import { HeightRoutingModule } from './route/height-routing.module';

@NgModule({
  imports: [SharedModule, HeightRoutingModule],
  declarations: [HeightComponent, HeightDetailComponent, HeightUpdateComponent, HeightDeleteDialogComponent],
  entryComponents: [HeightDeleteDialogComponent],
})
export class BabyHeightModule {}
