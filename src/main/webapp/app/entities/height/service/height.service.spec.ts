import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IHeight, Height } from '../height.model';

import { HeightService } from './height.service';

describe('Height Service', () => {
  let service: HeightService;
  let httpMock: HttpTestingController;
  let elemDefault: IHeight;
  let expectedResult: IHeight | IHeight[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(HeightService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      value: 0,
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

    it('should create a Height', () => {
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

      service.create(new Height()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Height', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          value: 1,
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

    it('should partial update a Height', () => {
      const patchObject = Object.assign(
        {
          date: currentDate.format(DATE_TIME_FORMAT),
        },
        new Height()
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

    it('should return a list of Height', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          value: 1,
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

    it('should delete a Height', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addHeightToCollectionIfMissing', () => {
      it('should add a Height to an empty array', () => {
        const height: IHeight = { id: 123 };
        expectedResult = service.addHeightToCollectionIfMissing([], height);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(height);
      });

      it('should not add a Height to an array that contains it', () => {
        const height: IHeight = { id: 123 };
        const heightCollection: IHeight[] = [
          {
            ...height,
          },
          { id: 456 },
        ];
        expectedResult = service.addHeightToCollectionIfMissing(heightCollection, height);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Height to an array that doesn't contain it", () => {
        const height: IHeight = { id: 123 };
        const heightCollection: IHeight[] = [{ id: 456 }];
        expectedResult = service.addHeightToCollectionIfMissing(heightCollection, height);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(height);
      });

      it('should add only unique Height to an array', () => {
        const heightArray: IHeight[] = [{ id: 123 }, { id: 456 }, { id: 7044 }];
        const heightCollection: IHeight[] = [{ id: 123 }];
        expectedResult = service.addHeightToCollectionIfMissing(heightCollection, ...heightArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const height: IHeight = { id: 123 };
        const height2: IHeight = { id: 456 };
        expectedResult = service.addHeightToCollectionIfMissing([], height, height2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(height);
        expect(expectedResult).toContain(height2);
      });

      it('should accept null and undefined values', () => {
        const height: IHeight = { id: 123 };
        expectedResult = service.addHeightToCollectionIfMissing([], null, height, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(height);
      });

      it('should return initial array if no Height is added', () => {
        const heightCollection: IHeight[] = [{ id: 123 }];
        expectedResult = service.addHeightToCollectionIfMissing(heightCollection, undefined, null);
        expect(expectedResult).toEqual(heightCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
