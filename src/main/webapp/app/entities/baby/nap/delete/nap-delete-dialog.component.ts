import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { INap } from '../nap.model';
import { NapService } from '../service/nap.service';

@Component({
  templateUrl: './nap-delete-dialog.component.html',
})
export class NapDeleteDialogComponent {
  nap?: INap;

  constructor(protected napService: NapService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.napService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
