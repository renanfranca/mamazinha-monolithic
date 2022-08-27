import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { IBreastFeed } from '../breast-feed.model';
import { BreastFeedDeleteDialogComponent } from '../delete/breast-feed-delete-dialog.component';
import { BreastFeedService } from '../service/breast-feed.service';

@Component({
  selector: 'jhi-breast-feed',
  templateUrl: './breast-feed.component.html',
})
export class BreastFeedComponent implements OnInit {
  breastFeeds: IBreastFeed[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected breastFeedService: BreastFeedService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.breastFeeds = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'start';
    this.ascending = false;
  }

  loadAll(): void {
    this.isLoading = true;

    this.breastFeedService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IBreastFeed[]>) => {
          this.isLoading = false;
          this.paginateBreastFeeds(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.breastFeeds = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IBreastFeed): number {
    return item.id!;
  }

  delete(breastFeed: IBreastFeed): void {
    const modalRef = this.modalService.open(BreastFeedDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.breastFeed = breastFeed;
    // unsubscribe not needed because closed completes on modal close
    modalRef.closed.subscribe(reason => {
      if (reason === 'deleted') {
        this.reset();
      }
    });
  }

  protected sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? ASC : DESC)];
    if (this.predicate !== 'start') {
      result.push('start');
    }
    return result;
  }

  protected paginateBreastFeeds(data: IBreastFeed[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.breastFeeds.push(d);
      }
    }
  }
}
