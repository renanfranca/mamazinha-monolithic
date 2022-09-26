import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IHumorHistory } from '../humor-history.model';
import { HumorHistoryService } from '../service/humor-history.service';

@Component({
  templateUrl: './humor-history-delete-dialog.component.html',
})
export class HumorHistoryDeleteDialogComponent {
  humorHistory?: IHumorHistory;

  constructor(protected humorHistoryService: HumorHistoryService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.humorHistoryService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
