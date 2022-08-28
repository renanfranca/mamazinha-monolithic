import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import dayjs from 'dayjs';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { getBabyProfileIdentifier, IBabyProfile } from '../baby-profile.model';

export type EntityResponseType = HttpResponse<IBabyProfile>;
export type EntityArrayResponseType = HttpResponse<IBabyProfile[]>;

@Injectable({ providedIn: 'root' })
export class BabyProfileService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/baby-profiles');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(babyProfile: IBabyProfile): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(babyProfile);
    return this.http
      .post<IBabyProfile>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(babyProfile: IBabyProfile): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(babyProfile);
    return this.http
      .put<IBabyProfile>(`${this.resourceUrl}/${getBabyProfileIdentifier(babyProfile) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(babyProfile: IBabyProfile): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(babyProfile);
    return this.http
      .patch<IBabyProfile>(`${this.resourceUrl}/${getBabyProfileIdentifier(babyProfile) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IBabyProfile>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IBabyProfile[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addBabyProfileToCollectionIfMissing(
    babyProfileCollection: IBabyProfile[],
    ...babyProfilesToCheck: (IBabyProfile | null | undefined)[]
  ): IBabyProfile[] {
    const babyProfiles: IBabyProfile[] = babyProfilesToCheck.filter(isPresent);
    if (babyProfiles.length > 0) {
      const babyProfileCollectionIdentifiers = babyProfileCollection.map(babyProfileItem => getBabyProfileIdentifier(babyProfileItem)!);
      const babyProfilesToAdd = babyProfiles.filter(babyProfileItem => {
        const babyProfileIdentifier = getBabyProfileIdentifier(babyProfileItem);
        if (babyProfileIdentifier == null || babyProfileCollectionIdentifiers.includes(babyProfileIdentifier)) {
          return false;
        }
        babyProfileCollectionIdentifiers.push(babyProfileIdentifier);
        return true;
      });
      return [...babyProfilesToAdd, ...babyProfileCollection];
    }
    return babyProfileCollection;
  }

  protected convertDateFromClient(babyProfile: IBabyProfile): IBabyProfile {
    return Object.assign({}, babyProfile, {
      birthday: babyProfile.birthday?.isValid() ? babyProfile.birthday.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.birthday = res.body.birthday ? dayjs(res.body.birthday) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((babyProfile: IBabyProfile) => {
        babyProfile.birthday = babyProfile.birthday ? dayjs(babyProfile.birthday) : undefined;
      });
    }
    return res;
  }
}
