import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IHumor, Humor } from '../humor.model';

import { HumorService } from './humor.service';

describe('Humor Service', () => {
  let service: HumorService;
  let httpMock: HttpTestingController;
  let elemDefault: IHumor;
  let expectedResult: IHumor | IHumor[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(HumorService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      value: 0,
      description: 'AAAAAAA',
      emoticoContentType: 'image/png',
      emotico: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Humor', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Humor()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Humor', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          value: 1,
          description: 'BBBBBB',
          emotico: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Humor', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
          emotico: 'BBBBBB',
        },
        new Humor()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Humor', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          value: 1,
          description: 'BBBBBB',
          emotico: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Humor', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addHumorToCollectionIfMissing', () => {
      it('should add a Humor to an empty array', () => {
        const humor: IHumor = { id: 123 };
        expectedResult = service.addHumorToCollectionIfMissing([], humor);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(humor);
      });

      it('should not add a Humor to an array that contains it', () => {
        const humor: IHumor = { id: 123 };
        const humorCollection: IHumor[] = [
          {
            ...humor,
          },
          { id: 456 },
        ];
        expectedResult = service.addHumorToCollectionIfMissing(humorCollection, humor);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Humor to an array that doesn't contain it", () => {
        const humor: IHumor = { id: 123 };
        const humorCollection: IHumor[] = [{ id: 456 }];
        expectedResult = service.addHumorToCollectionIfMissing(humorCollection, humor);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(humor);
      });

      it('should add only unique Humor to an array', () => {
        const humorArray: IHumor[] = [{ id: 123 }, { id: 456 }, { id: 49862 }];
        const humorCollection: IHumor[] = [{ id: 123 }];
        expectedResult = service.addHumorToCollectionIfMissing(humorCollection, ...humorArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const humor: IHumor = { id: 123 };
        const humor2: IHumor = { id: 456 };
        expectedResult = service.addHumorToCollectionIfMissing([], humor, humor2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(humor);
        expect(expectedResult).toContain(humor2);
      });

      it('should accept null and undefined values', () => {
        const humor: IHumor = { id: 123 };
        expectedResult = service.addHumorToCollectionIfMissing([], null, humor, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(humor);
      });

      it('should return initial array if no Humor is added', () => {
        const humorCollection: IHumor[] = [{ id: 123 }];
        expectedResult = service.addHumorToCollectionIfMissing(humorCollection, undefined, null);
        expect(expectedResult).toEqual(humorCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
