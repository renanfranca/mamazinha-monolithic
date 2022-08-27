import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBabyProfile, BabyProfile } from '../baby-profile.model';

import { BabyProfileService } from './baby-profile.service';

describe('Service Tests', () => {
  describe('BabyProfile Service', () => {
    let service: BabyProfileService;
    let httpMock: HttpTestingController;
    let elemDefault: IBabyProfile;
    let expectedResult: IBabyProfile | IBabyProfile[] | boolean | null;
    let currentDate: dayjs.Dayjs;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [HttpClientTestingModule],
      });
      expectedResult = null;
      service = TestBed.inject(BabyProfileService);
      httpMock = TestBed.inject(HttpTestingController);
      currentDate = dayjs();

      elemDefault = {
        id: 0,
        name: 'AAAAAAA',
        pictureContentType: 'image/png',
        picture: 'AAAAAAA',
        birthday: currentDate,
        sign: 'AAAAAAA',
        main: false,
        userId: 'AAAAAAA',
      };
    });

    describe('Service methods', () => {
      it('should find an element', () => {
        const returnedFromService = Object.assign(
          {
            birthday: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        service.find(123).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(elemDefault);
      });

      it('should create a BabyProfile', () => {
        const returnedFromService = Object.assign(
          {
            id: 0,
            birthday: currentDate.format(DATE_TIME_FORMAT),
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            birthday: currentDate,
          },
          returnedFromService
        );

        service.create(new BabyProfile()).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'POST' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should update a BabyProfile', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            picture: 'BBBBBB',
            birthday: currentDate.format(DATE_TIME_FORMAT),
            sign: 'BBBBBB',
            main: true,
            userId: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            birthday: currentDate,
          },
          returnedFromService
        );

        service.update(expected).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PUT' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should partial update a BabyProfile', () => {
        const patchObject = Object.assign(
          {
            main: true,
            userId: 'BBBBBB',
          },
          new BabyProfile()
        );

        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            birthday: currentDate,
          },
          returnedFromService
        );

        service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'PATCH' });
        req.flush(returnedFromService);
        expect(expectedResult).toMatchObject(expected);
      });

      it('should return a list of BabyProfile', () => {
        const returnedFromService = Object.assign(
          {
            id: 1,
            name: 'BBBBBB',
            picture: 'BBBBBB',
            birthday: currentDate.format(DATE_TIME_FORMAT),
            sign: 'BBBBBB',
            main: true,
            userId: 'BBBBBB',
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            birthday: currentDate,
          },
          returnedFromService
        );

        service.query().subscribe(resp => (expectedResult = resp.body));

        const req = httpMock.expectOne({ method: 'GET' });
        req.flush([returnedFromService]);
        httpMock.verify();
        expect(expectedResult).toContainEqual(expected);
      });

      it('should delete a BabyProfile', () => {
        service.delete(123).subscribe(resp => (expectedResult = resp.ok));

        const req = httpMock.expectOne({ method: 'DELETE' });
        req.flush({ status: 200 });
        expect(expectedResult);
      });

      describe('addBabyProfileToCollectionIfMissing', () => {
        it('should add a BabyProfile to an empty array', () => {
          const babyProfile: IBabyProfile = { id: 123 };
          expectedResult = service.addBabyProfileToCollectionIfMissing([], babyProfile);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(babyProfile);
        });

        it('should not add a BabyProfile to an array that contains it', () => {
          const babyProfile: IBabyProfile = { id: 123 };
          const babyProfileCollection: IBabyProfile[] = [
            {
              ...babyProfile,
            },
            { id: 456 },
          ];
          expectedResult = service.addBabyProfileToCollectionIfMissing(babyProfileCollection, babyProfile);
          expect(expectedResult).toHaveLength(2);
        });

        it("should add a BabyProfile to an array that doesn't contain it", () => {
          const babyProfile: IBabyProfile = { id: 123 };
          const babyProfileCollection: IBabyProfile[] = [{ id: 456 }];
          expectedResult = service.addBabyProfileToCollectionIfMissing(babyProfileCollection, babyProfile);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(babyProfile);
        });

        it('should add only unique BabyProfile to an array', () => {
          const babyProfileArray: IBabyProfile[] = [{ id: 123 }, { id: 456 }, { id: 59782 }];
          const babyProfileCollection: IBabyProfile[] = [{ id: 123 }];
          expectedResult = service.addBabyProfileToCollectionIfMissing(babyProfileCollection, ...babyProfileArray);
          expect(expectedResult).toHaveLength(3);
        });

        it('should accept varargs', () => {
          const babyProfile: IBabyProfile = { id: 123 };
          const babyProfile2: IBabyProfile = { id: 456 };
          expectedResult = service.addBabyProfileToCollectionIfMissing([], babyProfile, babyProfile2);
          expect(expectedResult).toHaveLength(2);
          expect(expectedResult).toContain(babyProfile);
          expect(expectedResult).toContain(babyProfile2);
        });

        it('should accept null and undefined values', () => {
          const babyProfile: IBabyProfile = { id: 123 };
          expectedResult = service.addBabyProfileToCollectionIfMissing([], null, babyProfile, undefined);
          expect(expectedResult).toHaveLength(1);
          expect(expectedResult).toContain(babyProfile);
        });

        it('should return initial array if no BabyProfile is added', () => {
          const babyProfileCollection: IBabyProfile[] = [{ id: 123 }];
          expectedResult = service.addBabyProfileToCollectionIfMissing(babyProfileCollection, undefined, null);
          expect(expectedResult).toEqual(babyProfileCollection);
        });
      });
    });

    afterEach(() => {
      httpMock.verify();
    });
  });
});
