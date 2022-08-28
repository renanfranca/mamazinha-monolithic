import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { isPresent } from 'app/core/util/operators';
import { getHumorIdentifier, IHumor } from '../humor.model';

export type EntityResponseType = HttpResponse<IHumor>;
export type EntityArrayResponseType = HttpResponse<IHumor[]>;

@Injectable({ providedIn: 'root' })
export class HumorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/humors');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(humor: IHumor): Observable<EntityResponseType> {
    return this.http.post<IHumor>(this.resourceUrl, humor, { observe: 'response' });
  }

  update(humor: IHumor): Observable<EntityResponseType> {
    return this.http.put<IHumor>(`${this.resourceUrl}/${getHumorIdentifier(humor) as number}`, humor, { observe: 'response' });
  }

  partialUpdate(humor: IHumor): Observable<EntityResponseType> {
    return this.http.patch<IHumor>(`${this.resourceUrl}/${getHumorIdentifier(humor) as number}`, humor, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IHumor>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IHumor[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addHumorToCollectionIfMissing(humorCollection: IHumor[], ...humorsToCheck: (IHumor | null | undefined)[]): IHumor[] {
    const humors: IHumor[] = humorsToCheck.filter(isPresent);
    if (humors.length > 0) {
      const humorCollectionIdentifiers = humorCollection.map(humorItem => getHumorIdentifier(humorItem)!);
      const humorsToAdd = humors.filter(humorItem => {
        const humorIdentifier = getHumorIdentifier(humorItem);
        if (humorIdentifier == null || humorCollectionIdentifiers.includes(humorIdentifier)) {
          return false;
        }
        humorCollectionIdentifiers.push(humorIdentifier);
        return true;
      });
      return [...humorsToAdd, ...humorCollection];
    }
    return humorCollection;
  }
}
