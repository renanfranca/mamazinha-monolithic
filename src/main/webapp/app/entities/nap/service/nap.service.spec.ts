import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { Place } from 'app/entities/enumerations/place.model';
import { INap, Nap } from '../nap.model';

import { NapService } from './nap.service';

describe('Nap Service', () => {
  let service: NapService;
  let httpMock: HttpTestingController;
  let elemDefault: INap;
  let expectedResult: INap | INap[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(NapService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      start: currentDate,
      end: currentDate,
      place: Place.LAP,
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

    it('should create a Nap', () => {
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

      service.create(new Nap()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Nap', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          start: currentDate.format(DATE_TIME_FORMAT),
          end: currentDate.format(DATE_TIME_FORMAT),
          place: 'BBBBBB',
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

    it('should partial update a Nap', () => {
      const patchObject = Object.assign(
        {
          start: currentDate.format(DATE_TIME_FORMAT),
          place: 'BBBBBB',
        },
        new Nap()
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

    it('should return a list of Nap', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          start: currentDate.format(DATE_TIME_FORMAT),
          end: currentDate.format(DATE_TIME_FORMAT),
          place: 'BBBBBB',
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

    it('should delete a Nap', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addNapToCollectionIfMissing', () => {
      it('should add a Nap to an empty array', () => {
        const nap: INap = { id: 123 };
        expectedResult = service.addNapToCollectionIfMissing([], nap);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(nap);
      });

      it('should not add a Nap to an array that contains it', () => {
        const nap: INap = { id: 123 };
        const napCollection: INap[] = [
          {
            ...nap,
          },
          { id: 456 },
        ];
        expectedResult = service.addNapToCollectionIfMissing(napCollection, nap);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Nap to an array that doesn't contain it", () => {
        const nap: INap = { id: 123 };
        const napCollection: INap[] = [{ id: 456 }];
        expectedResult = service.addNapToCollectionIfMissing(napCollection, nap);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(nap);
      });

      it('should add only unique Nap to an array', () => {
        const napArray: INap[] = [{ id: 123 }, { id: 456 }, { id: 46617 }];
        const napCollection: INap[] = [{ id: 123 }];
        expectedResult = service.addNapToCollectionIfMissing(napCollection, ...napArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const nap: INap = { id: 123 };
        const nap2: INap = { id: 456 };
        expectedResult = service.addNapToCollectionIfMissing([], nap, nap2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(nap);
        expect(expectedResult).toContain(nap2);
      });

      it('should accept null and undefined values', () => {
        const nap: INap = { id: 123 };
        expectedResult = service.addNapToCollectionIfMissing([], null, nap, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(nap);
      });

      it('should return initial array if no Nap is added', () => {
        const napCollection: INap[] = [{ id: 123 }];
        expectedResult = service.addNapToCollectionIfMissing(napCollection, undefined, null);
        expect(expectedResult).toEqual(napCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
