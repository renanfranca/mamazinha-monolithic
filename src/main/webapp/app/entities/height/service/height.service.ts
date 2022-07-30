import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IHeight, getHeightIdentifier } from '../height.model';

export type EntityResponseType = HttpResponse<IHeight>;
export type EntityArrayResponseType = HttpResponse<IHeight[]>;

@Injectable({ providedIn: 'root' })
export class HeightService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/heights');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(height: IHeight): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(height);
    return this.http
      .post<IHeight>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(height: IHeight): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(height);
    return this.http
      .put<IHeight>(`${this.resourceUrl}/${getHeightIdentifier(height) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(height: IHeight): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(height);
    return this.http
      .patch<IHeight>(`${this.resourceUrl}/${getHeightIdentifier(height) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IHeight>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IHeight[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addHeightToCollectionIfMissing(heightCollection: IHeight[], ...heightsToCheck: (IHeight | null | undefined)[]): IHeight[] {
    const heights: IHeight[] = heightsToCheck.filter(isPresent);
    if (heights.length > 0) {
      const heightCollectionIdentifiers = heightCollection.map(heightItem => getHeightIdentifier(heightItem)!);
      const heightsToAdd = heights.filter(heightItem => {
        const heightIdentifier = getHeightIdentifier(heightItem);
        if (heightIdentifier == null || heightCollectionIdentifiers.includes(heightIdentifier)) {
          return false;
        }
        heightCollectionIdentifiers.push(heightIdentifier);
        return true;
      });
      return [...heightsToAdd, ...heightCollection];
    }
    return heightCollection;
  }

  protected convertDateFromClient(height: IHeight): IHeight {
    return Object.assign({}, height, {
      date: height.date?.isValid() ? height.date.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? dayjs(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((height: IHeight) => {
        height.date = height.date ? dayjs(height.date) : undefined;
      });
    }
    return res;
  }
}
