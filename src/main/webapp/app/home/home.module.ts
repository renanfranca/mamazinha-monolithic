import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { SharedModule } from 'app/shared/shared.module';
import 'd3';
import { NvD3Module } from 'ng2-nvd3';
import 'nvd3';
import { HomeComponent } from './home.component';
import { HOME_ROUTE } from './home.route';

@NgModule({
  imports: [SharedModule, NvD3Module, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent],
})
export class HomeModule {}
