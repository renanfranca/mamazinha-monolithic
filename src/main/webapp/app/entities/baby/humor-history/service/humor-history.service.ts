import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import dayjs from 'dayjs';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { getHumorHistoryIdentifier, IHumorHistory } from '../humor-history.model';

export type EntityResponseType = HttpResponse<IHumorHistory>;
export type EntityArrayResponseType = HttpResponse<IHumorHistory[]>;

@Injectable({ providedIn: 'root' })
export class HumorHistoryService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/humor-histories');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(humorHistory: IHumorHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(humorHistory);
    return this.http
      .post<IHumorHistory>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(humorHistory: IHumorHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(humorHistory);
    return this.http
      .put<IHumorHistory>(`${this.resourceUrl}/${getHumorHistoryIdentifier(humorHistory) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(humorHistory: IHumorHistory): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(humorHistory);
    return this.http
      .patch<IHumorHistory>(`${this.resourceUrl}/${getHumorHistoryIdentifier(humorHistory) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IHumorHistory>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IHumorHistory[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  todayAverageHumorHistoryByBabyProfile(id: number): Observable<EntityResponseType> {
    const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
    return this.http
      .get<IHumorHistory>(`${this.resourceUrl}/today-average-humor-history-by-baby-profile/${id}?tz=${tz}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  lastWeekCurrentWeekAverageHumorHistoryEachDayByBabyProfile(id: number): Observable<EntityResponseType> {
    const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
    return this.http
      .get<IHumorHistory>(`${this.resourceUrl}/lastweek-currentweek-average-humor-history-by-baby-profile/${id}?tz=${tz}`, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addHumorHistoryToCollectionIfMissing(
    humorHistoryCollection: IHumorHistory[],
    ...humorHistoriesToCheck: (IHumorHistory | null | undefined)[]
  ): IHumorHistory[] {
    const humorHistories: IHumorHistory[] = humorHistoriesToCheck.filter(isPresent);
    if (humorHistories.length > 0) {
      const humorHistoryCollectionIdentifiers = humorHistoryCollection.map(
        humorHistoryItem => getHumorHistoryIdentifier(humorHistoryItem)!
      );
      const humorHistoriesToAdd = humorHistories.filter(humorHistoryItem => {
        const humorHistoryIdentifier = getHumorHistoryIdentifier(humorHistoryItem);
        if (humorHistoryIdentifier == null || humorHistoryCollectionIdentifiers.includes(humorHistoryIdentifier)) {
          return false;
        }
        humorHistoryCollectionIdentifiers.push(humorHistoryIdentifier);
        return true;
      });
      return [...humorHistoriesToAdd, ...humorHistoryCollection];
    }
    return humorHistoryCollection;
  }

  protected convertDateFromClient(humorHistory: IHumorHistory): IHumorHistory {
    return Object.assign({}, humorHistory, {
      date: humorHistory.date?.isValid() ? humorHistory.date.toJSON() : undefined,
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
      res.body.forEach((humorHistory: IHumorHistory) => {
        humorHistory.date = humorHistory.date ? dayjs(humorHistory.date) : undefined;
      });
    }
    return res;
  }
}
