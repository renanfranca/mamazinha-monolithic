import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { HumorHistoryDeleteDialogComponent } from '../delete/humor-history-delete-dialog.component';
import { IHumorHistory } from '../humor-history.model';
import { HumorHistoryService } from '../service/humor-history.service';

@Component({
  selector: 'jhi-humor-history',
  templateUrl: './humor-history.component.html',
})
export class HumorHistoryComponent implements OnInit {
  humorHistories: IHumorHistory[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected humorHistoryService: HumorHistoryService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.humorHistories = [];
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

    this.humorHistoryService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IHumorHistory[]>) => {
          this.isLoading = false;
          this.paginateHumorHistories(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.humorHistories = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IHumorHistory): number {
    return item.id!;
  }

  delete(humorHistory: IHumorHistory): void {
    const modalRef = this.modalService.open(HumorHistoryDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.humorHistory = humorHistory;
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

  protected paginateHumorHistories(data: IHumorHistory[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.humorHistories.push(d);
      }
    }
  }
}
