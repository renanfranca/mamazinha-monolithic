import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IHeight } from '../height.model';

@Component({
  selector: 'jhi-height-detail',
  templateUrl: './height-detail.component.html',
})
export class HeightDetailComponent implements OnInit {
  height: IHeight | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ height }) => {
      this.height = height;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
