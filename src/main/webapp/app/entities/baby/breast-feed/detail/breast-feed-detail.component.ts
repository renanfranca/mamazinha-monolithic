import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBreastFeed } from '../breast-feed.model';

@Component({
  selector: 'jhi-breast-feed-detail',
  templateUrl: './breast-feed-detail.component.html',
})
export class BreastFeedDetailComponent implements OnInit {
  breastFeed: IBreastFeed | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ breastFeed }) => {
      this.breastFeed = breastFeed;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
