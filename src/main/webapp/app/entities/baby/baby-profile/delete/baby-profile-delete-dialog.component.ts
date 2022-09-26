import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBabyProfile } from '../baby-profile.model';
import { BabyProfileService } from '../service/baby-profile.service';

@Component({
  templateUrl: './baby-profile-delete-dialog.component.html',
})
export class BabyProfileDeleteDialogComponent {
  babyProfile?: IBabyProfile;

  constructor(protected babyProfileService: BabyProfileService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.babyProfileService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
