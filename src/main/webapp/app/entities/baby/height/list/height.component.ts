import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { HeightDeleteDialogComponent } from '../delete/height-delete-dialog.component';
import { IHeight } from '../height.model';
import { HeightService } from '../service/height.service';

@Component({
  selector: 'jhi-height',
  templateUrl: './height.component.html',
})
export class HeightComponent implements OnInit {
  heights: IHeight[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected heightService: HeightService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.heights = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'date';
    this.ascending = false;
  }

  previousState(): void {
    window.history.back();
  }

  loadAll(): void {
    this.isLoading = true;

    this.heightService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IHeight[]>) => {
          this.isLoading = false;
          this.paginateHeights(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.heights = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IHeight): number {
    return item.id!;
  }

  delete(height: IHeight): void {
    const modalRef = this.modalService.open(HeightDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.height = height;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'date') {
      result.push('date');
    }
    return result;
  }

  protected paginateHeights(data: IHeight[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.heights.push(d);
      }
    }
  }
}
