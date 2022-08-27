import * as dayjs from 'dayjs';
import { IBabyProfile } from 'app/entities/baby/baby-profile/baby-profile.model';
import { Pain } from 'app/entities/enumerations/pain.model';

export interface IBreastFeed {
  id?: number;
  start?: dayjs.Dayjs | null;
  end?: dayjs.Dayjs | null;
  pain?: Pain | null;
  babyProfile?: IBabyProfile | null;
}

export class BreastFeed implements IBreastFeed {
  constructor(
    public id?: number,
    public start?: dayjs.Dayjs | null,
    public end?: dayjs.Dayjs | null,
    public pain?: Pain | null,
    public babyProfile?: IBabyProfile | null
  ) {}
}

export function getBreastFeedIdentifier(breastFeed: IBreastFeed): number | undefined {
  return breastFeed.id;
}
