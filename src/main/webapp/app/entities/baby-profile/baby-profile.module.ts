import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BabyProfileComponent } from './list/baby-profile.component';
import { BabyProfileDetailComponent } from './detail/baby-profile-detail.component';
import { BabyProfileUpdateComponent } from './update/baby-profile-update.component';
import { BabyProfileDeleteDialogComponent } from './delete/baby-profile-delete-dialog.component';
import { BabyProfileRoutingModule } from './route/baby-profile-routing.module';

@NgModule({
  imports: [SharedModule, BabyProfileRoutingModule],
  declarations: [BabyProfileComponent, BabyProfileDetailComponent, BabyProfileUpdateComponent, BabyProfileDeleteDialogComponent],
  entryComponents: [BabyProfileDeleteDialogComponent],
})
export class BabyProfileModule {}
