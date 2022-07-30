jest.mock('@ng-bootstrap/ng-bootstrap');

import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { BreastFeedService } from '../service/breast-feed.service';

import { BreastFeedDeleteDialogComponent } from './breast-feed-delete-dialog.component';

describe('BreastFeed Management Delete Component', () => {
  let comp: BreastFeedDeleteDialogComponent;
  let fixture: ComponentFixture<BreastFeedDeleteDialogComponent>;
  let service: BreastFeedService;
  let mockActiveModal: NgbActiveModal;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      declarations: [BreastFeedDeleteDialogComponent],
      providers: [NgbActiveModal],
    })
      .overrideTemplate(BreastFeedDeleteDialogComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BreastFeedDeleteDialogComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(BreastFeedService);
    mockActiveModal = TestBed.inject(NgbActiveModal);
  });

  describe('confirmDelete', () => {
    it('Should call delete service on confirmDelete', inject(
      [],
      fakeAsync(() => {
        // GIVEN
        jest.spyOn(service, 'delete').mockReturnValue(of(new HttpResponse({ body: {} })));

        // WHEN
        comp.confirmDelete(123);
        tick();

        // THEN
        expect(service.delete).toHaveBeenCalledWith(123);
        expect(mockActiveModal.close).toHaveBeenCalledWith('deleted');
      })
    ));

    it('Should not call delete service on clear', () => {
      // GIVEN
      jest.spyOn(service, 'delete');

      // WHEN
      comp.cancel();

      // THEN
      expect(service.delete).not.toHaveBeenCalled();
      expect(mockActiveModal.close).not.toHaveBeenCalled();
      expect(mockActiveModal.dismiss).toHaveBeenCalled();
    });
  });
});
