import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import dayjs from 'dayjs';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { getBreastFeedIdentifier, IBreastFeed } from '../breast-feed.model';

export type EntityResponseType = HttpResponse<IBreastFeed>;
export type EntityArrayResponseType = HttpResponse<IBreastFeed[]>;

@Injectable({ providedIn: 'root' })
export class BreastFeedService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/breast-feeds');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(breastFeed: IBreastFeed): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(breastFeed);
    return this.http
      .post<IBreastFeed>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(breastFeed: IBreastFeed): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(breastFeed);
    return this.http
      .put<IBreastFeed>(`${this.resourceUrl}/${getBreastFeedIdentifier(breastFeed) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(breastFeed: IBreastFeed): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(breastFeed);
    return this.http
      .patch<IBreastFeed>(`${this.resourceUrl}/${getBreastFeedIdentifier(breastFeed) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBreastFeed>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBreastFeed[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  todayBreastFeedsByBabyProfile(id: number): Observable<EntityArrayResponseType> {
    const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
    return this.http
      .get<IBreastFeed[]>(`${this.resourceUrl}/today-breast-feeds-by-baby-profile/${id}?tz=${tz}`, {
        observe: 'response',
      })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  lastWeekCurrentWeekAverageBreastFeedsInHoursEachDayByBabyProfile(id: number): Observable<EntityResponseType> {
    const tz = Intl.DateTimeFormat().resolvedOptions().timeZone;
    return this.http
      .get<IBreastFeed>(`${this.resourceUrl}/lastweek-currentweek-average-breast-feeds-in-hours-eachday-by-baby-profile/${id}?tz=${tz}`, {
        observe: 'response',
      })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  incompleteBreastFeedsByBabyProfile(id: number): Observable<EntityArrayResponseType> {
    return this.http
      .get<IBreastFeed[]>(`${this.resourceUrl}/incomplete-breast-feeds-by-baby-profile/${id}`, {
        observe: 'response',
      })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBreastFeedToCollectionIfMissing(
    breastFeedCollection: IBreastFeed[],
    ...breastFeedsToCheck: (IBreastFeed | null | undefined)[]
  ): IBreastFeed[] {
    const breastFeeds: IBreastFeed[] = breastFeedsToCheck.filter(isPresent);
    if (breastFeeds.length > 0) {
      const breastFeedCollectionIdentifiers = breastFeedCollection.map(breastFeedItem => getBreastFeedIdentifier(breastFeedItem)!);
      const breastFeedsToAdd = breastFeeds.filter(breastFeedItem => {
        const breastFeedIdentifier = getBreastFeedIdentifier(breastFeedItem);
        if (breastFeedIdentifier == null || breastFeedCollectionIdentifiers.includes(breastFeedIdentifier)) {
          return false;
        }
        breastFeedCollectionIdentifiers.push(breastFeedIdentifier);
        return true;
      });
      return [...breastFeedsToAdd, ...breastFeedCollection];
    }
    return breastFeedCollection;
  }

  protected convertDateFromClient(breastFeed: IBreastFeed): IBreastFeed {
    return Object.assign({}, breastFeed, {
      start: breastFeed.start?.isValid() ? breastFeed.start.toJSON() : undefined,
      end: breastFeed.end?.isValid() ? breastFeed.end.toJSON() : undefined,
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
      res.body.forEach((breastFeed: IBreastFeed) => {
        breastFeed.start = breastFeed.start ? dayjs(breastFeed.start) : undefined;
        breastFeed.end = breastFeed.end ? dayjs(breastFeed.end) : undefined;
      });
    }
    return res;
  }
}
