import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IHumorHistory } from '../humor-history.model';

@Component({
  selector: 'jhi-humor-history-detail',
  templateUrl: './humor-history-detail.component.html',
})
export class HumorHistoryDetailComponent implements OnInit {
  humorHistory: IHumorHistory | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ humorHistory }) => {
      this.humorHistory = humorHistory;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
