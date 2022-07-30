import { Component, OnInit } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { IBreastFeed } from '../breast-feed.model';

import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { BreastFeedService } from '../service/breast-feed.service';
import { BreastFeedDeleteDialogComponent } from '../delete/breast-feed-delete-dialog.component';
import { ParseLinks } from 'app/core/util/parse-links.service';

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
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.isLoading = true;

    this.breastFeedService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe({
        next: (res: HttpResponse<IBreastFeed[]>) => {
          this.isLoading = false;
          this.paginateBreastFeeds(res.body, res.headers);
        },
        error: () => {
          this.isLoading = false;
        },
      });
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

  trackId(_index: number, item: IBreastFeed): number {
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
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateBreastFeeds(data: IBreastFeed[] | null, headers: HttpHeaders): void {
    const linkHeader = headers.get('link');
    if (linkHeader) {
      this.links = this.parseLinks.parse(linkHeader);
    } else {
      this.links = {
        last: 0,
      };
    }
    if (data) {
      for (const d of data) {
        this.breastFeeds.push(d);
      }
    }
  }
}
