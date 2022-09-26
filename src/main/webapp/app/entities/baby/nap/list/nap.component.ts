import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { NapDeleteDialogComponent } from '../delete/nap-delete-dialog.component';
import { INap } from '../nap.model';
import { NapService } from '../service/nap.service';

@Component({
  selector: 'jhi-nap',
  templateUrl: './nap.component.html',
})
export class NapComponent implements OnInit {
  naps: INap[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected napService: NapService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.naps = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'start';
    this.ascending = false;
  }

  previousState(): void {
    window.history.back();
  }

  loadAll(): void {
    this.isLoading = true;

    this.napService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<INap[]>) => {
          this.isLoading = false;
          this.paginateNaps(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.naps = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: INap): number {
    return item.id!;
  }

  delete(nap: INap): void {
    const modalRef = this.modalService.open(NapDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.nap = nap;
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

  protected paginateNaps(data: INap[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.naps.push(d);
      }
    }
  }
}
