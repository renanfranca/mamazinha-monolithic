import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IHumorHistory, HumorHistory } from '../humor-history.model';

import { HumorHistoryService } from './humor-history.service';

describe('Service Tests', () => {
  describe('HumorHistory Service', () => {
    let service: HumorHistoryService;
    let httpMock: HttpTestingController;
    let elemDefault: IHumorHistory;
    let expectedResult: IHumorHistory | IHumorHistory[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(HumorHistoryService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        date: currentDate,
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            date: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a HumorHistory', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            date: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.create(new HumorHistory()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a HumorHistory', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            date: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a HumorHistory', () => {
        const patchObject = Object.assign(
          {
            date: currentDate.format(DATE_TIME_FORMAT),
          },
          new HumorHistory()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of HumorHistory', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            date: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            date: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a HumorHistory', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addHumorHistoryToCollectionIfMissing', () => {
        it('should add a HumorHistory to an empty array', () => {
          const humorHistory: IHumorHistory = { id: 123 };
          expectedResult = service.addHumorHistoryToCollectionIfMissing([], humorHistory);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(humorHistory);
        });

        it('should not add a HumorHistory to an array that contains it', () => {
          const humorHistory: IHumorHistory = { id: 123 };
          const humorHistoryCollection: IHumorHistory[] = [
            {
              ...humorHistory,
            },
            { id: 456 },
          ];
          expectedResult = service.addHumorHistoryToCollectionIfMissing(humorHistoryCollection, humorHistory);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a HumorHistory to an array that doesn't contain it", () => {
          const humorHistory: IHumorHistory = { id: 123 };
          const humorHistoryCollection: IHumorHistory[] = [{ id: 456 }];
          expectedResult = service.addHumorHistoryToCollectionIfMissing(humorHistoryCollection, humorHistory);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(humorHistory);
        });

        it('should add only unique HumorHistory to an array', () => {
          const humorHistoryArray: IHumorHistory[] = [{ id: 123 }, { id: 456 }, { id: 69586 }];
          const humorHistoryCollection: IHumorHistory[] = [{ id: 123 }];
          expectedResult = service.addHumorHistoryToCollectionIfMissing(humorHistoryCollection, ...humorHistoryArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const humorHistory: IHumorHistory = { id: 123 };
          const humorHistory2: IHumorHistory = { id: 456 };
          expectedResult = service.addHumorHistoryToCollectionIfMissing([], humorHistory, humorHistory2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(humorHistory);
          expect(expectedResult).toContain(humorHistory2);
        });

        it('should accept null and undefined values', () => {
          const humorHistory: IHumorHistory = { id: 123 };
          expectedResult = service.addHumorHistoryToCollectionIfMissing([], null, humorHistory, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(humorHistory);
        });

        it('should return initial array if no HumorHistory is added', () => {
          const humorHistoryCollection: IHumorHistory[] = [{ id: 123 }];
          expectedResult = service.addHumorHistoryToCollectionIfMissing(humorHistoryCollection, undefined, null);
          expect(expectedResult).toEqual(humorHistoryCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
