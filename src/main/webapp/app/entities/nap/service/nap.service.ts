import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { INap, getNapIdentifier } from '../nap.model';

export type EntityResponseType = HttpResponse<INap>;
export type EntityArrayResponseType = HttpResponse<INap[]>;

@Injectable({ providedIn: 'root' })
export class NapService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/naps');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(nap: INap): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(nap);
    return this.http
      .post<INap>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(nap: INap): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(nap);
    return this.http
      .put<INap>(`${this.resourceUrl}/${getNapIdentifier(nap) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(nap: INap): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(nap);
    return this.http
      .patch<INap>(`${this.resourceUrl}/${getNapIdentifier(nap) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<INap>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<INap[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addNapToCollectionIfMissing(napCollection: INap[], ...napsToCheck: (INap | null | undefined)[]): INap[] {
    const naps: INap[] = napsToCheck.filter(isPresent);
    if (naps.length > 0) {
      const napCollectionIdentifiers = napCollection.map(napItem => getNapIdentifier(napItem)!);
      const napsToAdd = naps.filter(napItem => {
        const napIdentifier = getNapIdentifier(napItem);
        if (napIdentifier == null || napCollectionIdentifiers.includes(napIdentifier)) {
          return false;
        }
        napCollectionIdentifiers.push(napIdentifier);
        return true;
      });
      return [...napsToAdd, ...napCollection];
    }
    return napCollection;
  }

  protected convertDateFromClient(nap: INap): INap {
    return Object.assign({}, nap, {
      start: nap.start?.isValid() ? nap.start.toJSON() : undefined,
      end: nap.end?.isValid() ? nap.end.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.start = res.body.start ? dayjs(res.body.start) : undefined;
      res.body.end = res.body.end ? dayjs(res.body.end) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((nap: INap) => {
        nap.start = nap.start ? dayjs(nap.start) : undefined;
        nap.end = nap.end ? dayjs(nap.end) : undefined;
      });
    }
    return res;
  }
}
