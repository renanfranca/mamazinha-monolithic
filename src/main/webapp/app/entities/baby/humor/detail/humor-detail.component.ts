import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IHumor } from '../humor.model';
import { DataUtils } from 'app/core/util/data-util.service';

@Component({
  selector: 'jhi-humor-detail',
  templateUrl: './humor-detail.component.html',
})
export class HumorDetailComponent implements OnInit {
  humor: IHumor | null = null;

  constructor(protected dataUtils: DataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ humor }) => {
      this.humor = humor;
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
