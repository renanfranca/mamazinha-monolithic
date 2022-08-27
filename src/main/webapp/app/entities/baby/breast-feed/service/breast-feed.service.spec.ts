import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { Pain } from 'app/entities/enumerations/pain.model';
import { IBreastFeed, BreastFeed } from '../breast-feed.model';

import { BreastFeedService } from './breast-feed.service';

describe('Service Tests', () => {
  describe('BreastFeed Service', () => {
    let service: BreastFeedService;
    let httpMock: HttpTestingController;
    let elemDefault: IBreastFeed;
    let expectedResult: IBreastFeed | IBreastFeed[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(BreastFeedService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        start: currentDate,
        end: currentDate,
        pain: Pain.NO_PAIN,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            start: currentDate.format(DATE_TIME_FORMAT),
            end: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a BreastFeed', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            start: currentDate.format(DATE_TIME_FORMAT),
            end: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
          },
          returnedFromService
        );

        service.create(new BreastFeed()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a BreastFeed', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            start: currentDate.format(DATE_TIME_FORMAT),
            end: currentDate.format(DATE_TIME_FORMAT),
            pain: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a BreastFeed', () => {
        const patchObject = Object.assign(
          {
            pain: 'BBBBBB',
          },
          new BreastFeed()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of BreastFeed', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            start: currentDate.format(DATE_TIME_FORMAT),
            end: currentDate.format(DATE_TIME_FORMAT),
            pain: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            start: currentDate,
            end: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a BreastFeed', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addBreastFeedToCollectionIfMissing', () => {
        it('should add a BreastFeed to an empty array', () => {
          const breastFeed: IBreastFeed = { id: 123 };
          expectedResult = service.addBreastFeedToCollectionIfMissing([], breastFeed);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(breastFeed);
        });

        it('should not add a BreastFeed to an array that contains it', () => {
          const breastFeed: IBreastFeed = { id: 123 };
          const breastFeedCollection: IBreastFeed[] = [
            {
              ...breastFeed,
            },
            { id: 456 },
          ];
          expectedResult = service.addBreastFeedToCollectionIfMissing(breastFeedCollection, breastFeed);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a BreastFeed to an array that doesn't contain it", () => {
          const breastFeed: IBreastFeed = { id: 123 };
          const breastFeedCollection: IBreastFeed[] = [{ id: 456 }];
          expectedResult = service.addBreastFeedToCollectionIfMissing(breastFeedCollection, breastFeed);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(breastFeed);
        });

        it('should add only unique BreastFeed to an array', () => {
          const breastFeedArray: IBreastFeed[] = [{ id: 123 }, { id: 456 }, { id: 17681 }];
          const breastFeedCollection: IBreastFeed[] = [{ id: 123 }];
          expectedResult = service.addBreastFeedToCollectionIfMissing(breastFeedCollection, ...breastFeedArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const breastFeed: IBreastFeed = { id: 123 };
          const breastFeed2: IBreastFeed = { id: 456 };
          expectedResult = service.addBreastFeedToCollectionIfMissing([], breastFeed, breastFeed2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(breastFeed);
          expect(expectedResult).toContain(breastFeed2);
        });

        it('should accept null and undefined values', () => {
          const breastFeed: IBreastFeed = { id: 123 };
          expectedResult = service.addBreastFeedToCollectionIfMissing([], null, breastFeed, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(breastFeed);
        });

        it('should return initial array if no BreastFeed is added', () => {
          const breastFeedCollection: IBreastFeed[] = [{ id: 123 }];
          expectedResult = service.addBreastFeedToCollectionIfMissing(breastFeedCollection, undefined, null);
          expect(expectedResult).toEqual(breastFeedCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
