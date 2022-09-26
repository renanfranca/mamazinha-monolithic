import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBreastFeed } from '../breast-feed.model';
import { BreastFeedService } from '../service/breast-feed.service';

@Component({
  templateUrl: './breast-feed-delete-dialog.component.html',
})
export class BreastFeedDeleteDialogComponent {
  breastFeed?: IBreastFeed;

  constructor(protected breastFeedService: BreastFeedService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.breastFeedService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
