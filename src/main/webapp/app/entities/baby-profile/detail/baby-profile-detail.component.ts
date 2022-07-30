import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBabyProfile } from '../baby-profile.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-baby-profile-detail',
  templateUrl: './baby-profile-detail.component.html',
})
export class BabyProfileDetailComponent implements OnInit {
  babyProfile: IBabyProfile | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ babyProfile }) => {
      this.babyProfile = babyProfile;
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
