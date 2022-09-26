import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import 'd3';
import { NvD3Module } from 'ng2-nvd3';
import 'nvd3';
import { BabyProfileDeleteDialogComponent } from './delete/baby-profile-delete-dialog.component';
import { BabyProfileDetailComponent } from './detail/baby-profile-detail.component';
import { BabyProfileComponent } from './list/baby-profile.component';
import { BabyProfileRoutingModule } from './route/baby-profile-routing.module';
import { BabyProfileUpdateComponent } from './update/baby-profile-update.component';

@NgModule({
  imports: [SharedModule, NvD3Module, BabyProfileRoutingModule],
  declarations: [BabyProfileComponent, BabyProfileDetailComponent, BabyProfileUpdateComponent, BabyProfileDeleteDialogComponent],
  entryComponents: [BabyProfileDeleteDialogComponent],
})
export class BabyBabyProfileModule {}
