import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { INap } from '../nap.model';

@Component({
  selector: 'jhi-nap-detail',
  templateUrl: './nap-detail.component.html',
})
export class NapDetailComponent implements OnInit {
  nap: INap | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ nap }) => {
      this.nap = nap;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
