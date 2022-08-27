import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IHumor } from '../humor.model';
import { HumorService } from '../service/humor.service';

@Component({
  templateUrl: './humor-delete-dialog.component.html',
})
export class HumorDeleteDialogComponent {
  humor?: IHumor;

  constructor(protected humorService: HumorService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.humorService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
