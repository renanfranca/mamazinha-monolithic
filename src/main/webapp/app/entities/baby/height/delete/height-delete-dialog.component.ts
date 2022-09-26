import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IHeight } from '../height.model';
import { HeightService } from '../service/height.service';

@Component({
  templateUrl: './height-delete-dialog.component.html',
})
export class HeightDeleteDialogComponent {
  height?: IHeight;

  constructor(protected heightService: HeightService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.heightService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
