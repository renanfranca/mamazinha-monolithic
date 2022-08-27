import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/config/pagination.constants';
import { ParseLinks } from 'app/core/util/parse-links.service';
import { WeightDeleteDialogComponent } from '../delete/weight-delete-dialog.component';
import { WeightService } from '../service/weight.service';
import { IWeight } from '../weight.model';

@Component({
  selector: 'jhi-weight',
  templateUrl: './weight.component.html',
})
export class WeightComponent implements OnInit {
  weights: IWeight[];
  isLoading = false;
  itemsPerPage: number;
  links: { [key: string]: number };
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(protected weightService: WeightService, protected modalService: NgbModal, protected parseLinks: ParseLinks) {
    this.weights = [];
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

    this.weightService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe(
        (res: HttpResponse<IWeight[]>) => {
          this.isLoading = false;
          this.paginateWeights(res.body, res.headers);
        },
        () => {
          this.isLoading = false;
        }
      );
  }

  reset(): void {
    this.page = 0;
    this.weights = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
  }

  trackId(index: number, item: IWeight): number {
    return item.id!;
  }

  delete(weight: IWeight): void {
    const modalRef = this.modalService.open(WeightDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.weight = weight;
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

  protected paginateWeights(data: IWeight[] | null, headers: HttpHeaders): void {
    this.links = this.parseLinks.parse(headers.get('link') ?? '');
    if (data) {
      for (const d of data) {
        this.weights.push(d);
      }
    }
  }
}
